package guiOnetimePassword;

import database.Database;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;

/*******
 * <p> Title: ControllerOnetimePassword Class. </p>
 * 
 * <p> Description: The Java/FX-based Add Remove Roles Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page has one of the more complex Controller Classes due to the fact that the changing the
 * values of widgets changes the layout of the page.  It is up to the Controller to determine what
 * to do and it involves the proper elements from View Class for this GUI page.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation *  
 */

public class ControllerOnetimePassword {
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerOnetimePassword() {
	}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	/**********
	 * <p> Method: doSelectUser() </p>
	 * 
	 * <p> Description: This method uses the ComboBox widget, fetches which item in the ComboBox
	 * was selected (a user in this case), and establishes that user and the current user, setting
	 * easily accessible values without needing to do a query. </p>
	 * 
	 */
	protected static void doSelectUser() {
		ViewOnetimePassword.theSelectedUser = 
				(String) ViewOnetimePassword.combobox_SelectUser.getValue();
		theDatabase.getUserAccountDetails(ViewOnetimePassword.theSelectedUser);
		setupSelectedUser();
	}
	
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then establishes
	 * the appropriate list of widgets in the Pane to show the proper set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewOnetimePassword.theRootPane.getChildren().clear();
		
		// Defermine which of the two views to show to the user
		if (ViewOnetimePassword.theSelectedUser.compareTo("<Select a User>") == 0) {
			// Only show the request to select a user to be updated and the ComboBox
			ViewOnetimePassword.theRootPane.getChildren().addAll(
					ViewOnetimePassword.label_PageTitle, ViewOnetimePassword.label_UserDetails, 
					ViewOnetimePassword.button_UpdateThisUser, ViewOnetimePassword.line_Separator1,
					ViewOnetimePassword.label_SelectUser, ViewOnetimePassword.combobox_SelectUser, 
					ViewOnetimePassword.line_Separator4, ViewOnetimePassword.button_Return,
					ViewOnetimePassword.button_Logout, ViewOnetimePassword.button_Quit);
		}
		else {
			// Show all the fields as there is a selected user (as opposed to the prompt)
			ViewOnetimePassword.theRootPane.getChildren().addAll(
					ViewOnetimePassword.label_PageTitle, ViewOnetimePassword.label_UserDetails,
					ViewOnetimePassword.button_UpdateThisUser, ViewOnetimePassword.line_Separator1,
					ViewOnetimePassword.label_SelectUser,
					ViewOnetimePassword.combobox_SelectUser, 
					ViewOnetimePassword.label_CurrentRoles,
					ViewOnetimePassword.label_SelectRoleToBeAdded,
					ViewOnetimePassword.combobox_SelectRoleToAdd,
					ViewOnetimePassword.button_AddRole,
					ViewOnetimePassword.label_SelectRoleToBeRemoved,
					ViewOnetimePassword.combobox_SelectRoleToRemove,
					ViewOnetimePassword.button_RemoveRole,
					ViewOnetimePassword.line_Separator4, 
					ViewOnetimePassword.button_Return,
					ViewOnetimePassword.button_Logout,
					ViewOnetimePassword.button_Quit);
		}
		
		// Add the list of widgets to the stage and show it
		
