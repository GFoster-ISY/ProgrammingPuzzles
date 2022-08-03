package application.keyword;

import application.PuzzleController;

public class VarInteger extends Variable {

//	protected int currentValue;
	public VarInteger(PuzzleController pc, String term, int id) {
		super(pc, term, id);
	}
	
//	public VarInteger(PuzzleController pc, String term, String name, int value, int id, CommandTerm parent) {
//		super(pc, term, name, value, id, parent);
//	}
	
	public void reset() {
		super.reset();
		args.set(1, initialValue);
//		currentValue = getInitialNumber();
	}
	
	public int getInitialNumber() {
		return java.lang.Integer.parseInt(initialValue);
	}

	public int getNumber() {return Integer.parseInt(args.get(1));}
	public void setNumber(int value) {args.set(1, ""+value);}
	public void increment() {setNumber(getNumber()+1);}

} // end of class Integer
