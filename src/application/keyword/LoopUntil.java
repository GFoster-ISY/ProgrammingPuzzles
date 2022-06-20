package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class LoopUntil extends CommandTerm {

	protected CommandTerm endLoop;
	protected Variable counter;

	
	public LoopUntil(PuzzleController pc) {
		super(pc);
		FXMLFileName = "NestedOneArg.fxml";
		commandTermName = "loop until";
		needsClosure = true;
		counter = new Variable(puzzleController, "counter", 0);
		puzzleController.addVariable(counter);
		endLoop = new EndLoopUntil(puzzleController, this);
	}

	public boolean hasLoopFinished() {
		int limit = Integer.parseInt(args.get(0));
		return (counter.getNumber() >= limit);
	}
	
	public int getLoopCounter() {return counter.getNumber();}
	public CommandTerm getChildTerm() {return endLoop;}
	@Override public CommandTerm getClosure() {return endLoop;}
	
	@Override protected void setController(FXMLLoader load) {
		controller = (NestedOneArgController)load.getController();
	}

	@Override protected void populateFXML() {
		controller.setName("Counter");
		controller.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		controller.setArgRequired(req);
	}


	public int argCount() {return 1;}
	
	@Override public String toString() {
		return  indent() + "loop";
	}
	
	@Override public void reset() {
		super.reset();
		counter.setNumber(0);
	}
	
	@Override public boolean exec() {
		if (Integer.parseInt(args.get(0)) < 1) {
			errorMessage = "The loop range is too small. The code contained within the loop will never be run.";
			return false;
		}
		return true;
	}


}
