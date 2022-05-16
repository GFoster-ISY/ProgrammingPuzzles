package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Cup {
	private int id;
	private int number;
	private ArrayList<Ball> contents;
	private int ballCount;
	private static int cupWidth;
	private static int cupHeight;
	private static boolean grading;
	private boolean cupCorrect;
	
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
	
	public boolean correctBallCount (int expected) {return expected == ballCount;}
	public static void gradingCups (boolean grading) {Cup.grading = grading;}
	public void gradeCup(boolean good) {cupCorrect = good;}
	
	void display(GraphicsContext gc, double width, double height) {
        int x = 10+id*5*cupWidth;
        gc.setFill(Color.WHITE);
        gc.fillRect(x+cupWidth, 5+cupHeight/6, 2*cupWidth, cupHeight/3);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
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
        // Add grading ticks or crosses
        if (grading) {
        	Image grade;
        	try {
        		if (cupCorrect) {
        			grade = new Image(new FileInputStream("resources/green-checkmark.jpg"));
        		} else {
        			grade = new Image(new FileInputStream("resources/red-cross.jpg"));
        		}
        		gc.drawImage(grade, x+cupWidth*3/2, 10+cupHeight/6, cupWidth, cupWidth);
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		}
        }
	}	
}
