package application.keyword;


import application.PuzzleController;
import javafx.collections.ObservableList;

public class LoopUntil extends LoopWithVariable {

	public LoopUntil(PuzzleController pc
			        ,String term
			        ,int id
			        ,ObservableList<CommandTerm> listing) {
		super(pc, term, id, listing);
		EndLoopUntil endLoop = new EndLoopUntil(puzzleController, "endloopuntil", pc.getNextId());
		endLoop.setParentTerm(this);
		endLoop.setRootTerm(this);
		childIds.add(endLoop.getId());
		childTerms[0] = endLoop;
	}

	@Override public String toString() {
		return  indent() + "loop";
	}
	
	public boolean hasLoopFinished() {
		int loopLimit = getLoopLimit();
		return (getLoopCounter().getNumber() >= loopLimit);
	}
	
} // end of class LoopUntil
