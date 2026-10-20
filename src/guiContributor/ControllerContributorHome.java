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
 * @version 2.10		2026-10-20 Add a lesson and My lessons now open the Add a Lesson and My
 * 							Lessons pages.  Add experience info still opens the Coming Soon page.
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
	 * <p> Description: This method directs the user to the Add a Lesson page, where a Contributor
	 * adds a new lesson with its core information. </p>
	 *
	 */
	protected static void performAddLesson () {
		guiAddLesson.ViewAddLesson.displayAddLesson(ViewContributorHome.theStage,
				ViewContributorHome.theUser);
	}

	/**********
	 * <p> Method: performMyLessons() </p>
	 *
	 * <p> Description: This method directs the user to the My Lessons page, which lists the
	 * lessons this Contributor has added. </p>
	 *
	 */
	protected static void performMyLessons () {
		guiMyLessons.ViewMyLessons.displayMyLessons(ViewContributorHome.theStage,
				ViewContributorHome.theUser);
	}

	/**********
	 * <p> Method: performAddExperience() </p>
	 *
	 * <p> Opens the Contributor page used to add experience information to
	 * an existing lesson.</p>
	 */
	protected static void performAddExperience() {
		guiAddExperience.ViewAddExperience.displayAddExperience(
				ViewContributorHome.theStage, ViewContributorHome.theUser);
	}

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
