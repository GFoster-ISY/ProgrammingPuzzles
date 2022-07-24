package application.keyword;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class EndLoopUntil extends CommandTerm {

//	protected LoopUntil openLoop;
	
	public EndLoopUntil(PuzzleController pc, CommandTerm loop, int id) {
		super(pc, "endloopuntil", id);
		FXMLFileName = "NestedZeroArgs.fxml";
		parentTerm = loop;
		rootTerm = loop;
	}
	
	@Override public void setParent(CommandTerm ct) {parentTerm = (LoopUntil)ct;}
	
	@Override protected void setController(FXMLLoader load) {
		controller = (NestedZeroArgsController)load.getController();
	}

	@Override protected void populateFXML() { }

	@Override public String toString() {
		return  indent() + "until counter equals " + parentTerm.args.get(0) ;
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
	
}
