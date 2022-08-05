package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class EndLoopUntil extends EndLoopWithVariable {

	public EndLoopUntil(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedOneArg.fxml";
	}
	
	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedOneArgController)load.getController();
	}

	protected void populateFXML() {
		if (parentTerm != null) {
			nestedController.setName(((Variable)(parentTerm.childTerms[1])).getVariableName());
			nestedController.setArgValue(parentTerm.args);
			ArrayList<Boolean> req = new ArrayList<>();
			req.add(true);
			nestedController.setArgRequired(req);
		}
	}

	public void updateArgs() {
		if (parentTerm != null) {
			parentTerm.args.set(0,nestedController.getArgValue(0));
		}
	}

	@Override public String toString() {
		return  indent() + "until ";// + ((Variable)(parentTerm.childTerms[1])).getVariableName() + " equals " + parentTerm.args.get(0) ;
	}
	
} // end of class EndLoopUntil
