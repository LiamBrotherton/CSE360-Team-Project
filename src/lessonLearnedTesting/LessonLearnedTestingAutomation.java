package lessonLearnedTesting;

import database.Database;
import entityClasses.LessonLearned;
import entityClasses.LessonLearnedList;

/*******
 * <p> Title: LessonLearnedTestingAutomation Class. </p>
 *
 * <p> Description: Automated testing for the Lesson Learned CRUD and input-validation
 * requirements. The tests exercise the same Foundation Database methods used by the
 * application rather than a second implementation of those operations.
 *
 * The test data is disposable and uniquely named so repeated executions can run against
 * the persistent Foundation database without assuming that the database is empty.
 *
 * The validation tests intentionally use the documented lesson-field limits:
 * title VARCHAR(255) and description VARCHAR(5000). Empty title and description values
 * are also tested because the HW2 User Story requires invalid inputs to be rejected.
 *
 *
 * </p>
 *
 * @author Alexis Wakefield
 *
 * @version 2.00 2026-10-05 
 */

public class LessonLearnedTestingAutomation {

	private static Database database;

	private static int passed = 0;
	private static int failed = 0;

	private static int lessonID;
	private static int secondLessonID;
	private static String contributorOne;
	private static String contributorTwo;

	private static final int MAX_TITLE_LENGTH = 255;
	private static final int MAX_DESCRIPTION_LENGTH = 5000;

	public static void main(String[] args) {

		System.out.println("========================================");
		System.out.println("LESSON LEARNED AUTOMATED TESTING");
		System.out.println("Tester: Lesson Learned CRUD and Validation Tests");
		System.out.println("========================================");

		try {
			database = new Database();
			database.connectToDatabase();

			long number = System.currentTimeMillis() % 100000;
			contributorOne = "LessonTesterOne" + number;
			contributorTwo = "LessonTesterTwo" + number;

			section("CREATE TESTS");
			testCreate();

			section("READ TESTS");
			testRead();

			section("UPDATE TESTS");
			testUpdate();

			section("DELETE TESTS");
			testDelete();

			section("INPUT VALIDATION TESTS");
			testValidation();

		} catch (Exception e) {
			System.out.println("Unexpected exception while executing the test suite:");
			e.printStackTrace();
			failed++;
		} finally {
			cleanup();

			System.out.println("\n========================================");
			System.out.println("TEST SUMMARY");
			System.out.println("Passed: " + passed);
			System.out.println("Failed: " + failed);
			System.out.println("========================================");
		}
	}

	/*******
	 * <p> Method: testCreate() </p>
	 *
	 * <p> Description: Tests successful creation of one or more valid lessons. Multiple
	 * records are required because the assignment explicitly permits arbitrarily large
	 * subsets rather than a list containing only one lesson. </p>
	 */
	private static void testCreate() {

		LessonLearned lesson = new LessonLearned(
				0,
				"Database Performance",
				"Adding indexes improved query performance.",
				contributorOne);

		boolean created = database.addLesson(lesson);

		checkTest("Test C1 - Create Valid Lesson", created, true);

		LessonLearnedList contributorLessons =
				database.getContributorLessons(contributorOne);

		if (contributorLessons.getSize() > 0) {
			lessonID = contributorLessons.getLesson(0).getLessonID();
		}

		checkTest("Test C2 - Created Lesson Appears In Contributor List",
				contributorLessons.getSize() >= 1, true);

		LessonLearned secondLesson = new LessonLearned(
				0,
				"Communication Planning",
				"Frequent status updates reduced duplicated work.",
				contributorOne);

		boolean secondCreated = database.addLesson(secondLesson);

		checkTest("Test C3 - Create Multiple Valid Lessons", secondCreated, true);

		contributorLessons = database.getContributorLessons(contributorOne);

		if (contributorLessons.getSize() >= 2) {
			secondLessonID =
					contributorLessons.getLesson(1).getLessonID();
		}

		checkTest("Test C4 - Multiple Lessons Are Stored In The Subset",
				contributorLessons.getSize() >= 2, true);

		LessonLearned boundaryLesson = new LessonLearned(
				0,
				repeat('T', MAX_TITLE_LENGTH),
				repeat('D', MAX_DESCRIPTION_LENGTH),
				contributorTwo);

		boolean boundaryCreated = database.addLesson(boundaryLesson);

		checkTest("Test C5 - Maximum-Length Valid Lesson", boundaryCreated, true);
	}

