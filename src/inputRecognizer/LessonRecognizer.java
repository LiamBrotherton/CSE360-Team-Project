package inputRecognizer;

import entityClasses.Lesson;

/*******
 * <p> Title: LessonRecognizer Class. </p>
 *
 * <p> Description: Input validation for the fields of a lesson learned and for the keyword a
 * Viewer searches on.  Every check method returns the empty String when the input is acceptable,
 * and otherwise returns a message written for the person who typed the input.  A message names
 * the field, says which rule was broken, says what the limit is, and, where the problem sits at
 * one place in the input, says where.  "Title is invalid" would be a correct message but not a
 * helpful one: the author cannot fix a title from it.
 *
 * The detailed result is also left in the three public attributes below, in the same way the
 * UserNameRecognizer leaves its result, so a GUI could point at the offending character.
 *
 * These checks are deliberately simple loops rather than the finite state machines used for the
 * username and the password.  A lesson field has no internal structure to track from one
 * character to the next: the rules are a length range and a set of characters that may not
 * appear.  The tag list is the exception, since it has structure (comma separated items), and it
 * is checked one item at a time.
 *
 * Every method is "public static" so the controller and the testing automation can call exactly
 * the same code, so no test ever exercises a copy of a rule.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-04 Initial version
 */
public class LessonRecognizer {

	/*-*********************************************************************************************
	 *
	 * Limits.  These are public so the database column sizes, the controller, and the tests all
	 * agree with the one place the numbers are decided.
	 *
	 */
	/** The shortest acceptable title, in characters. */
	public static final int TITLE_MIN = 3;
	/** The longest acceptable title, in characters. */
	public static final int TITLE_MAX = 100;
	/** The shortest acceptable situation or lesson text, in characters. */
	public static final int TEXT_MIN = 10;
	/** The longest acceptable situation or lesson text, in characters. */
	public static final int TEXT_MAX = 2000;
	/** The shortest acceptable single tag, in characters. */
	public static final int TAG_MIN = 2;
	/** The longest acceptable single tag, in characters. */
	public static final int TAG_MAX = 25;
	/** The most tags one lesson may carry. */
	public static final int MAX_TAGS = 10;
	/** The shortest acceptable search keyword, in characters. */
	public static final int KEYWORD_MIN = 2;
	/** The longest acceptable search keyword, in characters. */
	public static final int KEYWORD_MAX = 100;

	/*-*********************************************************************************************
	 *
	 * Result attributes for GUI applications where a detailed error message and a pointer to the
	 * character of the error will enhance the user experience.
	 *
	 */
	/** The message produced by the most recent check, or the empty String if it passed. */
	public static String lessonErrorMessage = "";
	/** The input examined by the most recent check. */
	public static String lessonInput = "";
	/** The index of the offending character in that input, or -1 if the problem has no position. */
	public static int lessonIndexofError = -1;

	/**
	 * Default constructor is not used.  Every method in this class is static.
	 */
	public LessonRecognizer() {
	}

	/**********
	 * <p> Method: String checkTitle(String input) </p>
	 *
	 * <p> Description: Check a lesson title.  A title must be a single line of
	 * TITLE_MIN to TITLE_MAX characters. </p>
	 *
	 * @param input specifies the title to check
	 *
	 * @return the empty String if the title is acceptable, else a message explaining the problem
	 */
	public static String checkTitle(String input) {
		return checkText("Title", input, TITLE_MIN, TITLE_MAX, false);
	}

	/**********
	 * <p> Method: String checkSituation(String input) </p>
	 *
	 * <p> Description: Check the description of the situation that gave rise to a lesson.  It may
	 * run over several lines and must be TEXT_MIN to TEXT_MAX characters. </p>
	 *
	 * @param input specifies the situation text to check
	 *
	 * @return the empty String if the text is acceptable, else a message explaining the problem
	 */
	public static String checkSituation(String input) {
		return checkText("Situation", input, TEXT_MIN, TEXT_MAX, true);
	}

