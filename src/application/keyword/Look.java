package application.keyword;

import java.util.ArrayList;

import application.Ball;
import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Look extends CommandTerm {
	
	public Look(PuzzleController pc) {
		super(pc);
		FXMLFileName = "NestedZeroArgs.fxml";
		commandTermName = "look";
	}
	
	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedZeroArgsController)load.getController();
	}
	
	@Override protected void populateFXML () {}

	@Override public boolean exec() {
		if (puzzleController.getHand().isEmpty()) {
			errorMessage = "There is no ball to look at.";
			return false;
		}
		puzzleController.getHand().lookAtBall();
		return true;
	}

}
