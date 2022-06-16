package application.problem;

import java.util.Map;

import org.json.simple.JSONObject;

import application.PuzzleController;

public class ProblemStats {

    private PuzzleController controller;
    private Problem parent;
    String lastRun;
	int attempts;
	int failCount;
	int errorCount;
    
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
	}

	public String getLastRun() {return lastRun;}
	public int getAttempts() {return attempts;}
	public int getFailCount() {return failCount;}
	public int getErrorCount() {return errorCount;}
	public String getSuccessRate() {return "" + 100*(attempts-failCount-errorCount)/attempts + "%";}
}
