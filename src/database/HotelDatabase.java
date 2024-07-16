package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Address;
import models.Bill;
import models.Guest;
import models.Reservation;
import models.Room;
import utility.ReservationStatus;
import utility.RoomStatus;
import utility.RoomType;


public class HotelDatabase {
	private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/hotelReservation?useSSL=false";
	private static final String DATABASE_USERNAME ="root";
	private static final String DATABASE_PASSWORD ="mypassword1";
	// Insert query without address
	private static final String INSERT_QUERY_GUEST_W_ADDRESS ="INSERT INTO guests (title , first_name, last_name , phone, address_id, email) VALUES (?, ?, ?, ?, ?, ?)";
	// Insert guest query with address
	private static final String INSERT_QUERY_GUEST ="INSERT INTO guests (title , first_name, last_name , phone, email) VALUES (?, ?, ?, ?, ?)";
	// Insert address
	private static final String INSERT_QUERY_ADDRESS ="INSERT INTO addresses (street , city, province , zip_code) VALUES (?, ?, ?, ?)";
	// insert room
	private static final String INSERT_QUERY_ROOM ="INSERT INTO rooms ( rate, status, type) VALUES (?, ?, ?)";
	// query to get all available rooms
	private static final String SELECT_AVAILABLE_ROOMS_QUERY = "SELECT * FROM rooms WHERE status = 'AVAILABLE'";
	// query to get all reservation
	private static final String SELECT_RESERVATION_QUERY = "SELECT booking_id, checkInDate, checkOutDate, guest_id, roomCount, numGuest, bill_id, revStatus FROM reservations";
	// query to get all reservation
	private static final String SELECT_RESERVATION_QUERY_ID = "SELECT booking_id, checkInDate, checkOutDate, guest_id, roomCount, numGuest, bill_id, revStatus FROM reservations WHERE booking_id = ?";
	// Insert reservation
	private static final String INSERT_QUERY_RSVP = "INSERT INTO Reservations (book_date, checkInDate, checkOutDate, guest_id, roomCount, numGuest, revStatus) VALUES (?, ?, ?, ?, ?, ?, ?)";
	// Update the room rsvp id
	private static final String UPDATE_QUERY_ROOM = "UPDATE rooms SET booking_id = ?, status = 'BOOKED'WHERE room_id = ?";
	// Define the SQL UPDATE query
    private static final String UPDATE_RESERVATION_STATUS_QUERY = "UPDATE reservations SET revStatus = ? WHERE booking_id = ?";
	// Get a room by id
	private static final String SELECT_QUERY_ROOM = "SELECT * FROM rooms WHERE room_id = ?";
	// Get guest by id
	private static final String SELECT_GUEST_BY_ID_QUERY = "SELECT * FROM guests WHERE guest_id = ?";
	// Get address by id
	private static final String SELECT_ADDRESS_BY_ID_QUERY = "SELECT * FROM addresses WHERE address_id = ?";
	//  Get a list of rooms associate with the id
	private static final String SELECT_ROOMS_BY_BOOKING_ID_QUERY = "SELECT * FROM rooms WHERE booking_id = ?";
	// Insert a bill
	private static final String INSERT_QUERY_BILL = "INSERT INTO Bills (amountToPay, isSettle, discount, payDate) VALUES (?, ?, ?, ?)";
	// update reseravation with bill id
	private static final String UPDATE_RESERVATION_BY_BILL_ID_QUERY = "UPDATE reservations SET bill_id = ? WHERE booking_id = ?";
	// update the room status to be available and disconnect form booking_id
	private static final String UPDATE_ROOM_STATUS_QUERY ="UPDATE rooms SET status = 'AVAILABLE' , booking_id = NULL WHERE room_id = ?";
	
	// For Admin Login
	private static final String SELECT_QUERY_ADMIN = "SELECT * FROM adminlogin WHERE username = ?";	
	// Get bill by id
	private static final String SELECT_QUERY_BILL_BY_ID = "SELECT * FROM Bills WHERE bill_id = ?";
	// other constant variable
	private static final int NUM_ROOM_CHEAP = 10;
	private static final int NUM_ROOM_EXPENSIVE =5;
	//private static final String SELECT_ADDRESS_QUERY = "SELECT * FROM registration WHERE  = ?";
	
