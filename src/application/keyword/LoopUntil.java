package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;

public class LoopUntil extends CommandTerm {

	public LoopUntil(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedOneArg.fxml";
		commandTermName = "loop until";
		VarInteger counter = new VarInteger(puzzleController, "integer", pc.getNextId());
		counter.setVariableName("counter");
		counter.setValue("0");
		counter.setParentTerm(this);
		counter.setRootTerm(this);
		EndLoopUntil endLoop = new EndLoopUntil(puzzleController, "endloopuntil", pc.getNextId());
		endLoop.setParentTerm(this);
		endLoop.setRootTerm(this);
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

	@Override protected void addToListing(ObservableList<CommandTerm> list) {
		list.add(childTerms[1]); // variable declaration
		list.add(this);          // loop
		list.add(childTerms[0]); // end loop
	}

	public static int argCount() {return 1;}
	
	@Override public String toString() {
		return  indent() + "loop";
	}
	
	@Override public void reset() {
		super.reset();
		((VarInteger)childTerms[1]).reset();
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
		return (getLoopCounter().getNumber() >= limit);
	}
	
	public VarInteger getLoopCounter() {
		Variable var = (Variable)childTerms[1];
		String varName = var.getVariableName();
		return ((VarInteger)exec.getVariable(varName));
	}
	
} // end of class LoopUntil
