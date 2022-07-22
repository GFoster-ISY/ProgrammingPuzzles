package application.keyword;

import java.util.Stack;

import application.PuzzleController;
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
    public static CommandTerm getNewKeyTerm(String term, PuzzleController pc, int id) throws UnknownKeywordException{
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
    		keyword = new LoopUntil(pc, term, id);
    	} else if(term.equals("if")){
    		keyword = new If(pc, term, id);
    	} else if(term.equals("replace()")){
    		keyword = new Replace(pc, term, id);
    	} else if(term.equals("increment(n)")){
    		keyword = new Increment(pc, term, id);
    	} else if(term.equals("look()")){
    		keyword = new Look(pc, term, id);
    	} else if(term.equals("endloop")){
    		keyword = new EndLoop(pc, null, id);
    	} else if (term.equals("endloopuntil")){
    		keyword = new EndLoopUntil(pc, null, id);
    	} else if (term.equals("endif")){
    		keyword = new EndIf(pc, null, id);
    	} else if (term.equals("else")){
    		keyword = new Else(pc, null, id);
    	} else if(term.equals("variable")){
    		keyword = new Variable(pc, term, id);
    	} else {
    		throw new UnknownKeywordException (term);
    	}// end if on keyword
    	return keyword;
    }
       
	public void setKeyTerm(String term, PuzzleController pc) throws UnknownKeywordException{
		keyword = getNewKeyTerm(term, pc, pc.getNextId());
    	displayNestedFXML();
    }
    
    public void setKeyTerm(CommandTerm term) {
    	keyword = term;
    	btnDefault.setText("Edit");
    	displayNestedFXML();
    }
    
    public int argCount() {return keyword.argCount();}
    protected void displayNestedFXML() {
    	lblCommand.setText(keyword.commandTermName);
    	keyword.display(fxmlEmbed, this);
    	fxmlEmbed.setMinHeight(getArgCount()*30);
    	gridPane.setMinHeight(getArgCount()*30+60);
    	enableDefaultButton();    	
    }

    public void enableDefaultButton() {
    	btnDefault.setDisable(nc==null || !nc.complete());
    }
    
    public int getArgCount() {return keyword.argCount();}
    public String getArgValue(int posn) {
    	return nc.getArgValue(posn);
    }

    public void showDeleteButton() {
    	btnDelete.setVisible(true);
    }
    public boolean okayPressed() {return okay;}
    public boolean deleteInstruction() {return delete;}
    public CommandTerm getInstruction() {
    	return keyword;
    }
    
}
