package application.problem;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import application.PuzzleController;
import application.keyword.CommandTerm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;

public class ProblemManager {

	private ObservableList<Problem> problemListing;
	private ObservableList<ProblemHistory> statsListing;
	private Problem currentProblem;
    private JSONObject generalJSONObject;
    private PuzzleController controller;
	
	public ProblemManager(PuzzleController pc) {
    	problemListing = FXCollections.observableArrayList();
    	statsListing = FXCollections.observableArrayList();
    	currentProblem = null;
    	generalJSONObject = null;
    	controller = pc;
   	}

	public void clear() {
		currentProblem.clear();
	}
	
	public static void indentCode(ObservableList<CommandTerm> list) {
    	if (list.size() == 0) {return;}
    	CommandTerm prev = list.get(0);
    	int indentLevel = 0;
		prev.setIndentLevel(indentLevel);
    	for ( int i = 1; i < list.size(); i++){
    		CommandTerm line = list.get(i);
    		if (prev.getPrimaryChildTerm()!=null)   {indentLevel++;}
    		if (line.getParentTerm()!=line) {
    			indentLevel--;
    		}
    		line.setIndentLevel(indentLevel);
    		prev = line;
    	};
	}
	
	public void copyCode(ListView<CommandTerm> codeListing) {
		currentProblem.copyCode(codeListing);
	}
	
	private Problem findProblem(String name) {
		for(Problem problem : problemListing) {
			if (problem.getName().equals(name)) {
				return problem;
			}
		}
		JSONObject json = getCurrentProblemJSONObjects().get(name);
		Problem newProblem = new Problem(controller, this, name, json);
 	   	problemListing.add(newProblem);
		return newProblem;
	}
	

	@SuppressWarnings("unchecked")
	private Map<?, JSONObject> getCurrentProblemJSONObjects() {
    	JSONParser parser = new JSONParser();
    	Map<String, JSONObject> problems = new HashMap<String, JSONObject>();
    	if (generalJSONObject == null) {
	        try {
	           Object obj = parser.parse(new FileReader("resources/currentProblem.json"));
	           generalJSONObject = (JSONObject)obj;
	        } catch( IOException | ParseException e) {
	        	problemListing.add(new Problem(controller, this, "Problem1", problems.get("Problem1")));
	        	// File is missing so create it.
	        	generalJSONObject = new JSONObject();
	        	generalJSONObject.put("CurrentProblem", "Problem1");
	        	try (FileWriter file = new FileWriter("resources/currentProblem.json")) {
	                file.write(generalJSONObject.toJSONString());
	        	} catch(Exception ew) {
	                ew.printStackTrace();
	        	}
	//        	e.printStackTrace();
	        	System.out.println("New currentProblem JSON file created");
	        }
    	}
	    problems = ((Map<String, JSONObject>)generalJSONObject.get("Results"));
		return problems;
	}
	
	public ObservableList<Problem> loadAllProblemsFromJSONFile(){
		problemListing.clear();
		Map<?, JSONObject> problems = getCurrentProblemJSONObjects();
		if (problems != null) {
	        String currentProblemName = (String)generalJSONObject.get("CurrentProblem");
			for(Object name: problems.keySet()) {
				Problem newProblem = new Problem(controller, this, (String)name, problems.get(name));
				problemListing.add(newProblem);
				statsListing.add(newProblem.getStats());
	
				if (currentProblemName.equals((String)name)) {
					currentProblem = newProblem;
		    		controller.lstPreviousRun.setItems(newProblem.getPreviousListing());
	    			controller.lstPreviousSuccessfulRun.setItems(newProblem.getSuccessfulListing());
	    		}
			}
		} // end if there are some problems

        ProblemComparator pc = new ProblemComparator();
        FXCollections.sort(problemListing, pc);
        controller.cbProblemList.setItems(problemListing);
        controller.cbProblemList.getSelectionModel().select(getCurrentProblemIndex());
		return problemListing;
	}
	


	public boolean isCurrentProblem(Problem p) {
		return p.equals(currentProblem);
	}
	public int getCurrentProblemIndex() {
		if (currentProblem == null) {
			currentProblem = problemListing.get(0);
			return 0;
		}
		return currentProblem.getID()-1;
	}
	public void setCurrentProblem(Problem newProblem) {
		currentProblem = newProblem;
		loadCurrentProblem();
	}
	public void loadCurrentProblem() {
		currentProblem.loadProblem();
	}
	
