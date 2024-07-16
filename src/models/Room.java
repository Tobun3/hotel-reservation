package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import utility.RoomStatus;
import utility.RoomType;

public class Room {
    private IntegerProperty roomId;
    private DoubleProperty rate;
    private RoomStatus status;
    private RoomType type;
    private int rsvpId;

    public Room() {
        this.roomId = new SimpleIntegerProperty(this, "roomId", 0);
        this.rate = new SimpleDoubleProperty(this, "rate", 0.0);
        this.status = RoomStatus.AVAILABLE; // by default the room should be available
        this.type = RoomType.SINGLE;
    }

    // Getter and Setters
    // RoomId
    public IntegerProperty getRoomIdProperty() {
        return roomId;
    }
    public int getRoomId() {
        return roomId.get();
    }

	public void setRoomId(int roomId) {
        this.roomId.set(roomId);
    }
	
	// RoomType
	public RoomType getType() {
		return type;
	}

	public void setType(RoomType type) {
		this.type = type;
	}

    // Rate
    public DoubleProperty getRateProperty() {
        return rate;
    }
    public double getRate() {
        return rate.get();
    }
    public void setRate(double rate) {
        this.rate.set(rate);
    }

    // Status
    public RoomStatus getStatus() {
        return status;
    }
    public void setStatus(RoomStatus status) {
        this.status = status;
    }

	public int getRsvpId() {
		return rsvpId;
	}

	public void setRsvpId(int rsvpId) {
		this.rsvpId = rsvpId;
	}
}
