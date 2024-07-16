package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import database.HotelDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import utility.CustomAlert;

public class MainController implements Initializable{
	

    @FXML
    private Spinner<Integer> guestNum;

    @FXML
    private DatePicker checkInDate;

    @FXML
    private DatePicker checkOutDate;

    @FXML
    private Button searchBtn;
    
    @FXML
    private MenuItem loginMenuItem;
    
    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        // Load the Login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
	    Parent root = loader.load();
	    Scene sc = new Scene(root);


	    // Get the stage from other node i.e, searchBtn this way we use the same instance of the stage
	    Stage window = (Stage) searchBtn.getScene().getWindow();
	    
	    window.setResizable(false);
	    window.setScene(sc);
	    window.show();
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// for the spinner 
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30);
		
		valueFactory.setValue(1);
		
		guestNum.setValueFactory(valueFactory);
		
		 // Disable dates before today for checkInDate
	    checkInDate.setDayCellFactory(getCheckInDayCellFactory());

	    // Disable dates before or the same as the selected check-in date for checkOutDate
	    checkOutDate.setDayCellFactory(getCheckOutDayCellFactory());
	    
//	    // Load the room records to database
//	    try {
//			HotelDatabase.loadRoomsToDatabase();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	 @FXML
    void searchBtnClick(ActionEvent event) throws IOException {
		 
		// Get the stage from other node i.e, searchBtn this way we use the same instance of the stage
		Stage window = (Stage) searchBtn.getScene().getWindow();
				    
		// Check if both dates are picked
	    if (checkInDate.getValue() == null || checkOutDate.getValue() == null) {
	        // Show an alert box
	    	CustomAlert.showAlert(Alert.AlertType.ERROR, window, "Missing dates", "Please select both check-in and check-out dates");
	        return;
	    }
		 
		 // Load the Login view
	     FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GuestBookingPage.fxml"));
		 Parent root = loader.load();
		 Scene sc = new Scene(root);

		 GuestBookingPageController controller = loader.getController();
		 // Set the value from the text field in the main scene
		 controller.setSearchDetail(checkInDate.getValue(), checkOutDate.getValue(), guestNum.getValue() );
		 
		    
		 window.setResizable(false);
		 window.setScene(sc);
		 window.show();
	    
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
		                    if (item.compareTo(checkInDate.getValue()) <= 0) {
		                        setDisable(true);
		                    }
		                }
		            };
		        }
		    };
		}
}

