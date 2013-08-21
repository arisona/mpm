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
package ch.ethz.fcl.mogl.ui;

// XXX COMPLETELY INCOMPLETE (IE DEFUNCT)
public class Button {
	public interface IButtonAction {
		void execute(Button button);
	}
	
	private int x;
	private int y;
	private String label;
	private String help;
	private int key;
	private IButtonAction action;
	
	public Button(int x, int y, String label, String help, int key) {
		this(x, y, label, help, key, null);
	}

	public Button(int x, int y, String label, String help, int key, IButtonAction action) {
		this.x = x;
		this.y = y;
		this.label = label;
		this.help = help;
		this.action = action;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getHelp() {
		return help;
	}
	
	public int getKey() {
		return key;
	}
	
	public IButtonAction getAction() {
		return action;
	}
	
	public void setAction(IButtonAction action) {
		this.action = action;
	}
	
	protected void run() {
		if (action == null)
			throw new UnsupportedOperationException("button '" + label + "' has no action defined");
		action.execute(this);
	}
}
