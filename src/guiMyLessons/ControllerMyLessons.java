package guiMyLessons;

/*******
 * <p> Title: ControllerMyLessons Class. </p>
 *
 * <p> Description: The Java/FX-based My Lessons Page controller.  This class provides the
 * controller actions for the page's three buttons: Open, which shows the chosen lesson; Add a
 * lesson; and Back to home.
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

public class ControllerMyLessons {

	/*-********************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and
	the Model is often just a stub, or will be a singleton instantiated object.

	 */

	/** The message shown when Open is pressed with no lesson chosen in the list. */
	protected static final String NOTHING_CHOSEN = "Choose a lesson in the list first.";

	/**
	 * Default constructor is not used.
	 */
	public ControllerMyLessons() {
	}

	/**********
	 * <p> Method: performOpen() </p>
	 *
	 * <p> Description: This method opens the Lesson Details page for the lesson chosen in the list
	 * (R3).  With no lesson chosen it says so and stays on this page.  The Lesson Details page
	 * checks again that the lesson still exists and is this Contributor's, so a lesson deleted
	 * by another role since the list was shown is handled there (R6). </p>
	 *
	 */
	protected static void performOpen() {
		int row = ViewMyLessons.list_Lessons.getSelectionModel().getSelectedIndex();
		if (row < 0) {
			ViewMyLessons.label_Message.setText(NOTHING_CHOSEN);
			return;
		}
		guiLessonDetail.ViewLessonDetail.displayLessonDetail(ViewMyLessons.theStage,
				ViewMyLessons.theUser, ViewMyLessons.lessonIds.get(row));
	}

	/**********
	 * <p> Method: performAddLesson() </p>
	 *
	 * <p> Description: This method opens the Add a Lesson page (R1). </p>
	 *
	 */
	protected static void performAddLesson() {
		guiAddLesson.ViewAddLesson.displayAddLesson(ViewMyLessons.theStage, ViewMyLessons.theUser);
	}

	/**********
	 * <p> Method: performBackToHome() </p>
	 *
	 * <p> Description: This method returns the Contributor to the Contributor Home page. </p>
	 *
	 */
	protected static void performBackToHome() {
		guiContributor.ViewContributorHome.displayContributorHome(ViewMyLessons.theStage,
				ViewMyLessons.theUser);
	}
}
