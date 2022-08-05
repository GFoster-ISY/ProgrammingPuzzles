package application.keyword;


import application.PuzzleController;
import javafx.collections.ObservableList;

public class LoopWhile extends LoopWithVariable {

	public LoopWhile(PuzzleController pc
		        ,String term
		        ,int id
		        ,ObservableList<CommandTerm> listing) {
		super(pc, term, id, listing);
		EndLoopWhile endLoop = new EndLoopWhile(puzzleController, "endloopwhile", pc.getNextId());
		endLoop.setParentTerm(this);
		endLoop.setRootTerm(this);
		childIds.add(endLoop.getId());
		childTerms[0] = endLoop;
	}

	@Override public String toString() {
		return  indent() + "loop while "  + ((Variable)(parentTerm.childTerms[1])).getVariableName() + " is less than or equal to " + parentTerm.args.get(0);
	}
	

	public boolean hasLoopFinished() {
		int loopLimit = getLoopLimit();
		return !(getLoopCounter().getNumber() <= loopLimit);
	}

} // end class LoopWhile