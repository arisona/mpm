package ch.ethz.fcl.mogl.ui;

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
