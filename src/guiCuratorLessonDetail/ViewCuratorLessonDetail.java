package guiCuratorLessonDetail;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import entityClasses.LessonsLearned;
import entityClasses.User;
import guiTools.GuiStyle;

/*******
 * <p> Title: ViewCuratorLessonDetail Class. </p>
 *
 * <p> Description: The Java/FX-based Review a Lesson page for a Curator.  It shows the core
 * information of one lesson, a Lock button for each core field, a button to lock the whole lesson,
 * and a button to refine the lesson with an AI tool.  It serves the Curator epics on locking
 * lessons and fields and on using an AI tool to edit and refine lesson details.</p>
 *
 * <p> This page is a layout only.  The values are placeholders and each action button says what it
 * will do once the feature is built.  Showing the chosen lesson, locking, and refining arrive with
 * the feature.</p>
 *
 * <p> The class has been written using a singleton design pattern and is the View portion of the
 * Model, View, Controller pattern.  All access starts by invoking the static method
 * displayCuratorLessonDetail.  There is no Model yet, because there is no data to hold until the
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

public class ViewCuratorLessonDetail {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// The column on the card where each row's name and value start, and the width of a value.  The
	// Lock buttons sit to the right of the value column.
	private static final double rowX = 70;
	private static final double valueWidth = 520;

	// These are the widget attributes for the GUI.  The page is one card holding the logo, the
	// title, one row for each core field, a line for messages, and the buttons.
	private static Label label_PageTitle = new Label("Review a lesson");
	private static Label label_Purpose = new Label(
			"See a lesson's core information, lock what the Contributor must not change, or refine the text.");

	private static Label label_Title = new Label("Title");
	private static Label label_TitleValue = new Label("The lesson's title will appear here.");
	private static Button button_LockTitle = new Button("Lock");

	private static Label label_Category = new Label("Category");
	private static Label label_CategoryValue = new Label("The lesson's category will appear here.");
	private static Button button_LockCategory = new Button("Lock");

	private static Label label_Body = new Label("Body");
	private static TextArea area_Body = new TextArea();
	private static Button button_LockBody = new Button("Lock");

	/** The line that tells the Curator what a button will do once it is built. */
	protected static Label label_Message = new Label();

	private static Button button_LockLesson = new Button("Lock whole lesson");
	private static Button button_Refine = new Button("Refine with AI");
	private static Button button_BackToLessons = new Button("Back to all lessons");

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewCuratorLessonDetail theView;		// Used to determine if instantiation of the
														// class is needed

	/** The Stage that JavaFX has established for us. */
	protected static Stage theStage;
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	/** The current logged in User. */
	protected static User theUser;

	private static Scene theCuratorLessonDetailScene;	// The shared Scene each invocation populates

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayCuratorLessonDetail(Stage ps, User user) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Review a Lesson page to be displayed.
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
	public static void displayCuratorLessonDetail(Stage ps, User user) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewCuratorLessonDetail();

		label_Message.setText("");

		// Set the title for the window, display the page, and wait for the Curator to act
		theStage.setTitle("CSE 360 Foundation Code: Review a Lesson");
		theStage.setScene(theCuratorLessonDetailScene);
		theStage.show();
		GuiStyle.startUnfocused(theRootPane);	// No button looks pressed on arrival
	}

	/**********
	 * <p> Method: ViewCuratorLessonDetail() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and event handlers for each GUI
	 * object.</p>
	 *
	 * This is a singleton and is only performed once.</p>
	 *
	 */
	private ViewCuratorLessonDetail() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theCuratorLessonDetailScene = new Scene(theRootPane, width, height);
		GuiStyle.applyBackground(theRootPane);

		// The title and the line below it
		label_PageTitle.setLayoutX(rowX);
		label_PageTitle.setLayoutY(100);
		GuiStyle.stylePageTitle(label_PageTitle);
		label_Purpose.setLayoutX(rowX);
		label_Purpose.setLayoutY(138);
		GuiStyle.styleMutedLabel(label_Purpose);

		// Title and Category: a name, the value below it, and Lock to the right
		setupRowUI(label_Title, label_TitleValue, button_LockTitle, 170);
		button_LockTitle.setOnAction((_) -> ControllerCuratorLessonDetail.performLockField(LessonsLearned.FIELD_TITLE));

		setupRowUI(label_Category, label_CategoryValue, button_LockCategory, 230);
		button_LockCategory.setOnAction((_) -> ControllerCuratorLessonDetail.performLockField(LessonsLearned.FIELD_CATEGORY));

		// Body: the value can be long, so it sits in a fixed area that scrolls.  It is a text area
		// that cannot be edited, because this page shows the lesson and does not change it.
		setupRowUI(label_Body, null, button_LockBody, 290);
		button_LockBody.setOnAction((_) -> ControllerCuratorLessonDetail.performLockField(LessonsLearned.FIELD_BODY));
		area_Body.setPromptText("The lesson's body will appear here.");
		area_Body.setEditable(false);
		area_Body.setLayoutX(rowX);
		area_Body.setLayoutY(312);
		area_Body.setPrefSize(valueWidth, 130);
		GuiStyle.styleTextArea(area_Body);

		// The line for what a button will do once it is built.  It is muted, not red, because
		// nothing has gone wrong.
		label_Message.setLayoutX(rowX);
		label_Message.setLayoutY(452);
		label_Message.setPrefWidth(660);
		label_Message.setWrapText(true);
		GuiStyle.styleMutedLabel(label_Message);

		// Refine is the page's main action; the others are secondary
		setupButtonUI(button_LockLesson, 180, rowX);
		GuiStyle.styleSecondaryButton(button_LockLesson);
		button_LockLesson.setOnAction((_) -> ControllerCuratorLessonDetail.performLockLesson());

		setupButtonUI(button_Refine, 170, 270);
		GuiStyle.stylePrimaryButton(button_Refine);
		button_Refine.setOnAction((_) -> ControllerCuratorLessonDetail.performRefine());

		setupButtonUI(button_BackToLessons, 190, 540);
		GuiStyle.styleSecondaryButton(button_BackToLessons);
		button_BackToLessons.setOnAction((_) -> ControllerCuratorLessonDetail.performBackToLessons());

		// Place all of the widget items into the Root Pane's list of children, the card first so it
		// is drawn behind the rest
		theRootPane.getChildren().addAll(
				GuiStyle.createCard(40, 40, 720, 520),
				GuiStyle.createLogo(rowX, 66),
				label_PageTitle, label_Purpose,
				label_Title, label_TitleValue, button_LockTitle,
				label_Category, label_CategoryValue, button_LockCategory,
				label_Body, area_Body, button_LockBody,
				label_Message, button_LockLesson, button_Refine, button_BackToLessons);
	}

	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	/**********
	 * Private local method to place one row: the field's name in small gray text, its value below
	 * it, and the Lock button to the right of both.
	 *
	 * @param name		The Label holding the name of the field
	 * @param value		The Label holding its value, or null when the value is placed separately
	 * @param lock		The row's Lock Button
	 * @param y			The location of the row from the top (y axis)
	 */
	private static void setupRowUI(Label name, Label value, Button lock, double y) {
		name.setLayoutX(rowX);
		name.setLayoutY(y);
		GuiStyle.styleFieldLabel(name);

		if (value != null) {
			value.setLayoutX(rowX);
			value.setLayoutY(y + 22);
			value.setPrefWidth(valueWidth);
			GuiStyle.styleMutedLabel(value);
		}

		lock.setMinWidth(110);
		lock.setAlignment(Pos.CENTER);
		lock.setLayoutX(rowX + valueWidth + 30);
		lock.setLayoutY(y + 4);
		GuiStyle.styleSecondaryButton(lock);
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
