package guiCuratorMerge;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import entityClasses.User;
import guiTools.GuiStyle;

/*******
 * <p> Title: ViewCuratorMerge Class. </p>
 *
 * <p> Description: The Java/FX-based Time and Effort page for a Curator.  The Curator chooses two or
 * more lessons, merges their time and effort data into one statistical summary, and chooses whether
 * Contributors can see that summary.  It serves the Curator epics on merging time and effort data
 * and on making the merged information visible to the Contributor or not.</p>
 *
 * <p> This page is a layout only.  The list is empty, the summary area is empty, and the Merge
 * button and the visibility choice say what they will do once the feature is built.  Merging the
 * data and saving the visibility choice arrive with the feature.</p>
 *
 * <p> The class has been written using a singleton design pattern and is the View portion of the
 * Model, View, Controller pattern.  All access starts by invoking the static method
 * displayCuratorMerge.  There is no Model yet, because there is no data to hold until the feature
 * is built.</p>
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

public class ViewCuratorMerge {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// These are the widget attributes for the GUI.  The page is one card holding the logo, the
	// title, the lessons to merge on the left, the summary and the visibility choice on the right,
	// a line for messages, and the buttons.
	private static Label label_PageTitle = new Label("Time and effort");
	private static Label label_Purpose = new Label(
			"Combine the time and effort from several lessons into one summary, and choose who can see it.");

	private static Label label_Lessons = new Label("Lessons to combine");

	/** The lessons that have time and effort data.  Two or more can be chosen.  Empty until the
	 *  feature is built. */
	protected static ListView<String> list_Lessons = new ListView<>();

	private static Label label_Summary = new Label("Combined summary");
	private static TextArea area_Summary = new TextArea();

	/** Whether Contributors can see the combined summary.  Off, so the summary stays with the
	 *  Curator until the Curator chooses otherwise. */
	protected static CheckBox check_ShowToContributors = new CheckBox("Show this summary to Contributors");

	/** The line that tells the Curator what a control will do once it is built. */
	protected static Label label_Message = new Label();

	private static Button button_Merge = new Button("Merge chosen lessons");
	private static Button button_BackToHome = new Button("Back to home");

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewCuratorMerge theView;	// Used to determine if instantiation of the class
												// is needed

	/** The Stage that JavaFX has established for us. */
	protected static Stage theStage;
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	/** The current logged in User. */
	protected static User theUser;

	private static Scene theCuratorMergeScene;	// The shared Scene each invocation populates

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayCuratorMerge(Stage ps, User user) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Time and Effort page to be displayed.
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
	public static void displayCuratorMerge(Stage ps, User user) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewCuratorMerge();

		label_Message.setText("");

		// Set the title for the window, display the page, and wait for the Curator to act
		theStage.setTitle("CSE 360 Foundation Code: Time and Effort");
		theStage.setScene(theCuratorMergeScene);
		theStage.show();
		GuiStyle.startUnfocused(theRootPane);	// No button looks pressed on arrival
	}

	/**********
	 * <p> Method: ViewCuratorMerge() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and event handlers for each GUI
	 * object.</p>
	 *
	 * This is a singleton and is only performed once.</p>
	 *
	 */
	private ViewCuratorMerge() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theCuratorMergeScene = new Scene(theRootPane, width, height);
		GuiStyle.applyBackground(theRootPane);

		// The title and the line below it
		label_PageTitle.setLayoutX(70);
		label_PageTitle.setLayoutY(104);
		GuiStyle.stylePageTitle(label_PageTitle);
		label_Purpose.setLayoutX(70);
		label_Purpose.setLayoutY(142);
		GuiStyle.styleMutedLabel(label_Purpose);

		// The lessons to combine, on the left.  Two or more are needed to merge, so more than one
		// can be chosen.
		label_Lessons.setLayoutX(70);
		label_Lessons.setLayoutY(176);
		GuiStyle.styleFieldLabel(label_Lessons);
		list_Lessons.setLayoutX(70);
		list_Lessons.setLayoutY(198);
		list_Lessons.setPrefSize(320, 244);
		list_Lessons.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		Label empty = new Label("Lessons with time and effort data will be listed here.");
		empty.setWrapText(true);
		empty.setMaxWidth(280);
		GuiStyle.styleMutedLabel(empty);
		list_Lessons.setPlaceholder(empty);
		GuiStyle.styleList(list_Lessons);

		// The combined summary, on the right.  It cannot be edited: it only shows the result.
		label_Summary.setLayoutX(420);
		label_Summary.setLayoutY(176);
		GuiStyle.styleFieldLabel(label_Summary);
		area_Summary.setPromptText("The combined time and effort will appear here after you press Merge.");
		area_Summary.setEditable(false);
		area_Summary.setLayoutX(420);
		area_Summary.setLayoutY(198);
		area_Summary.setPrefSize(310, 170);
		GuiStyle.styleTextArea(area_Summary);

		// The visibility choice, under the summary it controls
		check_ShowToContributors.setLayoutX(420);
		check_ShowToContributors.setLayoutY(384);
		check_ShowToContributors.setFont(GuiStyle.fontBody());
		check_ShowToContributors.setStyle("-fx-text-fill: " + GuiStyle.COLOR_TEXT + ";");
		check_ShowToContributors.setOnAction((_) -> ControllerCuratorMerge.performVisibilityChange());

		// The line for what a control will do once it is built.  It is muted, not red, because
		// nothing has gone wrong.
		label_Message.setLayoutX(70);
		label_Message.setLayoutY(452);
		label_Message.setPrefWidth(660);
		label_Message.setWrapText(true);
		GuiStyle.styleMutedLabel(label_Message);

		// Merge is the page's main action; Back is secondary
		setupButtonUI(button_Merge, 210, 70);
		GuiStyle.stylePrimaryButton(button_Merge);
		button_Merge.setOnAction((_) -> ControllerCuratorMerge.performMerge());

		setupButtonUI(button_BackToHome, 160, 570);
		GuiStyle.styleSecondaryButton(button_BackToHome);
		button_BackToHome.setOnAction((_) -> ControllerCuratorMerge.performBackToHome());

		// Place all of the widget items into the Root Pane's list of children, the card first so it
		// is drawn behind the rest
		theRootPane.getChildren().addAll(
				GuiStyle.createCard(40, 40, 720, 520),
				GuiStyle.createLogo(70, 66),
				label_PageTitle, label_Purpose,
				label_Lessons, list_Lessons,
				label_Summary, area_Summary, check_ShowToContributors,
				label_Message, button_Merge, button_BackToHome);
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
