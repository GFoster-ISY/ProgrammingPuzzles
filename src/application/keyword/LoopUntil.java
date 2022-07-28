package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class LoopUntil extends CommandTerm {

	public LoopUntil(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedOneArg.fxml";
		commandTermName = "loop until";
		Variable counter = puzzleController.getVariable("counter", 0, this);
		EndLoopUntil endLoop = new EndLoopUntil(puzzleController, this, pc.getNextId());
		childIds.add(endLoop.getId());
		childIds.add(counter.getId());
		childTerms = new CommandTerm[2];
		childTerms[0] = endLoop;
		childTerms[1] = counter;
	}

	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedOneArgController)load.getController();
	}

	@Override protected void populateFXML() {
		nestedController.setName("Counter");
		nestedController.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		nestedController.setArgRequired(req);
	}

	@Override public int argCount() {return 1;}
	
	@Override public String toString() {
		return  indent() + "loop";
	}
	
	@Override public void reset() {
		super.reset();
		((Variable)childTerms[1]).resetNumber();
	}
	
	@Override public boolean exec() {
		if (Integer.parseInt(args.get(0)) < 1) {
			errorMessage = "The loop range is too small. The code contained within the loop will never be run.";
			return false;
		}
		return true;
	}

	public boolean hasLoopFinished() {
		int limit = Integer.parseInt(args.get(0));
		return (getLoopCounter() >= limit);
	}
	
	public int getLoopCounter() {return ((Variable)childTerms[1]).getNumber();}
	
} // end of class LoopUntil
