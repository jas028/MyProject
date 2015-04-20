package budgetmanager.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Helper functions for handling dates.
 */
public class DateUtil {

	/** Date pattern that is used for conversion. */
	private static final String DATE_TIME_PATTERN = "MM/dd/yyyy";
	
	/** Date formatter */
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
	
	/**
	 * Returns the given date as a well formatted String. The format {@link DateUtil#DATE_TIME_PATTERN} is used.
	 * 
	 * @param date to be returned as string
	 * @return Formatted String
	 */
	public static String format(LocalDate date) {
		if(date == null) {
			return null;
		}
		return DATE_FORMATTER.format(date);
	}
	
	/**
	 * Converts a String in the format of {@link DateUtil#DATE_TIME_PATTERN} to a {@link LocalDate} object.
	 * Returns null if the string could not be converted.
	 * 
	 * @param dateString the date as a String
	 * @return The date object or null if it could not be converted
	 */
	public static LocalDate parse(String dateString) {
		try {
			return DATE_FORMATTER.parse(dateString, LocalDate::from);
		} catch(DateTimeParseException e) {
			return null;
		}
	}
	
	/**
	 * Checks whether the String is a valid date.
	 * 
	 * @param dateString
	 * @return True if the String is a valid date
	 */
	public static boolean validDate(String dateString) {
		// Try to parse the String
		return DateUtil.parse(dateString) != null;
	}
}
