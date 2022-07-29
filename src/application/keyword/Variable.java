package application.keyword;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public abstract class Variable extends CommandTerm {
	
	protected String initialValue;
	
	public Variable(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedTwoArgs.fxml";
		parentTerm = null;
		rootTerm = null;
		// The variable will be added to pc in addExtraData()
	}

	public Variable(PuzzleController pc, String term, String name, int value, int id, CommandTerm parent) {
		super(pc, term, id);
		FXMLFileName = "NestedTwoArgs.fxml";
		args.set(0, name);
		args.set(1, ""+value);
		initialValue = args.get(1);
		parentTerm = parent;
		rootTerm = parent;
	}
	
	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedTwoArgsController)load.getController();
	}

	@Override public String toString() {
		return  indent() + args.get(0) + " is set to " + args.get(1);
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
	
	public static int argCount() {return 2;}
	
	@Override protected void addExtraData(JSONObject line) {
		initialValue = args.get(1);
	}

	@Override public void reset() {
		super.reset();
		setValue(initialValue);
	}
	@Override public boolean exec() {
		return true;
	}

	public final String getVariableName() {return args.get(0);}
	public final void setVariableName(String name) {args.set(0, name);}
	public final String getValue() {return args.get(1);}
	public final void setValue(String value) {
		args.set(1, value);
	}
	
} // end of class Variable