	/**********
	 * <p> Method: String checkLessonText(String input) </p>
	 *
	 * <p> Description: Check the text of what was learned.  It may run over several lines and must
	 * be TEXT_MIN to TEXT_MAX characters. </p>
	 *
	 * @param input specifies the lesson text to check
	 *
	 * @return the empty String if the text is acceptable, else a message explaining the problem
	 */
	public static String checkLessonText(String input) {
		return checkText("Lesson", input, TEXT_MIN, TEXT_MAX, true);
	}

	/**********
	 * <p> Method: String checkKeyword(String input) </p>
	 *
	 * <p> Description: Check a search keyword or phrase.  It must be a single line of
	 * KEYWORD_MIN to KEYWORD_MAX characters.  A one character keyword is refused because it would
	 * match nearly every lesson and so would not narrow anything down. </p>
	 *
	 * @param input specifies the keyword to check
	 *
	 * @return the empty String if the keyword is acceptable, else a message explaining the problem
	 */
	public static String checkKeyword(String input) {
		return checkText("Search keyword", input, KEYWORD_MIN, KEYWORD_MAX, false);
	}

	/**********
	 * <p> Method: String checkCategory(String input) </p>
	 *
	 * <p> Description: Check a category.  It must be exactly one of the values in
	 * Lesson.CATEGORIES.  The message lists the legal values, since the author's most likely
	 * mistake is a misspelling or a category the system does not have. </p>
	 *
	 * @param input specifies the category to check
	 *
	 * @return the empty String if the category is acceptable, else a message explaining the problem
	 */
	public static String checkCategory(String input) {
		reset(input);
		String legal = String.join(", ", Lesson.CATEGORIES);
		if (input == null || input.trim().isEmpty()) {
			lessonIndexofError = 0;
			return fail("Category is required. Choose one of: " + legal + ".");
		}
		for (String c : Lesson.CATEGORIES) {
			if (c.equals(input)) return "";
		}
		lessonIndexofError = 0;
		return fail("Category \"" + input + "\" is not recognized. Choose one of: " + legal + ".");
	}

	/**********
	 * <p> Method: String checkTags(String input) </p>
	 *
	 * <p> Description: Check a comma separated list of tags.  Tags are optional, so an empty list
	 * is acceptable.  When present, there may be at most MAX_TAGS, and each must be TAG_MIN to
	 * TAG_MAX characters of letters, digits, and hyphens.  Spaces around a comma are ignored. </p>
	 *
	 * @param input specifies the tag list to check
	 *
	 * @return the empty String if the tags are acceptable, else a message explaining the problem
	 */
	public static String checkTags(String input) {
		reset(input);
		if (input == null || input.trim().isEmpty()) return "";

		String[] tags = input.split(",", -1);
		if (tags.length > MAX_TAGS) {
			lessonIndexofError = -1;
			return fail("Too many tags: " + tags.length + " were entered, but a lesson may have at most "
					+ MAX_TAGS + ".");
		}

		int offset = 0;	// Where the current tag starts in the original input
		for (int t = 0; t < tags.length; t++) {
			String tag = tags[t].trim();
			int number = t + 1;
			lessonIndexofError = offset;

			if (tag.isEmpty())
				return fail("Tag " + number + " is empty. Remove the extra comma, or type a tag between"
						+ " the commas.");
			if (tag.length() < TAG_MIN)
				return fail("Tag " + number + " (\"" + tag + "\") is too short. A tag must be at least "
						+ TAG_MIN + " characters.");
			if (tag.length() > TAG_MAX)
				return fail("Tag " + number + " (\"" + tag + "\") is too long: " + tag.length()
						+ " characters, but the limit is " + TAG_MAX + ".");

			for (int i = 0; i < tag.length(); i++) {
				char c = tag.charAt(i);
				boolean letter = (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
				boolean digit = (c >= '0' && c <= '9');
				if (!(letter || digit || c == '-')) {
					lessonIndexofError = input.indexOf(tag, offset) + i;
					return fail("Tag " + number + " (\"" + tag + "\") contains the invalid character '" + c
							+ "'. Tags may use only letters, digits, and hyphens.");
				}
			}
			offset += tags[t].length() + 1;
		}
		lessonIndexofError = -1;
		return "";
	}

	/**********
	 * <p> Method: String normalizeTags(String input) </p>
	 *
	 * <p> Description: Put an acceptable tag list into the one form that gets stored: each tag
	 * trimmed, joined by a single comma with no spaces.  Without this, "java, h2" and "java,h2"
	 * would be two different stored values for the same tags.  This must only be called on input
	 * that checkTags accepted. </p>
	 *
	 * @param input specifies a tag list that checkTags has accepted
	 *
	 * @return the tag list in stored form, or the empty String if there are no tags
	 */
	public static String normalizeTags(String input) {
		if (input == null || input.trim().isEmpty()) return "";
		String[] tags = input.split(",", -1);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tags.length; i++) {
			if (i > 0) sb.append(",");
			sb.append(tags[i].trim());
		}
		return sb.toString();
	}

