package application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.*;
import org.json.simple.parser.*;

import application.exec.Execute;
import application.keyword.CommandTerm;
import application.keyword.KeyTermController;
import application.keyword.UnknownKeywordException;
import application.keyword.Variable;

public class PuzzleController {

    @FXML private HBox hboxRoot;
    @FXML private VBox vboxRoot;
    @FXML private TextArea txtProblem;
    @FXML private Canvas cvsCups;
    @FXML private Canvas cvsHand;
    @FXML private Canvas cvsContainer;
    @FXML private TextField txtErrorMsg;
    @FXML private ListView<CommandTerm> lstListing;
    @FXML private HBox hboxRunningButtons;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private Button btnFinish;
    @FXML private HBox hboxStartExecuteButtons;
    @FXML private Button btnStep;
    @FXML private Button btnAuto;
    @FXML private Button btnFinal;
    @FXML private ListView<String> lstLexicon;
    

    private String problem;
    private Container container;
    private Cup[] cups;
    private Hand hand;
    private int cupCount;
    private int ballCount;
    private ObservableList<CommandTerm> fullListing;
    private ObservableList<String> allKeyTerms;
    private Map<String, Variable> variableList;
    private JSONObject generalJSONObject;
    private JSONObject problemJSONObject;
    private Map solution;
    private Execute exec;
    Thread execThread;
    private boolean execThreadRunning = false;
    
    public Container getContainer() {return container;}
    public Cup[] getCups() { return cups;}
    public Hand getHand() {return hand;}
    
    @FXML void initialize() {
    	fullListing = FXCollections.observableArrayList();
    	variableList = new HashMap<>();
    	lstListing.setItems(fullListing);
    	lstListing.setCellFactory(param -> new DragNDropCommandTermCell(this));
    	
    	problem = getCurrentProblem();
    	loadProblem();
    	
        exec = null;

        hboxRunningButtons.setManaged(false);
		display();
    	
    }
    
    @FXML private void selectKeyTerm(MouseEvent event) {
        event.consume();
        String keyTerm = lstLexicon.getSelectionModel().getSelectedItem();
        if (keyTerm == null) return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("keyword/KeyTerm.fxml"));
        Stage stage = getStage(loader);
        if (stage == null) return;

        // Get the dialog controller so that a public method can be run to send data to the dialog
        KeyTermController ktc = loader.<KeyTermController>getController();

        try {
        	ktc.setKeyTerm(keyTerm, this);
        } catch (UnknownKeywordException ex) {
        	System.err.println("UnknownKeywordException: " + ex.getMessage());
        	ex.printStackTrace();
        	return;
        }
        // Only display the dialog box if we have some arguments to fill.
        if (ktc.getArgCount()>0) {
            // Show the dialog (and wait for the user to close it)
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
        
        if (ktc.okayPressed() || ktc.getArgCount()==0) {
        	ktc.getInstruction().updateArgs();
        	addInstruction(ktc.getInstruction());
        }
        showRunTimeButtons(fullListing.size() > 0);
        lstLexicon.getSelectionModel().clearSelection();
    }
    
    @FXML private void selectInstruction(MouseEvent event) {
    	event.consume();
        CommandTerm instruction = lstListing.getSelectionModel().getSelectedItem();
        if (instruction == null) {return;}
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("keyword/KeyTerm.fxml"));
        Stage stage = getStage(loader);
        if (stage == null) return;

        // Get the dialog controller so that a public method can be run to send data to the dialog
        KeyTermController ktc = loader.<KeyTermController>getController();

        ktc.setKeyTerm(instruction);
        ktc.showDeleteButton();
        
        // Show the dialog (and wait for the user to close it)
        stage.initModality(Modality.APPLICATION_MODAL); 
        stage.showAndWait();
        if (ktc.okayPressed()) { 
        	instruction.updateArgs();
        } else if (ktc.deleteInstruction()) {
        	removeInstruction(instruction);
        }

