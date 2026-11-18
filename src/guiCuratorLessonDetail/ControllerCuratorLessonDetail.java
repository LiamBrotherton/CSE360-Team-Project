package guiCuratorLessonDetail;

/*******
 * <p> Title: ControllerCuratorLessonDetail Class. </p>
 *
 * <p> Description: The Java/FX-based Review a Lesson page controller.  It provides the actions for
 * the buttons defined by the View class.  Back is navigation.  Every other button is not built yet,
 * so each one tells the Curator what it will do, in the muted line on the page, and changes
 * nothing.</p>
 *
 * <p> The class has been written assuming that the View is the only class that can invoke these
 * methods.  This is why each has been declared "protected".  Do not change any of these methods to
 * public.</p>
 *
 * <p> Tested by: no automated test, since the actions only navigate or show a note.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-15 Initial version
 */

public class ControllerCuratorLessonDetail {

	/*-********************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View, which is a singleton instantiated object.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerCuratorLessonDetail() {
	}

	/**********
	 * <p> Method: performLockField(String field) </p>
	 *
	 * <p> Description: This method will lock one core field of the lesson, so the Contributor can
	 * no longer change it.  It is not built yet, so it only says so. </p>
	 *
	 * @param field		LessonsLearned.FIELD_TITLE, LessonsLearned.FIELD_BODY, or LessonsLearned.FIELD_CATEGORY
	 */
	protected static void performLockField(String field) {
		ViewCuratorLessonDetail.label_Message.setText("Locking the " + field
				+ " is not built yet.  Once it is, the Contributor cannot change it.");
	}

	/**********
	 * <p> Method: performLockLesson() </p>
	 *
	 * <p> Description: This method will lock the whole lesson, so the Contributor can neither
	 * change nor delete it.  It is not built yet, so it only says so. </p>
	 *
	 */
	protected static void performLockLesson() {
		ViewCuratorLessonDetail.label_Message.setText("Locking a whole lesson is not built yet.  "
				+ "Once it is, the Contributor cannot edit or delete it.");
	}

	/**********
	 * <p> Method: performRefine() </p>
	 *
	 * <p> Description: This method will ask an AI tool to help edit and refine the lesson's
	 * details.  It is not built yet, so it only says so. </p>
	 *
	 */
	protected static void performRefine() {
		ViewCuratorLessonDetail.label_Message.setText("Refining with an AI tool is not built yet.  "
				+ "Once it is, it will suggest edits to the lesson.");
	}

	/**********
	 * <p> Method: performBackToLessons() </p>
	 *
	 * <p> Description: This method returns the Curator to the All Lessons page. </p>
	 *
	 */
	protected static void performBackToLessons() {
		guiCuratorLessons.ViewCuratorLessons.displayCuratorLessons(
				ViewCuratorLessonDetail.theStage, ViewCuratorLessonDetail.theUser);
	}
}
