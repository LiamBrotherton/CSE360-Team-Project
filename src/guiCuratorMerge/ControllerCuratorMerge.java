package guiCuratorMerge;

/*******
 * <p> Title: ControllerCuratorMerge Class. </p>
 *
 * <p> Description: The Java/FX-based Time and Effort page controller.  It provides the actions for
 * the controls defined by the View class.  Back is navigation.  Merge and the visibility choice are
 * not built yet, so each tells the Curator what it will do, in the muted line on the page, and
 * changes nothing.</p>
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

public class ControllerCuratorMerge {

	/*-********************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View, which is a singleton instantiated object.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerCuratorMerge() {
	}

	/**********
	 * <p> Method: performMerge() </p>
	 *
	 * <p> Description: This method will merge the time and effort data of the lessons chosen in the
	 * list into one statistical summary.  It is not built yet, so it only says so. </p>
	 *
	 */
	protected static void performMerge() {
		ViewCuratorMerge.label_Message.setText("Merging is not built yet.  "
				+ "Once it is, the chosen lessons will be combined into one summary.");
	}

	/**********
	 * <p> Method: performVisibilityChange() </p>
	 *
	 * <p> Description: This method will save whether Contributors can see the combined summary.  It
	 * is not built yet, so it only says so, and the box is put back to unchecked so the page does
	 * not show a choice that was never saved. </p>
	 *
	 */
	protected static void performVisibilityChange() {
		ViewCuratorMerge.check_ShowToContributors.setSelected(false);
		ViewCuratorMerge.label_Message.setText("Choosing who can see the summary is not built yet.  "
				+ "Once it is, this box will decide it.");
	}

	/**********
	 * <p> Method: performBackToHome() </p>
	 *
	 * <p> Description: This method returns the Curator to the Curator Home page. </p>
	 *
	 */
	protected static void performBackToHome() {
		guiCurator.ViewCuratorHome.displayCuratorHome(ViewCuratorMerge.theStage,
				ViewCuratorMerge.theUser);
	}
}
