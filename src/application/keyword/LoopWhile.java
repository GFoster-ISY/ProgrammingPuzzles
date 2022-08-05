package application.keyword;


import java.util.ArrayList;

import application.PuzzleController;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;

public class LoopWhile extends LoopWithVariable {

	public LoopWhile(PuzzleController pc
		        ,String term
		        ,int id
		        ,ObservableList<CommandTerm> listing) {
		super(pc, term, id, listing);
		FXMLFileName = "NestedWhile.fxml";
		EndLoopWhile endLoop = new EndLoopWhile(puzzleController, "endloopwhile", pc.getNextId());
		endLoop.setParentTerm(this);
		endLoop.setRootTerm(this);
		childIds.add(endLoop.getId());
		childTerms[0] = endLoop;
	}

	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedWhileController)load.getController();
	}

	@Override protected void populateFXML() {
		// Find out how many loop variable exist
		int varCount = varCount(puzzleController.getFullListing());
		// If this commandTerm hasn't been added yet, then increment by one
		if (!(puzzleController.getFullListing().contains(this))) {
			varCount++;
		}
		((NestedWhileController)nestedController).setVariableList(varCount);
		nestedController.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		req.add(true);
		req.add(true);
		nestedController.setArgRequired(req);
	}

	public int argCount() {return 3;}

	@Override public String toString() {
		// TODO needed until the JSON file is updated with 3 args
		if (args.size() < 3) {
			return "loop while";
		} else {
			return  indent() + "loop while "  
		            + ((Variable)(childTerms[1])).getVariableName() 
		            + " is " + args.get(1) + " "
		            + args.get(2);
		}
	}
	

	public boolean hasLoopFinished() {
		int loopLimit = getLoopLimit(2);
		String condition = args.get(1);
		if (condition.equals("less than")) {
			return !(getLoopCounter().getNumber() < loopLimit);
		} else if (condition.equals("less than or equal to")) {
			return !(getLoopCounter().getNumber() <= loopLimit);
		} else {
			System.err.println("Unknown condition: " + condition);
			return true; // Unknown condition
		}
	}

} // end class LoopWhile
