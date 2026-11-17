package guiMyLessons;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: ModelMyLessons Class. </p>
 *
 * <p> Description: The My Lessons Page Model.  It decides which lessons a Contributor's list
 * holds and how each lesson is described in that list.
 *
 * The rules live here and not in the View so that the testing automation can call exactly the
 * code the page calls without starting JavaFX.  This class has no JavaFX imports for that reason.
 *
 * Requirements satisfied: R2, "Once a lesson has been added to the system, I can see ... it", and
 * the part of R3 that lets a Contributor see which of their lessons another role has locked.  R7,
 * a Contributor sees only the lessons that Contributor added, follows from lessonsFor reading
 * lessons by author.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-20 Initial version
 */

public class ModelMyLessons {

	/** The text shown in place of the list when the Contributor has not added any lessons. */
	public static final String EMPTY_MESSAGE = "You have not added any lessons yet.";

	/**
	 * Default constructor is not used.  Every method in this class is static.
	 */
	public ModelMyLessons() {
	}

	/**********
	 * <p> Method: LessonsLearnedList lessonsFor(Database db, String username) </p>
	 *
	 * <p> Description: Gets the lessons a Contributor added, in the order they were added (R2).
	 * Only lessons recorded with this Contributor as the author are returned, so another
	 * Contributor's lessons never appear (R7).  A lesson a Curator or Admin has changed or locked
	 * is read fresh each time, so the list always shows its current state (R6). </p>
	 *
	 * @param db		the database to read
	 * @param username	the Contributor whose lessons are wanted
	 *
	 * @return that Contributor's lessons, which may be empty but is never null
	 */
	public static LessonsLearnedList lessonsFor(Database db, String username) {
		return db.getLessonsAddedBy(username);
	}

	/**********
	 * <p> Method: String rowText(LessonsLearned lesson) </p>
	 *
	 * <p> Description: Builds the line the list shows for one lesson: its title, its category
	 * in parentheses, and, when another role has locked any of its fields, how many (R3).  The
	 * wording is plain text, "1 field locked" or "2 fields locked", so it reads the same on every
	 * screen.  An unlocked lesson's line says nothing about locks. </p>
	 *
	 * @param lessonsLearned	the lesson to describe
	 *
	 * @return the text for that lesson's row in the list
	 */
	public static String rowText(LessonsLearned lessonsLearned) {
		String text = lessonsLearned.getTitle() + "  (" + lessonsLearned.getCategory() + ")";

		// R3: count the locked core fields so the Contributor can see which lessons are locked
		int locked = 0;
		if (lessonsLearned.getTitleLocked()) locked++;
		if (lessonsLearned.getCategoryLocked()) locked++;
		if (lessonsLearned.getBodyLocked()) locked++;
		if (locked == 1) text += "  -  1 field locked";
		else if (locked > 1) text += "  -  " + locked + " fields locked";
		return text;
	}
}
