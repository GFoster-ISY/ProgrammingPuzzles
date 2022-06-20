package application.problem;

import java.io.FileReader;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import application.Bag;
import application.Container;
import application.Cup;
import application.PuzzleController;
import application.Tray;

public class Problem {

	private int id;
	private String nextProblemName;
	private ProblemStats ps;
    private PuzzleController controller;
    private ProblemManager pm;
    private Map<?, ?> solution;
	
	public Problem(PuzzleController pc, ProblemManager manager, String name) {
    	controller = pc;
    	pm = manager;
		int number = Integer.parseInt(name.substring(7));
		id = number;
		nextProblemName = null;
	}
 
	public void setStats (ProblemStats stats) {ps = stats;}
	public ProblemStats getStats() {return ps;}
    public void loadProblem() {
    	String task; 
    	JSONParser parser = new JSONParser();
        try {
           Object obj = parser.parse(new FileReader(fileName()));
           JSONObject problemJSONObject = (JSONObject)obj;
           task = (String)problemJSONObject.get("ObjectiveStatement");
           controller.setProblemText(task);
           controller.setCupCount(((Long)problemJSONObject.get("PotCount")).intValue());
           String containerType = "Tray";
           if (problemJSONObject.containsKey("Container")) {
        	   containerType = (String)problemJSONObject.get("Container");
           }
    	   Container container = null;
           if (problemJSONObject.containsKey("BallCount")) {
        	   int ballCount = ((Long)problemJSONObject.get("BallCount")).intValue();
        	   if (containerType.equals("Bag")) {
        		   container = new Bag(ballCount);
        	   } else {
        		   container = new Tray(ballCount);
        	   }
           } else if (problemJSONObject.containsKey("BallColour")) {
        	   @SuppressWarnings("unchecked")
			Map<String, Long> ballColourMap = (Map<String, Long>)problemJSONObject.get("BallColour");
        	   if (containerType.equals("Bag")) {
            	   container = new Bag(ballColourMap);
        	   } else {
            	   container = new Tray(ballColourMap);
        	   }
           }
           if (problemJSONObject.containsKey("NextProblem")) {
        	   nextProblemName = (String)problemJSONObject.get("NextProblem");
    	   }
           
           controller.setContainer(container);
           JSONArray keyTerms = (JSONArray)problemJSONObject.get("KeyTerms");
           controller.setAllKeyTerms(keyTerms);
           controller.initilaliseControls();
           
           solution = ((Map<?, ?>)problemJSONObject.get("Solution"));
        } catch(Exception e) {
                e.printStackTrace();
        }    	
    }
    
    public boolean gradeSolution() {
    	Cup.gradingCups(true);
    	boolean testPassed = true;
    	Cup[] cups = controller.getCups();
    	int cupCount = cups.length;
    	if (solution.containsKey("PotCount")) {
    		JSONArray objs = (JSONArray) solution.get("PotCount");
    		for (int i = 0; i<cupCount; i++) {
    			int requiredBallsInCup = ((Long)objs.get(i)).intValue();
    			if (!cups[i].correctBallCount(requiredBallsInCup)) {
    				testPassed = false;
    			}
    		}
    	} else if (solution.containsKey("PotColour")){
    		JSONArray objs = (JSONArray) solution.get("PotColour");
    		for(int i = 0; i<cupCount; i++) {
    			 @SuppressWarnings("unchecked")
				Map<String, Long> ballColourMap = (Map<String, Long>)objs.get(i);
    			 if (!cups[i].correctBallCount(ballColourMap)) {
    				 testPassed = false;
    			 }
    		}
    	}
		return testPassed;
    }
    
    
    public int getID() {return id;}

	public String toString() {
		return "Problem " + id;
	}
	
	public String getName() {
		return "Problem" + id;
	}
	private String fileName() {
		return "resources/"+getName()+".json";
	}
	
	public String getNextProblemName() {
		return nextProblemName;
	}
	
	public boolean isCurrentProblem() {
		return pm.isCurrentProblem(this);
	}
}
