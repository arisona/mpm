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
package ch.ethz.fcl.mogl.gl;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import ch.ethz.fcl.mogl.scene.IView;

import com.jogamp.opengl.util.FPSAnimator;

/**
 * OpenGL frame class (i.e. an OpenGL window) that combines a GLCanvas and a
 * JFrame. TODO: The OpenGL Capabilities code here is still unflexible, and need
 * to be improved
 * 
 * @author radar
 * 
 */
public final class Frame extends GLCanvas {
	private static final long serialVersionUID = 3901950325854383346L;

	private static ArrayList<Frame> frames = new ArrayList<Frame>();

	private final JFrame jframe;
	private FPSAnimator animator;

	private IView view;

	/**
	 * Creates undecorated frame.
	 * 
	 * @param width
	 *            the frame's width
	 * @param height
	 *            the frame's height
	 */
	public Frame(int width, int height) {
		this(width, height, null);
	}

	/**
	 * Creates a decorated or undecorated frame with given dimensions
	 * 
	 * @param width
	 *            the frame's width
	 * @param height
	 *            the frame's height
	 * @param title
	 *            the frame's title, nor null for an undecorated frame
	 */
	public Frame(int width, int height, String title) {
		super(getCapabilities(), null, frames.isEmpty() ? null : frames.get(0).getContext(), null);
		frames.add(this);
		setPreferredSize(new Dimension(width, height));
		addGLEventListener(new GLEventListener() {
			private GLU glu;

			/**
			 * Called back immediately after the OpenGL context is initialized.
			 * Can be used to perform one-time initialization. Run only once.
			 */
			@Override
			public void init(GLAutoDrawable drawable) {
				GL2 gl = drawable.getGL().getGL2();
				gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
				glu = new GLU();
				if (view != null)
					view.init(drawable, gl, glu);
			}

			/**
			 * Called after resize. Also called when the drawable is first set
			 * to visible.
			 */
			@Override
			public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
				if (view != null)
					view.reshape(drawable, drawable.getGL().getGL2(), glu, x, y, width, height);
			}

			/**
			 * Called to perform rendering.
			 */
			@Override
			public void display(GLAutoDrawable drawable) {
				if (view != null)
					view.display(drawable, drawable.getGL().getGL2(), glu);
			}

			/**
			 * Notifies the listener to perform the release of all OpenGL
			 * resources per GLContext, such as memory buffers and GLSL
			 * programs.
			 */
			@Override
			public void dispose(GLAutoDrawable drawable) {
				if (view != null)
					view.dispose(drawable, drawable.getGL().getGL2(), glu);
			}
		});

		jframe = new JFrame();
		jframe.getContentPane().add(this);
		jframe.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (animator != null && animator.isStarted())
					animator.stop();
				frames.remove(Frame.this);
				if (frames.isEmpty())
					System.exit(0);
			}
		});
		if (title != null)
			jframe.setTitle(title);
		else
			jframe.setUndecorated(true);
		jframe.pack();
		jframe.setVisible(true);
	}

	/**
	 * Sets/clears the view for this frame.
	 * 
	 * @param view
	 *            The view to be assigned, or null if view to be cleared.
	 */
	public void setView(IView view) {
		if (this.view == view)
			return;
		removeMouseListener(this.view);
		removeMouseMotionListener(this.view);
		removeMouseWheelListener(this.view);
		removeKeyListener(this.view);
		this.view = view;
		if (this.view != null) {
			addMouseListener(this.view);
			addMouseMotionListener(this.view);
			addMouseWheelListener(this.view);
			addKeyListener(this.view);
		}
	}

	public JFrame getJFrame() {
		return jframe;
	}

	private static GLCapabilities getCapabilities() {
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(profile);
		caps.setAlphaBits(8);
		caps.setStencilBits(16);
		// caps.setSampleBuffers(true);
		// caps.setNumSamples(4);
		return caps;
	}
}
