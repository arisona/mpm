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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import ch.ethz.fcl.mpm.Scene;
import ch.ethz.fcl.mpm.View;

public class ShadowVolumeRenderer implements IRenderer<View> {
	private static final float EXTRUDE = 100f;
	private static final float[] SHADOW_COLOR = { 1, 0, 0, 1 };
	
	private boolean enableShadows = false;

	@Override
	public void renderModel(GL2 gl, View view) {
		// enable depth test
		gl.glEnable(GL2.GL_DEPTH_TEST);

		// render ground plane
		gl.glColor4fv(Scene.GRID_COLOR, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(-0.5f, -0.5f, -0.001f);
		gl.glVertex3f(0.5f, -0.5f, -0.001f);
		gl.glVertex3f(0.5f, 0.5f, -0.001f);
		gl.glVertex3f(-0.5f, 0.5f, -0.001f);
		gl.glEnd();

		// render geometry
		renderGeometry(gl, view);
		
		//debug
		//gl.glColor4d(1.0, 1.0, 0.0, 0.5);
		//renderShadowVolumes(gl, view);

		// render shadow volumes
		if (enableShadows) renderShadows(gl, view);
		
		// cleanup
		gl.glDisable(GL2.GL_DEPTH_TEST);
	}
	
	public boolean getEnableShadows() {
		return enableShadows;
	}
	
	public void setEnableShadows(boolean enableShadows) {
		this.enableShadows = enableShadows;
	}

	private void renderGeometry(GL2 gl, View view) {
		gl.glColor3fv(Scene.MODEL_COLOR, 0);
		drawTriangles(gl, view.getScene().getModel().getModelFaces(), view.getScene().getModel().getModelNormals(), view.getScene().getModel().getModelColors());
	}

	private void renderShadows(GL2 gl, View view) {
		gl.glColorMask(false, false, false, false);
		gl.glDepthMask(false);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(0.0f, 100.0f);

		gl.glCullFace(GL.GL_FRONT);
		gl.glStencilFunc(GL.GL_ALWAYS, 0x0, 0xff);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_INCR, GL.GL_KEEP);
		renderShadowVolumes(gl, view);

		gl.glCullFace(GL.GL_BACK);
		gl.glStencilFunc(GL.GL_ALWAYS, 0x0, 0xff);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_DECR, GL.GL_KEEP);
		renderShadowVolumes(gl, view);

		gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
		gl.glDisable(GL.GL_CULL_FACE);
		gl.glColorMask(true, true, true, true);
		gl.glDepthMask(true);

