package application.keyword;

import application.PuzzleController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class KeyTermController {
	
    @FXML private Label lblCommand;
    @FXML private GridPane gridPane;
    @FXML private Pane fxmlEmbed;
    @FXML private Button btnDefault;
    @FXML private Button btnDelete;
        
    NestedController nc;
    CommandTerm keyword;
    protected boolean okay = false;
    protected boolean delete = false;
    
    @FXML private void onClickOkay() {
    	okay = true;
    	close();
    }
    @FXML private void onClickCancel() {
    	okay = false;
    	close();    	
    }

    @FXML private void onClickDelete() {
    	okay = false;
    	delete = true;
    	close();
    }

    @FXML void initialize() {
    	NestedController.setParentController(this);
    }
    
    public void setNestedController(NestedController controller) {
    	nc = controller;
    }
    
    private void close() {
    	Stage stage = (Stage) fxmlEmbed.getScene().getWindow();
        stage.close();    	
    }
    public static CommandTerm getNewKeyTerm(String term
    		                               ,PuzzleController pc
    		                               ,int id
    		                               ,ObservableList<CommandTerm> listing
    		                               ) throws UnknownKeywordException{
    	CommandTerm keyword;
    	if (term.equals("put(n)")){
    		keyword = new Put(pc, term, id);
    	} else if(term.equals("pick()")){
    		keyword = new Pick(pc, term, id);
    	} else if(term.equals("pick(colour)")){
    		keyword = new PickColour(pc, term, id);
    	} else if(term.equals("loop")){
    		keyword = new Loop(pc, term, id);
    	} else if(term.equals("loop until")){
    		keyword = new LoopUntil(pc, term, id, listing);
    	} else if (term.equals("loop while")){
    		keyword = new LoopWhile(pc, term, id, listing);
    	} else if(term.equals("if")){
    		keyword = new If(pc, term, id);
    	} else if(term.equals("replace()")){
    		keyword = new Replace(pc, term, id);
    	} else if(term.equals("increment(n)")){
    		keyword = new Increment(pc, term, id);
    	} else if(term.equals("look()")){
    		keyword = new Look(pc, term, id);
    	} else if(term.equals("endloop")){
    		keyword = new EndLoop(pc, term, id);
    	} else if (term.equals("endloopuntil")){
    		keyword = new EndLoopUntil(pc, term, id);
    	} else if (term.equals("endloopwhile")){
    		keyword = new EndLoopWhile(pc, term, id);
    	} else if (term.equals("endif")){
    		keyword = new EndIf(pc, term, id);
    	} else if (term.equals("else")){
    		keyword = new Else(pc, term, id);
    	} else if(term.equals("integer")){
    		keyword = new VarInteger(pc, term, id);
    	} else {
    		throw new UnknownKeywordException (term);
    	}// end if on keyword
    	return keyword;
    }
    
    public void createInstance(PuzzleController pc, String term, ObservableList<CommandTerm>listing) throws UnknownKeywordException {
    	CommandTerm newTerm = getNewKeyTerm(term, pc, pc.getNextId(),listing);
    	newTerm.updateArgs();
    	newTerm.addToListing(listing);
    }

    public void enableDefaultButton() {
    	btnDefault.setDisable(nc==null || !nc.complete());
    }
    
    public String getArgValue(int posn) {
    	return nc.getArgValue(posn);
    }

    public void showEditButton() {
    	btnDefault.setText("Edit");
    }
    public void showDeleteButton() {
    	btnDelete.setVisible(true);
    }
    public boolean okayPressed() {return okay;}
    public boolean deleteInstruction() {return delete;}
    public CommandTerm getInstruction() {
    	return keyword;
    }

    public int getArgCount() { return getArgCount(keyword);}
    public int getArgCount(CommandTerm ct) {
    	if (ct == null) return 0;
    	return ct.argCount();
	}
    public boolean hasArguments() {
    	return hasArguments(keyword);
    }
    public boolean hasArguments(CommandTerm ct) {
    	if (ct == null) return false;
    	return  ct.argCount()>0;
    }
    public void displayNestedFXML(CommandTerm ct) {
    	lblCommand.setText(ct.commandTermName);
    	ct.display(fxmlEmbed, this);
    	fxmlEmbed.setMinHeight(getArgCount(ct)*30);
    	gridPane.setMinHeight(getArgCount(ct)*30+70);
    	enableDefaultButton();    	
    }

}
