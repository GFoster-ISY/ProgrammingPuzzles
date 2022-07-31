package application.problem;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import application.PuzzleController;
import application.keyword.CommandTerm;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ProblemHistory {

    private PuzzleController controller;
    private Problem parent;
    String lastRun = "A new problem awaits.";
	int attempts;
	int failCount;
	int errorCount;
	public ObservableList<CommandTerm> previousRunListing;
	public ObservableList<CommandTerm> previousSuccessfulRunListing;
    
	public ProblemHistory(PuzzleController pc, Problem problem, JSONObject json) {
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
		previousRunListing = readListingFromJSON(json, "LastRunListing");
		previousRunListing.addListener(new ListChangeListener<CommandTerm>() {
	   	     public void onChanged(Change<? extends CommandTerm> c) {
	   	    	 ProblemManager.indentCode(previousRunListing);
	   	     }
			});
		controller.lstPreviousRun.setItems(previousRunListing);
		previousSuccessfulRunListing = readListingFromJSON(json, "LastSuccessfulListing");
		previousSuccessfulRunListing.addListener(new ListChangeListener<CommandTerm>() {
	   	     public void onChanged(Change<? extends CommandTerm> c) {
	   	    	 ProblemManager.indentCode(previousSuccessfulRunListing);
	   	     }
			});
		controller.lstPreviousSuccessfulRun.setItems(previousSuccessfulRunListing);
	}
	
	private ObservableList<CommandTerm> readListingFromJSON (JSONObject json, String type) {
		LinkedHashMap <Integer, CommandTerm> commandTermById = new LinkedHashMap <>();
		LinkedHashMap <String, ArrayDeque<CommandTerm>> openCommandTerm;
		openCommandTerm = initOpenCommandTerm();
		ObservableList<CommandTerm> listing = FXCollections.observableArrayList();
		JSONArray commands = (JSONArray)json.get(type);
		if (commands != null) {
			for (Object line : commands) {
				CommandTerm ct = CommandTerm.fromJSON(controller, (JSONObject)line, openCommandTerm);
				commandTermById.put(ct.getId(), ct);
		 	}
			listing = FXCollections.observableArrayList(commandTermById.values());
		 	modifyAllParentCommandTerms(commandTermById, listing);
			modifyAllChildCommandTerms(commandTermById, listing, openCommandTerm);
		 	ProblemManager.indentCode(listing);
		}
		return listing;
	}

	public String getLastRun() {return lastRun;}
	public int getAttempts() {return attempts;}
	public int getFailCount() {return failCount;}
	public int getErrorCount() {return errorCount;}
	public String getSuccessRate() {
		if (attempts == 0) return "";
		return "" + 100*(attempts-failCount-errorCount)/attempts + "%";
	}
	
	private LinkedHashMap <String, ArrayDeque<CommandTerm>> initOpenCommandTerm(){
		LinkedHashMap <String, ArrayDeque<CommandTerm>> openCommandTerm;
		openCommandTerm = new LinkedHashMap <>();
		openCommandTerm.put("loop",new ArrayDeque<>());
		openCommandTerm.put("loop until",new ArrayDeque<>());
		openCommandTerm.put("if",new ArrayDeque<>());
		openCommandTerm.put("else",new ArrayDeque<>());
		return openCommandTerm;
	}
	
	private void modifyAllParentCommandTerms(HashMap<Integer, CommandTerm> commandTermById
			,ObservableList<CommandTerm> commands) {
		for (CommandTerm ct : commands) {
			if (ct.getChildrenId().size() > 0) {
				ct.setChildTerms(commandTermById);
			}
		}
	}
	
	private void modifyAllChildCommandTerms(HashMap<Integer, CommandTerm> commandTermById
			,ObservableList<CommandTerm> commands,
			HashMap<String,ArrayDeque<CommandTerm>> openCT) {
		for (CommandTerm ct : commands) {
			if (ct.getParentTerm() == null) {
				ct.setParentTerm(commandTermById.get(ct.getParentId()));
				ct.setRootTerm(commandTermById.get(ct.getRootId()));
			}
		}
	}
}
