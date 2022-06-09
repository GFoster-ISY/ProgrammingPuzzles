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
			bag = new Image(new FileInputStream("resources/empty-sack.png")); // Image by <a href="https://pixabay.com/users/mostafaelturkey36-13328910/?utm_source=link-attribution&amp;utm_medium=referral&amp;utm_campaign=image&amp;utm_content=5026598">Mostafa Elturkey</a> from <a href="https://pixabay.com/?utm_source=link-attribution&amp;utm_medium=referral&amp;utm_campaign=image&amp;utm_content=5026598">Pixabay</a>
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int imageHeight = (int)(Math.min(width, height));
		gc.drawImage(bag, 10, 5, imageHeight*2-20, imageHeight-10);
	}

	protected boolean lookedAtBall() {return false;}
}
