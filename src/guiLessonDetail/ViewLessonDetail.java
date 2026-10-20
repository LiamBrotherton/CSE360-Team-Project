package guiLessonDetail;

import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.User;
import guiTools.GuiStyle;

/*******
 * <p> Title: ViewLessonDetail Class. </p>
 *
 * <p> Description: The Java/FX-based LessonsLearned Details Page.  It shows a Contributor one of the
 * lessons that Contributor added: its title, its category, and its body, which is the lesson's
 * core information and nothing else.  Information other roles may add to a lesson is not shown.
 *
 * Each of the three has a row.  When the field may be changed the row has an Edit button, which
 * opens a dialog holding the current value.  When a Curator or Admin has locked the field the row
 * says "Locked" instead and has no Edit button.  Delete lesson asks "Delete this lesson?" before
 * doing anything; a lesson with any locked field cannot be deleted, and the page says why.  Back
 * to my lessons returns to the list.
 *
 * Every rule is in ModelLessonDetail.  The lesson is read fresh each time this page is shown and
 * again before every Edit and Delete, so a change made by another role is always what the
 * Contributor works with.  If another role has deleted the lesson, the page returns to My Lessons
 * and says the lesson is no longer available.
 *
 * Requirements satisfied, from the Contributor user story: R3, see a lesson's core information
 * and which fields are locked; R4, change a field unless it is locked; R5, delete the lesson;
 * R6, changes, locks, and deletions by another role are reflected; R7, only this Contributor's
 * lessons can be opened.
 *
 * The class has been written using a singleton design pattern and is the View portion of the
 * Model, View, Controller pattern.  All access starts by invoking the static method
 * displayLessonDetail.  No other method should attempt to instantiate this class as that is
 * controlled by displayLessonDetail.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-20 Initial version
 */

public class ViewLessonDetail {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	/** The database the lesson is read from and changed in. */
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	// The layout of the rows: where the values start, how wide they are, and where Edit goes
	private static final double rowX = 70;
	private static final double valueWidth = 550;
	private static final double editX = 640;
	private static final double editWidth = 90;

	// These are the widget attributes for the GUI.  The page is one card holding the logo, the
	// title, a row for each core field, a line for messages, and the two buttons.
	private static Label label_PageTitle = new Label("LessonsLearned details");
	private static Label label_Purpose = new Label(
			"A Curator or Admin may lock a field so that it cannot be changed.");

	private static Label label_Title = new Label("Title");
	private static Label label_TitleValue = new Label();
	private static Button button_EditTitle = new Button("Edit");
	private static Label label_TitleLocked = new Label("Locked");

	private static Label label_Category = new Label("Category");
	private static Label label_CategoryValue = new Label();
	private static Button button_EditCategory = new Button("Edit");
	private static Label label_CategoryLocked = new Label("Locked");

	private static Label label_Body = new Label("Body");
	/** The body of the lesson.  It wraps inside the scrolling area below. */
	protected static Label label_BodyValue = new Label();
	/** The fixed-size area that holds the body, so a long body scrolls instead of growing. */
	protected static ScrollPane scroll_Body = new ScrollPane(label_BodyValue);
	private static Button button_EditBody = new Button("Edit");
	private static Label label_BodyLocked = new Label("Locked");

	/** The line that tells the Contributor why something could not be done. */
	protected static Label label_Message = new Label();

	private static Button button_DeleteLesson = new Button("Delete lesson");
	private static Button button_BackToMyLessons = new Button("Back to my lessons");

	/** The question asked before a lesson is deleted (R5). */
	protected static Alert alertConfirmDelete = new Alert(AlertType.CONFIRMATION);

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewLessonDetail theView;	// Used to determine if instantiation of the class
												// is needed

	/** The Stage that JavaFX has established for us. */
	protected static Stage theStage;
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	/** The current logged in User. */
	protected static User theUser;
	/** The ID of the lesson this page is showing. */
	protected static int theLessonId;

	private static Scene theLessonDetailScene;	// The shared Scene each invocation populates

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayLessonDetail(Stage ps, User user, int lessonId) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the LessonsLearned Details page to be displayed for one lesson, with no message showing. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 *
	 * @param user specifies the User for this GUI and it's methods
	 *
	 * @param lessonId specifies the lesson to show
	 *
	 */
	public static void displayLessonDetail(Stage ps, User user, int lessonId) {
		displayLessonDetail(ps, user, lessonId, "");
	}

