package guiCuratorSearchTerms;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import entityClasses.User;
import guiTools.GuiStyle;

/*******
 * <p> Title: ViewCuratorSearchTerms Class. </p>
 *
 * <p> Description: The Java/FX-based Search Terms page for a Curator.  The Curator chooses a lesson,
 * asks an AI tool to suggest search terms, and keeps the terms that would help people find the
 * lesson when their words do not exactly match the lesson's text.  It serves the Curator epic on
 * using an AI tool to build a list of search terms.</p>
 *
 * <p> This page is a layout only.  The lesson list and both term lists are empty, and the buttons
 * say what they will do once the feature is built.  Suggesting and saving terms arrive with the
 * feature.</p>
 *
 * <p> The class has been written using a singleton design pattern and is the View portion of the
 * Model, View, Controller pattern.  All access starts by invoking the static method
 * displayCuratorSearchTerms.  There is no Model yet, because there is no data to hold until the
 * feature is built.</p>
 *
 * <p> Tested by: no automated test, since the page only navigates and shows notes.  It is covered by
 * the manual checks of the Curator pages.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-15 Initial version, a layout without the feature
 */

public class ViewCuratorSearchTerms {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// These are the widget attributes for the GUI.  The page is one card holding the logo, the
	// title, the lesson to work on, the suggested terms on the left and the terms to keep on the
	// right, a line for messages, and the buttons.
	private static Label label_PageTitle = new Label("Search terms");
	private static Label label_Purpose = new Label(
			"Build a list of words that help people find a lesson when they do not use the lesson's own words.");

	private static Label label_Lesson = new Label("Lesson");

	/** The drop-down that chooses the lesson.  Empty until the feature is built. */
	protected static ComboBox<String> combo_Lesson = new ComboBox<>();

	private static Label label_Suggested = new Label("Suggested terms");

	/** The terms the AI tool suggested.  Empty until the feature is built. */
	protected static ListView<String> list_Suggested = new ListView<>();

	private static Label label_Kept = new Label("Terms to keep");

	/** The terms the Curator chose to keep.  Empty until the feature is built. */
	protected static ListView<String> list_Kept = new ListView<>();

	/** The line that tells the Curator what a button will do once it is built. */
	protected static Label label_Message = new Label();

	private static Button button_Suggest = new Button("Suggest terms");
	private static Button button_Save = new Button("Save terms");
	private static Button button_BackToHome = new Button("Back to home");

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewCuratorSearchTerms theView;	// Used to determine if instantiation of the
													// class is needed

	/** The Stage that JavaFX has established for us. */
	protected static Stage theStage;
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	/** The current logged in User. */
	protected static User theUser;

	private static Scene theCuratorSearchTermsScene;	// The shared Scene each invocation populates

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayCuratorSearchTerms(Stage ps, User user) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Search Terms page to be displayed.
	 *
	 * It first sets up the shared attributes so we don't have to pass parameters.  It then checks
	 * to see if the page has been setup.  If not, it instantiates the class, which initializes all
	 * the static aspects of the GUI widgets.  It then clears the message, sets the Scene onto the
	 * stage, and makes it visible.</p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 *
	 * @param user specifies the User for this GUI and it's methods
	 *
	 */
	public static void displayCuratorSearchTerms(Stage ps, User user) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewCuratorSearchTerms();

		label_Message.setText("");

