package application;

import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tray extends Container {

	public Tray(int ballCount) {
		super(ballCount);
	}

	public Tray(Map<String, Long> ballColour) {
		super(ballColour);
	}
	@Override
	void display(GraphicsContext gc, double width, double height) {
		gc.clearRect(0, 0, width, height);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);
        gc.strokeRect(10, 5, width-20, height-10);
        int row = (int)Math.ceil(Math.sqrt(ballCount/2.0));
        int col = (int)Math.ceil((double)ballCount/row);
        int scale = (int) (Math.min((width-20)/2,height-10) / row);
		for (int i = 0; i < ballCount; i++) {
			contents[i].display(gc, (i%col)*scale+20, (i/col)*scale+10, scale);
		}
	}

}
