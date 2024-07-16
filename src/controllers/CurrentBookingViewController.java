package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import database.HotelDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Bill;
import models.Reservation;
import models.Room;
import utility.CustomAlert;
import utility.ReservationStatus;

public class CurrentBookingViewController implements Initializable{
	
	ObservableList<Reservation> bookingList = FXCollections.observableArrayList();
	List<Reservation> rsvpList;
	
	
	@FXML
    private Button backBtn;
	
	@FXML
    private TableView<Reservation> bookTable;

    @FXML
    private TableColumn<Reservation, Integer> bookingNumColumn;

    @FXML
    private Button cancelBookingBtn;

    @FXML
    private Button checkOutBtn;

    @FXML
    private TableColumn<Reservation, String> nameColumn;

    @FXML
    private TableColumn<Reservation, LocalDate> checkInColumn;
    
    @FXML
    private TableColumn<Reservation, LocalDate> checkOutColumn;

    @FXML
    private TableColumn<Reservation, Integer> numRoomColumn;
    
    @FXML
    private TableColumn<Reservation, ReservationStatus> statusColumn;

    @FXML
    private TextField searchTxt;

    @FXML
    private TableColumn<Reservation, String> typeColumn;

    @FXML
    void backBtnClick(ActionEvent event) throws IOException {
		// Load the Login view
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AdminMainPage.fxml"));
	    Parent root = loader.load();
	    Scene sc = new Scene(root);
	
	
	    // Get the stage from the event
	    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
	    
	    window.setResizable(false);
	    window.setScene(sc);
	    window.show();
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Set the column value
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("guest"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("roomList"));
        numRoomColumn.setCellValueFactory(new PropertyValueFactory<>("roomCount"));
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        bookingNumColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("revStatus"));
        
        // for room type because it is a list of room so we need to use custom cell factory
        typeColumn.setCellValueFactory(cellData -> {
            List<Room> rooms;
			try {
				rooms = cellData.getValue().getRoomList();
				
				if (rooms != null) {
					return new SimpleStringProperty(rooms.stream()
							.map(room -> room.getType().toString())			
							.collect(Collectors.joining(", ")));
				} else {
					return new SimpleStringProperty("");
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
            
			return new SimpleStringProperty("");
            
        });
        //  To get sub class to display as we want
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGuest().getFirstName()));
        