		// Set the title for the window
		ViewOnetimePassword.theStage.setTitle("CSE 360 Foundation Code: Admin Opertaions Page");
		ViewOnetimePassword.theStage.setScene(ViewOnetimePassword.theOnetimePasswordScene);
		ViewOnetimePassword.theStage.show();
	}
	
	
	/**********
	 * <p> Method: setupSelectedUser() </p>
	 * 
	 * <p> Description: This method fetches the current values for the widgets whose values change
	 * based on which user has been selected and any actions that the admin takes. </p>
	 * 
	 */
	private static void setupSelectedUser() {
		System.out.println("*** Entering setupSelectedUser");
		
		// Create the list of roles that could be added for the currently selected user (e.g., Do
		// not show a role to add that the user already has!)
		ViewOnetimePassword.addList.clear();
		ViewOnetimePassword.addList.add("<Select a role>");
		if (!theDatabase.getCurrentAdminRole())
			ViewOnetimePassword.addList.add("Admin");
		if (!theDatabase.getCurrentNewRole1())
			ViewOnetimePassword.addList.add("Role1");
		if (!theDatabase.getCurrentNewRole2())
			ViewOnetimePassword.addList.add("Role2");

		// Create the list of roles that could be removed for the currently selected user (e.g., Do
		// not show a role to remove that the user does not have!)
		ViewOnetimePassword.removeList.clear();
		ViewOnetimePassword.removeList.add("<Select a role>");
		if (theDatabase.getCurrentAdminRole())
			ViewOnetimePassword.removeList.add("Admin");
		if (theDatabase.getCurrentNewRole1())
			ViewOnetimePassword.removeList.add("Role1");
		if (theDatabase.getCurrentNewRole2())
			ViewOnetimePassword.removeList.add("Role2");
		
		// Create the list or roles that the user currently has with proper use of a comma between
		// items
		boolean notTheFirst = false;
		String theCurrentRoles = "";
		
		// Admin role - It can only be at the head of a list
		if (theDatabase.getCurrentAdminRole()) {
			theCurrentRoles += "Admin";
			notTheFirst = true;
		}
		
		// Roles 1 - It could be at the head of the list or later in the list
		if (theDatabase.getCurrentNewRole1()) {
			if (notTheFirst)
				theCurrentRoles += ", Role1"; 
			else {
				theCurrentRoles += "Role1";
				notTheFirst = true;
			}
		}

		// Roles 2 - It could be at the head of the list or later in the list
		if (theDatabase.getCurrentNewRole2()) {
			if (notTheFirst)
				theCurrentRoles += ", Role2"; 
			else {
				theCurrentRoles += "Role2";
				notTheFirst = true;
			}
		}

		// Given the above actions, populate the related widgets with the new values
		ViewOnetimePassword.label_CurrentRoles.setText("This user's current roles: " + 
				theCurrentRoles);		
		ViewOnetimePassword.setupComboBoxUI(ViewOnetimePassword.combobox_SelectRoleToAdd, "Dialog",
				16, 150, 280, 205);
		ViewOnetimePassword.combobox_SelectRoleToAdd.setItems(FXCollections.
				observableArrayList(ViewOnetimePassword.addList));
		ViewOnetimePassword.combobox_SelectRoleToAdd.getSelectionModel().clearAndSelect(0);		
		ViewOnetimePassword.setupButtonUI(ViewOnetimePassword.button_AddRole, "Dialog", 16, 150, 
				Pos.CENTER, 460, 205);
		ViewOnetimePassword.setupComboBoxUI(ViewOnetimePassword.combobox_SelectRoleToRemove, "Dialog",
				16, 150, 280, 275);
		ViewOnetimePassword.combobox_SelectRoleToRemove.setItems(FXCollections.
				observableArrayList(ViewOnetimePassword.removeList));
		ViewOnetimePassword.combobox_SelectRoleToRemove.getSelectionModel().select(0);

		// Repaint the window showing this new values
		repaintTheWindow();

	}
	
	
	/**********
	 * <p> Method: performAddRole() </p>
	 * 
	 * <p> Description: This method adds a new role to the list of role in the ComboBox select
	 * list. </p>
	 * 
	 */
	protected static void performAddRole() {
		
		// Determine which item in the ComboBox list was selected
		ViewOnetimePassword.theAddRole =
				(String) ViewOnetimePassword.combobox_SelectRoleToAdd.getValue();
		
		// If the selection is the list header (e.g., "<Select a role>") don't do anything
		if (ViewOnetimePassword.theAddRole.compareTo("<Select a role>") != 0) {
			
			// If an actual role was selected, update the database entry for that user for the role
			if (theDatabase.updateUserRole(ViewOnetimePassword.theSelectedUser,
					ViewOnetimePassword.theAddRole, "true") ) {
				ViewOnetimePassword.combobox_SelectRoleToAdd = new ComboBox <String>();
				ViewOnetimePassword.combobox_SelectRoleToAdd.setItems(FXCollections.
					observableArrayList(ViewOnetimePassword.addList));
				ViewOnetimePassword.combobox_SelectRoleToAdd.getSelectionModel().clearAndSelect(0);		
				setupSelectedUser();
			}
		}
	}
	
	
	/**********
	 * <p> Method: performRemoveRole() </p>
	 * 
	 * <p> Description: This method removes an existing role to the list of role in the ComboBox
	 * select list. </p>
	 * 
	 */
	protected static void performRemoveRole() {
		
		// Determine which item in the ComboBox list was selected
		ViewOnetimePassword.theRemoveRole = (String) ViewOnetimePassword.
				combobox_SelectRoleToRemove.getValue();
		
		// If the selection is the list header (e.g., "<Select a role>") don't do anything
		if (ViewOnetimePassword.theRemoveRole.compareTo("<Select a role>") != 0) {
			
			// If an actual role was selected, update the database entry for that user for the role
			if (theDatabase.updateUserRole(ViewOnetimePassword.theSelectedUser, 
					ViewOnetimePassword.theRemoveRole, "false") ) {
				ViewOnetimePassword.combobox_SelectRoleToRemove = new ComboBox <String>();
				ViewOnetimePassword.combobox_SelectRoleToRemove.setItems(FXCollections.
					observableArrayList(ViewOnetimePassword.addList));
				ViewOnetimePassword.combobox_SelectRoleToRemove.getSelectionModel().
					clearAndSelect(0);		
				setupSelectedUser();
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
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewOnetimePassword.theStage,
				ViewOnetimePassword.theUser);
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewOnetimePassword.theStage);
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