		gl.glStencilFunc(GL.GL_NOTEQUAL, 0x0, 0xff);
		gl.glStencilOp(GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);
		renderShadowOverlay(gl, view);
		gl.glDisable(GL.GL_STENCIL_TEST);
	}

	private void renderShadowVolumes(GL2 gl, View view) {
		float[] vertices = view.getScene().getModel().getModelFaces();
		float[] normals = view.getScene().getModel().getModelNormals();
		float[] lightPosition = view.getScene().getLightPosition();
		float[] f = new float[9]; // current face (triangle)
		float[] e = new float[9]; // current extruded face
		
		for (int i = 0; i < vertices.length; i+=9) {
			if (dot(lightPosition, 0, normals, i) > 0) {
				f[0] = vertices[i+0];
				f[1] = vertices[i+1];
				f[2] = vertices[i+2];
				f[3] = vertices[i+3];
				f[4] = vertices[i+4];
				f[5] = vertices[i+5];
				f[6] = vertices[i+6];
				f[7] = vertices[i+7];
				f[8] = vertices[i+8];
			} else {
				f[0] = vertices[i+6];
				f[1] = vertices[i+7];
				f[2] = vertices[i+8];
				f[3] = vertices[i+3];
				f[4] = vertices[i+4];
				f[5] = vertices[i+5];
				f[6] = vertices[i+0];
				f[7] = vertices[i+1];
				f[8] = vertices[i+2];				
			}
			
			e[0] = f[0] - lightPosition[0];
			e[1] = f[1] - lightPosition[1];
			e[2] = f[2] - lightPosition[2];
			normalize(e, 0);
			scale(e, 0, EXTRUDE);
			add(e, 0, f, 0);
			e[3] = f[3] - lightPosition[0];
			e[4] = f[4] - lightPosition[1];
			e[5] = f[5] - lightPosition[2];
			normalize(e, 3);
			scale(e, 3, EXTRUDE);
			add(e, 3, f, 3);
			e[6] = f[6] - lightPosition[0];
			e[7] = f[7] - lightPosition[1];
			e[8] = f[8] - lightPosition[2];
			normalize(e, 6);
			scale(e, 6, EXTRUDE);
			add(e, 6, f, 6);
			
			// front cap & back cap
			gl.glBegin(GL2.GL_TRIANGLES);
			gl.glVertex3f(f[0], f[1], f[2]);
			gl.glVertex3f(f[3], f[4], f[5]);
			gl.glVertex3f(f[6], f[7], f[8]);
			gl.glVertex3f(e[6], e[7], e[8]);
			gl.glVertex3f(e[3], e[4], e[5]);
			gl.glVertex3f(e[0], e[1], e[2]);
			gl.glEnd();
			
			// sides
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(f[0], f[1], f[2]);
			gl.glVertex3f(e[0], e[1], e[2]);
			gl.glVertex3f(e[3], e[4], e[5]);
			gl.glVertex3f(f[3], f[4], f[5]);

			gl.glVertex3f(f[3], f[4], f[5]);
			gl.glVertex3f(e[3], e[4], e[5]);
			gl.glVertex3f(e[6], e[7], e[8]);
			gl.glVertex3f(f[6], f[7], f[8]);
			
			gl.glVertex3f(f[6], f[7], f[8]);
			gl.glVertex3f(e[6], e[7], e[8]);
			gl.glVertex3f(e[0], e[1], e[2]);
			gl.glVertex3f(f[0], f[1], f[2]);
			gl.glEnd();
		}
	}
	
	private void renderShadowOverlay(GL2 gl, View view) {
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0, 1, 1, 0, 0, 1);
		gl.glDisable(GL.GL_DEPTH_TEST);

		gl.glColor4fv(SHADOW_COLOR, 0);
		gl.glRectd(0, 0, 100, 100);

		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();
	}
	
	private static void drawTriangles(GL2 gl, float[] vertices, float[] normals, float[] colors) {
		if (colors == null) {
			gl.glColor4f(1f, 1f, 1f, 1f);
		}
		gl.glBegin(GL2.GL_TRIANGLES);
		for (int i = 0; i < vertices.length; i+=3) {
			if (colors != null) gl.glColor3fv(colors, i);
			if (normals != null) gl.glNormal3fv(normals, i);
			gl.glVertex3fv(vertices, i);
		}			
		gl.glEnd();		
	}


	private static float dot(float v1[], int i1, float v2[], int i2) {
		return (v1[i1] * v2[i2] + v1[i1 + 1] * v2[i2 + 1] + v1[i1 + 2] * v2[i2 + 2]);
	}

	private static void normalize(float v[], int i) {
		float f = 1.0f / (float) Math.sqrt(dot(v, i, v, i));
		v[i + 0] *= f;
		v[i + 1] *= f;
		v[i + 2] *= f;
	}
	
	private static void scale(float v[], int i, float scale) {
		v[i] *= scale;
		v[i+1] *= scale;
		v[i+2] *= scale;
	}
	
	private static void add(float[] v, int i, float[] w, int j) {
		v[i] += w[j];
		v[i+1] += w[j+1];
		v[i+2] += w[j+2];
	}

	/*
	private static void cross(float[] out, float[] v1, int i1, float[] v2, int i2) {
		out[0] = v1[i1 + 1] * v2[i2 + 2] - v1[i1 + 2] * v2[i2 + 1];
		out[1] = v1[i1 + 2] * v2[i2 + 0] - v1[i1 + 0] * v2[i2 + 2];
		out[2] = v1[i1 + 0] * v2[i2 + 1] - v1[i1 + 1] * v2[i2 + 0];
	}
	*/
}
