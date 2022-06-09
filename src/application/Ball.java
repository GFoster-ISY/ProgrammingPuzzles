package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball {
	private int id;
	private int value;
	private boolean colourKnown; 
	private Colour colour;
	
	public Ball(int id, int value, Colour c){
		this.id = id;
		this.value = value;
		colourKnown = false;
		colour = c;
	}

	public void look() {
		colourKnown = true;
	}
	public Colour getColour() {
		if (colourKnown) {
			return colour;
		} else {
			return Colour.UNKNOWN_COLOUR;
		}
	}
	public void display(GraphicsContext gc, int x, int y, int scale) {
		int diameter = scale/2-2;
		gc.setFill(Color.valueOf(getColour().getCode()));
        gc.fillOval(x,y,diameter,diameter);
	}
}
