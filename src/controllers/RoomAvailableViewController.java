package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import database.HotelDatabase;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Room;

public class RoomAvailableViewController implements Initializable{
	
	ObservableList<Room> availableRooms = FXCollections.observableArrayList();
	@FXML
    private Button backBtn;

    @FXML
    private TableColumn<Room, Integer> idColumn;

    @FXML
    private Label noRoomTxt;

    @FXML
    private TableColumn<Room, Double> priceColumn;

    @FXML
    private TableView<Room> roomTable;

    @FXML
    private TableColumn<Room, String> typeColumn;

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
		idColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("rate"));
        
        
        try {
            List<Room> roomList = HotelDatabase.getAvailableRooms();
            availableRooms = FXCollections.observableArrayList(roomList);
            roomTable.setItems(availableRooms);	
            
         // Bind the number of rooms text to the size of the room list
            noRoomTxt.textProperty().bind(Bindings.concat(Bindings.size(availableRooms).asString(), availableRooms.size() > 1 ? " rooms" : " room"));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
		
	}

}
