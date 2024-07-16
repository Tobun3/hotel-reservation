package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import database.HotelDatabase;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Bill;
import models.Reservation;
import models.Room;
import utility.CustomAlert;

public class BillServiceViewController implements Initializable {
	
	private final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance();
	private Reservation rsvp;
	private double originalTotalAmount;
	private Bill bill;
	private double total = 0;
	private double discount = 0;
	ObservableList<Room> bookedRooms = FXCollections.observableArrayList();
	@FXML
    private Button backBtn;
	
	@FXML
    private Button bookingMenuBtn;

    @FXML
    private TextField bookingIdTxt;

	@FXML
    private TextField discountTxt;

    @FXML
    private TableColumn<Room, Integer> idColumn;

    @FXML
    private TextField nameTxt;

    @FXML
    private TextField numRoomTxt;

    @FXML
    private Button payBtn;
    
    @FXML
    private Text settleTxt;

    @FXML
    private TableColumn<Room, Double> priceColumn;

    @FXML
    private TableView<Room> roomTable;

    @FXML
    private TextField totalTxt;

    @FXML
    private TableColumn<Room, String> typeColumn;
    
    @FXML
    private TextField durationTxt;
    
    @FXML
    private Button applyDiscountBtn;
    
    @FXML
    void applyDiscountClick(ActionEvent event) {
    	
    	// Get the stage from the event
	    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
    	// Get the discount value
        String discountText = discountTxt.getText();

        // Check if the discount text is a number
        try {
            discount = Double.parseDouble(discountText);
        } catch (NumberFormatException e) {
        	CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Error!", "Invalid discount - Should be number only.");
            return;
        }

        // Check if the discount is over 25%
        if (discount < 0 || discount > 25) {
        	CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Error!", "Discount should be between 1% - 25% only");
            return;
        }

        // Calculate the total based on the original total and the discount
        total = originalTotalAmount * (1 - discount / 100);

        // Set the total text
        totalTxt.setText(CURRENCY.format(total));
        
        
    }
    

    @FXML
    void backBtnClick(ActionEvent event) throws IOException {
    	// Load the Admin main page
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
		rsvp = new Reservation();
		bill = new Bill();
		// Set the column value
		idColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("rate"));
        
  
        List<Room> roomList = new ArrayList();
        bookingIdTxt.setDisable(true);
        nameTxt.setDisable(true);
        numRoomTxt.setDisable(true);
        durationTxt.setDisable(true);
        applyDiscountBtn.setDisable(true);
        
        // add listener to see any changes
        discountTxt.textProperty().addListener((observable, oldValue, newValue)->{
        	applyDiscountBtn.setDisable(newValue.trim().isEmpty());
        });
       
	}
	
	public void setReservation(Reservation rev) throws SQLException {
		this.rsvp = rev;
		String name = "";
		
		// Set the value of the table in bill view
		// because in database doesn't contain a room list so we query the room in database directly
		rsvp.setRoomList(HotelDatabase.getRoomByBookingId(rsvp.getBookingId()));
	    bookedRooms = FXCollections.observableArrayList(rsvp.getRoomList());
	    roomTable.setItems(bookedRooms);
	    
	    bookingIdTxt.setText(String.valueOf(rsvp.getBookingId()));
	    if (rsvp.getGuest().getTitle() != null) {
    	    name += rsvp.getGuest().getTitle() + " ";
    	}
	    
	    numRoomTxt.setText(String.valueOf(rsvp.getRoomCount()));

    	name += rsvp.getGuest().getFirstName() + " " + rsvp.getGuest().getLastName();

    	nameTxt.setText(name);
    	
    	originalTotalAmount = rsvp.getTotalPrice();
    	
    	// set the number of night the guest staying
    	int dateBetween = (int) ChronoUnit.DAYS.between(rsvp.getCheckInDate(), rsvp.getCheckOutDate());
    	durationTxt.setText(dateBetween + (dateBetween > 1? " nights" : " night"));
    	
    	totalTxt.setText(CURRENCY.format(originalTotalAmount));
    	
    	// If the bill is already settle it should tell the user
        // and also disable the pay button to prevent from creating another bill
    	if (rsvp.getBill() != null ) {
    	    payBtn.setDisable(true);
    	    settleTxt.setVisible(true); 
    	    totalTxt.setDisable(true);
    	    discountTxt.setDisable(true);
    	    // set the value of discount and text field with bill data
    	    discountTxt.setText(String.valueOf(rsvp.getBill().getDiscount()));
    	    totalTxt.setText(CURRENCY.format(rsvp.getBill().getAmountToPay()));
    	} else {
    	    settleTxt.setVisible(false);
    	}
	}
	
	 @FXML
    void payBtnClick(ActionEvent event) throws SQLException {
	 	// set the bill attribute
	 	bill.setAmountToPay(total > 0 ? total : originalTotalAmount);
	 	bill.setPayDate(LocalDate.now());
	 	bill.setDiscount(discount);
	 	bill.setIsSettle(true);
	 	
	 	// Put the bill into database
	 	HotelDatabase.insertBillRecord(bill, rsvp.getBookingId());
	 	Optional<ButtonType> result = CustomAlert.infoBox("The Bill has been processed successfully!", "Payment Success", null);
		if (result.get() == ButtonType.OK){
			payBtn.setDisable(true);
    	    settleTxt.setVisible(true); 
    	    totalTxt.setDisable(true);
    	    discountTxt.setDisable(true);
		}
    }
	 
	 @FXML
    void bookingMenuClick(ActionEvent event) throws IOException {
		 
		// Load the Booking menu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CurrentBookingView.fxml"));
	    Parent root = loader.load();
	    Scene sc = new Scene(root);


	    // Get the stage from the event
	    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
	    
	    window.setResizable(false);
	    window.setScene(sc);
	    window.show();

    }

}