    	lstListing.refresh();
        showRunTimeButtons(fullListing.size() > 0);
        lstListing.getSelectionModel().clearSelection();
    }
    
    private void addInstruction(CommandTerm instruction) {
    	fullListing.add(instruction);
    	if (instruction.hasClosure()) {
    		addInstruction(instruction.getClosure());
    	}
    	indentCode();
    }
    
    private void removeInstruction(CommandTerm instruction) {
    	// Move to the start of any closure group and then delete each term
    	if(instruction.getClosesIndent() && fullListing.contains(instruction.getParentTerm())) {
    		removeInstruction(instruction.getParentTerm());
    	}
    	if (fullListing.contains(instruction)) {
    		fullListing.remove(instruction);
        	if (instruction.hasClosure()) {
        		removeInstruction(instruction.getChildTerm());
        	}
    	}
    	indentCode();
    }
    
    public void indentCode() {
    	CommandTerm prev = fullListing.get(0);
    	int indentLevel = 0;
		prev.setIndentLevel(indentLevel);
    	for ( int i = 1; i < fullListing.size(); i++){
    		CommandTerm line = fullListing.get(i);
    		if (prev.hasClosure())   {indentLevel++;}
    		if (line.getClosesIndent()) {indentLevel--;}
    		line.setIndentLevel(indentLevel);
    		prev = line;
    	};
    	lstListing.refresh();
    }
    
    public void addVariable(Variable var) {
    	if (!variableList.containsKey(var.getName())){
    		variableList.put(var.getName(), var);
    		addInstruction(var);
    	}
    }
    
    public Variable getVariable(String name) {
    	if (variableList.containsKey(name)){
    		return variableList.get(name);
    	}
    	return null;
    }
    
    private void runOneLineOfCode() {
    	if (exec == null) {
    		ArrayList<CommandTerm> code = new ArrayList<>();
    		fullListing.forEach(line -> code.add(line));
    		exec = new Execute(code);
    	}
    	exec.step();    	
    }
    
    private void codeCompleted() {
    	execThreadRunning = false;
    	boolean passed = false;
		if (exec.inError()) {
			displayError();
		} else {
			passed = gradeSolution();
		}
		storeResultInJSON(passed);
		resetPuzzle(passed);
    }
    
    @FXML private void execByStep() {
    	runOneLineOfCode();
    	manageButtons(!exec.finished());
		display();    		
		
    	if (exec.finished()) {
    		codeCompleted();
    	}
    }
    @FXML private void execByAuto() {
    	if (execThreadRunning) {
    		exec();
    		return;
    	}
    	execThread = new Thread(() -> {
    		execThreadRunning = true;
	    	while (exec == null || !exec.finished()) {
	        	runOneLineOfCode();
	        	manageButtons(!exec.finished());
	        	Platform.runLater(() -> display());
	    		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					return;
					//e.printStackTrace();
				}
	    		
	    	}
	    	Platform.runLater(() -> codeCompleted());
    	});
    	execThread.start();
    }
    
    @FXML private void execUndo() {
    	// TODO Not yet implemented
    }
    
    @FXML private void exec() {
    	if (execThreadRunning) {
    		execThread.interrupt();
    	}
    	while (exec == null || !exec.finished()) {
    		runOneLineOfCode();
    	}
		codeCompleted();
    }
    
    protected void resizeWidth(Number newWidth) {
    	hboxRoot.setPrefWidth(newWidth.doubleValue());
    	double column1Width = Math.max(newWidth.doubleValue() / 3,200);
    	txtProblem.setPrefWidth(column1Width);
    	cvsCups.setWidth(column1Width);
    	cvsHand.setWidth(column1Width);
    	cvsContainer.setWidth(column1Width);
    	display();
    }
    protected void resizeHeight(Number newHeight) {
    	hboxRoot.setPrefHeight(newHeight.doubleValue());
    	vboxRoot.setPrefHeight(newHeight.doubleValue());
    	double canvasHeight = (newHeight.doubleValue() - 105)/3;
    	cvsCups.setHeight(canvasHeight);
    	cvsHand.setHeight(canvasHeight);
    	cvsContainer.setHeight(canvasHeight);
    	display();
    }

    private String getCurrentProblem() {
    	String name;
       	JSONParser parser = new JSONParser();
        try {
           Object obj = parser.parse(new FileReader("resources/currentProblem.json"));
           generalJSONObject = (JSONObject)obj;
           name = (String)generalJSONObject.get("CurrentProblem");
        } catch(Exception e) {
        	name = "Problem1";
        	JSONObject obj = new JSONObject();
            obj.put("CurrentProblem", name);
        	try (FileWriter file = new FileWriter("resources/currentProblem.json")) {
                file.write(obj.toJSONString());
        	} catch(Exception ew) {
                ew.printStackTrace();
        	}
        }
        return name;
    }
    
    private void loadProblem() {
    	String task; 
    	JSONParser parser = new JSONParser();
        try {
           Object obj = parser.parse(new FileReader("resources/"+problem+".json"));
           problemJSONObject = (JSONObject)obj;
           task = (String)problemJSONObject.get("ObjectiveStatement");
           txtProblem.setText(task);
           cupCount = ((Long)problemJSONObject.get("PotCount")).intValue();
           if (problemJSONObject.containsKey("BallCount")) {
        	   ballCount = ((Long)problemJSONObject.get("BallCount")).intValue();
        	   container = new Tray(ballCount);
           } else if (problemJSONObject.containsKey("BallColour")) {
        	   Map<String, Long> ballColourMap = (Map)problemJSONObject.get("BallColour");
        	   container = new Tray(ballColourMap);
           }
           JSONArray keyTerms = (JSONArray)problemJSONObject.get("KeyTerms");
           allKeyTerms = FXCollections.observableArrayList(); 
           allKeyTerms.addAll(keyTerms);
           lstLexicon.setItems(allKeyTerms);
           
           
           hand = new Hand();
           cups = new Cup[cupCount];
           Cup.gradingCups(false);
           for (int i = 0; i < cupCount; i++) {
               cups[i] = new Cup(i);
           }
           solution = ((Map)problemJSONObject.get("Solution"));
        } catch(Exception e) {
                e.printStackTrace();
        }    	
    }
    
    private void display() {
    	Cup.setCupSize(cupCount, (int)cvsCups.getWidth(), (int)cvsCups.getHeight());
    	GraphicsContext gc = cvsCups.getGraphicsContext2D();
		gc.clearRect(0, 0, cvsCups.getWidth(), cvsCups.getHeight());
		for (int i = 0; i < cupCount; i++) {
			cups[i].display(gc,cvsCups.getWidth(), cvsCups.getHeight());
		}
    	container.display(cvsContainer.getGraphicsContext2D(),cvsContainer.getWidth(), cvsContainer.getHeight());
    	hand.display(cvsHand.getGraphicsContext2D(),cvsHand.getWidth(), cvsHand.getHeight());
    	if (exec != null && exec.getErrorMsg()!=null) {
    		txtErrorMsg.setText(exec.getErrorMsg());
    	} else {
    		if (exec != null && !exec.finished()) {
    			txtErrorMsg.setText("");
    		}
    	}
    	lstListing.refresh();
    }
    
    private void manageButtons(boolean running) {
    	showRunTimeButtons(running);

    	hboxStartExecuteButtons.setVisible(!running);
    	hboxStartExecuteButtons.setManaged(!running);
    	hboxRunningButtons.setVisible(running);
    	hboxRunningButtons.setManaged(running);
    }
    
    private void showRunTimeButtons(boolean show) {
    	btnStep.setDisable(!show);
    	btnAuto.setDisable(!show);
    	btnFinal.setDisable(!show);
    	btnNext.setDisable(!show);
    	btnPrev.setDisable(!show);
    	btnFinish.setDisable(!show);
    }
    
    private Stage getStage(FXMLLoader loader){
        // Loads the scene from the fxml file
        Scene newScene;
        try {
            newScene = new Scene(loader.load());
        } catch (IOException e) {
            System.out.println("Fail");
            System.out.println(e);
            e.printStackTrace();
            return null;
        }
        Stage inputStage = new Stage();
        inputStage.initOwner(Main.primaryStage);
        inputStage.setScene(newScene);
        
        return inputStage;
    }
    
    private void displayError() {
    	Alert a = new Alert(AlertType.NONE);
		String message = exec.getErrorMsg();
		txtErrorMsg.setText(message);
		a.setAlertType(AlertType.ERROR);
		a.setHeaderText("Your code needs fixing");
		a.setContentText(message);
		a.initModality(Modality.APPLICATION_MODAL); 
		a.showAndWait();	
    }
    
    private boolean gradeSolution() {
    	Cup.gradingCups(true);
    	boolean testPassed = true;
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
    			 Map<String, Long> ballColourMap = (Map)objs.get(i);
    			 if (!cups[i].correctBallCount(ballColourMap)) {
    				 testPassed = false;
    			 }
    		}
    	}
		Alert a = new Alert(AlertType.NONE);
		String message = "";
    	if (!testPassed) {
    		a.setAlertType(AlertType.ERROR);
    		message = "You didn't find the right solution. Please try again.";
    	} else {
    		a.setAlertType(AlertType.INFORMATION);
    		message = "Congratulations you solved the puzzle.";
    	}
		txtErrorMsg.setText(message);
		display();
		a.setHeaderText("You code has finished running");
		a.setContentText(message);
		a.initModality(Modality.APPLICATION_MODAL); 
		a.showAndWait();
		return testPassed;
    }
    
    private void resetPuzzle(boolean passed) {
    	exec.stopExec();
		exec = null;
		String oldProblem = problem;
		if (passed) {
			problem = (String)problemJSONObject.get("NextProblem");
			fullListing.clear();
		} else {
			fullListing.forEach(term -> {
				term.reset();
			});
		}
		if (problem == null) {
			problem = oldProblem;
			String message = "You have completed all the problems";
			Alert a = new Alert(AlertType.INFORMATION);
			a.setHeaderText("Congratulations");
			a.setContentText(message);
			a.initModality(Modality.APPLICATION_MODAL); 
			a.showAndWait();
		}
		loadProblem();
		
		if (passed) {
			display();
		}
        lstListing.getSelectionModel().clearSelection();
		lstListing.refresh();
        manageButtons(false);
        showRunTimeButtons(fullListing.size() > 0);
    }
    
    private void storeResultInJSON(boolean passed) {	
    	Map results = (Map)generalJSONObject.get("Results");
    	if (results == null) {
    		results = new JSONObject();
    		generalJSONObject.put("Results", results);
    	}
    	Map problemStats = (Map)results.get(problem);
    	if (problemStats == null) {
    		problemStats = new JSONObject();
    		results.put(problem, problemStats);
    	}
    	long attempts = 1;
    	if (problemStats.containsKey("Attempts")) {
    		attempts = ((Long)problemStats.get("Attempts")).longValue();
    		attempts++;
    	}
    	problemStats.put("Attempts", attempts);
    	if (exec.inError()) {
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
    		if (problemJSONObject.containsKey("NextProblem")) {
    			generalJSONObject.put("CurrentProblem",(String)problemJSONObject.get("NextProblem"));
    		}
    		problemStats.put("LastRun", "SUCCESS");
    	}
    	//Write JSON file
        try (FileWriter file = new FileWriter("resources/currentProblem.json")) {
            file.write(generalJSONObject.toJSONString()); 
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
