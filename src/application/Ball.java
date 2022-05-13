package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball {
	private int id;
	private int value;
	private Colour colour;
	
	public Ball(int id, int value, Colour c){
		this.id = id;
		this.value = value;
		colour = c;
	}

	public void display(GraphicsContext gc, int x, int y, int scale) {
		int diameter = scale/2-2;
		gc.setFill(Color.valueOf(colour.getCode()));
        gc.fillOval(x,y,diameter,diameter);
	}
}
