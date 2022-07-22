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

	private int id;
	/* There are two types of keywords
	 * 1) keyword     - The display name in the lexicon and JSON files
	 * 2) commandName - The value used in formatted display and excludes any parenthesis
	 * The second version is used to format the display properly so put(n) can become put(2)
	 */
	protected String keyword;
	protected String commandTermName;
	/* There are three types of Terms a CommandTerm can have
	 * 1) parentTerm - the name of the CommandTerm this CommandTerm belongs with
	 * 2) rootTerm   - the name of the CommandTerm that originally started a chain of CommandTerm
	 * 3) childTerms - the name(s) of the CommandTerm that this will spawn
	 * 
	 * For example consider the if-else-endif block
	 * if is the root CommandTerm so the first two properties will be set to if, and childTerms will be set to [else, endif]
	 * else is a child of the if CommandTerm so the parentTerm and rootTerm will both be set to if and childTerms will be set to [endif]
	 * endif is a child of the else CommandTerm so parentTerm will be else the rootTerm will be if and childTerms will be set to []
	 * 
	 * thus:
	 * 
	 * if
	 *    parentTerm: if
	 *    rootTerm:   if
	 *    childTerms: [else, endif]
	 * else
	 *    parentTerm: if
	 *    rootTerm:   if
	 *    childTerms: [endif]
	 * endif
	 *    parentTerm: else
	 *    rootTerm:   if
	 *    childTerms: [] 
	 */
	protected CommandTerm parentTerm;
	protected CommandTerm rootTerm;
	protected CommandTerm[] childTerms;
	
	private int parentId;
	private int rootId;
	private ArrayList<String> childrenId;
	
	
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
	
	CommandTerm(PuzzleController pc, String keywordName, int id){
		this.id = id;
		errorMessage = null;
		puzzleController = pc;
		this.keyword = keywordName;
		parentTerm = this;
		rootTerm = this;
		childTerms = new CommandTerm[0];
		commandTermName = keywordName;
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

	public int getId() {return id;}
	public void updateArgs() {
		for (int i = 0; i < argCount() ; i++) {
			args.set(i,controller.getArgValue(i));
		}
	}
	
	public int argCount() {return 0;}
	public String getKeyword() {return keyword;}
	public CommandTerm getRootKeyword() {return rootTerm;}
	public CommandTerm getParentKeyword() {return parentTerm;}
	public int getParentId() {return parentId;}
	public int getRootId() {return rootId;}
	public void setParent(CommandTerm ct) {parentTerm = ct;}
	public void setRoot(CommandTerm ct) {rootTerm = ct;}
	
	public boolean hasClosure() { return needsClosure;}
	public boolean getClosesIndent() { return closesIndent;}
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
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("keyword", keyword);
		object.put("id", id);
		object.put("parentId", parentTerm.getId());
		object.put("rootId", rootTerm.getId());
		JSONArray array = new JSONArray();
		for(CommandTerm ct : childTerms){
			array.add(ct.getId());
		}
		object.put("childenId", array);
		if (args.size()>0) {
			array = new JSONArray();
			for(String arg : args){
				array.add(arg);
			}
			object.put("Arguments", array);
		}
		return object;
	}
	
	@SuppressWarnings("unchecked")
	public static CommandTerm fromJSON(PuzzleController pc
			                          ,JSONObject line
			                          ,HashMap<String
			                          ,ArrayDeque<CommandTerm>> openCT
			                          ,HashMap<Integer, CommandTerm> commandTermById
			                          ) {
		String term = (String)line.get("keyword");
		int id = ((Long)line.get("id")).intValue();
		pc.updateNextId(id);
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
			ct = KeyTermController.getNewKeyTerm(term, pc, id);
			// for the child commandTerms they will not have their parent or root details
			// so add the parent and root ids.
			if (ct.getParentKeyword() == null) {
				ct.parentId = (int)line.get("parentId");
				ct.rootId = (int)line.get("rootId");
			}
			ct.childrenId = new ArrayList<String>();
			JSONArray jsonArray = (JSONArray) line.get("childrenId");
			if (jsonArray != null) {
				Iterator<String> iterator = jsonArray.iterator();
		         while(iterator.hasNext()) {
		        	 ct.childrenId.add(iterator.next());
		         }
			}
			ct.args = args;
			if (ct.hasClosure()) {
				openCT.get(term).addLast(ct);
			}
        } catch (UnknownKeywordException ex) {
        	System.err.println("UnknownKeywordException: " + ex.getMessage());
        	ex.printStackTrace();
        	return null;
        }
		ct.addExtraData(line);
		return ct;
	}
	
	protected void addExtraData(JSONObject line) {}
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
