package application.keyword;

import java.util.ArrayList;

import application.Ball;
import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Pick extends CommandTerm {
	
	public Pick(PuzzleController pc) {
		super(pc);
		FXMLFileName = "NestedZeroArgs.fxml";
		term = "pick";
	}
	
	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedZeroArgsController)load.getController();
	}
	
	@Override protected void populateFXML () {}

	@Override public void setArgs() {
		args = new ArrayList<>();
	}

	protected Ball getBall() {
		return puzzleController.getContainer().getBall();
	}
	@Override public boolean exec() {
		if (!puzzleController.getHand().isEmpty()) {
			errorMessage = "The hand is already holding a ball";
			return false;
		}
		Ball ball = getBall();
		if (ball != null) {
			puzzleController.getHand().put(ball);
			return true;
		} else {
			errorMessage = "No ball is available to pick up";
			return false;
		}
	}

}
