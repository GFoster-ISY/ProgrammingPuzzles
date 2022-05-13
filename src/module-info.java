module ProgrammingPuzzles {
	requires javafx.controls;
	requires javafx.fxml;
	
	requires json.simple;
	requires javafx.graphics;
	requires javafx.base;
	
	opens application to javafx.graphics, javafx.fxml;
	opens application.keyword to javafx.graphics, javafx.fxml;
}
