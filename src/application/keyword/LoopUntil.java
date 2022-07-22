package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class LoopUntil extends CommandTerm {

	protected CommandTerm endLoop;
	protected Variable counter;

	
	public LoopUntil(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedOneArg.fxml";
		commandTermName = "loop until";
		needsClosure = true;
		counter = puzzleController.getVariable("counter", 0, this);
		endLoop = new EndLoopUntil(puzzleController, this, pc.getNextId());
	}

	public boolean hasLoopFinished() {
		int limit = Integer.parseInt(args.get(0));
		return (counter.getNumber() >= limit);
	}
	
	public int getLoopCounter() {return counter.getNumber();}
	public CommandTerm getChildTerm() {return endLoop;}
	
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
		counter.resetNumber();
	}
	
	@Override public boolean exec() {
		if (Integer.parseInt(args.get(0)) < 1) {
			errorMessage = "The loop range is too small. The code contained within the loop will never be run.";
			return false;
		}
		return true;
	}


}