	/*******
	 * <p> Method: testRead() </p>
	 *
	 * <p> Description: Tests retrieval of existing lessons, empty subsets, missing records,
	 * contributor ownership filtering, and persistence across a new Database connection.
	 * </p>
	 */
	private static void testRead() {

		LessonLearned readLesson = database.getLesson(lessonID);

		checkTest("Test R1 - Retrieve Existing Lesson",
				readLesson != null, true);

		if (readLesson != null) {
			checkTest("Test R2 - Retrieved Title Is Correct",
			        "Database Performance".equals(readLesson.getTitle()), true);

			checkTest("Test R3 - Retrieved Description Is Correct",
			        "Adding indexes improved query performance.".equals(readLesson.getDescription()), true);

			checkTest("Test R4 - Retrieved Creator Is Correct",
			        contributorOne.equals(readLesson.getCreatorUsername()), true);
		}

		int nonexistentLessonID = 999999;

		boolean nonexistentLessonExists =
		        database.getLesson(nonexistentLessonID) != null;

		checkTest(
		        "R5 - Nonexistent Lesson Is Not Retrieved",
		        nonexistentLessonExists,
		        false);

		LessonLearnedList contributorLessons =
		        database.getContributorLessons(contributorOne);

		boolean onlyOwnLessons = true;

		for (int i = 0; i < contributorLessons.getSize(); i++) {
			LessonLearned lesson = contributorLessons.getLesson(i);

			if (!contributorOne.equals(lesson.getCreatorUsername())) {
				onlyOwnLessons = false;
				break;
			}
		}

		checkTest("Test R6 - Contributor Sees Only Own Lessons",
				onlyOwnLessons, true);

		LessonLearnedList emptyLessons =
				database.getContributorLessons("NoSuchContributor_" +
						System.currentTimeMillis());

		checkTest("Test R7 - Empty Contributor List Is Valid",
				emptyLessons != null && emptyLessons.getSize() == 0, true);

		database.closeConnection();

		database = new Database();

		try {
			database.connectToDatabase();
		} catch (Exception e) {
			System.out.println("Could not reconnect for persistence test.");
			e.printStackTrace();

			checkTest("Test R8 - Lesson Persists Across Database Reconnection",
					false, true);

			return;
		}

		LessonLearned persistedLesson = database.getLesson(lessonID);

		checkTest("Test R8 - Lesson Persists Across Database Reconnection",
				persistedLesson != null, true);
	}

	/*******
	 * <p> Method: testUpdate() </p>
	 *
	 * <p> Description: Tests successful updates and negative update requests for nonexistent
	 * records and invalid lesson fields. </p>
	 */
	private static void testUpdate() {

		LessonLearned updatedLesson = new LessonLearned(
				lessonID,
				"Updated Database Performance",
				"Updated description after additional experience.",
				contributorOne);

		boolean updated = database.updateLesson(updatedLesson);

		checkTest("Test U1 - Update Existing Lesson", updated, true);

		LessonLearned verifyUpdate = database.getLesson(lessonID);

		checkTest("Test U2 - Updated Lesson Can Be Read Back",
				verifyUpdate != null
						&& "Updated Database Performance".equals(verifyUpdate.getTitle())
						&& "Updated description after additional experience."
								.equals(verifyUpdate.getDescription()),
				true);

		boolean updateMissing = database.updateLesson(
				new LessonLearned(
						-1,
						"Missing",
						"Does not exist",
						contributorOne));

		checkTest("Test U3 - Update Nonexistent Lesson",
				updateMissing, false);

		boolean updateEmptyTitle = database.updateLesson(
				new LessonLearned(
						lessonID,
						"",
						"Valid description",
						contributorOne));

		checkTest("Test U4 - Update With Empty Title Is Rejected",
				updateEmptyTitle, false);

		boolean updateEmptyDescription = database.updateLesson(
				new LessonLearned(
						lessonID,
						"Valid title",
						"",
						contributorOne));

		checkTest("Test U5 - Update With Empty Description Is Rejected",
				updateEmptyDescription, false);

		boolean updateLongTitle = database.updateLesson(
				new LessonLearned(
						lessonID,
						repeat('X', MAX_TITLE_LENGTH + 1),
						"Valid description",
						contributorOne));

		checkTest("Test U6 - Update With Overlength Title Is Rejected",
				updateLongTitle, false);

		boolean updateLongDescription = database.updateLesson(
				new LessonLearned(
						lessonID,
						"Valid title",
						repeat('X', MAX_DESCRIPTION_LENGTH + 1),
						contributorOne));

		checkTest("Test U7 - Update With Overlength Description Is Rejected",
				updateLongDescription, false);
	}

	/*******
	 * <p> Method: testDelete() </p>
	 *
	 * <p> Description: Tests deletion of existing and nonexistent records. Confirmation
	 * dialogs are a GUI responsibility and are not tested by this database-level automation. 
	 */
	private static void testDelete() {

	    boolean deleted = database.deleteLesson(secondLessonID);

	    checkTest("Test D1 - Delete Existing Lesson", deleted, true);

	    boolean deletedAgain = database.deleteLesson(secondLessonID);

	    checkTest("Test D2 - Delete Nonexistent Lesson", deletedAgain, false);

	    boolean deletedLessonExists =
	            database.getLesson(secondLessonID) != null;

	    checkTest("Test D3 - Deleted Lesson Is Not Retrievable", deletedLessonExists, false);
	}

