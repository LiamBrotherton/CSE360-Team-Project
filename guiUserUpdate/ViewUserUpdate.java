package guiUserUpdate;

import java.util.Optional;

import database.Database;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;
import guiTools.GuiStyle;

/*******
 * <p> Title: ViewUserUpdate Class. </p>
 *
 * <p> Description: The Java/FX-based Account Update Page.  It lets any user change the name and
 * email address stored for the account, and it is the way to the page that changes the password.
 *
 * The page follows the team's user experience design, which took Discord's account settings as its
 * model.  A card holds a side menu on the left (My Account, Security, Back to home) and, on the
 * right, one row for each piece of account information with its current value and an Edit button.
 * The password is not shown on this page.  It has its own page, reached from the Security menu
 * item, so that account information and security settings are kept apart.
 *
 * The class has been written using a singleton design pattern and is the View portion of the
 * Model, View, Controller pattern.  All access starts by invoking the static method
 * displayUserUpdate.  No other method should attempt to instantiate this class as that is
 * controlled by displayUserUpdate.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 2.00		2026-10-16 Restyled to the team's user experience design: a card on a
 * 							full-page gradient with a side menu and one Edit row for each piece of
 * 							account information.  The password is no longer shown here, and the
 * 							Update Password button became the Security menu item, which opens the
 * 							same Reset Password page.  The Edit dialogs and their validation are
 * 							unchanged.
 * @version 2.10		2026-10-17 Polish: the shared font and type scale, controls of one height,
 * 							and a tooltip on every value showing the whole of it, so a value cut
 * 							short to fit its row can still be read.
 *
 */

public class ViewUserUpdate {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1: The side menu on the left of the card.  My Account is the page being shown, so
	// it has no action.  Security opens the Reset Password page, and Back to home returns to the
	// home page of the role being played.
	private static Button button_MyAccount = new Button("My Account");
	private static Button button_Security = new Button("Security");
	private static Button button_BackToHome = new Button("Back to home");

	// GUI Area 2: The title of the account information, on the right of the card
	private static Label label_ApplicationTitle = new Label("My Account");
	private static Label label_Purpose = new Label("Use this page to define or update your account information.");

	// GUI Area 3: One row for each piece of account information: its name, its current value, and
	// an Edit button where the value can be changed.  The user name has no button, as it cannot be
	// changed here.
	private static Label label_Username = new Label("Username");
	private static Label label_FirstName = new Label("First Name");
	private static Label label_MiddleName = new Label("Middle Name");
	private static Label label_LastName = new Label("Last Name");
	private static Label label_PreferredFirstName = new Label("Preferred First Name");
	private static Label label_EmailAddress = new Label("Email Address");

	private static Label label_CurrentUsername = new Label();
	private static Label label_CurrentFirstName = new Label();
	private static Label label_CurrentMiddleName = new Label();
	private static Label label_CurrentLastName = new Label();
	private static Label label_CurrentPreferredFirstName = new Label();
	private static Label label_CurrentEmailAddress = new Label();

	private static Button button_UpdateUsername = new Button("Update Username");
	private static Button button_UpdateFirstName = new Button("Edit");
	private static Button button_UpdateMiddleName = new Button("Edit");
	private static Button button_UpdateLastName = new Button("Edit");
	private static Button button_UpdatePreferredFirstName = new Button("Edit");
	private static Button button_UpdateEmailAddress = new Button("Edit");

	private static TextInputDialog dialogUpdateFirstName;
	private static TextInputDialog dialogUpdateMiddleName;
	private static TextInputDialog dialogUpdateLastName;
	private static TextInputDialog dialogUpdatePreferredFirstName;
	private static TextInputDialog dialogUpdateEmailAddresss;

	/** UI Alert for validation errors. */
	public static Alert alertUpdateError = new Alert(AlertType.ERROR);

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewUserUpdate theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	private static Stage theStage;
	private static Pane theRootPane;
	private static User theUser;

	/** The Scene for the user update page. */
	public static Scene theUserUpdateScene = null;
	private static Optional<String> result;

