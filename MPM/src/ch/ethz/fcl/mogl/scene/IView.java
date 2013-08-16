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
package ch.ethz.fcl.mogl.scene;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

/**
 * A 'view' here is a view with some control functionality, i.e. it handles the
 * rendering of the model and also the user input specific to the view.
 * 
 * @author radar
 * 
 */
public interface IView extends KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	IScene getScene();
	Camera getCamera();
	int getWidth();
	int getHeight();
	String getId();
	
	int[] getViewport();
	
	double[] getProjectionMatrix();
	void setProjectionMatrix(double[] projectionMatrix);

	double[] getModelviewMatrix();
	void setModelviewMatrix(double[] modelviewMatrix);
	
	GLU getGLU();
	
	/**
	 * Called immediately after the OpenGL context is initialized. Can be used
	 * to perform one-time initialization. Run only once. Caution: If you used
	 * same scene instance with multiple Frames, this will be called for
	 * initialization for each Frame.
	 */
	void init(GLAutoDrawable drawable, GL2 gl, GLU glu);

	/**
	 * Called after resize. Also called when the drawable is first set to
	 * visible.
	 */
	void reshape(GLAutoDrawable drawable, GL2 gl, GLU glu, int x, int y, int width, int height);

	/**
	 * Called to perform rendering.
	 */
	void display(GLAutoDrawable drawable, GL2 gl, GLU glu);

	/**
	 * Notifies the listener to perform the release of all OpenGL resources per
	 * GLContext, such as memory buffers and GLSL programs. Caution: Use this
	 * mechanism with care if you're using multiple Frames with shared contexts.
	 */
	void dispose(GLAutoDrawable drawable, GL2 gl, GLU glu);

	/**
	 * Can be called to request to repaint this view.
	 */
	void repaint();
}
