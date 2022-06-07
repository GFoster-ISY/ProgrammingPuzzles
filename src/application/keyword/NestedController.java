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
    public final int argCount() {return parentController.getArgCount();};
    public final void setName(String name) {setName(name,0);}
	public abstract void setName(String name, int argCount);
	public abstract void setArgValue(ArrayList<String> args);
	public abstract void setArgRequired(ArrayList<Boolean> required);
	public abstract boolean complete();
	public abstract String getArgValue(int posn);
	@FXML abstract void onChange();
}
