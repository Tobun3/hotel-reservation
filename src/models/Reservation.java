package models;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import database.HotelDatabase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import utility.ReservationStatus;

public class Reservation {
	private IntegerProperty bookingId;
	private ObjectProperty<LocalDate> bookDate;
	private ObjectProperty<LocalDate> checkInDate;
	private ObjectProperty<LocalDate> checkOutDate;
	private Guest guest;
	private Bill bill;
	private IntegerProperty roomCount;
	private IntegerProperty numGuest;
	private List<Room> roomList = new ArrayList<>();
	private ReservationStatus revStatus;
	
	public ReservationStatus getRevStatus() {
		return revStatus;
	}

	public void setRevStatus(ReservationStatus revStatus) {
		this.revStatus = revStatus;
	}

	public Reservation() {
	    this.bookingId = new SimpleIntegerProperty();
	    this.bookDate = new SimpleObjectProperty<>();
	    this.checkInDate = new SimpleObjectProperty<>();
	    this.checkOutDate = new SimpleObjectProperty<>();
	    this.roomCount = new SimpleIntegerProperty();
	    this.numGuest = new SimpleIntegerProperty();
	    this.guest = new Guest();
	    this.bill = new Bill();
	    this.revStatus = ReservationStatus.ACTIVE;
	}
	
	// Getters and Setters for all properties
	//BookingId
    public IntegerProperty bookingIdProperty() {
        return this.bookingId;
    }
    public int getBookingId() {
        return this.bookingIdProperty().get();
    }
    public void setBookingId(final int bookingId) {
        this.bookingIdProperty().set(bookingId);
    }
    
    // Check in date
    public ObjectProperty<LocalDate> checkInDateProperty() {
        return this.checkInDate;
    }
    public LocalDate getCheckInDate() {
        return this.checkInDateProperty().get();
    }
    public void setCheckInDate(final LocalDate checkInDate) {
        this.checkInDateProperty().set(checkInDate);
    }
    
    // Check out Date
    public ObjectProperty<LocalDate> checkOutDateProperty() {
        return this.checkOutDate;
    }
    public LocalDate getCheckOutDate() {
        return this.checkOutDateProperty().get();
    }
    public void setCheckOutDate(final LocalDate checkOutDate) {
        this.checkOutDateProperty().set(checkOutDate);
    }

	//Book Date
	public final ObjectProperty<LocalDate> bookDateProperty() {
		return this.bookDate;
	}
	
	public final LocalDate getBookDate() {
		return this.bookDateProperty().get();
	}
	
	public final void setBookDate(final LocalDate bookDate) {
		this.bookDateProperty().set(bookDate);
	}
	
	// Getter and Setter for guest
	public Guest getGuest() {
	    return guest;
	}
	public void setGuest(Guest guest) {
	    this.guest = guest;
	}

	// Getter and Setter for bill
	public Bill getBill() {
	    return bill;
	}
	public void setBill(Bill bill) {
	    this.bill = bill;
	}

	// Getter and Setter for roomCount
	public IntegerProperty roomCountProperty() {
	    return roomCount;
	}
	public int getRoomCount() {
	    return roomCount.get();
	}
	public void setRoomCount(int roomCount) {
	    this.roomCount.set(roomCount);
	}

	// Getter and Setter for numGuest
	public IntegerProperty numGuestProperty() {
	    return numGuest;
	}
	public int getNumGuest() {
	    return numGuest.get();
	}
	public void setNumGuest(int numGuest) {
	    this.numGuest.set(numGuest);
	}

	public List<Room> getRoomList() throws SQLException {
		return roomList;
	}

	public void setRoomList(List<Room> roomList) {
		this.roomList = roomList;
	}
	
	// Get the total price for the reservation
	public double getTotalPrice() throws SQLException {
		int dateBetween = (int) ChronoUnit.DAYS.between(getCheckInDate(), getCheckOutDate());
	    double totalPrice = 0.0;
	    for (Room room : roomList) {
	        totalPrice += room.getRate();
	    }
	    totalPrice *= dateBetween;
	    return totalPrice;
	}

	@Override
	public String toString() {
		return "Reservation [bookDate=" + bookDate + ", checkInDate=" + checkInDate + ", checkOutDate=" + checkOutDate
				+ ", roomCount=" + roomCount + ", numGuest=" + numGuest + ", roomList=" + roomList + "]";
	}

	
}
