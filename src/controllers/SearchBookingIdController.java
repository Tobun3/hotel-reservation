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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Reservation;
import models.Room;
import utility.CustomAlert;
import utility.ReservationStatus;

public class SearchBookingIdController implements Initializable{
	
	ObservableList<Reservation> bookingList = FXCollections.observableArrayList();
	List<Reservation> rsvpList;

	@FXML
    private Button backBtn;

    @FXML
    private TextField idSearchTxt;

    @FXML
    private Button search;
    @FXML
    private TableView<Reservation> bookTable;

    @FXML
    private TableColumn<Reservation, Integer> bookingNumColumn;

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
    private TableColumn<Reservation, String> typeColumn;
    

    
    @FXML
    void BackBtnClick(ActionEvent event) throws IOException {
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

    @FXML
    void searchBtnClick(ActionEvent event) throws IOException, SQLException {
    	
    	// Get the stage from the event
    	Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
    	// Get the selected item
    	Reservation selectedRsvp = bookTable.getSelectionModel().getSelectedItem();
    	int idInt = 0;
  
    	if (selectedRsvp != null) {
    		// Search the database for the reservation
    		idInt = selectedRsvp.getBookingId();
            Reservation reservation = HotelDatabase.getReservationById(idInt);
            if (reservation != null) {
            	// Load the bill service
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BillServiceView.fxml"));
        	    Parent root = loader.load();
        	    Scene sc = new Scene(root);
        	    
        	    
        	    BillServiceViewController controller = loader.getController();
        	    
        	    // pass the value to adminGuestInfo scene
        	    controller.setReservation(reservation);
        	    
        	    window.setResizable(false);
        	    window.setScene(sc);
        	    window.show();
            
            
            }
            else {
            	CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Error!", "Reservation not found.");
            	return;
            }
        } else {
        	
            CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Error!", "Please select a reservation to proceed to Bill Service.");
        	return;
        }
    
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
				        	if ( rsvp.getRevStatus().equals(ReservationStatus.ACTIVE) || rsvp.getRevStatus().equals(ReservationStatus.CHECKEDOUT)) {
				        		activeRsvpList.add(rsvp);
				        	}
				        }
		            
		            bookingList = FXCollections.observableArrayList(activeRsvpList);
		            bookTable.setItems(bookingList);
		        } catch (SQLException e) {
		            e.printStackTrace();
		        }
		        
		        // Add a listener to the text property of the search field
		        idSearchTxt.textProperty().addListener((observable, oldValue, newValue) -> {
			        ObservableList<Reservation> matchingReservations;
			
			     // Check if the search text is empty
			        if (newValue.isEmpty()) {
			            // If it's empty, set the table items to the original bookingList
			            bookTable.setItems(bookingList);
			        } else {
			            // Try to parse the new text as an integer for a booking ID search
			            try {
			                int bookingId = Integer.parseInt(newValue);
			                matchingReservations = searchReservationByBookingId(bookingId);
			            } catch (NumberFormatException e) {
			                // If the new text is not a valid integer, perform a guest name search
			                matchingReservations = searchReservationByGuestEmail(newValue);
			            }

			            // Update the table view with the matching reservations
			            bookTable.setItems(matchingReservations);
			        }
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

		public ObservableList<Reservation> searchReservationByGuestEmail(String guestEmail) {
		    ObservableList<Reservation> matchingReservations = FXCollections.observableArrayList();
		 // Loop through the list and see if there any matching name
		    for (Reservation reservation : bookingList) {
		    	// to prevent the error when calling .toLowerCase() on null value
		    	if(reservation.getGuest().getEmail() != null){
			        if (reservation.getGuest().getEmail().toLowerCase().contains(guestEmail.toLowerCase())) {
			            matchingReservations.add(reservation);
			        }
			    }
		    }
		    return matchingReservations;
		}
		

}
