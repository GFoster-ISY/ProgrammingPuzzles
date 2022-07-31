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
import application.keyword.CommandTerm;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class Problem {

	private int id;
	private String nextProblemName;
	private ProblemHistory ps;
    private PuzzleController controller;
    private ProblemManager pm;
    public ObservableList<CommandTerm> fullListing; // TODO change visibility to private
    private Map<?, ?> solution;
	
	public Problem(PuzzleController pc, ProblemManager manager, String name) {
    	controller = pc;
    	pm = manager;
    	fullListing = FXCollections.observableArrayList();
    	fullListing.addListener(new ListChangeListener<CommandTerm>() {
    	     public void onChanged(Change<? extends CommandTerm> c) {
    	    	 ProblemManager.indentCode(fullListing);
    	     }
		});
    	controller.lstListing.setItems(fullListing);
		int number = Integer.parseInt(name.substring(7));
		id = number;
		nextProblemName = name;
	}
 
	public void clear() {
		fullListing.clear();
	}
	public void copyCode(ListView<CommandTerm> codeListing) {
    	clear();
    	for (CommandTerm command : codeListing.getItems()) {
    		if (controller.allKeyTerms.contains(command.getRootTerm().getKeyword())) {
    			fullListing.add(command);
    		}
    	}
    	
	}
	
	public void remove(CommandTerm existingTerm) {
		CommandTerm rootTerm = existingTerm.getRootTerm();
		for (CommandTerm ct :rootTerm.getChildTerms()) {
			fullListing.remove(ct);
		}
		fullListing.remove(rootTerm);
	}
	public void setStats (ProblemHistory stats) {ps = stats;}
	public ProblemHistory getStats() {return ps;}
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
           controller.lstListing.setItems(fullListing);
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