	/**********
	 * <p> Method: displayLessonDetail(Stage ps, User user, int lessonId, String message) </p>
	 *
	 * <p> Description: This method displays the LessonsLearned Details page with a message showing.  The
	 * controller uses it to show the lesson again after an Edit, with the reason if the change
	 * was refused.
	 *
	 * It first sets up the shared attributes so we don't have to pass parameters.  It then checks
	 * to see if the page has been setup.  If not, it instantiates the class, which initializes all
	 * the static aspects of the GUI widgets.  After that, it reads the lesson from the database.
	 * If the lesson no longer exists, or is not this Contributor's, the My Lessons page is shown
	 * instead with the message saying so (R6, R7).  Otherwise the rows are filled in, each with an
	 * Edit button or the word "Locked" (R3, R4), and the page is shown.</p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 *
	 * @param user specifies the User for this GUI and it's methods
	 *
	 * @param lessonId specifies the lesson to show
	 *
	 * @param message specifies the message to show, or the empty String for none
	 *
	 */
	public static void displayLessonDetail(Stage ps, User user, int lessonId, String message) {

		// Establish the references to the GUI, the current user, and the lesson
		theStage = ps;
		theUser = user;
		theLessonId = lessonId;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewLessonDetail();

		// R6 and R7: the lesson must still exist and must be this Contributor's
		LessonsLearned lessonsLearned = ModelLessonDetail.lessonFor(theDatabase, theUser.getUserName(), lessonId);
		if (lessonsLearned == null) {
			guiMyLessons.ViewMyLessons.displayMyLessons(theStage, theUser,
					ModelLessonDetail.NOT_AVAILABLE);
			return;
		}

		// R3: the core information, and which fields another role has locked (R4)
		GuiStyle.setValueText(label_TitleValue, lessonsLearned.getTitle());
		GuiStyle.setValueText(label_CategoryValue, lessonsLearned.getCategory());
		label_BodyValue.setText(lessonsLearned.getBody());
		scroll_Body.setVvalue(0);
		showLock(button_EditTitle, label_TitleLocked, lessonsLearned.getTitleLocked());
		showLock(button_EditCategory, label_CategoryLocked, lessonsLearned.getCategoryLocked());
		showLock(button_EditBody, label_BodyLocked, lessonsLearned.getBodyLocked());
		label_Message.setText(message);

		// Set the title for the window, display the page, and wait for the user to act
		theStage.setTitle("CSE 360 Foundation Code: LessonsLearned Details");
		theStage.setScene(theLessonDetailScene);
		theStage.show();
		GuiStyle.startUnfocused(theRootPane);	// No button looks pressed on arrival
	}

	/**********
	 * <p> Method: ViewLessonDetail() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and event handlers for each GUI
	 * object.</p>
	 *
	 * This is a singleton and is only performed once.  Subsequent uses fill in the lesson using
	 * the displayLessonDetail method.</p>
	 *
	 */
	private ViewLessonDetail() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theLessonDetailScene = new Scene(theRootPane, width, height);
		GuiStyle.applyBackground(theRootPane);

		// The question asked before a delete, with the fixed wording (R5)
		alertConfirmDelete.setTitle("Delete LessonsLearned");
		alertConfirmDelete.setHeaderText("Delete this lesson?");
		alertConfirmDelete.setContentText("This cannot be undone.");

		// The title and the line below it
		label_PageTitle.setLayoutX(rowX);
		label_PageTitle.setLayoutY(100);
		GuiStyle.stylePageTitle(label_PageTitle);
		label_Purpose.setLayoutX(rowX);
		label_Purpose.setLayoutY(138);
		GuiStyle.styleMutedLabel(label_Purpose);

		// Title and Category: a name, the value below it, and Edit or "Locked" to the right
		setupRowUI(label_Title, label_TitleValue, button_EditTitle, label_TitleLocked, 170);
		button_EditTitle.setOnAction((_) -> ControllerLessonDetail.performEdit(LessonsLearned.FIELD_TITLE));

		setupRowUI(label_Category, label_CategoryValue, button_EditCategory, label_CategoryLocked, 230);
		button_EditCategory.setOnAction((_) ->
				ControllerLessonDetail.performEdit(LessonsLearned.FIELD_CATEGORY));

		// Body: the value can be long, so it wraps inside a fixed area that scrolls (Review Focus 4)
		setupRowUI(label_Body, null, button_EditBody, label_BodyLocked, 290);
		button_EditBody.setOnAction((_) -> ControllerLessonDetail.performEdit(LessonsLearned.FIELD_BODY));
		GuiStyle.styleCardLabel(label_BodyValue);
		label_BodyValue.setWrapText(true);
		label_BodyValue.setMaxWidth(valueWidth - 30);
		scroll_Body.setLayoutX(rowX);
		scroll_Body.setLayoutY(310);
		scroll_Body.setPrefSize(valueWidth, 136);
		scroll_Body.setMaxHeight(136);
		scroll_Body.setFitToWidth(true);
		scroll_Body.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scroll_Body.setStyle("-fx-background: white; -fx-background-color: white; "
				+ "-fx-border-color: #e6e8ec; -fx-border-radius: 8; -fx-background-radius: 8; "
				+ "-fx-padding: 8;");

