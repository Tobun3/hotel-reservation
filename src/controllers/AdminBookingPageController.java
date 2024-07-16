package controllers;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Address;
import models.Guest;
import models.Reservation;
import models.Room;
import utility.CustomAlert;

public class AdminBookingPageController implements Initializable{
	
	LocalDate checkinDate;
	LocalDate checkoutDate;
	IntegerProperty numGuest;
	IntegerProperty numRoom;
	ObservableList<Room> rooms = FXCollections.observableArrayList();
	ObservableList<Room> guestRooms = FXCollections.observableArrayList();
	// Create instance of guest and address to use it later
	Reservation rsvp = new Reservation();
	
	private int numSingleRooms = 0;
	private int numDoubleRooms = 0;
	private int numDeluxeRooms = 0;
	private int numPenthouseRooms = 0;
	
	private final int SINGLE_ROOM = 2;
	private final int DOUBLE_ROOM = 4;
	private final int DELUXE_ROOM = 5;
	private final int PENTHOUSE_ROOM = 6;
	
	public AdminBookingPageController() {
		this.checkinDate = LocalDate.now();
		this.checkoutDate = LocalDate.now();
		this.numGuest = new SimpleIntegerProperty(0); 
		this.numRoom = new SimpleIntegerProperty();
	}

	public IntegerProperty getNumRoomProperty() {
		return numRoom;
	}

	public void setNumRoom(int numRoom) {
		this.numRoom.set(numRoom);;
	}
	
	public int getNumroom() {
		return numRoom.get();
	}
	
	public IntegerProperty getNumGuestProperty() {
		return numGuest;
	}

	public void setNumGuest(int numGuest) {
		this.numGuest.set(numGuest);;
	}
	
	public int getNumGuest() {
		return numGuest.get();
	}


	@FXML
    private Button addBtn;
	
	@FXML
	private Button confirmBtn;

	 @FXML
    private TableColumn<Room, String> avPriceColumn;

    @FXML
    private TableColumn<Room, String> avRoomTypeColumn;

    @FXML
    private TableView<Room> availableTable;

    @FXML
    private Button backBtn;

    @FXML
    private DatePicker checkInDate;

    @FXML
    private DatePicker checkOutDate;

    @FXML
    private TableColumn<Room, String> gPriceColumn;

    @FXML
    private TableColumn<Room, String> gRoomTypeColumn;

    @FXML
    private TableView<Room> guestRoomTable;
    

    @FXML
    private Label guestRoomTxt;

    @FXML
    private TextField offerRateTxt;

    @FXML
    private Button removeBtn;

    @FXML
    private Label roomAvTxt;
    
    @FXML
    private Spinner<Integer> guestNum;
    
    @FXML
    private Label ruleTxt;

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
    
    @FXML
    private void clearBtnClick(ActionEvent event) {
		
		Optional<ButtonType> result = CustomAlert.infoBox("Are you sure you want to clear all fields?", "Confirmation Dialog", null);
		if (result.get() == ButtonType.OK){
		    // User chose OK, clear all fields
		    checkInDate.setValue(null);
		    checkOutDate.setValue(null);
		    guestNum.setValueFactory(null);
		    // move the rooms back to the available table
		    rooms.addAll(guestRooms);
	        guestRooms.clear();
		}
	}

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
    
