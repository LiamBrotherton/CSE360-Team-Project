package guiAdminHome;

import database.Database;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
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

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerAdminHome() {
	}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	public static String emailAddressErrorMessage = "";	// The error message text
	public static String emailAddressInput = "";		// The input being processed
	public static int emailAddressIndexofError = -1;	// The index where the error was located
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	private static boolean finalState = false;			// Is this state a final state?
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is 
	private static int domainPartCounter = 0;			// A domain name may not exceed 63 characters
	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
	        ViewAdminHome.alertEmailError.setContentText(emailAddressErrorMessage);
	        ViewAdminHome.alertEmailError.showAndWait();
	        return;
	    }
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);

		// A null code means the invitation was never stored, so say so rather than
		// showing the Admin a code that nobody can use.
		if (invitationCode == null) {
			ViewAdminHome.alertEmailError.setContentText(
					"The invitation could not be created. Please try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}

		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to set a onetime password for a user. </p>
	 */
	protected static void setOnetimePassword () {
		guiOnetimePassword.ViewOnetimePassword.displayOnetimePassword(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is allows an admin to delete someone's account
	 * Confirmation must be given before an account is removed. If there is one admin
	 * (or less than one), they cannot be deleted. </p>
	 */
	protected static void deleteUser() {
		guiDeleteUser.ViewDeleteUser.displayDeleteUser(
				ViewAdminHome.theStage, ViewAdminHome.theUser);
	}

	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to view a list of every user
	 * account currently in the system.  This is done by invoking the ListUsers Page. There
	 * is no need to specify the home page for the return as this can only be initiated by
	 * an Admin.</p>
	 */
	protected static void listUsers() {
	    guiListUsers.ViewListUsers.displayListUsers(ViewAdminHome.theStage, ViewAdminHome.theUser);
	}

	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		// The following are the local variable used to perform the Finite State Machine simulation
				state = 0;							// This is the FSM state number
				String input = emailAddress;
				inputLine = input;					// Save the reference to the input line as a global
				currentCharNdx = 0;					// The index of the current character

				// The Finite State Machines continues until the end of the input is reached or at some 
				// state the current character does not match any valid transition to a next state

				emailAddressInput = input;			// Save a copy of the input

				// Let's ensure there is input
				if (input.length() <= 0) {
					emailAddressErrorMessage = "There was no email address found.\n";
					emailAddressErrorMessage = emailAddressErrorMessage + displayInput(input, 0);
					return true;
				}
				currentChar = input.charAt(0);		// The current character from the above indexed position

				// Let's ensure the address is not too long
				if (input.length() > 255) {
					emailAddressErrorMessage = "A valid email address must be no more than 255 characters.\n";
					emailAddressErrorMessage = emailAddressErrorMessage + displayInput(input, 255);
					return true;
				}
				running = true;						// Start the loop
				System.out.println("\nCurrent Final Input  Next  DomainName\nState   State Char  State  Size");

				// The Finite State Machines continues until the end of the input is reached or at some 
				// state the current character does not match any valid transition to a next state
				while (running) {
					// The switch statement takes the execution to the code for the current state, where
					// that code sees whether or not the current character is valid to transition to a
					// next state
					nextState = -1;						// Default to there is no next state		
					
					switch (state) {
					case 0: 
						// State 0 has just 1 valid transition.
						// The current character is must be checked against 62 options. If any are matched
						// the FSM must go to state 1
						// The first and the second check for an alphabet character the third a numeric
						if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
								(currentChar >= 'a' && currentChar <= 'z') ||	// Lower case
								(currentChar >= '0' && currentChar <= '9')) {	// Digit
							nextState = 1;
						}
										
						// If it is none of those characters, the FSM halts
						else { 
							running = false;
						}
						
						break;				
						// The execution of this state is finished
					
					case 1: 
						// State 1 has three valid transitions.  

						// Another local-part (LPChar) character keeps us in state 1
						if ((currentChar >= 'A' && currentChar <= 'Z') ||
								(currentChar >= 'a' && currentChar <= 'z') ||
								(currentChar >= '0' && currentChar <= '9')) {
							nextState = 1;
						}
						// A period sends us back to state 0 to start a new local-part segment
						else if (currentChar == '.') {
							nextState = 0;
						}
						// The @ symbol moves us into the domain part
						else if (currentChar == '@') {
							nextState = 2;
						}
						// Anything else is invalid here, so the FSM halts
						else {
							running = false;
						}
						
						break;
						// The execution of this state is finished
									
					case 2: 
						// State 2 has one valid transition.
						
						// A domain-part (DPChar) character moves us into state 3
						if ((currentChar >= 'A' && currentChar <= 'Z') ||
								(currentChar >= 'a' && currentChar <= 'z') ||
								(currentChar >= '0' && currentChar <= '9')) {
							nextState = 3;
						}
						else {
							running = false;
						}

						// The execution of this state is finished
						break;
			
					case 3:
						// State 3 has three valid transition.
						
						// Another domain-part character keeps us in state 3
						if ((currentChar >= 'A' && currentChar <= 'Z') ||
								(currentChar >= 'a' && currentChar <= 'z') ||
								(currentChar >= '0' && currentChar <= '9')) {
							nextState = 3;
						}
						// A period sends us back to state 2 to start a new domain segment
						else if (currentChar == '.') {
							nextState = 2;
						}
						// A hyphen moves us to state 4
						else if (currentChar == '-') {
							nextState = 4;
						}
						// Anything else halts the FSM. Note state 3 is the accepting state,
						// so running out of input here (Eol) is handled after the loop, not
						// treated as an error in this switch.
						else {
							running = false;
						}

						// The execution of this state is finished
						break;

					case 4: 
						// State 4 has one valid transition.

						// A domain-part character after the hyphen sends us back to state 3
						if ((currentChar >= 'A' && currentChar <= 'Z') ||
								(currentChar >= 'a' && currentChar <= 'z') ||
								(currentChar >= '0' && currentChar <= '9')) {
							nextState = 3;
						}
						else {
							running = false;
						}

						// The execution of this state is finished
						break;

					}
					
					if (running) {
						displayDebuggingInfo();
						// When the processing of a state has finished, the FSM proceeds to the next character
						// in the input and if there is one, it fetches that character and updates the 
						// currentChar.  If there is no next character the currentChar is set to a blank.
						
						moveToNextCharacter();
						
						// Move to the next state
						state = nextState;
						nextState = -1;
					}
					// Should the FSM get here, the loop starts again

				}
				displayDebuggingInfo();
				
				System.out.println("The loop has ended.");

				emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
				
				// When the FSM halts, we must determine if the situation is an error or not.  That depends
				// of the current state of the FSM and whether or not the whole string has been consumed.
				// This switch directs the execution to separate code for each of the FSM states and that
				// makes it possible for this code to display a very specific error message to improve the
				// user experience.
				switch (state) {
				case 0:
					// State 0 is not a final state, so we can return a very specific error message
					emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
					emailAddressErrorMessage = "May only be alphanumberic.\n";
					return true;

				case 1:
					// State 1 is not a final state, so we can return a very specific error message

					emailAddressIndexofError = currentCharNdx;
					emailAddressErrorMessage = "The character(s) after an alphanumeric character must be "
							+ "alphanumeric, a period, or an @ symbol.\n";
					emailAddressErrorMessage = emailAddressErrorMessage + displayInput(input, currentCharNdx);
					return true;

				case 2:
					// State 2 is not a final state, so we can return a very specific error message
								
					emailAddressIndexofError = currentCharNdx;
					emailAddressErrorMessage = "There must be at least one alphanumeric character "
							+ "after the @ symbol.\n";
					emailAddressErrorMessage = emailAddressErrorMessage + displayInput(input, currentCharNdx);
					return true;

				case 3:
					// State 3 is a Final State, so this is not an error if the input is empty, otherwise
					// we can return a very specific error message.

					if (currentCharNdx<input.length()) {
						// If not all of the string has been consumed, we point to the current character
						// in the input line and specify what that character must be in order to move
						// forward.
						emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
						emailAddressErrorMessage = "This must be the end of the input.\n";
						emailAddressErrorMessage = emailAddressErrorMessage + displayInput(input, currentCharNdx);
						return true;
					}
					else 
					{
						emailAddressIndexofError = -1;
						emailAddressErrorMessage = "";
						return false;
					}

				case 4:
					// State 4 is not a final state, so we can return a very specific error message. 

					emailAddressIndexofError = currentCharNdx;
					emailAddressErrorMessage = "There must be at least one alphanumeric character "
							+ "after a hyphen in the domain name.\n";
					emailAddressErrorMessage = emailAddressErrorMessage + displayInput(input, currentCharNdx);
					return true;

				default:
					return false;
				}
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
	
	private static String displayInput(String input, int currentCharNdx) {
		// Display the entire input line
		String result = input.substring(0,currentCharNdx) + "?\n";

		return result;
	}


	private static void displayDebuggingInfo() {
		// Display the current state of the FSM as part of an execution trace
		if (currentCharNdx >= inputLine.length())
			// display the line with the current state numbers aligned
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
					((finalState) ? "       F   " : "           ") + "None");
		else
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
					((finalState) ? "       F   " : "           ") + "  " + currentChar + " " + 
					((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + 
					nextState + "     " + domainPartCounter);
	}

	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			System.out.println("End of input was found!");
			currentChar = ' ';
			running = false;
		}
	}
}
