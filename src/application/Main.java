//progress circle and thread broken // add choice not centered
//items don't resize
//set save-able preferences for default start roll roof
//add load shuffle positions

package application;
	
import application.controller.MainController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	private int defaultRoll = 9999;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader rootLoader = new FXMLLoader();
			rootLoader.setLocation(Main.class.getResource("view/MainView.fxml"));
			VBox root = (VBox) rootLoader.load();
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css")
					.toExternalForm());
			primaryStage.setScene(scene);
			
			MainController controller = rootLoader.getController();
			controller.init(primaryStage,defaultRoll);
			
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