	// The geometry of the rows on the right of the card.  Every row has its name above its current
	// value.  A value longer than the room it has is cut short with an ellipsis instead of running
	// into the Edit button.
	private static final double rowX = 280;			// Left edge of the names and values
	private static final double valueWidth = 340;	// The room a value has before it is cut short
	private static final double editX = 640;		// Left edge of the Edit buttons
	private static final double firstRowY = 130;	// Top of the first row
	private static final double rowStep = 66;		// Distance from one row to the next

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayUserUpdate(Stage ps, User user) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Account Update page to be displayed.
	 *
	 * It first sets up the shared attributes so we don't have to pass parameters.  It then checks
	 * to see if the page has been setup.  If not, it instantiates the class, which initializes all
	 * the static aspects of the GUI widgets.  After the instantiation, it fills in the current
	 * value of each piece of account information, sets the Scene onto the stage, and makes it
	 * visible to the user.</p>
	 *
	 * @param ps the Stage to display the page
	 *
	 * @param user the User whose information is being updated
	 */
	public static void displayUserUpdate(Stage ps, User user) {
		theUser = user;
		theStage = ps;

		if (theView == null) theView = new ViewUserUpdate();

		String s = theUser.getUserName();
		System.out.println("*** Fetching account data for user: " + s);
		if (s == null || s.length() < 1) GuiStyle.setValueText(label_CurrentUsername, "<none>");
		else GuiStyle.setValueText(label_CurrentUsername, s);

		s = theUser.getFirstName();
		if (s == null || s.length() < 1) GuiStyle.setValueText(label_CurrentFirstName, "<none>");
		else GuiStyle.setValueText(label_CurrentFirstName, s);

		s = theUser.getMiddleName();
		if (s == null || s.length() < 1) GuiStyle.setValueText(label_CurrentMiddleName, "<none>");
		else GuiStyle.setValueText(label_CurrentMiddleName, s);

		s = theUser.getLastName();
		if (s == null || s.length() < 1) GuiStyle.setValueText(label_CurrentLastName, "<none>");
		else GuiStyle.setValueText(label_CurrentLastName, s);

		s = theUser.getPreferredFirstName();
		if (s == null || s.length() < 1) GuiStyle.setValueText(label_CurrentPreferredFirstName, "<none>");
		else GuiStyle.setValueText(label_CurrentPreferredFirstName, s);

		s = theUser.getEmailAddress();
		if (s == null || s.length() < 1) GuiStyle.setValueText(label_CurrentEmailAddress, "<none>");
		else GuiStyle.setValueText(label_CurrentEmailAddress, s);

		theStage.setTitle("CSE 360 Foundation Code: Update User Account Details");
		theStage.setScene(theUserUpdateScene);
		theStage.show();
		GuiStyle.startUnfocused(theRootPane);	// No button looks pressed on arrival
	}

