package application.keyword;

import application.Ball;
import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Pick extends CommandTerm {
	
	public Pick(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedZeroArgs.fxml";
		commandTermName = "pick";
	}
	
	@Override
	protected void setController(FXMLLoader load) {
		nestedController = (NestedZeroArgsController)load.getController();
	}
	
	@Override protected void populateFXML () {}

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
