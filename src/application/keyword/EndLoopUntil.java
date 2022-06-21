package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class EndLoopUntil extends CommandTerm {

	protected LoopUntil openLoop;
	
	public EndLoopUntil(PuzzleController pc, CommandTerm loop) {
		super(pc, "endloopuntil");
		FXMLFileName = "NestedZeroArgs.fxml";
		commandTermName = "endloopuntil";
		closesIndent = true;
		openLoop = (LoopUntil)loop;
	}
	
	@Override public CommandTerm getClosure() {return null;}

	public CommandTerm getParentTerm() {return openLoop;}
	
	@Override protected void setController(FXMLLoader load) {
		controller = (NestedZeroArgsController)load.getController();
	}

	@Override protected void populateFXML() { }

	@Override public String toString() {
		return  indent() + "until counter equals " + openLoop.args.get(0) ;
	}
	@Override public boolean exec() {
		return true;
	}

	@Override public CommandTerm nextCommand() {
		if (!openLoop.hasLoopFinished()) {
			return openLoop;
		}
		return null;
	}
	
}