	/**********
	 * <p> Method: ViewUserUpdate() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 *
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayUserUpdate method.</p>
	 *
	 */
	private ViewUserUpdate() {
		theRootPane = new Pane();
		theUserUpdateScene = new Scene(theRootPane, width, height);
		GuiStyle.applyBackground(theRootPane);

		dialogUpdateFirstName = new TextInputDialog("");
		dialogUpdateMiddleName = new TextInputDialog("");
		dialogUpdateLastName = new TextInputDialog("");
		dialogUpdatePreferredFirstName = new TextInputDialog("");
		dialogUpdateEmailAddresss = new TextInputDialog("");

		dialogUpdateFirstName.setTitle("Update First Name");
		dialogUpdateFirstName.setHeaderText("Update your First Name");

		dialogUpdateMiddleName.setTitle("Update Middle Name");
		dialogUpdateMiddleName.setHeaderText("Update your Middle Name");

		dialogUpdateLastName.setTitle("Update Last Name");
		dialogUpdateLastName.setHeaderText("Update your Last Name");

		dialogUpdatePreferredFirstName.setTitle("Update Preferred First Name");
		dialogUpdatePreferredFirstName.setHeaderText("Update your Preferred First Name");

		dialogUpdateEmailAddresss.setTitle("Update Email Address");
		dialogUpdateEmailAddresss.setHeaderText("Update your Email Address");

		alertUpdateError.setTitle("Update Error");
		alertUpdateError.setHeaderText("Invalid Input");

		// GUI Area 1: the side menu
		setupMenuButtonUI(button_MyAccount, 130);
		GuiStyle.styleMenuButton(button_MyAccount, true);

		setupMenuButtonUI(button_Security, 176);
		GuiStyle.styleMenuButton(button_Security, false);
		button_Security.setOnAction((_) -> {
			System.out.println("**** Calling doCallResetPassword");
			ControllerUserUpdate.doCallResetPassword(theStage, theUser);
		});

		setupMenuButtonUI(button_BackToHome, 222);
		GuiStyle.styleMenuButton(button_BackToHome, false);
		button_BackToHome.setOnAction((_) -> ControllerUserUpdate.goToUserHomePage(theStage, theUser));

		// GUI Area 2: the title
		setupLabelUI(label_ApplicationTitle, "Arial", 26, 440, Pos.BASELINE_LEFT, rowX, 55);
		GuiStyle.stylePageTitle(label_ApplicationTitle);
		setupLabelUI(label_Purpose, "Arial", 14, 450, Pos.BASELINE_LEFT, rowX, 92);
		GuiStyle.styleMutedLabel(label_Purpose);

		// GUI Area 3: the rows.  The user name is the first row and has no Edit button.
		setupRowUI(label_Username, label_CurrentUsername, 0);

		// First Name
		setupRowUI(label_FirstName, label_CurrentFirstName, 1);
		setupEditButtonUI(button_UpdateFirstName, 1);
		button_UpdateFirstName.setOnAction((_) -> {
			result = dialogUpdateFirstName.showAndWait();
			result.ifPresent(value -> {
				if (ControllerUserUpdate.invalidName(value, "First Name")) {
					alertUpdateError.setContentText(ControllerUserUpdate.nameErrorMessage);
					alertUpdateError.showAndWait();
					return;
				}
				theDatabase.updateFirstName(theUser.getUserName(), value);
				theDatabase.getUserAccountDetails(theUser.getUserName());
				String newName = theDatabase.getCurrentFirstName();
				theUser.setFirstName(newName);
				GuiStyle.setValueText(label_CurrentFirstName, (newName == null || newName.length() < 1) ? "<none>" : newName);
			});
		});

		// Middle Name
		setupRowUI(label_MiddleName, label_CurrentMiddleName, 2);
		setupEditButtonUI(button_UpdateMiddleName, 2);
		button_UpdateMiddleName.setOnAction((_) -> {
			result = dialogUpdateMiddleName.showAndWait();
			result.ifPresent(value -> {
				if (ControllerUserUpdate.invalidName(value, "Middle Name")) {
					alertUpdateError.setContentText(ControllerUserUpdate.nameErrorMessage);
					alertUpdateError.showAndWait();
					return;
				}
				theDatabase.updateMiddleName(theUser.getUserName(), value);
				theDatabase.getUserAccountDetails(theUser.getUserName());
				String newName = theDatabase.getCurrentMiddleName();
				theUser.setMiddleName(newName);
				GuiStyle.setValueText(label_CurrentMiddleName, (newName == null || newName.length() < 1) ? "<none>" : newName);
			});
		});

		// Last Name
		setupRowUI(label_LastName, label_CurrentLastName, 3);
		setupEditButtonUI(button_UpdateLastName, 3);
		button_UpdateLastName.setOnAction((_) -> {
			result = dialogUpdateLastName.showAndWait();
			result.ifPresent(value -> {
				if (ControllerUserUpdate.invalidName(value, "Last Name")) {
					alertUpdateError.setContentText(ControllerUserUpdate.nameErrorMessage);
					alertUpdateError.showAndWait();
					return;
				}
				theDatabase.updateLastName(theUser.getUserName(), value);
				theDatabase.getUserAccountDetails(theUser.getUserName());
				String newName = theDatabase.getCurrentLastName();
				theUser.setLastName(newName);
				GuiStyle.setValueText(label_CurrentLastName, (newName == null || newName.length() < 1) ? "<none>" : newName);
			});
		});

		// Preferred First Name
		setupRowUI(label_PreferredFirstName, label_CurrentPreferredFirstName, 4);
		setupEditButtonUI(button_UpdatePreferredFirstName, 4);
		button_UpdatePreferredFirstName.setOnAction((_) -> {
			result = dialogUpdatePreferredFirstName.showAndWait();
			result.ifPresent(value -> {
				if (ControllerUserUpdate.invalidName(value, "Preferred First Name")) {
					alertUpdateError.setContentText(ControllerUserUpdate.nameErrorMessage);
					alertUpdateError.showAndWait();
					return;
				}
				theDatabase.updatePreferredFirstName(theUser.getUserName(), value);
				theDatabase.getUserAccountDetails(theUser.getUserName());
				String newName = theDatabase.getCurrentPreferredFirstName();
				theUser.setPreferredFirstName(newName);
				GuiStyle.setValueText(label_CurrentPreferredFirstName, (newName == null || newName.length() < 1) ? "<none>" : newName);
			});
		});

		// Email Address
		setupRowUI(label_EmailAddress, label_CurrentEmailAddress, 5);
		setupEditButtonUI(button_UpdateEmailAddress, 5);
		button_UpdateEmailAddress.setOnAction((_) -> {
			result = dialogUpdateEmailAddresss.showAndWait();
			result.ifPresent(email -> {
				if (ControllerUserUpdate.invalidEmailAddress(email)) {
					alertUpdateError.setContentText(ControllerUserUpdate.emailAddressErrorMessage);
					alertUpdateError.showAndWait();
					return;
				}
				theDatabase.updateEmailAddress(theUser.getUserName(), email);
				theDatabase.getUserAccountDetails(theUser.getUserName());
				String newEmail = theDatabase.getCurrentEmailAddress();
				theUser.setEmailAddress(newEmail);
				GuiStyle.setValueText(label_CurrentEmailAddress, (newEmail == null || newEmail.length() < 1) ? "<none>" : newEmail);
			});
		});

		// The card first, then its side panel, so both are drawn behind the widgets that follow.
		// A thin line separates each row from the next.
		theRootPane.getChildren().addAll(
				GuiStyle.createCard(40, 40, 720, 520),
				GuiStyle.createSidebar(40, 40, 230, 520),
				GuiStyle.createLogo(55, 65),
				button_MyAccount, button_Security, button_BackToHome,
				label_ApplicationTitle, label_Purpose,
				label_Username, label_CurrentUsername,
				label_FirstName, label_CurrentFirstName, button_UpdateFirstName,
				label_MiddleName, label_CurrentMiddleName, button_UpdateMiddleName,
				label_LastName, label_CurrentLastName, button_UpdateLastName,
				label_PreferredFirstName, label_CurrentPreferredFirstName,
				button_UpdatePreferredFirstName,
				label_EmailAddress, label_CurrentEmailAddress, button_UpdateEmailAddress);
		for (int row = 1; row <= 5; row++) {
			Line divider = new Line(rowX, firstRowY + row * rowStep - 10, 735, firstRowY + row * rowStep - 10);
			divider.setStroke(Color.web("#e6e8ec"));
			theRootPane.getChildren().add(divider);
		}
	}

	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	/**********
	 * Private local method to initialize the standard fields for a label
	 *
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Label
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
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
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	/**********
	 * Private local method to place one item of the side menu.  Every item has the same size and
	 * its text is aligned to the left.
	 *
	 * @param b		The menu Button to be placed
	 * @param y		The location from the top (y axis)
	 */
	private static void setupMenuButtonUI(Button b, double y) {
		setupButtonUI(b, "Dialog", 16, 180, Pos.CENTER_LEFT, 55, y);
	}

