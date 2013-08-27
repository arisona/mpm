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

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/**
 * Simple, even primitive VBO wrapper.
 * 
 * @author radar
 * 
 */
// XXX work in progress
public class VBO {
	private int vboV;
	private int vboN;
	private int vboC;
	private boolean hasNormals;
	private boolean hasColors;
	private int size;

	public VBO(GL2 gl) {
		this(gl, false, false);
	}
	
	public VBO(GL2 gl, boolean hasNormals, boolean hasColors) {
		// generate a VBO pointer / handle
		int[] buf = new int[3];
		gl.glGenBuffers(3, buf, 0);
		vboV = buf[0];
		vboN = buf[1];
		vboC = buf[2];
		this.hasNormals = hasNormals;
		this.hasColors = hasColors;
	}
	
	public void load(GL2 gl, float[] vertices) {
		load(gl, vertices.length / 3, vertices, null, null);
	}

	public void load(GL2 gl, int numVertices, float[] vertices, float[] normals, float[] colors) {
		size = numVertices * 3;

		if (vertices != null) {
			FloatBuffer data = Buffers.newDirectFloatBuffer(vertices);
			data.rewind();

			int bytesPerFloat = Float.SIZE / Byte.SIZE;

			// transfer data to VBO
			int numBytes = data.capacity() * bytesPerFloat;
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboV);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, numBytes, data, GL.GL_STATIC_DRAW);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		}

		if (hasNormals && normals != null) {
			FloatBuffer data = Buffers.newDirectFloatBuffer(normals);
			data.rewind();

			int bytesPerFloat = Float.SIZE / Byte.SIZE;

			// transfer data to VBO
			int numBytes = data.capacity() * bytesPerFloat;
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboN);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, numBytes, data, GL.GL_STATIC_DRAW);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		}

		if (hasColors && colors != null) {
			FloatBuffer data = Buffers.newDirectFloatBuffer(colors);
			data.rewind();

			int bytesPerFloat = Float.SIZE / Byte.SIZE;

			// transfer data to VBO
			int numBytes = data.capacity() * bytesPerFloat;
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboC);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, numBytes, data, GL.GL_STATIC_DRAW);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		}
	}

	public void render(GL2 gl, int mode) {
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboV);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

		if (hasNormals) {
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboN);
			gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL.GL_FLOAT, 0, 0);
		}

		if (hasColors) {
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboC);
			gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL.GL_FLOAT, 0, 0);
		}

		gl.glDrawArrays(mode, 0, size / 3);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		if (hasNormals)
			gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		if (hasColors)
			gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	}

	public void dispose(GL2 gl) {
		gl.glDeleteBuffers(3, new int[] { vboV, vboN, vboC }, 0);
	}
}