	/**********
	 * Private method that clears the result attributes before a check runs.
	 */
	private static void reset(String input) {
		lessonInput = input;
		lessonIndexofError = -1;
		lessonErrorMessage = "";
	}

	/**********
	 * Private method that records a message in the result attribute as well as returning it, so
	 * a check method can end with a single "return fail(...)".
	 */
	private static String fail(String message) {
		lessonErrorMessage = message;
		return message;
	}

	/**********
	 * Private method holding the checks the free text fields share: required, not too long, free
	 * of control characters, and not too short.  The order is deliberate.  A field with nothing
	 * in it gets the "required" message and not a "too short" one, since "too short" would
	 * suggest the author had made a start.  A field over the limit is reported before its
	 * characters are examined, since there is no point scanning 50,000 characters to report a
	 * problem with the 40,000th.
	 *
	 * Length is measured on the text as entered but the minimum is measured on the trimmed text,
	 * so a field made of three spaces and a letter does not pass a ten character minimum.
	 *
	 * @param fieldName	the name to use for this field in messages
	 * @param input		the text to check
	 * @param min		the fewest characters, not counting leading and trailing spaces
	 * @param max		the most characters
	 * @param multiLine	whether line breaks and tabs are allowed
	 *
	 * @return the empty String if the text is acceptable, else a message explaining the problem
	 */
	private static String checkText(String fieldName, String input, int min, int max,
			boolean multiLine) {
		reset(input);

		if (input == null || input.trim().isEmpty()) {
			lessonIndexofError = 0;
			return fail(fieldName + " is required. Enter at least " + min + " characters.");
		}

		if (input.length() > max) {
			lessonIndexofError = max;
			return fail(fieldName + " is too long: " + input.length() + " characters were entered, but the "
					+ "limit is " + max + ". Shorten it by " + (input.length() - max) + " characters.");
		}

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (!Character.isISOControl(c)) continue;
			if (multiLine && (c == '\n' || c == '\r' || c == '\t')) continue;
			lessonIndexofError = i;
			if (c == '\n' || c == '\r')
				return fail(fieldName + " must be a single line, but a line break was found at position "
						+ i + ".");
			return fail(fieldName + " contains a control character (code " + (int) c + ") at position "
					+ i + ". Use only printable characters.");
		}

		int trimmedLength = input.trim().length();
		if (trimmedLength < min) {
			lessonIndexofError = input.length();
			return fail(fieldName + " is too short: " + trimmedLength
					+ (trimmedLength == 1 ? " character was" : " characters were")
					+ " entered, but at least " + min + " are required.");
		}
		return "";
	}
}
