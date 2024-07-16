package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import database.HotelDatabase;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Address;
import models.Guest;
import models.Reservation;
import models.Room;
import utility.CustomAlert;
import utility.DateUtility;

public class GuestBookingPageController implements Initializable{
	LocalDate checkinDate;
	LocalDate checkoutDate;
	int numGuest;
	IntegerProperty numRoom;
	List<String> title = new ArrayList<>();
	ObservableList<Room> rooms = FXCollections.observableArrayList();
	ObservableList<Room> guestRooms = FXCollections.observableArrayList();
	// Create instance of guest and address to use it later
	Guest guestObj = new Guest();
	Address addressObj = new Address();
	Reservation rsvp = new Reservation();
	
	private int numSingleRooms = 0;
	private int numDoubleRooms = 0;
	private int numDeluxeRooms = 0;
	private int numPenthouseRooms = 0;
	
	private final int SINGLE_ROOM = 2;
	private final int DOUBLE_ROOM = 4;
	private final int DELUXE_ROOM = 5;
	private final int PENTHOUSE_ROOM = 6;
	    			
	
	public GuestBookingPageController() {
		this.checkinDate = LocalDate.now();
		this.checkoutDate = LocalDate.now();
		this.numGuest = 0;	
		this.numRoom = new SimpleIntegerProperty();
	}

	public IntegerProperty getNumRoomProperty() {
		return numRoom;
	}

	public void setNumRoom(IntegerProperty numRoom) {
		this.numRoom = numRoom;
	}
	
	public int getNumroom() {
		return numRoom.get();
	}

	@FXML
    private Button addBtn;

    @FXML
    private TableColumn<Room, String> avPriceColumn;

    @FXML
    private TableColumn<Room, String> avRoomTypeColumn;

    @FXML
    private TableView<Room> availableTable;

    @FXML
    private Text checkInDateTxt;

    @FXML
    private TextField cityTxt;

    @FXML
    private Button clearBtn;

    @FXML
    private Button comfirmBtn;

    @FXML
    private TextField emailTxt;
    
    @FXML
    private Label roomAvTxt;

    @FXML
    private TableColumn<Room, String> gPriceColumn;

    @FXML
    private TableColumn<Room, String> gRoomTypeColumn;

    @FXML
    private TableView<Room> guestRoomTable;

    @FXML
    private TextField lastNameTxt;

    @FXML
    private TextField nameTxt;

    @FXML
    private Text numGuestTxt;

    @FXML
    private Text numRoomTxt;

    @FXML
    private Text numStayTxt;

    @FXML
    private TextField phoneTxt;

    @FXML
    private TextField provinceTxt;

    @FXML
    private Button removeBtn;

    @FXML
    private TextField streetTxt;

    @FXML
    private TextField titleTxt;

    @FXML
    private TextField zipCodeTxt;
    
    @FXML
    private Label ruleTxt;
    
    @FXML
    private ComboBox<String> titleBox;

