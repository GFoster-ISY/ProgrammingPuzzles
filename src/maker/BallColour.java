package maker;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class BallColour {
	private String colour;
	private int amount;
	private BooleanProperty filtered = new SimpleBooleanProperty();
	
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
		setFiltered(amount != 0);
		this.amount = amount;
	}
    public final BooleanProperty filteredProperty() {
        return this.filtered;
    } 
    public final void setFiltered(final boolean filtered) {
        this.filteredProperty().set(filtered);
    }
	
    public boolean isFiltered(boolean on) {
    	if (on) {
    		return this.filteredProperty().get();
    	} else {
    		return true;
    	}
	}

}
