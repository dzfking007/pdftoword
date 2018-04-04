package application;
	
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	public static Stage stage;
	public static Map<String,Scene> scenesMap = new HashMap<>();
	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = (VBox)FXMLLoader.load(getClass().getResource("PdfToWord.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("香蕉转换器");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("res/logo.jpg")));
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
