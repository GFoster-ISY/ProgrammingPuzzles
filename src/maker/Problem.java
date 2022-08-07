package maker;

import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Problem {

	int id;
	String objectiveStatement;
	int potCount;
	String container;
	String ballDetails;
	int ballCount;
	ArrayList<BallColour> ballColours;
	JSONArray keyTerms;
	String solutionType;
	JSONArray solutionPotCount;
	
	public Problem(String name, JSONObject json) {
		String problemName = name.substring(0,name.length()-5);
		id = Integer.parseInt(problemName.substring(7));
		objectiveStatement = (String)json.get("ObjectiveStatement");
		potCount = ((Long)json.get("PotCount")).intValue();
		container = (String)json.get("Container");
		
		if (json.containsKey("BallCount")) {
			ballDetails = "BallCount";
			ballCount = ((Long)json.get("BallCount")).intValue();
		} else if (json.containsKey("BallColour")) {
			ballDetails = "BallColour";
			Map<?,?> ballColourMap = (Map<?,?>)json.get("BallColour");
			ballColours = new ArrayList<>();
			for (Map.Entry<?,?> entry : ballColourMap.entrySet()) {
				BallColour bc = new BallColour((String)entry.getKey(), ((Long)entry.getValue()).intValue());
				ballColours.add(bc);
			}
		}
		keyTerms = (JSONArray)json.get("KeyTerms");
		Map<?, ?> solution = ((Map<?, ?>)json.get("Solution"));
		Map.Entry<?, ?> entry = solution.entrySet().iterator().next();
		solutionType = (String)entry.getKey();
		solutionPotCount = (JSONArray)entry.getValue();
	}

	public String toString() {
		return "Problem " + id;
	}
	public int getId() { return id;}
}
