package application.keyword;

import java.util.ArrayList;

import application.Ball;
import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Increment extends CommandTerm {
	
	public Increment(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedOneArg.fxml";
		commandTermName = "increment";
	}

	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedOneArgController)load.getController();
	}
	
	@Override protected void populateFXML () {
		nestedController.setName("Valiable Name");
		nestedController.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		nestedController.setArgRequired(req);
	}

	@Override public int argCount() {return 1;}
	
	@Override public boolean exec() {
		if (args.get(0).isEmpty()) {
			errorMessage = "No variable to increment was provided.";
			return false;
		}

		VarInteger var = ((VarInteger) exec.getVariable(args.get(0)));
		if (var == null) {
			errorMessage = "There is no variable with the name " + args.get(0);
			return false;
		}
		
		var.increment();
		return true;
	}

}
