package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import database.HotelDatabase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.AdminLogin;
import utility.CustomAlert;

public class LoginViewController implements Initializable{
	
	private final static BooleanProperty ACCESS_GRANTED = new SimpleBooleanProperty();
	// a global admin to use in this controller
	AdminLogin admin;
	String password;
    @FXML
    private Button cancleBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private PasswordField passwordTxt;

    @FXML
    private TextField userNameTxt;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
		
		admin = new AdminLogin();
		
		// Bind the value input in textFild with admin property
		admin.userNameProperty().bind(userNameTxt.textProperty());
		
		//populate user object's password from password field
		admin.passwordProperty().bind(passwordTxt.textProperty());
		
		//listener when the user types into the password field
		passwordTxt.textProperty().addListener((observable,oldValue,newValue)->{
			ACCESS_GRANTED.set(passwordTxt.getText().equals(password));
		});
	}
	
	// To login and go the admin ui
	 @FXML
	    private void loginBtnClick(ActionEvent event) throws IOException {
		 
		 Map<String, String> loginCredential = HotelDatabase.getAdminByUsername(admin.getUserName());
		 
		// Get the stage from the event
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		 
		if (loginCredential != null) {
			
			password = loginCredential.get(admin.getUserName());
			
			//listener when the user types into the password field
	        ACCESS_GRANTED.set(passwordTxt.getText().equals(password));
	
			// Only goes to next scence when input the correct password
			if(ACCESS_GRANTED.get()) {
				// Load the Login view
		        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AdminMainPage.fxml"));
			    Parent root = loader.load();
			    Scene sc = new Scene(root);
			   
			    window.setResizable(false);
			    window.setScene(sc);
			    window.show();
				
			}
			 else {
				 // Show an alert box when the password is incorrect
		        CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Wrong Password!!", "The password is not correct! please try again");
	
				}
		 }else {
			 	// Show an alert box when there is no username in the database
			    CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Invalid Username", "The username doesn't exist! Please try again");
			    return;
		 }
		        
	    }
	 
	 // To cancel and go back to main ui
	 @FXML
	    private void cancelBtnClick(ActionEvent event) throws IOException {
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

