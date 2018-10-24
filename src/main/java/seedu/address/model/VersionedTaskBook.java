package seedu.address.model;

import java.util.ArrayList;
import java.util.List;


/**
 * {@code TaskBook} that keeps track of its own history.
 */
public class VersionedTaskBook extends TaskBook {
    private final List<ReadOnlyTaskBook> studentPlannerStateList;
    private int currentStatePointer;

    public VersionedTaskBook(ReadOnlyTaskBook initialState) {
        super(initialState);

        studentPlannerStateList = new ArrayList<>();
        studentPlannerStateList.add(new TaskBook(initialState));
        currentStatePointer = 0;
    }

    /**
     * Saves a copy of the current {@code TaskBook} state at the end of the state list.
     */
    public void commit() {
        removeStatesAfterCurrentPointer();
        studentPlannerStateList.add(new TaskBook(this));
        currentStatePointer++;
    }

    private void removeStatesAfterCurrentPointer() {
        studentPlannerStateList.subList(currentStatePointer + 1, studentPlannerStateList.size()).clear();
    }

}
