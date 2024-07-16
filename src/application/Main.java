package application;
	
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Main extends Application {
	private Socket socket;
	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("/views/Main.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/cssStyle/application.css").toExternalForm());
			Image icon = new Image("logo.png");
			primaryStage.setScene(scene);
			primaryStage.setTitle("Sukhothai Hotel");
			primaryStage.getIcons().add(icon);
			primaryStage.show();
			
			// Start the thread
			// Set the socket to use the same port as the server
			socket = new Socket("localhost", 8000);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// Implement the run function
	public void run() {
		System.out.println("Connect to the server");
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
