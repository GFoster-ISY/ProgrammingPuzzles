package application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.*;

import application.exec.Execute;
import application.keyword.CommandTerm;
import application.keyword.KeyTermController;
import application.keyword.UnknownKeywordException;
import application.problem.Problem;
import application.problem.ProblemListViewCell;
import application.problem.ProblemManager;
import application.problem.ProblemHistory;

public class PuzzleController {

    @FXML private HBox hboxRoot;

    @FXML private Accordion problemView;
	@FXML private TitledPane problemListing;
	@FXML private TitledPane selectedProblem;
	@FXML private TitledPane statistics;
	
    @FXML private VBox vboxSelectedProblem;
    @FXML private TextArea txtProblem;
    @FXML private Canvas cvsCups;
    @FXML private Canvas cvsHand;
    @FXML private Canvas cvsContainer;
    @FXML private TextField txtErrorMsg;
    
    @FXML
	public ChoiceBox<Problem> cbProblemList;
    @FXML private Label lblStatsLastRun;
    @FXML private Label lblStatsAttempts;
    @FXML private Label lblStatsFailCount;
    @FXML private Label lblStatsErrorCount;
    @FXML private Label lblStatsSuccessRate;

    @FXML private Accordion previousRunView;
	@FXML private TitledPane previousRun;
	@FXML private TitledPane previousSuccessfulRun;
    @FXML public ListView<CommandTerm> lstPreviousRun;
    @FXML private Button btnPrevCopy;
    @FXML public ListView<CommandTerm> lstPreviousSuccessfulRun; 
    @FXML private Button btnSuccessCopy;
    
    @FXML private ListView<Problem> lstProblemListing;
    
    @FXML public ListView<CommandTerm> lstListing;
    @FXML private HBox hboxRunningButtons;
    @FXML private Button btnAbort;
    @FXML private Button btnNext;
    @FXML private Button btnFinish;
    @FXML private HBox hboxStartExecuteButtons;
    @FXML private Button btnStep;
    @FXML private Button btnAuto;
    @FXML private Button btnFinal;
    @FXML private Button btnCopy;
    @FXML private ListView<String> lstLexicon;
    

    private Container container;
    private Cup[] cups;
    private Hand hand;
    private int cupCount;
    private ProblemManager pm;
    public ObservableList<String> allKeyTerms;
    private int nextUniqueId = 1;
    public Execute exec;
    Thread execThread;
    Thread finishThread;
    private boolean execThreadRunning = false;
    private boolean finishThreadRunning = false;
    
    
    @FXML void initialize() {
    	pm = new ProblemManager(this);
    	problemView.setExpandedPane(selectedProblem);
    	previousRunView.setExpandedPane(previousRun);
    	allKeyTerms = FXCollections.observableArrayList();

    	lstProblemListing.setItems(pm.loadAllProblemsFromJSONFile());
    	lstProblemListing.getSelectionModel().select(pm.getCurrentProblemIndex());
    	lstProblemListing.setCellFactory(problemCell -> new ProblemListViewCell());
    	
    	cbProblemList.setItems(pm.getProblemListing());
    	cbProblemList.getSelectionModel().select(pm.getCurrentProblemIndex());
    	
    	lstListing.setCellFactory(param -> new DragNDropCommandTermCell(this));

        lstLexicon.setItems(allKeyTerms);
    	
        pm.loadCurrentProblem();

    	lstPreviousRun.refresh();
    	lstPreviousSuccessfulRun.refresh();
    	
        clear();
        exec = null;

        hboxRunningButtons.setManaged(false);
		display();
    }
    
