package application.keyword;

import java.util.ArrayList;

import application.Ball;
import application.Container;
import application.Cup;
import application.Hand;
import javafx.fxml.FXMLLoader;

public class Pick extends CommandTerm {
	
	public Pick(Container container, Hand hand, Cup[] cups) {
		super(container, hand, cups);
		FXMLFileName = "NestedZeroArgs.fxml";
		term = "pick";
	}
	
	@Override
	protected void setController(FXMLLoader load) {
		controller = (NestedZeroArgsController)load.getController();
	}
	
	@Override protected void populateFXML () {}

	@Override public void setArgs() {
		args = new ArrayList<>();
	}

	@Override public boolean exec() {
		if (!hand.isEmpty()) {
			errorMessage = "The hand is already holding a ball";
			return false;
		}
		Ball ball = bag.getBall();
		if (ball != null) {
			hand.put(ball);
			return true;
		} else {
			errorMessage = "No ball is available to pick up";
			return false;
		}
	}

}
