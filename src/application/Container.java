package application;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.scene.canvas.GraphicsContext;

public abstract class Container {

	protected Ball[] contents;
	private Ball[] initialValue;
	protected int ballCount;
	protected abstract boolean lookedAtBall();
	
	public Container(int ballCount) {
		contents = new Ball[ballCount];
		this.ballCount = ballCount;
		for (int i = 0; i < ballCount; i++) {
			contents[i] = new Ball(i+1, 1, new Colour());
			if (lookedAtBall()) {contents[i].look();}
		}
		initialValue = Arrays.copyOf(contents, contents.length);
	}
	
	public Container(Map<String, Long> ballColour) {
		int count = 0;
		for (Long value: ballColour.values()) {
			count += value.intValue();
		}
		contents = new Ball[count];
		this.ballCount = count;
		int index = 0;
		// loop through each colour
		for (String key: ballColour.keySet()) {
			int total = ballColour.get(key).intValue();;
			// Loop through each ball of the current colour
			for (int i = 0; i < total; i++) {
				contents[index+i] = new Ball(index+i+1, 1, new Colour(key));
				if (lookedAtBall()) {contents[index+i].look();}
			}
			index += total;
		}
		List<Ball> ballList = Arrays.asList(contents);
		Collections.shuffle(ballList, new Random());
		ballList.toArray(contents);
		initialValue = Arrays.copyOf(contents, contents.length);
	}
	
	public Ball getBall() {
		if (ballCount > 0) {
			Ball ball = contents[ballCount-1];
			contents[ballCount-1]= null;
			ballCount--;
			return ball;
		}
		return null;
	}
	
	public Ball getBall(Colour colour) {
		for (int i = 0; i < ballCount; i++) {
			if (contents[i].getColour().equals(colour)){
				Ball ball = contents[i];
				contents[i] = contents[ballCount-1]; 
				contents[ballCount-1]= null;
				ballCount--;
				return ball;
			}
		}
		return null;
	}
	
	public int getBallCount() { return ballCount;}
	
	public void replace(Ball ball) {
		for (int count = ballCount; count>0; count--) {
			contents[count] = contents[count-1];
		}
		contents[0] = ball;
		ballCount++;
	}
	abstract void display(GraphicsContext gc, double width, double height);
	
	public void resetContainer() {
		ballCount = initialValue.length;
		contents = Arrays.copyOf(initialValue, initialValue.length);
	}
}
