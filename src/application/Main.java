package application;
	
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	static PuzzleController controller;
	public static Stage primaryStage;
	@Override
	public void start(Stage stage) {
		try {
			primaryStage = stage;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Puzzle.fxml"));
			AnchorPane root = (AnchorPane)loader.load();
			controller = (PuzzleController)loader.getController();
			Scene scene = new Scene(root,1200,600);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setMinHeight(600);
			primaryStage.setMinWidth(900);
			scene.widthProperty().addListener(new ChangeListener<Number>() {
			    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
			    	controller.resizeWidth(newSceneWidth);
			    }
			});
			scene.heightProperty().addListener(new ChangeListener<Number>() {
			    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
			    	controller.resizeHeight(newSceneHeight);
			    }
			});
			primaryStage.show();
			controller.resizeWidth(1200);
			controller.resizeHeight(600);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
