package application.keyword;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class EndIf extends CommandTerm {

	public EndIf(PuzzleController pc, String term, int id) {
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
		return  indent() + "end if";
	}
	
	@Override public boolean exec() {
		return true;
	}
} // end of class EndIf
