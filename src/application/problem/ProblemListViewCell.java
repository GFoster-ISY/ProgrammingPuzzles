package application.problem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class ProblemListViewCell extends ListCell<Problem> {

	@FXML private GridPane gridPane;
	@FXML  Label name;
	@FXML  Label attempts;
	@FXML  Label successRate;
	@FXML  ImageView successIcon;
	
	private FXMLLoader mLLoader;
	
	@Override
    protected void updateItem(Problem problem, boolean empty) {
        super.updateItem(problem, empty);

        if(empty || problem == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("ProblemDisplayCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

            }

            name.setText(problem.toString());
            if (problem.getStats()!= null) {
            	try {
            		Image grade;
		            if (problem.getStats().getLastRun().equals("SUCCESS")) {
		            	if (problem.isCurrentProblem()) {
		            		gridPane.setBackground(new Background(new BackgroundFill(Color.rgb(30, 200, 30),CornerRadii.EMPTY,Insets.EMPTY)));
		            	} else if (isSelected()) {
		            		gridPane.setBackground(new Background(new BackgroundFill(Color.rgb(70, 200, 70),CornerRadii.EMPTY,Insets.EMPTY)));
		            	} else {
		            		gridPane.setBackground(new Background(new BackgroundFill(Color.rgb(140, 200, 140),CornerRadii.EMPTY,Insets.EMPTY)));
		            	}
		            	grade = new Image(new FileInputStream("resources/green-checkmark.jpg"));
		            } else {
		            	if (problem.isCurrentProblem()) {
		            		gridPane.setBackground(new Background(new BackgroundFill(Color.rgb(200, 30, 30),CornerRadii.EMPTY,Insets.EMPTY)));
		            	} else if (isSelected()) {
		            		gridPane.setBackground(new Background(new BackgroundFill(Color.rgb(200, 70, 70),CornerRadii.EMPTY,Insets.EMPTY)));
		            	} else {
		            		gridPane.setBackground(new Background(new BackgroundFill(Color.rgb(200, 140, 140),CornerRadii.EMPTY,Insets.EMPTY)));
		            	}
		            	grade = new Image(new FileInputStream("resources/red-cross.jpg"));
		            }
		            successIcon.setImage(grade);
            	} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		}
	            attempts.setText("Attempts " + problem.getStats().getAttempts());
	            successRate.setText("Success Rate " + problem.getStats().getSuccessRate());
            }
            setText(null);
            setGraphic(gridPane);
        }

    }
}
