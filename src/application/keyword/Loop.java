package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Loop extends CommandTerm {

	int loopCounter = 1;
	
	public Loop(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedOneArg.fxml";
		commandTermName = "loop";
		EndLoop endLoop = new EndLoop(puzzleController, this, pc.getNextId());
		childIds.add(endLoop.getId());
		childTerms = new CommandTerm[1];
		childTerms[0] = endLoop;
	}

	public int getLoopCounter() {return loopCounter;}
	public void setChild(CommandTerm ct) {childTerms[0] = ct;}
	public void incrementLoopCounter() {loopCounter++;}
	
	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedOneArgController)load.getController();
	}

	@Override protected void populateFXML() {
		nestedController.setName("Times");
		nestedController.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		nestedController.setArgRequired(req);
	}


	public int argCount() {return 1;}

	@Override public String toString() {
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


}
