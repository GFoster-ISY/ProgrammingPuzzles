package application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
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
import java.util.HashMap;
import java.util.Map;

import org.json.simple.*;

import application.exec.Execute;
import application.keyword.CommandTerm;
import application.keyword.KeyTermController;
import application.keyword.UnknownKeywordException;
import application.keyword.Variable;
import application.problem.Problem;
import application.problem.ProblemManager;

public class PuzzleController {

    @FXML private HBox hboxRoot;

    @FXML private Accordion ProblemView;
	@FXML private TitledPane ProblemListing;
	@FXML private TitledPane SelectedProblem;
	@FXML private TitledPane Statistics;
	
    @FXML private VBox vboxSelectedProblem;
    @FXML private TextArea txtProblem;
    @FXML private Canvas cvsCups;
    @FXML private Canvas cvsHand;
    @FXML private Canvas cvsContainer;
    @FXML private TextField txtErrorMsg;
    
    @FXML private ListView<Problem> lstProblemListing;
    
    @FXML private ListView<CommandTerm> lstListing;
    @FXML private HBox hboxRunningButtons;
    @FXML private Button btnAbort;
    @FXML private Button btnNext;
    @FXML private Button btnFinish;
    @FXML private HBox hboxStartExecuteButtons;
    @FXML private Button btnStep;
    @FXML private Button btnAuto;
    @FXML private Button btnFinal;
    @FXML private ListView<String> lstLexicon;
    

    private Container container;
    private Cup[] cups;
    private Hand hand;
    private int cupCount;
    private ProblemManager pm;
    private ObservableList<CommandTerm> fullListing;
    private ObservableList<String> allKeyTerms;
    private Map<String, Variable> variableList;
    public Execute exec;
    Thread execThread;
    Thread finishThread;
    private boolean execThreadRunning = false;
    private boolean finishThreadRunning = false;
    
    
    @FXML void initialize() {
    	pm = new ProblemManager(this);
    	ProblemView.setExpandedPane(SelectedProblem);
    	lstProblemListing.setItems(pm.loadAllProblemsFromJSONFile());
    	
    	fullListing = FXCollections.observableArrayList();
    	lstListing.setItems(fullListing);
    	lstListing.setCellFactory(param -> new DragNDropCommandTermCell(this));

    	allKeyTerms = FXCollections.observableArrayList();
        lstLexicon.setItems(allKeyTerms);
    	
        variableList = new HashMap<>();
    	
        pm.loadCurrentProblem();
        
        exec = null;

        hboxRunningButtons.setManaged(false);
		display();
    }
    
    public Container getContainer() {return container;}
    public Cup[] getCups() { return cups;}
    public Hand getHand() {return hand;}
    public void setProblemText (String text) {txtProblem.setText(text);}
    public int getCupCount () {return cupCount;}
    public void setCupCount (int number) {cupCount = number;}
    public void setContainer(Container container) {this.container = container;}
    @SuppressWarnings("unchecked")
	public void setAllKeyTerms(JSONArray keyTerms) {
    	allKeyTerms = FXCollections.observableArrayList();
    	allKeyTerms.addAll(keyTerms);
    	lstLexicon.setItems(allKeyTerms);
    }
    public void initilaliseControls() {
        hand = new Hand();
        cups = new Cup[cupCount];
        Cup.gradingCups(false);
        for (int i = 0; i < cupCount; i++) {
            cups[i] = new Cup(i);
        }
    }
    
    public boolean hasCodeListing() {
    	return fullListing.size() > 0;
    }
    public void clearListingSelection() {
    	lstListing.getSelectionModel().clearSelection();
		lstListing.refresh();
    }
    
    public void clear() {
    	fullListing.clear();
		variableList.clear();
    }
    
    public void reset() {
    	clearListingSelection();
    	fullListing.forEach(term -> {
			term.reset();
		});
    	initilaliseControls();
    	exec = null;
    }
    
    @FXML private void selectProblem(MouseEvent click) {
    	if (click.getClickCount() == 2) {
            Problem selectedProblem = lstProblemListing.getSelectionModel()
                                                     .getSelectedItem();
            pm.setCurrentProblem(selectedProblem);
            display();
            SelectedProblem.setExpanded(true);
         }
    }
    
    public void setErrorMsg(String msg) {txtErrorMsg.setText(msg);}
    
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
		if (exec.inError()) {
			displayError();
		} else {
			pm.gradeSolution();
		}
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
    	ProblemView.setMinWidth(columnWidth);
    	ProblemView.setMaxWidth(columnWidth);
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
    	lstListing.refresh();
        SelectedProblem.setText("Selected Problem - " + pm.getCurrentProblemName());
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
		reset();
    }

}
