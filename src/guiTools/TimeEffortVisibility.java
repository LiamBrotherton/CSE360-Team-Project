package guiTools;

import entityClasses.User;

/*******
 * <p> Title: TimeEffortVisibility Class. </p>
 *
 * <p> Description: The one place that decides whether a viewer may see a lesson's time/effort
 * data (the duration and effort values added with a lesson's experience information).</p>
 *
 * <p> A Curator can mark a lesson's time/effort data as hidden.  This class applies that setting.
 * When the data is hidden, only a Curator is allowed to see the values; everyone else gets an
 * empty String.  Every screen that displays a duration or effort value should pass the value
 * through this class instead of making its own decision, so the rule cannot drift between
 * screens and a mistake in one screen cannot leak hidden data.</p>
 *
 * <p> This class has no database and no JavaFX dependency, so it can be tested on its own.
 * Storing the hidden/shown setting and letting a Curator change it are separate pieces that
 * are not part of this class.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Liam Brotherton
 *
 * @version 1.00		2026-11-11 Initial version
 */
public class TimeEffortVisibility {

	/**
	 * Default constructor is not used.  The only method in this class is static.
	 */
	public TimeEffortVisibility() {
	}

	/**********
	 * <p> Method: String visibleValue(String value, boolean hidden, User viewer) </p>
	 *
	 * <p> Description: Returns the time/effort value the given viewer is allowed to see.
	 *
	 * The rules, in the order they are applied, are:</p>
	 * <ol>
	 * <li>A null or empty value returns an empty String.</li>
	 * <li>A Curator always receives the value, because a Curator has to see the data to
	 * manage it.</li>
	 * <li>If the data is not hidden, every viewer receives the value.</li>
	 * <li>Otherwise (the data is hidden and the viewer is not a Curator) an empty String is
	 * returned.</li>
	 * </ol>
	 *
	 * <p> The requirement only names Contributors as the people the data is hidden from.  This
	 * method hides it from every user who is not a Curator, including a null viewer.  When the
	 * requirements say nothing about a case, the safe choice is to show less, because a value
	 * shown by mistake cannot be taken back, while a value hidden by mistake can be fixed.</p>
	 *
	 * @param value		the duration or effort text stored with the lesson, which may be null
	 * @param hidden	true if a Curator has marked this lesson's time/effort data as hidden
	 * @param viewer	the user who is looking at the lesson, which may be null
	 *
	 * @return the value if the viewer may see it, otherwise an empty String; never null
	 */
	public static String visibleValue(String value, boolean hidden, User viewer) {

		// Return an empty String instead of null so callers can put the result straight into a
		// label or message without a null check.  A null that slipped through could end up
		// printed as the word "null", which looks like data and confuses the viewer.
		if (value == null || value.isEmpty()) return "";

		// The Curator check comes before the hidden check because the Curator must always see
		// the data.  A user can play more than one role, so this tests the Curator role itself
		// rather than testing that the user is not a Contributor; a user who is both a
		// Contributor and a Curator has to be treated as a Curator.
		if (viewer != null && viewer.getCuratorRole()) return value;

		// Data that has not been hidden is visible to everyone.
		if (!hidden) return value;

		// Everything else falls through to here on purpose, including a null viewer.  The
		// method fails closed: hidden data is only returned when the code can positively show
		// that the viewer is a Curator.
		return "";
	}
}
