package application.keyword;

import java.io.IOException;
import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public abstract class CommandTerm {

	protected String term;
	protected ArrayList<String> args;
	protected boolean needsClosure;
	protected boolean closesIndent;
	protected int indentLevel;
	protected NestedController controller;
	protected String FXMLFileName;
	protected PuzzleController puzzleController;
	protected String errorMessage;
	protected boolean runningState;
	
	CommandTerm(PuzzleController pc){
		errorMessage = null;
		puzzleController = pc;
		needsClosure = false;
		closesIndent = false;
		indentLevel = 0;
		runningState = false;
	}
	
	public boolean hasClosure() { return needsClosure;}
	public boolean getClosesIndent() { return closesIndent;}
	public CommandTerm getRelatedTerm() {return null;}
	public int getIndentLevel() {return indentLevel;}
	public void setIndentLevel(int level) {indentLevel = level;}
	
	protected String indent() {return "   ".repeat(indentLevel);}
	public String toString() {
		if (args == null) {
			setArgs();
		}
		String argList = String.join(",", args);
		return indent() + term + "(" + argList + ")";
	}
	
	public CommandTerm getClosure() {return null;}
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
	
	public void setRunningState(boolean state) {runningState = state;}
	public boolean isRunning() {return runningState;}
	public boolean isInError() {return errorMessage != null;}
	public CommandTerm nextCommand() {return null;}
	public void reset() {runningState = false;}
	
	protected abstract void setController(FXMLLoader load);
	protected abstract void populateFXML();
	public abstract void setArgs();
	public abstract boolean exec();
}
