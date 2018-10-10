package seedu.address.model.task;

public class DateTime {
    public static final String MESSAGE_DATETIME_CONSTRAINTS =
            "Date & Time should only contain numbers, and it should be at least 4 digits long";
    public static final String DATETIME_VALIDATION_REGEX = "\\d{4,}";

    private String dateTimeString;

    public DateTime(String dateTimeString) {
        this.dateTimeString = dateTimeString;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidDateTime(String test) {
        return true;
        //return test.matches(DATETIME_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return dateTimeString;
    }
}
