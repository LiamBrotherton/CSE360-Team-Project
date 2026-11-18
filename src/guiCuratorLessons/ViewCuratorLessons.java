package guiCuratorLessons;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import entityClasses.User;
import guiTools.GuiStyle;

/*******
 * <p> Title: ViewCuratorLessons Class. </p>
 *
 * <p> Description: The Java/FX-based All Lessons page for a Curator.  It is the page from which a
 * Curator chooses a lesson to review, lock, or refine, so it serves the Curator epics on locking
 * lessons and fields and on refining lesson details.</p>
 *
 * <p> This page is a layout only.  The list is empty and Open goes straight to the Review a Lesson
 * page, so the page can be seen and moved through before the feature behind it exists.  Reading the
 * lessons from the database and opening the one that was chosen arrive with the feature.</p>
 *
 * <p> The class has been written using a singleton design pattern and is the View portion of the
 * Model, View, Controller pattern.  All access starts by invoking the static method
 * displayCuratorLessons.  There is no Model yet, because there is no data to hold until the
 * feature is built.</p>
 *
 * <p> Tested by: no automated test, since the page only navigates.  It is covered by the manual
 * checks of the Curator pages.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-15 Initial version, a layout without the feature
 */

public class ViewCuratorLessons {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// These are the widget attributes for the GUI.  The page is one card holding the logo, the
	// title, the list of lessons, and the buttons.
	private static Label label_PageTitle = new Label("All lessons");
	private static Label label_Purpose = new Label(
			"Every lesson in the system.  Choose one and press Open to review, lock, or refine it.");

	/** The list of lessons, one row per lesson.  Empty until the feature is built. */
	protected static ListView<String> list_Lessons = new ListView<>();

	private static Button button_Open = new Button("Open");
	private static Button button_BackToHome = new Button("Back to home");

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewCuratorLessons theView;	// Used to determine if instantiation of the class
												// is needed

	/** The Stage that JavaFX has established for us. */
	protected static Stage theStage;
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	/** The current logged in User. */
	protected static User theUser;

	private static Scene theCuratorLessonsScene;	// The shared Scene each invocation populates

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayCuratorLessons(Stage ps, User user) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the All Lessons page to be displayed.
	 *
	 * It first sets up the shared attributes so we don't have to pass parameters.  It then checks
	 * to see if the page has been setup.  If not, it instantiates the class, which initializes all
	 * the static aspects of the GUI widgets.  It then sets the Scene onto the stage and makes it
	 * visible.</p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 *
	 * @param user specifies the User for this GUI and it's methods
	 *
	 */
	public static void displayCuratorLessons(Stage ps, User user) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewCuratorLessons();

		// Set the title for the window, display the page, and wait for the Curator to act
		theStage.setTitle("CSE 360 Foundation Code: All Lessons");
		theStage.setScene(theCuratorLessonsScene);
		theStage.show();
		GuiStyle.startUnfocused(theRootPane);	// No button looks pressed on arrival
	}

	/**********
	 * <p> Method: ViewCuratorLessons() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and event handlers for each GUI
	 * object.</p>
	 *
	 * This is a singleton and is only performed once.</p>
	 *
	 */
	private ViewCuratorLessons() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theCuratorLessonsScene = new Scene(theRootPane, width, height);
		GuiStyle.applyBackground(theRootPane);

		// The title and the line below it
		label_PageTitle.setLayoutX(70);
		label_PageTitle.setLayoutY(104);
		GuiStyle.stylePageTitle(label_PageTitle);
		label_Purpose.setLayoutX(70);
		label_Purpose.setLayoutY(142);
		GuiStyle.styleMutedLabel(label_Purpose);

		// The list of lessons.  Until the feature is built it shows the line below in its place.
		list_Lessons.setLayoutX(70);
		list_Lessons.setLayoutY(172);
		list_Lessons.setPrefSize(660, 270);
		Label empty = new Label("The lessons will be listed here.");
		GuiStyle.styleMutedLabel(empty);
		list_Lessons.setPlaceholder(empty);
		GuiStyle.styleList(list_Lessons);

		// Open is the page's main action; Back is secondary
		setupButtonUI(button_Open, 140, 70);
		GuiStyle.stylePrimaryButton(button_Open);
		button_Open.setOnAction((_) -> ControllerCuratorLessons.performOpen());

		setupButtonUI(button_BackToHome, 160, 570);
		GuiStyle.styleSecondaryButton(button_BackToHome);
		button_BackToHome.setOnAction((_) -> ControllerCuratorLessons.performBackToHome());

		// Place all of the widget items into the Root Pane's list of children, the card first so it
		// is drawn behind the rest
		theRootPane.getChildren().addAll(
				GuiStyle.createCard(40, 40, 720, 520),
				GuiStyle.createLogo(70, 66),
				label_PageTitle, label_Purpose, list_Lessons,
				button_Open, button_BackToHome);
	}

	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

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
