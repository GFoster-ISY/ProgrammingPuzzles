package application.keyword;

import java.util.ArrayList;

import application.Ball;
import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Put extends CommandTerm {
	
	public Put(PuzzleController pc) {
		super(pc);
		FXMLFileName = "NestedOneArg.fxml";
		term = "put";
	}

	protected void populateFXML () {
		controller.setName("Pot number");
		controller.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		controller.setArgRequired(req);
	}

	@Override protected void setController(FXMLLoader load) {
		controller = (NestedOneArgController)load.getController();
	}
	

	public int argCount() {return 1;}
	
	@Override public boolean exec() {
		if (puzzleController.getHand().isEmpty()) {
			errorMessage = "There is no ball to place in a cup";
			return false;
		}
		if (args.get(0).isEmpty()) {
			errorMessage = "No cup number, for where to put the ball was given.";
			return false;
		}
		int selectedCup = Integer.parseInt(args.get(0))-1; // Subtract one to convert to array indices
		if (selectedCup < 0 || selectedCup >= puzzleController.getCups().length) {
			errorMessage = "There is no cup at position " + (selectedCup+1);
			return false;
		}
		Ball ball = puzzleController.getHand().getBall();
		puzzleController.getCups()[selectedCup].put(ball);
		return true;
	}

}
