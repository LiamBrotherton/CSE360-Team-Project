package guiListUsers;

/*******
 * <p> Title: ControllerListUsers Class. </p>
 * 
 * <p> Description: The Java/FX-based List Users Page controller.  This class provides the
 * controller actions for navigation away from the List Users page: returning to the Admin
 * Home page, logging out, or quitting the application.
 * 
 * This page has no data-modifying actions since it is a read-only view of all user
 * accounts, so this controller is simpler than most others in this codebase.
 * 
 * The class has been written assuming that the View or the Model are the only class
 * methods that can invoke these methods.  This is why each has been declared "protected".
 * Do not change any of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Dhruv
 * 
 * @version 1.00		2026-09-15 Initial version
 */
public class ControllerListUsers {

    /**
     * Default constructor is not used.
     */
    public ControllerListUsers() {
    }

    /**********
     * <p> Method: performReturn() </p>
     * 
     * <p> Description: This method returns the user (who must be an Admin, as only admins
     * have access to this page) to the Admin Home page. </p>
     * 
     */
    protected static void performReturn() {
        guiAdminHome.ViewAdminHome.displayAdminHome(ViewListUsers.theStage,
                ViewListUsers.theUser);
    }

    /**********
     * <p> Method: performLogout() </p>
     * 
     * <p> Description: This method logs out the current user and proceeds to the normal
     * login page where existing users can log in or potential new users with an invitation
     * code can start the process of setting up an account. </p>
     * 
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewListUsers.theStage);
    }

    /**********
     * <p> Method: performQuit() </p>
     * 
     * <p> Description: This method terminates the execution of the program.  It leaves the
     * database in a state where the normal login page will be displayed when the
     * application is restarted.</p>
     * 
     */
    protected static void performQuit() {
        System.exit(0);
    }
}

