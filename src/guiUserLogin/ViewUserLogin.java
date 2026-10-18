package guiUserLogin;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import guiTools.GuiStyle;


/*******
 * <p> Title: ViewUserLogin Class. </p>
 *
 * <p> Description: The Java/FX-based System Startup Page.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 1.00		2025-04-20 Initial version
 * @version 2.00		2026-10-16 Restyled to the team's user experience design: a single card on
 * 							a full-page gradient.  The user name, password and Log In button are
 * 							together at the top, as on GitHub's sign-in page, and the invitation
 * 							code that starts an account setup is below a divider in the same card.
 * 							The widgets, their actions, and the alert are unchanged.
 * @version 2.10		2026-10-17 Polish: a "Sign in" title and hint, the shared font and type
 * 							scale, controls of one height, and the quiet Quit button in the
 * 							bottom-right corner used on every page.
 *
 */

public class ViewUserLogin {

	/*-********************************************************************************************

	Attributes

	 *********************************************************************************************/

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// The geometry of the card.  Every widget on it is the same width and starts at the same left
	// margin, so the fields and buttons line up.
	private static final double cardX = 200, cardY = 40, cardW = 400, cardH = 516;
	private static final double innerX = cardX + 40, innerW = cardW - 80;

	// This set is for all subsequent starts of the system.  The page is one card: the existing
	// user logs in at the top, and a person with an invitation code sets up an account below.
	private static Label label_Title = new Label("Sign in");
	private static Label label_Hint = new Label("Use your username and password to continue.");
	private static Label label_Username = new Label("Username");
	private static Label label_Password = new Label("Password");
	protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);


	//	private User user;
	protected static TextField text_Username = new TextField();
	protected static PasswordField text_Password = new PasswordField();
	private static Button button_Login = new Button("Log In");

	private static Line line_Divider = new Line(innerX, 392, innerX + innerW, 392);
	private static Label label_AccountSetupInsrtuctions = new Label("Have an invitation code?");
	private static TextField text_Invitation = new TextField();
	private static Button button_SetupAccount = new Button("Set up my account");

	private static Button button_Quit = new Button("Quit");

	private static Stage theStage;
	private static Pane theRootPane;
	/** The Scene for the user login page. */
	public static Scene theUserLoginScene = null;


	private static ViewUserLogin theView = null;	//	private static guiUserLogin.ControllerUserLogin theController;


	/*-********************************************************************************************

	Constructor

	 *********************************************************************************************/

	/**
	 * Displays the user login page.
	 * @param ps the Stage on which to display the page
	 */
	public static void displayUserLogin(Stage ps) {

		// Establish the references to the GUI. There is no current user yet.
		theStage = ps;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewUserLogin();

		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		text_Username.setText("");		// Reset the username and password from the last use
		text_Password.setText("");
		text_Invitation.setText("");	// Same for the invitation code

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundation Code: User Login Page");
		theStage.setScene(theUserLoginScene);
		theStage.show();
	}

	/**********
	 * <p> Method: ViewUserLoginPage() </p>
	 *
	 * <p> Description: This method is called when the application first starts. It must handle
	 * two cases: 1) when no has been established and 2) when one or more users have been
	 * established.
	 *
	 * If there are no users in the database, this means that the person starting the system jmust
	 * be an administrator, so a special GUI is provided to allow this Admin to set a username and
	 * password.
	 *
	 * If there is at least one user, then a different display is shown for existing users to login
	 * and for potential new users to provide an invitation code and if it is valid, they are taken
	 * to a page where they can specify a username and password.</p>
	 *
	 */
	private ViewUserLogin() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theUserLoginScene = new Scene(theRootPane, width, height);

		// The page is a single card on a full-page gradient, centered across the window
		GuiStyle.applyBackground(theRootPane);

		// The title of the card and a one-line hint below it
		place(label_Title, innerX, 104);
		GuiStyle.stylePageTitle(label_Title);
		place(label_Hint, innerX, 142);
		GuiStyle.styleMutedLabel(label_Hint);

		// Existing user log in portion of the page

		place(label_Username, innerX, 176);
		GuiStyle.styleFieldLabel(label_Username);

		// Establish the text input operand field for the username
		setupTextUI(text_Username, innerX, 196);
		text_Username.setPromptText("Enter Username");
		GuiStyle.styleField(text_Username);

		place(label_Password, innerX, 248);
		GuiStyle.styleFieldLabel(label_Password);

		// Establish the text input operand field for the password
		setupTextUI(text_Password, innerX, 268);
		text_Password.setPromptText("Enter Password");
		GuiStyle.styleField(text_Password);

		// Set up the Log In button
		setupButtonUI(button_Login, innerX, 328);
		GuiStyle.stylePrimaryButton(button_Login);
		button_Login.setOnAction((_) -> {ControllerUserLogin.doLogin(theStage); });

		alertUsernamePasswordError.setTitle("Invalid username/password!");
		alertUsernamePasswordError.setHeaderText(null);


		// The invitation to setup an account portion of the page, below a divider

		line_Divider.setStroke(Color.web("#eaecf0"));

		place(label_AccountSetupInsrtuctions, innerX, 408);
		GuiStyle.styleFieldLabel(label_AccountSetupInsrtuctions);

		// Establish the text input operand field for the invitation code
		setupTextUI(text_Invitation, innerX, 428);
		text_Invitation.setPromptText("Enter Invitation Code");
		GuiStyle.styleField(text_Invitation);

		// Set up the setup button
		setupButtonUI(button_SetupAccount, innerX, 484);
		GuiStyle.styleSecondaryButton(button_SetupAccount);
		button_SetupAccount.setOnAction((_) -> {
			System.out.println("**** Calling doSetupAccount");
			ControllerUserLogin.doSetupAccount(theStage, text_Invitation.getText());
		});

		// Set up the Quit button, which GuiStyle places in the same corner on every page
		GuiStyle.styleQuitButton(button_Quit);
		button_Quit.setOnAction((_) -> {ControllerUserLogin.performQuit(); });

		// The card goes in first so it is drawn behind every other widget
		theRootPane.getChildren().addAll(
				GuiStyle.createCard(cardX, cardY, cardW, cardH),
				GuiStyle.createLogo(innerX, cardY + 30),
				label_Title, label_Hint,
				label_Username, text_Username, label_Password, text_Password, button_Login,
				line_Divider, label_AccountSetupInsrtuctions, text_Invitation, button_SetupAccount,
				button_Quit);
	}


	/*-********************************************************************************************

	Helper methods to reduce code length

	 *********************************************************************************************/

	/**********
	 * Private local method to place a label.  Its font and color come from GuiStyle.
	 *
	 * @param l		The Label object to be placed
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void place(Label l, double x, double y) {
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	/**********
	 * Private local method to place a button across the full width of the card.  Its font, height,
	 * and color come from GuiStyle.
	 *
	 * @param b		The Button object to be placed
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, double x, double y) {
		b.setMinWidth(innerW);
		b.setPrefWidth(innerW);
		b.setAlignment(Pos.CENTER);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	/**********
	 * Private local method to place a text field across the full width of the card.  Its font,
	 * height, and outline come from GuiStyle.
	 *
	 * @param t		The TextField object to be placed
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupTextUI(TextField t, double x, double y) {
		t.setMinWidth(innerW);
		t.setMaxWidth(innerW);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(true);
	}
}
