package testing;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.Lesson;
import entityClasses.LessonList;
import inputRecognizer.LessonValidator;

/*******
 * <p> Title: LessonTestingAutomation Class. </p>
 *
 * <p> Description: Automated testing for the HW2 lessons learned user stories.  There is one
 * test for each test case in Test Cases.pdf, in the same order and with the same name.
 *
 * <p> The lessons already stored are copied into memory first,
 * lessonDB is emptied for the run, and the finally block puts the copied lessons back. </p>
 *
 * @version 1.00		2026-10-05 Initial version
 */
public class LessonTestingAutomation {

	/** Creates a new LessonTestingAutomation. */
	public LessonTestingAutomation() {}

	private static Database database;

	private static int passed = 0;		// Counter of the number of passed tests
	private static int failed = 0;		// Counter of the number of failed tests

	// The bullets of the current test that failed, one line each.  Empty means the test passed.
	private static StringBuilder problems = new StringBuilder();

	// The lessons that were in lessonDB before this run, so they can be put back afterward
	private static List<Lesson> originalLessons = new ArrayList<Lesson>();

	// The ID that addLessonChecked most recently saved, or -1 if it saved nothing
	private static int lastAddedId = -1;

	// An ID that is not in the database, because only a handful of lessons are ever added
	private static final int UNKNOWN_ID = 987654;

	// Real sample data: Ed Discussion post #53, posted in the General category
	private static final String TITLE = "Review Session Recording from Christian Halas";
	private static final String BODY = "I saw the message from Christian Halas on Canvas that this "
			+ "Wednesday at 2pm there will be a review session for the exam. Unfortunately, I have "
			+ "an exam in a different course scheduled for exactly that time. Can the review "
			+ "session be recorded so students unable to attend may watch it back?";
	private static final String CATEGORY = "General";

	
	public static void main(String[] args) {

		System.out.println("========================================");
		System.out.println("LESSONS LEARNED AUTOMATED TESTING");
		System.out.println("========================================");

		try {
			database = new Database();
			database.connectToDatabase();

			// Copy out the lessons already stored, then empty lessonDB for the run
			originalLessons = new ArrayList<Lesson>(database.getAllLessons().getLessons());
			System.out.println("Lessons already in the database (restored afterward): "
					+ originalLessons.size());
			database.clearAllLessons();

			testAddValid();
			testAddBlank();
			testAddTooLong();
			testLockedDefaultFlags();
			testLockedUpdate();
			testLockedDelete();
			testValidUpdate();
			testInvalidUpdate();
			testDelete();
			testLessonDoesNotExist();
			testExperienceValid();
			testExperienceBlankText();
			testExperienceInvalidDuration();
			testExperienceInvalidEffort();
			testExperienceLessonDoesNotExist();
			testListFull();
			testListEmpty();
			testExamineValid();
			testExamineInvalid();

			System.out.println("\n========================================");
			System.out.println("TEST SUMMARY");
			System.out.println("========================================");
			System.out.println("Passed: " + passed);
			System.out.println("Failed: " + failed);
			System.out.println("Total:  " + (passed + failed));
			System.out.println("========================================");

		} catch (SQLException e) {
			System.out.println("Database error while running tests:");
			e.printStackTrace();
		} finally {
			// Empty lessonDB of the test lessons, then put back the lessons that were there
			if (database != null) {
				try {
					database.clearAllLessons();
					for (Lesson l : originalLessons) {
						int id = database.addLesson(l.getTitle(), l.getBody(), l.getCategory());
						if (l.getTitleLocked())
							database.setLessonFieldLocked(id, Lesson.FIELD_TITLE, true);
						if (l.getBodyLocked())
							database.setLessonFieldLocked(id, Lesson.FIELD_BODY, true);
						if (l.getCategoryLocked())
							database.setLessonFieldLocked(id, Lesson.FIELD_CATEGORY, true);
						if (l.hasExperienceInformation())
							database.addExperienceInformation(id, l.getWhatWasDone(),
									l.getHowItWasDone(), l.getDuration(), l.getEffort());
					}
				} catch (Exception e) {
					System.out.println("Warning: restoring the original lessons failed.");
				}
				database.closeConnection();
			}
		}
	}

