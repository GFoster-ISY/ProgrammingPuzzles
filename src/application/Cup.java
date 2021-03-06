package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Cup {
	private int id;
	private int number;
	private ArrayList<Ball> contents;
	private int ballCount;
	private static int cupWidth;
	private static int cupHeight;
	private static int rows = 1;
	private static boolean grading;
	private boolean cupCorrect;
	
	public Cup(int i){
		id = i;
		number = i+1;
		contents = new ArrayList<>();
		ballCount = 0;
	}
	
	public static void setCupSize(int cupCount, int canvasWidth, int canvasHeight) {
    	int initialWidth =(canvasWidth-20)/(5*cupCount);
    	int initialHeight = (int) ((6*initialWidth)/Math.sqrt(2))+5;
    	if (cupCount > 4) {
    		rows = (int)Math.sqrt(canvasHeight/initialHeight);
    	} else {
    		rows = 1;
    	}
    	int width = initialWidth * rows;
    	int height = initialHeight * rows;

		cupWidth = width;
		cupHeight = height-(5*rows);
	}
	
	public void put(Ball ball) {
		contents.add(ball);
		ballCount++;
	}
	
	public boolean correctBallCount (int expected) {
		cupCorrect = (expected == ballCount);
		return cupCorrect;
	}
	public boolean correctBallCount (Map<String, Long> required) {
		
		for (String key: required.keySet()) {
			Colour currectColour = new Colour(key);
			int expected = required.get(key).intValue();
			var wrapper = new Object() {int ballColourCount = 0; };
			
			// Loop through each ball in the cup looking for those of the current colour
			contents.forEach(ball -> {
				if (ball.getColour().equals(currectColour)) {
					wrapper.ballColourCount++;
				}
			});
			if (wrapper.ballColourCount != expected) {
				cupCorrect = false;
				return cupCorrect;
			}
		}
		cupCorrect = true;
		return cupCorrect;
	}
	public static void gradingCups (boolean grading) {Cup.grading = grading;}
	public void gradeCup(boolean good) {cupCorrect = good;}
	
	void display(GraphicsContext gc, double width, double height) {
        int x = 10+(id/rows)*5*cupWidth;
        int y = id%rows*(cupHeight+5);
        gc.setFill(Color.WHITE);
        gc.fillRect(x+cupWidth, y+5+cupHeight/6, 2*cupWidth, cupHeight/3);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeRect(x+cupWidth, y+5+cupHeight/6, 2*cupWidth, cupHeight/3);
        gc.strokeLine(x, y+5, x+cupWidth, y+5+cupHeight);
        gc.strokeLine(x+cupWidth, y+5+cupHeight, x+3*cupWidth, y+5+cupHeight);
        gc.strokeLine(x+3*cupWidth, y+5+cupHeight, x+4*cupWidth, y+5);
        if (ballCount > 0) {
        	int d = cupWidth /2;
        	int scale = 4+2*d;
			for (int i = 0; i < ballCount; i++) {
				if (i < 4) {
					contents.get(i).display(gc, x+cupWidth+ d*i, y+cupHeight-d, scale);
				} else if (i < 9) {
					contents.get(i).display(gc, x+cupWidth+ (int)(d*(i-4.3)), y+cupHeight-2*d, scale);
				} else if (i < 15) {
					contents.get(i).display(gc, x+cupWidth+ (int)(d*(i-9.6)), y+cupHeight-3*d, scale);
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
        		gc.drawImage(grade, x+cupWidth*3/2, y+7+cupHeight/6, cupWidth, cupWidth);
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		}
        } else {
        	gc.setFill(Color.BLACK);
        	gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
            gc.fillText(""+number, x+cupWidth*2, y+7+cupHeight/6+cupWidth/2);
        }
	}	
}
