package application.exec;

import java.util.ArrayList;
import java.util.Stack;

import application.PuzzleController;
import application.keyword.CommandTerm;

public class Execute {

	ArrayList<CommandTerm> instructions;
	CommandTerm term;
	Stack<CommandTerm> completed;
	int currentLine;
	
	
	public Execute(ArrayList<CommandTerm> listing) {
		instructions = listing;
		currentLine = 0;
		term = null;
		completed = new Stack<>();
	}

	public boolean step() {
		if (term != null) {
			term.setRunningState(false);
		}
		if (currentLine < instructions.size()) {
			term = instructions.get(currentLine);
			term.clearError();
			if (term.exec()) {
				CommandTerm nc = term.nextCommand(); 
				if (nc == null) {
					currentLine++;
				} else {
					currentLine = instructions.indexOf(nc);
				}
				completed.push(term);
				term.setRunningState(true);
				return true;
			}
		}
		return false;
	}
	
	public void stopExec() {
		term.setRunningState(false);
	}
	
	public CommandTerm getTerm() {return term;}
	public String getErrorMsg() {
		return term.errorMsg();
	}
	
	public boolean inError() { 
		return term.errorMsg() != null;
	}
	
	public boolean finished() {
		return (currentLine == instructions.size() || inError());
	}
}
