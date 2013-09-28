package ch.ethz.fcl.hid.tuio;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.fcl.mogl.geom.BoundingBox;
import ch.ethz.fcl.mogl.scene.IScene;
import ch.ethz.fcl.mogl.scene.IView;
import ch.ethz.fcl.mogl.scene.IView.ViewType;
import ch.ethz.fcl.net.osc.OSCError;
import ch.ethz.fcl.net.osc.OSCHandler;
import ch.ethz.fcl.net.osc.OSCServer;

// XXX: this code is defunct at the moment (gesture handling to be implemented)
public class TUIO {
	public static final int DEFAULT_PORT = 3333;
	
	private static final long WAIT_TIME_MS = 200;
	
	private class Cursor {
		private int id;
		private float x;
		private float y;
		private float vx;
		private float vy;
		private float accel;
		private Cursor previous;
		
		public Cursor(int id, float x, float y, float vx, float vy, float accel, Cursor previous) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.accel = accel;
			this.previous = previous;
			if (previous != null)
				previous.previous = null;
		}
	}
	
	private enum GestureState {
		IDLE,
		POINT,
		DRAG,
		PINCH_2,
		SWIPE_2,
		SWIPE_3,
		WAIT,
		BLOCK
	}

	private class Handler implements OSCHandler {
		private final List<Integer> alive = new ArrayList<Integer>();

		@Override
		public Object[] handle(String[] address, int addrIdx, StringBuilder typeString, long timestamp, Object... args) throws OSCError {
			if (address.length < 3 || !address[1].equals("tuio") || !address[2].equals("2Dcur") || args.length == 0)
				return null;
			
			try {
				if (args[0].equals("set")) {
					if (cursors.isEmpty()) {
						startTime = System.currentTimeMillis();
					}
					
					Integer id = (Integer)args[1];
					Float x = (Float)args[2];
					Float y = (Float)args[3];
					Float vx = (Float)args[4];
					Float vy = (Float)args[5];
					Float accel = (Float)args[6];
					Cursor previous = cursors.get(id);
					cursors.put(id, new Cursor(id, x, y, vx, vy, accel, previous));
					System.out.print(x + " " + y + " ");
					detectGesture();
				} else if (args[0].equals("alive")) {
					// only detect gesture if something changes (since "alive" is sent frequently)
					alive.clear();
					for (int i = 1; i < args.length; ++i)
						alive.add((Integer)args[i]);
					if (alive.isEmpty()) {
						if (!cursors.isEmpty()) {
							cursors.clear();
							detectGesture();
						}
					} else {
						if (cursors.keySet().retainAll(alive)) {
							detectGesture();							
						}
					}
				}
			} catch (Exception e) {
			}
			return null;
		}
	}

	private IScene scene;
	private IView view;
	
	private OSCServer server;
	
	private Map<Integer, Cursor> cursors = new HashMap<Integer, Cursor>();
	
	private long startTime;
	private GestureState gestureState = GestureState.IDLE;
	
	private BoundingBox bounds = new BoundingBox();

	public TUIO(IScene scene, int port) throws UnknownHostException, IOException {
		this.scene = scene;
		for (IView view : scene.getViews()) {
			if (view.getViewType() == ViewType.INTERACTIVE_VIEW) {
				this.view = view;
				break;
			}
		}
		if (this.view == null) {
			throw new IllegalArgumentException("interactive view required for multitouch");
		}
		
		server = new OSCServer(port);
		server.installHandler("/", new Handler());		
		
		// get screen information
		
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (GraphicsDevice device : g.getScreenDevices()) {
			Rectangle rect = device.getDefaultConfiguration().getBounds();
			bounds.add(rect.getMinX(), rect.getMinY());
			bounds.add(rect.getMaxX(), rect.getMaxY());
		}
	}

	public TUIO(IScene scene) throws UnknownHostException, IOException {
		this(scene, DEFAULT_PORT);
	}

	private void detectGesture() {
		System.out.print(cursors.size() + " ");
		GestureState g;
		switch (cursors.size()) {
		case 0:
			g = GestureState.IDLE;
			break;
		case 1:
			g = GestureState.POINT;
			break;
		case 2:
			g = GestureState.SWIPE_2;
			break;
		case 3:
		default:
			g = GestureState.SWIPE_3;
		}
		
		switch (gestureState) {
		case IDLE:
			// no action yet, need to find out if one or more fingers down
			switch (g) {
			case POINT:
				gestureState = GestureState.WAIT;
				break;
			default:
				gestureState = g;
			}
			break;
			
		case WAIT:
			switch (g) {
			case IDLE:
				System.out.println("point down & up");
				gestureState = GestureState.IDLE;
				break;
			case POINT:
				System.out.println("point down");
				gestureState = GestureState.POINT;
				break;
			default:
				gestureState = g;
			}
			break;
			
		case POINT:
			switch (g) {
			case IDLE:
				System.out.println("point up");
				gestureState = GestureState.IDLE;
				break;
			case POINT:
				System.out.println("drag");
				gestureState = GestureState.DRAG;
				break;
			default:
				gestureState = GestureState.BLOCK;
			}
			break;
			
		case DRAG:
			switch (g) {
			case IDLE:
				System.out.println("point up");
				gestureState = GestureState.IDLE;
				break;
			case POINT:
				System.out.println("drag");
				gestureState = GestureState.DRAG;
				break;
			default:
				System.out.println("block");
				gestureState = GestureState.BLOCK;
			}
			break;
			
		case PINCH_2:
			break;
			
		case SWIPE_2:
			switch (g) {
			case IDLE:
				System.out.println("swipe 2 end");
				gestureState = GestureState.IDLE;
				break;
			case SWIPE_2:
				System.out.println("swipe 2");
				break;
			default:
				System.out.println("block");
				gestureState = GestureState.BLOCK;
			}
			break;

		case SWIPE_3:
			switch (g) {
			case IDLE:
				System.out.println("swipe 3 end");
				gestureState = GestureState.IDLE;
				break;
			case SWIPE_3:
				System.out.println("swipe 3");
				break;
			default:
				System.out.println("block");
				gestureState = GestureState.BLOCK;
			}
			break;

		case BLOCK:
			switch (g) {
			case IDLE:
				System.out.println("release");
				gestureState = GestureState.IDLE;
				break;
			default:
				System.out.println("blocked");
			}
			break;
		}
		//System.out.println(gestureState);
	}

//	private void handle() {
//		List<TuioCursor> c = new ArrayList<TuioCursor>();
//		c.addAll(cursors.values());
//		if (c.size() == 2)
//			rotateOrScale(c);
//		else if (c.size() == 3)
//			pan(c);
//	}
//	
//	private void rotateOrScale(List<TuioCursor> c) {
//		Vector2D v0 = new Vector2D(c.get(0).getXSpeed(), c.get(0).getYSpeed());
//		Vector2D v1 = new Vector2D(c.get(1).getXSpeed(), c.get(1).getYSpeed());
//		double dot = v0.dotProduct(v1);
//		System.out.println(dot);
//		if (dot > 0) {
//			view.getCamera().addToRotateZ(0.5 * c.get(0).getXSpeed());
//			view.getCamera().addToRotateX(0.5 * c.get(0).getYSpeed());		
//		} else {
//			// XXX TO BE IMPLEMENTED
//		}
//	}
//
//	private void pan(List<TuioCursor> c) {
//		view.getCamera().addToTranslateX(0.02 * c.get(0).getXSpeed());
//		view.getCamera().addToTranslateY(-0.02 * c.get(0).getYSpeed());
//	}
}
