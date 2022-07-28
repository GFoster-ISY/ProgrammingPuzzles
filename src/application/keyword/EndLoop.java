package application.keyword;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class EndLoop extends CommandTerm {

	public EndLoop(PuzzleController pc, CommandTerm loop, int id) {
		super(pc, "endloop", id);
		FXMLFileName = "NestedZeroArgs.fxml";
		parentTerm = loop;
		rootTerm = loop;
	}
	
	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedZeroArgsController)load.getController();
	}

	@Override protected void populateFXML() { }

	@Override public String toString() {
		return  indent() + "end loop";
	}
	@Override public boolean exec() {
		((Loop)parentTerm).incrementLoopCounter();
		return true;
	}

	@Override public CommandTerm nextCommand() {
		int limit = Integer.parseInt(((Loop)parentTerm).args.get(0));
		int count = ((Loop)parentTerm).getLoopCounter();
		if (count <= limit) {
			return parentTerm;
		}
		return null;
	}
} // end class EndLoop