		// The line for messages, such as why a lesson cannot be deleted
		label_Message.setLayoutX(rowX);
		label_Message.setLayoutY(452);
		label_Message.setPrefWidth(660);
		GuiStyle.styleErrorLabel(label_Message);

		// The two buttons along the bottom of the card
		setupButtonUI(button_DeleteLesson, 160, rowX);
		GuiStyle.styleSecondaryButton(button_DeleteLesson);
		button_DeleteLesson.setOnAction((_) -> ControllerLessonDetail.performDelete());

		setupButtonUI(button_BackToMyLessons, 190, 540);
		GuiStyle.styleSecondaryButton(button_BackToMyLessons);
		button_BackToMyLessons.setOnAction((_) -> ControllerLessonDetail.performBackToMyLessons());

		// Place all of the widget items into the Root Pane's list of children, the card first so it
		// is drawn behind the rest
		theRootPane.getChildren().addAll(
				GuiStyle.createCard(40, 40, 720, 520),
				GuiStyle.createLogo(rowX, 66),
				label_PageTitle, label_Purpose,
				label_Title, label_TitleValue, button_EditTitle, label_TitleLocked,
				label_Category, label_CategoryValue, button_EditCategory, label_CategoryLocked,
				label_Body, scroll_Body, button_EditBody, label_BodyLocked,
				label_Message, button_DeleteLesson, button_BackToMyLessons);
	}

	/*-********************************************************************************************

	Dialogs used by the controller

	 */

	/**********
	 * <p> Method: Optional&lt;String&gt; askForValue(String field, String current, String problem)
	 * </p>
	 *
	 * <p> Description: Opens the dialog for changing one field, with a value already in it (R4):
	 * the field's current value the first time, or what the Contributor typed when that was
	 * refused.  A refusal's reason is shown at the top of the dialog in place of the usual
	 * heading.  Title and Category use a one-line dialog.  Body uses a dialog with a text area,
	 * since a body is usually several lines long. </p>
	 *
	 * @param field		LessonsLearned.FIELD_TITLE, LessonsLearned.FIELD_CATEGORY, or LessonsLearned.FIELD_BODY
	 * @param current	the value to start from
	 * @param problem	why the last value was refused, or the empty String
	 *
	 * @return the value entered, or empty if the dialog was cancelled
	 */
	protected static Optional<String> askForValue(String field, String current, String problem) {
		String name = Character.toUpperCase(field.charAt(0)) + field.substring(1);
		String heading = problem.isEmpty() ? "Change the " + field + " of this lesson" : problem;

		if (!LessonsLearned.FIELD_BODY.equals(field)) {
			TextInputDialog dialog = new TextInputDialog(current);
			dialog.setTitle("Edit " + name);
			dialog.setHeaderText(heading);
			return dialog.showAndWait();
		}

		TextArea area = new TextArea(current);
		area.setPrefSize(460, 220);
		GuiStyle.styleTextArea(area);
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Edit " + name);
		dialog.setHeaderText(heading);
		dialog.getDialogPane().setContent(area);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialog.setResultConverter(button -> button == ButtonType.OK ? area.getText() : null);
		return dialog.showAndWait();
	}

	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	/**********
	 * Private local method to show either a row's Edit button or its "Locked" word, never both.
	 *
	 * @param edit		The row's Edit Button
	 * @param locked	The row's "Locked" Label
	 * @param isLocked	True when another role has locked the field
	 */
	private static void showLock(Button edit, Label locked, boolean isLocked) {
		edit.setVisible(!isLocked);
		locked.setVisible(isLocked);
	}

	/**********
	 * Private local method to place one row: the field's name in small gray text, its value below
	 * (held to a fixed width, so a long value ends in an ellipsis and never reaches the Edit
	 * button), and the Edit button and the "Locked" word in the same place to the right.
	 *
	 * @param name		The Label holding the name of the field
	 * @param value		The Label holding its current value, or null when the value is placed
	 * 					separately
	 * @param edit		The row's Edit Button
	 * @param locked	The row's "Locked" Label
	 * @param y			The location of the row from the top (y axis)
	 */
	private static void setupRowUI(Label name, Label value, Button edit, Label locked, double y) {
		name.setLayoutX(rowX);
		name.setLayoutY(y);
		GuiStyle.styleFieldLabel(name);

		if (value != null) {
			value.setLayoutX(rowX);
			value.setLayoutY(y + 20);
			value.setMinWidth(valueWidth);
			value.setMaxWidth(valueWidth);
			GuiStyle.styleCardLabel(value);
		}

		edit.setMinWidth(editWidth);
		edit.setLayoutX(editX);
		edit.setLayoutY(y + 2);
		GuiStyle.styleSecondaryButton(edit);

		locked.setMinWidth(editWidth);
		locked.setAlignment(Pos.CENTER);
		locked.setLayoutX(editX);
		locked.setLayoutY(y + 12);
		GuiStyle.styleMutedLabel(locked);
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
