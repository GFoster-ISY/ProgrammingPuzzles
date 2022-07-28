package application.keyword;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class EndIf extends CommandTerm {

	public EndIf(PuzzleController pc, CommandTerm theElse, int id) {
		super(pc, "endif", id);
		FXMLFileName = "NestedZeroArgs.fxml";
		if (theElse != null) {
			parentTerm = theElse;
			rootTerm = theElse.getRootTerm();
		} else {
			parentTerm = null;
			rootTerm = null;
		}
	}
	
	@Override
	protected void setController(FXMLLoader load) {
		nestedController = (NestedZeroArgsController)load.getController();
	}

	@Override
	protected void populateFXML() { }

	@Override public String toString() {
		return  indent() + "end if";
	}
	@Override public boolean exec() {
		return true;
	}

}
