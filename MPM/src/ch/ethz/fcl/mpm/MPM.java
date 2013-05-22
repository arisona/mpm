/*
Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona, Eva Friedrich)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
* Neither the name of ETH Zurich nor the names of its contributors may be 
  used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
 
package ch.ethz.fcl.mpm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import ch.ethz.fcl.mpm.model.IGeometryModel;
import ch.ethz.fcl.mpm.model.SGModel;
import ch.ethz.fcl.net.osc.OSCError;
import ch.ethz.fcl.net.osc.OSCHandler;
import ch.ethz.fcl.net.osc.OSCServer;
import ch.ethz.fcl.net.util.UDPServer;

public final class MPM {
	private Scene scene;

	public static void main(String[] args) {
		// Make sure everything runs on GUI thread...
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MPM();
			}
		});
	}

	public MPM() {
		IGeometryModel model = chooseModel();

		scene = new Scene();
		scene.setModel(model);

		scene.addView(new View(scene, 0, 10, 640, 480, "View 0", 0, 0.0, View.ViewType.CONTROL_VIEW));

		scene.addView(new View(scene, 1920, 0, 640, 480, "View 1", 1, -90.0, View.ViewType.PROJECTION_VIEW));
		scene.addView(new View(scene, 1920 + 640, 0, 640, 480, "View 2", 2, 0.0, View.ViewType.PROJECTION_VIEW));
		scene.addView(new View(scene, 1920 + 640 + 640, 0, 640, 480, "View 3", 3, 90.0, View.ViewType.PROJECTION_VIEW));
		scene.addView(new View(scene, 1920 + 640 + 640 + 640, 0, 640, 480, "View 4", 4, 180.0, View.ViewType.PROJECTION_VIEW));

		//runOSCScan(32000);
		runOSCSun(7777);
		runUDP(7000);
	}

	private IGeometryModel chooseModel() {
		IGeometryModel model = null;

		// try {
		// JFileChooser fc = new JFileChooser();
		// fc.setFileFilter(new FileFilter() {
		//
		// @Override
		// public String getDescription() {
		// return "3D Files (.stl)";
		// }
		//
		// @Override
		// public boolean accept(File f) {
		// if (f.isDirectory())
		// return true;
		// if (FileUtils.getExtension(f).equals("stl"))
		// return true;
		// return false;
		// }
		// });
		// File f = new File(PreferencesStore.get().get("selected_file", ""));
		// fc.setSelectedFile(f);
		// if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		// f = fc.getSelectedFile();
		// PreferencesStore.get().put("selected_file", f.getCanonicalPath());
		// if (FileUtils.getExtension(f).equals("stl")) {
		// model = new STLFileModel(f);
		// }
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		if (model == null) {
			// model = new HalfBoxModel();
			model = new SGModel();
		}
		return model;
	}


	private static OSCServer oscServerScan = null;

	private void runOSCScan(int port) {
		try {
			if (oscServerScan == null) {
				oscServerScan = new OSCServer(port, "224.0.1.0");
				oscServerScan.installHandler("/", new OSCHandler() {
					@Override
					public Object[] handle(String[] address, int addrIdx, StringBuilder typeString, long timestamp, Object... args) throws OSCError {
						try {
							System.out.println("osc args: " + args.length);
							// /address[1]/address[2]/...
							// double someValue =
							// ((Number)args[0]).doubleValue();
							ArrayList<ArrayList<Number>> objects = new ArrayList<ArrayList<Number>>();
							ArrayList<Number> object = new ArrayList<Number>();
							for (int i = 1; i < args.length; ) {
								if (args[i] instanceof String) {
									objects.add(object);
									object = new ArrayList<Number>();
									i++;
								} else {
									object.add((Number)args[i]);
									object.add((Number)args[i+1]);
									object.add((Number)args[i+2]);
									i+=3;
								}
							}
							createGeometry(objects);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static OSCServer oscServerSun = null;

	private void runOSCSun(int port) {
		final float[] sunPosition = new float[3];
		sunPosition[0] = scene.getLightPosition()[0];
		sunPosition[1] = scene.getLightPosition()[1];
		try {
			if (oscServerSun == null) {
				final Timer timer = new Timer(50, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						float[] position = scene.getLightPosition();
						position[0] += (sunPosition[0] - position[0]) / 10f;
						position[1] += (sunPosition[1] - position[1]) / 10f;
						// System.out.println(scene.getLightPosition()[0]+" "+scene.getLightPosition()[1]);
						scene.setLightPosition(position);
						scene.repaintAll();
					}
				});

				oscServerSun = new OSCServer(port, null);
				//oscserver = new OSCServer(port, "224.0.1.0");
				oscServerSun.installHandler("/", new OSCHandler() {
					@Override
					public Object[] handle(String[] address, int addrIdx, StringBuilder typeString, long timestamp, Object... args) throws OSCError {
						try {
							// /address[1]/address[2]/...
							// double someValue =
							if (args.length >= 2) {
								sunPosition[0] = ((Number)args[0]).floatValue()/100.0f*5;
								sunPosition[1] = ((Number)args[1]).floatValue()/100.0f*5;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

				});
				timer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static UDPServer udpserver = null;

	private void runUDP(int port) {
		try {
			udpserver = new UDPServer(port, new UDPServer.UDPHandler() {
				@Override
				public void handle(DatagramPacket packet) {
					try {
						// format: /x,y,z,r,g,b;x,y,z,r,g,b;x,y,z,r,g,b/x,y,z,r,g,b;...
						System.out.println("got data");
						InputStreamReader input = new InputStreamReader(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()),
								Charset.forName("UTF-8"));
						StringBuilder sb = new StringBuilder();
						for (int value; (value = input.read()) != -1;)
							sb.append((char) value);
						String message = sb.toString();
						String[] triangles = message.split("/");
						float[] vertices = new float[9 * (triangles.length - 1)];
						float[] colors = new float[9 * (triangles.length - 1)];
						int k = 0;
						for (int i = 1; i < triangles.length; ++i) {
							String[] v = triangles[i].split(";");
							//System.out.print("triangle: ");
							for (int j = 0; j < 3; ++j) {
								String[] vv = v[j].split(",");
								vertices[k + 0] = Float.parseFloat(vv[0]) / 1000f;
								vertices[k + 1] = Float.parseFloat(vv[1]) / 1000f;
								vertices[k + 2] = Float.parseFloat(vv[2]) / 1000f;
								colors[k + 0] = Float.parseFloat(vv[3]) / 256f;
								colors[k + 1] = Float.parseFloat(vv[4]) / 256f;
								colors[k + 2] = Float.parseFloat(vv[5]) / 256f;
								//System.out.print(vertices[k+0] + " " + vertices[k+1] + " " + vertices[k+2] + " | ");
								//System.out.print(colors[k+0] + " " + colors[k+1] + " " + colors[k+2] + " | ");
								k += 3;
							}
							//System.out.println();
						}
						if (vertices.length > 0) {
							System.out.println("udp: scene updated");
							scene.getModel().setTriangles(vertices, colors);
							scene.modelChanged();
						} else {
							System.out.println("udp: no data received");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createGeometry(ArrayList<ArrayList<Number>> objects) {
		ArrayList<float[]> geometry = new ArrayList<float[]>();
		for (ArrayList<Number> object : objects) {
			int i = 0;
			int n = object.size();
			float z = object.get(2).floatValue() / 1000f;
			float[] triangles = new float[object.size() * 6];
			for (int j = 0; j < n; j += 3) {
				triangles[i++] = object.get(j).floatValue() / 1000f;
				triangles[i++] = object.get(j+1).floatValue() / 1000f;
				triangles[i++] = 0;
				triangles[i++] = object.get((j+3) % n).floatValue() / 1000f;
				triangles[i++] = object.get((j+4) % n).floatValue() / 1000f;
				triangles[i++] = 0;
				triangles[i++] = object.get((j+3) % n).floatValue() / 1000f;
				triangles[i++] = object.get((j+4) % n).floatValue() / 1000f;
				triangles[i++] = z;
				
				triangles[i++] = object.get(j).floatValue() / 1000f;
				triangles[i++] = object.get(j+1).floatValue() / 1000f;
				triangles[i++] = 0;
				triangles[i++] = object.get((j+3) % n).floatValue() / 1000f;
				triangles[i++] = object.get((j+4) % n).floatValue() / 1000f;
				triangles[i++] = z;
				triangles[i++] = object.get(j).floatValue() / 1000f;
				triangles[i++] = object.get(j+1).floatValue() / 1000f;
				triangles[i++] = z;
			}
			geometry.add(triangles);
		}
		int n = 0;
		for (float[] triangles : geometry) {
			n += triangles.length;
		}
		int i = 0;
		float[] allTriangles = new float[n];
		for (float[] triangles : geometry) {
			System.arraycopy(triangles, 0, allTriangles, i, triangles.length);
			i += triangles.length;
		}
		if (allTriangles.length > 0) {
			scene.getModel().setTriangles(allTriangles, null);
		} else {
			System.out.println("empty scene");
		}
	}
}
