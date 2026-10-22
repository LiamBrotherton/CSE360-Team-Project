package testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.Lesson;
import modifiedGuiContributor.ModifiedModelContributorHome;
import java.util.List;

public class ContributorHomeTest {

    private Database db;
    // Updated test profile names
    private final String TEST_CONTRIBUTOR = "Jack";
    private final String OTHER_CONTRIBUTOR = "Jill";

    @BeforeEach
    public void setUp() throws Exception { // <--- Added "throws Exception" here
        if (applicationMain.FoundationsMain.database == null) {
            applicationMain.FoundationsMain.database = new database.Database();
        }
        
        db = applicationMain.FoundationsMain.database;
        
        // This will now compile perfectly without the unhandled exception error
        db.connectToDatabase();
        
        db.clearAllLessons();
    }

    /**
     * Requirement Test: Display all lessons created by the contributor.
     */
    @Test
    public void testGetLessonsByContributor() {
        Lesson lesson1 = new Lesson(101, "Java Basics", "Intro text", "Coding", false, false, false, null, null, null, null);
        Lesson lesson2 = new Lesson(102, "Java FX", "GUI text", "Coding", false, false, false, null, null, null, null);
        Lesson lesson3 = new Lesson(103, "Python Data", "Data text", "Scripting", false, false, false, null, null, null, null);

        // Jack inserts two records; Jill inserts one
        db.insertLesson(lesson1, TEST_CONTRIBUTOR);
        db.insertLesson(lesson2, TEST_CONTRIBUTOR);
        db.insertLesson(lesson3, OTHER_CONTRIBUTOR);

        // Load the lessons via the model framework
        ModifiedModelContributorHome.loadLessons(TEST_CONTRIBUTOR);
        List<Lesson> jackLessons = ModifiedModelContributorHome.getContributorLessons();

        // Verification: Jack should see exactly his 2 lessons, completely filtering out Jill's
        assertEquals(2, jackLessons.size(), "Should load exactly 2 lessons for the active contributor.");
        assertEquals("Java Basics", jackLessons.get(0).getTitle());
        assertEquals("Java FX", jackLessons.get(1).getTitle());
    }

    /**
     * Requirement Test: Allow contributors to select and examine individual lessons.
     */
    @Test
    public void testSelectAndExamineLesson() {
        Lesson testLesson = new Lesson(201, "UI Architecture", "MVC Layout details", "Design", false, false, false, null, null, null, null);
        
        // Simulate selecting a lesson in the UI
        ModifiedModelContributorHome.setSelectedLesson(testLesson);
        
        // Verification: Ensure the model accurately holds and serves the inspected entity
        assertNotNull(ModifiedModelContributorHome.getSelectedLesson(), "Selected lesson should not be null.");
        assertEquals(201, ModifiedModelContributorHome.getSelectedLesson().getId());
        assertEquals("UI Architecture", ModifiedModelContributorHome.getSelectedLesson().getTitle());
        assertEquals("MVC Layout details", ModifiedModelContributorHome.getSelectedLesson().getBody());
    }

    /**
     * Requirement Test: Ensure lessons with locked fields cannot be deleted.
     */
    @Test
    public void testCannotDeleteLockedLesson() {
        Lesson lockedLesson = new Lesson(301, "Secure Lesson", "Private Data", "Security", true, false, false, null, null, null, null);
        db.insertLesson(lockedLesson, TEST_CONTRIBUTOR);

        ModifiedModelContributorHome.loadLessons(TEST_CONTRIBUTOR);
        ModifiedModelContributorHome.setSelectedLesson(lockedLesson);

        assertTrue(lockedLesson.hasLockedField(), "Lesson should detect its title is locked.");

        // Attempt deletion via the model framework
        boolean deletionResult = ModifiedModelContributorHome.deleteSelectedLesson();

        // Verification: The backend logic must return false and block deletion
        assertFalse(deletionResult, "Model must reject deleting a lesson with locked fields.");
        assertTrue(db.lessonExists(301), "The locked lesson must still exist in the database table.");
    }

    /**
     * Requirement Test: Ensure lessons with NO locked fields can be successfully deleted.
     */
    @Test
    public void testCanDeleteUnlockedLesson() {
        Lesson unlockedLesson = new Lesson(401, "Draft Lesson", "Temporary text", "Sandbox", false, false, false, null, null, null, null);
        db.insertLesson(unlockedLesson, TEST_CONTRIBUTOR);

        ModifiedModelContributorHome.loadLessons(TEST_CONTRIBUTOR);
        ModifiedModelContributorHome.setSelectedLesson(unlockedLesson);

        assertFalse(unlockedLesson.hasLockedField(), "Lesson should not have any locked fields.");

        // Attempt deletion
        boolean deletionResult = ModifiedModelContributorHome.deleteSelectedLesson();

        // Verification: Unlocked data should clear cleanly
        assertTrue(deletionResult, "Model should successfully allow deleting an unlocked lesson.");
        assertFalse(db.lessonExists(401), "The unlocked lesson should no longer exist in the database table.");
    }
}