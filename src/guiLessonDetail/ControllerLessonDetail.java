package guiLessonDetail;

import java.util.Optional;

import javafx.scene.control.ButtonType;
import entityClasses.Lesson;
import inputRecognizer.LessonValidator;

/*******
 * <p> Title: ControllerLessonDetail Class. </p>
 *
 * <p> Description: The Java/FX-based Lesson Details Page controller.  This class provides the
 * controller actions for the page's buttons: Edit on each unlocked field, Delete lesson, and Back
 * to my lessons.
 *
 * Before an Edit or a Delete does anything, the lesson is read again.  Another role may have
 * changed, locked, or deleted it since the page was shown, and the Contributor should act on
 * what is there now (R6).  The rules themselves are in ModelLessonDetail, which repeats every
 * check before it saves.
 *
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-20 Initial version
 */

public class ControllerLessonDetail {

	/*-********************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and
	the Model is often just a stub, or will be a singleton instantiated object.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerLessonDetail() {
	}

	/**********
	 * <p> Method: performEdit(String field) </p>
	 *
	 * <p> Description: This method changes one core field of the lesson (R4).  If the lesson is no
	 * longer available, the My Lessons page says so (R6, R7).  If another role has locked the
	 * field since the page was shown, the page is shown again with the reason and without that
	 * field's Edit button.  Otherwise the dialog opens with the current value.  If the value
	 * entered breaks a rule, such as a blank title or a body that is too long, the dialog opens
	 * again holding what was typed, with the reason, so nothing typed is lost; this repeats until
	 * the value is acceptable or the dialog is cancelled.  An acceptable value is passed to
	 * ModelLessonDetail.updateField, which checks everything again and saves it, and the page is
	 * shown again with the new value or with the reason it was refused. </p>
	 *
	 * @param field		Lesson.FIELD_TITLE, Lesson.FIELD_CATEGORY, or Lesson.FIELD_BODY
	 */
	protected static void performEdit(String field) {
		String username = ViewLessonDetail.theUser.getUserName();
		int id = ViewLessonDetail.theLessonId;

		// R6 and R7: act on the lesson as it is now
		Lesson lesson = ModelLessonDetail.lessonFor(ViewLessonDetail.theDatabase, username, id);
		if (lesson == null) {
			backToMyLessons(ModelLessonDetail.NOT_AVAILABLE);
			return;
		}

		// R4: a field locked since the page was shown cannot be edited
		String problem = LessonValidator.checkFieldNotLocked(lesson, field);
		if (!problem.isEmpty()) {
			showAgain(problem);
			return;
		}

		// R4: ask for the new value, starting from the current one.  A value that breaks a rule
		// is offered again with the reason, so the Contributor can fix it instead of retyping it.
		Optional<String> value = ViewLessonDetail.askForValue(field, currentValue(lesson, field), "");
		if (value.isEmpty()) return;
		problem = ModelLessonDetail.checkValue(field, value.get());
		while (!problem.isEmpty()) {
			value = ViewLessonDetail.askForValue(field, value.get(), problem);
			if (value.isEmpty()) return;
			problem = ModelLessonDetail.checkValue(field, value.get());
		}

		String result = ModelLessonDetail.updateField(ViewLessonDetail.theDatabase, username, id,
				field, value.get());
		if (result.equals(ModelLessonDetail.NOT_AVAILABLE)) backToMyLessons(result);
		else showAgain(result);
	}

	/**********
	 * <p> Method: performDelete() </p>
	 *
	 * <p> Description: This method deletes the lesson (R5).  If the lesson is no longer available,
	 * the My Lessons page says so (R6, R7).  If any of its fields is locked, the page says why it
	 * cannot be deleted without asking first, since the answer would not matter.  Otherwise it asks
	 * "Delete this lesson?", and only if the Contributor confirms does
	 * ModelLessonDetail.deleteLesson delete it, after which the My Lessons page is shown. </p>
	 *
	 */
	protected static void performDelete() {
		String username = ViewLessonDetail.theUser.getUserName();
		int id = ViewLessonDetail.theLessonId;

		// R6 and R7: act on the lesson as it is now
		Lesson lesson = ModelLessonDetail.lessonFor(ViewLessonDetail.theDatabase, username, id);
		if (lesson == null) {
			backToMyLessons(ModelLessonDetail.NOT_AVAILABLE);
			return;
		}

		// R5: a lesson with a locked field cannot be deleted, so there is nothing to confirm
		String problem = LessonValidator.checkCanDelete(lesson);
		if (!problem.isEmpty()) {
			ViewLessonDetail.label_Message.setText(problem);
			return;
		}

		// R5: delete only when the Contributor confirms
		Optional<ButtonType> answer = ViewLessonDetail.alertConfirmDelete.showAndWait();
		if (answer.isEmpty() || answer.get() != ButtonType.OK) return;

		String result = ModelLessonDetail.deleteLesson(ViewLessonDetail.theDatabase, username, id);
		if (result.isEmpty()) backToMyLessons("");
		else if (result.equals(ModelLessonDetail.NOT_AVAILABLE)) backToMyLessons(result);
		else ViewLessonDetail.label_Message.setText(result);
	}

	/**********
	 * <p> Method: performBackToMyLessons() </p>
	 *
	 * <p> Description: This method returns to the My Lessons page. </p>
	 *
	 */
	protected static void performBackToMyLessons() {
		backToMyLessons("");
	}

	/*-********************************************************************************************

	Helper methods

	 */

	/**********
	 * Private local method that shows the My Lessons page with a message, or none.
	 *
	 * @param message	The message to show, or the empty String
	 */
	private static void backToMyLessons(String message) {
		guiMyLessons.ViewMyLessons.displayMyLessons(ViewLessonDetail.theStage,
				ViewLessonDetail.theUser, message);
	}

	/**********
	 * Private local method that shows this lesson again, read fresh, with a message or none.
	 *
	 * @param message	The message to show, or the empty String
	 */
	private static void showAgain(String message) {
		ViewLessonDetail.displayLessonDetail(ViewLessonDetail.theStage, ViewLessonDetail.theUser,
				ViewLessonDetail.theLessonId, message);
	}

	/**********
	 * Private local method that gives a field's current value.
	 *
	 * @param lesson	The lesson
	 * @param field		Lesson.FIELD_TITLE, Lesson.FIELD_CATEGORY, or Lesson.FIELD_BODY
	 *
	 * @return the value of that field
	 */
	private static String currentValue(Lesson lesson, String field) {
		if (Lesson.FIELD_TITLE.equals(field)) return lesson.getTitle();
		if (Lesson.FIELD_CATEGORY.equals(field)) return lesson.getCategory();
		return lesson.getBody();
	}
}
