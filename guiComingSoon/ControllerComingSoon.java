package guiComingSoon;

/*******
 * <p> Title: ControllerComingSoon Class. </p>
 *
 * <p> Description: The Java/FX-based Coming Soon Page controller.  This class provides the
 * controller action for the page's one button, Back, which returns the user to the home page of
 * the role the user is playing.
 *
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-16 Initial version
 */

public class ControllerComingSoon {

	/*-********************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and
	the Model is often just a stub, or will be a singleton instantiated object.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerComingSoon() {
	}

	/**********
	 * <p> Method: performBack() </p>
	 *
	 * <p> Description: This method returns the user to the home page of the role being played.
	 * Which role that is was recorded when the home page was displayed, so a person with more than
	 * one role returns to the role they chose and not to the role chooser.  The choice of page is
	 * the same one the Account Update page makes when it returns to a home page. </p>
	 *
	 */
	protected static void performBack() {
		int theRole = applicationMain.FoundationsMain.activeHomePage;

		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(ViewComingSoon.theStage, ViewComingSoon.theUser);
			break;
		case 2:
			guiContributor.ViewContributorHome.displayContributorHome(ViewComingSoon.theStage,
					ViewComingSoon.theUser);
			break;
		case 3:
			guiViewer.ViewViewerHome.displayViewerHome(ViewComingSoon.theStage, ViewComingSoon.theUser);
			break;
		case 4:
			guiCurator.ViewCuratorHome.displayCuratorHome(ViewComingSoon.theStage, ViewComingSoon.theUser);
			break;
		default:
			System.out.println("*** ERROR *** ComingSoon performBack has an invalid role: " + theRole);
			System.exit(0);
		}
	}
}
