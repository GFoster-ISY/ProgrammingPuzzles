package application;

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
import java.util.Map;

import org.json.simple.*;
import org.json.simple.parser.*;

import application.exec.Execute;
import application.keyword.CommandTerm;
import application.keyword.KeyTermController;

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
    private JSONObject jsonObject;
    private Map solution;
    private Execute exec;
    
    public Container getContainer() {return container;}
    public Cup[] getCups() { return cups;}
    public Hand getHand() {return hand;}
    
    @FXML void initialize() {
    	fullListing = FXCollections.observableArrayList();
    	lstListing.setItems(fullListing);
    	lstListing.setCellFactory(param -> new DragNDropCell());
    	
    	problem = getCurrentProblem();
    	loadProblem();
    	
        exec = null;

        hboxRunningButtons.setManaged(false);
		display();
    	
    }
    
    @FXML private void selectKeyTerm(MouseEvent event) {
        event.consume();
        String keyTerm = lstLexicon.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("keyword/KeyTerm.fxml"));
        Stage stage = getStage(loader);
        if (stage == null) return;

        // Get the dialog controller so that a public method can be run to send data to the dialog
        KeyTermController ktc = loader.<KeyTermController>getController();

        ktc.setKeyTerm(keyTerm, this);
        // Only display the dialog box if we have some arguments to fill.
        if (ktc.getArgCount()>0) {
            // Show the dialog (and wait for the user to close it)
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait();
        }
        
        if (ktc.okayPressed() || ktc.getArgCount()==0) {
        	fullListing.add(ktc.getInstruction());
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
        	instruction.setArgs();
        } else if (ktc.deleteInstruction()) {
        	fullListing.remove(instruction);
        }

    	lstListing.refresh();
        showRunTimeButtons(fullListing.size() > 0);
        lstListing.getSelectionModel().clearSelection();
    }
    
    @FXML private void execByStep() {
    	if (exec == null) {
    		ArrayList<CommandTerm> code = new ArrayList<>();
    		fullListing.forEach(line -> code.add(line));
    		exec = new Execute(code);
    	}
    	exec.step();
    	manageButtons(!exec.finished());
		display();    		
		
    	if (exec.finished()) {
    		boolean passed = false;
    		if (exec.inError()) {
    			displayError();
    		} else {
    			passed = gradeSolution();
    		}
    		resetPuzzle(passed);
    	} else {
    	}
    }
    @FXML private void execByAuto() {
    	
    }
    @FXML private void execUndo() {
    	
    }
    @FXML private void exec() {
    	
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
           JSONObject jsonObject = (JSONObject)obj;
           name = (String)jsonObject.get("CurrentProblem");
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
           jsonObject = (JSONObject)obj;
           task = (String)jsonObject.get("ObjectiveStatement");
           txtProblem.setText(task);
           cupCount = ((Long)jsonObject.get("PotCount")).intValue();
           ballCount = ((Long)jsonObject.get("BallCount")).intValue();
           JSONArray keyTerms = (JSONArray)jsonObject.get("KeyTerms");
           allKeyTerms = FXCollections.observableArrayList(); 
           allKeyTerms.addAll(keyTerms);
           lstLexicon.setItems(allKeyTerms);
           
           container = new Tray(ballCount);
           hand = new Hand();
           cups = new Cup[cupCount];
           Cup.gradingCups(false);
           for (int i = 0; i < cupCount; i++) {
               cups[i] = new Cup(i);
           }
           solution = ((Map)jsonObject.get("Solution"));
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
        // Create the stage
        Stage inputStage = new Stage();
        // Sets the owner to being this window NOTE primaryStage is set up in StockManagement
        inputStage.initOwner(Main.primaryStage);
        // Add the Scene to the stage
        inputStage.setScene(newScene);
        
        return inputStage;
    }
    
    private void displayError() {
    	Alert a = new Alert(AlertType.NONE);
		String message = exec.getErrorMsg();
		txtErrorMsg.setText(message);
		a.setAlertType(AlertType.ERROR);
		a.setHeaderText("You code needs fixing");
		a.setContentText(message);
		a.initModality(Modality.APPLICATION_MODAL); 
		a.showAndWait();	
    }
    
    private boolean gradeSolution() {
    	JSONObject s = (JSONObject) jsonObject.get("Solution");
    	Cup.gradingCups(true);
    	boolean testPassed = true;
    	if (solution.containsKey("PotCount")) {
    		JSONArray objs = (JSONArray) s.get("PotCount");
    		for (int i = 0; i<cupCount; i++) {
    			int requiredBallsInCup = ((Long)objs.get(i)).intValue();
    			if (!cups[i].correctBallCount(requiredBallsInCup)) {
    				testPassed = false;
    				cups[i].gradeCup(false);
    			}
    			else {
    				cups[i].gradeCup(true);
    			}
    		}
    	}
		Alert a = new Alert(AlertType.NONE);
		String message = "";
    	if (!testPassed) {
    		message = "You didn't find the right solution. Please try again.";
    	} else {
    		message = "Congratulations you solved the puzzle.";
    	}
		txtErrorMsg.setText(message);
		display();
		a.setAlertType(AlertType.ERROR);
		a.setHeaderText("You code has finished running");
		a.setContentText(message);
		a.initModality(Modality.APPLICATION_MODAL); 
		a.showAndWait();
		return testPassed;
    }
    
    private void resetPuzzle(boolean passed) {
    	exec.stopExec();
		exec = null;
		if (passed) {
			// reset problem to the next available problem
		}
		loadProblem();
		
		if (passed) {
			display();
		}
        lstListing.getSelectionModel().clearSelection();
		lstListing.refresh();
        showRunTimeButtons(fullListing.size() > 0);
    }
}
