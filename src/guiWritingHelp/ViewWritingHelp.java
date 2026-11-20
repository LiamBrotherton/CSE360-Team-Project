package guiWritingHelp;

import java.util.ArrayList;
import java.util.List;
import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.User;
import guiTools.GuiStyle;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import aiWritingHelper.ModelAiWritingHelper;

/*******
 * <p> Title: ViewWritingHelp Class. </p>
 *
 * <p> Description: The Java/FX-based Writing Help page for a Contributor.  It is opened from the
 * Lesson Details page and works on that one lesson.  The Contributor chooses a field, types what
 * they want, and then gets a suggestion, brainstorms several ideas, or searches their own lessons
 * for examples.  A suggestion appears in a box the Contributor can edit, and nothing is saved until
 * they press Use this text.  It serves the Contributor Phase 3 epic on AI-based writing,
 * brainstorming, and searching tools.</p>
 *
 * <p> The page holds no rules.  Every check, and every call to the assistant, is made by
 * ModelAiWritingHelper, so the page only shows what the Model returns.</p>
 *
 * <p> The class has been written using a singleton design pattern and is the View portion of the
 * Model, View, Controller pattern.  All access starts by invoking the static method
 * displayWritingHelp.  The Model is ModelAiWritingHelper, in the aiWritingHelper package.</p>
 *
 * <p> Tested by: the rules behind every button are tested by AiWritingHelperTestingAutomation
 * (R1 to R9).  The page itself is covered by the manual tests of the Writing Help page.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-19 Initial version
 */

public class ViewWritingHelp {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	/** The database the lesson is read from and saved to. */
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	// These are the widget attributes for the GUI.  The page is one card holding the logo, the
	// title, the lesson's name, the field and request, three buttons, the suggestion and the list of
	// options, a line for messages, and the buttons along the bottom.
	private static Label label_PageTitle = new Label("Writing help");
	private static Label label_LessonName = new Label();
	private static Label label_Field = new Label("Field");
	private static Label label_Request = new Label("Your request");
	private static Label label_Suggestion = new Label("Suggestion (you can edit it)");

	/** The label above the options list, which says whether it holds ideas or examples. */
	protected static Label label_Options = new Label("Options");

	/** The field the help is for: Title, Body, or Category. */
	protected static ComboBox<String> combo_Field = new ComboBox<>();

	/** What the Contributor wants, for example "make this clearer", or the words to search for. */
	protected static TextField text_Request = new TextField();

	/** The suggested text.  The Contributor can change it before using it. */
	protected static TextArea area_Suggestion = new TextArea();

	/** The ideas from brainstorming, or the lessons that matched a search. */
	protected static ListView<String> list_Options = new ListView<>();

	/** The line that tells the Contributor what happened or why something could not be done. */
	protected static Label label_Message = new Label();

	private static Button button_Suggest = new Button("Get suggestion");
	private static Button button_Brainstorm = new Button("Brainstorm ideas");
	private static Button button_Examples = new Button("Find examples");
	private static Button button_Accept = new Button("Use this text");
	private static Button button_Back = new Button("Back to lesson");

	/** The full text of each idea in the options list, in the same order as the rows.  A row is cut
	 *  short to fit the list, so the text copied into the suggestion box comes from here. */
	protected static List<String> ideaTexts = new ArrayList<>();

	/** The lesson in each row of the options list when it holds examples, in the same order. */
	protected static List<LessonsLearned> exampleLessons = new ArrayList<>();

	/** True when the options list holds examples, false when it holds ideas. */
	protected static boolean showingExamples = false;

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewWritingHelp theView;		// Used to determine if instantiation of the class
												// is needed

	/** The Stage that JavaFX has established for us. */
	protected static Stage theStage;
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	/** The current logged in User. */
	protected static User theUser;
	/** The lesson the help is for. */
	protected static int theLessonId;

