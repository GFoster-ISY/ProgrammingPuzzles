package application.keyword;

import java.util.ArrayList;

import application.Ball;
import application.Colour;
import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class PickColour extends Pick {
	
	public PickColour(PuzzleController pc) {
		super(pc);
		FXMLFileName = "NestedOneArg.fxml";
		term = "pick";
	}
	
	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedOneArgController)load.getController();
	}
	
	@Override protected void populateFXML () {}

	@Override public void setArgs() {
		args = new ArrayList<>();
		args.add(controller.getArgValue(0));
	}

	@Override protected Ball getBall() {
		String colourName = args.get(0);
		if (Colour.validColourName(colourName)) {
			return puzzleController.getContainer().getBall(new Colour(colourName));
		}
		return null;
	}

}
