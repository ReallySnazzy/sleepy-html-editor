package drowsysaturn.sleepyhtmleditor;

import drowsysaturn.sleepyhtmleditor.gui.MainWindowController;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(MainWindowController.class.getResource("MainWindow.fxml"));
		primaryStage.setTitle("Sleepy Website Designer");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

    public static void main(String[] args) {
		launch(args);
    }
}

