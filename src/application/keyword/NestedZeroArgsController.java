package application.keyword;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class NestedZeroArgsController extends NestedController{

    @FXML private Label arg0Name;
    @FXML private TextField arg0Value;
    
    public void setName(String name) {
    	arg0Name.setText(name);
    }
    public String getArgValue(int posn) {
    	return null;
    }
	@Override public void setArgValue(ArrayList<String> args) {}
	@Override public void setArgRequired(ArrayList<Boolean> required) {}
	@Override public boolean complete() {return true;}
	@Override void onChange() {}
	@Override public int argCount() {return 0;}
}