	/*******
	 * <p> Method: testValidation() </p>
	 *
	 * <p> Description: Exercises invalid values through the same persistence methods used
	 * by the application. Over-limit values must also be rejected. 
	 *
	 * </p>
	 */
	private static void testValidation() {

		LessonLearned emptyTitle = new LessonLearned(
				0,
				"",
				"Description with valid content.",
				contributorTwo);

		boolean emptyTitleCreated = database.addLesson(emptyTitle);

		checkTest("Test V1 - Empty Title Is Rejected",
				emptyTitleCreated, false);

		LessonLearned emptyDescription = new LessonLearned(
				0,
				"Valid Title",
				"",
				contributorTwo);

		boolean emptyDescriptionCreated =
				database.addLesson(emptyDescription);

		checkTest("Test V2 - Empty Description Is Rejected",
				emptyDescriptionCreated, false);

		LessonLearned overTitle = new LessonLearned(
				0,
				repeat('T', MAX_TITLE_LENGTH + 1),
				"Valid description.",
				contributorTwo);

		boolean overTitleCreated = database.addLesson(overTitle);

		checkTest("Test V3 - Title Over Maximum Length Is Rejected",
				overTitleCreated, false);

		LessonLearned overDescription = new LessonLearned(
				0,
				"Valid Title",
				repeat('D', MAX_DESCRIPTION_LENGTH + 1),
				contributorTwo);

		boolean overDescriptionCreated =
				database.addLesson(overDescription);

		checkTest("Test V4 - Description Over Maximum Length Is Rejected",
				overDescriptionCreated, false);

		
		System.out.println();
		String errorMessage = database.getLastLessonErrorMessage();

		boolean helpfulMessage =
		        errorMessage != null
		        && !errorMessage.trim().isEmpty();

		checkTest(
		        "Test V5 - Helpful Validation Error Message",
		        helpfulMessage,
		        true);

		System.out.println("Message returned by application:");
		System.out.println(errorMessage);
	}

	/*******
	 * <p> Method: cleanup() </p>
	 *
	 * <p> Description: Removes only the disposable lessons created by this test run.
	 * The persistent database may contain real project data, so the automation never drops
	 * the entire database or deletes records belonging to another user.
	 */
	private static void cleanup() {

		if (database == null) {
			return;
		}

		try {
			LessonLearnedList cleanupOne =
					database.getContributorLessons(contributorOne);

			for (int i = 0; i < cleanupOne.getSize(); i++) {
				database.deleteLesson(
						cleanupOne.getLesson(i).getLessonID());
			}

			LessonLearnedList cleanupTwo =
					database.getContributorLessons(contributorTwo);

			for (int i = 0; i < cleanupTwo.getSize(); i++) {
				database.deleteLesson(
						cleanupTwo.getLesson(i).getLessonID());
			}

		} catch (Exception e) {
			System.out.println("Cleanup failed.");
			e.printStackTrace();
		} finally {
			database.closeConnection();
		}
	}

	/*******
	 * <p> Method: checkTest() </p>
	 *
	 * <p> Description: Compares an actual result against the expected result and records
	 * whether the test passed or failed. Overloads support both boolean and object values
	 * while keeping the output format consistent.
	 */
	private static void checkTest(String testName, boolean actual, boolean expected) {

		System.out.println();
		System.out.println(testName);
		System.out.println("Expected: " + expected);
		System.out.println("Actual:   " + actual);

		if (actual == expected) {
			System.out.println("Result:   PASS");
			passed++;
		} else {
			System.out.println("Result:   FAIL");
			failed++;
		}
	}

	private static void checkTest(String testName, String actual, String expected) {

		System.out.println();
		System.out.println(testName);
		System.out.println("Expected: " + expected);
		System.out.println("Actual:   " + actual);

		if (expected == null ? actual == null : expected.equals(actual)) {
			System.out.println("Result:   PASS");
			passed++;
		} else {
			System.out.println("Result:   FAIL");
			failed++;
		}
	}

	/*******
	 * <p> Method: section() </p>
	 *
	 * <p> Description: Displays a heading for a group of related tests so the console
	 * output is easy to follow.
	 */
	private static void section(String title) {

		System.out.println();
		System.out.println("----------------------------------------");
		System.out.println(title);
		System.out.println("----------------------------------------");
	}

	/*******
	 * <p> Method: repeat() </p>
	 *
	 * <p> Description: Creates deterministic boundary-test input without embedding hundreds
	 * or thousands of repetitive characters directly in the test source.
	 */
	private static String repeat(char character, int count) {

		StringBuilder value = new StringBuilder(count);

		for (int i = 0; i < count; i++) {
			value.append(character);
		}

		return value.toString();
	}
}
