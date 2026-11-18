package guiCuratorSearchTerms;

/*******
 * <p> Title: ControllerCuratorSearchTerms Class. </p>
 *
 * <p> Description: The Java/FX-based Search Terms page controller.  It provides the actions for the
 * buttons defined by the View class.  Back is navigation.  Suggest and Save are not built yet, so
 * each tells the Curator what it will do, in the muted line on the page, and changes nothing.</p>
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

public class ControllerCuratorSearchTerms {

	/*-********************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View, which is a singleton instantiated object.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerCuratorSearchTerms() {
	}

	/**********
	 * <p> Method: performSuggest() </p>
	 *
	 * <p> Description: This method will ask an AI tool for search terms for the lesson chosen in
	 * the drop-down.  It is not built yet, so it only says so. </p>
	 *
	 */
	protected static void performSuggest() {
		ViewCuratorSearchTerms.label_Message.setText("Suggesting terms is not built yet.  "
				+ "Once it is, the suggestions will be listed on the left.");
	}

	/**********
	 * <p> Method: performSave() </p>
	 *
	 * <p> Description: This method will save the terms in the list of terms to keep with the
	 * lesson.  It is not built yet, so it only says so. </p>
	 *
	 */
	protected static void performSave() {
		ViewCuratorSearchTerms.label_Message.setText("Saving terms is not built yet.  "
				+ "Once it is, the terms on the right will be saved.");
	}

	/**********
	 * <p> Method: performBackToHome() </p>
	 *
	 * <p> Description: This method returns the Curator to the Curator Home page. </p>
	 *
	 */
	protected static void performBackToHome() {
		guiCurator.ViewCuratorHome.displayCuratorHome(ViewCuratorSearchTerms.theStage,
				ViewCuratorSearchTerms.theUser);
	}
}
