package application.keyword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public abstract class CommandTerm {

	private String keyword;
	protected String commandTermName;
	protected ArrayList<String> args;
	protected boolean needsClosure;
	protected boolean closesIndent;
	protected int indentLevel;
	
	protected NestedController controller;
	protected PuzzleController puzzleController;
	protected String FXMLFileName;
	
	protected String errorMessage;
	protected boolean runningState;
	
	CommandTerm(PuzzleController pc, String keyword){
		errorMessage = null;
		puzzleController = pc;
		this.keyword = keyword;
		needsClosure = false;
		closesIndent = false;
		indentLevel = 0;
		runningState = false;
		args = new ArrayList<>();
		for (int i = 0; i < argCount() ; i++) {
			args.add("");
		}
	}

	public void updateArgs() {
		for (int i = 0; i < argCount() ; i++) {
			args.set(i,controller.getArgValue(i));
		}
	}
	
	public int argCount() {return 0;}
	public boolean hasClosure() { return needsClosure;}
	public boolean getClosesIndent() { return closesIndent;}
	public CommandTerm getParentTerm() {return null;}
	public CommandTerm getChildTerm() {return null;}
	public int getIndentLevel() {return indentLevel;}
	public void setIndentLevel(int level) {indentLevel = level;}
	public void abort() { errorMessage = "User aborted execution of the code.";}
	protected String indent() {
		if (indentLevel > 0) return "   ".repeat(indentLevel);
		else return "";
	}
	public String toString() {
		String argList = String.join(",", args);
		return indent() + commandTermName + "(" + argList + ")";
	}
	
	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("keyword", keyword);
		if (args.size()>0) {
			JSONArray array = new JSONArray();
			for(String arg : args){
				array.add(arg);
			}
			object.put("Arguments", array);
		}
		return object;
	}
	
	public static CommandTerm fromJSON(PuzzleController pc, JSONObject line, Stack<CommandTerm> openCT) {
		String term = (String)line.get("keyword");
		ArrayList<String> args = new ArrayList<String>();
		if (line.containsKey("Arguments")) {
			JSONArray jsonArray = (JSONArray) line.get("Arguments");
			Iterator<String> iterator = jsonArray.iterator();
	         while(iterator.hasNext()) {
	           args.add(iterator.next());
	         }
		}
		CommandTerm ct;
		try {
			ct = KeyTermController.getNewKeyTerm(term, pc);
			if (ct != null) {
				ct.args = args;
				if (ct.hasClosure()) {
					openCT.push(ct);
				}
			} else {
				// TODO it could be a closing keyword such as endloop
				ct = KeyTermController.getClosingKeyTerm(term, pc, openCT);
			}
        } catch (UnknownKeywordException ex) {
        	System.err.println("UnknownKeywordException: " + ex.getMessage());
        	ex.printStackTrace();
        	return null;
        }
		return ct;
	}
	
	public CommandTerm getClosure() {return null;}
	public String errorMsg() { return errorMessage;}
	public void clearError() { errorMessage = null;}
	
	public void display(Pane fxmlEmbed, KeyTermController controller) {
		FXMLLoader load = new FXMLLoader(getClass().getResource(FXMLFileName));
		try {
			fxmlEmbed.getChildren().add((Node)load.load());
			setController(load);
			controller.setNestedController(this.controller);
			populateFXML ();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setRunningState(boolean state) {runningState = state;}
	public boolean isRunning() {return runningState;}
	public boolean isInError() {return errorMessage != null;}
	public CommandTerm nextCommand() {return null;}
	public void reset() {
		clearError();
		runningState = false;
	}
	
	protected abstract void setController(FXMLLoader load);
	protected abstract void populateFXML();
	public abstract boolean exec();
}
