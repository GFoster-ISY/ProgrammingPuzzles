package application.problem;

import java.util.ArrayDeque;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import application.PuzzleController;
import application.keyword.CommandTerm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProblemStats {

    private PuzzleController controller;
    private Problem parent;
    String lastRun;
	int attempts;
	int failCount;
	int errorCount;
	public ObservableList<CommandTerm> previousRunListing;
	public ObservableList<CommandTerm> previousSuccessfulRunListing;
    
	public ProblemStats(PuzzleController pc, Problem problem, JSONObject json) {
		controller = pc;
		parent = problem;
		readFromJSONFile(json);
	}
	
	private void readFromJSONFile(JSONObject json) {
		if (json.containsKey("LastRun")) {
			lastRun = ((String)json.get("LastRun"));
		}
		if (json.containsKey("Attempts")) {
			attempts = ((Long)json.get("Attempts")).intValue();
		}
		if (json.containsKey("FailCount")) {
			failCount = ((Long)json.get("FailCount")).intValue();
		}
		if (json.containsKey("ErrorCount")) {
			errorCount = ((Long)json.get("ErrorCount")).intValue();
		}

    	HashMap<String, ArrayDeque<CommandTerm>> openCommandTerm;

    	previousRunListing = FXCollections.observableArrayList();
    	openCommandTerm = initOpenCommandTerm();
 	   	JSONArray commands = (JSONArray)json.get("LastRunListing");
 	   	if (commands != null) {
 	   		for (Object line : commands) {
 	   			previousRunListing.add(CommandTerm.fromJSON(controller, (JSONObject)line, openCommandTerm));
 	   		}
     	   controller.indentCode(controller.lstPreviousRun, previousRunListing);
 	   }
 	   modifyAllEndCommandTerms(previousRunListing, openCommandTerm);
 	   
 	   previousSuccessfulRunListing = FXCollections.observableArrayList();
 	   openCommandTerm = initOpenCommandTerm();
 	   commands = (JSONArray)json.get("LastSuccessfulListing");
 	   if (commands != null) {
     	   for (Object line : commands) {
     		  previousSuccessfulRunListing.add(CommandTerm.fromJSON(controller, (JSONObject)line, openCommandTerm));
     	   }
     	   controller.indentCode(controller.lstPreviousRun, previousSuccessfulRunListing);
 	   }
 	   modifyAllEndCommandTerms(previousSuccessfulRunListing, openCommandTerm);
	}

	public String getLastRun() {return lastRun;}
	public int getAttempts() {return attempts;}
	public int getFailCount() {return failCount;}
	public int getErrorCount() {return errorCount;}
	public String getSuccessRate() {return "" + 100*(attempts-failCount-errorCount)/attempts + "%";}
	
	private HashMap<String, ArrayDeque<CommandTerm>> initOpenCommandTerm(){
		HashMap<String, ArrayDeque<CommandTerm>> openCommandTerm;
		openCommandTerm = new HashMap<>();
		openCommandTerm.put("loop",new ArrayDeque<>());
		openCommandTerm.put("loop until",new ArrayDeque<>());
		openCommandTerm.put("if",new ArrayDeque<>());
		openCommandTerm.put("else",new ArrayDeque<>());
		return openCommandTerm;
	}
	private void modifyAllEndCommandTerms(ObservableList<CommandTerm> commands, HashMap<String, ArrayDeque<CommandTerm>> openCT) {
		for (CommandTerm ct : commands) {
			if (ct.getClosesIndent()) {
				ct.setParent(openCT.get(ct.getParentKeyword()).pollFirst());
				ct.getParentTerm().setChild(ct);
			}
		}
	}
}
