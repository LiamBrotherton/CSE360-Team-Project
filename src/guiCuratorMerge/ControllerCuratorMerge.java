package guiCuratorMerge;

import java.util.ArrayList;
import java.util.List;
import entityClasses.LessonsLearned;

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
 * @author Liam Brotherton
 *
 * @version 1.00		2026-11-15 Initial version
 * @version 1.10        2026-11-19 Save checkbox changes
 * @version 1.20		2026-11-22 Merge reads the chosen lessons and requires at least two
 */

public class ControllerCuratorMerge {

	/*-********************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View, which is a singleton instantiated object.

	 */
	/** The message shown when the database refuses to store a lesson that passed every check. */
	public static final String SAVE_FAILED = "The summary could not be saved. Try again.";

	/**
	 * Default constructor is not used.
	 */
	public ControllerCuratorMerge() {
	}

	/**********
	 * <p> Method: performMerge() </p>
	 *
	 * <p> Description: This method reads the lessons chosen in the list, requires at least two, and
	 * will merge their time and effort data into one statistical summary.  The merge itself is not
	 * built yet, so after the check it only says so. </p>
	 *
	 */
	protected static void performMerge() {

		// Turn the chosen rows back into lesson IDs
		List<Integer> chosenIds = new ArrayList<>();
		for (int index : ViewCuratorMerge.list_Lessons.getSelectionModel().getSelectedIndices()) {
			chosenIds.add(ViewCuratorMerge.lessonIds.get(index));
		}

		if (chosenIds.size() < 2) {
			ViewCuratorMerge.label_Message.setText("Choose at least two lessons to merge.");
			return;
		}

		ViewCuratorMerge.label_Message.setText(chosenIds.size() + " lessons merged");
		
		StringBuilder Time = new StringBuilder();
		for (int id : chosenIds) {
			LessonsLearned lesson = ViewCuratorMerge.theDatabase.getLessonById(id);	
			if (Time.length() > 0) Time.append(" + ");
			Time.append(lesson.getEffort());								
		}
		
		StringBuilder Effort = new StringBuilder();
		for (int id : chosenIds) {
			LessonsLearned lesson = ViewCuratorMerge.theDatabase.getLessonById(id);	
			if (Effort.length() > 0) Effort.append(" + ");
			Effort.append(lesson.getEffort());								
		}
		
		StringBuilder Title = new StringBuilder();
		for (int id : chosenIds) {
			LessonsLearned lesson = ViewCuratorMerge.theDatabase.getLessonById(id);	
			if (Title.length() > 0) Title.append(" + ");
			Title.append(lesson.getTitle());								
		}
		
		String summary = "Lessons merged: " + chosenIds.size() + ", "+ Title.toString() + "\n"
				+ "Time: " + Time.toString() + "\n"
				+ "Effort: " + Effort.toString();
		
		ViewCuratorMerge.area_Summary.setText(summary);
		for (int id : chosenIds) {
			if (!ViewCuratorMerge.theDatabase.saveCuratorSummary(id, summary, ViewCuratorMerge.theUser)) {
				ViewCuratorMerge.label_Message.setText(SAVE_FAILED);
			}
		}
		return;
		
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