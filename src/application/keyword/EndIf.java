package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class EndIf extends CommandTerm {

	CommandTerm elseCondition;
	public EndIf(PuzzleController pc, CommandTerm theElse) {
		super(pc);
		FXMLFileName = "NestedZeroArgs.fxml";
		commandTermName = "endif";
		closesIndent = true;
		elseCondition = theElse;
	}
	public CommandTerm getParentTerm() {return elseCondition;}
	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedZeroArgsController)load.getController();
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