	/*-*******************************************************************************************

	The tests.  One method for each test case in Test Cases.pdf.

	*/

	// Test case: Add a lesson learned: valid lesson
	private static void testAddValid() throws SQLException {
		startTest("Add a lesson learned: valid lesson");
		String error = addLessonChecked(TITLE, BODY, CATEGORY);
		expect(error.isEmpty(), "a valid lesson is accepted");
		int id = lastAddedId;

		Lesson read = database.getLessonById(id);
		expect(fieldOf(read, Lesson.FIELD_TITLE).equals(TITLE), "title reads back the same");
		expect(fieldOf(read, Lesson.FIELD_BODY).equals(BODY), "body reads back the same");
		expect(fieldOf(read, Lesson.FIELD_CATEGORY).equals(CATEGORY),
				"category reads back the same");

		reopenDatabase();
		read = database.getLessonById(id);
		expect(fieldOf(read, Lesson.FIELD_TITLE).equals(TITLE),
				"the lesson is still there after the database is closed and reopened");
		endTest();
	}

	// Test case: Add a lesson learned: blank fields
	private static void testAddBlank() {
		startTest("Add a lesson learned: blank fields");
		int before = database.getNumberOfLessons();

		expectMessage(addLessonChecked("", BODY, CATEGORY), "Title");
		expectMessage(addLessonChecked(TITLE, "   ", CATEGORY), "Body");
		expectMessage(addLessonChecked(TITLE, BODY, ""), "Category");
		expect(database.getNumberOfLessons() == before, "nothing was saved");
		endTest();
	}

	// Test case: Add a lesson learned: too long fields
	private static void testAddTooLong() {
		startTest("Add a lesson learned: too long fields");
		int before = database.getNumberOfLessons();

		expectMessage(addLessonChecked(text(LessonValidator.MAX_TITLE_LENGTH + 1), BODY,
				CATEGORY), "Title", String.valueOf(LessonValidator.MAX_TITLE_LENGTH));
		expectMessage(addLessonChecked(TITLE, text(LessonValidator.MAX_BODY_LENGTH + 1),
				CATEGORY), "Body", String.valueOf(LessonValidator.MAX_BODY_LENGTH));
		expectMessage(addLessonChecked(TITLE, BODY, text(LessonValidator.MAX_CATEGORY_LENGTH + 1)),
				"Category", String.valueOf(LessonValidator.MAX_CATEGORY_LENGTH));
		expect(database.getNumberOfLessons() == before, "nothing was saved");

		String error = addLessonChecked(text(LessonValidator.MAX_TITLE_LENGTH),
				text(LessonValidator.MAX_BODY_LENGTH), text(LessonValidator.MAX_CATEGORY_LENGTH));
		expect(error.isEmpty(), "fields exactly at the limit are accepted");
		endTest();
	}

	// Test case: Locked fields: default flags
	private static void testLockedDefaultFlags() throws SQLException {
		startTest("Locked fields: default flags");
		int id = newLesson();
		expect(lockedText(database.getLessonById(id)).equals(
				"title=false body=false category=false"), "a new lesson has every field unlocked");

		database.setLessonFieldLocked(id, Lesson.FIELD_BODY, true);
		reopenDatabase();
		expect(lockedText(database.getLessonById(id)).equals(
				"title=false body=true category=false"),
				"a flag set directly is still locked after reopening");
		endTest();
	}

