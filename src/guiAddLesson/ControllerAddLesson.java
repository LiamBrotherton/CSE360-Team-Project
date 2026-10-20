package guiAddLesson;

/*******
 * <p> Title: ControllerAddLesson Class. </p>
 *
 * <p> Description: The Java/FX-based Add a Lesson Page controller.  This class provides the
 * controller actions for the page's two buttons, Save and Cancel.
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

public class ControllerAddLesson {

	/*-********************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and
	the Model is often just a stub, or will be a singleton instantiated object.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerAddLesson() {
	}

	/**********
	 * <p> Method: performSave() </p>
	 *
	 * <p> Description: This method passes what was typed to ModelAddLesson.addLesson, which checks
	 * it and stores it with this Contributor as the author (R1).  If a rule was broken, its message
	 * is shown on the page and the typing is kept so it can be fixed.  If the lesson was stored,
	 * the My Lessons page opens and lists it (R2). </p>
	 *
	 */
	protected static void performSave() {
		String problem = ModelAddLesson.addLesson(ViewAddLesson.theDatabase,
				ViewAddLesson.theUser.getUserName(), ViewAddLesson.text_Title.getText(),
				ViewAddLesson.text_Category.getText(), ViewAddLesson.text_Body.getText());
		if (!problem.isEmpty()) {
			ViewAddLesson.label_Message.setText(problem);
			return;
		}
		guiMyLessons.ViewMyLessons.displayMyLessons(ViewAddLesson.theStage, ViewAddLesson.theUser);
	}

	/**********
	 * <p> Method: performCancel() </p>
	 *
	 * <p> Description: This method returns to the My Lessons page without storing anything. </p>
	 *
	 */
	protected static void performCancel() {
		guiMyLessons.ViewMyLessons.displayMyLessons(ViewAddLesson.theStage, ViewAddLesson.theUser);
	}
}
