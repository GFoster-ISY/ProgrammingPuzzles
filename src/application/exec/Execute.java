package application.exec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import application.PuzzleController;
import application.keyword.CommandTerm;
import application.keyword.Variable;

public class Execute {

	ArrayList<CommandTerm> instructions;
	Map<String, Variable> variableDeclarationList;
	CommandTerm term;
	Stack<CommandTerm> completed;
	int currentLine;
	
	
	public Execute(ArrayList<CommandTerm> listing) {
		instructions = listing;
		initialise();
	}

	public final void initialise() {
		currentLine = 0;
		term = null;
		completed = new Stack<>();
		variableDeclarationList = new HashMap<>();
		for (CommandTerm ct: instructions) {
			ct.setExec(this);
			if (ct instanceof Variable) {
				Variable var = (Variable)ct;
				ct.reset();
				variableDeclarationList.put(var.getVariableName(), var);
			}
		}
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
	
	public void abort() {
		term.abort();
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
	
    public boolean hasVariable(String var) {
    	return variableDeclarationList.containsKey(var);
    }
    public Variable getVariable(String name) {
    	if (variableDeclarationList.containsKey(name)){
    		return variableDeclarationList.get(name);
    	}
    	return null;
    }
}
