package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.User;

/**
 * Tests the Curator's saved visibility setting and Contributor access checks.
 * Each test uses a separate temporary H2 database instead of the live application
 * database, so tests cannot overwrite existing user/lesson records or collide
 * with a running instance of the application.
 *
 * @author Hassan Mohamed
 */
public class CuratorSummaryVisibilityTest {
    @TempDir
    Path tempDirectory;

    private Database database;
    private int lessonId;
    private String testDatabaseUrl;

    private User makeUser(String username, boolean contributor, boolean curator) {
        return new User(username, "Pass1!word", "Test", "", "Person", "", 
                username + "@example.test", false, contributor, false, curator);
    }

    private final User curator = makeUser("test_curator", false, true);
    private final User owner = makeUser("test_visibility_owner", true, false);
    private final User stranger = makeUser("test_visibility_stranger", true, false);

    @BeforeEach
    public void setUp() throws SQLException {
        // Use a unique, temporary file-based DB for each JUnit test method.
        // File-based (rather than in-memory) lets the reconnection test verify
        // that the visibility state survives closing and reopening the database.
        testDatabaseUrl = "jdbc:h2:file:" + tempDirectory.resolve("visibility_test")
                .toAbsolutePath().toString();
        database = new Database(testDatabaseUrl);
        database.connectToDatabase();
        lessonId = database.addLesson("Statistics visibility test", "Test lesson", "Testing", "test_visibility_owner");
        assertTrue(lessonId > 0);
    }

    @AfterEach
    public void tearDown() {
        if (database != null) {
            if (lessonId > 0) database.deleteLesson(lessonId);
            database.closeConnection();
        }
    }

    @Test
    public void summaryIsHiddenByDefault() {
        assertTrue(database.saveCuratorSummary(lessonId, "Average: 3 hours", curator));
        assertFalse(database.isCuratorSummaryVisible(lessonId));
        assertEquals("", database.getCuratorSummaryForViewer(lessonId, owner));
        assertEquals("Average: 3 hours", database.getCuratorSummaryForViewer(lessonId, curator));
    }

    @Test
    public void curatorCanShareThenHideSummary() {
        database.saveCuratorSummary(lessonId, "Average: 3 hours", curator);
        assertTrue(database.setCuratorSummaryVisibility(lessonId, true, curator));
        assertEquals("Average: 3 hours", database.getCuratorSummaryForViewer(lessonId, owner));
        assertTrue(database.setCuratorSummaryVisibility(lessonId, false, curator));
        assertEquals("", database.getCuratorSummaryForViewer(lessonId, owner));
    }

    @Test
    public void contributorCannotChangeVisibilityAndOtherContributorsCannotRead() {
        database.saveCuratorSummary(lessonId, "Average: 3 hours", curator);
        assertFalse(database.setCuratorSummaryVisibility(lessonId, true, owner));
        assertTrue(database.setCuratorSummaryVisibility(lessonId, true, curator));
        assertEquals("", database.getCuratorSummaryForViewer(lessonId, stranger));
        assertEquals("", database.getCuratorSummaryForViewer(lessonId, null));
    }

    @Test
    public void replacementSummaryIsHiddenUntilApprovedAgain() {
        database.saveCuratorSummary(lessonId, "Old summary", curator);
        database.setCuratorSummaryVisibility(lessonId, true, curator);
        assertTrue(database.saveCuratorSummary(lessonId, "New summary", curator));
        assertFalse(database.isCuratorSummaryVisible(lessonId));
        assertEquals("", database.getCuratorSummaryForViewer(lessonId, owner));
        assertEquals("New summary", database.getCuratorSummaryForViewer(lessonId, curator));
    }

    @Test
    public void visibilitySurvivesReconnectingToDatabase() throws SQLException {
        database.saveCuratorSummary(lessonId, "Average: 3 hours", curator);
        database.setCuratorSummaryVisibility(lessonId, true, curator);
        database.closeConnection();
        database = new Database(testDatabaseUrl);
        database.connectToDatabase();
        assertTrue(database.isCuratorSummaryVisible(lessonId));
        assertEquals("Average: 3 hours", database.getCuratorSummaryForViewer(lessonId, owner));
    }
}
