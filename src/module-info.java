module HotelReservation {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	requires javafx.base;
	
	opens application to javafx.graphics, javafx.fxml;
	opens controllers to javafx.fxml;
	exports controllers to javafx.graphics,javafx.fxml;
	opens models to javafx.base;
}
