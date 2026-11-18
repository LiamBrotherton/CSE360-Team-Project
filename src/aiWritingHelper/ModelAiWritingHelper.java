package aiWritingHelper;

import database.Database;
import entityClasses.LessonsLearned;
import inputRecognizer.LessonValidator;

/*******
 * <p> Title: ModelAiWritingHelper Class. </p>
 *
 * <p> Description: The Contributor AI Writing Help Model.  It holds every rule for a Contributor
 * asking an assistant for help with one core field of a lesson, and for accepting what comes back.
 * The assistant is untrusted, so its reply is treated exactly like text a person typed: cleaned,
 * then checked by the same LessonValidator rules.  The rules live here and not in a page so the
 * testing automation can call exactly the code a page would call, without starting JavaFX.
 * This class has no JavaFX imports for that reason.</p>
 *
 * <p> Requirements satisfied:
 * <br>R1, only the Contributor who added a lesson can ask for help with it;
 * <br>R2, help is only given for a title, body, or category that is not locked;
 * <br>R3, the request must be present and no longer than MAX_PROMPT_LENGTH;
 * <br>R4, the reply is cleaned and validated, and an assistant that fails or is missing produces a
 * message instead of a crash;
 * <br>R5, a suggestion is never saved until acceptSuggestion is called;
 * <br>R6, acceptSuggestion repeats the ownership, lock, and text checks;
 * <br>R7, suggested text only ever reaches the database as a bound value, so text that looks like a
 * database command is stored as plain text.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-09 Initial version
 */
public class ModelAiWritingHelper {

	/** The longest request a Contributor may send.  The limit stops a huge paste being forwarded to
	 *  an assistant that may charge or fail by size. */
	public static final int MAX_PROMPT_LENGTH = 500;

	/** The message for a lesson that does not exist or belongs to someone else.  The two cases share
	 *  one message so a Contributor cannot learn that another person's lesson exists. */
	public static final String NOT_AVAILABLE = "That lesson is no longer available.";

	/** The message when the assistant is missing or fails. */
	public static final String ASSISTANT_UNAVAILABLE =
			"The writing assistant is not available right now. Try again later.";

	/** The message when the database refuses a change that passed every check. */
	public static final String SAVE_FAILED = "The lesson could not be saved. Try again.";

	/**
	 * Default constructor is not used.  Every method in this class is static.
	 */
	public ModelAiWritingHelper() {
	}

	/**********
	 * <p> Method: SuggestionResult requestSuggestion(Database db, WritingAssistant assistant,
	 * String username, int lessonId, String field, String prompt) </p>
	 *
	 * <p> Description: Asks the assistant for text for one field of a Contributor's lesson.  The
	 * checks run in this order: the lesson exists and is this Contributor's (R1); the field is one
	 * that may be changed and is not locked (R2); the request is acceptable (R3).  Only then is the
	 * assistant asked, so a refused request never costs the assistant any work.  Its reply is then
	 * cleaned and validated (R4).  Nothing is saved (R5). </p>
	 *
	 * @param db			the database to read
	 * @param assistant		the assistant to ask, which may be null
	 * @param username		the Contributor asking
	 * @param lessonId		the lesson the help is for
	 * @param field			LessonsLearned.FIELD_TITLE, LessonsLearned.FIELD_BODY, or LessonsLearned.FIELD_CATEGORY
	 * @param prompt		what the Contributor wants the assistant to do
	 *
	 * @return a result holding the suggested text, or the message explaining why there is none
	 */
	public static SuggestionResult requestSuggestion(Database db, WritingAssistant assistant,
			String username, int lessonId, String field, String prompt) {

		// R1: ownership is checked by the database call itself, which matches the lesson id AND the
		// owner.  Using it avoids a lookup by id alone that a later edit might forget to follow
		// with an owner check.
		LessonsLearned lesson = db.getLessonForContributor(lessonId, username);
		if (lesson == null) return SuggestionResult.failure(NOT_AVAILABLE);

		// R2: this also rejects a field name that is not lockable, so an unknown field is refused
		// here instead of being passed on.
		String problem = LessonValidator.checkFieldNotLocked(lesson, field);
		if (!problem.isEmpty()) return SuggestionResult.failure(problem);

		// R3: checked before the assistant is called, for the reason given above.
		problem = checkPrompt(prompt);
		if (!problem.isEmpty()) return SuggestionResult.failure(problem);

		if (assistant == null) return SuggestionResult.failure(ASSISTANT_UNAVAILABLE);

		String reply;
		try {
			reply = assistant.suggest(field, currentText(lesson, field), prompt.trim());
		} catch (RuntimeException e) {
			// Any failure inside an assistant must not reach the page.  The cause is not shown
			// to the Contributor because it may expose details of the service behind it.
			return SuggestionResult.failure(ASSISTANT_UNAVAILABLE);
		}

		// R4: the reply is untrusted, so it is cleaned and then held to the rules for typed text.
		String cleaned = clean(reply);
		problem = checkField(field, cleaned);
		if (!problem.isEmpty()) {
			return SuggestionResult.failure("The assistant's suggestion was rejected: " + problem);
		}
		return SuggestionResult.success(cleaned);
	}

