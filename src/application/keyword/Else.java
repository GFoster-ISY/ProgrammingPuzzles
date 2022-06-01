package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Else extends CommandTerm {


    protected CommandTerm endIf;
	CommandTerm ifCondition;
	public Else(PuzzleController pc, CommandTerm theIf) {
		super(pc);
		FXMLFileName = "NestedZeroArgs.fxml";
		term = "else";
		closesIndent = true;
		ifCondition = theIf;
		needsClosure = true;
		endIf = new EndIf(puzzleController, this);
	}

	public CommandTerm getRelatedTerm() {return endIf;}
	@Override public CommandTerm getClosure() {return endIf;}
	
	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedZeroArgsController)load.getController();
	}

	@Override
	protected void populateFXML() { }

	@Override public void setArgs() {args = new ArrayList<>();}

	@Override public String toString() {
		return  indent() + "else";
	}
	@Override public boolean exec() {
		return true;
	}
	
	@Override public CommandTerm nextCommand() {
		if (((If)ifCondition).conditionState) {
			return endIf;
		}
		return null;
	}

}
