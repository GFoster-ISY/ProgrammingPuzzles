package application;

import javafx.scene.canvas.GraphicsContext;

public abstract class Container {

	protected Ball[] contents;
	protected int ballCount;
	
	public Container(int ballCount) {
		contents = new Ball[ballCount];
		this.ballCount = ballCount;
		for (int i = 0; i < ballCount; i++) {
			contents[i] = new Ball(i+1, 1, new Colour());
		}
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
	
	abstract void display(GraphicsContext gc, double width, double height);
}
