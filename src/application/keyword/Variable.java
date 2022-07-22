package application.keyword;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Variable extends CommandTerm {
	
	protected String initialValue;
	public Variable(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedTwoArgs.fxml";
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

	@Override public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("keyword", keyword);
		if (args.size()>0) {
			JSONArray array = new JSONArray();
			array.add(args.get(0));
			array.add(initialValue);
			json.put("Arguments", array);
		}
		json.put("Parent", rootTerm);
		return json;
	}
	@Override protected void addExtraData(JSONObject line) {
		initialValue = args.get(1);
		if (line.containsKey("Parent")) {
			// TODO get the Object from an ID which also needs to be added to the JSON
			// rootKeyword = (String) line.get("Parent");
		}
		puzzleController.addVariable(this,false);

	}
}
