package application.keyword;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class EndLoopUntil extends CommandTerm {

	public EndLoopUntil(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedZeroArgs.fxml";
		parentTerm = null;
		rootTerm = null;
	}
	
	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedZeroArgsController)load.getController();
	}

	@Override protected void populateFXML() { }

	@Override public String toString() {
		return  indent() + "until " + ((Variable)(parentTerm.childTerms[1])).getVariableName() + " equals " + parentTerm.args.get(0) ;
	}
	
	@Override public boolean exec() {
		return true;
	}

	@Override public CommandTerm nextCommand() {
		if (!((LoopUntil)parentTerm).hasLoopFinished()) {
			return parentTerm;
		}
		return null;
	}
	
} // end of class EndLoopUntil