	private static Scene theWritingHelpScene;	// The shared Scene each invocation populates

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayWritingHelp(Stage ps, User user, int lessonId) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Writing Help page to be displayed for one lesson.
	 *
	 * It first sets up the shared attributes so we don't have to pass parameters.  It then checks
	 * to see if the page has been setup.  If not, it instantiates the class, which initializes all
	 * the static aspects of the GUI widgets.  It then reads the lesson.  If the lesson no longer
	 * exists, or is not this Contributor's, the My Lessons page is shown instead with the message
	 * saying so, the same as the Lesson Details page does.  Otherwise the page is cleared of any
	 * earlier suggestion, and shown.</p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 *
	 * @param user specifies the User for this GUI and it's methods
	 *
	 * @param lessonId specifies the lesson the help is for
	 *
	 */
	public static void displayWritingHelp(Stage ps, User user, int lessonId) {

		// Establish the references to the GUI, the current user, and the lesson
		theStage = ps;
		theUser = user;
		theLessonId = lessonId;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewWritingHelp();

		// The lesson must still exist and must be this Contributor's.  The Model's own call is used
		// so the page and the rules agree on what "this Contributor's" means.
		LessonsLearned lesson = theDatabase.getLessonForContributor(lessonId, theUser.getUserName());
		if (lesson == null) {
			guiMyLessons.ViewMyLessons.displayMyLessons(theStage, theUser,
					ModelAiWritingHelper.NOT_AVAILABLE);
			return;
		}

		// Start clean, so nothing from the last lesson's session is still showing
		GuiStyle.setValueText(label_LessonName, "Lesson: " + lesson.getTitle());
		combo_Field.getSelectionModel().select("Body");
		text_Request.setText("");
		area_Suggestion.setText("");
		showOptions(false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		showMessage("", false);

		// Set the title for the window, display the page, and wait for the Contributor to act
		theStage.setTitle("CSE 360 Foundation Code: Writing Help");
		theStage.setScene(theWritingHelpScene);
		theStage.show();
		GuiStyle.startUnfocused(theRootPane);	// No button looks pressed on arrival
	}

	/**********
	 * <p> Method: ViewWritingHelp() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and event handlers for each GUI
	 * object.</p>
	 *
	 * This is a singleton and is only performed once.  Subsequent uses fill in the lesson using
	 * the displayWritingHelp method.</p>
	 *
	 */
	private ViewWritingHelp() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theWritingHelpScene = new Scene(theRootPane, width, height);
		GuiStyle.applyBackground(theRootPane);

		// The title and the name of the lesson below it
		label_PageTitle.setLayoutX(70);
		label_PageTitle.setLayoutY(100);
		GuiStyle.stylePageTitle(label_PageTitle);
		label_LessonName.setLayoutX(70);
		label_LessonName.setLayoutY(138);
		label_LessonName.setPrefWidth(660);
		GuiStyle.styleMutedLabel(label_LessonName);

		// The field and the request, side by side
		label_Field.setLayoutX(70);
		label_Field.setLayoutY(170);
		GuiStyle.styleFieldLabel(label_Field);
		combo_Field.setItems(FXCollections.observableArrayList("Title", "Body", "Category"));
		combo_Field.setLayoutX(70);
		combo_Field.setLayoutY(190);
		combo_Field.setPrefWidth(200);
		GuiStyle.styleComboBox(combo_Field);

		label_Request.setLayoutX(290);
		label_Request.setLayoutY(170);
		GuiStyle.styleFieldLabel(label_Request);
		text_Request.setPromptText("For example: make this clearer");
		text_Request.setLayoutX(290);
		text_Request.setLayoutY(190);
		text_Request.setPrefWidth(440);
		GuiStyle.styleField(text_Request);

		// Get suggestion is the page's main action; the other two are secondary
		setupButtonUI(button_Suggest, 160, 70, 252);
		GuiStyle.stylePrimaryButton(button_Suggest);
		button_Suggest.setOnAction((_) -> ControllerWritingHelp.performSuggest());

		setupButtonUI(button_Brainstorm, 170, 240, 252);
		GuiStyle.styleSecondaryButton(button_Brainstorm);
		button_Brainstorm.setOnAction((_) -> ControllerWritingHelp.performBrainstorm());

		setupButtonUI(button_Examples, 150, 420, 252);
		GuiStyle.styleSecondaryButton(button_Examples);
		button_Examples.setOnAction((_) -> ControllerWritingHelp.performExamples());

		// The suggestion on the left, in a box the Contributor can edit
		label_Suggestion.setLayoutX(70);
		label_Suggestion.setLayoutY(312);
		GuiStyle.styleFieldLabel(label_Suggestion);
		area_Suggestion.setPromptText("A suggestion will appear here.");
		area_Suggestion.setLayoutX(70);
		area_Suggestion.setLayoutY(334);
		area_Suggestion.setPrefSize(350, 98);
		GuiStyle.styleTextArea(area_Suggestion);

		// The ideas or examples on the right.  Choosing a row is an action, so it has a listener.
		label_Options.setLayoutX(440);
		label_Options.setLayoutY(312);
		GuiStyle.styleFieldLabel(label_Options);
		list_Options.setLayoutX(440);
		list_Options.setLayoutY(334);
		list_Options.setPrefSize(290, 98);
		Label empty = new Label("Ideas or examples will appear here.");
		GuiStyle.styleMutedLabel(empty);
		list_Options.setPlaceholder(empty);
		GuiStyle.styleList(list_Options);
		list_Options.getSelectionModel().selectedIndexProperty().addListener(
				(_, _, _) -> ControllerWritingHelp.performOptionChosen());

		// The line for what happened.  It wraps, so a long reason stays on the card.
		label_Message.setLayoutX(70);
		label_Message.setLayoutY(440);
		label_Message.setPrefWidth(660);
		label_Message.setWrapText(true);
		label_Message.setFont(GuiStyle.fontBody());

		// The buttons along the bottom of the card
		setupButtonUI(button_Accept, 150, 70, 490);
		GuiStyle.stylePrimaryButton(button_Accept);
		button_Accept.setOnAction((_) -> ControllerWritingHelp.performAccept());

		setupButtonUI(button_Back, 190, 540, 490);
		GuiStyle.styleSecondaryButton(button_Back);
		button_Back.setOnAction((_) -> ControllerWritingHelp.performBack());

		// Place all of the widget items into the Root Pane's list of children, the card first so it
		// is drawn behind the rest
		theRootPane.getChildren().addAll(
				GuiStyle.createCard(40, 40, 720, 520),
				GuiStyle.createLogo(70, 66),
				label_PageTitle, label_LessonName,
				label_Field, combo_Field, label_Request, text_Request,
				button_Suggest, button_Brainstorm, button_Examples,
				label_Suggestion, area_Suggestion, label_Options, list_Options,
				label_Message, button_Accept, button_Back);
	}

