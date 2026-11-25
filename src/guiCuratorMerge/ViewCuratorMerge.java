package guiCuratorMerge;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import java.util.HashMap;
import java.util.Map;
import database.Database;
import entityClasses.LessonsLearned;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import entityClasses.LessonsLearnedList;
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
 * <p> The list of lessons is read fresh from the database each time the page is displayed, so
 * changes made elsewhere show up the next time the Curator arrives.  Merging the data and saving
 * the visibility choice arrive with the feature.</p>
 *
 * <p> The class has been written using a singleton design pattern and is the View portion of the
 * Model, View, Controller pattern.  All access starts by invoking the static method
 * displayCuratorMerge.</p>
 *
 * <p> Backend permission and persistence rules are covered by
 * testing.CuratorSummaryVisibilityTest. Checkbox interaction requires a manual GUI test.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 * @author Hassan Mohamed (visibility integration)
 * @author Liam Brotherton
 *
 * @version 1.00		2026-11-15 Initial version, a layout without the feature
 * @version 1.10        2026-11-19 Persist Curator visibility for stored summaries
 * @version 1.20		2026-11-22 The lesson list is now populated from the database
 */

public class ViewCuratorMerge {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	/** The database the lessons are read from. */
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	// These are the widget attributes for the GUI.  The page is one card holding the logo, the
	// title, the lessons to merge on the left, the summary and the visibility choice on the right,
	// a line for messages, and the buttons.
	private static Label label_PageTitle = new Label("Time and effort");
	private static Label label_Purpose = new Label(
			"Combine the time and effort from several lessons into one summary, and choose who can see it.");

	private static Label label_Lessons = new Label("Lessons to combine");

	/** The lessons that have time and effort data.  Two or more can be chosen. */
	protected static ListView<String> list_Lessons = new ListView<>();

	/** The ID of the lesson in each row of the list, in the same order as the rows. */
	protected static List<Integer> lessonIds = new ArrayList<>();

	private static Label label_Summary = new Label("Combined summary");
	private static Label label_TargetLesson = new Label("Saved summary for lesson");
	protected static ComboBox<String> combo_TargetLesson = new ComboBox<>();
	protected static Map<String, Integer> targetLessonIds = new HashMap<>();
	protected static TextArea area_Summary = new TextArea();

	/** Whether Contributors can see the combined summary.  Off, so the summary stays with the
	 *  Curator until the Curator chooses otherwise. */
	protected static CheckBox check_ShowToContributors = new CheckBox("Show this summary to Contributors");

	/** Feedback on merging or saving the visibility setting. */
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
	 * stage, makes it visible, and fills the list of lessons from the database.</p>
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
        // Requery when the page opens, so the summaries Liam creates appear immediately.
        list_Lessons.getItems().clear();
        combo_TargetLesson.getItems().clear();
        targetLessonIds.clear();
        for (LessonsLearned lesson : theDatabase.getAllLessons().getLessons()) {
            String row = "#" + lesson.getId() + " - " + lesson.getTitle();
            if (lesson.hasExperienceInformation()) list_Lessons.getItems().add(row);
            if (!theDatabase.getCuratorSummaryForViewer(lesson.getId(), user).isEmpty()) {
                combo_TargetLesson.getItems().add(row);
                targetLessonIds.put(row, lesson.getId());
            }
        }
        combo_TargetLesson.setDisable(combo_TargetLesson.getItems().isEmpty());
        if (!combo_TargetLesson.getItems().isEmpty()) combo_TargetLesson.getSelectionModel().selectFirst();
        refreshSelectedSummary();

		// Set the title for the window and display the page.  The list is filled after the page is
		// on the stage, so its new rows are styled as part of the window they appear in.
		theStage.setTitle("CSE 360 Foundation Code: Time and Effort");
		theStage.setScene(theCuratorMergeScene);
		theStage.show();

		// Populate the dynamic aspects of the GUI: the lessons, read fresh each time.  Both lists
		// are cleared together so row i always matches lessonIds.get(i).
		LessonsLearnedList lessons = theDatabase.getAllLessons();
		list_Lessons.getItems().clear();
		lessonIds.clear();
		for (LessonsLearned lessonsLearned : lessons.getLessons()) {
			if (lessonsLearned.getDuration() == null || lessonsLearned.getEffort() == null) continue;
			list_Lessons.getItems().add(lessonsLearned.getTitle());
			lessonIds.add(lessonsLearned.getId());
		}

		GuiStyle.startUnfocused(theRootPane);	// No button looks pressed on arrival
	}

	/**********
	 * <p> Method: ViewCuratorMerge() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and event handlers for each GUI
	 * object.</p>
	 *
	 * This is a singleton and is only performed once.  Subsequent uses fill in the list using
	 * the displayCuratorMerge method.</p>
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
		Label empty = new Label("No lessons have time and effort data yet.");
		empty.setWrapText(true);
		empty.setMaxWidth(280);
		GuiStyle.styleMutedLabel(empty);
		list_Lessons.setPlaceholder(empty);
		GuiStyle.styleList(list_Lessons);
		
		// A click toggles a row, so several lessons can be chosen without holding Ctrl or Shift.
		list_Lessons.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
			Node node = e.getPickResult().getIntersectedNode();
			while (node != null && !(node instanceof ListCell)) node = node.getParent();
			if (node instanceof ListCell<?> cell && !cell.isEmpty()) {
				MultipleSelectionModel<String> sm = list_Lessons.getSelectionModel();
				int i = cell.getIndex();
				if (sm.getSelectedIndices().contains(i)) sm.clearSelection(i);
				else sm.select(i);
				e.consume();	// stops the default "replace the selection" behavior
			}
		});

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

		// Choose which saved summary's visibility to control, independently of the list
        // that Liam's merging feature will use for selecting source lessons.
        label_TargetLesson.setLayoutX(420);
        label_TargetLesson.setLayoutY(375);
        GuiStyle.styleFieldLabel(label_TargetLesson);
        combo_TargetLesson.setLayoutX(420);
        combo_TargetLesson.setLayoutY(394);
        combo_TargetLesson.setPrefWidth(310);
        combo_TargetLesson.setOnAction((_) -> refreshSelectedSummary());

		// The visibility choice, under the saved-summary selector
		check_ShowToContributors.setLayoutX(420);
		check_ShowToContributors.setLayoutY(427);
		check_ShowToContributors.setFont(GuiStyle.fontBody());
		check_ShowToContributors.setStyle("-fx-text-fill: " + GuiStyle.COLOR_TEXT + ";");
		check_ShowToContributors.setOnAction((_) -> ControllerCuratorMerge.performVisibilityChange());

		// The line for messages.  It is muted, not red, because nothing has gone wrong.
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
				label_Summary, area_Summary, label_TargetLesson, combo_TargetLesson,
                check_ShowToContributors,
				label_Message, button_Merge, button_BackToHome);
	}

    /** Refreshes the summary text and checkbox for the selected destination lesson. */
    protected static void refreshSelectedSummary() {
        Integer lessonId = targetLessonIds.get(combo_TargetLesson.getValue());
        boolean valid = lessonId != null && theUser != null && theUser.getCuratorRole();
        String summary = valid ? theDatabase.getCuratorSummaryForViewer(lessonId, theUser) : "";
        area_Summary.setText(summary);
        check_ShowToContributors.setDisable(summary.isEmpty());
        check_ShowToContributors.setSelected(valid && !summary.isEmpty()
                && theDatabase.isCuratorSummaryVisible(lessonId));
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