package controllers;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Reservation;

public class GuestBookingDetailViewController implements Initializable{

	@FXML
    private Button backBtn;

    @FXML
    private TextField bookingNoTxt;

    @FXML
    private TextField nameTxt;

    @FXML
    private TextField numGuestTxt;

    @FXML
    private TextField numRoomTxt;

    @FXML
    private TextField numStayTxt;

    @FXML
    private TextField totalTxt;

    @FXML
    void BackBtnClick(ActionEvent event) throws IOException {
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
    
    void setBookingDetail(Reservation rsvpObj, double total) {
    	
    	bookingNoTxt.setText(String.valueOf(rsvpObj.getBookingId()));
    	
    	
    	String name = "";

    	if (rsvpObj.getGuest().getTitle() != null) {
    	    name += rsvpObj.getGuest().getTitle() + " ";
    	}

    	name += rsvpObj.getGuest().getFirstName() + " " + rsvpObj.getGuest().getLastName();

    	nameTxt.setText(name);
    	numGuestTxt.setText(String.valueOf(rsvpObj.getNumGuest()) + " " + (rsvpObj.getNumGuest() > 1 ? "guest" : "guests"));
    	numRoomTxt.setText(String.valueOf(rsvpObj.getRoomCount()) + " " + (rsvpObj.getRoomCount() > 1 ? "rooms" : "room"));
    	
    	int dateBetween = (int) ChronoUnit.DAYS.between(rsvpObj.getCheckInDate(), rsvpObj.getCheckOutDate());
		numStayTxt.setText(dateBetween + (dateBetween > 1? " nights" : " night"));
		
		NumberFormat CURRENCY = NumberFormat.getCurrencyInstance();
		// Set the text to show Paid amount and total amount
		totalTxt.setText( CURRENCY.format(total));
    	
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		// set all the text field to be disable
		bookingNoTxt.setDisable(true);
		nameTxt.setDisable(true);
		numGuestTxt.setDisable(true);
		numRoomTxt.setDisable(true);
		totalTxt.setDisable(true);
		numStayTxt.setDisable(true);
		
	}

}
