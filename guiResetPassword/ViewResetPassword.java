package guiResetPassword;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiTools.GuiStyle;

/*******
 * <p> Title: ViewResetPassword Class. </p>
 * 
 * <p> Description: The ViewResetPassword Page is used to enable a potential user with an invitation
 * code to establish an account after they have specified an invitation code on the standard login
 * page. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-19 Initial version
 * @version 2.00		2026-10-16 Restyled to the team's user experience design: one card on a
 * 							full-page gradient, matching the login page.  The page has no side
 * 							menu because a person who arrives with a one-time password has nowhere
 * 							else to go.  The fields, buttons, alerts, and actions are unchanged.
 * @version 2.10		2026-10-17 Polish: the title "Set a new password" and a hint, a name above
 * 							each field, the button renamed "Save new password", the shared font and
 * 							type scale, and the quiet Quit button in the same corner as on every page.
 *
 */

public class ViewResetPassword {
	
	/*-********************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	// This is a simple GUI login Page, very similar to the FirstAdmin login page.  The only real
	// difference is in this case we also know an email address, since it was used to send the
	// invitation to the potential user.
	private static Region card_ResetPassword;	// The white card the widgets sit on
	private static Pane logo_ResetPassword;		// The logo at the top of the card
    protected static Label label_ResetPassword = new Label("Set a new password");
    protected static Label label_ResetPasswordLine = new Label("Type the new password twice so it is not mistyped.");
    private static Label label_Password1 = new Label("New password");
    private static Label label_Password2 = new Label("Confirm new password");
    protected static PasswordField text_Password1 = new PasswordField();
    protected static PasswordField text_Password2 = new PasswordField();
    protected static Button button_ConfirmPassword = new Button("Save new password");

	// This alert is used should the user enter two passwords that do not match
	protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

	// This alert is used should the password not satisfy the strength requirements.  It is a
	// separate alert from the one above because that one's title and header are fixed at
	// construction and describe a mismatch, which is a different problem.
	protected static Alert alertPasswordRequirements = new Alert(AlertType.INFORMATION);

    protected static Button button_Quit = new Button("Quit");

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewResetPassword theView;		// Is instantiation of the class needed?

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current logged in User
	public static Scene theResetPasswordScene = null;	// Access to the User Update page's GUI Widgets
	

	/*-********************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayResetPassword(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the entry point from outside this package to cause
	 * the ResetPassword page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the user for this GUI and it's methods
	 * 
	 */
	public static void displayResetPassword(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;				// Save the reference to the Stage and user for the rest of this package
		theUser = user;
		
		if (theView == null) theView = new ViewResetPassword();
		
		text_Password1.setText("");	
		text_Password2.setText("");	
		
    	// Place all of the established GUI elements into the pane.  The pane is cleared and refilled
    	// every time the page is shown, so the card and the logo must be put back each time, and
    	// the card must come first so it is drawn behind everything else.
    	theRootPane.getChildren().clear();
    	theRootPane.getChildren().addAll(card_ResetPassword, logo_ResetPassword, label_ResetPassword,
    			label_ResetPasswordLine, label_Password1, text_Password1, label_Password2, text_Password2,
    			button_ConfirmPassword, button_Quit);

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundation Code: Reset Password");	
        theStage.setScene(theResetPasswordScene);
		theStage.show();
	}
	
	/**********
	 * <p> Constructor: ViewResetPassword() </p>
	 * 
	 * <p> Description: This constructor is called just once, the first time a new account needs to
	 * be created.  It establishes all of the common GUI widgets for the page so they are only
	 * created once and reused when needed.
	 * 
	 * The do
	 * 		
	 */
	private ViewResetPassword() {
		
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theResetPasswordScene = new Scene(theRootPane, width, height);

		// The page is a single card on a full-page gradient, like the login page.  The card is
		// centered across the window and every widget below is 320 wide inside its margins.
		GuiStyle.applyBackground(theRootPane);
		card_ResetPassword = GuiStyle.createCard(200, 80, 400, 384);
		logo_ResetPassword = GuiStyle.createLogo(240, 110);

    	// The title of the page and the instruction to the user
    	setupLabelUI(label_ResetPassword, "Arial", 26, 320, Pos.BASELINE_LEFT, 240, 144);
    	GuiStyle.stylePageTitle(label_ResetPassword);
    	setupLabelUI(label_ResetPasswordLine, "Arial", 13, 320, Pos.BASELINE_LEFT, 240, 182);
    	GuiStyle.styleMutedLabel(label_ResetPasswordLine);

		// Establish the text input operand field for the password, with its name above it
    	setupLabelUI(label_Password1, "Arial", 13, 320, Pos.BASELINE_LEFT, 240, 216);
    	GuiStyle.styleFieldLabel(label_Password1);
		setupTextUI(text_Password1, "Arial", 15, 320, Pos.BASELINE_LEFT, 240, 236, true);
		text_Password1.setPromptText("Enter the Password");
		GuiStyle.styleField(text_Password1);

		// Establish the text input operand field to confirm the password
    	setupLabelUI(label_Password2, "Arial", 13, 320, Pos.BASELINE_LEFT, 240, 288);
    	GuiStyle.styleFieldLabel(label_Password2);
		setupTextUI(text_Password2, "Arial", 15, 320, Pos.BASELINE_LEFT, 240, 308, true);
		text_Password2.setPromptText("Enter the Password Again");
		GuiStyle.styleField(text_Password2);

		// If the passwords do not match, this alert dialog will tell the user
		alertUsernamePasswordError.setTitle("Passwords Do Not Match");
		alertUsernamePasswordError.setHeaderText("The two passwords must be identical.");
		alertUsernamePasswordError.setContentText("Correct the passwords and try again.");

		// If the password is too weak, this alert dialog will tell the user what is missing.
		// The content text is set by the controller, since it depends on the password typed.
		alertPasswordRequirements.setTitle("Password Requirements");
		alertPasswordRequirements.setHeaderText("That password cannot be used.");

        // Take user to reset password page
        setupButtonUI(button_ConfirmPassword, "Dialog", 15, 320, Pos.CENTER, 240, 368);
        GuiStyle.stylePrimaryButton(button_ConfirmPassword);
        button_ConfirmPassword.setOnAction((_) -> {
        	System.out.println("**** Calling doResetPassword");
        	ControllerResetPassword.doResetPassword(theStage, theUser); });
		
        // Enable the user to quit the application.  GuiStyle places Quit in the same corner on
        // every page.
        GuiStyle.styleQuitButton(button_Quit);
        button_Quit.setOnAction((_) -> {ControllerResetPassword.performQuit(); });
	}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 */
	
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}	
}
