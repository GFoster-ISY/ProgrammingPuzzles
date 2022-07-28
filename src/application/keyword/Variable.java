package application.keyword;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Variable extends CommandTerm {
	
	protected String initialValue;
	public Variable(PuzzleController pc, String term, int id) {
		super(pc, "variable", id);
		FXMLFileName = "NestedTwoArgs.fxml";
		parentTerm = null;
		rootTerm = null;
		// The variable will be added to pc in addExtraData()
	}
	public Variable(PuzzleController pc, String name, int value, int id, CommandTerm parent) {
		super(pc, "variable", id);
		FXMLFileName = "NestedTwoArgs.fxml";
		args.set(0, name);
		args.set(1, ""+value);
		initialValue = args.get(1);
		parentTerm = parent;
		rootTerm = parent;
		pc.addVariable(this, true);
	}
	
	public String getName() {return args.get(0);}
	@Override public String toString() {
		return  indent() + args.get(0) + " is set to " + args.get(1);
	}
	@Override
	protected void setController(FXMLLoader load) {
		nestedController = (NestedTwoArgsController)load.getController();
	}
	
	@Override protected void populateFXML () {
		nestedController.setName("Variable Name",0);
		nestedController.setName("Initial Value",1);
		nestedController.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		req.add(true);
		nestedController.setArgRequired(req);
	}

	@SuppressWarnings("unchecked")
	@Override public JSONObject toJSON() {
		JSONObject json = super.toJSON();
		JSONArray originalArguments = (JSONArray) json.get("Arguments");
        JSONArray arguments = new JSONArray();
        arguments.add(originalArguments.get(0));
        arguments.add(initialValue);
        json.put("Arguments", arguments);
		return json;
	}
	public int argCount() {return 2;}
	
	public void increment() {
		int value = Integer.parseInt(args.get(1));
		value++;
		setNumber(value);
	}
	
	@Override public void reset() {
		super.reset();
		setNumber(resetNumber());
	}
	
	public int resetNumber() {
		return Integer.parseInt(initialValue);
	}
	
	public int getInitialNumber() {
		return Integer.parseInt(initialValue);
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

	@Override protected void addExtraData(JSONObject line) {
		initialValue = args.get(1);
		puzzleController.addVariable(this,false);
	}
}
