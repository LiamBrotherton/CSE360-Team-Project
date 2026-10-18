package guiMultipleRoleDispatch;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiTools.GuiStyle;

/*******
 * <p> Title: ViewMultipleRoleDispatch Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Update Page.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-20 Initial version
 * @version 2.00		2026-10-16 Restyled to the team's user experience design: one card on a
 * 							full-page gradient, with Logout and Quit below it.  The role list, the
 * 							Perform Role button, and the actions are unchanged.
 * @version 2.10		2026-10-17 Polish: the title "Choose a role", a "Signed in as" line, the
 * 							button renamed "Continue", Logout moved onto the card, the shared font
 * 							and type scale, and the quiet Quit button in the same corner as on every
 * 							page.  The role list and the actions are unchanged.
 *
 */

public class ViewMultipleRoleDispatch {

	/*-****************************************************************************************

		Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.

	// GUI Area 1: It informs the user about the purpose of this page and whose account is being
	// used. There is no button to allow this user to update the account settings.
	private static Label label_PageTitle = new Label("Choose a role");
	private static Label label_UserDetails = new Label();

	// GUI Area 2: This area consists of a label to ask the user what roles to place, a ComboBox
	// so the user can select the role, and a button to perform that role.  Areas 1 and 2 sit on
	// one card.
	private static Label label_WhichRole = new Label("Role");
	protected static ComboBox <String> combobox_SelectRole = new ComboBox <String>();
	private static Button button_PerformRole = new Button("Continue");

	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and
	// logging out.  Logout sits on the card; Quit sits in the corner GuiStyle gives it on every page.
	private static Button button_Logout = new Button("Logout");
	private static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information

	private static ViewMultipleRoleDispatch theView;	// Used to determine if instantiation of
														// the class is needed


	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application

	private static Scene theMultipleRoleDispatchScene = null;	


	/*-********************************************************************************************

	Constructor

	 */

	public static void displayMultipleRoleDispatch(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		if (theView == null) theView = new ViewMultipleRoleDispatch();

		List<String> list = new ArrayList<String>();	// Create a new list empty list
		theDatabase.getUserAccountDetails(theUser.getUserName());
		
		label_UserDetails.setText("Signed in as " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 13, 320, Pos.BASELINE_LEFT, 240, 192);
		GuiStyle.styleMutedLabel(label_UserDetails);

		System.out.println("*** Getting multiple role details for user: " + theUser.getUserName());
		list = new ArrayList<String>();
		list.add("<Select a role>");
		if (theDatabase.getCurrentAdminRole()) list.add("Admin");
		if (theDatabase.getCurrentContributorRole()) list.add("Contributor");
		if (theDatabase.getCurrentViewerRole()) list.add("Viewer");
		if (theDatabase.getCurrentCuratorRole()) list.add("Curator");
		combobox_SelectRole.setItems(FXCollections.observableArrayList(list));

		// Populate the dynamic aspects of the GUI with the data from the user and the current
		combobox_SelectRole.getSelectionModel().select(0);
		
		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundation Code: Multiple Role Dispatch");	
		theStage.setScene(theMultipleRoleDispatchScene);		// Set this page onto the stage
		theStage.show();										// Display it to the user
	}


	/**********
	 * <p> Method: ViewMultipleRoleDispatch() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	private ViewMultipleRoleDispatch() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theMultipleRoleDispatchScene = new Scene(theRootPane, width, height);

		// The page is a single card on a full-page gradient, like the login page.  The card is
		// centered across the window and every widget on it is 320 wide inside its margins.
		GuiStyle.applyBackground(theRootPane);

		// Populate the window with the title and other common widgets and set their static state

		// GUI Area 1
		setupLabelUI(label_PageTitle, "Arial", 26, 320, Pos.BASELINE_LEFT, 240, 154);
		GuiStyle.stylePageTitle(label_PageTitle);

		// GUI Area 2
		setupLabelUI(label_WhichRole, "Arial", 13, 320, Pos.BASELINE_LEFT, 240, 228);
		GuiStyle.styleFieldLabel(label_WhichRole);

		setupComboBoxUI(combobox_SelectRole, "Dialog", 15, 320, 240, 248);
		GuiStyle.styleComboBox(combobox_SelectRole);

		setupButtonUI(button_PerformRole, "Dialog", 15, 320, Pos.CENTER, 240, 308);
		GuiStyle.stylePrimaryButton(button_PerformRole);
		button_PerformRole.setOnAction((_) ->
		{guiMultipleRoleDispatch.ControllerMultipleRoleDispatch.performRole(); });


		// GUI Area 3
		setupButtonUI(button_Logout, "Dialog", 15, 320, Pos.CENTER, 240, 358);
		GuiStyle.styleSecondaryButton(button_Logout);
		button_Logout.setOnAction((_) ->
		{guiMultipleRoleDispatch.ControllerMultipleRoleDispatch.performLogout(); });

		GuiStyle.styleQuitButton(button_Quit);
		button_Quit.setOnAction((_) ->
		{guiMultipleRoleDispatch.ControllerMultipleRoleDispatch.performQuit(); });

		// This is the end of the GUI Widgets for the page

		// Place all of the just-initialized GUI elements into the pane, the card first so it is
		// drawn behind the rest
		theRootPane.getChildren().addAll(
				GuiStyle.createCard(200, 90, 400, 336),
				GuiStyle.createLogo(240, 120),
				label_PageTitle,
				label_UserDetails,
				label_WhichRole,
				combobox_SelectRole,
				button_PerformRole,
				button_Logout,
				button_Quit);
	}


	/*-********************************************************************************************

		Helper methods to reduce code length

	 */

	/**********
	 * Private local method to initialize the standard fields for a label
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
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w, double x, 
			double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
}
