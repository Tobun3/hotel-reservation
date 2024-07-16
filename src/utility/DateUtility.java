package utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtility {

	private static final String DATE_PATTERN = "dd-MMM-yyyy";
	
	private static final DateTimeFormatter DATE_FORMATTER = 
			DateTimeFormatter.ofPattern(DATE_PATTERN);
	
	public static String format(LocalDate date) {
		if(date == null)
			return null;
		return DATE_FORMATTER.format(date);
	}
	
	public static LocalDate parse(String date) throws DateTimeParseException{
		return DATE_FORMATTER.parse(date,LocalDate::from);
	}
	
	
}
