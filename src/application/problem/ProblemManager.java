package application.problem;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import application.PuzzleController;
import application.keyword.CommandTerm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;

public class ProblemManager {

	private ObservableList<Problem> problemListing;
	private ObservableList<ProblemStats> statsListing;
	private ObservableList<CommandTerm> previousRun;
	private ObservableList<CommandTerm> previousSuccessfulRun;
	private Problem currentProblem;
    private JSONObject generalJSONObject;
    private PuzzleController controller;
	
	public ProblemManager(PuzzleController pc) {
    	problemListing = FXCollections.observableArrayList();
    	statsListing = FXCollections.observableArrayList();
    	controller = pc;
   	}

	private Problem findProblem(String name) {
		for(Problem problem : problemListing) {
			if (problem.getName().equals(name)) {
				return problem;
			}
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public ObservableList<Problem> loadAllProblemsFromJSONFile(){
    	JSONParser parser = new JSONParser();
    	Stack<CommandTerm> openCommandTerm = new Stack<>();
        try {
           Object obj = parser.parse(new FileReader("resources/currentProblem.json"));
           generalJSONObject = (JSONObject)obj;
           
           Map<?, JSONObject> problems = ((Map<?, JSONObject>)generalJSONObject.get("Results"));
           String currentProblemName = (String)generalJSONObject.get("CurrentProblem");
           for(Object name: problems.keySet()) {
        	   Problem newProblem = new Problem(controller, this, (String)name);
        	   problemListing.add(newProblem);
        	   ProblemStats stats = new ProblemStats(controller, newProblem, problems.get(name));
        	   statsListing.add(stats);
        	   newProblem.setStats(stats);

        	   if (currentProblemName.equals((String)name)) {
        		   currentProblem = newProblem;

        		   previousRun = FXCollections.observableArrayList();
            	   JSONArray commands = (JSONArray)problems.get(name).get("LastRunListing");
            	   if (commands != null) {
    	        	   for (Object line : commands) {
    	        		   previousRun.add(CommandTerm.fromJSON(controller, (JSONObject)line, openCommandTerm));
    	        	   }
    	        	   controller.indentCode(controller.lstPreviousRun, previousRun);
            	   }
            	   controller.lstPreviousRun.setItems(previousRun);
            	   previousSuccessfulRun = FXCollections.observableArrayList();
            	   commands = (JSONArray)problems.get(name).get("LastSuccessfulRunListing");
            	   if (commands != null) {
    	        	   for (Object line : commands) {
    	        		   previousSuccessfulRun.add(CommandTerm.fromJSON(controller, (JSONObject)line, openCommandTerm));
    	        	   }
    	        	   controller.indentCode(controller.lstPreviousRun, previousSuccessfulRun);
            	   }
            	   controller.lstPreviousSuccessfulRun.setItems(previousSuccessfulRun);
        	   }
           }
        } catch(IOException | ParseException e) {
        	problemListing.add(new Problem(controller, this, "Problem1"));
        	// File is missing so create it.
        	JSONObject obj = new JSONObject();
            obj.put("CurrentProblem", "Problem1");
        	try (FileWriter file = new FileWriter("resources/currentProblem.json")) {
                file.write(obj.toJSONString());
        	} catch(Exception ew) {
                ew.printStackTrace();
        	}
        	e.printStackTrace();
        }

        ProblemComparator pc = new ProblemComparator();
        FXCollections.sort(problemListing, pc);
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
	
	public void gradeSolution() {
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
    	for (CommandTerm ct : controller.getListing()) {
    		lastListing.add(ct.toJSON());
    	}
    	problemStats.put("LastRunListing",lastListing);
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
    		if (currentProblem.getNextProblemName() != null) {
    			generalJSONObject.put("CurrentProblem",currentProblem.getNextProblemName());
    		}
    		problemStats.put("LastRun", "SUCCESS");
        	problemStats.put("LastSuccessfulListing",lastListing);
    	}
    	//Write JSON file
        try (FileWriter file = new FileWriter("resources/currentProblem.json")) {
            file.write(generalJSONObject.toJSONString()); 
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void resetPuzzle(boolean passed) {
    	controller.exec.stopExec();
    	controller.exec = null;
		Problem oldProblem = currentProblem;
		if (passed) {
			String newProblemName = currentProblem.getNextProblemName();
			currentProblem = findProblem(newProblemName);
			controller.clear();
		} else {
			controller.reset();
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
		
		if (passed) {
			controller.display();
		}
		controller.clearListingSelection();
		controller.manageButtons(false);
		controller.showRunTimeButtons(controller.hasCodeListing());
    }
    
    public ObservableList<Problem> getProblemListing(){ return problemListing;}
    public String getCurrentProblemName() {
    	return currentProblem.toString();
    }
}
