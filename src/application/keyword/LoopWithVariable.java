package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;

public abstract class LoopWithVariable extends CommandTerm {

	public LoopWithVariable(PuzzleController pc
				           ,String term
				           ,int id
				           ,ObservableList<CommandTerm> listing
				           ) {
		super(pc, term, id);
		FXMLFileName = "NestedOneArg.fxml";
		commandTermName = "loop while";
		VarInteger counter = new VarInteger(puzzleController, "integer", pc.getNextId());
		// Use listing to determine if we have multiple loops
		int existingVars = varCount(listing);
		counter.setVariableName("counter"+(existingVars+1));
		counter.initialValue = "0";
		counter.setValue("0");
		counter.setParentTerm(this);
		counter.setRootTerm(this);
		childIds.add(counter.getId());
		childTerms = new CommandTerm[2];
		childTerms[1] = counter;
	}
	
	protected int varCount(ObservableList<CommandTerm> listing) {
		int varCount = 0;
		if (listing != null) {
			for (CommandTerm ct : listing) {
				if (ct instanceof LoopWithVariable) {
					varCount++;
				}
			}
		}
		return varCount;
	}
	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedOneArgController)load.getController();
	}
	
	
	@Override protected void addToListing(ObservableList<CommandTerm> list) {
		list.add(childTerms[1]); // variable declaration
		list.add(this);          // loop
		list.add(childTerms[0]); // end loop
	}
	
	@Override public int argCount() {return 1;}
	
	@Override protected void populateFXML() {
		nestedController.setName(((Variable)(childTerms[1])).getVariableName());
		nestedController.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		nestedController.setArgRequired(req);
	}
	
	@Override public void reset() {
		super.reset();
		((VarInteger)childTerms[1]).reset();
	}
	
	protected int getLoopLimit(int argPosn) {
		int loopLimit = 0;
		try {
			loopLimit = Integer.parseInt(args.get(argPosn));
		} catch (NumberFormatException e){
			Variable var = exec.getVariable(args.get(argPosn));
			if (var == null) {
				errorMessage = "There is no variable with the name " + args.get(argPosn);
				return -1;
			}
			try {
				loopLimit = ((VarInteger)var).getNumber();
			}catch (NumberFormatException ex) {
				errorMessage = "The variable " + args.get(argPosn) + " doesn't contain a number.";
				return -1;
			}
		}
		return loopLimit;
	}
	public abstract boolean hasLoopFinished();
	
	@Override public CommandTerm nextCommand() {
		if (hasLoopFinished()) {
			return childTerms[0];
		}
		return null;
	}

	public VarInteger getLoopCounter() {
		Variable var = (Variable)childTerms[1];
		String varName = var.getVariableName();
		return ((VarInteger)exec.getVariable(varName));
	}
	
	@Override public boolean exec() {
		return true;
	}

}
