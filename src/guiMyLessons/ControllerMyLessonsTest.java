package guiMyLessons;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * JUnit tests for the My Lessons page Open action.
 *
 * TC1 checks that trying to open without selecting an index displays the proper message.
 * TC2 checks the first valid lesson.
 * TC3 checks the last valid lesson.
 * TC4 checks an out of bounds input
 * TC5 checks mismatched index and ID information by providing no ID information
 *
 *
 * TC4 and TC5 currently detect an IndexOutOfBoundsException in the controller.
 * A passing test in those two cases means the defect was reproduced as expected,
 * not that the controller handles the invalid selection correctly.
 */
public class ControllerMyLessonsTest {

    private static Database db;

    private Stage stage;
    private User testUser;
    private final List<Integer> createdLessonIds = new ArrayList<>();

    /**
     * Opens the database connection for all tests to access.
     */
    @BeforeAll
    public static void setUpDatabase() throws Exception {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }

        Platform.setImplicitExit(false);

        db = FoundationsMain.database;
        db.connectToDatabase();
    }

    /**
     * Closes the database connection after all tests have finished.
     */
    @AfterAll
    public static void closeDatabase() {
        if (db != null) {
            db.closeConnection();
        }
        Platform.exit();
    }

    /**
     * Sets ups fresh user and stage for each test
     */
    @BeforeEach
    public void setUpTests() throws Exception {
        createdLessonIds.clear();

        testUser = new User(
                "MyLessonsTest" + System.nanoTime(), "testPassword", "Test", "", "Contributor", "Test", "test@example.com",
                false, true, false, false
        );

        checkFX(() -> {
            stage = new Stage();

            ViewMyLessons.theStage = stage;
            ViewMyLessons.theUser = testUser;
            ViewMyLessons.theDatabase = db;

            ViewMyLessons.list_Lessons.getItems().clear();
            ViewMyLessons.lessonIds.clear();
            ViewMyLessons.label_Message.setText("");

            return null;
        });
    }

    /**
     * Removes test lessons and closes the test window after each test.
     */
    @AfterEach
    public void endTests() throws Exception {
        for (int id : createdLessonIds) {
            if (db.lessonExists(id)) {
                db.deleteLesson(id);
            }
        }

        checkFX(() -> {
            if (stage != null) {
                stage.close();
            }
            return null;
        });
    }

    /**
     * Adds a lesson to the database and remembers its ID for cleanup.
     *
     * @param title : the lesson title
     * @return the ID assigned by the database
     */
    private int addTestLesson(String title) {
    	
        int id = db.addLesson(title, "Test lesson body for " + title, "Testing", testUser.getUserName());

        assertTrue(id > 0, "The test lesson should be saved successfully.");
        createdLessonIds.add(id);
        return id;
    }

    /**
     * Adds three lessons to the database and displays them in the same order in the My Lessons list.
     *
     * @return the three database IDs in display order
     * @throws Exception if the JavaFX operation fails
     */
    private List<Integer> addThreeDisplayedLessons() throws Exception {
        int firstId = addTestLesson("First Test Lesson");
        int secondId = addTestLesson("Second Test Lesson");
        int thirdId = addTestLesson("Third Test Lesson");

        List<Integer> ids = List.of(firstId, secondId, thirdId);

        checkFX(() -> {
            ViewMyLessons.list_Lessons.getItems().addAll( "First Test Lesson", "Second Test Lesson", "Third Test Lesson");
            ViewMyLessons.lessonIds.addAll(ids);
            return null;
        });

        return ids;
    }

    /**
     * TC1: Opening without a lesson selected should display the correct message and should not open the page.
     *
     * @throws Exception if the JavaFX operation fails
     */
    @Test
    public void testNoSelection() throws Exception {
        checkFX(() -> {
            ControllerMyLessons.performOpen();

            assertEquals(
                    ControllerMyLessons.NOTHING_CHOSEN,
                    ViewMyLessons.label_Message.getText()
            );

            assertNull(stage.getScene(),
                    "Nothing should open when no lesson is selected.");

            return null;
        });
    }

    /**
     * TC2: Selecting the first valid row should open the corresponding lesson's details.
     *
     * @throws Exception if the JavaFX operation fails
     */
    @Test
    public void testFirstValidSelection() throws Exception {
        addThreeDisplayedLessons();

        checkFX(() -> {
            ViewMyLessons.list_Lessons.getSelectionModel().select(0);

            ControllerMyLessons.performOpen();

            assertEquals("CSE 360 Foundation Code: LessonsLearned Details", stage.getTitle());

            List<String> displayedLabels = getLabelTexts(stage.getScene().getRoot());

            assertTrue(displayedLabels.contains("First Test Lesson"), "The detail page should display the first selected lesson.");

            return null;
        });
    }

    /**
     * TC3: Selecting the last valid row should open the corresponding lesson's details.
     *
     * @throws Exception if the JavaFX operation fails
     */
    @Test
    public void testLastValidSelection() throws Exception {
        addThreeDisplayedLessons();

        checkFX(() -> {
            ViewMyLessons.list_Lessons.getSelectionModel().select(2);

            ControllerMyLessons.performOpen();

            assertEquals("CSE 360 Foundation Code: LessonsLearned Details", stage.getTitle()
            );

            List<String> displayedLabels = getLabelTexts(stage.getScene().getRoot());

            assertTrue(displayedLabels.contains("Third Test Lesson"), "The detail page should display the last selected lesson.");

            return null;
        });
    }

    /**
     * TC4: Selecting a visible row that has no corresponding lesson ID should handle the out-of-bounds check properly.
     *
     * The current implementation is expected to throw IndexOutOfBoundsException.
     *
     * @throws Exception if the JavaFX operation fails
     */
    @Test
    public void testSelectionBeyondLessonIdList() throws Exception {
        checkFX(() -> {
            ViewMyLessons.list_Lessons.getItems().addAll(
                    "First Visible Lesson",
                    "Second Visible Lesson"
            );

            ViewMyLessons.lessonIds.add(12345);

            ViewMyLessons.list_Lessons.getSelectionModel().select(1);

            assertThrows(IndexOutOfBoundsException.class, ControllerMyLessons::performOpen);

            return null;
        });
    }

    /**
     * TC5: Selecting a visible row when the lesson ID list is empty should should handle the out-of-bounds check properly.
     *
     * The current implementation is expected to throw IndexOutOfBoundsException.
     *
     * @throws Exception if the JavaFX operation fails
     */
    @Test
    public void testSelectionWithEmptyLessonIdList() throws Exception {
        checkFX(() -> {
            ViewMyLessons.list_Lessons.getItems().add("Visible Lesson");

            ViewMyLessons.list_Lessons.getSelectionModel().select(0);

            assertThrows(IndexOutOfBoundsException.class, ControllerMyLessons::performOpen);

            return null;
        });
    }

    /**
     * Collects the text from Labels in a JavaFX scene graph.
     *
     * @param node the node to inspect
     * @return all Label text values below the supplied node
     */
    private static List<String> getLabelTexts(Node node) {
        List<String> texts = new ArrayList<>();

        if (node instanceof Label) {
            texts.add(((Label) node).getText());
        }

        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                texts.addAll(getLabelTexts(child));
            }
        }

        return texts;
    }

    /**
     * Runs a task on the JavaFX Application Thread and waits for its result.
     *
     * @param task the operation to run
     * @param <T> the operation's return type
     * @return the operation's result
     * @throws Exception if the operation fails or times out
     */
    private static <T> T checkFX(Callable<T> task) throws Exception {
        if (Platform.isFxApplicationThread()) {
            return task.call();
        }

        FutureTask<T> futureTask = new FutureTask<>(task);
        Platform.runLater(futureTask);

        return futureTask.get(10, TimeUnit.SECONDS);
    }
}
