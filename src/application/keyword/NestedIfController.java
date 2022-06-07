package application.keyword;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class NestedIfController extends NestedController {

    @FXML private Label arg0Name;
    @FXML private ChoiceBox<String> arg0Value;
    @FXML private Label arg1Name;
    @FXML private ChoiceBox<String> arg1Value;
    @FXML private Label arg2Name;
    @FXML private ChoiceBox<String> arg2Value;
    
	public NestedIfController() {}

    @FXML void initialize() {
    	super.initialize();

		arg0Value.getItems().add("Colour");
		arg0Value.getSelectionModel().selectFirst();
		arg0Value.setDisable(true);
		
		arg1Value.getItems().add("Equal To");
		arg1Value.getItems().add("Not Equal To");
		arg1Value.getSelectionModel().selectFirst();
		
		arg2Value.getItems().add("Red");
		arg2Value.getItems().add("Green");
		arg2Value.getItems().add("Blue");
		arg2Value.getItems().add("Yellow");
		arg2Value.requestFocus();
    }
    
	@Override
	public void setName(String name, int count) {}

	@Override
	public void setArgValue(ArrayList<String> args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setArgRequired(ArrayList<Boolean> required) {}

	@Override
	public boolean complete() {
		if ( arg0Value.getValue() == null 
		  || arg1Value.getValue() == null
		  || arg2Value.getValue() == null
		   ) return false; 
		return true;
	}

	@Override
	public String getArgValue(int posn) {
		if (posn == 0) return arg0Value.getValue();
		if (posn == 1) return arg1Value.getValue();
		if (posn == 2) return arg2Value.getValue();
		return null;
	}

	@Override
	void onChange() {
		parentController.enableDefaultButton();
	}

}
