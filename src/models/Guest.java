package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Guest {
    private IntegerProperty guestId;
    private StringProperty title;
    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty phone;
    private StringProperty email;
    private Address address;

    public Guest() {
        this.guestId = new SimpleIntegerProperty(this, "guestId", 0);
        this.title = new SimpleStringProperty(this, "title", "");
        this.firstName = new SimpleStringProperty(this, "firstName", "");
        this.lastName = new SimpleStringProperty(this, "lastName", "");
        this.phone = new SimpleStringProperty(this, "phone", "");
        this.address = new Address();
        this.email = new SimpleStringProperty(this, "email", "");
    }

    // Getter and Setters
    // GuestId
    public IntegerProperty getGuestIdProperty() {
        return guestId;
    }
    public int getGuestId() {
        return guestId.get();
    }
    public void setGuestId(int guestId) {
        this.guestId.set(guestId);
    }

    // Title
    public StringProperty getTitleProperty() {
        return title;
    }
    public String getTitle() {
        return title.get();
    }
    public void setTitle(String title) {
        this.title.set(title);
    }
    
    // Email
    public StringProperty getEamilProperty() {
        return email;
    }
    public String getEmail() {
        return email.get();
    }
    public void setEmail(String email) {
        this.email.set(email);
    }

    // FirstName
    public StringProperty getFirstNameProperty() {
        return firstName;
    }
    public String getFirstName() {
        return firstName.get();
    }
    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    // LastName
    public StringProperty getLastNameProperty() {
        return lastName;
    }
    public String getLastName() {
        return lastName.get();
    }
    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    // Phone
    public StringProperty getPhoneProperty() {
        return phone;
    }
    public String getPhone() {
        return phone.get();
    }
    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    // Address
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
}