package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class EndIf extends CommandTerm {

	CommandTerm ifCondition;
	public EndIf(PuzzleController pc, CommandTerm theIf) {
		super(pc);
		FXMLFileName = "NestedZeroArgs.fxml";
		term = "endif";
		closesIndent = true;
		ifCondition = theIf;
	}

	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedZeroArgsController)load.getController();
	}

	@Override
	protected void populateFXML() { }

	@Override public void setArgs() {args = new ArrayList<>();}

	@Override public String toString() {
		return  indent() + "end if";
	}
	@Override public boolean exec() {
		return true;
	}

}
