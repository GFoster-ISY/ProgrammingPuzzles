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
		rootKeyword = "loop until";
		parentKeywordTerm = "loop until";
		closesIndent = true;
		openLoop = (LoopUntil)loop;
	}
	
	@Override public void setParent(CommandTerm ct) {openLoop = (LoopUntil)ct;}
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
