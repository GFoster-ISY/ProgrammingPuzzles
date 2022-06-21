package application.keyword;

import java.util.ArrayList;

import application.Ball;
import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Increment extends CommandTerm {
	
	public Increment(PuzzleController pc, String term) {
		super(pc, term);
		FXMLFileName = "NestedOneArg.fxml";
		commandTermName = "increment";
	}

	protected void populateFXML () {
		controller.setName("Valiable Name");
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
		if (args.get(0).isEmpty()) {
			errorMessage = "No variable to increment was provided.";
			return false;
		}

		Variable var = puzzleController.getVariable(args.get(0));
		if (var == null) {
			errorMessage = "There is no variable with the name " + args.get(0);
			return false;
		}
		
		var.increment();
		return true;
	}

}
