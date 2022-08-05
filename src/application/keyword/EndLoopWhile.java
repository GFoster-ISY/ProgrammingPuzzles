package application.keyword;

import application.PuzzleController;

public class EndLoopWhile extends EndLoopWithVariable {

	public EndLoopWhile(PuzzleController pc, String term, int id) {
		super(pc, term, id);
	}

	@Override public String toString() {
		return  indent() + "end while";
	}

}
