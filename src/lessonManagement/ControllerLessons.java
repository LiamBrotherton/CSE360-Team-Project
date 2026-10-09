package lessonManagement;

import database.Database;
import entityClasses.Lesson;
import entityClasses.LessonList;
import inputRecognizer.LessonRecognizer;

/*******
 * <p> Title: ControllerLessons Class. </p>
 *
 * <p> Description: The controller for the lessons learned Create, Read, Update, and Delete (CRUD)
 * operations.  It sits between whatever asks for an operation (the HW2 console application, the
 * testing automation, and later a JavaFX page) and database.Database.  It does two jobs the
 * database should not: it validates every input using inputRecognizer.LessonRecognizer, and it
 * enforces who may do what.
 *
 * The ownership rule comes from the Contributor user stories.  A Contributor can add lessons and
 * can see and manage only the lessons that Contributor added.  So update, delete, and
 * "view my lesson" are refused for a lesson added by someone else.  Reading the full list and
 * searching are the Viewer's stories, so those carry no ownership check.
 *
 * When an operation is refused, the reason is left in lastErrorMessage in words the person can
 * act on, and the operation returns a failure value (-1, false, null, or an empty list).  This is
 * the same shape as the other Foundations controllers, which leave their error text in public
 * static attributes.  lastErrorMessage is reset to the empty String at the start of every
 * operation, so after a call it describes that call and no earlier one.
 *
 * The class has no JavaFX imports, so the testing automation can call exactly the code a GUI
 * page would call without starting the JavaFX runtime.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-04 Initial version
 */
public class ControllerLessons {

	/**
	 * Default constructor is not used.  Every method in this class is static.
	 */
	public ControllerLessons() {
	}

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The reason the most recent operation was refused, or the empty String if it succeeded. */
	public static String lastErrorMessage = "";

	/**********
	 * <p> Method: int addLesson(String author, String title, String situation, String lessonText,
	 * String category, String tags) </p>
	 *
	 * <p> Description: Validate the fields of a new lesson and, if they are all acceptable, store
	 * it.  The fields are checked in the order they appear on the page, and the first problem is
	 * reported, so the author fixes one thing at a time from the top.  Surrounding spaces are
	 * stripped from the single line fields before checking, so a title with a trailing space is
	 * not refused for it. </p>
	 *
	 * @param author specifies the username of the Contributor adding the lesson, who must exist
	 *
	 * @param title specifies the title
	 *
	 * @param situation specifies what happened
	 *
	 * @param lessonText specifies what was learned
	 *
	 * @param category specifies the category
	 *
	 * @param tags specifies the comma separated tags, which may be empty
	 *
	 * @return the id of the new lesson, or -1 if it was refused, in which case lastErrorMessage
	 * says why
	 */
	public static int addLesson(String author, String title, String situation, String lessonText,
			String category, String tags) {
		lastErrorMessage = "";

		if (author == null || author.isEmpty() || !theDatabase.doesUserExist(author)) {
			lastErrorMessage = "The author \"" + author + "\" is not a user of this system. A lesson "
					+ "can only be added by an existing user.";
			return -1;
		}

		title = strip(title);
		category = strip(category);
		tags = strip(tags);

		if (!validFields(title, situation, lessonText, category, tags)) return -1;

		int id = theDatabase.createLesson(new Lesson(title, situation, lessonText, category,
				LessonRecognizer.normalizeTags(tags), author));
		if (id < 0) lastErrorMessage = "The lesson could not be saved because of a database "
				+ "problem. Nothing was stored, so it is safe to try again.";
		return id;
	}

	/**********
	 * <p> Method: Lesson viewMyLesson(String requestingUser, int id) </p>
	 *
	 * <p> Description: Read one lesson on behalf of a Contributor.  A Contributor may only see
	 * lessons that Contributor added.  A lesson added by someone else is refused with the same
	 * kind of message as one that does not exist, so a Contributor cannot probe for which ids
	 * belong to other people. </p>
	 *
	 * @param requestingUser specifies the username of the Contributor asking
	 *
	 * @param id specifies the id of the lesson to read
	 *
	 * @return the lesson, or null if it does not exist or is not the requester's, in which case
	 * lastErrorMessage says why
	 */
	public static Lesson viewMyLesson(String requestingUser, int id) {
		lastErrorMessage = "";
		Lesson lesson = theDatabase.getLesson(id);
		if (lesson == null || !lesson.getAuthor().equals(requestingUser)) {
			lastErrorMessage = "There is no lesson number " + id + " among the lessons you added.";
			return null;
		}
		return lesson;
	}

	/**********
	 * <p> Method: Lesson viewLesson(int id) </p>
	 *
	 * <p> Description: Read one lesson on behalf of a Viewer, who may explore any lesson, whether
	 * chosen from the list of all lessons or from a search result. </p>
	 *
	 * @param id specifies the id of the lesson to read
	 *
	 * @return the lesson, or null if it does not exist, in which case lastErrorMessage says so
	 */
	public static Lesson viewLesson(int id) {
		lastErrorMessage = "";
		Lesson lesson = theDatabase.getLesson(id);
		if (lesson == null) lastErrorMessage = "There is no lesson number " + id + ".";
		return lesson;
	}

	/**********
	 * <p> Method: LessonList listMyLessons(String author) </p>
	 *
	 * <p> Description: List the lessons one Contributor added, newest first.  A Contributor who
	 * has added none gets an empty list, which is not an error. </p>
	 *
	 * @param author specifies the username of the Contributor
	 *
	 * @return that Contributor's lessons, never null
	 */
	public static LessonList listMyLessons(String author) {
		lastErrorMessage = "";
		return theDatabase.getLessonsByAuthor(author);
	}

