package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class Loop extends CommandTerm {

	public Loop(PuzzleController pc) {
		super(pc);
		FXMLFileName = "NestedOneArg.fxml";
		term = "loop";
		closure = true;
	}

	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedOneArgController)load.getController();
	}

	@Override
	protected void populateFXML() {
		controller.setName("Times");
		controller.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		controller.setArgRequired(req);
	}

	@Override
	public void setArgs() {
		args = new ArrayList<>();
		args.add(controller.getArgValue(0));
	}

	@Override
	public String toString() {
		if (args == null) {
			setArgs();
		}
		return  indent() + "loop from 1 to " + args.get(0);
	}
	
	@Override
	public boolean exec() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override public CommandTerm getClosure() {return new EndLoop(puzzleController, this);}
}
