package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import guiViewer.ModelViewerLessons;

/**
 * Tests the Viewer requirement to retrieve and display all lessons.
 *
 * @author Alexis
 * @version 1.00 2026-11-11 Initial prototype tests.
 */
public class ViewerLessonsTest {

    /** Database connection used by the tests. */
    private Database db;

    /** IDs of test-created lessons, retained for cleanup. */
    private final List<Integer> createdLessonIds = new ArrayList<>();

    /** Unique suffix used to avoid title collisions. */
    private final String suffix =
            Long.toString(System.nanoTime());

    /**
     * Opens the database before each test.
     *
     * @throws SQLException if the database connection fails
     */
    @BeforeEach
    public void setUp() throws SQLException {
        db = new Database();
        db.connectToDatabase();
        createdLessonIds.clear();
    }

    /**
     * Deletes test-created lessons and closes the database connection.
     */
    @AfterEach
    public void tearDown() {
        if (db != null) {
            for (int id : createdLessonIds) {
                if (db.lessonExists(id)) {
                    db.deleteLesson(id);
                }
            }

            db.closeConnection();
        }
    }

    /**
     * Creates a lesson for testing and records its ID for cleanup.
     *
     * @param title lesson title
     * @param body lesson body
     * @param category lesson category
     * @return generated database ID
     */
    private int addTestLesson(
            String title, String body, String category) {

        int id = db.addLesson(
                title + " " + suffix,
                body,
                category,
                "ViewerTestContributor");

        assertTrue(id > 0,
                "The test lesson should be inserted successfully.");

        createdLessonIds.add(id);
        return id;
    }

    /**
     * Verifies that every stored lesson is returned exactly once.
     */
    @Test
    public void testRetrievesEveryLessonWithoutDuplicates() {

        Set<Integer> expectedIds = new HashSet<>();

        for (LessonsLearned lesson :
                db.getAllLessons().getLessons()) {
            expectedIds.add(lesson.getId());
        }

        int firstId = addTestLesson(
                "Viewer browse one", "First test lesson", "Testing");

        int secondId = addTestLesson(
                "Viewer browse two", "Second test lesson", "Testing");

        int thirdId = addTestLesson(
                "Viewer browse three", "Third test lesson", "Design");

        expectedIds.add(firstId);
        expectedIds.add(secondId);
        expectedIds.add(thirdId);

        LessonsLearnedList actual =
                ModelViewerLessons.lessonsFor(db);

        List<Integer> actualIds = new ArrayList<>();

        for (LessonsLearned lesson : actual.getLessons()) {
            actualIds.add(lesson.getId());
        }

        assertEquals(expectedIds.size(), actual.size(),
                "Every lesson should appear exactly once.");

        assertEquals(expectedIds, new HashSet<>(actualIds),
                "There should be no missing or unexpected lessons.");

        assertEquals(actualIds.size(),
                new HashSet<>(actualIds).size(),
                "The result should not contain duplicate lesson IDs.");
    }

    /**
     * Verifies that lesson records are converted into display rows.
     */
    @Test
    public void testFormatsLessonRows() {

        addTestLesson(
                "Viewer row title",
                "A body for the row test",
                "Architecture");

        LessonsLearnedList lessons =
                ModelViewerLessons.lessonsFor(db);

        LessonsLearned inserted = null;

        for (LessonsLearned lesson : lessons.getLessons()) {
            if (lesson.getTitle().equals(
                    "Viewer row title " + suffix)) {
                inserted = lesson;
                break;
            }
        }

        assertNotNull(inserted,
                "The inserted lesson should be returned.");

        assertEquals(
                "Viewer row title " + suffix + "  (Architecture)",
                ModelViewerLessons.rowText(inserted));

        assertEquals(lessons.size(),
                ModelViewerLessons.rowsFor(lessons).size(),
                "There should be one display row per lesson.");
    }

    /**
     * Verifies that an empty collection produces no display rows.
     */
    @Test
    public void testEmptyLessonCollectionProducesNoRows() {

        LessonsLearnedList emptyLessons =
                new LessonsLearnedList();

        assertTrue(emptyLessons.isEmpty());

        assertEquals(0,
                ModelViewerLessons.rowsFor(emptyLessons).size());

        assertEquals(
                ModelViewerLessons.EMPTY_MESSAGE,
                emptyLessons.getMessage());
    }

    /**
     * Verifies that null inputs are rejected.
     */
    @Test
    public void testRejectsNullInputs() {

        assertThrows(
                IllegalArgumentException.class,
                () -> ModelViewerLessons.lessonsFor(null));

        assertThrows(
                IllegalArgumentException.class,
                () -> ModelViewerLessons.rowsFor(null));

        assertThrows(
                IllegalArgumentException.class,
                () -> ModelViewerLessons.rowText(null));
    }
}