        try {
        		// get all rev
	            rsvpList = HotelDatabase.getAllReservations();
	            // get only active rev
	            List<Reservation> activeRsvpList = new ArrayList<>();
	            // by default we show the active booking
		        for ( Reservation rsvp : rsvpList) {
		        	if ( rsvp.getRevStatus().equals(ReservationStatus.ACTIVE)) {
		        		activeRsvpList.add(rsvp);
		        	}
		        }
            
            bookingList = FXCollections.observableArrayList(activeRsvpList);
            bookTable.setItems(bookingList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
		   
     // Add a listener to the text property of the search field
        searchTxt.textProperty().addListener((observable, oldValue, newValue) -> {
	        ObservableList<Reservation> matchingReservations;
	
	        // Try to parse the new text as an integer for a booking ID search
	        try {
	            int bookingId = Integer.parseInt(newValue);
	            matchingReservations = searchReservationByBookingId(bookingId);
	        } catch (NumberFormatException e) {
	            // If the new text is not a valid integer, perform a guest name search
	            matchingReservations = searchReservationByGuestName(newValue);
	        }
	
	        // Update the table view with the matching reservations
	        bookTable.setItems(matchingReservations);
        });
		
	}
	
	
	// For search functionality
	public ObservableList<Reservation> searchReservationByBookingId(int bookingId) {
	    ObservableList<Reservation> matchingReservations = FXCollections.observableArrayList();
	    // Loop through the list and see if there any matching id
	    for (Reservation reservation : bookingList) {
	        if (reservation.getBookingId() == bookingId) {
	            matchingReservations.add(reservation);
	        }
	    }
	    return matchingReservations;
	}

	public ObservableList<Reservation> searchReservationByGuestName(String guestName) {
	    ObservableList<Reservation> matchingReservations = FXCollections.observableArrayList();
	 // Loop through the list and see if there any matching name
	    for (Reservation reservation : bookingList) {
	        if (reservation.getGuest().getFirstName().toLowerCase().contains(guestName.toLowerCase())) {
	            matchingReservations.add(reservation);
	        }
	    }
	    return matchingReservations;
	}
	
	@FXML
    void cancelBtnClick(ActionEvent event) throws SQLException {
		
		 // Get the stage from the event
	    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		
		// get selected booking
		Reservation selectedRsvp = bookTable.getSelectionModel().getSelectedItem();
		if (selectedRsvp != null) {
            // Change the status of the reservation to CANCELLED
			// Can change it only when it is not a checked out status
			if ( !selectedRsvp.getRevStatus().equals(ReservationStatus.CHECKEDOUT) ) {
				HotelDatabase.updateReservationStatus(selectedRsvp.getBookingId(), ReservationStatus.CANCELLED.name()); 
				 selectedRsvp.setRevStatus(ReservationStatus.CANCELLED);
				 
				 // remove it from the list
				 bookingList.remove(selectedRsvp);
				// reload the table to update
	            bookTable.refresh();
	            
	            // Change the status of the room in database
	            // change the status of room and disconnect the room with the booking id
	            List<Room> rsvpRoomList = selectedRsvp.getRoomList();
	            
	            for(Room room : rsvpRoomList) {
	            	HotelDatabase.updateRoomStatus(room.getRoomId());
	            }
	            
	            CustomAlert.showAlert(AlertType.CONFIRMATION, window, "Cancelation Complete", "The booking has been cancelled.");
			}else {
		
				CustomAlert.showAlert(AlertType.ERROR, window, "Error!", "Can't cancel the booking that has been checked out!!");
			}
        }else {
        	CustomAlert.showAlert(AlertType.ERROR, window, "Error!", "No Reservation has been selected");
        }
    }

    @FXML
    void checkoutBtnClick(ActionEvent event) throws SQLException, IOException {
  
    	 // Get the stage from the event
	    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
	    
    	Reservation selectedRsvp = bookTable.getSelectionModel().getSelectedItem();
    	
    	// only it is active then we can check out
    	if(selectedRsvp.getRevStatus().equals(ReservationStatus.ACTIVE)) {
    		// check the bill status 
    		Bill bill = selectedRsvp.getBill();
    	    if(bill != null) {
    	        
    	        if(bill.getIsSettle()) {
    	            // if it settle already just change the rsvp status to checked out
    	            selectedRsvp.setRevStatus(ReservationStatus.CHECKEDOUT);
    	            // update it in the database
    	            HotelDatabase.updateReservationStatus(selectedRsvp.getBookingId(), selectedRsvp.getRevStatus().name());
    	            
	    	         // remove it from the list
	   				 bookingList.remove(selectedRsvp);
    	            // reload the table to update
    	            bookTable.refresh();
    	            
    	            // change the status of room and disconnect the room with the booking id
    	            List<Room> rsvpRoomList = selectedRsvp.getRoomList();
    	            
    	            for(Room room : rsvpRoomList) {
    	            	HotelDatabase.updateRoomStatus(room.getRoomId());
    	            }
    	            
    	            
    	            CustomAlert.infoBox("Check out succuessfully", "Check Out Completed", null);
    	        } else {
    	            // go to bill service page
    	            // Load the bill service
    	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BillServiceView.fxml"));
    	            Parent root = loader.load();
    	            Scene sc = new Scene(root);
    	            
    	            BillServiceViewController controller = loader.getController();
    	            
    	            // pass the value to adminGuestInfo scene
    	            controller.setReservation(selectedRsvp);
    	            
    	            window.setResizable(false);
    	            window.setScene(sc);
    	            window.show();
    	        }
    	    }else {
    	    	 // go to bill service page
	            // Load the bill service
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BillServiceView.fxml"));
	            Parent root = loader.load();
	            Scene sc = new Scene(root);
	            
	            BillServiceViewController controller = loader.getController();
	            
	            // pass the value to adminGuestInfo scene
	            controller.setReservation(selectedRsvp);
	            
	            window.setResizable(false);
	            window.setScene(sc);
	            window.show();
    	    }
    	}else if(selectedRsvp.getRevStatus().equals(ReservationStatus.CHECKEDOUT)) {
    		CustomAlert.infoBox("This reservation has already been checked out!", "Check Out Completed", null);
    	}else {
    		CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Error!", "Cannot check out the cancelled booking!");
    	}
    }

}