    @FXML
    void comfirmBtnClick(ActionEvent event) throws IOException, SQLException {
    	
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
        

        // validate the number of room
        setNumRoombyType();
        int totalGuestCapacity = numSingleRooms * SINGLE_ROOM + numDoubleRooms * DOUBLE_ROOM + numDeluxeRooms * DELUXE_ROOM + numPenthouseRooms * PENTHOUSE_ROOM;
        if (totalGuestCapacity < numGuest) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, window, "More room required", getSuggestText() );
            return;
        }
        
        setGuestRecord(guestObj, addressObj);
        
        // Insert record to database
        int guestId = HotelDatabase.insertGuestRecord(guestObj, addressObj);
        
        // add the id to the guestObj
        guestObj.setGuestId(guestId);
        
        setReservationRecord(rsvp);
        
        // Insert rsvp record to databas
        int rsvpId = HotelDatabase.insertReservationRecord(rsvp, guestId);
        
        rsvp.setBookingId(rsvpId);
        
        // Update rooms with the new rsvpId
        for (Room room : guestRooms) {
            HotelDatabase.updateRoomBookingId(room.getRoomId(), rsvpId);
        }
        
        // Load the Login view  
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GuestBookingDetailView.fxml"));
        Parent root = loader.load();
        GuestBookingDetailViewController controller = loader.getController();
		// Set the value from the text field in the main scene
		controller.setBookingDetail(rsvp, this.getTotalPrice());
	    Scene sc = new Scene(root);
	    
	    window.setResizable(false);
	    window.setScene(sc);
	    window.show();
    }

    // set the value of reservation attributes
	private void setReservationRecord(Reservation rsvp) {
		
		rsvp.setBookDate(LocalDate.now());
		rsvp.setCheckInDate(checkinDate);
		rsvp.setCheckOutDate(checkoutDate);
		rsvp.setGuest(guestObj);
		rsvp.setNumGuest(numGuest);
		rsvp.setRoomCount(getNumroom());
		rsvp.setRoomList(guestRooms);
	}

	public void setSearchDetail( LocalDate checkIn, LocalDate checkOut, int numGuest) {
		
		this.checkinDate = checkIn;
		this.checkoutDate = checkOut;
		this.numGuest = numGuest;
		
		String guest = numGuest > 1? " guests" : " guest";
				
		// set the number of night text
		numGuestTxt.setText(numGuest + " " + guest);
		// set the check in date text
		checkInDateTxt.setText(DateUtility.format(checkIn));
		// calculate and set the text for number of night the guest will stay
		int dateBetween = (int) ChronoUnit.DAYS.between(checkinDate, checkoutDate);
		numStayTxt.setText(dateBetween + (dateBetween > 1? " nights" : " night"));
		// set the number to be zero when first enter the page
		numRoomTxt.setText(0 + " Room");
		
		
		// set the rule base on number of guest
		ruleTxt.setText(getSuggestText());
		
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
	
	// Add a room to the guest room table
	 @FXML
	 void addButtonClick(ActionEvent event) throws IOException {
    	Room selectedRoom = availableTable.getSelectionModel().getSelectedItem();
    	//int selectedIndex = availableTable.getSelectionModel().getSelectedIndex();
    	if (selectedRoom != null) {

            // Add the selected part to the associated parts
            guestRooms.add(selectedRoom);
            guestRoomTable.setItems(guestRooms);

            // Refresh the allPartTable to reflect the changes
            rooms.remove(selectedRoom);
            availableTable.refresh();
            availableTable.setItems(rooms);
            
        }
    	
    }
	 
	 // Remove the room from guest room list
	 @FXML
	 void removeButtonClick(ActionEvent event) throws IOException {
    	Room selectedRoom = guestRoomTable.getSelectionModel().getSelectedItem();
    	//int selectedIndex = guestRoomTable.getSelectionModel().getSelectedIndex();
    	if (selectedRoom != null) {

            // Add the selected part to the associated parts
            guestRooms.remove(selectedRoom);
            guestRoomTable.setItems(guestRooms);

            // Refresh the allPartTable to reflect the changes
            rooms.add(selectedRoom);
            availableTable.refresh();
            availableTable.setItems(rooms);
            
        }
    	
    }
	 

	// To go back to the welcome page
    @FXML
    private void backBtnClick(ActionEvent event) throws IOException {
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// add value to combo box
		title.add("Mr");
		title.add("Mrs");
		title.add("Ms");
		title.add("Dr");
		title.add("Sir");
		title.add("Lady");
		
		titleBox.setItems(FXCollections.observableArrayList(title));
		
		// generate tableview
		// Set the value in each column
		// For Part
		avRoomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		avPriceColumn.setCellValueFactory(new PropertyValueFactory<>("rate"));
        gRoomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        gPriceColumn.setCellValueFactory(new PropertyValueFactory<>("rate"));
        
        getNumRoomProperty().bind(Bindings.size(guestRooms));
        
        try {
            List<Room> roomList = HotelDatabase.getAvailableRooms();
            rooms = FXCollections.observableArrayList(roomList);
            availableTable.setItems(rooms);	
            
            // set the number of available rooms
            StringProperty availableRoomsText = new SimpleStringProperty();
            availableRoomsText.bind(Bindings.createStringBinding(() -> 
                "Number of Available : " + (HotelDatabase.getAvailableRoomCount() - guestRooms.size()) + " rooms", 
                guestRooms));

            roomAvTxt.textProperty().bind(availableRoomsText);
        } catch (SQLException e) {
            e.printStackTrace();
        }      
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
	
	// Get the total price for the reservation
	public double getTotalPrice() {
		int dateBetween = (int) ChronoUnit.DAYS.between(checkinDate, checkoutDate);
	    double totalPrice = 0.0;
	    for (Room room : guestRooms) {
	        totalPrice += room.getRate();
	    }
	    totalPrice *= dateBetween;
	    return totalPrice;
	}
	
	public void setNumRoombyType() {
		for (Room room : guestRooms) {
		    switch (room.getType()) {
		        case SINGLE:
		            numSingleRooms++;
		            break;
		        case DOUBLE:
		            numDoubleRooms++;
		            break;
		        case DELUXE:
		            numDeluxeRooms++;
		            break;
		        case PENTHOUSE:
		            numPenthouseRooms++;
		            break;
		    }
		}
	}
	
	public String getSuggestText() {
		// To give suggestion
				String suggestTxt = "";

				if (numGuest <= SINGLE_ROOM) {
					suggestTxt = "1 single room";
				} else if (numGuest <= DOUBLE_ROOM) {
					suggestTxt = "1 double room or 2 single rooms";
				} else if (numGuest <= DELUXE_ROOM) {
					suggestTxt = "1 deluxe room or " + (int) Math.ceil((double) numGuest / SINGLE_ROOM) + " single rooms or 1 single room and 1 double room";
				} else if (numGuest <= PENTHOUSE_ROOM) {
					suggestTxt = "1 penthouse room or " + (int) Math.ceil((double) numGuest / SINGLE_ROOM) + " single rooms or " + (int) Math.ceil((double) numGuest / DOUBLE_ROOM) + " double rooms or 1 single room and 1 deluxe room";
				} else {
				    int singleRoomsNeeded = (int) Math.ceil((double) numGuest / SINGLE_ROOM);
				    int doubleRoomsNeeded = (int) Math.ceil((double) numGuest / DOUBLE_ROOM);
				    int deluxeRoomsNeeded = (int) Math.ceil((double) numGuest / DELUXE_ROOM);
				    int penthouseRoomsNeeded = (int) Math.ceil((double) numGuest / PENTHOUSE_ROOM);
				    suggestTxt = singleRoomsNeeded + " single rooms or " + doubleRoomsNeeded + " double rooms or " + deluxeRoomsNeeded + " deluxe rooms or " + penthouseRoomsNeeded + " penthouse rooms";
				}

				suggestTxt = "** For " + numGuest + ( numGuest > 1 ? " guests" : " guest") + ", " + suggestTxt + " required.";
				
				return suggestTxt;
	}

}
