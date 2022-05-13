package application.keyword;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class NestedOneArgController extends NestedController{

    @FXML private Label arg0Name;
    @FXML private TextField arg0Value;
    private boolean required;
    
    public void setName(String name) {
    	arg0Name.setText(name);
    }
    public void setArgValue(ArrayList<String> args) {
    	if (args != null && args.size()>0) {
    		arg0Value.setText(args.get(0));
    	}
    }
    public String getArgValue(int posn) {
    	return arg0Value.getText();
    }
	@Override public void setArgRequired(ArrayList<Boolean> required) {
		this.required = required.get(0);
	}
	@Override public boolean complete() {
		if (required && arg0Value.getText().equals("")) return false; 
		return true;
	}
	@Override
	void onChange() {
		parentController.enableDefaultButton();
	}
	
	@Override public int argCount() {return 1;}
}
