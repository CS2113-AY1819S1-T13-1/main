package seedu.address.model.task;

public class Priority {
    public static final String MESSAGE_PRIORITY_CONSTRAINTS =
            "Task body can take any values, and it should not be blank";
    public static final String PRIORITY_VALIDATION_REGEX = "[\\p{Alnum}]*";

    private String priorityString;

    public Priority(String priorityString) {
        this.priorityString = priorityString;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPriority(String test) {
        return test.matches(PRIORITY_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return priorityString;
    }
}