		// Set the title for the window, display the page, and wait for the Curator to act
		theStage.setTitle("CSE 360 Foundation Code: Search Terms");
		theStage.setScene(theCuratorSearchTermsScene);
		theStage.show();
		GuiStyle.startUnfocused(theRootPane);	// No button looks pressed on arrival
	}

	/**********
	 * <p> Method: ViewCuratorSearchTerms() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and event handlers for each GUI
	 * object.</p>
	 *
	 * This is a singleton and is only performed once.</p>
	 *
	 */
	private ViewCuratorSearchTerms() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theCuratorSearchTermsScene = new Scene(theRootPane, width, height);
		GuiStyle.applyBackground(theRootPane);

		// The title and the line below it
		label_PageTitle.setLayoutX(70);
		label_PageTitle.setLayoutY(104);
		GuiStyle.stylePageTitle(label_PageTitle);
		label_Purpose.setLayoutX(70);
		label_Purpose.setLayoutY(142);
		GuiStyle.styleMutedLabel(label_Purpose);

		// The lesson to work on
		label_Lesson.setLayoutX(70);
		label_Lesson.setLayoutY(176);
		GuiStyle.styleFieldLabel(label_Lesson);
		combo_Lesson.setPromptText("Choose a lesson");
		combo_Lesson.setLayoutX(70);
		combo_Lesson.setLayoutY(198);
		combo_Lesson.setPrefWidth(660);
		GuiStyle.styleComboBox(combo_Lesson);

		// The suggested terms on the left, and the terms to keep on the right
		setupListUI(label_Suggested, list_Suggested, 70, "Suggested terms will appear here.");
		setupListUI(label_Kept, list_Kept, 420, "The terms you keep will appear here.");

		// The line for what a button will do once it is built.  It is muted, not red, because
		// nothing has gone wrong.
		label_Message.setLayoutX(70);
		label_Message.setLayoutY(452);
		label_Message.setPrefWidth(660);
		label_Message.setWrapText(true);
		GuiStyle.styleMutedLabel(label_Message);

		// Suggest is the page's main action; the others are secondary
		setupButtonUI(button_Suggest, 160, 70);
		GuiStyle.stylePrimaryButton(button_Suggest);
		button_Suggest.setOnAction((_) -> ControllerCuratorSearchTerms.performSuggest());

		setupButtonUI(button_Save, 160, 250);
		GuiStyle.styleSecondaryButton(button_Save);
		button_Save.setOnAction((_) -> ControllerCuratorSearchTerms.performSave());

		setupButtonUI(button_BackToHome, 160, 570);
		GuiStyle.styleSecondaryButton(button_BackToHome);
		button_BackToHome.setOnAction((_) -> ControllerCuratorSearchTerms.performBackToHome());

		// Place all of the widget items into the Root Pane's list of children, the card first so it
		// is drawn behind the rest
		theRootPane.getChildren().addAll(
				GuiStyle.createCard(40, 40, 720, 520),
				GuiStyle.createLogo(70, 66),
				label_PageTitle, label_Purpose,
				label_Lesson, combo_Lesson,
				label_Suggested, list_Suggested, label_Kept, list_Kept,
				label_Message, button_Suggest, button_Save, button_BackToHome);
	}

	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	/**********
	 * Private local method to place one of the two term lists: its name in small gray text and the
	 * list below it, with a line shown in the list while it is empty.
	 *
	 * @param name		The Label holding the name of the list
	 * @param list		The ListView to be placed
	 * @param x			The location from the left edge (x axis)
	 * @param emptyText	The line shown while the list is empty
	 */
	private static void setupListUI(Label name, ListView<String> list, double x, String emptyText) {
		name.setLayoutX(x);
		name.setLayoutY(250);
		GuiStyle.styleFieldLabel(name);

		list.setLayoutX(x);
		list.setLayoutY(272);
		list.setPrefSize(310, 170);
		Label empty = new Label(emptyText);
		GuiStyle.styleMutedLabel(empty);
		list.setPlaceholder(empty);
		GuiStyle.styleList(list);
	}

	/**********
	 * Private local method to place a button in the row of buttons along the bottom of the card.
	 *
	 * @param b		The Button object to be placed
	 * @param w		The width of the Button
	 * @param x		The location from the left edge (x axis)
	 */
	private static void setupButtonUI(Button b, double w, double x) {
		b.setMinWidth(w);
		b.setAlignment(Pos.CENTER);
		b.setLayoutX(x);
		b.setLayoutY(490);
	}
}