	// Test case: Locked fields: update to a locked field
	private static void testLockedUpdate() {
		startTest("Locked fields: update to a locked field");
		int id = newLesson();
		database.setLessonFieldLocked(id, Lesson.FIELD_TITLE, true);

		expectMessage(updateChecked(id, Lesson.FIELD_TITLE, "A different title"), "title");
		expect(fieldOf(database.getLessonById(id), Lesson.FIELD_TITLE).equals(TITLE),
				"the stored title is unchanged");

		expect(updateChecked(id, Lesson.FIELD_CATEGORY, "Exam").isEmpty(),
				"an unlocked field on the same lesson can be updated");
		expect(fieldOf(database.getLessonById(id), Lesson.FIELD_CATEGORY).equals("Exam"),
				"the unlocked field was saved");
		endTest();
	}

	// Test case: Locked fields: delete a locked lesson
	private static void testLockedDelete() {
		startTest("Locked fields: delete a locked lesson");
		int id = newLesson();
		database.setLessonFieldLocked(id, Lesson.FIELD_CATEGORY, true);

		expectMessage(deleteChecked(id), "locked");
		expect(database.lessonExists(id), "the lesson is still in the database");
		endTest();
	}

	// Test case: Update or delete a lesson: valid update
	private static void testValidUpdate() {
		startTest("Update or delete a lesson: valid update");
		int id = newLesson();
		expect(updateChecked(id, Lesson.FIELD_TITLE, "Review session recording request").isEmpty(),
				"a valid title is accepted");
		String newBody = "Asking that the exam review session be recorded for students who "
				+ "cannot attend.";
		expect(updateChecked(id, Lesson.FIELD_BODY, newBody).isEmpty(),
				"a valid body is accepted");
		expect(updateChecked(id, Lesson.FIELD_CATEGORY, "Review Sessions").isEmpty(),
				"a valid category is accepted");

		Lesson read = database.getLessonById(id);
		expect(fieldOf(read, Lesson.FIELD_TITLE).equals("Review session recording request"),
				"the new title was saved");
		expect(fieldOf(read, Lesson.FIELD_BODY).equals(newBody),
				"the new body was saved");
		expect(fieldOf(read, Lesson.FIELD_CATEGORY).equals("Review Sessions"),
				"the new category was saved");
		endTest();
	}

	// Test case: Update or delete a lesson: invalid update
	private static void testInvalidUpdate() {
		startTest("Update or delete a lesson: invalid update");
		int id = newLesson();
		expectRejectedUpdate(id, Lesson.FIELD_TITLE, "Title", "");
		expectRejectedUpdate(id, Lesson.FIELD_TITLE, "Title",
				text(LessonValidator.MAX_TITLE_LENGTH + 1));
		expectRejectedUpdate(id, Lesson.FIELD_BODY, "Body", "   ");
		expectRejectedUpdate(id, Lesson.FIELD_BODY, "Body",
				text(LessonValidator.MAX_BODY_LENGTH + 1));
		expectRejectedUpdate(id, Lesson.FIELD_CATEGORY, "Category", "");
		expectRejectedUpdate(id, Lesson.FIELD_CATEGORY, "Category",
				text(LessonValidator.MAX_CATEGORY_LENGTH + 1));
		endTest();
	}

	// Test case: Update or delete a lesson: delete
	private static void testDelete() throws SQLException {
		startTest("Update or delete a lesson: delete");
		int id = newLesson();
		expect(deleteChecked(id).isEmpty(), "an unlocked lesson can be deleted");

		reopenDatabase();
		expect(!database.lessonExists(id), "it is no longer in the database after reopening");
		expect(!containsId(database.getAllLessons(), id), "it is not in the list after reopening");
		endTest();
	}

	// Test case: Update or delete a lesson: lesson does not exist
	private static void testLessonDoesNotExist() {
		startTest("Update or delete a lesson: lesson does not exist");
		int before = database.getNumberOfLessons();
		expect(!database.lessonExists(UNKNOWN_ID), "the ID used really is not in the database");

		expectMessage(updateChecked(UNKNOWN_ID, Lesson.FIELD_TITLE, TITLE), "not found");
		expectMessage(deleteChecked(UNKNOWN_ID), "not found");
		expect(database.getNumberOfLessons() == before, "nothing changed");
		endTest();
	}

