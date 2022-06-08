package application.keyword;

import java.util.ArrayList;

import application.Ball;
import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Variable extends CommandTerm {
		
	public Variable(PuzzleController pc) {
		super(pc);
		FXMLFileName = "NestedTwoArgs.fxml";
		term = "variable";
	}
	public Variable(PuzzleController pc, String name, int value) {
		super(pc);
		FXMLFileName = "NestedTwoArgs.fxml";
		term = "variable";
		args.set(0, name);
		args.set(1, ""+value);
	}
	
	public String getName() {return args.get(0);}
	@Override public String toString() {
		return  indent() + args.get(0) + " is set to " + args.get(1);
	}
	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedTwoArgsController)load.getController();
	}
	
	@Override protected void populateFXML () {
		controller.setName("Variable Name",0);
		controller.setName("Initial Value",1);
		controller.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		req.add(true);
		controller.setArgRequired(req);
	}


	public int argCount() {return 2;}
	
	public void increment() {
		int value = Integer.parseInt(args.get(1));
		value++;
		setNumber(value);
	}
	
	public int getNumber() {
		return Integer.parseInt(args.get(1));
	}
	
	public void setNumber(int value) {
		args.set(1, ""+value);
	}

	@Override public boolean exec() {
		return true;
	}

}
