package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.LessonsLearned;
import guiLessonDetail.ModelLessonDetail;
import guiMyLessons.ModelMyLessons;

/*******
 * <p> Title: ContributorHomeTest Class. </p>
 *
 * <p> Description: JUnit tests for Contributor User Story 2, List and Examine Lessons:
 *
 * "As a contributor, I can see a list of all the lessons learned I have added to the system.  I
 * can use that list to specify which lesson learned to examine more closely.  When I examine a
 * lesson more closely, I can see the fields I can alter, and which are locked and unavailable for
 * me to edit.  (When a lesson I created is locked or has a locked field, I am unable to delete
 * that lesson.)"
 *
 * The story is shown in the application by the My Lessons page and the Lesson Details page, so
 * these tests call the Models those pages use: ModelMyLessons for the list, and ModelLessonDetail
 * for examining and deleting a lesson.
 *
 * The tests run against the real database.  Each test uses two Contributor usernames made up for
 * this run, so it never touches anyone else's lessons, and the lessons it creates are deleted
 * after it finishes.  The tests never empty the lesson table.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Ravi
 *
 * @version 1.00		2026-10-22 Initial version
 * @version 1.10		2026-10-23 Tests now call ModelMyLessons and ModelLessonDetail, use the
 * 							LessonsLearned class, and delete only the lessons they create
 */
public class ContributorHomeTest {

	/** Creates a new ContributorHomeTest. */
	public ContributorHomeTest() {}

	private Database db;

	// Made-up Contributor usernames, unique to this run so no real lessons are involved
	private final String TEST_CONTRIBUTOR = "Jack" + System.nanoTime() % 100000;
	private final String OTHER_CONTRIBUTOR = "Jill" + System.nanoTime() % 100000;

	// Every lesson a test creates, so it can be deleted afterwards
	private final List<Integer> created = new ArrayList<>();

	/**
	 * Connects to the database before each test.
	 *
	 * @throws Exception if the database cannot be opened
	 */
	@BeforeEach
	public void setUp() throws Exception {
		db = new Database();
		db.connectToDatabase();
	}

	/**
	 * Deletes the lessons the test created and closes the database after each test.
	 */
	@AfterEach
	public void tearDown() {
		for (int id : created) if (db.lessonExists(id)) db.deleteLesson(id);
		db.closeConnection();
	}

	/**
	 * Adds a lesson for a Contributor and remembers it so it is deleted after the test.
	 *
	 * @param title			the title
	 * @param body			the body
	 * @param category		the category
	 * @param contributor	the Contributor who adds it
	 *
	 * @return the new lesson's ID
	 */
	private int addLesson(String title, String body, String category, String contributor) {
		int id = db.addLesson(title, body, category, contributor);
		created.add(id);
		return id;
	}

	/**
	 * Requirement Test: Display all lessons created by the contributor.
	 */
	@Test
	public void testGetLessonsByContributor() {
		// Jack adds two lessons; Jill adds one
		addLesson("Java Basics", "Intro text", "Coding", TEST_CONTRIBUTOR);
		addLesson("Java FX", "GUI text", "Coding", TEST_CONTRIBUTOR);
		addLesson("Python Data", "Data text", "Scripting", OTHER_CONTRIBUTOR);

		// Load the lessons the way the My Lessons page does
		List<LessonsLearned> jackLessons = ModelMyLessons.lessonsFor(db, TEST_CONTRIBUTOR).getLessons();

		// Jack sees exactly his 2 lessons, in the order he added them, and none of Jill's
		assertEquals(2, jackLessons.size(), "Should load exactly 2 lessons for the active contributor.");
		assertEquals("Java Basics", jackLessons.get(0).getTitle());
		assertEquals("Java FX", jackLessons.get(1).getTitle());
	}

	/**
	 * Requirement Test: Allow contributors to select and examine individual lessons, and see which
	 * fields they can alter and which are locked.
	 */
	@Test
	public void testSelectAndExamineLesson() {
		int id = addLesson("UI Architecture", "MVC Layout details", "Design", TEST_CONTRIBUTOR);

		// Open the lesson the way the Lesson Details page does
		LessonsLearned examined = ModelLessonDetail.lessonFor(db, TEST_CONTRIBUTOR, id);

		// The lesson is found, with its core information, and no field is locked yet
		assertNotNull(examined, "Selected lesson should not be null.");
		assertEquals(id, examined.getId());
		assertEquals("UI Architecture", examined.getTitle());
		assertEquals("MVC Layout details", examined.getBody());
		assertFalse(examined.hasLockedField(), "A new lesson should have every field editable.");

		// Another contributor cannot examine it
		assertNull(ModelLessonDetail.lessonFor(db, OTHER_CONTRIBUTOR, id),
				"Another contributor should not be able to open this lesson.");
	}

	/**
	 * Requirement Test: Ensure lessons with locked fields cannot be deleted.
	 */
	@Test
	public void testCannotDeleteLockedLesson() {
		int id = addLesson("Secure Lesson", "Private Data", "Security", TEST_CONTRIBUTOR);
		db.setLessonFieldLocked(id, LessonsLearned.FIELD_TITLE, true);		// another role locks the title

		LessonsLearned lockedLesson = ModelLessonDetail.lessonFor(db, TEST_CONTRIBUTOR, id);
		assertTrue(lockedLesson.hasLockedField(), "Lesson should detect its title is locked.");
		assertTrue(lockedLesson.getTitleLocked(), "The title should show as locked.");

		// Attempt deletion the way the Lesson Details page does
		String message = ModelLessonDetail.deleteLesson(db, TEST_CONTRIBUTOR, id);

		// The deletion is refused with a reason, and the lesson is still there
		assertFalse(message.isEmpty(), "Model must reject deleting a lesson with locked fields.");
		assertTrue(db.lessonExists(id), "The locked lesson must still exist in the database table.");
	}

	/**
	 * Requirement Test: Ensure lessons with NO locked fields can be successfully deleted.
	 */
	@Test
	public void testCanDeleteUnlockedLesson() {
		int id = addLesson("Draft Lesson", "Temporary text", "Sandbox", TEST_CONTRIBUTOR);

		LessonsLearned unlockedLesson = ModelLessonDetail.lessonFor(db, TEST_CONTRIBUTOR, id);
		assertFalse(unlockedLesson.hasLockedField(), "Lesson should not have any locked fields.");

		// Attempt deletion
		String message = ModelLessonDetail.deleteLesson(db, TEST_CONTRIBUTOR, id);

		// Unlocked lessons are deleted with no message
		assertEquals("", message, "Model should successfully allow deleting an unlocked lesson.");
		assertFalse(db.lessonExists(id), "The unlocked lesson should no longer exist in the database table.");
	}
}