	/*-********************************************************************************************

	Methods used by the controller

	 */

	/**********
	 * <p> Method: showMessage(String text, boolean isProblem) </p>
	 *
	 * <p> Description: Shows a line under the lists.  A problem is shown in red and anything else in
	 * gray, so the Contributor can tell "this was refused" from "this worked" at a glance.  The
	 * color is set on the label itself rather than by style, because a color set in code is not
	 * replaced by the style the other helpers apply. </p>
	 *
	 * @param text			the line to show, or the empty String for none
	 * @param isProblem		true if the line says something could not be done
	 */
	protected static void showMessage(String text, boolean isProblem) {
		label_Message.setTextFill(Color.web(isProblem ? GuiStyle.COLOR_ERROR : GuiStyle.COLOR_MUTED));
		label_Message.setText(text);
	}

	/**********
	 * <p> Method: showOptions(boolean examples, List&lt;String&gt; rows, List&lt;String&gt; ideas,
	 * List&lt;LessonsLearned&gt; lessons) </p>
	 *
	 * <p> Description: Fills the options list with ideas or with examples, and keeps the full text
	 * of each idea, or each matching lesson, so choosing a row can use it.  Passing empty lists
	 * clears the list. </p>
	 *
	 * @param examples	true if the rows are examples, false if they are ideas
	 * @param rows		the text of each row
	 * @param ideas		the full text of each idea, in row order, when the rows are ideas
	 * @param lessons	the lesson in each row, in row order, when the rows are examples
	 */
	protected static void showOptions(boolean examples, List<String> rows, List<String> ideas,
			List<LessonsLearned> lessons) {
		showingExamples = examples;
		ideaTexts = ideas;
		exampleLessons = lessons;
		label_Options.setText(examples ? "Examples from your lessons" : "Ideas");
		list_Options.getItems().setAll(rows);
	}

	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	/**********
	 * Private local method to place a button.
	 *
	 * @param b		The Button object to be placed
	 * @param w		The width of the Button
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, double w, double x, double y) {
		b.setMinWidth(w);
		b.setAlignment(Pos.CENTER);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}
