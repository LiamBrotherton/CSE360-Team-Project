package guiDeleteUser;

import database.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/*******
 * <p> Title: ControllerDeleteUser Class. </p>
 * 
 * <p> Description: The Java/FX-based Delete User Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page has one of the more complex Controller Classes due to the fact that the changing the
 * values of widgets changes the layout of the page.  It is up to the Controller to determine what
 * to do and it involves the proper elements from View Class for this GUI page.</p>
 * 
 * @author Alexis Wakefield, based on Lynn Robert Carter's code
 * 
 * @version 1.00		2026-09-15 Initial version
 */

public class ControllerDeleteUser {
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerDeleteUser() {
	}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;			
	/**********
	 * <p> Method: performDeleteUser() </p>
	 * 
	 * <p> Description: This method removes an existing user after receiving confirmation. 
	 * An admin cannot remove themselves if they are the final admin.</p>
	 * 
	 */
	protected static void performDeleteUser() {
		
		// Determine which item in the ComboBox list was selected
		ViewDeleteUser.theSelectedUser = (String) ViewDeleteUser.
				combobox_SelectUser.getValue();
		
		// If the selection is the list header (e.g., "<Select a User>") don't do anything
		if (ViewDeleteUser.theSelectedUser.compareTo("<Select a User>") != 0) {
			
			//ran into issues when deleting self, so this will keep track
			boolean deletingSelf = ViewDeleteUser.theSelectedUser.compareTo(ViewDeleteUser.theUser.getUserName()) == 0;
			
			// Confirm Deletion
			Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
			confirmation.setTitle("Delete User");
			confirmation.setHeaderText("Are you sure?");
			confirmation.setContentText("Are you sure you want to delete user " + ViewDeleteUser.theSelectedUser + "?");
			
			ButtonType result = confirmation.showAndWait().orElse(ButtonType.CANCEL); //if not yes, don't do it
			
			//delete them if yes
			if (result == ButtonType.OK) {
				
				//delete
				if (theDatabase.deleteUser(ViewDeleteUser.theSelectedUser) ) {
				
					//inform of deletion 
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Delete User");
					alert.setHeaderText("User Deleted");
					alert.setContentText("User " + ViewDeleteUser.theSelectedUser + " has been deleted.");
					alert.showAndWait();

					// If self-delete, log out due to account no longer existing
					if (deletingSelf) {
						performLogout();
					}
					else {
						//update home display
						//guiAdminHome.ViewAdminHome.label_NumberOfUsers.setText("Number of Users: " + theDatabase.getNumberOfUsers());
						// Otherwise, return to page to act as visual cue
						performReturn();
					}
				}
				else {
					// The role removal was rejected, likely because an admin 
					// cannot delete themselves if they are the last one
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Remove User Issue");
					alert.setHeaderText("Admin Role Cannot Be Removed");
					alert.setContentText("The Admin role cannot be removed from the last Admin.");
					alert.showAndWait();
				}
			}	
		}
	}
	
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user (who must be an Admin as only admins are the
	 * only users who have access to this page) to the Admin Home page. </p>
	 * 
	 */
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewDeleteUser.theStage,
				ViewDeleteUser.theUser);
	}
	
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewDeleteUser.theStage);
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