	// Test case: Add experience information: valid experience information
	private static void testExperienceValid() {
		startTest("Add experience information: valid experience information");
		int id = newLesson();
		String error = experienceChecked(id, "Asked for the review session to be recorded",
				"Posted the question on Ed Discussion", "10 minutes", "1 person");
		expect(error.isEmpty(), "valid experience information is accepted");
		expect(experienceText(database.getLessonById(id)).equals(
				"Asked for the review session to be recorded | Posted the question on Ed Discussion"
				+ " | 10 minutes | 1 person"), "the experience information was saved");
		endTest();
	}

	// Test case: Add experience information: blank text
	private static void testExperienceBlankText() {
		startTest("Add experience information: blank text");
		int id = newLesson();
		expectMessage(experienceChecked(id, "", "Posted on Ed Discussion", "10 minutes",
				"1 person"), "What was done");
		expectMessage(experienceChecked(id, "Asked for a recording", "", "10 minutes", "1 person"),
				"How it was done");
		expect(experienceText(database.getLessonById(id)).equals("none"), "nothing was saved");
		endTest();
	}

	// Test case: Add experience information: invalid duration
	private static void testExperienceInvalidDuration() {
		startTest("Add experience information: invalid duration");
		int id = newLesson();
		expectMessage(experienceChecked(id, "Asked for a recording", "Posted on Ed Discussion", "",
				"1 person"), "Duration");
		expectMessage(experienceChecked(id, "Asked for a recording", "Posted on Ed Discussion",
				text(LessonValidator.MAX_DURATION_LENGTH + 1), "1 person"), "Duration",
				String.valueOf(LessonValidator.MAX_DURATION_LENGTH));
		expect(experienceText(database.getLessonById(id)).equals("none"), "nothing was saved");
		endTest();
	}

	// Test case: Add experience information: invalid effort
	private static void testExperienceInvalidEffort() {
		startTest("Add experience information: invalid effort");
		int id = newLesson();
		expectMessage(experienceChecked(id, "Asked for a recording", "Posted on Ed Discussion",
				"10 minutes", ""), "Effort");
		expectMessage(experienceChecked(id, "Asked for a recording", "Posted on Ed Discussion",
				"10 minutes", text(LessonValidator.MAX_EFFORT_LENGTH + 1)), "Effort",
				String.valueOf(LessonValidator.MAX_EFFORT_LENGTH));
		expect(experienceText(database.getLessonById(id)).equals("none"), "nothing was saved");

		String error = experienceChecked(id, "Asked for a recording", "Posted on Ed Discussion",
				text(LessonValidator.MAX_DURATION_LENGTH), text(LessonValidator.MAX_EFFORT_LENGTH));
		expect(error.isEmpty(), "a duration and an effort exactly at the limit are accepted");
		endTest();
	}

	// Test case: Add experience information: lesson does not exist
	private static void testExperienceLessonDoesNotExist() {
		startTest("Add experience information: lesson does not exist");
		int before = database.getNumberOfLessons();
		expectMessage(experienceChecked(UNKNOWN_ID, "Asked for a recording",
				"Posted on Ed Discussion", "10 minutes", "1 person"), "not found");
		expect(database.getNumberOfLessons() == before && !database.lessonExists(UNKNOWN_ID),
				"nothing was saved");
		endTest();
	}

	// Test case: See a list of my lessons: full list
	private static void testListFull() {
		startTest("See a list of my lessons: full list");
		database.clearAllLessons();
		addLessonChecked("Lesson one", "Body one", "General");
		addLessonChecked("Lesson two", "Body two", "Testing");
		addLessonChecked("Lesson three", "Body three", "Design");

		LessonList all = database.getAllLessons();
		expect(all.size() == 3, "the list holds three lessons");
		expect(titlesOf(all).equals("Lesson one | Lesson two | Lesson three"),
				"the contents match what was added");
		endTest();
	}

