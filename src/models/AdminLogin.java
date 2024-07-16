package models;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AdminLogin {
	private int adminId;
	private final static String USERNAME_PROP_NAME = "userName";
	private final SimpleStringProperty userName;
	private final static String PASSWORD_PROP_NAME = "password";
	private final SimpleStringProperty password;
	
	public AdminLogin() {
		userName = new SimpleStringProperty(this,
											USERNAME_PROP_NAME, 
											" ");
		password = new SimpleStringProperty(this, PASSWORD_PROP_NAME, "");
	}

	

	public final StringProperty userNameProperty() {
		return this.userName;
	}
	

	public final String getUserName() {
		return this.userNameProperty().get();
	}
	

	public final void setUserName(final String userName) {
		this.userNameProperty().set(userName);
	}
	

	public final StringProperty passwordProperty() {
		return this.password;
	}
	

	public final String getPassword() {
		return this.passwordProperty().get();
	}
	

	public final void setPassword(final String password) {
		this.passwordProperty().set(password);
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}
	
}
