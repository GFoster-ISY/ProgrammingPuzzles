package maker;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class PuzzleMaker extends Application{

	public static Stage primaryStage;
	static EditorController controller;

	@Override public void start(Stage stage) {
		try {
			primaryStage = stage;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("editor.fxml"));
			AnchorPane root = (AnchorPane)loader.load();
			controller = (EditorController)loader.getController();
			Scene scene = new Scene(root,650,570);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setMinHeight(500);
			primaryStage.setMinWidth(650);
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
