package application.keyword;

import java.util.ArrayList;

import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class EndLoop extends CommandTerm {

	protected CommandTerm openLoop;
	
	public EndLoop(PuzzleController pc, CommandTerm loop) {
		super(pc);
		FXMLFileName = "NestedZeroArgs.fxml";
		term = "endloop";
		closesIndent = true;
		openLoop = loop;
	}
	
	@Override public CommandTerm getClosure() {return null;}

	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedZeroArgsController)load.getController();
	}

	@Override protected void populateFXML() { }
	@Override public void setArgs() {args = new ArrayList<>();}

	@Override
	public String toString() {
		return  indent() + "end loop";
	}
	@Override
	public boolean exec() {
		// TODO Auto-generated method stub
		return false;
	}

}
