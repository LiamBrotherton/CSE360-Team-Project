package lessonsLearnedTesting;

import database.Database;
import entityClasses.LessonLearned;
import entityClasses.LessonsLearnedList;
import inputRecognizer.LessonLearnedValidator;

/**
 * Automated testing for the HW2 Lessons Learned CRUD functionality.
 *
 * These tests cover:
 * Create
 * Read
 * Update
 * Delete
 * Search/subsets
 * Input validation
 *
 * The tests correspond to the cases documented in Test Cases.pdf.
 */
public class LessonsLearnedTestingAutomation {

	private static int passed = 0;
	private static int failed = 0;

	private static Database database;

	public static void main(String[] args) {

		System.out.println("======================================");
		System.out.println(" HW2 Lessons Learned Testing");
		System.out.println("======================================\n");

		try {

			database = new Database();
			database.connectToDatabase();

			testCreateValidLesson();
			testCreateInvalidLesson();

			testReadAllLessons();
			testReadEmptySearch();
			testReadMatchingSubset();

			testValidUpdate();
			testInvalidUpdate();
			testUpdateMissingLesson();

			testDeleteExistingLesson();
			testDeleteMissingLesson();

			testPersistence();

		}
		catch (Exception e) {

			System.out.println("Testing could not continue.");
			System.out.println("Error: " + e.getMessage());

			e.printStackTrace();
		}

		System.out.println();
		System.out.println("======================================");
		System.out.println(" Testing Complete");
		System.out.println("======================================");
		System.out.println("Passed: " + passed);
		System.out.println("Failed: " + failed);
		System.out.println("Total:  " + (passed + failed));
		System.out.println("======================================");
	}

