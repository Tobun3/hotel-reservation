package controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminMainPageController {

    @FXML
    private Button availableRoomBtn;

    @FXML
    private Button billBtn;

    @FXML
    private Button bookRoomBtn;

    @FXML
    private Button currentBookingBtn;

    @FXML
    private Button logOutbtn;
    
 // To go back to the welcome page
    @FXML
    private void bookRoomBtnClick(ActionEvent event) throws IOException {
        // Load the Login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AdminBookingPage.fxml"));
	    Parent root = loader.load();
	    Scene sc = new Scene(root);


	 // Get the stage from the event
	    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
	    
	    window.setResizable(false);
	    window.setScene(sc);
	    window.show();
    }
    
    // To go back to the welcome page
    @FXML
    private void billBtnClick(ActionEvent event) throws IOException {
        // Load the Login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FindBookingId.fxml"));
	    Parent root = loader.load();
	    Scene sc = new Scene(root);


	    // Get the stage from the event
	    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
	    
	    window.setResizable(false);
	    window.setScene(sc);
	    window.show();
    }
    
    // To go back to the welcome page
    @FXML
    private void currentBookingBtnClick(ActionEvent event) throws IOException {
        // Load the Login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CurrentBookingView.fxml"));
	    Parent root = loader.load();
	    Scene sc = new Scene(root);


	 // Get the stage from the event
	    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
	    
	    window.setResizable(false);
	    window.setScene(sc);
	    window.show();
    }
    
    // To go back to the welcome page
    @FXML
    private void availableRoomBtnClick(ActionEvent event) throws IOException {
        // Load the Login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RoomAvailableView.fxml"));
	    Parent root = loader.load();
	    Scene sc = new Scene(root);


	    // Get the stage from the event
	    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
	    
	    window.setResizable(false);
	    window.setScene(sc);
	    window.show();
    }
    
    // To go back to the welcome page
    @FXML
    private void logoutBtnClick(ActionEvent event) throws IOException {
        // Load the Login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Main.fxml"));
	    Parent root = loader.load();
	    Scene sc = new Scene(root);


	 // Get the stage from the event
	    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
	    
	    window.setResizable(false);
	    window.setScene(sc);
	    window.show();
    }

}

