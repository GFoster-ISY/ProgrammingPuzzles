package application.keyword;

import java.util.ArrayList;

import javafx.fxml.FXML;

public abstract class NestedController {
	static KeyTermController parentController;
    public static void setParentController(KeyTermController pc) {
    	parentController = pc;
    }
    @FXML void initialize() {
    	
    }
    public abstract int argCount();
	public abstract void setName(String name);
	public abstract void setArgValue(ArrayList<String> args);
	public abstract void setArgRequired(ArrayList<Boolean> required);
	public abstract boolean complete();
	public abstract String getArgValue(int posn);
	@FXML abstract void onChange();
}
