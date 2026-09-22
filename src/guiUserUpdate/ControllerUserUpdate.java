package guiUserUpdate;

import entityClasses.User;
import javafx.stage.Stage;

public class ControllerUserUpdate {

	public ControllerUserUpdate() {
	}

	// Finite State Machine variables matching ControllerAdminHome
	public static String nameErrorMessage = "";       // The error message text
	public static String nameInput = "";              // The input being processed
	public static int nameIndexofError = -1;          // The index where the error was located
	private static int state = 0;                     // The current state value
	private static int nextState = 0;                 // The next state value
	private static String inputLine = "";             // The input line
	private static char currentChar;                  // The current character in the line
	private static int currentCharNdx;                // The index of the current character
	private static boolean running;                   // The flag that specifies if the FSM is running

	
	// Email validation state tracking
		public static String emailAddressErrorMessage = "";
		public static int emailAddressIndexofError = -1;

		/**********
		 * <p> Method: invalidEmailAddress(String emailAddress) </p>
		 * 
		 * <p> Description: Checks the email address using an FSM matching AdminHome. </p>
		 */
		protected static boolean invalidEmailAddress(String emailAddress) {
			state = 0;
			inputLine = (emailAddress == null) ? "" : emailAddress;
			currentCharNdx = 0;

			if (inputLine.length() <= 0) {
				emailAddressErrorMessage = "There was no email address found.\n"
						+ displayInput(inputLine, 0);
				emailAddressIndexofError = 0;
				return true;
			}

			if (inputLine.length() > 255) {
				emailAddressErrorMessage = "A valid email address must be no more than 255 characters.\n"
						+ displayInput(inputLine, Math.min(inputLine.length(), 255));
				emailAddressIndexofError = 255;
				return true;
			}

			currentChar = inputLine.charAt(0);
			running = true;

			while (running) {
				nextState = -1;

				switch (state) {
				case 0:
					if ((currentChar >= 'A' && currentChar <= 'Z') ||
						(currentChar >= 'a' && currentChar <= 'z') ||
						(currentChar >= '0' && currentChar <= '9')) {
						nextState = 1;
					} else {
						running = false;
					}
					break;

				case 1:
					if ((currentChar >= 'A' && currentChar <= 'Z') ||
						(currentChar >= 'a' && currentChar <= 'z') ||
						(currentChar >= '0' && currentChar <= '9')) {
						nextState = 1;
					} else if (currentChar == '.') {
						nextState = 0;
					} else if (currentChar == '@') {
						nextState = 2;
					} else {
						running = false;
					}
					break;

				case 2:
					if ((currentChar >= 'A' && currentChar <= 'Z') ||
						(currentChar >= 'a' && currentChar <= 'z') ||
						(currentChar >= '0' && currentChar <= '9')) {
						nextState = 3;
					} else {
						running = false;
					}
					break;

				case 3:
					if ((currentChar >= 'A' && currentChar <= 'Z') ||
						(currentChar >= 'a' && currentChar <= 'z') ||
						(currentChar >= '0' && currentChar <= '9')) {
						nextState = 3;
					} else if (currentChar == '.') {
						nextState = 2;
					} else if (currentChar == '-') {
						nextState = 4;
					} else {
						running = false;
					}
					break;

				case 4:
					if ((currentChar >= 'A' && currentChar <= 'Z') ||
						(currentChar >= 'a' && currentChar <= 'z') ||
						(currentChar >= '0' && currentChar <= '9')) {
						nextState = 3;
					} else {
						running = false;
					}
					break;
				}

				if (running) {
					moveToNextCharacter();
					state = nextState;
					nextState = -1;
				}
			}

			emailAddressIndexofError = currentCharNdx;

			switch (state) {
			case 0:
				emailAddressErrorMessage = "May only be alphanumeric.\n"
						+ displayInput(inputLine, currentCharNdx);
				return true;

			case 1:
				emailAddressErrorMessage = "The character(s) after an alphanumeric character must be "
						+ "alphanumeric, a period, or an @ symbol.\n"
						+ displayInput(inputLine, currentCharNdx);
				return true;

			case 2:
				emailAddressErrorMessage = "There must be at least one alphanumeric character "
						+ "after the @ symbol.\n"
						+ displayInput(inputLine, currentCharNdx);
				return true;

			case 3:
				if (currentCharNdx < inputLine.length()) {
					emailAddressErrorMessage = "This must be the end of the input.\n"
							+ displayInput(inputLine, currentCharNdx);
					return true;
				} else {
					emailAddressIndexofError = -1;
					emailAddressErrorMessage = "";
					return false;
				}

			case 4:
				emailAddressErrorMessage = "There must be at least one alphanumeric character "
						+ "after a hyphen in the domain name.\n"
						+ displayInput(inputLine, currentCharNdx);
				return true;

			default:
				return false;
			}
		}
		