	// To insert Guest Record
	public static int insertGuestRecord(Guest guest, Address address) throws SQLException {
		
		int guestId = 0;
	    // Step 1: Establishing a Connection and
	    // try with resource statement will auto close the connection.
		try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
		    int addressId = 0; // Initialize to default value
		    // Step 2:Create a statement using connection object
		    // Insert address first
		    if (address != null) {
			        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY_ADDRESS, Statement.RETURN_GENERATED_KEYS);
			        preparedStatement.setString(1, address.getStreet());
			        preparedStatement.setString(2, address.getCity());
			        preparedStatement.setString(3, address.getProvince());
			        preparedStatement.setString(4, address.getZipCode());
			        // Step 3: Execute the query or update query
			        preparedStatement.executeUpdate();
	
			        // Get the generated address_id
			        ResultSet rs = preparedStatement.getGeneratedKeys();
			        if (rs.next()) {
			            addressId = rs.getInt(1);
			            
				        // Now you can use the addressId to put guest record in guests table
					    PreparedStatement preparedStatementGuest = connection.prepareStatement(INSERT_QUERY_GUEST_W_ADDRESS, Statement.RETURN_GENERATED_KEYS);
					    // Set the parameters for the guest record
					    preparedStatementGuest.setString(1, guest.getTitle());
					    preparedStatementGuest.setString(2, guest.getFirstName());
					    preparedStatementGuest.setString(3, guest.getLastName());
					    preparedStatementGuest.setString(4, guest.getPhone());
					    preparedStatementGuest.setInt(5, addressId);
					    preparedStatementGuest.setString(6,  guest.getEmail());
					    preparedStatementGuest.executeUpdate();
					    
					    rs = preparedStatementGuest.getGeneratedKeys();
					    
					    if (rs.next()) {
				            	guestId = rs.getInt(1);
				            return guestId;
					    }
			        }
		    }else {
		    	// Insert record without the address
	        	System.out.println("Guest No address");
	        	PreparedStatement preparedStatementGuest = connection.prepareStatement(INSERT_QUERY_GUEST, Statement.RETURN_GENERATED_KEYS);
			    // Set the parameters for the guest record
			    preparedStatementGuest.setString(1, guest.getTitle());
			    preparedStatementGuest.setString(2, guest.getFirstName());
			    preparedStatementGuest.setString(3, guest.getLastName());
			    preparedStatementGuest.setString(4, guest.getPhone());
			    preparedStatementGuest.setString(5,  guest.getEmail());
			    preparedStatementGuest.executeUpdate();
			    
			    // Get the generated guest_id
		        ResultSet rs = preparedStatementGuest.getGeneratedKeys();
		        if (rs.next()) {
		            guestId = rs.getInt(1);
		            return guestId;
			    }
		        
		    }

		    
		    System.out.println("Insert Guest Record Successfully!");
	    	
	    } catch (SQLException e) {
	        // print SQL exception information
	        printSQLException(e);
	    }
		return guestId;
		
	}
	
	
	// Insert Reservation record
	public static int insertReservationRecord(Reservation reservation, int guestId) throws SQLException {
		
	    int bookingId = 0;
	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
	        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY_RSVP, Statement.RETURN_GENERATED_KEYS);
	        preparedStatement.setDate(1, java.sql.Date.valueOf(reservation.getBookDate()));
	        preparedStatement.setDate(2, java.sql.Date.valueOf(reservation.getCheckInDate()));
	        preparedStatement.setDate(3, java.sql.Date.valueOf(reservation.getCheckOutDate()));
	        preparedStatement.setInt(4, guestId);
	        preparedStatement.setInt(5, reservation.getRoomCount());
	        preparedStatement.setInt(6, reservation.getNumGuest());
	        preparedStatement.setString(7, reservation.getRevStatus().name());
	        preparedStatement.executeUpdate();

	        ResultSet rs = preparedStatement.getGeneratedKeys();
	        if (rs.next()) {
	            bookingId = rs.getInt(1);
	        }

	        System.out.println("Insert Reservation Record Successfully!");

	    } catch (SQLException e) {
	        printSQLException(e);
	    }
	    return bookingId;
	}
	
	// Insert Bill and return the bill id
	public static int insertBillRecord( Bill bill, int rspvId) throws SQLException {
		int billId = 0;
		 // connect to database
		try(Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)){
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY_BILL, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setDouble(1, bill.getAmountToPay());
			preparedStatement.setBoolean(2, bill.getIsSettle());
			preparedStatement.setDouble(3, bill.getDiscount());
			preparedStatement.setDate(4, java.sql.Date.valueOf(bill.getPayDate()));
			
			preparedStatement.executeUpdate();
			
			// if success we get the bookingId
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
	            billId = rs.getInt(1);
	            
	            // update the reservation with the bill_id
	         // update the reservation with the bill_id
	            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_RESERVATION_BY_BILL_ID_QUERY);
	            updateStatement.setInt(1, billId);
	            updateStatement.setInt(2, rspvId);
	            updateStatement.executeUpdate();
	        }

	        System.out.println("Insert Bill Record Successfully!");
	        

	    } catch (SQLException e) {
	        printSQLException(e);
	    }
		
		return billId;
	}
	
	
	// To load the room into database
	public static void loadRoomsToDatabase() throws SQLException {
	    
		try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
			PreparedStatement preparedStatementGuest = connection.prepareStatement(INSERT_QUERY_ROOM);
		    // Set the parameters for the guest record
        	for(int i = 0; i < NUM_ROOM_CHEAP ; i++) {
			    preparedStatementGuest.setDouble(1, 150.99);
			    preparedStatementGuest.setString(2, RoomStatus.AVAILABLE.name());
			    preparedStatementGuest.setString(3, RoomType.SINGLE.name());
			    preparedStatementGuest.executeUpdate();
        	}
        	
        	for(int i = 0; i < NUM_ROOM_CHEAP ; i++) {
			    preparedStatementGuest.setDouble(1, 150.99);
			    preparedStatementGuest.setString(2, RoomStatus.AVAILABLE.name());
			    preparedStatementGuest.setString(3, RoomType.DOUBLE.name());
			    preparedStatementGuest.executeUpdate();
        	}
        	
        	for(int i = 0; i < NUM_ROOM_EXPENSIVE ; i++) {
			    preparedStatementGuest.setDouble(1, 299.99);
			    preparedStatementGuest.setString(2, RoomStatus.AVAILABLE.name());
			    preparedStatementGuest.setString(3, RoomType.DELUXE.name());
			    preparedStatementGuest.executeUpdate();
        	}
        	
        	for(int i = 0; i < NUM_ROOM_EXPENSIVE ; i++) {
			    preparedStatementGuest.setDouble(1, 599.99);
			    preparedStatementGuest.setString(2, RoomStatus.AVAILABLE.name());
			    preparedStatementGuest.setString(3, RoomType.PENTHOUSE.name());
			    preparedStatementGuest.executeUpdate();
        	}
		    
	    } catch (SQLException e) {
	        // print SQL exception information
	        printSQLException(e);
	    }
	}
	
	public static void printSQLException (SQLException ex )
	{
		for(Throwable e: ex)
		{
			if(e instanceof SQLException )
			{
				e.printStackTrace (System.err ); 
				System.err.println ("SQLState : "
									+ ((SQLException) e).getSQLState ()); 
				System.err.println("Error Code: "
									+ ((SQLException) e).getErrorCode()); 
				System.err.println("Message: " + e.getMessage ()); 
				Throwable t = ex.getCause();
				while (t != null )
				{
					System.out.println("Cause: " +t);
					t =t.getCause ();
				}
			}
		}
	}
	
	// Method to get all available rooms
	public static List<Room> getAvailableRooms() throws SQLException {
		// create a list to store our room
	    List<Room> rooms = new ArrayList<>();

	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
	        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AVAILABLE_ROOMS_QUERY);
	        ResultSet rs = preparedStatement.executeQuery();

	        while (rs.next()) {
	            Room room = new Room();
	            room.setRoomId(rs.getInt("room_id"));
	            room.setRate(rs.getDouble("rate"));
	            room.setStatus(RoomStatus.valueOf(rs.getString("status")));
	            room.setType(RoomType.valueOf(rs.getString("type")));
	            rooms.add(room);
	        }
	    } catch (SQLException e) {
	        printSQLException(e);
	    }

	    return rooms;
	}
	
	
	// Method to get the count of available rooms
	public static int getAvailableRoomCount() throws SQLException {
	    return getAvailableRooms().size();
	}
	
	
	public static void testConnection() {
	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
	        System.out.println("Successfully connected to the database!");
	    } catch (SQLException e) {
	        System.out.println("Failed to connect to the database.");
	        printSQLException(e);
	    }
	}
	
	// update the status of the reservation
	public static void updateReservationStatus(int bookingId, String newStatus) throws SQLException {

	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
	        // Create a PreparedStatement object
	        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RESERVATION_STATUS_QUERY);

	        // Set the parameters for the update query
	        preparedStatement.setString(1, newStatus);
	        preparedStatement.setInt(2, bookingId);

	        // Execute the update query
	        int rowsAffected = preparedStatement.executeUpdate();

	        if (rowsAffected > 0) {
	            System.out.println("Reservation status updated successfully!");
	        } else {
	            System.out.println("No reservation found with the given booking ID.");
	        }
	    } catch (SQLException e) {
	        printSQLException(e);
	    }
	}
	
	// update the room when the room has been booked 
	public static void updateRoomBookingId(int roomId, int bookingId) {

	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
	         PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY_ROOM)) {

	        preparedStatement.setInt(1, bookingId);
	        preparedStatement.setInt(2, roomId);

	        preparedStatement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// Get a room by its id
	public static Room getRoomById(int roomId) {
	    Room room = null;

	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
	         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY_ROOM)) {

	        preparedStatement.setInt(1, roomId);

	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            room = new Room();
	            room.setRoomId(resultSet.getInt("room_id"));
	            room.setRate(resultSet.getDouble("rate"));
	            room.setStatus(RoomStatus.valueOf(resultSet.getString("status")));
	            room.setType(RoomType.valueOf(resultSet.getString("type")));
	            room.setRsvpId(resultSet.getInt("rsvp_id"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return room;
	}
	
	// Get a admin user by username
	public static Map<String, String> getAdminByUsername(String username) {
	    Map<String, String> adminCredentials = null;

	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
	         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY_ADMIN)) {

	        preparedStatement.setString(1, username);

	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            adminCredentials = new HashMap<>();
	            adminCredentials.put(resultSet.getString("username"), resultSet.getString("password"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return adminCredentials;
	}
	
	public static List<Reservation> getAllReservations() throws SQLException {
	    List<Reservation> reservations = new ArrayList<>();

	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
	        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_RESERVATION_QUERY);
	        ResultSet rs = preparedStatement.executeQuery();

	        while (rs.next()) {
	            Reservation reservation = new Reservation();
	            reservation.setBookingId(rs.getInt("booking_id"));
	            reservation.setCheckInDate(rs.getDate("checkInDate").toLocalDate());
	            reservation.setCheckOutDate(rs.getDate("checkOutDate").toLocalDate());
	            reservation.setGuest(getGuestById(rs.getInt("guest_id")));
	            reservation.setRoomCount(rs.getInt("roomCount"));
	            reservation.setRoomList(getRoomByBookingId(rs.getInt("booking_id")));
	            reservation.setNumGuest(rs.getInt("numGuest"));
	            reservation.setBill(HotelDatabase.getBillById(rs.getInt("bill_id")));
	            reservation.setRevStatus(ReservationStatus.valueOf(rs.getString("revStatus").toUpperCase()));
	            reservations.add(reservation);
	        }
	    } catch (SQLException e) {
	        printSQLException(e);
	    }

	    return reservations;
	}
	
	// Get guest by id
	public static Guest getGuestById(int guestId) throws SQLException {
	    Guest guest = null;

	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
	        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GUEST_BY_ID_QUERY);
	        preparedStatement.setInt(1, guestId);
	        ResultSet rs = preparedStatement.executeQuery();

	        if (rs.next()) {
	            guest = new Guest();
	            guest.setTitle(rs.getString("title"));
	            guest.setFirstName(rs.getString("first_name"));
	            guest.setLastName(rs.getString("last_name"));
	            guest.setPhone(rs.getString("phone"));
	            guest.setAddress(getAddressById(rs.getInt("address_id")));
	            guest.setEmail(rs.getString("email"));
	        }
	    } catch (SQLException e) {
	        printSQLException(e);
	    }

	    return guest;
	}
	
	// Get address by its id
	public static Address getAddressById(int addressId) throws SQLException {
	    Address address = null;

	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
	        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ADDRESS_BY_ID_QUERY);
	        preparedStatement.setInt(1, addressId);
	        ResultSet rs = preparedStatement.executeQuery();

	        if (rs.next()) {
	            address = new Address();
	            address.setStreet(rs.getString("street"));
	            address.setCity(rs.getString("city"));
	            address.setProvince(rs.getString("province"));
	            address.setZipCode(rs.getString("zip_code"));
	        }
	    } catch (SQLException e) {
	        printSQLException(e);
	    }

	    return address;
	}
	
	// Get a list of room that belong the the booking
	public static List<Room> getRoomByBookingId(int bookingId) throws SQLException {
	    List<Room> rooms = new ArrayList<>();

	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
	        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROOMS_BY_BOOKING_ID_QUERY);
	        preparedStatement.setInt(1, bookingId);
	        ResultSet rs = preparedStatement.executeQuery();

	        while (rs.next()) {
	            Room room = new Room();
	            room.setRoomId(rs.getInt("room_id"));
	            room.setRate(rs.getDouble("rate"));
	            room.setStatus(RoomStatus.valueOf(rs.getString("status").toUpperCase()));
	            room.setType(RoomType.valueOf(rs.getString("type").toUpperCase()));
	            rooms.add(room);
	        }
	    } catch (SQLException e) {
	        printSQLException(e);
	    }

	    return rooms;
	}
	
	// Get a booking by its id
	public static Reservation getReservationById(int reservationId) {
	    Reservation reservation = null;

	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
	         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_RESERVATION_QUERY_ID)) {

	        preparedStatement.setInt(1, reservationId);

	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            reservation = new Reservation();
	            reservation.setBookingId(resultSet.getInt("booking_id"));
	            reservation.setCheckInDate(resultSet.getDate("checkInDate").toLocalDate());
	            reservation.setCheckOutDate(resultSet.getDate("checkOutDate").toLocalDate());
	            reservation.setGuest(getGuestById(resultSet.getInt("guest_id")));
	            reservation.setRoomCount(resultSet.getInt("roomCount"));
	            reservation.setNumGuest(resultSet.getInt("numGuest"));
	            reservation.setRoomList(getRoomByBookingId(resultSet.getInt("booking_id")));
	            reservation.setBill(HotelDatabase.getBillById(resultSet.getInt("bill_id")));
	            reservation.setRevStatus(ReservationStatus.valueOf(resultSet.getString("revStatus").toUpperCase()));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return reservation;
	}
	
	public static Bill getBillById(int billId) throws SQLException {
	    Bill bill = null;
	    // Establishing a Connection
	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
	        //Create a statement using connection object
	        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY_BILL_BY_ID);
	        preparedStatement.setInt(1, billId);
	        
	        // Execute the query or update query
	        ResultSet rs = preparedStatement.executeQuery();

	        // SProcess the ResultSet object.
	        while (rs.next()) {
	            bill = new Bill();
	            bill.setBillId(billId);
	            bill.setAmountToPay(rs.getDouble("amountToPay"));
	            bill.setDiscount(rs.getDouble("discount"));
	            bill.setPayDate(rs.getDate("payDate").toLocalDate());
	            bill.setIsSettle(rs.getBoolean("isSettle"));
	        }
	    } catch (SQLException e) {
	        printSQLException(e);
	    }
	    return bill;
	}
	
	// Function to update room status
	public static void updateRoomStatus(int roomId) throws SQLException {
	    try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
	        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ROOM_STATUS_QUERY);
	        preparedStatement.setInt(1, roomId);
	        preparedStatement.executeUpdate();

	        System.out.println("Room status updated successfully!");
	    } catch (SQLException e) {
	        printSQLException(e);
	    }
	}
	
}
