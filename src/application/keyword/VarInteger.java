package application.keyword;

import application.PuzzleController;

public class VarInteger extends Variable {

	protected int currentValue;
	public VarInteger(PuzzleController pc, String term, int id) {
		super(pc, term, id);
	}
	
	public VarInteger(PuzzleController pc, String term, String name, int value, int id, CommandTerm parent) {
		super(pc, term, name, value, id, parent);
	}
	
	public void reset() {
		super.reset();
		currentValue = getInitialNumber();
	}
	
	public int getInitialNumber() {
		return java.lang.Integer.parseInt(initialValue);
	}

	public int getNumber() {return currentValue;}
	public void setNumber(int value) {currentValue = value;}
	public void increment() {currentValue++;}

} // end of class Integer
