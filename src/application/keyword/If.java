package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class If extends CommandTerm {

	protected CommandTerm endIf;
	
	public If(PuzzleController pc) {
		super(pc);
		FXMLFileName = "NestedIf.fxml";
		term = "if";
		needsClosure = true;
		endIf = new EndIf(puzzleController, this);
	}
	

	public CommandTerm getRelatedTerm() {return endIf;}
	@Override public CommandTerm getClosure() {return endIf;}
	
	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedIfController)load.getController();
	}

	@Override
	protected void populateFXML() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setArgs() {
		args = new ArrayList<>();
		args.add(controller.getArgValue(0));
		args.add(controller.getArgValue(1));
		args.add(controller.getArgValue(2));
	}

	@Override
	public boolean exec() {
		// TODO Auto-generated method stub
		return false;
	}

}
