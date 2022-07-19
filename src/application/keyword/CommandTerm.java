package application.keyword;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public abstract class CommandTerm {

	private String keyword;
	protected String rootKeyword;
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
	protected boolean hoverState;
	protected boolean hoverLinkedState;
	
	CommandTerm(PuzzleController pc, String keyword){
		errorMessage = null;
		puzzleController = pc;
		this.keyword = keyword;
		rootKeyword = keyword;
		needsClosure = false;
		closesIndent = false;
		indentLevel = 0;
		runningState = false;
		hoverState = false;
		hoverLinkedState = false;
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
	public String getKeyword() {return keyword;}
	public String getRootKeyword() {return rootKeyword;}
	public String getParentKeyword() {return "";}
	public boolean hasClosure() { return needsClosure;}
	public boolean getClosesIndent() { return closesIndent;}
	public CommandTerm getParentTerm() {return null;}
	public void setParent(CommandTerm ct) {}
	public CommandTerm getChildTerm() {return null;}
	public void setChild(CommandTerm ct) {}
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
	
	public static CommandTerm fromJSON(PuzzleController pc, JSONObject line, HashMap<String, ArrayDeque<CommandTerm>> openCT) {
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
					openCT.get(term).addLast(ct);
				}
			} else {
				ct = KeyTermController.getClosingKeyTerm(term, pc);
				if (ct.hasClosure()) {
					openCT.get(term).addLast(ct);
				}
			}
        } catch (UnknownKeywordException ex) {
        	System.err.println("UnknownKeywordException: " + ex.getMessage());
        	ex.printStackTrace();
        	return null;
        }
		return ct;
	}
	
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
	public void setHover(boolean state) {hoverState = state;}
	public boolean isHover() {return hoverState;}
	public void setHoverLinked(boolean state) {hoverLinkedState = state;}
	public boolean isHoverLinked() {return hoverLinkedState;}
	public CommandTerm nextCommand() {return null;}
	public void reset() {
		clearError();
		runningState = false;
	}
	
	protected abstract void setController(FXMLLoader load);
	protected abstract void populateFXML();
	public abstract boolean exec();
}
