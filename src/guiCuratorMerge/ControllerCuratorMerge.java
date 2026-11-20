package guiCuratorMerge;

/*******
 * <p> Title: ControllerCuratorMerge Class. </p>
 *
 * <p> Description: The Java/FX-based Time and Effort page controller.  It provides the actions for
 * the controls defined by the View class.  Back is navigation.  Merge and the visibility choice are
 * not built yet, so Merge displays a note. Visibility changes are saved in H2 and
 * require Curator authorization.</p>
 *
 * <p> The class has been written assuming that the View is the only class that can invoke these
 * methods.  This is why each has been declared "protected".  Do not change any of these methods to
 * public.</p>
 *
 * <p> Tested by: testing.CuratorSummaryVisibilityTest verifies persistence and access;
 * JavaFX interactions also need manual testing.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 * @author Hassan Mohamed (visibility integration)
 *
 * @version 1.00		2026-11-15 Initial version
 * @version 1.10        2026-11-19 Save checkbox changes
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
	 * <p> Description: Saves the checkbox state for the currently selected Curator
     * summary and refreshes the screen from the database. </p>
	 *
	 */
	protected static void performVisibilityChange() {
		Integer lessonId = ViewCuratorMerge.targetLessonIds.get(
                ViewCuratorMerge.combo_TargetLesson.getValue());
        if (lessonId == null) {
            ViewCuratorMerge.label_Message.setText("Select a saved summary first.");
            ViewCuratorMerge.refreshSelectedSummary();
            return;
        }
        boolean visible = ViewCuratorMerge.check_ShowToContributors.isSelected();
        boolean saved = applicationMain.FoundationsMain.database.setCuratorSummaryVisibility(
                lessonId, visible, ViewCuratorMerge.theUser);
        ViewCuratorMerge.refreshSelectedSummary();
        ViewCuratorMerge.label_Message.setText(saved
                ? (visible ? "Statistics are now shared with the original Contributor."
                           : "Statistics are now hidden from Contributors.")
                : "Could not save visibility. Verify that you are a Curator and a summary exists.");
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
