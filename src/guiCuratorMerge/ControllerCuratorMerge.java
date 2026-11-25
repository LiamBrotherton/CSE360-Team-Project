package guiCuratorMerge;

import java.util.ArrayList;
import java.util.List;
import entityClasses.LessonsLearned;

/*******
 * <p> Title: ControllerCuratorMerge Class. </p>
 *
 * <p> Description: The Java/FX-based Time and Effort page controller.  It provides the actions for
 * the controls defined by the View class.  Back is navigation.  Merge checks that at least two
 * lessons are chosen and then says the merge is not built yet.  The visibility choice is not built
 * yet, so it tells the Curator what it will do, in the muted line on the page, and changes
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
 * @author Dhruv, Liam
 *
 * @version 1.01		2026-11-22 Merge reads the chosen lessons and requires at least two
 */

public class ControllerCuratorMerge {

	/*-********************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View, which is a singleton instantiated object.

	 */
	/** The message shown when the database refuses to store a lesson that passed every check. */
	public static final String SAVE_FAILED = "The lesson could not be saved. Try again.";
	
	/** The ID the database gave the most recently added lesson, or -1 if the last add stored
	 *  nothing.  The page uses it to know the add succeeded; the tests use it to find the lesson. */
	public static int lastAddedId = -1;

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
	protected static String performMerge() {

		// Turn the chosen rows back into lesson IDs
		List<Integer> chosenIds = new ArrayList<>();
		for (int index : ViewCuratorMerge.list_Lessons.getSelectionModel().getSelectedIndices()) {
			chosenIds.add(ViewCuratorMerge.lessonIds.get(index));
		}

		if (chosenIds.size() < 2) {
			ViewCuratorMerge.label_Message.setText("Choose at least two lessons to merge.");
			return "";
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
		
		String body = "Time and Effort merge of " + Title;
		
		String username = ViewCuratorMerge.theUser.getUserName();
		
		ViewCuratorMerge.area_Summary.setText(
				"Lessons merged: " + chosenIds.size() + "\n"
				+ "Time: " + Time.toString() + "\n"
				+ "Effort: " + Effort.toString());
		
		lastAddedId = -1;
		int id = ViewCuratorMerge.theDatabase.addLesson(Title.toString(), body, "Time and Effort Merge", username);
		if (id < 0) return SAVE_FAILED;
		
		boolean saved = ViewCuratorMerge.theDatabase.addExperienceInformation(id, " ",
                " ", Time.toString(), Effort.toString());
        return saved ? "" : SAVE_FAILED;
		
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