package guiListUsers;

import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: GUIListUsersPage Class. </p>
 * 
 * <p> Description: The Java/FX-based List Users Page.  This class allows an Admin to view
 * every user account currently in the system, showing each user's username, full name,
 * email address, and assigned roles in a table.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Dhruv
 * 
 * @version 1.00		2026-09-15 Initial version
 * @version 1.01		2026-09-15 Switched Username and Email columns from
 * 							PropertyValueFactory to explicit lambdas, since
 * 							PropertyValueFactory's reflection-based lookup was not
 * 							matching this User class's plain getters and left those
 * 							two columns blank.
 *  
 */
public class ViewListUsers {

    // These are the application values required by the user interface
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // GUI Area 1: Page title and separator
    protected static Label label_PageTitle = new Label();
    protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);

    // GUI Area 2: Table showing every user account and its details
    protected static TableView<User> table_Users = new TableView<User>();
    protected static TableColumn<User, String> col_UserName = new TableColumn<User, String>("Username");
    protected static TableColumn<User, String> col_Name = new TableColumn<User, String>("Name");
    protected static TableColumn<User, String> col_Email = new TableColumn<User, String>("Email");
    protected static TableColumn<User, String> col_Roles = new TableColumn<User, String>("Roles");

    // GUI Area 3: Navigation buttons, consistent with other Admin-only pages
    protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);
    protected static Button button_Return = new Button("Return");
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");

    // This is the end of the GUI objects for the page.

    // These attributes are used to configure the page and populate it with data
    private static ViewListUsers theView;	// Used to determine if instantiation of the class
											// is needed
    // Reference for the in-memory database so this package has access
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;			// The Stage that JavaFX has established for us
    protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
    protected static User theUser;				// The current user of the application
    public static Scene theListUsersScene = null;	// The Scene each invocation populates

    /**********
     * <p> Method: displayListUsers(Stage ps, User user) </p>
     * 
     * <p> Description: This method is the single entry point from outside this package to
     * cause the List Users page to be displayed.
     * 
     * It first sets up the shared attributes so we don't have to pass parameters.
     * 
     * It then checks to see if the page has been set up.  If not, it instantiates the class
     * and initializes all the static aspects of the GUI widgets.
     * 
     * After instantiation, it fetches the current list of all user accounts from the
     * database and populates the table, then sets the Scene onto the stage and makes it
     * visible to the user.
     * 
     * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
     * 
     * @param user specifies the currently logged-in Admin user viewing this page
     *
     */
    public static void displayListUsers(Stage ps, User user) {

        // Establish the references to the GUI and the current user
        theStage = ps;
        theUser = user;

        // If not yet established, populate the static aspects of the GUI by creating the
        // singleton instance of this class
        if (theView == null) theView = new ViewListUsers();

        // Fetch every user account from the database and populate the table
        List<User> userAccounts = theDatabase.getAllUserAccounts();
        table_Users.getItems().setAll(userAccounts);

        // Set the title for the window and show it
        theStage.setTitle("CSE 360 Foundation Code: List Users Page");
        theStage.setScene(theListUsersScene);
        theStage.show();
    }

    /**********
     * <p> Method: ViewListUsers() </p>
     * 
     * <p> Description: This method initializes all the elements of the graphical user
     * interface.  This method determines the location, size, font, and event handlers for
     * each GUI object.
     * 
     * This is a singleton, so this is performed just once.  Subsequent uses fetch fresh
     * data using the displayListUsers method.</p>
     * 
     */
    public ViewListUsers() {

        // This page is only reachable by an Admin, so we do not check other roles

        // Create the Pane for the list of widgets and the Scene for the window
        theRootPane = new Pane();
        theListUsersScene = new Scene(theRootPane, width, height);

        // Populate the window with the title and other common widgets and set their state

        // GUI Area 1
        label_PageTitle.setText("List of User Accounts");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        // GUI Area 2: Configure how each table column gets its value from a User object.
        // Username and Email use explicit lambdas rather than PropertyValueFactory,
        // since PropertyValueFactory relies on reflection that expects either a JavaFX
        // Property or a matching getter/setter convention, and it was not correctly
        // picking up getUserName() / getEmailAddress() on this User class.
        col_UserName.setCellValueFactory(cellData -> 
        new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserName()));

        col_Email.setCellValueFactory(cellData -> 
        new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmailAddress()));

        // Combine first and last name into a single displayed column
        col_Name.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            String fullName = u.getFirstName() + " " + u.getMiddleName() + " " + u.getLastName();
            return new javafx.beans.property.SimpleStringProperty(fullName);
        });

        // Build a readable string of every role this user currently plays
        col_Roles.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            StringBuilder roles = new StringBuilder();
            if (u.getAdminRole()) roles.append("Admin ");
            if (u.getNewRole1()) roles.append("Role1 ");
            if (u.getNewRole2()) roles.append("Role2 ");
            return new javafx.beans.property.SimpleStringProperty(roles.toString().trim());
        });

        table_Users.getColumns().addAll(col_UserName, col_Name, col_Email, col_Roles);
        table_Users.setLayoutX(20);
        table_Users.setLayoutY(110);
        table_Users.setPrefWidth(width - 40);
        table_Users.setPrefHeight(400);

        // GUI Area 3
        setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
        button_Return.setOnAction((_) -> { ControllerListUsers.performReturn(); });

        setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
        button_Logout.setOnAction((_) -> { ControllerListUsers.performLogout(); });

        setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
        button_Quit.setOnAction((_) -> { ControllerListUsers.performQuit(); });

        // This is the end of the GUI Widgets for the page

        // Add all the widgets to the Pane. Unlike AddRemoveRoles, this page's layout does
        // not change dynamically, so the widgets can be added once here rather than in
        // the Controller.
        theRootPane.getChildren().addAll(label_PageTitle, line_Separator1, table_Users,
                line_Separator4, button_Return, button_Logout, button_Quit);
    }

    /*-*******************************************************************************************

    Helper methods used to minimize the number of lines of code needed above

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
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
            double y) {
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
    protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
            double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}

