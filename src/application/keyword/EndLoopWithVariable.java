package application.keyword;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class EndLoopWithVariable extends CommandTerm {

	public EndLoopWithVariable(PuzzleController pc, String keywordName, int id) {
		super(pc, keywordName, id);
		FXMLFileName = "NestedZeroArg.fxml";
		parentTerm = null;
		rootTerm = null;
	}

	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedZeroArgsController)load.getController();
	}

	@Override protected void populateFXML() { }
	@Override public boolean exec() {return true;}
	@Override public CommandTerm nextCommand() {
		if (!((LoopWithVariable)parentTerm).hasLoopFinished()) {
			return parentTerm;
		}
		return null;
	}
}
