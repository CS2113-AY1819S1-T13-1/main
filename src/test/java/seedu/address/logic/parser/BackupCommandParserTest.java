package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import seedu.address.logic.commands.BackupCommand;
import seedu.address.storage.OnlineStorage;

//@@author QzSG
public class BackupCommandParserTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private BackupCommandParser parser = new BackupCommandParser();

    BackupCommand expectedValidOnlineBackupCommand =
            new BackupCommand(Optional.empty(), false,
                    Optional.ofNullable(OnlineStorage.Type.GITHUB),
                    Optional.ofNullable("VALID_TOKEN"));


    @Test
    public void parse_emptyArg_parsesPasses() {
        BackupCommand expectedBackupCommand =
                new BackupCommand(Optional.empty(), true, Optional.empty(), Optional.empty());

        assertParseSuccess(parser, "  ", expectedBackupCommand);
    }

    @Test
    public void parse_onlineBack_noTokenArg_throws() {
        thrown.expect(IllegalArgumentException.class);
        assertParseSuccess(parser, " github", expectedValidOnlineBackupCommand);
    }

    @Test
    public void parse_onlineBack_hasTokenArg_parsesPasses() {
        assertParseSuccess(parser, " github AUTH_TOKEN", expectedValidOnlineBackupCommand);
    }

    @Test
    public void parse_invalidArgs_parsesFails() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, BackupCommand.MESSAGE_USAGE);

        Path tempBackupFilePath = testFolder.getRoot().toPath().resolve("Temp.bak");
        // invalid arguments
        assertParseFailure(parser, tempBackupFilePath.toString(), expectedMessage);

    }
}
