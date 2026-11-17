package guiAddLesson;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiTools.GuiStyle;

/*******
 * <p> Title: ViewAddLesson Class. </p>
 *
 * <p> Description: The Java/FX-based Add a LessonsLearned Page.  A Contributor enters a new lesson's core
 * information here: its title, its category, and its body, which says what was learned.
 *
 * Save checks the information with the rules in ModelAddLesson.  If any of it breaks a rule, the
 * page shows that rule's message and stays, with what was typed still in place, so it can be
 * fixed.  If all of it is acceptable, the lesson is stored with this Contributor as its author
 * and the My Lessons page opens, showing the new lesson.  Cancel returns to My Lessons and stores
 * nothing.
 *
 * Requirement satisfied, from the Contributor user story: R1, "As a contributor, I can add new
 * lessons learned to the system, specifying core information."
 *
 * The class has been written using a singleton design pattern and is the View portion of the
 * Model, View, Controller pattern.  All access starts by invoking the static method
 * displayAddLesson.  No other method should attempt to instantiate this class as that is
 * controlled by displayAddLesson.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-20 Initial version
 */

public class ViewAddLesson {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	/** The database the new lesson is stored in. */
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	// These are the widget attributes for the GUI.  The page is one card holding the logo, the
	// title, the three fields with their names, a line for messages, and the two buttons.
	private static Label label_PageTitle = new Label("Add a lesson");
	private static Label label_Purpose = new Label(
			"Give the lesson a title, a category, and what was learned.");

	private static Label label_Title = new Label("Title");
	/** The field for the lesson's title. */
	protected static TextField text_Title = new TextField();

	private static Label label_Category = new Label("Category");
	/** The field for the lesson's category. */
	protected static TextField text_Category = new TextField();

	private static Label label_Body = new Label("Body");
	/** The area for the lesson's body: what happened and what was learned. */
	protected static TextArea text_Body = new TextArea();

	/** The line that tells the Contributor why the lesson could not be saved. */
	protected static Label label_Message = new Label();

	private static Button button_Save = new Button("Save");
	private static Button button_Cancel = new Button("Cancel");

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewAddLesson theView;		// Used to determine if instantiation of the class
												// is needed

	/** The Stage that JavaFX has established for us. */
	protected static Stage theStage;
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	/** The current logged in User. */
	protected static User theUser;

	private static Scene theAddLessonScene;		// The shared Scene each invocation populates

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayAddLesson(Stage ps, User user) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Add a LessonsLearned page to be displayed.
	 *
	 * It first sets up the shared attributes so we don't have to pass parameters.  It then checks
	 * to see if the page has been setup.  If not, it instantiates the class, which initializes all
	 * the static aspects of the GUI widgets.  After that, it empties the fields and the message, so
	 * nothing typed on an earlier visit is left behind, sets the Scene onto the stage, and makes it
	 * visible to the user with the typing in the Title field.</p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 *
	 * @param user specifies the User for this GUI and it's methods
	 *
	 */
	public static void displayAddLesson(Stage ps, User user) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewAddLesson();

		// Set the title for the window and display the page
		theStage.setTitle("CSE 360 Foundation Code: Add a LessonsLearned");
		theStage.setScene(theAddLessonScene);
		theStage.show();

		// Every visit starts with an empty form.  The fields are emptied after the page is on the
		// stage, so they are restyled as part of the window they appear in.
		text_Title.clear();
		text_Category.clear();
		text_Body.clear();
		label_Message.setText("");
		text_Title.requestFocus();
	}

	/**********
	 * <p> Method: ViewAddLesson() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and event handlers for each GUI
	 * object.</p>
	 *
	 * This is a singleton and is only performed once.  Subsequent uses empty the form using the
	 * displayAddLesson method.</p>
	 *
	 */
	private ViewAddLesson() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theAddLessonScene = new Scene(theRootPane, width, height);
		GuiStyle.applyBackground(theRootPane);

		// The title and the line below it
		label_PageTitle.setLayoutX(70);
		label_PageTitle.setLayoutY(104);
		GuiStyle.stylePageTitle(label_PageTitle);
		label_Purpose.setLayoutX(70);
		label_Purpose.setLayoutY(142);
		GuiStyle.styleMutedLabel(label_Purpose);

		// The three fields, each with its name above it (R1)
		setupFieldLabelUI(label_Title, 172);
		setupFieldUI(text_Title, "A short name for the lesson", 192);

		setupFieldLabelUI(label_Category, 242);
		setupFieldUI(text_Category, "For example: Design, Testing, Teamwork", 262);

		setupFieldLabelUI(label_Body, 312);
		text_Body.setPromptText("What happened, and what was learned");
		text_Body.setLayoutX(70);
		text_Body.setLayoutY(332);
		text_Body.setPrefSize(660, 110);
		GuiStyle.styleTextArea(text_Body);

		// The line for the message saying why the lesson could not be saved
		label_Message.setLayoutX(70);
		label_Message.setLayoutY(452);
		label_Message.setPrefWidth(660);
		GuiStyle.styleErrorLabel(label_Message);

		// Save is the page's main action; Cancel is secondary
		setupButtonUI(button_Save, 70);
		GuiStyle.stylePrimaryButton(button_Save);
		button_Save.setOnAction((_) -> ControllerAddLesson.performSave());

		setupButtonUI(button_Cancel, 225);
		GuiStyle.styleSecondaryButton(button_Cancel);
		button_Cancel.setOnAction((_) -> ControllerAddLesson.performCancel());

		// Place all of the widget items into the Root Pane's list of children, the card first so it
		// is drawn behind the rest
		theRootPane.getChildren().addAll(
				GuiStyle.createCard(40, 40, 720, 520),
				GuiStyle.createLogo(70, 66),
				label_PageTitle, label_Purpose,
				label_Title, text_Title, label_Category, text_Category, label_Body, text_Body,
				label_Message, button_Save, button_Cancel);
	}

	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	/**********
	 * Private local method to place the small name shown above a field.
	 *
	 * @param l		The Label object to be placed
	 * @param y		The location from the top (y axis)
	 */
	private static void setupFieldLabelUI(Label l, double y) {
		l.setLayoutX(70);
		l.setLayoutY(y);
		GuiStyle.styleFieldLabel(l);
	}

	/**********
	 * Private local method to place and style a one-line field across the card.
	 *
	 * @param t			The TextField object to be placed
	 * @param prompt	The hint shown in the field while it is empty
	 * @param y			The location from the top (y axis)
	 */
	private static void setupFieldUI(TextField t, String prompt, double y) {
		t.setPromptText(prompt);
		t.setLayoutX(70);
		t.setLayoutY(y);
		t.setPrefWidth(660);
		GuiStyle.styleField(t);
	}

	/**********
	 * Private local method to place a button in the row of buttons along the bottom of the card.
	 *
	 * @param b		The Button object to be placed
	 * @param x		The location from the left edge (x axis)
	 */
	private static void setupButtonUI(Button b, double x) {
		b.setMinWidth(140);
		b.setAlignment(Pos.CENTER);
		b.setLayoutX(x);
		b.setLayoutY(490);
	}
}
