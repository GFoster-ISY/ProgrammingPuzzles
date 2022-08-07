package maker;

public class SolutionPotColour {

	private int potId;
	private int red;
	private int blue;
	private int green;
	private int yellow;
	
	public SolutionPotColour(int id, int red, int blue, int green, int yellow) {
		setPotId(id);
		setRed(red);
		setBlue(blue);
		setGreen(green);
		setYellow(yellow);
	}

	public int getPotId() {
		return potId;
	}

	public void setPotId(int potId) {
		this.potId = potId;
	}

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getYellow() {
		return yellow;
	}

	public void setYellow(int yellow) {
		this.yellow = yellow;
	}

}