	protected static void goToUserHomePage(Stage theStage, User theUser) {
		int theRole = applicationMain.FoundationsMain.activeHomePage;

		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
			break;
		case 2:
			guiContributor.ViewContributorHome.displayContributorHome(theStage, theUser);
			break;
		case 3:
			guiViewer.ViewViewerHome.displayViewerHome(theStage, theUser);
			break;
		case 4:
			guiCurator.ViewCuratorHome.displayCuratorHome(theStage, theUser);
			break;
		default: 
			System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + theRole);
			System.exit(0);
		}
	}
	
	protected static void doCallResetPassword(Stage theStage, User theUser) {
		guiResetPassword.ViewResetPassword.displayResetPassword(theStage, theUser);
	}

	/**********
	 * Checks a name string using a Finite State Machine matching ControllerAdminHome.
	 * Returns true if invalid, and populates nameErrorMessage.
	 */
	protected static boolean invalidName(String name, String fieldLabel) {
		state = 0;
		inputLine = (name == null) ? "" : name;
		currentCharNdx = 0;
		nameInput = inputLine;

		// Empty input validation
		if (inputLine.length() <= 0) {
			nameErrorMessage = "There was no " + fieldLabel.toLowerCase() + " found.\n"
					+ displayInput(inputLine, 0);
			nameIndexofError = 0;
			return true;
		}

		// Length validation
		if (inputLine.length() >= 30) {
			nameErrorMessage = fieldLabel + " must be shorter than 30 characters.\n"
					+ displayInput(inputLine, Math.min(inputLine.length(), 29));
			nameIndexofError = 29;
			return true;
		}

		currentChar = inputLine.charAt(0);
		running = true;

		// Run FSM loop
		while (running) {
			nextState = -1;

			switch (state) {
			case 0:
				// First character must be alphabetic
				if ((currentChar >= 'A' && currentChar <= 'Z') ||
					(currentChar >= 'a' && currentChar <= 'z')) {
					nextState = 1;
				} else {
					running = false;
				}
				break;

			case 1:
				// Subsequent characters must be alphabetic
				if ((currentChar >= 'A' && currentChar <= 'Z') ||
					(currentChar >= 'a' && currentChar <= 'z')) {
					nextState = 1;
				} else {
					running = false;
				}
				break;
			}

			if (running) {
				moveToNextCharacter();
				state = nextState;
				nextState = -1;
			}
		}

		nameIndexofError = currentCharNdx;

		// Formulate specific error messages matching state
		switch (state) {
		case 0:
			nameErrorMessage = fieldLabel + " must start with an alphabetic character.\n"
					+ displayInput(inputLine, currentCharNdx);
			return true;

		case 1:
			if (currentCharNdx < inputLine.length()) {
				nameErrorMessage = fieldLabel + " may only contain alphabetic characters.\n"
						+ displayInput(inputLine, currentCharNdx);
				return true;
			} else {
				nameIndexofError = -1;
				nameErrorMessage = "";
				return false;
			}

		default:
			return false;
		}
	}

	private static String displayInput(String input, int currentCharNdx) {
		return input.substring(0, currentCharNdx) + "?\n";
	}

	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length()) {
			currentChar = inputLine.charAt(currentCharNdx);
		} else {
			currentChar = ' ';
			running = false;
		}
	}
}