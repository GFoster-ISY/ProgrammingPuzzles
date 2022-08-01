package application.problem;

import org.json.simple.JSONObject;

import application.PuzzleController;

public class ProblemHistory {

    String lastRun = "A new problem awaits.";
	int attempts;
	int failCount;
	int errorCount;
    
	public ProblemHistory(PuzzleController pc, Problem problem, JSONObject json) {
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
	public String getSuccessRate() {
		if (attempts == 0) return "";
		return "" + 100*(attempts-failCount-errorCount)/attempts + "%";
	}

}
