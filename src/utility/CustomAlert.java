package utility;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;

public class CustomAlert{
	// For alert box such as error
	 public static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
	        Alert alert = new Alert(alertType);
	        alert.setTitle(title);
	        alert.setHeaderText(null);
	        alert.setContentText(message);
	        alert.initOwner(owner);
	        alert.showAndWait();
	        
	        DialogPane dialogPane = alert.getDialogPane();
	        dialogPane.getStylesheets().add(CustomAlert.class.getResource("/cssStyle/application.css").toExternalForm());
	    }
	 
	 // For information box
	 public static Optional<ButtonType> infoBox(String infoMessage, String title, String headerText) {
		    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		    alert.setTitle(title);
		    alert.setHeaderText(headerText);
		    alert.setContentText(infoMessage);
		    return alert.showAndWait();
		}
}
