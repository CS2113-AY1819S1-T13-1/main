package seedu.address.model;

import java.util.ArrayList;
import java.util.List;


/**
 * {@code StudentPlanner} that keeps track of its own history.
 */
public class VersionedStudentPlanner extends StudentPlanner {
    private final List<ReadOnlyStudentPlanner> studentPlannerStateList;
    private int currentStatePointer;

    public VersionedStudentPlanner(ReadOnlyStudentPlanner initialState) {
        super(initialState);

        studentPlannerStateList = new ArrayList<>();
        studentPlannerStateList.add(new StudentPlanner(initialState));
        currentStatePointer = 0;
    }

    /**
     * Saves a copy of the current {@code StudentPlanner} state at the end of the state list.
     */
    public void commit() {
        removeStatesAfterCurrentPointer();
        studentPlannerStateList.add(new StudentPlanner(this));
        currentStatePointer++;
    }

    private void removeStatesAfterCurrentPointer() {
        studentPlannerStateList.subList(currentStatePointer + 1, studentPlannerStateList.size()).clear();
    }

}
