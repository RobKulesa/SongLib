package songlib.app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import songlib.view.Controller;

public class App extends Application {

	Stage mainStage;
	
	public void start(Stage stage) {
		mainStage = stage;
		mainStage.setTitle("SongLib =)");
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/songlib/view/songlib.fxml"));
			VBox vbox = (VBox)loader.load();
			
			Controller controller = loader.getController();
			controller.setMainStage(mainStage);
			
			Scene scene = new Scene(vbox);
			mainStage.setScene(scene);
			mainStage.setResizable(false);
			mainStage.show();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}