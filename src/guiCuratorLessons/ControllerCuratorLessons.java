package guiCuratorLessons;

/*******
 * <p> Title: ControllerCuratorLessons Class. </p>
 *
 * <p> Description: The Java/FX-based All Lessons page controller.  It provides the actions for the
 * buttons defined by the View class.  Both buttons are navigation: Open moves to the Review a
 * Lesson page and Back returns to the Curator Home page.  Choosing the lesson that Open shows
 * arrives with the feature.</p>
 *
 * <p> The class has been written assuming that the View is the only class that can invoke these
 * methods.  This is why each has been declared "protected".  Do not change any of these methods to
 * public.</p>
 *
 * <p> Tested by: no automated test, since both actions only navigate.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-15 Initial version
 */

public class ControllerCuratorLessons {

	/*-********************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View, which is a singleton instantiated object.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerCuratorLessons() {
	}

	/**********
	 * <p> Method: performOpen() </p>
	 *
	 * <p> Description: This method opens the Review a Lesson page.  Until the list holds real
	 * lessons there is no choice to read, so the page opens with its layout only. </p>
	 *
	 */
	protected static void performOpen() {
		guiCuratorLessonDetail.ViewCuratorLessonDetail.displayCuratorLessonDetail(
				ViewCuratorLessons.theStage, ViewCuratorLessons.theUser);
	}

	/**********
	 * <p> Method: performBackToHome() </p>
	 *
	 * <p> Description: This method returns the Curator to the Curator Home page. </p>
	 *
	 */
	protected static void performBackToHome() {
		guiCurator.ViewCuratorHome.displayCuratorHome(ViewCuratorLessons.theStage,
				ViewCuratorLessons.theUser);
	}
}
