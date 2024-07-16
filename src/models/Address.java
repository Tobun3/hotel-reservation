package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Address {
	private StringProperty street;
	private StringProperty city;
	private StringProperty province;
	private StringProperty zipCode;
	
	public Address() {
		this.street = new SimpleStringProperty(this,"street"," ");
		this.city = new SimpleStringProperty(this,"city","");
		this.province = new SimpleStringProperty(this,"province"," ");
		this.zipCode = new SimpleStringProperty(this,"zipCode","");
	}

	// Getter and Setters
	//Street
	public StringProperty getStreetProperty() {
		return street;
	}
	public String getStreet() {
		return street.get();
	}
	public void setStreet(String street) {
		this.street.set(street);
	}
	
	//City
	public StringProperty getCityProperty() {
		return city;
	}
	
	public String getCity() {
		return city.get();
	}
	public void setCity(String city) {
	    this.city.set(city);
	}
	
	
	// Province
	public StringProperty getProvinceProperty() {
		return province;
	}
	public String getProvince() {
		return province.get();
	}
	public void setProvince(String province) {
		this.province.set(province);
	}
	
	// Zipcode
	public StringProperty getZipCodeProperty() {
		return zipCode;
	}
	public String getZipCode() {
		return zipCode.get();
	}
	public void setZipCode(String zipCode) {
		this.zipCode.set(zipCode);
	}
	
}
