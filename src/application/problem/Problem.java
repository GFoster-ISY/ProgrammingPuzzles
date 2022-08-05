package application.problem;

import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private PuzzleController controller;
    private ProblemManager pm;
    // If these are not public then the ListView in PuzzleController doesn't display the values.
    public ObservableList<CommandTerm> fullListing;
	public ObservableList<CommandTerm> previousRunListing;
	public ObservableList<CommandTerm> previousSuccessfulRunListing;
	private ProblemHistory stats;
    private Map<?, ?> solution;
	
	public Problem(PuzzleController pc, ProblemManager manager, String name, JSONObject json) {
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
		nextProblemName = null;
		loadProblem();
		loadHistory(json);
	}
 
	public void clear() {
		fullListing.clear();
	}
	
	public void copyCode(ObservableList<CommandTerm> from, ObservableList<CommandTerm> to) {
		to.clear();
		for (CommandTerm command : from) {
			to.add(command);
    	}
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
	public ProblemHistory getStats() {return stats;}
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
           controller.initialiseControls();
           
           solution = ((Map<?, ?>)problemJSONObject.get("Solution"));
           
        } catch(Exception e) {
                e.printStackTrace();
        }    	
    }
    
    public void updateHistory(Map<String, Object> problemStats) {
    	if (problemStats.containsKey("Attempts")) {
    		stats.attempts = ((Long)problemStats.get("Attempts")).intValue();
    	} else {
    		stats.attempts = 0;
    	}
    	if (problemStats.containsKey("FailCount")) {
    		stats.failCount = ((Long)problemStats.get("FailCount")).intValue();
    	} else {
    		stats.failCount = 0;
    	}
    	if (problemStats.containsKey("ErrorCount")) {
    		stats.errorCount = ((Long)problemStats.get("ErrorCount")).intValue();
    	} else {
    		stats.errorCount = 0;
    	}
    	if (problemStats.containsKey("LastRun")) {
    		stats.lastRun = (String)problemStats.get("LastRun");
    	} else {
    		stats.lastRun = "A new problem awaits.";
    	}
    	controller.displayStats(stats);
    }
    
    private void loadHistory(JSONObject json) {
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
    	
        stats = new ProblemHistory(controller, this, json);
    }
    
	private ObservableList<CommandTerm> readListingFromJSON (JSONObject json, String type) {
		LinkedHashMap <Integer, CommandTerm> commandTermById = new LinkedHashMap <>();
		LinkedHashMap <String, ArrayDeque<CommandTerm>> openCommandTerm;
		openCommandTerm = initOpenCommandTerm();
		ObservableList<CommandTerm> listing = FXCollections.observableArrayList();
		JSONArray commands = (JSONArray)json.get(type);
		if (commands != null) {
			for (Object line : commands) {
				CommandTerm ct = CommandTerm.fromJSON(controller, (JSONObject)line, openCommandTerm, listing);
				commandTermById.put(ct.getId(), ct);
				listing.add(ct);
		 	}
//			listing = FXCollections.observableArrayList(commandTermById.values());
		 	modifyAllParentCommandTerms(commandTermById, listing);
			modifyAllChildCommandTerms(commandTermById, listing, openCommandTerm);
		 	ProblemManager.indentCode(listing);
		}
		return listing;
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
