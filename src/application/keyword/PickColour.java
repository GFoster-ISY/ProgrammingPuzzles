package application.keyword;

import java.util.ArrayList;

import application.Ball;
import application.Colour;
import application.PuzzleController;
import javafx.fxml.FXMLLoader;

public class PickColour extends Pick {
	
	public PickColour(PuzzleController pc, String term, int id) {
		super(pc, term, id);
		FXMLFileName = "NestedOneArg.fxml";
		commandTermName = "pick";
	}
	
	@Override
	protected void setController(FXMLLoader load) {
		nestedController = (NestedOneArgController)load.getController();
	}
	
	@Override protected void populateFXML () {
		nestedController.setName("Ball Colour");
		nestedController.setArgValue(args);
		ArrayList<Boolean> req = new ArrayList<>();
		req.add(true);
		nestedController.setArgRequired(req);
	}


	public int argCount() {return 1;}

	@Override protected Ball getBall() {
		String colourName = args.get(0);
		if (Colour.validColourName(colourName)) {
			return puzzleController.getContainer().getBall(new Colour(colourName));
		}
		return null;
	}

}
