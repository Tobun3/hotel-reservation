package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utility.RoomStatus;

public class RoomList {
    private ObservableList<Room> roomList;
    private int numRoomAvailable;

    public RoomList() {
        this.roomList = FXCollections.observableArrayList();
        this.numRoomAvailable = 0;
    }

    // Getter and Setter
    public ObservableList<Room> getRoomList() {
        return roomList;
    }
    public void setRoomList(ObservableList<Room> roomList) {
        this.roomList = roomList;
    }

    public int getNumRoomAvailable() {
        return numRoomAvailable;
    }
    public void setNumRoomAvailable(int numRoomAvailable) {
        this.numRoomAvailable = numRoomAvailable;
    }

    // Add a room to the roomList
    public void addRoom(Room room) {
        this.roomList.add(room);
    }

    // Remove a room from the roomList
    public void removeRoom(Room room) {
        this.roomList.remove(room);
    }

    // Check available rooms
    public ObservableList<Room> checkAvailableRoom() {
        ObservableList<Room> availableRooms = FXCollections.observableArrayList();
        for (Room room : this.roomList) {
            if (room.getStatus() == RoomStatus.AVAILABLE) {
                availableRooms.add(room);
            }
        }
        this.numRoomAvailable = availableRooms.size();
        return availableRooms;
    }
}
