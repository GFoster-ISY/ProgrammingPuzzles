package application.keyword;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class NestedTwoArgsController extends NestedController{

    @FXML private Label arg0Name;
    @FXML private Label arg1Name;
    @FXML private TextField arg0Value;
    @FXML private TextField arg1Value;
    
    private boolean required0 = false;
    private boolean required1 = false;

    @FXML void initialize() {
    	arg0Value.setText("");
    	arg1Value.setText("");
    }
    @FXML void onChange() {
    	parentController.enableDefaultButton();
    }


	@Override
	public void setName(String name, int count) {
		if (count == 0) {arg0Name.setText(name);}
		else {arg1Name.setText(name);}
	}

	@Override
	public void setArgValue(ArrayList<String> args) {
    	if (args != null) {
    		if (args.size()>0) {
    			arg0Value.setText(args.get(0));
    		}
    		if (args.size()>1) {
    			arg1Value.setText(args.get(1));
    		}
    	}
	}

	@Override
	public void setArgRequired(ArrayList<Boolean> required) {
    	if (required != null) {
    		if (required.size()>0) {
    			this.required0 = required.get(0);
    		}
    		if (required.size()>1) {
    			this.required1 = required.get(1);
    		}
    	}
	}

	@Override
	public boolean complete() {
		if (required0 && arg0Value.getText().equals("")) return false; 
		if (required1 && arg1Value.getText().equals("")) return false; 
		return true;
	}

	@Override
	public String getArgValue(int posn) {
		if (posn == 0) {return arg0Value.getText();}
		if (posn == 1) {return arg1Value.getText();}
		return null;
	}

}
