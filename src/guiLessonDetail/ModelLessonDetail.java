package guiLessonDetail;

import database.Database;
import entityClasses.Lesson;
import inputRecognizer.LessonValidator;

/*******
 * <p> Title: ModelLessonDetail Class. </p>
 *
 * <p> Description: The Lesson Details Page Model.  It holds every rule for a Contributor seeing
 * and managing one lesson: opening it, changing one of its core fields, and deleting it.
 *
 * The rules live here and not in the View or the Controller so that the testing automation can
 * call exactly the code the page calls without starting JavaFX.  This class has no JavaFX
 * imports for that reason.
 *
 * Requirements satisfied, from the Contributor user story:
 * R3, open a lesson and see its core information and which fields are locked;
 * R4, change the title, category or body, unless another role has locked that field;
 * R5, delete the lesson, unless a field is locked;
 * R6, changes, locks and deletions made by a Curator or Admin are what the Contributor works
 * with, because every method reads the lesson fresh from the database;
 * R7, a Contributor can only open or change lessons that Contributor added.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-20 Initial version
 */

public class ModelLessonDetail {

	/** The message for a lesson that does not exist or belongs to another Contributor.  The two
	 *  cases share one message so a Contributor cannot learn that another person's lesson exists,
	 *  and so a lesson another role deleted is described the same way. */
	public static final String NOT_AVAILABLE = "That lesson is no longer available.";

	/** The message shown when the database refuses a change that passed every check. */
	public static final String SAVE_FAILED = "The lesson could not be saved. Try again.";

	/**
	 * Default constructor is not used.  Every method in this class is static.
	 */
	public ModelLessonDetail() {
	}

	/**********
	 * <p> Method: Lesson lessonFor(Database db, String username, int lessonId) </p>
	 *
	 * <p> Description: Gets a lesson for the Contributor who added it (R3, R7).  The lesson is
	 * read from the database every time, so a change, lock or deletion made by a Curator or Admin
	 * is always what the Contributor sees (R6). </p>
	 *
	 * @param db		the database to read
	 * @param username	the Contributor asking
	 * @param lessonId	the lesson wanted
	 *
	 * @return the lesson, or null if it does not exist or another Contributor added it
	 */
	public static Lesson lessonFor(Database db, String username, int lessonId) {
		Lesson lesson = db.getLessonById(lessonId);

		// R7: a lesson added by someone else is treated exactly like one that does not exist
		if (lesson == null || username == null || !username.equals(lesson.getAddedBy())) return null;
		return lesson;
	}

	/**********
	 * <p> Method: String updateField(Database db, String username, int lessonId, String field,
	 * String value) </p>
	 *
	 * <p> Description: Changes one core field of a Contributor's lesson (R4).  The checks run in
	 * this order: the lesson still exists and is the Contributor's (R6, R7); the field is not
	 * locked by another role (R4); the new value passes the same rule used when a lesson is added
	 * (R1, R4).  Only then is the value saved.  The page hides Edit on a locked field, but the
	 * check is repeated here so the rule holds no matter how the change is asked for. </p>
	 *
	 * @param db		the database to change
	 * @param username	the Contributor asking
	 * @param lessonId	the lesson to change
	 * @param field		Lesson.FIELD_TITLE, Lesson.FIELD_CATEGORY, or Lesson.FIELD_BODY
	 * @param value		the new value
	 *
	 * @return the empty String if the field was changed, else the message explaining why not
	 */
	public static String updateField(Database db, String username, int lessonId, String field,
			String value) {
		// R6 and R7: the lesson must still exist and must be this Contributor's
		Lesson lesson = lessonFor(db, username, lessonId);
		if (lesson == null) return NOT_AVAILABLE;

		// R4: another role may have locked this field so the Contributor cannot alter it
		String problem = LessonValidator.checkFieldNotLocked(lesson, field);
		if (!problem.isEmpty()) return problem;

		// R4: the new value must be acceptable core information, by the same rule as R1
		problem = checkValue(field, value);
		if (!problem.isEmpty()) return problem;

		boolean saved;
		if (Lesson.FIELD_TITLE.equals(field)) saved = db.updateLessonTitle(lessonId, value);
		else if (Lesson.FIELD_CATEGORY.equals(field)) saved = db.updateLessonCategory(lessonId, value);
		else saved = db.updateLessonBody(lessonId, value);
		return saved ? "" : SAVE_FAILED;
	}

	/**********
	 * <p> Method: String checkValue(String field, String value) </p>
	 *
	 * <p> Description: Checks a new value for one core field by the same rule used when a lesson
	 * is added (R1, R4).  updateField uses it, and so does the page, which keeps the Edit dialog
	 * open with what was typed when the value is refused, so the Contributor can fix it rather
	 * than type it again. </p>
	 *
	 * @param field		Lesson.FIELD_TITLE, Lesson.FIELD_CATEGORY, or Lesson.FIELD_BODY
	 * @param value		the new value
	 *
	 * @return the empty String if the value is acceptable, else the message explaining why not
	 */
	public static String checkValue(String field, String value) {
		if (Lesson.FIELD_TITLE.equals(field)) return LessonValidator.checkTitle(value);
		if (Lesson.FIELD_CATEGORY.equals(field)) return LessonValidator.checkCategory(value);
		return LessonValidator.checkBody(value);
	}

	/**********
	 * <p> Method: String deleteLesson(Database db, String username, int lessonId) </p>
	 *
	 * <p> Description: Deletes a Contributor's lesson (R5).  The lesson must still exist and be
	 * the Contributor's (R6, R7), and it must have no locked field: deleting it would alter fields
	 * another role locked, so the team's LessonValidator.checkCanDelete refuses it.  The page asks
	 * "Are you sure?" before calling this method. </p>
	 *
	 * @param db		the database to change
	 * @param username	the Contributor asking
	 * @param lessonId	the lesson to delete
	 *
	 * @return the empty String if the lesson was deleted, else the message explaining why not
	 */
	public static String deleteLesson(Database db, String username, int lessonId) {
		// R6 and R7: the lesson must still exist and must be this Contributor's
		Lesson lesson = lessonFor(db, username, lessonId);
		if (lesson == null) return NOT_AVAILABLE;

		// R5: a lesson with any locked field cannot be deleted by its Contributor
		String problem = LessonValidator.checkCanDelete(lesson);
		if (!problem.isEmpty()) return problem;

		return db.deleteLesson(lessonId) ? "" : SAVE_FAILED;
	}
}
