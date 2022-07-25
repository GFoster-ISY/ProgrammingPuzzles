package application.keyword;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Else extends CommandTerm {

    public Else(PuzzleController pc, CommandTerm theIf, int id) {
		super(pc, "else", id);
		FXMLFileName = "NestedZeroArgs.fxml";
		parentTerm = theIf;
		rootTerm = theIf;
		EndIf endIf = new EndIf(puzzleController, this, pc.getNextId());
		childIds.add(endIf.getId());
		childTerms = new CommandTerm[1];
		childTerms[0] = endIf;
	}
	
	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedZeroArgsController)load.getController();
	}

	@Override
	protected void populateFXML() { }

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

}