	/**********
	 * Private local method to place one row of account information: its name in small gray text
	 * with its current value below.  The value is held to a fixed width, so a value that is too
	 * long is cut short with an ellipsis and never reaches the Edit button.
	 *
	 * @param name	The Label holding the name of the information (e.g. "Email Address")
	 * @param value	The Label holding its current value
	 * @param row	The position of the row, counting from 0 at the top
	 */
	private static void setupRowUI(Label name, Label value, int row) {
		double y = firstRowY + row * rowStep;
		setupLabelUI(name, "Arial", 13, valueWidth, Pos.BASELINE_LEFT, rowX, y);
		GuiStyle.styleFieldLabel(name);
		setupLabelUI(value, "Arial", 15, valueWidth, Pos.BASELINE_LEFT, rowX, y + 20);
		value.setMaxWidth(valueWidth);
		GuiStyle.styleCardLabel(value);
	}

	/**********
	 * Private local method to place and style the Edit button of one row.
	 *
	 * @param b		The Edit Button to be placed
	 * @param row	The position of the row, counting from 0 at the top
	 */
	private static void setupEditButtonUI(Button b, int row) {
		setupButtonUI(b, "Dialog", 16, 90, Pos.CENTER, editX, firstRowY + row * rowStep + 8);
		GuiStyle.styleSecondaryButton(b);
	}
}
