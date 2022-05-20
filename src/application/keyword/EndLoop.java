package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class EndLoop extends CommandTerm {

	protected Loop openLoop;
	
	public EndLoop(PuzzleController pc, CommandTerm loop) {
		super(pc);
		FXMLFileName = "NestedZeroArgs.fxml";
		term = "endloop";
		closesIndent = true;
		openLoop = (Loop)loop;
	}
	
	@Override public CommandTerm getClosure() {return null;}

	public CommandTerm getRelatedTerm() {return openLoop;}
	
	@Override protected void setController(FXMLLoader load) {
		controller = (NestedZeroArgsController)load.getController();
	}

	@Override protected void populateFXML() { }
	@Override public void setArgs() {args = new ArrayList<>();}

	@Override public String toString() {
		return  indent() + "end loop";
	}
	@Override public boolean exec() {
		openLoop.incrementLoopCounter();
		return true;
	}

	@Override public CommandTerm nextCommand() {
		int limit = Integer.parseInt(openLoop.args.get(0));
		int count = openLoop.getLoopCounter();
		if (count <= limit) {
			return openLoop;
		}
		return null;
	}
	
}
