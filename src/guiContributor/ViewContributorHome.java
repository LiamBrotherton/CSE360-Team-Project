package guiContributor;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiTools.GuiStyle;


/*******
 * <p> Title: ViewContributorHome Class. </p>
 * 
 * <p> Description: The Java/FX-based Contributor Home Page.  The page is a stub for some role needed for
 * the application.  The widgets on this page are likely the minimum number and kind for other role
 * pages that may be needed.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-20 Initial version
 * @version 2.00		2026-10-16 Restyled to the team's user experience design: a full-page
 * 							gradient with white header text, and a row of cards, each holding the
 * 							button for one TP2 lesson feature.  The three feature buttons are
 * 							placeholders that open the Coming Soon page until the features are
 * 							built.  The Account Update, Logout, and Quit buttons and their actions
 * 							are unchanged.
 * @version 2.10		2026-10-17 Polish: a header bar with the logo, the signed-in user, and the
 * 							Account Update and Logout buttons; a page title with a line below it;
 * 							feature cards with a title and a description on the shared grid; and
 * 							the quiet Quit button in the same corner as on every page.
 *
 */

public class ViewContributorHome {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	private static Label label_Subtitle = new Label("Pick a task to get started.");
	protected static Button button_UpdateThisUser = new Button("Account Update");

	// GUI Area 2: One button for each TP2 lesson feature a Contributor will use, each on its own
	// card.  Until a feature is built its button opens the Coming Soon page.
	/** The button that opens the page for adding a lesson (a placeholder for now). */
	protected static Button button_AddLesson = new Button("Add a lesson");
	/** The button that opens the list of this Contributor's lessons (a placeholder for now). */
	protected static Button button_MyLessons = new Button("My lessons");
	/** The button that opens the page for adding experience information to a lesson (a placeholder for now). */
	protected static Button button_AddExperience = new Button("Add experience info");

	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and for
	// logging out.
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewContributorHome theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User
	

	private static Scene theViewContributorHomeScene;	// The shared Scene each invocation populates
	protected static final int theRole = 2;		// Admin: 1; Contributor: 2; Viewer: 3; Curator: 4

	/*-*******************************************************************************************

	Constructors
	
	 */


	/**********
	 * <p> Method: displayContributorHome(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Contributor Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GIUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayContributorHome(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewContributorHome();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		label_UserDetails.setText("Signed in as " + theUser.getUserName());
				
		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundations: Contributor Home Page");
		theStage.setScene(theViewContributorHomeScene);
		theStage.show();
		GuiStyle.startUnfocused(theRootPane);				// No header button looks pressed on arrival
	}
	
	/**********
	 * <p> Method: ViewContributorHome() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayRole2Home method.</p>
	 * 
	 */
	private ViewContributorHome() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theViewContributorHomeScene = new Scene(theRootPane, width, height);	// Create the scene
		
		// Set the title for the window
		
		// Populate the window with the title and other common widgets and set their static state
		
		// The page sits on a full-page gradient, so the header text is white
		GuiStyle.applyBackground(theRootPane);

		// GUI Area 1: the header bar across the top, then the page title.  GuiStyle places these
		// in the same spots on every home page.
		label_UserDetails.setText("Signed in as " + theUser.getUserName());
		GuiStyle.placeHomeHeader(label_UserDetails, button_UpdateThisUser, button_Logout);
		button_UpdateThisUser.setOnAction((_) -> {ControllerContributorHome.performUpdate(); });
		button_Logout.setOnAction((_) -> {ControllerContributorHome.performLogout(); });

		label_PageTitle.setText("Contributor Home");
		GuiStyle.placeHomeTitle(label_PageTitle, label_Subtitle);

		// GUI Area 2: one card for each lesson feature, on the shared grid.  Each card names the
		// feature, says in a line what it is for, and holds its button.
		button_AddLesson.setOnAction((_) -> {ControllerContributorHome.performAddLesson(); });
		button_MyLessons.setOnAction((_) -> {ControllerContributorHome.performMyLessons(); });
		button_AddExperience.setOnAction((_) -> {ControllerContributorHome.performAddExperience(); });

		// GUI Area 3: Quit, which GuiStyle places in the same corner on every page
		GuiStyle.styleQuitButton(button_Quit);
        button_Quit.setOnAction((_) -> {ControllerContributorHome.performQuit(); });

		// This is the end of the GUI initialization code

		// Place all of the widget items into the Root Pane's list of children, the header bar first
		// so it is drawn behind the widgets that sit on it
         theRootPane.getChildren().addAll(
			GuiStyle.createHeaderBar(), GuiStyle.createLogo(24, 20, true),
			label_UserDetails, button_UpdateThisUser, button_Logout,
			label_PageTitle, label_Subtitle,
			GuiStyle.createFeatureCard(GuiStyle.featureCardX(0), GuiStyle.FEATURE_ROW_Y, "New lesson",
					"Record what happened on a project and what you learned.", button_AddLesson),
			GuiStyle.createFeatureCard(GuiStyle.featureCardX(1), GuiStyle.FEATURE_ROW_Y, "Your lessons",
					"See the lessons you have added and open one to review.", button_MyLessons),
			GuiStyle.createFeatureCard(GuiStyle.featureCardX(2), GuiStyle.FEATURE_ROW_Y, "Experience",
					"Add the time and effort from a new attempt to a lesson.", button_AddExperience),
			button_Quit);
	}
}
