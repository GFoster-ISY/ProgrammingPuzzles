package maker;

public class BallColour {
	private String colour;
	private int amount;
	BallColour (String c, int n){
		setColour(c);
		setAmount(n);
	}
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
}
