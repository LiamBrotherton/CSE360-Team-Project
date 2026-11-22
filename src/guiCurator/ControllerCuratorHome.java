package guiCurator;


/*******
 * <p> Title: ControllerCuratorHome Class. </p>
 * 
 * <p> Description: The Java/FX-based Curator Home Page.  This class provides the controller
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
 */

public class ControllerCuratorHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerCuratorHome() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method directs the user to the User Update Page so the user can change
	 * the user account attributes. </p>
	 * 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewCuratorHome.theStage, ViewCuratorHome.theUser);
	}	

	/**********
	 * <p> Method: performLessons() </p>
	 *
	 * <p> Description: This method directs the user to the All Lessons page, where a Curator
	 * chooses a lesson to review, lock, or refine. </p>
	 *
	 */
	protected static void performLessons() {
		guiCuratorLessons.ViewCuratorLessons.displayCuratorLessons(ViewCuratorHome.theStage,
				ViewCuratorHome.theUser);
	}

	/**********
	 * <p> Method: performTimeAndEffort() </p>
	 *
	 * <p> Description: This method directs the user to the Time and Effort page, where a Curator
	 * combines the time and effort of several lessons. </p>
	 *
	 */
	protected static void performTimeAndEffort() {
		guiCuratorMerge.ViewCuratorMerge.displayCuratorMerge(ViewCuratorHome.theStage,
				ViewCuratorHome.theUser);
	}

	/**********
	 * <p> Method: performSearchTerms() </p>
	 *
	 * <p> Description: This method directs the user to the Search Terms page, where a Curator builds
	 * the list of search terms for a lesson. </p>
	 *
	 */
	protected static void performSearchTerms() {
		guiCuratorSearchTerms.ViewCuratorSearchTerms.displayCuratorSearchTerms(
				ViewCuratorHome.theStage, ViewCuratorHome.theUser);
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewCuratorHome.theStage);
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
	
	// Here is the new performAITools method
    protected static void performAITools() {
        guiCuratorAI.ViewCuratorAI.displayCuratorAI(
                ViewCuratorHome.theStage, ViewCuratorHome.theUser);
    }
}
