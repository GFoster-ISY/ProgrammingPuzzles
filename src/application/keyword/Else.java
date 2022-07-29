package application.keyword;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Else extends CommandTerm {

    public Else(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedZeroArgs.fxml";
		parentTerm = null;
		rootTerm = null;
		EndIf endIf = new EndIf(puzzleController, "endif", pc.getNextId());
		endIf.setParentTerm(this);
		endIf.setRootTerm(this);
		childIds.add(endIf.getId());
		childTerms = new CommandTerm[1];
		childTerms[0] = endIf;
	}
	
	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedZeroArgsController)load.getController();
	}

	@Override protected void populateFXML() {}
	
	@Override public String toString() {
		return  indent() + "else";
	}

	@Override public boolean exec() {
		return true;
	}
	
	@Override public CommandTerm nextCommand() {
		if (((If)parentTerm).conditionState) {
			return childTerms[0];
		}
		return null;
	}

} // end class Else
