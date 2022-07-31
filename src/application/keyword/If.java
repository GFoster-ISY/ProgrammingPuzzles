package application.keyword;

import java.util.ArrayList;

import application.Colour;
import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class If extends CommandTerm {

    public boolean conditionState = false;
	
	public If(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedIf.fxml";
		commandTermName = "if";
		Else theElse = new Else(puzzleController, "else", pc.getNextId());
		theElse.setParentTerm(this);
		theElse.setRootTerm(this);
		EndIf endif = (EndIf)theElse.getPrimaryChildTerm();
		endif.setParentTerm(theElse);
		endif.setRootTerm(this);
		childIds.add(theElse.getId());
		childIds.add(endif.getId());
		childTerms = new CommandTerm[2];
		childTerms[0] = theElse;
		childTerms[1] = endif;
	}
	
	@Override protected void setController(FXMLLoader load) {
		nestedController = (NestedIfController)load.getController();
	}

	@Override protected void populateFXML() {
		nestedController.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		req.add(true);
		req.add(true);
		nestedController.setArgRequired(req);
	}

	public int argCount() {return 3;}

	@Override public String toString() {
		return  indent() + "if " + args.get(0) + " " + args.get(1) + " " + args.get(2) + " then";
	}
	
	@Override public boolean exec() {
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
		} else if (args.get(1).equals("Not Equal To")) {
			if (requiredColour.equals(ballColour)) {
				conditionState = false;
			} else {
				conditionState =  true;
			}
		}
		return true;
	}

	@Override public CommandTerm nextCommand() {
		if (conditionState) {
			return null;
		}
		return childTerms[0];
	}
} // end of class If
