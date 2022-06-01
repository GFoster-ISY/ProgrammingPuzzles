package application.keyword;

import java.util.ArrayList;

import application.Ball;
import application.Colour;
import application.PuzzleController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class If extends CommandTerm {


    protected CommandTerm theElse;
    public boolean conditionState = false;
	
	public If(PuzzleController pc) {
		super(pc);
		FXMLFileName = "NestedIf.fxml";
		term = "if";
		needsClosure = true;
		theElse = new Else(puzzleController, this);
	}
	

	public CommandTerm getRelatedTerm() {return theElse;}
	@Override public CommandTerm getClosure() {return theElse;}
	
	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedIfController)load.getController();
	}

	@Override
	protected void populateFXML() {
		controller.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		req.add(true);
		req.add(true);
		controller.setArgRequired(req);
	}

	@Override
	public void setArgs() {
		args = new ArrayList<>();
		args.add(controller.getArgValue(0));
		args.add(controller.getArgValue(1));
		args.add(controller.getArgValue(2));
	}

	@Override public String toString() {
		if (args == null) {
			setArgs();
		}
		return  indent() + "if " + args.get(0) + " " + args.get(1) + " " + args.get(2) + " then";
	}
	
	@Override
	public boolean exec() {
		if (puzzleController.getHand().isEmpty()) {
			errorMessage = "There is no ball to look at";
			return false;
		}
		Colour ballColour = puzzleController.getHand().getBallColour();
		Colour requiredColour = new Colour(args.get(2));
		if (args.get(1).equals("Equal To")) {
			if (requiredColour.equals(ballColour)) {
				conditionState = true;
			} else {
				conditionState =  false;
			}
		}
		return true;
	}

	@Override public CommandTerm nextCommand() {
		if (conditionState) {
			return null;
		}
		return theElse;
	}
}
