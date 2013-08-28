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

import java.nio.ByteBuffer;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/**
 * Simple, even primitive texture wrapper.
 * 
 * @author radar
 * 
 */
// XXX work in progress
public class Texture {
	private int tex;

	public Texture(GL2 gl) {
		// generate a VBO pointer / handle
		int[] buf = new int[1];
		gl.glGenTextures(1, buf, 0);
		tex = buf[0];

		gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
	    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
	    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
	
	public void dispose(GL2 gl) {
		gl.glDeleteTextures(1, new int[] { tex }, 0);
	}	
	
	public void load(GL2 gl, int width, int height, byte[] rgba) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
	    gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
	    ByteBuffer data = Buffers.newDirectByteBuffer(rgba);
	    data.rewind();
	    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, width, height, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, data);
	    gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);
	    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
	}
	
	public void enable(GL2 gl) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
		gl.glEnable(GL2.GL_TEXTURE_2D);
	}

	public void disable(GL2 gl) {
	    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}
}
