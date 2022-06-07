package application.keyword;

import java.util.ArrayList;

import application.Ball;
import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Replace extends CommandTerm {

	public Replace(PuzzleController pc) {
		super(pc);
		FXMLFileName = "NestedZeroArgs.fxml";
		term = "replace";
	}

	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedZeroArgsController)load.getController();
	}

	@Override
	protected void populateFXML() { }


	@Override
	public boolean exec() {
		if (puzzleController.getHand().isEmpty()) {
			errorMessage = "There is no ball to put back in the container";
			return false;
		}
		Ball ball = puzzleController.getHand().getBall();
		puzzleController.getContainer().replace(ball);
		return true;
	}

}