	public boolean gradeSolution() {
		boolean testPassed = currentProblem.gradeSolution();
    	storeResultInJSON(testPassed);
    	
		Alert a = new Alert(AlertType.NONE);
		String message = "";
    	if (!testPassed) {
    		a.setAlertType(AlertType.ERROR);
    		message = "You didn't find the right solution. Please try again.";
    	} else {
    		a.setAlertType(AlertType.INFORMATION);
    		message = "Congratulations you solved the puzzle.";
    	}
		controller.setErrorMsg(message);
		controller.display();
		a.setHeaderText("You code has finished running");
		a.setContentText(message);
		a.initModality(Modality.APPLICATION_MODAL); 
		a.showAndWait();

		resetPuzzle(testPassed);
		return testPassed;
	}

    @SuppressWarnings("unchecked")
	private void storeResultInJSON(boolean passed) {	
    	Map<String, Object> results = (Map<String, Object>)generalJSONObject.get("Results");
    	if (results == null) {
    		results = new JSONObject();
    		generalJSONObject.put("Results", results);
    	}
    	Map<String, Object> problemStats = (Map<String,Object>)results.get(currentProblem.getName());
    	if (problemStats == null) {
    		problemStats = new JSONObject();
    		results.put(currentProblem.getName(), problemStats);
    	}
    	long attempts = 1;
    	if (problemStats.containsKey("Attempts")) {
    		attempts = ((Long)problemStats.get("Attempts")).longValue();
    		attempts++;
    	}
    	problemStats.put("Attempts", attempts);
    	JSONArray lastListing = new JSONArray();
    	for (CommandTerm ct : getFullListing()) { // TODO move to Problem ??
    		lastListing.add(ct.toJSON());
    	}
    	problemStats.put("LastRunListing",lastListing);
    	currentProblem.copyCode(getFullListing(), getPreviousListing());
    	if (controller.exec.inError()) {
        	long errorCount = 1;
        	if (problemStats.containsKey("ErrorCount")) {
        		errorCount = ((Long)problemStats.get("ErrorCount")).longValue();
        		errorCount++;
        	}
        	problemStats.put("ErrorCount", errorCount);
    		problemStats.put("LastRun", "ERROR");
    	} else if (!passed) {
        	long failCount = 1;
        	if (problemStats.containsKey("FailCount")) {
        		failCount = ((Long)problemStats.get("FailCount")).longValue();
        		failCount++;
        	}
        	problemStats.put("FailCount", failCount);
    		problemStats.put("LastRun", "FAILED");
    	} else {
    		currentProblem.clear();
    		if (currentProblem.getNextProblemName() != null) {
    			generalJSONObject.put("CurrentProblem",currentProblem.getNextProblemName());
            	if (!results.containsKey(currentProblem.getNextProblemName())) {
            		results.put(currentProblem.getNextProblemName(), new JSONObject());
            	}
    		}
    		problemStats.put("LastRun", "SUCCESS");
        	problemStats.put("LastSuccessfulListing",lastListing);
        	currentProblem.copyCode(getFullListing(), currentProblem.getSuccessfulListing());
    	}
    	//Write JSON file
        try (FileWriter file = new FileWriter("resources/currentProblem.json")) {
            file.write(generalJSONObject.toJSONString()); 
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentProblem.updateHistory(problemStats);
    }
    
	public void reset() {
    	controller.exec.stopExec();
    	controller.exec = null;
		for (CommandTerm ct : getFullListing()) {
			ct.reset();
		}
		controller.initialiseControls();
		controller.clearListingSelection();
	}
	

    private void resetPuzzle(boolean passed) {
    	controller.exec.stopExec();
    	controller.exec = null;
		Problem oldProblem = currentProblem;
		if (passed) {
			String newProblemName = currentProblem.getNextProblemName();
			currentProblem = findProblem(newProblemName);
		} else {
			for (CommandTerm ct : getFullListing()) {
				ct.reset();
			}
		}
		if (currentProblem == null) {
			currentProblem = oldProblem;
			String message = "You have completed all the problems";
			Alert a = new Alert(AlertType.INFORMATION);
			a.setHeaderText("Congratulations");
			a.setContentText(message);
			a.initModality(Modality.APPLICATION_MODAL); 
			a.showAndWait();
		}
		currentProblem.loadProblem();
    }
    
    public ObservableList<Problem> getProblemListing(){ return problemListing;}
    public ObservableList<CommandTerm> getFullListing(){ return currentProblem.getFullListing();}
    public ObservableList<CommandTerm> getPreviousListing(){ return currentProblem.getPreviousListing();}
    public ObservableList<CommandTerm> getSuccessfulListing(){ return currentProblem.getSuccessfulListing();}
    public Problem getCurrentProblem() {return currentProblem;}
    public String getCurrentProblemName() {
    	return currentProblem.toString();
    }
}
