package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import database.HotelDatabase;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Address;
import models.Guest;
import models.Reservation;
import models.Room;
import utility.CustomAlert;

public class AdminGuestInfoController implements Initializable{
	
	List<String> title = new ArrayList<>();
	// Create instance of guest and address to use it later
	Guest guestObj;
	Address addressObj;
	Reservation rsvp;
	
	@FXML
    private TextField cityTxt;

    @FXML
    private Button clearBtn;

    @FXML
    private Button confirmBtn;

    @FXML
    private TextField emailTxt;

    @FXML
    private TextField lastNameTxt;

    @FXML
    private TextField nameTxt;

    @FXML
    private TextField phoneTxt;

    @FXML
    private TextField provinceTxt;

    @FXML
    private TextField streetTxt;

    @FXML
    private ComboBox<String> titleBox;

    @FXML
    private TextField zipCodeTxt;

	public void setGuestInfo(Reservation rsvpInfo) {
		rsvp = rsvpInfo;
		System.out.println(rsvp);
		
	}
	
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		rsvp = new Reservation();
		guestObj = new Guest();
		addressObj = new Address();
		
		// add value to combo box
		title.add("Mr");
		title.add("Mrs");
		title.add("Ms");
		title.add("Dr");
		title.add("Sir");
		title.add("Lady");
				
		titleBox.setItems(FXCollections.observableArrayList(title));
	}
	
	@FXML
    private void clearBtnClick(ActionEvent event) {
		
		Optional<ButtonType> result = CustomAlert.infoBox("Are you sure you want to clear all fields?", "Confirmation Dialog", null);
		if (result.get() == ButtonType.OK){
		    // User chose OK, clear all fields
		    titleBox.getSelectionModel().clearSelection();
		    nameTxt.clear();
		    lastNameTxt.clear();
		    phoneTxt.clear();
		    emailTxt.clear();
		    streetTxt.clear();
		    cityTxt.clear();
		    provinceTxt.clear();
		    zipCodeTxt.clear();
		    emailTxt.clear();
		}
	}
	
	@FXML
    void adminConfirm(ActionEvent event) throws IOException, SQLException {
    	
    	// Get the stage from the event
	    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
    	
    	
    	// Validate required fields
        if (nameTxt.getText().trim().isEmpty() || lastNameTxt.getText().trim().isEmpty() || emailTxt.getText().trim().isEmpty()) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Requried Information", "Name, Last Name, and Email are required fields.");
            return;
        }
        // Validate phone number format
        if (!phoneTxt.getText().trim().isEmpty() ) {
	        String phoneNumber = phoneTxt.getText().trim();
	        if (!phoneNumber.matches("\\d{3}-?\\d{3}-?\\d{4}")) {
	        	CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Invalid phone number", "Invalid phone number format.");
	        	return;
	        }
        }

        // Validate email format
        if (!emailTxt.getText().trim().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Invalid Email", "Invalid email format.");
            return;
        }
        
        if (!zipCodeTxt.getText().trim().isEmpty()) {
	        // Validate zip code format
	        String zipCode = zipCodeTxt.getText().trim();
	        if (!zipCode.matches("[A-Za-z]\\d[A-Za-z](\\s)?\\d[A-Za-z]\\d")) {
	            CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Invalid Postal Code", "Invalid postal code format.");
	            return;
	        }
        }
        
        
        // Check if street, city, province, and zip code fields are empty
        if (streetTxt.getText().trim().isEmpty() || cityTxt.getText().trim().isEmpty() || provinceTxt.getText().trim().isEmpty() || zipCodeTxt.getText().trim().isEmpty()) {
            addressObj = null;
        } else {
            setAddressRecord(addressObj);
        }
        
  
        
        setGuestRecord(guestObj, addressObj);
        
        // Insert record to database
        int guestId = HotelDatabase.insertGuestRecord(guestObj, addressObj);
        
        // add the id to the guestObj
        guestObj.setGuestId(guestId);
        
        setGuestRecord(guestObj, addressObj);
        setReservationRecord();
        
        // Insert rsvp record to database
        int rsvpId = HotelDatabase.insertReservationRecord(rsvp, guestId);
        
        rsvp.setBookingId(rsvpId);
        
        // Update rooms with the new rsvpId
        for (Room room : rsvp.getRoomList()) {
            HotelDatabase.updateRoomBookingId(room.getRoomId(), rsvpId);
        }
        
        // Load the booking detail view for admin  
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AdminBookingDetailView.fxml"));
        Parent root = loader.load();
        AdminBookingDetailViewController controller = loader.getController();
		// Set the value from the text field in the main scene
		controller.setBookingDetail(rsvp);
	    Scene sc = new Scene(root);
	    
	    window.setResizable(false);
	    window.setScene(sc);
	    window.show();
    }
	

	// set the value of reservation attributes
	private void setReservationRecord() {
		
		rsvp.setBookDate(LocalDate.now());
		rsvp.setGuest(guestObj);
		
	}
	
	private void setAddressRecord(Address address) {
		
		address.setStreet(streetTxt.getText().trim().toLowerCase());
		address.setCity(cityTxt.getText().trim().toLowerCase());
		address.setProvince(provinceTxt.getText().trim().toLowerCase());
		address.setZipCode(zipCodeTxt.getText().trim().toLowerCase());
	}
	

	private void setGuestRecord(Guest guest, Address address) {
		guest.setTitle(titleBox.getValue());
		guest.setFirstName(nameTxt.getText().trim().toLowerCase());
		guest.setLastName(lastNameTxt.getText().trim().toLowerCase());
		guest.setPhone(phoneTxt.getText().trim().toLowerCase());
		guest.setEmail(emailTxt.getText().trim().toLowerCase());
		guest.setAddress(address);
		
	}
	
}
