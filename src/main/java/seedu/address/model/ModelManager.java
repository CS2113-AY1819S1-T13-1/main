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
import seedu.address.commons.events.ui.NewResultAvailableEvent;
import seedu.address.model.event.Event;
import seedu.address.model.person.Person;
import seedu.address.model.task.Task;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedAddressBook versionedAddressBook;
    private final FilteredList<Person> filteredPersons;
    private final UserPrefs userPrefs;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        versionedAddressBook = new VersionedAddressBook(addressBook);
        filteredPersons = new FilteredList<>(versionedAddressBook.getPersonList());
        this.userPrefs = userPrefs;
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        versionedAddressBook.resetData(newData);
        indicateAddressBookChanged();
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
    public void restoreAddressBook(ReadOnlyAddressBook restoredAddressBook) {
        versionedAddressBook.resetData(restoredAddressBook);
        Platform.runLater(() -> indicateAddressBookChanged("Address Book Data Restored"));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleAddressBookLocalRestoreEvent(AddressBookLocalRestoreEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Restoring address book from local storage"));
        restoreAddressBook(event.readOnlyAddressBook);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleAddressBookOnlineRestoreEvent(AddressBookOnlineRestoreEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Restoring address book from online storage"));
        restoreAddressBook(event.data);
    }
    //@@author

    //@@author luhan02
    @Override
    public boolean hasTask(Task task) {
        return false;
    }

    @Override
    public void deleteTask(Task target) {

    }

    @Override
    public void addTask(Task person) {

    }

    @Override
    public void updateTask(Task target, Task editedPerson) {

    }

    @Override
    public ObservableList<Task> getFilteredTaskList() {
        return null;
    }

    @Override
    public void updateFilteredTaskList(Predicate<Task> predicate) {

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

}