	/**********
	 * <p> Method: LessonList listAllLessons() </p>
	 *
	 * <p> Description: List every lesson, newest first, for a Viewer. </p>
	 *
	 * @return every lesson, never null
	 */
	public static LessonList listAllLessons() {
		lastErrorMessage = "";
		return theDatabase.getAllLessons();
	}

	/**********
	 * <p> Method: LessonList searchLessons(String keyword) </p>
	 *
	 * <p> Description: Validate a search keyword and, if it is acceptable, list the lessons that
	 * match it.  No match is an ordinary result and gives an empty list with no error message.
	 * A keyword that is refused also gives an empty list, but lastErrorMessage says why, which
	 * is how a caller tells "nothing matched" apart from "the search never ran". </p>
	 *
	 * @param keyword specifies the word or phrase to look for
	 *
	 * @return the matching lessons, never null
	 */
	public static LessonList searchLessons(String keyword) {
		lastErrorMessage = "";
		String trimmed = strip(keyword);
		String problem = LessonRecognizer.checkKeyword(trimmed);
		if (!problem.isEmpty()) {
			lastErrorMessage = problem;
			return new LessonList();
		}
		return theDatabase.searchLessons(trimmed);
	}

	/**********
	 * <p> Method: boolean updateLesson(String requestingUser, int id, String title,
	 * String situation, String lessonText, String category, String tags) </p>
	 *
	 * <p> Description: Replace the editable fields of a lesson on behalf of the Contributor who
	 * added it.  The lesson must exist, must belong to the requester, and the new values must all
	 * pass validation.  Validation runs before anything is written, so a refused update leaves
	 * the stored lesson exactly as it was. </p>
	 *
	 * @param requestingUser specifies the username of the Contributor asking
	 *
	 * @param id specifies the id of the lesson to update
	 *
	 * @param title specifies the new title
	 *
	 * @param situation specifies the new situation
	 *
	 * @param lessonText specifies the new lesson text
	 *
	 * @param category specifies the new category
	 *
	 * @param tags specifies the new comma separated tags, which may be empty
	 *
	 * @return true if the lesson was updated, else false with lastErrorMessage saying why
	 */
	public static boolean updateLesson(String requestingUser, int id, String title,
			String situation, String lessonText, String category, String tags) {
		lastErrorMessage = "";

		Lesson existing = theDatabase.getLesson(id);
		if (existing == null) {
			lastErrorMessage = "There is no lesson number " + id + " to update.";
			return false;
		}
		if (!existing.getAuthor().equals(requestingUser)) {
			lastErrorMessage = "Lesson number " + id + " was added by another user. You can only "
					+ "update lessons you added.";
			return false;
		}

		title = strip(title);
		category = strip(category);
		tags = strip(tags);

		if (!validFields(title, situation, lessonText, category, tags)) return false;

		existing.setTitle(title);
		existing.setSituation(situation);
		existing.setLessonText(lessonText);
		existing.setCategory(category);
		existing.setTags(LessonRecognizer.normalizeTags(tags));

		if (!theDatabase.updateLesson(existing)) {
			lastErrorMessage = "The lesson could not be updated because of a database problem. "
					+ "It has not been changed.";
			return false;
		}
		return true;
	}

	/**********
	 * <p> Method: boolean deleteLesson(String requestingUser, int id) </p>
	 *
	 * <p> Description: Delete a lesson on behalf of the Contributor who added it.  The lesson must
	 * exist and must belong to the requester.  Deleting a lesson that is already gone is refused
	 * rather than reported as a success, so a repeated click is visible as a repeat. </p>
	 *
	 * @param requestingUser specifies the username of the Contributor asking
	 *
	 * @param id specifies the id of the lesson to delete
	 *
	 * @return true if the lesson was deleted, else false with lastErrorMessage saying why
	 */
	public static boolean deleteLesson(String requestingUser, int id) {
		lastErrorMessage = "";

		Lesson existing = theDatabase.getLesson(id);
		if (existing == null) {
			lastErrorMessage = "There is no lesson number " + id + " to delete. It may already have "
					+ "been deleted.";
			return false;
		}
		if (!existing.getAuthor().equals(requestingUser)) {
			lastErrorMessage = "Lesson number " + id + " was added by another user. You can only "
					+ "delete lessons you added.";
			return false;
		}

		if (!theDatabase.deleteLesson(id)) {
			lastErrorMessage = "The lesson could not be deleted because of a database problem. "
					+ "It is still stored.";
			return false;
		}
		return true;
	}

	/*
	 * Private method that runs the field checks in page order and stops at the first problem,
	 * leaving that problem in lastErrorMessage.  Both add and update use it, so the two cannot
	 * drift apart and accept different things.
	 */
	private static boolean validFields(String title, String situation, String lessonText,
			String category, String tags) {
		String problem = LessonRecognizer.checkTitle(title);
		if (problem.isEmpty()) problem = LessonRecognizer.checkSituation(situation);
		if (problem.isEmpty()) problem = LessonRecognizer.checkLessonText(lessonText);
		if (problem.isEmpty()) problem = LessonRecognizer.checkCategory(category);
		if (problem.isEmpty()) problem = LessonRecognizer.checkTags(tags);
		lastErrorMessage = problem;
		return problem.isEmpty();
	}

	/*
	 * Private method that strips surrounding spaces, turning null into the empty String so the
	 * recognizers report a missing value as "required" rather than failing on null.
	 */
	private static String strip(String s) {
		return s == null ? "" : s.trim();
	}
}