	/*
	 * TEST 1
	 * Create a valid lesson learned.
	 */
	private static void testCreateValidLesson() {

		System.out.println("Test 1 - Create Valid Lesson");

		try {

			LessonLearned lesson = new LessonLearned(
					"JavaFX Setup",
					"JavaFX requires the correct VM arguments before the application can run.",
					"JavaFX",
					"javafx,eclipse,setup",
					"Hassan"
			);

			String error = LessonLearnedValidator.validateLesson(
					lesson.getTitle(),
					lesson.getDescription(),
					lesson.getCategory(),
					lesson.getTags(),
					lesson.getAuthor()
			);

			if (!error.isEmpty()) {

				fail("Valid lesson was rejected: " + error);
				return;
			}

			boolean result = database.createLesson(lesson);

			if (result) {
				pass();
			}
			else {
				fail("Valid lesson was not created.");
			}

		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/*
	 * TEST 2
	 * Reject a lesson with missing required information.
	 */
	private static void testCreateInvalidLesson() {

		System.out.println("\nTest 2 - Create With Missing Information");

		String error = LessonLearnedValidator.validateLesson(
				"",
				"A lesson description",
				"Java",
				"testing",
				"Hassan"
		);

		if (!error.isEmpty()) {

			System.out.println("Expected error: " + error);
			pass();
		}
		else {

			fail("Empty title was accepted.");
		}
	}

	/*
	 * TEST 3
	 * Read all stored lessons.
	 */
	private static void testReadAllLessons() {

		System.out.println("\nTest 3 - Read All Lessons");

		try {

			LessonsLearnedList lessons =
					database.getAllLessons();

			if (lessons != null && lessons.size() > 0) {

				System.out.println(
						"Lessons returned: " + lessons.size());

				pass();
			}
			else {

				fail("No stored lessons were returned.");
			}

		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/*
	 * TEST 4
	 * Search that produces an empty subset.
	 */
	private static void testReadEmptySearch() {

		System.out.println("\nTest 4 - Read Empty Search Result");

		try {

			LessonsLearnedList results =
					database.searchLessons(
							"THIS_SHOULD_NOT_EXIST_12345");

			if (results != null && results.isEmpty()) {

				System.out.println(
						"Expected message: No lessons learned were found.");

				pass();
			}
			else {

				fail("Expected an empty search result.");
			}

		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/*
	 * TEST 5
	 * Return a subset of matching lessons.
	 */
	private static void testReadMatchingSubset() {

		System.out.println("\nTest 5 - Read Matching Subset");

		try {

			LessonsLearnedList results =
					database.searchLessons("JavaFX");

			if (results != null && results.size() > 0) {

				System.out.println(
						"Matching lessons: " + results.size());

				pass();
			}
			else {

				fail("Expected at least one matching lesson.");
			}

		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/*
	 * TEST 6
	 * Update an existing lesson using valid information.
	 */
	private static void testValidUpdate() {

		System.out.println("\nTest 6 - Valid Update");

		try {

			LessonsLearnedList lessons =
					database.searchLessons("JavaFX Setup");

			if (lessons.isEmpty()) {

				fail("Lesson needed for update test was not found.");
				return;
			}

			LessonLearned lesson = lessons.getLesson(0);

			lesson.setDescription(
					"JavaFX requires the proper VM arguments and module path.");

			String error =
					LessonLearnedValidator.validateLesson(
							lesson.getTitle(),
							lesson.getDescription(),
							lesson.getCategory(),
							lesson.getTags(),
							lesson.getAuthor()
					);

			if (!error.isEmpty()) {

				fail(error);
				return;
			}

			boolean result = database.updateLesson(lesson);

			if (result) {
				pass();
			}
			else {
				fail("Lesson was not updated.");
			}

		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/*
	 * TEST 7
	 * Reject an invalid update.
	 */
	private static void testInvalidUpdate() {

		System.out.println("\nTest 7 - Invalid Update");

		String error =
				LessonLearnedValidator.validateLesson(
						"JavaFX Setup",
						"",
						"JavaFX",
						"javafx",
						"Hassan"
				);

		if (!error.isEmpty()) {

			System.out.println("Expected error: " + error);
			pass();
		}
		else {

			fail("Invalid update was accepted.");
		}
	}

	/*
	 * TEST 8
	 * Attempt to update a lesson that does not exist.
	 */
	private static void testUpdateMissingLesson() {

		System.out.println("\nTest 8 - Update Missing Lesson");

		try {

			LessonLearned lesson =
					new LessonLearned(
							999999,
							"Missing Lesson",
							"This lesson does not exist.",
							"Testing",
							"missing",
							"Hassan"
					);

			boolean result =
					database.updateLesson(lesson);

			if (!result) {

				System.out.println(
						"Expected error: Lesson learned was not found.");

				pass();
			}
			else {

				fail("A nonexistent lesson was updated.");
			}

		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/*
	 * TEST 9
	 * Delete an existing lesson.
	 */
	private static void testDeleteExistingLesson() {

		System.out.println("\nTest 9 - Delete Existing Lesson");

		try {

			LessonLearned temporaryLesson =
					new LessonLearned(
							"Delete Test Lesson",
							"This lesson exists only for the delete test.",
							"Testing",
							"delete,test",
							"Hassan"
					);

			boolean created =
					database.createLesson(temporaryLesson);

			if (!created) {

				fail("Could not create lesson for delete test.");
				return;
			}

			LessonsLearnedList results =
					database.searchLessons("Delete Test Lesson");

			if (results.isEmpty()) {

				fail("Created lesson could not be found.");
				return;
			}

			int id = results.getLesson(0).getId();

			boolean deleted =
					database.deleteLesson(id);

			if (deleted) {

				pass();
			}
			else {

				fail("Existing lesson was not deleted.");
			}

		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/*
	 * TEST 10
	 * Delete a lesson that does not exist.
	 */
	private static void testDeleteMissingLesson() {

		System.out.println("\nTest 10 - Delete Missing Lesson");

		try {

			boolean result =
					database.deleteLesson(999999);

			if (!result) {

				System.out.println(
						"Expected error: Lesson learned was not found.");

				pass();
			}
			else {

				fail("Nonexistent lesson was reported as deleted.");
			}

		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/*
	 * TEST 11
	 * Verify that stored data can be retrieved again.
	 *
	 * Since the Foundations Database is persistent, retrieving the
	 * previously created lesson demonstrates that it remains stored
	 * independently of the LessonLearned object originally created.
	 */
	private static void testPersistence() {

		System.out.println("\nTest 11 - Persistence");

		try {

			LessonsLearnedList results =
					database.searchLessons("JavaFX Setup");

			if (results != null && !results.isEmpty()) {

				LessonLearned stored =
						results.getLesson(0);

				System.out.println(
						"Retrieved stored lesson: "
						+ stored.getTitle());

				pass();
			}
			else {

				fail("Stored lesson could not be retrieved.");
			}

		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private static void pass() {

		System.out.println("PASS");
		passed++;
	}

	private static void fail(String message) {

		System.out.println("FAIL");
		System.out.println("Reason: " + message);
		failed++;
	}
}