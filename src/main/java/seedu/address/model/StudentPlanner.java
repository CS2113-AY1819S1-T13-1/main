package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.model.task.Task;
import seedu.address.model.task.UniqueTaskList;

/**
 * Wraps all data at the student planner level
 * Duplicates are not allowed (by .isSameTask comparison)
 */
public class StudentPlanner implements ReadOnlyStudentPlanner {

    private final UniqueTaskList tasks;

    {
        tasks = new UniqueTaskList();
    }
    public StudentPlanner() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public StudentPlanner(ReadOnlyStudentPlanner toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    /**
     * Replaces the contents of the task list with {@code tasks}.
     * {@code tasks} must not contain duplicate tasks.
     */
    public void setTasks(List<Task> tasks) {
        this.tasks.setTasks(tasks);
    }

    /**
     * Resets the existing data of this {@code StudentPlanner} with {@code newData}.
     */
    public void resetData(ReadOnlyStudentPlanner newData) {
        requireNonNull(newData);

        setTasks(newData.getTaskList());
    }

    /**
     * Returns true if a task with the same identity as {@code task} exists in the student planner.
     */
    public boolean hasTask(Task task) {
        requireNonNull(task);
        return tasks.contains(task);
    }

    /**
     * Adds a task to the student planner.
     * The task must not already exist in the student planner.
     */
    public void addTask(Task t) {
        tasks.add(t);
    }

    /**
     * Replaces the given task {@code target} in the list with {@code editedTask}.
     * {@code target} must exist in the student planner.
     * The task identity of {@code editedTask} must not be the same as another existing task in the student planner.
     */
    public void updateTask(Task target, Task editedTask) {
        requireNonNull(editedTask);

        tasks.setTask(target, editedTask);
    }

    /**
     * Removes {@code key} from this {@code StudentPlanner}.
     * {@code key} must exist in the student planner.
     */
    public void removeTask(Task key) {
        tasks.remove(key);

    }

    //// util methods

    @Override
    public String toString() {
        return tasks.asUnmodifiableObservableList().size() + " tasks";
    }

    @Override
    public ObservableList<Task> getTaskList() {
        return tasks.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof StudentPlanner // instanceof handles nulls
                && tasks.equals(((StudentPlanner) other).tasks));
    }

    @Override
    public int hashCode() {
        return tasks.hashCode();
    }


}
