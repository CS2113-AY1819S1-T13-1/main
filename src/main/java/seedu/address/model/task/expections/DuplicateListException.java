package seedu.address.model.task.expections;

public class DuplicateListException extends RuntimeException {
    public DuplicateListException() {
        super("Operation would result in duplicate tasks");
    }
}