    @FXML
    void confirmBtnClick(ActionEvent event) throws IOException, SQLException {
    	
    	// Get the stage from the event
    	Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
    	
    	// Check if both dates are picked
	    if (checkInDate.getValue() == null || checkOutDate.getValue() == null) {
	        // Show an alert box
	    	CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Missing dates", "Please select both check-in and check-out dates");
	        return;
	    }
	    
	    // validate the number of room
        setNumRoombyType();
        int totalGuestCapacity = numSingleRooms * SINGLE_ROOM + numDoubleRooms * DOUBLE_ROOM + numDeluxeRooms * DELUXE_ROOM + numPenthouseRooms * PENTHOUSE_ROOM;
        if (totalGuestCapacity < getNumGuest()) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, window, "More room required", getSuggestText() );
            return;
        }
	    
        setReservationRecord();
        
        
        URL url = getClass().getResource("/views/AdminGuestInfo.fxml");
        if (url == null) {
            System.out.println("Resource not found");
        } else {
        	FXMLLoader loader = new FXMLLoader(url);
    	    Parent root = loader.load();
    	    
    	    AdminGuestInfoController controller = loader.getController();
    	    
    	    // pass the value to adminGuestInfo scene
    	    System.out.println("In set revervation record" + rsvp.getRoomList());
    	    System.out.println("In set revervation record" + rsvp.getTotalPrice());
    	    controller.setGuestInfo(rsvp);
    	    Scene sc = new Scene(root);


    	    window.setResizable(false);
    	    window.setScene(sc);
    	    window.show();
        }
        
	    
    }


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		// for the spinner 
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30);
		
		
		guestNum.setValueFactory(valueFactory);
		
		setNumGuest(1);
		 // Disable dates before today for checkInDate
	    checkInDate.setDayCellFactory(getCheckInDayCellFactory());

	    // Disable dates before or the same as the selected check-in date for checkOutDate
	    checkOutDate.setDayCellFactory(getCheckOutDayCellFactory());
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
            
            guestRoomTxt.textProperty().bind(Bindings.createStringBinding(() -> 
            	guestRooms.size() + (guestRooms.size() > 1 ? " rooms" : " room"), 
            	guestRooms));
            
            // this will update our numGuest according to the spinner
            guestNum.valueProperty().addListener((obs, oldValue, newValue) -> {
                getNumGuestProperty().set(newValue);
      
                // the rule text will change accordingly to the numGuest
                ruleTxt.setText(getSuggestText());
            });
            
            getNumRoomProperty().bind(Bindings.size(guestRooms));
        

        } catch (SQLException e) {
            e.printStackTrace();
        }	
	}
	
	// A callback to the daCellFactory property to the DatePicker
		 // this callback allow us to customize the date cells in DatePicker
		 // Disable the date cell that come before the current date
		 private Callback<DatePicker, DateCell> getCheckInDayCellFactory() {
			    return new Callback<DatePicker, DateCell>() {
			        @Override
			        public DateCell call(final DatePicker datePicker) {
			            return new DateCell() {
			                @Override
			                public void updateItem(LocalDate item, boolean empty) {
			                    super.updateItem(item, empty);
			                    
			                    // Disable all dates before today
			                    if (item.compareTo(LocalDate.now()) < 0) {
			                        setDisable(true);
			                    }
			                }
			            };
			        }
			    };
			}
		 
		 // Disable the date that come before the check in date
		 private Callback<DatePicker, DateCell> getCheckOutDayCellFactory() {
			    return new Callback<DatePicker, DateCell>() {
			        @Override
			        public DateCell call(final DatePicker datePicker) {
			            return new DateCell() {
			                @Override
			                public void updateItem(LocalDate item, boolean empty) {
			                    super.updateItem(item, empty);
			                    
			                    // Disable all dates before or the same as the selected check-in date
			                    if (checkInDate.getValue() != null && item.compareTo(checkInDate.getValue()) <= 0) {
			                        setDisable(true);
			                    }
			                }
			            };
			        }
			    };
			}
		 
		// set the value of reservation attributes
		private void setReservationRecord() {
			
			rsvp.setBookDate(LocalDate.now());
			rsvp.setCheckInDate(checkInDate.getValue());
			rsvp.setCheckOutDate(checkOutDate.getValue());
			rsvp.setNumGuest(getNumGuest());
			rsvp.setRoomCount(getNumroom());
			rsvp.setRoomList(guestRooms);
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

					if (getNumGuest() <= SINGLE_ROOM) {
						suggestTxt = "1 single room";
					} else if (getNumGuest() <= DOUBLE_ROOM) {
						suggestTxt = "1 double room or 2 single rooms";
					} else if (getNumGuest() <= DELUXE_ROOM) {
						suggestTxt = "1 deluxe room or " + (int) Math.ceil((double) getNumGuest() / SINGLE_ROOM) + " single rooms or 1 single room and 1 double room";
					} else if (getNumGuest() <= PENTHOUSE_ROOM) {
						suggestTxt = "1 penthouse room or " + (int) Math.ceil((double) getNumGuest() / SINGLE_ROOM) + " single rooms or " + (int) Math.ceil((double) getNumGuest() / DOUBLE_ROOM) + " double rooms or 1 single room and 1 deluxe room";
					} else {
					    int singleRoomsNeeded = (int) Math.ceil((double) getNumGuest() / SINGLE_ROOM);
					    int doubleRoomsNeeded = (int) Math.ceil((double) getNumGuest() / DOUBLE_ROOM);
					    int deluxeRoomsNeeded = (int) Math.ceil((double) getNumGuest() / DELUXE_ROOM);
					    int penthouseRoomsNeeded = (int) Math.ceil((double) getNumGuest() / PENTHOUSE_ROOM);
					    suggestTxt = singleRoomsNeeded + " single rooms or " + doubleRoomsNeeded + " double rooms or " + deluxeRoomsNeeded + " deluxe rooms or " + penthouseRoomsNeeded + " penthouse rooms";
					}
					
					suggestTxt = "** For " + getNumGuest() + ( getNumGuest() > 1 ? " guests" : " guest") + ", " + suggestTxt + " required.";
					
					return suggestTxt;
		}

}
