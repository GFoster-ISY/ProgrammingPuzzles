package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Loop extends CommandTerm {

	int loopCounter = 1;
	
	public Loop(PuzzleController pc) {
		super(pc);
		FXMLFileName = "NestedOneArg.fxml";
		term = "loop";
		closure = true;
	}

	public int getLoopCounter() {return loopCounter;}
	public void incrementLoopCounter() {loopCounter++;}
	
	@Override protected void setController(FXMLLoader load) {
		controller = (NestedOneArgController)load.getController();
	}

	@Override protected void populateFXML() {
		controller.setName("Times");
		controller.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		controller.setArgRequired(req);
	}

	@Override public void setArgs() {
		args = new ArrayList<>();
		args.add(controller.getArgValue(0));
	}

	@Override public String toString() {
		if (args == null) {
			setArgs();
		}
		return  indent() + "loop from 1 to " + args.get(0);
	}
	
	@Override public void reset() {
		super.reset();
		loopCounter=1;
	}
	
	@Override public boolean exec() {
		if (Integer.parseInt(args.get(0)) < 1) {
			errorMessage = "The loop range is too small. The code contained within the loop will never be run.";
			return false;
		}
		return true;
	}

	@Override public CommandTerm getClosure() {return new EndLoop(puzzleController, this);}
}
