package application.keyword;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import application.PuzzleController;
import application.exec.Execute;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public abstract class CommandTerm {

	private int id;

	protected PuzzleController puzzleController;
	protected Execute exec;
	protected static NestedController nestedController;
	protected String FXMLFileName;

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
	
	/* These are the ids of the terms mentioned above.
	 * The recording of the ids is important since they will be held in the JSON file
	 * This will allow the objects to be recreated and the pairing to be made correctly.
	 */
	private int parentId;
	private int rootId;
	protected ArrayList<Integer> childIds;
	
	/* Each CommandTerm may have a different number of properties these are held as arguments
	 * The number of args is returned by argCount().
	 * These args are obtained either from the FXML file or from the JSON file.
	 * All args are held as String but may need to be converted to other data types.
	 */
	protected ArrayList<String> args;
		
	protected int indentLevel;
	
	protected boolean hoverState;
	protected boolean hoverLinkedState;
	
	protected String errorMessage;
	protected boolean runningState;
	
	CommandTerm(PuzzleController pc, String keywordName, int id){
		this.id = id;
		puzzleController = pc;
		exec = null;
		
		this.keyword = keywordName;
		commandTermName = keywordName;
		
		parentTerm = this;
		rootTerm = this;
		childTerms = new CommandTerm[0];
		
		parentId = id;
		rootId = id;
		childIds = new ArrayList<>();
		
		args = new ArrayList<>();
		for (int i = 0; i < argCount() ; i++) {
			args.add("");
		}

		indentLevel = 0;
		hoverState = false;
		hoverLinkedState = false;

		runningState = false;
		errorMessage = null;
	}

	public final void setExec(Execute e) {exec = e;}
	
	public final int getId() {return id;}

	public final String getKeyword() {return keyword;}

	public final CommandTerm getParentTerm() {return parentTerm;}
	public final void setParentTerm(CommandTerm ct) {parentTerm = ct;}
	public final CommandTerm getRootTerm() {return rootTerm;}
	public final void setRootTerm(CommandTerm ct) {rootTerm = ct;}
	public final CommandTerm[] getChildTerms() {return childTerms;}
	public final CommandTerm getPrimaryChildTerm() {
		if (childTerms.length > 0) {
			return childTerms[0];
		}
		return null;
	}
	public final void setChildTerms(HashMap<Integer, CommandTerm> commandTermById) {
		childTerms = new CommandTerm[childIds.size()];
		for (int i = 0; i < childIds.size(); i++) {
			childTerms[i] = commandTermById.get(childIds.get(i));
		}
	}

	public final int getParentId() {return parentId;}
	public final int getRootId() {return rootId;}
	public final ArrayList<Integer> getChildrenId() {return childIds;}
	
	
	public int argCount() {return 0;}
	public final void updateArgs() {
		for (int i = 0; i < argCount() ; i++) {
			args.set(i,nestedController.getArgValue(i));
		}
	}
	
	public final int getIndentLevel() {return indentLevel;}
	public final void setIndentLevel(int level) {indentLevel = level;}
	
	protected final String indent() {
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
			                          ,HashMap<String, ArrayDeque<CommandTerm>> openCT
			                          ,ObservableList<CommandTerm> listing
			                          ) {
		String term = (String)line.get("keyword");
		int id = ((Long)line.get("id")).intValue();
		pc.updateNextId(id);
		ArrayList<String> args = new ArrayList<>();
		if (line.containsKey("Arguments")) {
			JSONArray jsonArray = (JSONArray) line.get("Arguments");
			Iterator<String> iterator = jsonArray.iterator();
	         while(iterator.hasNext()) {
	           args.add(iterator.next());
	         }
		}
		ArrayList<Integer> childId = new ArrayList<>();
		JSONArray jsonArray = (JSONArray) line.get("childenId");
		Iterator<Long> iterator = jsonArray.iterator();
         while(iterator.hasNext()) {
        	 childId.add(iterator.next().intValue());
         }
		CommandTerm ct;
		try {
			ct = KeyTermController.getNewKeyTerm(term, pc, id, listing);
			// for the child commandTerms they will not have their parent or root details
			// so add the parent and root ids.
			// The terms will be added by ProblemHistory::modifyAllChildCommandTerms() after
			// this call is returned to ProblemHstory::readListingFromJSON()
			if (ct.getParentTerm() == null) {
				ct.parentId = ((Long)line.get("parentId")).intValue();
				ct.rootId = ((Long)line.get("rootId")).intValue();
			}
			ct.args = args;
			ct.childIds = childId;
			if(openCT.containsKey(term)) {
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
	
	protected void addToListing(ObservableList<CommandTerm> list) {
		list.add(this);
		for (CommandTerm ct : childTerms) {
			list.add(ct);
		}
	}
	protected void addExtraData(JSONObject line) {}
	public String errorMsg() { return errorMessage;}
	public void clearError() { errorMessage = null;}
	
	public void display(Pane fxmlEmbed, KeyTermController controller) {
		FXMLLoader load = new FXMLLoader(CommandTerm.class.getResource(FXMLFileName));
		try {
			fxmlEmbed.getChildren().add((Node)load.load());
			nestedController = load.getController();
			controller.setNestedController(nestedController);
			populateFXML ();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public final void setHover(boolean state) {hoverState = state;}
	public final boolean isHover() {return hoverState;}
	public final void setHoverLinked(boolean state) {hoverLinkedState = state;}
	public final boolean isHoverLinked() {return hoverLinkedState;}

	public final void setRunningState(boolean state) {runningState = state;}
	public final boolean isRunning() {return runningState;}
	public final boolean isInError() {return errorMessage != null;}
	public CommandTerm nextCommand() {return null;}
	public void reset() {
		clearError();
		runningState = false;
	}
	
	protected  abstract void setController(FXMLLoader load);
	protected void populateFXML() {};
	public abstract boolean exec();
	public final void abort() { errorMessage = "User aborted execution of the code.";}
}