	// Test case: See a list of my lessons: empty list
	private static void testListEmpty() {
		startTest("See a list of my lessons: empty list");
		database.clearAllLessons();
		LessonList none = database.getAllLessons();
		expect(none.size() == 0, "the list has no lessons");
		expect(none.getMessage().equals(LessonList.NO_LESSONS_MESSAGE),
				"the list gives the no lessons found message");
		endTest();
	}

	// Test case: Examine a lesson more closely: valid ID
	private static void testExamineValid() {
		startTest("Examine a lesson more closely: valid ID");
		int id = newLesson();
		database.setLessonFieldLocked(id, Lesson.FIELD_TITLE, true);
		experienceChecked(id, "Asked for a recording", "Posted on Ed Discussion", "10 minutes",
				"1 person");

		expect(examineError(String.valueOf(id)).isEmpty(), "a valid ID is accepted");
		Lesson read = database.getLessonById(id);
		expect(fieldOf(read, Lesson.FIELD_TITLE).equals(TITLE)
				&& fieldOf(read, Lesson.FIELD_BODY).equals(BODY)
				&& fieldOf(read, Lesson.FIELD_CATEGORY).equals(CATEGORY), "the fields are returned");
		expect(lockedText(read).equals("title=true body=false category=false"),
				"the locked flags are returned");
		expect(experienceText(read).equals(
				"Asked for a recording | Posted on Ed Discussion | 10 minutes | 1 person"),
				"the experience information is returned");
		endTest();
	}

	// Test case: Examine a lesson more closely: invalid ID
	private static void testExamineInvalid() {
		startTest("Examine a lesson more closely: invalid ID");
		expectMessage(examineError(""), "blank");
		expectMessage(examineError("abc"), "whole number");
		expectMessage(examineError(String.valueOf(UNKNOWN_ID)), "not found");
		endTest();
	}

	/*-*******************************************************************************************

	Helper methods that report results

	*/

	/**********
	 * Starts a test: prints its name and clears the list of failed bullets.
	 *
	 * @param name	the test case name, exactly as in Test Cases.pdf
	 */
	private static void startTest(String name) {
		problems = new StringBuilder();
		System.out.println();
		System.out.println("Test " + (passed + failed + 1) + " - " + name);
	}

	/**********
	 * Records that one bullet of the current test failed if the condition is false.
	 *
	 * @param condition		what must be true for the bullet to pass
	 * @param description	what the bullet checks, shown only if it fails
	 */
	private static void expect(boolean condition, String description) {
		if (!condition) problems.append("    Failed: ").append(description).append("\n");
	}

	/**********
	 * Prints an error message and checks that it exists and contains the words that tell the
	 * person what is wrong.
	 *
	 * @param message		the error message the code under test produced
	 * @param fragments		text the message must contain
	 */
	private static void expectMessage(String message, String... fragments) {
		System.out.println("    Message: " + (message == null ? "null" : "<" + message + ">"));
		boolean ok = (message != null) && !message.isEmpty();
		for (String fragment : fragments) {
			if (ok && !message.contains(fragment)) ok = false;
		}
		expect(ok, "message <" + message + "> should contain " + String.join(" and ", fragments));
	}

	/**********
	 * Ends a test: prints PASS if no bullet failed, otherwise FAIL and the bullets that failed.
	 */
	private static void endTest() {
		if (problems.length() == 0) {
			System.out.println("    Result: PASS");
			passed++;
		} else {
			System.out.print(problems);
			System.out.println("    Result: FAIL");
			failed++;
		}
	}

	/*-*******************************************************************************************

	Helper methods that do what the application does

	*/

	/**********
	 * Closes the database and opens it again, so a value that is still there afterward really
	 * was saved.
	 *
	 * @throws SQLException if the database cannot be reopened
	 */
	private static void reopenDatabase() throws SQLException {
		database.closeConnection();
		database = new Database();
		database.connectToDatabase();
	}

