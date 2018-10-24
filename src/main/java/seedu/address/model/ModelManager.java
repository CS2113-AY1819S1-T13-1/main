package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.events.model.AddressBookLocalBackupEvent;
import seedu.address.commons.events.model.AddressBookLocalRestoreEvent;
import seedu.address.commons.events.model.AddressBookOnlineRestoreEvent;
import seedu.address.commons.events.model.ExpenseBookChangedEvent;
import seedu.address.commons.events.model.ExpenseBookLocalBackupEvent;
import seedu.address.commons.events.model.ExpenseBookLocalRestoreEvent;
import seedu.address.commons.events.model.ExpenseBookOnlineRestoreEvent;
import seedu.address.commons.events.model.UserPrefsChangedEvent;
import seedu.address.commons.events.storage.OnlineBackupSuccessResultEvent;
import seedu.address.commons.events.ui.NewResultAvailableEvent;
import seedu.address.model.event.Event;
import seedu.address.model.expense.Expense;
import seedu.address.model.person.Person;
import seedu.address.model.task.Task;
import seedu.address.storage.OnlineStorage;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private VersionedExpenseBook versionedExpenseBook;
    private FilteredList<Expense> filteredExpenses;
    private VersionedAddressBook versionedAddressBook;
    private FilteredList<Person> filteredPersons;
    private VersionedTaskBook versionedStudentPlanner;
    private FilteredList<Task> filteredTasks;
    private UserPrefs userPrefs;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyTaskBook studentPlanner,
                        ReadOnlyExpenseBook expenseBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        versionedAddressBook = new VersionedAddressBook(addressBook);
        versionedExpenseBook = new VersionedExpenseBook(expenseBook);
        filteredPersons = new FilteredList<>(versionedAddressBook.getPersonList());
        filteredExpenses = new FilteredList<>(versionedExpenseBook.getExpenseList());

        versionedStudentPlanner = new VersionedTaskBook(studentPlanner);
        filteredTasks = new FilteredList<>(versionedStudentPlanner.getTaskList());
        this.userPrefs = userPrefs;
    }

    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyTaskBook studentPlanner, UserPrefs userPrefs) {
        super();


        requireAllNonNull(studentPlanner, userPrefs);
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        versionedAddressBook = new VersionedAddressBook(addressBook);
        filteredPersons = new FilteredList<>(versionedAddressBook.getPersonList());
        this.userPrefs = userPrefs;

        versionedStudentPlanner = new VersionedTaskBook(studentPlanner);
        filteredTasks = new FilteredList<>(versionedStudentPlanner.getTaskList());
        this.userPrefs = userPrefs;
    }

    public ModelManager() {
        this(new AddressBook(), new TaskBook(), new ExpenseBook(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        versionedAddressBook.resetData(newData);
        indicateAddressBookChanged();
    }

    @Override
    public void resetData(ReadOnlyExpenseBook newData) {
        versionedExpenseBook.resetData(newData);
        indicateExpenseBookChanged();
    }


    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return versionedAddressBook;
    }

    @Override
    public UserPrefs getUserPrefs() {
        return userPrefs;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateAddressBookChanged() {
        raise(new AddressBookChangedEvent(versionedAddressBook));
    }

    //@@author QzSG
    /** Raises an event to indicate the model has changed with custom message*/
    private void indicateAddressBookChanged(String message) {
        raise(new AddressBookChangedEvent(versionedAddressBook));
        raise(new NewResultAvailableEvent(message));
    }

    /** Raises an event to indicate the request to backup model to persistent storage*/
    private void indicateAddressBookBackupRequest(Path backupPath) {
        raise(new AddressBookLocalBackupEvent(versionedAddressBook, backupPath));
    }

    //@@author

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return versionedAddressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        versionedAddressBook.removePerson(target);
        indicateAddressBookChanged();
    }

    @Override
    public void addPerson(Person person) {
        versionedAddressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

    @Override
    public void updatePerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        versionedAddressBook.updatePerson(target, editedPerson);
        indicateAddressBookChanged();
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return FXCollections.unmodifiableObservableList(filteredPersons);
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    //=========== Undo/Redo =================================================================================

    @Override
    public boolean canUndoAddressBook() {
        return versionedAddressBook.canUndo();
    }

    @Override
    public boolean canRedoAddressBook() {
        return versionedAddressBook.canRedo();
    }

    @Override
    public void undoAddressBook() {
        versionedAddressBook.undo();
        indicateAddressBookChanged();
    }

    @Override
    public void redoAddressBook() {
        versionedAddressBook.redo();
        indicateAddressBookChanged();
    }

    @Override
    public void commitAddressBook() {
        versionedAddressBook.commit();
    }

    //@@author QzSG
    @Override
    public void backupAddressBookLocal(Path backupPath) {
        indicateAddressBookBackupRequest(backupPath);
    }

    @Override
    public void backupExpenseBookLocal(Path backupPath) {
        indicateExpenseBookBackupRequest(backupPath);
    }


    @Override
    public void restoreAddressBook(ReadOnlyAddressBook restoredAddressBook) {
        versionedAddressBook.resetData(restoredAddressBook);
        Platform.runLater(() -> indicateAddressBookChanged("Data Restored"));
    }

    @Override
    public void restoreExpenseBook(ReadOnlyExpenseBook restoredExpenseBook) {
        versionedExpenseBook.resetData(restoredExpenseBook);
        Platform.runLater(() -> indicateExpenseBookChanged("Data Restored"));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleAddressBookLocalRestoreEvent(AddressBookLocalRestoreEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Restoring address book from local storage"));
        restoreAddressBook(event.readOnlyAddressBook);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleExpenseBookLocalRestoreEvent(ExpenseBookLocalRestoreEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Restoring expense book from local storage"));
        restoreExpenseBook(event.readOnlyExpenseBook);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleAddressBookOnlineRestoreEvent(AddressBookOnlineRestoreEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Restoring address book from online storage"));
        restoreAddressBook(event.data);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleOnlineBackupSuccessResultEvent(OnlineBackupSuccessResultEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Restoring address book from online storage"));
        handleOnlineBackupSuccessResult(event.target, event.targetBook, event.ref);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleExpenseBookOnlineRestoreEvent(ExpenseBookOnlineRestoreEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Restoring expense book from online storage"));
        restoreExpenseBook(event.data);
    }

    /**
     * Processes the success callback object returned from {@code OnlineBackupSuccessResultEvent}. Updates the relevant
     * fields in UserPreferences and raises an event to Storage Manager.
     * @param target {@code OnlineStorage.Type}
     * @param ref Reference object returned from successful online backup callback
     */
    private void handleOnlineBackupSuccessResult(OnlineStorage.Type target, UserPrefs.TargetBook targetBook,
                                                 String ref) {
        switch (target) {
        case GITHUB:
        default:
            updateGithubRelevantUserPrefs(targetBook, ref);
        }
        raise(new UserPrefsChangedEvent(userPrefs));
    }

    /**
     * Updates the relevant fields inside User Preferences based on the {@code targetBook}
     * @param targetBook AddressBook, ExpenseBook, etc
     * @param ref Reference Field depending on online service
     */
    private void updateGithubRelevantUserPrefs(UserPrefs.TargetBook targetBook, String ref) {
        switch (targetBook) {
        case AddressBook:
            userPrefs.setAddressBookGistId(ref);
            break;
        case ExpenseBook:
            userPrefs.setExpenseBookGistId(ref);
            break;
        default:
            throw (new IllegalStateException("Reached illegal flow of code."));
        }
    }
    //@@author

    //@@author luhan02
    @Override
    public boolean hasTask(Task task) {
        requireNonNull(task);
        return versionedStudentPlanner.hasTask(task);
    }

    @Override
    public void deleteTask(Task target) {
        versionedStudentPlanner.removeTask(target);
    }

    @Override
    public void addTask(Task task) {
        versionedStudentPlanner.addTask(task);
        updateFilteredTaskList(PREDICATE_SHOW_ALL_TASKS);
    }

    @Override
    public void updateTask(Task target, Task editedPerson) {

    }

    @Override
    public ObservableList<Task> getFilteredTaskList() {
        return FXCollections.unmodifiableObservableList(filteredTasks);
    }

    @Override
    public void updateFilteredTaskList(Predicate<Task> predicate) {
        requireNonNull(predicate);
        filteredTasks.setPredicate(predicate);
    }
    @Override
    public void commitStudentPlanner() {
        versionedStudentPlanner.commit();
    }
    //@@author

    @Override
    public boolean hasEvent(Event event) {
        requireNonNull(event);
        return false;
    }

    @Override
    public void addEvent(Event event) {

    }

    @Override
    public void deleteEvent(Event target) {

    }

    @Override
    public ObservableList<Event> getFilteredEventList() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return versionedAddressBook.equals(other.versionedAddressBook)
                && filteredPersons.equals(other.filteredPersons);
    }

    //@@author ChenSongJian
    //=========== Expense =================================================================================

    @Override
    public ReadOnlyExpenseBook getExpenseBook() {
        return versionedExpenseBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateExpenseBookChanged() {
        raise(new ExpenseBookChangedEvent(versionedExpenseBook));
    }

    //@@author QzSG
    /** Raises an event to indicate the model has changed with custom message*/
    private void indicateExpenseBookChanged(String message) {
        raise(new ExpenseBookChangedEvent(versionedExpenseBook));
        raise(new NewResultAvailableEvent(message));
    }

    /** Raises an event to indicate the request to backup model to persistent storage*/
    private void indicateExpenseBookBackupRequest(Path backupPath) {
        raise(new ExpenseBookLocalBackupEvent(versionedExpenseBook, backupPath));
    }

    //@@author ChenSongJian

    @Override
    public void addExpense(Expense expense) {
        versionedExpenseBook.addExpense(expense);
        updateFilteredExpenseList(PREDICATE_SHOW_ALL_EXPENSES);
        indicateExpenseBookChanged();
    }

    @Override
    public void deleteExpense(Expense target) {
        versionedExpenseBook.removeExpense(target);
        indicateExpenseBookChanged();
    }

    @Override
    public void updateExpense(Expense target, Expense editedExpense) {
        requireAllNonNull(target, editedExpense);

        versionedExpenseBook.updateExpense(target, editedExpense);
        indicateExpenseBookChanged();
    }

    /**
     * Returns an unmodifiable view of the list of {@code Expense} backed by the internal list of
     * {@code versionedExpenseBook}
     */
    @Override
    public ObservableList<Expense> getFilteredExpenseList() {
        return FXCollections.unmodifiableObservableList(filteredExpenses);
    }

    @Override
    public void updateFilteredExpenseList(Predicate<Expense> predicate) {
        requireNonNull(predicate);
        filteredExpenses.setPredicate(predicate);
    }

    @Override
    public boolean canUndoExpenseBook() {
        return versionedExpenseBook.canUndo();
    }

    @Override
    public boolean canRedoExpenseBook() {
        return versionedExpenseBook.canRedo();
    }

    @Override
    public void undoExpenseBook() {
        versionedExpenseBook.undo();
        indicateExpenseBookChanged();
    }

    @Override
    public void redoExpenseBook() {
        versionedExpenseBook.redo();
        indicateExpenseBookChanged();
    }

    @Override
    public void commitExpenseBook() {
        versionedExpenseBook.commit();
    }
    //@@author
}
