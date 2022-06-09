package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Bag extends Container {

	Image bag;
	
	public Bag(int ballCount) {
		super(ballCount);
	}

	public Bag(Map<String, Long> ballColour) {
		super(ballColour);
	}
	@Override
	void display(GraphicsContext gc, double width, double height) {
		gc.clearRect(0, 0, width, height);
		try {
			bag = new Image(new FileInputStream("resources/empty-sack.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int imageHeight = (int)(Math.min(width, height));
		gc.drawImage(bag, 10, 5, imageHeight*2-20, imageHeight-10);
	}

	protected boolean lookedAtBall() {return false;}
}