	/**********
	 * Builds text of an exact length, for the limit tests.
	 *
	 * @param length	how many characters the text is to have
	 *
	 * @return that many letters
	 */
	private static String text(int length) {
		return "x".repeat(length);
	}

	/**********
	 * Adds the standard sample lesson, for the tests that need a lesson to work on.
	 *
	 * @return the ID of the new lesson
	 */
	private static int newLesson() {
		addLessonChecked(TITLE, BODY, CATEGORY);
		return lastAddedId;
	}

	/**********
	 * Validates and adds a lesson.  The ID of a saved lesson is left in lastAddedId.
	 *
	 * @param title		the title to add
	 * @param body		the body to add
	 * @param category	the category to add
	 *
	 * @return an empty String if the lesson was saved, else the error message
	 */
	private static String addLessonChecked(String title, String body, String category) {
		lastAddedId = -1;
		String error = LessonValidator.checkTitle(title);
		if (error.isEmpty()) error = LessonValidator.checkBody(body);
		if (error.isEmpty()) error = LessonValidator.checkCategory(category);
		if (!error.isEmpty()) return error;

		lastAddedId = database.addLesson(title, body, category);
		return lastAddedId < 0 ? "The lesson could not be saved." : "";
	}

	/**********
	 * Validates and updates one field of a lesson: the lesson must exist, the field must not be
	 * locked, and the new value must be acceptable.
	 *
	 * @param id		the lesson to update
	 * @param field		Lesson.FIELD_TITLE, Lesson.FIELD_BODY, or Lesson.FIELD_CATEGORY
	 * @param value		the new value
	 *
	 * @return an empty String if the update was saved, else the error message
	 */
	private static String updateChecked(int id, String field, String value) {
		Lesson lesson = database.getLessonById(id);
		String error = LessonValidator.checkLessonFound(lesson, id);
		if (error.isEmpty()) error = LessonValidator.checkFieldNotLocked(lesson, field);
		if (error.isEmpty()) {
			if (Lesson.FIELD_TITLE.equals(field)) error = LessonValidator.checkTitle(value);
			else if (Lesson.FIELD_BODY.equals(field)) error = LessonValidator.checkBody(value);
			else error = LessonValidator.checkCategory(value);
		}
		if (!error.isEmpty()) return error;

		boolean saved;
		if (Lesson.FIELD_TITLE.equals(field)) saved = database.updateLessonTitle(id, value);
		else if (Lesson.FIELD_BODY.equals(field)) saved = database.updateLessonBody(id, value);
		else saved = database.updateLessonCategory(id, value);
		return saved ? "" : "The " + field + " could not be saved.";
	}

	/**********
	 * Validates and deletes a lesson: it must exist and none of its fields may be locked.
	 *
	 * @param id	the lesson to delete
	 *
	 * @return an empty String if the lesson was deleted, else the error message
	 */
	private static String deleteChecked(int id) {
		Lesson lesson = database.getLessonById(id);
		String error = LessonValidator.checkLessonFound(lesson, id);
		if (error.isEmpty()) error = LessonValidator.checkCanDelete(lesson);
		if (!error.isEmpty()) return error;

		return database.deleteLesson(id) ? "" : "The lesson could not be deleted.";
	}

	/**********
	 * Validates and saves experience information.
	 *
	 * @param id			the lesson to add to
	 * @param whatWasDone	what was done
	 * @param howItWasDone	how it was done
	 * @param duration		how long it took
	 * @param effort		the effort used
	 *
	 * @return an empty String if the information was saved, else the error message
	 */
	private static String experienceChecked(int id, String whatWasDone, String howItWasDone,
			String duration, String effort) {
		String error = LessonValidator.checkLessonFound(database.getLessonById(id), id);
		if (error.isEmpty()) error = LessonValidator.checkWhatWasDone(whatWasDone);
		if (error.isEmpty()) error = LessonValidator.checkHowItWasDone(howItWasDone);
		if (error.isEmpty()) error = LessonValidator.checkDuration(duration);
		if (error.isEmpty()) error = LessonValidator.checkEffort(effort);
		if (!error.isEmpty()) return error;

		return database.addExperienceInformation(id, whatWasDone, howItWasDone, duration, effort)
				? "" : "The experience information could not be saved.";
	}