    @FXML private void changeStatsProblem(ActionEvent ev) {
        Problem selectedItem = cbProblemList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
	        ProblemHistory ph = selectedItem.getStats();
	        displayStats(ph);
        }
    }
    
    public void displayStats(ProblemHistory ph) {
		if (ph == null) {
		    lblStatsLastRun.setText("Welcome have a go at this problem");
		    lblStatsAttempts.setText("0");
		    lblStatsFailCount.setText("0");
		    lblStatsErrorCount.setText("0");
		    lblStatsSuccessRate.setText("");
		} else {
		    lblStatsLastRun.setText(ph.getLastRun());
		    lblStatsAttempts.setText(""+ph.getAttempts());
		    lblStatsFailCount.setText(""+ph.getFailCount());
		    lblStatsErrorCount.setText(""+ph.getErrorCount());
		    lblStatsSuccessRate.setText(ph.getSuccessRate());
		    lstPreviousRun.setItems(pm.currentProblem.previousRunListing);
		   	lstPreviousSuccessfulRun.setItems(pm.currentProblem.previousSuccessfulRunListing);
		}
    }
    
    public Container getContainer() {return container;}
    public Cup[] getCups() { return cups;}
    public Hand getHand() {return hand;}
    public void setProblemText (String text) {txtProblem.setText(text);}
    public int getCupCount () {return cupCount;}
    public void setCupCount (int number) {cupCount = number;}
    public void setContainer(Container container) {this.container = container;}
    public ObservableList<CommandTerm> getFullListing(){return pm.currentProblem.fullListing;}
    @SuppressWarnings("unchecked")
	public void setAllKeyTerms(JSONArray keyTerms) {
    	allKeyTerms = FXCollections.observableArrayList();
    	allKeyTerms.addAll(keyTerms);
    	lstLexicon.setItems(allKeyTerms);
    }
    public void initialiseControls() {
        hand = new Hand();
        cups = new Cup[cupCount];
        Cup.gradingCups(false);
        for (int i = 0; i < cupCount; i++) {
            cups[i] = new Cup(i);
        }
		container.resetContainer();
    }
    
    public boolean hasCodeListing() {
    	return lstListing.getItems().size() > 0;
    }

    public void clearListingSelection() {
    	lstListing.getSelectionModel().clearSelection();
		lstListing.refresh();
    }
    
    public void clear() {
    	pm.clear();
    }
    
    @FXML private void selectProblem(MouseEvent click) {
    	if (click.getClickCount() == 2) {
            Problem problem = lstProblemListing.getSelectionModel()
                                                     .getSelectedItem();
            pm.setCurrentProblem(problem);
            display();
            lstProblemListing.refresh();
            selectedProblem.setExpanded(true);
         }
    }
    
    @FXML private void copyPrevCode() {
    	copyCode(lstPreviousRun);
    }
    
    @FXML private void copySuccessCode() {
    	copyCode(lstPreviousSuccessfulRun);
    }
    
    @FXML private void clearCode() {
    	clear();
    	showRunTimeButtons(false);
    }
    
    private void copyCode(ListView<CommandTerm> codeListing) {
    	pm.copyCode(codeListing);
        selectedProblem.setExpanded(true);
        showRunTimeButtons(hasCodeListing());    	
    }
    public void setErrorMsg(String msg) {txtErrorMsg.setText(msg);}
    
    @FXML private void addNewKeyTerm(MouseEvent event) {
        event.consume();
        String keyTerm = lstLexicon.getSelectionModel().getSelectedItem();
        if (keyTerm == null) return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("keyword/KeyTerm.fxml"));
        Stage stage = getStage(loader);
        if (stage == null) return;

        CommandTerm dummyct = null;
        KeyTermController ktc = loader.<KeyTermController>getController();
        // Create a dummy object so that the dialog box can be populated
        try {
        	dummyct = KeyTermController.getNewKeyTerm(keyTerm, this, getNextId(),null);
            // Only display the dialog box if we have some arguments to fill.
            if (ktc.hasArguments(dummyct)) {
            	ktc.displayNestedFXML(dummyct);
                // Show the dialog (and wait for the user to close it)
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            }
            
            if (ktc.okayPressed() || !ktc.hasArguments(dummyct)) {
            	// Now create an instance of the commandTerm
            	ktc.createInstance(this, keyTerm, pm.currentProblem.fullListing);
            }
        } catch (UnknownKeywordException ex) {
        	System.err.println("UnknownKeywordException: " + ex.getMessage());
        	ex.printStackTrace();
        	return;
        }
        showRunTimeButtons(hasCodeListing());
        lstLexicon.getSelectionModel().clearSelection();
    }
    
    @FXML private void modifyKeyTerm(MouseEvent event) {
    	event.consume();
        CommandTerm instruction = lstListing.getSelectionModel().getSelectedItem();
        if (instruction == null) {return;}
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("keyword/KeyTerm.fxml"));
        Stage stage = getStage(loader);
        if (stage == null) return;

        // Get the dialog controller so that a public method can be run to send data to the dialog
        KeyTermController ktc = loader.<KeyTermController>getController();
        ktc.displayNestedFXML(instruction);
        ktc.showEditButton();
        ktc.showDeleteButton();
        
        // Show the dialog (and wait for the user to close it)
        stage.initModality(Modality.APPLICATION_MODAL); 
        stage.showAndWait();
        if (ktc.okayPressed()) { 
        	instruction.updateArgs();
        } else if (ktc.deleteInstruction()) {
        	pm.currentProblem.remove(instruction);
        }

    	lstListing.refresh();
        showRunTimeButtons(hasCodeListing());
        lstListing.getSelectionModel().clearSelection();
    }
        
    public int getNextId() {
    	return nextUniqueId++;
    }
    
    public void updateNextId(int newId) {
    	if (newId >= nextUniqueId) {nextUniqueId++;}
    }
    private void runOneLineOfCode() {
    	if (exec == null) {
    		ArrayList<CommandTerm> code = new ArrayList<>();
    		for (CommandTerm ct: pm.currentProblem.fullListing) {
    			code.add(ct);
    		}
    		exec = new Execute(code);
    	}
    	exec.step();
    	lstListing.refresh();
    }
    
    private void codeCompleted() {
    	execThreadRunning = false;
		if (exec.inError()) {
			displayError();
		} else {
			if (pm.gradeSolution()) {
				clear();
				display();
				lstProblemListing.getSelectionModel().select(pm.currentProblem);
				cbProblemList.getSelectionModel().select(pm.currentProblem);
			} else {
				initialiseControls();
			}
		}
		clearListingSelection();
		manageButtons(false);
		showRunTimeButtons(hasCodeListing());

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
    
    @FXML private void execAbort() {
    	exec.abort();
    	if (execThreadRunning) {
    		execThread.interrupt();
    	}
    	if (finishThreadRunning) {
    		finishThread.interrupt();
    	}
    	codeCompleted();
    }
    
    @FXML private void exec() {
    	if (execThreadRunning) {
    		execThread.interrupt();
    	}
    	finishThread = new Thread(() -> {
    		finishThreadRunning = true;
    		try {
		    	while (exec == null || !exec.finished()) {
		        	runOneLineOfCode();
		        	manageButtons(!exec.finished());
		        	Platform.runLater(() -> display());
		        	Thread.sleep(10);
				}
			} catch (InterruptedException e) {
				return;
			} catch (Exception e) {
				System.out.println("OUT OF TIME");
				e.printStackTrace();
	    	}
	    	Platform.runLater(() -> codeCompleted());
    	});
    	finishThread.start();
    }
    
    protected void resizeWidth(Number newWidth) {
    	hboxRoot.setPrefWidth(newWidth.doubleValue());
    	double columnWidth = Math.max(newWidth.doubleValue() / 3,300);
    	txtProblem.setPrefWidth(columnWidth);
    	cvsCups.setWidth(columnWidth);
    	cvsHand.setWidth(columnWidth);
    	cvsContainer.setWidth(columnWidth);
    	problemView.setMinWidth(columnWidth);
    	problemView.setMaxWidth(columnWidth);
    	lstListing.setMinWidth(columnWidth);
    	lstListing.setMaxWidth(columnWidth);
    	lstLexicon.setMinWidth(columnWidth);
    	lstLexicon.setMaxWidth(columnWidth);
    	display();
    }
    protected void resizeHeight(Number newHeight) {
    	hboxRoot.setPrefHeight(newHeight.doubleValue());
    	vboxSelectedProblem.setPrefHeight(newHeight.doubleValue());
    	double canvasHeight = (newHeight.doubleValue() - 180)/3;
    	cvsCups.setHeight(canvasHeight);
    	cvsHand.setHeight(canvasHeight);
    	cvsContainer.setHeight(canvasHeight);
    	display();
    }
    
    public void display() {
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
        selectedProblem.setText("Selected Problem - " + pm.getCurrentProblemName());
    	Main.primaryStage.setTitle("Programming Puzzles - " + pm.getCurrentProblemName() );
    }
    
    public void manageButtons(boolean running) {
    	showRunTimeButtons(running);

    	hboxStartExecuteButtons.setVisible(!running);
    	hboxStartExecuteButtons.setManaged(!running);
    	hboxRunningButtons.setVisible(running);
    	hboxRunningButtons.setManaged(running);
    }
    
    public void showRunTimeButtons(boolean show) {
    	btnStep.setDisable(!show);
    	btnAuto.setDisable(!show);
    	btnFinal.setDisable(!show);
    	btnCopy.setDisable(!show);
    	btnNext.setDisable(!show);
    	btnAbort.setDisable(!show);
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
		pm.reset();
		initialiseControls();
    }

}
