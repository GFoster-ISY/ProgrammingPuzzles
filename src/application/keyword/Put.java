package application.keyword;

import java.util.ArrayList;

import application.Ball;
import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Put extends CommandTerm {
	
	public Put(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedOneArg.fxml";
		commandTermName = "put";
	}

	protected void populateFXML () {
		nestedController.setName("Pot number");
		nestedController.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		nestedController.setArgRequired(req);
	}

	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedOneArgController)load.getController();
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
		int cupNumber = 0;
		try {
			cupNumber = Integer.parseInt(args.get(0))-1;// Subtract one to convert to array indices
		} catch (NumberFormatException e) {
			Variable var = puzzleController.getVariable(args.get(0));
			if (var == null) {
				errorMessage = "There is no variable with the name " + args.get(0);
				return false;
			}
			try {
				cupNumber = var.getNumber();
			}catch (NumberFormatException ex) {
				errorMessage = "The variable " + args.get(0) + " doesn't contain a number.";
				return false;
			}
		}
		int selectedCup =  cupNumber;
		if (selectedCup < 0 || selectedCup >= puzzleController.getCups().length) {
			errorMessage = "There is no cup at position " + (selectedCup+1);
			return false;
		}
		Ball ball = puzzleController.getHand().getBall();
		puzzleController.getCups()[selectedCup].put(ball);
		return true;
	}

}