	/**********
	 * Validates an ID as a person would type it and looks the lesson up.
	 *
	 * @param idText	the ID as typed
	 *
	 * @return an empty String if the ID names a lesson, else the error message
	 */
	private static String examineError(String idText) {
		String error = LessonValidator.checkLessonIdText(idText);
		if (!error.isEmpty()) return error;
		int id = Integer.parseInt(idText.trim());
		return LessonValidator.checkLessonFound(database.getLessonById(id), id);
	}

	/**********
	 * Tries an invalid update and checks the message names the field and the stored value did
	 * not change.
	 *
	 * @param id		the lesson being updated
	 * @param field		the field being updated
	 * @param label		the field name the message must contain
	 * @param value		the invalid value to try
	 */
	private static void expectRejectedUpdate(int id, String field, String label, String value) {
		String storedBefore = fieldOf(database.getLessonById(id), field);
		expectMessage(updateChecked(id, field, value), label);
		expect(fieldOf(database.getLessonById(id), field).equals(storedBefore),
				"the stored " + field + " is unchanged after a rejected update");
	}

	/*-*******************************************************************************************

	Helper methods that describe a lesson as text, so one comparison covers everything

	*/

	/**********
	 * Reads one core field of a lesson, tolerating a lesson that was not found.
	 *
	 * @param lesson	the lesson to read, which may be null
	 * @param field		Lesson.FIELD_TITLE, Lesson.FIELD_BODY, or Lesson.FIELD_CATEGORY
	 *
	 * @return the field's value, or a marker if the lesson is null
	 */
	private static String fieldOf(Lesson lesson, String field) {
		if (lesson == null) return "<lesson was not found>";
		if (Lesson.FIELD_TITLE.equals(field)) return lesson.getTitle();
		if (Lesson.FIELD_BODY.equals(field)) return lesson.getBody();
		return lesson.getCategory();
	}

	/**********
	 * Summarizes the three locked flags of a lesson.
	 *
	 * @param lesson	the lesson to describe, which may be null
	 *
	 * @return for example "title=true body=false category=false", or a marker if null
	 */
	private static String lockedText(Lesson lesson) {
		if (lesson == null) return "<lesson was not found>";
		return "title=" + lesson.getTitleLocked() + " body=" + lesson.getBodyLocked()
				+ " category=" + lesson.getCategoryLocked();
	}

	/**********
	 * Summarizes the experience information of a lesson.
	 *
	 * @param lesson	the lesson to describe, which may be null
	 *
	 * @return "what | how | duration | effort", "none" if there is none, or a marker if null
	 */
	private static String experienceText(Lesson lesson) {
		if (lesson == null) return "<lesson was not found>";
		if (!lesson.hasExperienceInformation()) return "none";
		return lesson.getWhatWasDone() + " | " + lesson.getHowItWasDone() + " | "
				+ lesson.getDuration() + " | " + lesson.getEffort();
	}

	/**********
	 * Lists the titles in a LessonList, in order.
	 *
	 * @param list	the list to describe
	 *
	 * @return the titles separated by " | "
	 */
	private static String titlesOf(LessonList list) {
		StringBuilder sb = new StringBuilder();
		for (Lesson l : list.getLessons()) {
			if (sb.length() > 0) sb.append(" | ");
			sb.append(l.getTitle());
		}
		return sb.toString();
	}

	/**********
	 * Searches a list of lessons for one with the given ID.
	 *
	 * @param list	the list to search
	 * @param id	the ID being looked for
	 *
	 * @return true if the list holds that lesson, else false
	 */
	private static boolean containsId(LessonList list, int id) {
		for (Lesson l : list.getLessons()) {
			if (l.getId() == id) return true;
		}
		return false;
	}
}
