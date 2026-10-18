package guiContributor;


/*******
 * <p> Title: ControllerContributorHome Class. </p>
 * 
 * <p> Description: The Java/FX-based Contributor Home Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page is a stub for establish future roles for the application.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation *
 * @version 2.00		2026-10-16 Added the performAddLesson, performMyLessons, and
 * 							performAddExperience actions for the three TP2 feature buttons.  Each
 * 							opens the Coming Soon page until its feature is built.
 */

public class ControllerContributorHome {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerContributorHome() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method directs the user to the User Update Page so the user can change
	 * the user account attributes. </p>
	 * 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewContributorHome.theStage, ViewContributorHome.theUser);
	}	

	/**********
	 * <p> Method: performAddLesson() </p>
	 *
	 * <p> Description: This method directs the user to the Coming Soon page for adding a lesson.
	 * It is a placeholder until the Add a Lesson page has been built. </p>
	 *
	 */
	protected static void performAddLesson () {
		guiComingSoon.ViewComingSoon.displayComingSoon(ViewContributorHome.theStage,
				ViewContributorHome.theUser, "Add a lesson");
	}

	/**********
	 * <p> Method: performMyLessons() </p>
	 *
	 * <p> Description: This method directs the user to the Coming Soon page for the list of the
	 * lessons this Contributor has added.  It is a placeholder until the My Lessons page has been
	 * built. </p>
	 *
	 */
	protected static void performMyLessons () {
		guiComingSoon.ViewComingSoon.displayComingSoon(ViewContributorHome.theStage,
				ViewContributorHome.theUser, "My lessons");
	}

	/**********
	 * <p> Method: performAddExperience() </p>
	 *
	 * <p> Description: This method directs the user to the Coming Soon page for adding experience
	 * information to a lesson.  It is a placeholder until that page has been built. </p>
	 *
	 */
	protected static void performAddExperience () {
		guiComingSoon.ViewComingSoon.displayComingSoon(ViewContributorHome.theStage,
				ViewContributorHome.theUser, "Add experience info");
	}

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewContributorHome.theStage);
	}
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	protected static void performQuit() {
		System.exit(0);
	}
}
