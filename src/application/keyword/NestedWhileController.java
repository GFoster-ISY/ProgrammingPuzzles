package application.keyword;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class NestedWhileController extends NestedController {

    @FXML private Label arg0Name;
    @FXML private ChoiceBox<String> arg0Value;
    @FXML private Label arg1Name;
    @FXML private ChoiceBox<String> arg1Value;
    @FXML private Label arg2Name;
    @FXML private TextField arg2Value;
    
	public NestedWhileController() {}

    @FXML void initialize() {
    	super.initialize();

		arg0Value.getItems().add("counter");
		arg0Value.getSelectionModel().selectFirst();
		arg0Value.setDisable(true);
		
		arg1Value.getItems().add("less than");
		arg1Value.getItems().add("less than or equal to");
		arg1Value.getSelectionModel().selectFirst();
		
		arg2Value.requestFocus();
    }
    
    public void setVariableList(int varCount) {
    	arg0Value.getItems().clear();
    	for (int i = 1; i<= varCount; i++) {
    		arg0Value.getItems().add("counter"+i);
    	}
		arg0Value.getSelectionModel().selectLast();
    }
	@Override
	public void setName(String name, int count) {}

	@Override
	public void setArgValue(ArrayList<String> args) {
    	if (args != null) {
    		if (args.size()>0) {
				if (args.get(0) != "") {
					arg0Value.getSelectionModel().select(args.get(0));
				}
    		}
    		if (args.size()>1) {
    			if (args.get(1) != "") {
    				arg1Value.getSelectionModel().select(args.get(1));
    			}
    		}
    		if (args.size()>2) {
    			if (args.get(2) != "") {
    				arg2Value.setText(args.get(2));
    			}
    		}
    	}
	}

	@Override
	public void setArgRequired(ArrayList<Boolean> required) {}

	@Override
	public boolean complete() {
		if ( arg0Value.getValue() == null 
		  || arg1Value.getValue() == null
		  || arg2Value.getText().equals("")
		   ) return false; 
		return true;
	}

	@Override
	public String getArgValue(int posn) {
		if (posn == 0) return arg0Value.getValue();
		if (posn == 1) return arg1Value.getValue();
		if (posn == 2) return arg2Value.getText();
		return null;
	}

	@Override
	void onChange() {
		parentController.enableDefaultButton();
	}

}
