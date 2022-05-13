package application;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cup {
	private int id;
	private int number;
	private ArrayList<Ball> contents;
	private int ballCount;
	private static int cupWidth;
	private static int cupHeight;
	
	public Cup(int i){
		id = i;
		contents = new ArrayList<>();
		ballCount = 0;
	}
	
	public static void setCupSize(int cupCount, int canvasWidth, int canvasHeight) {
		int displayWidth = (int)(Math.min(canvasWidth, canvasHeight*2));
    	int width =(displayWidth-20)/(5*cupCount);
    	int height = (int) ((6*width)/Math.sqrt(2));

		cupWidth = width;
		cupHeight = height;
	}
	
	public void put(Ball ball) {
		contents.add(ball);
		ballCount++;
	}
	
	void display(GraphicsContext gc, double width, double height) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        int x = 10+id*5*cupWidth;
        gc.strokeRect(x+cupWidth, 5+cupHeight/6, 2*cupWidth, cupHeight/3);
        gc.strokeLine(x, 5, x+cupWidth, 5+cupHeight);
        gc.strokeLine(x+cupWidth, 5+cupHeight, x+3*cupWidth, 5+cupHeight);
        gc.strokeLine(x+3*cupWidth, 5+cupHeight, x+4*cupWidth, 5);
        if (ballCount > 0) {
        	int d = cupWidth /2;
        	int scale = 4+2*d;
			for (int i = 0; i < ballCount; i++) {
				if (i < 4) {
					contents.get(i).display(gc, x+cupWidth+ d*i, cupHeight-d, scale);
				} else if (i < 9) {
					contents.get(i).display(gc, x+cupWidth+ (int)(d*(i-4.3)), cupHeight-2*d, scale);
				} else if (i < 15) {
					contents.get(i).display(gc, x+cupWidth+ (int)(d*(i-9.6)), cupHeight-3*d, scale);
				}

			}
        }
	}	
}
