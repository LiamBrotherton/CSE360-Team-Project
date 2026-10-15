package guiTools;

import java.util.ArrayList;
import java.util.List;

import fPasswordPopUpWindow.Model;

/*******
 * <p> Title: PasswordCheck Class. </p>
 *
 * <p> Description: The one place the application asks whether a password is acceptable.
 *
 * The rules themselves are not implemented here.  This class calls
 * fPasswordPopUpWindow.Model.evaluatePassword, which is the finite state machine the
 * password testbed exercises, so the live application and the testing automation are
 * always judging passwords by exactly the same code.  Re-implementing the rules here would
 * let the two drift apart without any test noticing.
 *
 * What this class adds is wording.  The recognizer reports unmet requirements as a terse
 * list built for the testbed's console output, for example
 * "Upper case; Numeric digits; conditions were not satisfied".  That is fine on a console
 * and poor in a dialog, so the flags the recognizer sets are turned into a sentence a user
 * can act on.
 *
 * Note that evaluatePassword writes a running trace to System.out as it walks the input.
 * That is the testbed's behaviour and is left alone, so expect a burst of console output
 * each time a password is submitted.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-09-19 Initial version
 */
public class PasswordCheck {

	/*
	 * The exact set of symbols the recognizer accepts.  Listed here only so the error
	 * message can show the user what is allowed; the recognizer remains the authority on
	 * what actually passes.
	 */
	private static final String ALLOWED_SYMBOLS = "~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/";

	/**
	 * Default constructor is not used.  Every method in this class is static.
	 */
	public PasswordCheck() {
	}

	/**********
	 * <p> Method: firstProblem(String password) </p>
	 *
	 * <p> Description: Checks a proposed password and describes what is wrong with it.
	 *
	 * The current requirements, all of which must hold, are: at least one upper case letter,
	 * at least one lower case letter, at least one digit, at least one of the accepted
	 * symbols, and a length of at least eight characters.
	 *
	 * A space is not an accepted character, so passphrases are rejected.  That was a
	 * deliberate team decision: the recognizer rejected spaces while one of the password
	 * testbed cases asserted a password containing one was valid, and the recognizer was
	 * confirmed to be the correct side of that disagreement.</p>
	 *
	 * @param password the password the user proposed
	 *
	 * @return an empty String if the password is acceptable, otherwise a sentence explaining
	 * 			what needs to change
	 */
	public static String firstProblem(String password) {

		if (password == null || password.isEmpty()) {
			return "Enter a password.";
		}

		String raw = Model.evaluatePassword(password);

		// An empty result from the recognizer means every requirement was satisfied
		if (raw.isEmpty()) return "";

		/*
		 * The recognizer stops at the first character it does not recognise and reports it
		 * as a hard error rather than as an unmet requirement.  In that case its flags only
		 * describe the part of the input it managed to read, so listing them would be
		 * misleading - the user needs to know about the character instead.
		 */
		if (raw.startsWith("*** Error ***")) {

			/*
			 * Name the offending character when it is a space.  A space is invisible in
			 * the list of accepted symbols below, so somebody who typed a passphrase would
			 * otherwise be told their password contains a character that is not allowed
			 * while looking at a list that appears to contain nothing wrong.
			 *
			 * The recognizer records where it stopped, so the character is read from
			 * there rather than searched for.
			 */
			int at = Model.passwordIndexofError;
			if (at >= 0 && at < password.length()
					&& Character.isWhitespace(password.charAt(at))) {
				return "Passwords cannot contain spaces.";
			}

			return "That password contains a character that is not allowed. You can use "
					+ "letters, digits, and these symbols: " + ALLOWED_SYMBOLS;
		}

		// Otherwise, build the list of requirements that were not met
		List<String> missing = new ArrayList<String>();
		if (!Model.foundUpperCase)    missing.add("an upper case letter");
		if (!Model.foundLowerCase)    missing.add("a lower case letter");
		if (!Model.foundNumericDigit) missing.add("a digit");
		if (!Model.foundSpecialChar)  missing.add("a symbol");
		if (!Model.foundLongEnough)   missing.add("at least 8 characters");

		// Should not happen, but never return a sentence with nothing in it
		if (missing.isEmpty()) {
			return "That password does not meet the requirements.";
		}

		return "Your password still needs " + readableList(missing) + ".";
	}

	/**********
	 * <p> Method: readableList(List items) </p>
	 *
	 * <p> Description: Joins the unmet requirements the way a person would write them, so
	 * the message reads as a sentence rather than as a dumped list.</p>
	 *
	 * @param items the unmet requirements, which is never empty when this is called
	 *
	 * @return the items joined with commas and a final "and"
	 */
	private static String readableList(List<String> items) {

		if (items.size() == 1) return items.get(0);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < items.size(); i++) {
			if (i > 0) {
				sb.append(i == items.size() - 1 ? " and " : ", ");
			}
			sb.append(items.get(i));
		}
		return sb.toString();
	}
}