	/**********
	 * <p> Method: String acceptSuggestion(Database db, String username, int lessonId, String field,
	 * String text) </p>
	 *
	 * <p> Description: Saves text the Contributor chose to keep.  Everything is checked again (R6):
	 * a Curator may have locked the field, or the lesson may have been deleted, after the
	 * suggestion was made, and the text may have been edited by the Contributor before accepting.
	 * The save goes through the database's owner-checked update, which repeats the owner and lock
	 * test inside the SQL (R7: the text is a bound value, never part of the command). </p>
	 *
	 * @param db			the database to change
	 * @param username		the Contributor accepting
	 * @param lessonId		the lesson to change
	 * @param field			LessonsLearned.FIELD_TITLE, LessonsLearned.FIELD_BODY, or LessonsLearned.FIELD_CATEGORY
	 * @param text			the text to save
	 *
	 * @return the empty String if the text was saved, else the message explaining why not
	 */
	public static String acceptSuggestion(Database db, String username, int lessonId, String field,
			String text) {

		LessonsLearned lesson = db.getLessonForContributor(lessonId, username);
		if (lesson == null) return NOT_AVAILABLE;

		String problem = LessonValidator.checkFieldNotLocked(lesson, field);
		if (!problem.isEmpty()) return problem;

		problem = checkField(field, text);
		if (!problem.isEmpty()) return problem;

		boolean saved;
		if (LessonsLearned.FIELD_TITLE.equals(field)) saved = db.updateContributorLessonTitle(lessonId, username, text);
		else if (LessonsLearned.FIELD_CATEGORY.equals(field)) saved = db.updateContributorLessonCategory(lessonId, username, text);
		else saved = db.updateContributorLessonBody(lessonId, username, text);
		return saved ? "" : SAVE_FAILED;
	}

	/**********
	 * <p> Method: String checkPrompt(String prompt) </p>
	 *
	 * <p> Description: Checks a request: not blank, and at most MAX_PROMPT_LENGTH characters. </p>
	 *
	 * @param prompt the request to check
	 *
	 * @return the empty String if the request is acceptable, else the message explaining why not
	 */
	public static String checkPrompt(String prompt) {
		if (prompt == null || prompt.trim().isEmpty()) return "The request cannot be blank.";
		if (prompt.length() > MAX_PROMPT_LENGTH) {
			return "The request is too long: " + prompt.length() + " characters, but the limit is "
					+ MAX_PROMPT_LENGTH + ".";
		}
		return "";
	}

	// Runs the LessonValidator rule for the field, so a suggestion is held to the same limits as
	// text a person typed.  An unknown field is reported rather than treated as valid.
	private static String checkField(String field, String text) {
		if (LessonsLearned.FIELD_TITLE.equals(field)) return LessonValidator.checkTitle(text);
		if (LessonsLearned.FIELD_BODY.equals(field)) return LessonValidator.checkBody(text);
		if (LessonsLearned.FIELD_CATEGORY.equals(field)) return LessonValidator.checkCategory(text);
		return "\"" + field + "\" is not a field the assistant can write.";
	}

	// Gives the assistant the text the field holds now, so it can improve it rather than start over.
	private static String currentText(LessonsLearned lesson, String field) {
		if (LessonsLearned.FIELD_TITLE.equals(field)) return lesson.getTitle();
		if (LessonsLearned.FIELD_CATEGORY.equals(field)) return lesson.getCategory();
		return lesson.getBody();
	}

	// Removes control characters other than a line break or tab, then trims the ends.  Control
	// characters (NUL, bell, and so on) have no place in lesson text and can break displays and
	// exports, so they are removed instead of trusted.  A line break is kept because a body may
	// legitimately have several lines.  Null becomes the empty String so the validator, not a
	// NullPointerException, reports it.
	private static String clean(String reply) {
		if (reply == null) return "";
		StringBuilder kept = new StringBuilder();
		for (int i = 0; i < reply.length(); i++) {
			char c = reply.charAt(i);
			if (Character.isISOControl(c) && c != '\n' && c != '\t') continue;
			kept.append(c);
		}
		return kept.toString().trim();
	}
}
