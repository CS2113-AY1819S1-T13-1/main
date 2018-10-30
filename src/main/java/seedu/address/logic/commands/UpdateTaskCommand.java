package seedu.address.logic.commands;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.tag.Tag;
import seedu.address.model.task.Body;
import seedu.address.model.task.DateTime;
import seedu.address.model.task.Priority;
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskName;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BODY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRIORITY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_TASKS;

/**
 * Update a existing task content from the task book
 */
//public class UpdateTaskCommand extends Command {
//    public static final String COMMAND_WORD = "updateTask";
//
//    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Updates the details of the existing task "
//            + "by the index number used in the displayed task list. "
//            + "Existing values will be overwritten by the input values.\n"
//            + "Parameters: INDEX (must be a positive integer) "
//            + "[" + PREFIX_NAME + "NAME] "
//            + "[" + PREFIX_BODY + "BODY] "
//            + "[" + PREFIX_START + "START_DATETIME] "
//            + "[" + PREFIX_END + "END_DATETIME] "
//            + "[" + PREFIX_PRIORITY + "PRIORITY] "
//            + "[" + PREFIX_TAG + "TAG]...\n"
//            + "Example: " + COMMAND_WORD + " 1 "
//            + PREFIX_END + "1/11_1400 "
//            + PREFIX_TAG + "Hardcopy turn in";
//
//    public static final String MESSAGE_UPDATE_TASK_SUCCESS = "Updated Task: %1$s";
//    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
//
//    private final Index index;
//    private final EditTaskDescriptor editTaskDescriptor;
//
//    @Override
//    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
//        requireNonNull(model);
//        List<Task> lastShownList = model.getFilteredTaskList();
//
//        if (index.getZeroBased() >= lastShownList.size()) {
//            throw new CommandException(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
//        }
//
//        Task taskToEdit = lastShownList.get(index.getZeroBased());
//        Task editedTask = createEditedTask(taskToEdit, editTaskDescriptor);
//
//        model.updateTask(taskToEdit, editedtask);
//        model.updateFilteredTaskList(PREDICATE_SHOW_ALL_TASKS);
//        model.commitExpenseBook();
//        return new CommandResult(String.format(MESSAGE_UPDATE_TASK_SUCCESS, editedTask));
//    }
//
//    /**
//     * Creates and returns a {@code Expense} with the details of {@code expenseToEdit}
//     * edited with {@code editExpenseDescriptor}.
//     */
//    private static Task createEditedTask(Task taskToEdit, EditTaskDescriptor editTaskDescriptor) {
//        assert taskToEdit != null;
//
//        TaskName updatedTaskName = editExpenseDescriptor.getExpenseCategory()
//                .orElse(expenseToEdit.getExpenseCategory());
//        Body updatedBody = editExpenseDescriptor.getExpenseDate()
//                .orElse(expenseToEdit.getExpenseDate());
//        DateTime updatedTime = editExpenseDescriptor.getExpenseValue()
//                .orElse(expenseToEdit.getExpenseValue());
//        DateTime
//        Priority
//        Set<Tag> updatedTags = editExpenseDescriptor.getTags().orElse(expenseToEdit.getTags());
//
//        return new Expense(updatedExpenseCategory, updatedExpenseDate, updatedExpenseValue, updatedTags);
//    }
//
//    @Override
//    public boolean equals(Object other) {
//        // short circuit if same object
//        if (other == this) {
//            return true;
//        }
//
//        // instanceof handles nulls
//        if (!(other instanceof EditExpenseCommand)) {
//            return false;
//        }
//
//        // state check
//        EditExpenseCommand e = (EditExpenseCommand) other;
//        return index.equals(e.index)
//                && editExpenseDescriptor.equals(e.editExpenseDescriptor);
//    }
//
//    /**
//     * Stores the details to edit the task with. Each non-empty field value will replace the
//     * corresponding field value of task.
//     */
//    public static class EditTaskDescriptor {
//        private TaskName taskName;
//        private Body body;
//        private DateTime startDateTime;
//        private DateTime endDateTime;
//        private Priority priority;
//        private Set<Tag> tags;
//
//        public EditTaskDescriptor() {
//        }
//
//        /**
//         * Copy constructor.
//         * A defensive copy of {@code tags} is used internally.
//         */
//        public EditTaskDescriptor(EditTaskDescriptor toCopy) {
//            setTaskName(toCopy.taskName);
//            setBody(toCopy.body);
//            setStartDateTime(toCopy.startDateTime);
//            setEndDateTime(toCopy.endDateTime);
//            setPriority(toCopy.priority)
//            setTags(toCopy.tags);
//        }
//
//        /**
//         * Returns true if at least one field is edited.
//         */
//        public boolean isAnyFieldEdited() {
//            return CollectionUtil.isAnyNonNull(taskName, body, startDateTime, endDateTime, priority, tags);
//        }
//
//        public void setTaskName(TaskName taskName) {
//            this.taskName = taskName;
//        }
//
//        public Optional<TaskName> getTaskName() {
//            return Optional.ofNullable(taskName);
//        }
//
//        public void setBody(Body body) {
//            this.body = body;
//        }
//
//        public Optional<Body> getBody() {
//            return Optional.ofNullable(body);
//        }
//
//        public void setStartDateTime(DateTime startDateTime) {
//            this.startDateTime = startDateTime;
//        }
//
//        public Optional<DateTime> getStartDateTime() {
//            return Optional.ofNullable(startDateTime);
//        }
//
//        public void setEndDateTime(DateTime endDateTime) {
//            this.endDateTime = endDateTime;
//        }
//
//        public Optional<DateTime> getEndDateTime() {
//            return Optional.ofNullable(endDateTime);
//        }
//
//        public void setPriority(Priority priority) {
//            this.priority = priority;
//        }
//
//        public Optional<Priority> getPriority() {
//            return Optional.ofNullable(priority);
//        }
//
//        /**
//         * Sets {@code tags} to this object's {@code tags}.
//         * A defensive copy of {@code tags} is used internally.
//         */
//        public void setTags(Set<Tag> tags) {
//            this.tags = (tags != null) ? new HashSet<>(tags) : null;
//        }
//
//        /**
//         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
//         * if modification is attempted.
//         * Returns {@code Optional#empty()} if {@code tags} is null.
//         */
//        public Optional<Set<Tag>> getTags() {
//            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
//        }
//
//        @Override
//        public boolean equals(Object other) {
//            // short circuit if same object
//            if (other == this) {
//                return true;
//            }
//
//            // instanceof handles nulls
//            if (!(other instanceof UpdateTaskCommand.EditTaskDescriptor)) {
//                return false;
//            }
//
//            // state check
//            UpdateTaskCommand.EditTaskDescriptor e = (UpdateTaskCommand.EditTaskDescriptor) other;
//
//            return getTaskName().equals(e.getTaskName())
//                    && getBody().equals(e.getBody())
//                    && getStartDateTime().equals(e.getStartDateTime())
//                    && getEndDateTime().equals(e.getEndDateTime())
//                    && getPriority().equals(e.getPriority())
//                    && getTags().equals(e.getTags());
//        }
//    }
//}