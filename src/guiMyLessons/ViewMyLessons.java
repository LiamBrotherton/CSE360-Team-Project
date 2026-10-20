package guiMyLessons;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import entityClasses.User;
import guiTools.GuiStyle;

/*******
 * <p> Title: ViewMyLessons Class. </p>
 *
 * <p> Description: The Java/FX-based My Lessons Page.  It shows a Contributor the lessons that
 * Contributor has added, as a list, and is where the Contributor goes to open one of them, to add
 * another, or to return to the Contributor Home page.
 *
 * Each row of the list gives a lesson's title and category and, when a Curator or Admin has
 * locked any of its fields, how many.  The wording of a row and the choice of which lessons are
 * listed are decided by ModelMyLessons, so the testing automation checks the same rules this page
 * shows.  When the Contributor has not added any lessons the list says so.
 *
 * Requirements satisfied, from the Contributor user story: R2, "Once a lesson has been added to
 * the system, I can see ... it"; part of R3, seeing which lessons have locked fields; R6, a change,
 * lock, or deletion made by another role shows the next time the list is displayed, because the
 * list is read fresh each time; R7, only this Contributor's lessons are listed.
 *
 * The class has been written using a singleton design pattern and is the View portion of the
 * Model, View, Controller pattern.  All access starts by invoking the static method
 * displayMyLessons.  No other method should attempt to instantiate this class as that is
 * controlled by displayMyLessons.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-20 Initial version
 */

public class ViewMyLessons {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	/** The database the lessons are read from. */
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	// These are the widget attributes for the GUI.  The page is one card holding the logo, the
	// title, the list of lessons, a line for messages, and the three buttons.
	private static Label label_PageTitle = new Label("My lessons");
	private static Label label_Purpose = new Label(
			"The lessons you have added.  Choose one and press Open to see or change it.");

	/** The list of this Contributor's lessons, one row per lesson. */
	protected static ListView<String> list_Lessons = new ListView<>();

	/** The line that tells the Contributor why something could not be done. */
	protected static Label label_Message = new Label();

	private static Button button_Open = new Button("Open");
	private static Button button_AddLesson = new Button("Add a lesson");
	private static Button button_BackToHome = new Button("Back to home");

	/** The ID of the lesson in each row of the list, in the same order as the rows. */
	protected static List<Integer> lessonIds = new ArrayList<>();

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewMyLessons theView;		// Used to determine if instantiation of the class
												// is needed

	/** The Stage that JavaFX has established for us. */
	protected static Stage theStage;
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	/** The current logged in User. */
	protected static User theUser;

	private static Scene theMyLessonsScene;		// The shared Scene each invocation populates

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayMyLessons(Stage ps, User user) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the My Lessons page to be displayed, with no message showing. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 *
	 * @param user specifies the User for this GUI and it's methods
	 *
	 */
	public static void displayMyLessons(Stage ps, User user) {
		displayMyLessons(ps, user, "");
	}

	/**********
	 * <p> Method: displayMyLessons(Stage ps, User user, String message) </p>
	 *
	 * <p> Description: This method displays the My Lessons page with a message showing.  The
	 * LessonsLearned Details page uses it to return here and say why, for example when another role has
	 * deleted the lesson that was open (R6).
	 *
	 * It first sets up the shared attributes so we don't have to pass parameters.  It then checks
	 * to see if the page has been setup.  If not, it instantiates the class, which initializes all
	 * the static aspects of the GUI widgets.  After that, it reads this Contributor's lessons from
	 * the database, fills the list, sets the Scene onto the stage, and makes it visible.</p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 *
	 * @param user specifies the User for this GUI and it's methods
	 *
	 * @param message specifies the message to show, or the empty String for none
	 *
	 */
	public static void displayMyLessons(Stage ps, User user, String message) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewMyLessons();

		// Set the title for the window and display the page.  The list is filled after the page is
		// on the stage, so its new rows are styled as part of the window they appear in.
		theStage.setTitle("CSE 360 Foundation Code: My Lessons");
		theStage.setScene(theMyLessonsScene);
		theStage.show();

		// Populate the dynamic aspects of the GUI: this Contributor's lessons, read fresh (R2, R6)
		LessonsLearnedList lessons = ModelMyLessons.lessonsFor(theDatabase, theUser.getUserName());
		list_Lessons.getItems().clear();
		lessonIds.clear();
		for (LessonsLearned lessonsLearned : lessons.getLessons()) {
			list_Lessons.getItems().add(ModelMyLessons.rowText(lessonsLearned));
			lessonIds.add(lessonsLearned.getId());
		}
		label_Message.setText(message);
		GuiStyle.startUnfocused(theRootPane);	// No button looks pressed on arrival
	}

	/**********
	 * <p> Method: ViewMyLessons() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and event handlers for each GUI
	 * object.</p>
	 *
	 * This is a singleton and is only performed once.  Subsequent uses fill in the list using
	 * the displayMyLessons method.</p>
	 *
	 */
	private ViewMyLessons() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theMyLessonsScene = new Scene(theRootPane, width, height);
		GuiStyle.applyBackground(theRootPane);

		// The title and the line below it
		label_PageTitle.setLayoutX(70);
		label_PageTitle.setLayoutY(104);
		GuiStyle.stylePageTitle(label_PageTitle);
		label_Purpose.setLayoutX(70);
		label_Purpose.setLayoutY(142);
		GuiStyle.styleMutedLabel(label_Purpose);

		// The list of lessons.  With no lessons it shows the empty message in its place (R2).
		list_Lessons.setLayoutX(70);
		list_Lessons.setLayoutY(172);
		list_Lessons.setPrefSize(660, 270);
		Label empty = new Label(ModelMyLessons.EMPTY_MESSAGE);
		GuiStyle.styleMutedLabel(empty);
		list_Lessons.setPlaceholder(empty);
		GuiStyle.styleList(list_Lessons);

		// The line for messages, such as "choose a lesson first"
		label_Message.setLayoutX(70);
		label_Message.setLayoutY(452);
		label_Message.setPrefWidth(660);
		GuiStyle.styleErrorLabel(label_Message);

		// Open is the page's main action; the other two are secondary
		setupButtonUI(button_Open, 140, 70);
		GuiStyle.stylePrimaryButton(button_Open);
		button_Open.setOnAction((_) -> ControllerMyLessons.performOpen());

		setupButtonUI(button_AddLesson, 160, 225);
		GuiStyle.styleSecondaryButton(button_AddLesson);
		button_AddLesson.setOnAction((_) -> ControllerMyLessons.performAddLesson());

		setupButtonUI(button_BackToHome, 160, 570);
		GuiStyle.styleSecondaryButton(button_BackToHome);
		button_BackToHome.setOnAction((_) -> ControllerMyLessons.performBackToHome());

		// Place all of the widget items into the Root Pane's list of children, the card first so it
		// is drawn behind the rest
		theRootPane.getChildren().addAll(
				GuiStyle.createCard(40, 40, 720, 520),
				GuiStyle.createLogo(70, 66),
				label_PageTitle, label_Purpose, list_Lessons, label_Message,
				button_Open, button_AddLesson, button_BackToHome);
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
