package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Hand {

	Image hand;
	Ball ball;
	
	public Hand() {
		ball = null;
	}
	
	public void display(GraphicsContext gc, double width, double height) {
		gc.clearRect(0, 0, width, height);
		try {
			hand = new Image(new FileInputStream("resources/hand.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int imageHeight = (int)(Math.min(width/2, height));
		gc.drawImage(hand, 10, 5, imageHeight*2-20, imageHeight-10);
		if (ball != null) {
			int scale = imageHeight/2; 
			ball.display(gc, imageHeight-scale/4, imageHeight/2-scale/2, scale);
		}
	}
	
	public boolean isEmpty() {return this.ball == null;}
	public boolean put(Ball ball) {
		if (isEmpty()) {
			this.ball = ball;
			return true;
		}
		return false;
	}
	
	public Ball getBall() {
		Ball ball = this.ball;
		this.ball = null;
		return ball;
	}

	public void lookAtBall() {
		ball.look();
	}
	public Colour getBallColour() {
		if (this.ball == null)
			return null;
		return this.ball.getColour();
	}

} // end of class Hand
