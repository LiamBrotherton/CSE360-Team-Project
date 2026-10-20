package guiAddLesson;

import database.Database;
import inputRecognizer.LessonValidator;

/*******
 * <p> Title: ModelAddLesson Class. </p>
 *
 * <p> Description: The Add a Lesson Page Model.  It holds the one rule this page has: a
 * Contributor's new lesson is checked, and only stored when all of its core information is
 * acceptable, and it is stored with that Contributor recorded as its author.
 *
 * The rule lives here and not in the View or the Controller so that the testing automation can
 * call exactly the code the page calls without starting JavaFX.  This class has no JavaFX
 * imports for that reason.
 *
 * Requirement satisfied: R1, "As a contributor, I can add new lessons learned to the system,
 * specifying core information."  Recording the author is also what makes R2 (a Contributor sees
 * the lessons that Contributor added) possible.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-20 Initial version
 */

public class ModelAddLesson {

	/** The message shown when the database refuses to store a lesson that passed every check. */
	public static final String SAVE_FAILED = "The lesson could not be saved. Try again.";

	/** The ID the database gave the most recently added lesson, or -1 if the last add stored
	 *  nothing.  The page uses it to know the add succeeded; the tests use it to find the lesson. */
	public static int lastAddedId = -1;

	/**
	 * Default constructor is not used.  The method in this class is static.
	 */
	public ModelAddLesson() {
	}

	/**********
	 * <p> Method: String addLesson(Database db, String username, String title, String category,
	 * String body) </p>
	 *
	 * <p> Description: Checks a new lesson's core information and, if it is all acceptable,
	 * stores the lesson with the Contributor as its author (R1).  The fields are checked in the
	 * order they appear on the page (title, category, body) and the first problem is reported,
	 * so the Contributor fixes one thing at a time from the top.  The checks are the team's
	 * existing LessonValidator rules, so a lesson added here obeys the same limits as one added
	 * anywhere else. </p>
	 *
	 * @param db		the database to store the lesson in
	 * @param username	the Contributor adding the lesson, recorded as its author
	 * @param title		the title entered
	 * @param category	the category entered
	 * @param body		the body entered
	 *
	 * @return the empty String if the lesson was stored, else the message explaining why not
	 */
	public static String addLesson(Database db, String username, String title, String category,
			String body) {
		lastAddedId = -1;

		// R1: the core information must be acceptable before anything is stored
		String problem = LessonValidator.checkTitle(title);
		if (problem.isEmpty()) problem = LessonValidator.checkCategory(category);
		if (problem.isEmpty()) problem = LessonValidator.checkBody(body);
		if (!problem.isEmpty()) return problem;

		// R1 and R2: store the lesson with its author, so it appears in this Contributor's list
		int id = db.addLesson(title, body, category, username);
		if (id < 0) return SAVE_FAILED;
		lastAddedId = id;
		return "";
	}
}
