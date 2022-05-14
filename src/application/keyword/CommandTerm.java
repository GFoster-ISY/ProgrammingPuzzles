package application.keyword;

import java.io.IOException;
import java.util.ArrayList;

import application.Container;
import application.Cup;
import application.Hand;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public abstract class CommandTerm {

	protected String term;
	protected ArrayList<String> args;
	protected NestedController controller;
	protected String FXMLFileName;
	protected Container bag;
	protected Hand hand;
	protected Cup[] cups;
	protected String errorMessage;
	protected boolean runningState;
	
	CommandTerm(Container container, Hand hand, Cup[] cups){
		errorMessage = null;
		bag = container;
		this.hand = hand;
		this.cups = cups;
		runningState = false;
	}
	
	public String toString() {
		if (args == null) {
			setArgs();
		}
		String argList = String.join(",", args);
		return term + "(" + argList + ")";
	}
	
	public String errorMsg() { return errorMessage;}
	public void clearError() { errorMessage = null;}
	
	public void display(Pane fxmlEmbed, KeyTermController controller) {
		FXMLLoader load = new FXMLLoader(getClass().getResource(FXMLFileName));
		try {
			fxmlEmbed.getChildren().add((Node)load.load());
			setController(load);
			controller.setNestedController(this.controller);
			populateFXML ();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showRunning(boolean state) {runningState = state;}
	public boolean isRunning() {return runningState;}
	public boolean isInError() {return errorMessage != null;}
	
	protected abstract void setController(FXMLLoader load);
	protected abstract void populateFXML();
	public abstract void setArgs();
	public abstract boolean exec();
}
