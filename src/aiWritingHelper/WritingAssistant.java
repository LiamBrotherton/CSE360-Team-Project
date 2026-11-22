package aiWritingHelper;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: WritingAssistant Interface. </p>
 *
 * <p> Description: The one thing the Contributor AI writing help needs from an assistant: given a
 * lesson field, the text it holds now, and what the Contributor asked for, return suggested text.
 * The system has no AI service, so the Model talks to this interface and never to a particular
 * assistant.  That is what lets the tests use assistants that return bad answers on purpose, and
 * what lets a real service replace the stand-in later without touching the rules.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-09 Initial version
 */
public interface WritingAssistant {

	/**********
	 * <p> Method: String suggest(String field, String currentText, String prompt) </p>
	 *
	 * <p> Description: Returns suggested text for one lesson field.  The reply is untrusted: it may
	 * be null, blank, too long, or contain control characters, and the caller must check it before
	 * using it.  An implementation may throw a RuntimeException if its service is not available. </p>
	 *
	 * @param field			LessonsLearned.FIELD_TITLE, LessonsLearned.FIELD_BODY, or LessonsLearned.FIELD_CATEGORY
	 * @param currentText	the text the field holds now
	 * @param prompt		what the Contributor asked the assistant to do
	 *
	 * @return the suggested text
	 */
	String suggest(String field, String currentText, String prompt);

	/**********
	 * <p> Method: List&lt;String&gt; suggestAlternatives(String field, String currentText,
	 * String prompt, int count) </p>
	 *
	 * <p> Description: Returns several different suggestions for one lesson field, for a
	 * Contributor who is brainstorming.  The default asks for one suggestion at a time and numbers
	 * each request, so an assistant that only knows how to give one answer still gives different
	 * ones, and an implementation that can give a list in one call may override this.  Like a single
	 * suggestion, every entry is untrusted and must be checked by the caller. </p>
	 *
	 * @param field			LessonsLearned.FIELD_TITLE, LessonsLearned.FIELD_BODY, or LessonsLearned.FIELD_CATEGORY
	 * @param currentText	the text the field holds now
	 * @param prompt		what the Contributor asked the assistant to do
	 * @param count			how many suggestions to ask for
	 *
	 * @return the suggestions, in the order they were produced
	 */
	default List<String> suggestAlternatives(String field, String currentText, String prompt,
			int count) {
		List<String> suggestions = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			// The number goes at the front, not the end.  A stand-in that cuts its reply to a short
			// limit would otherwise cut the number off and return the same text every time.
			suggestions.add(suggest(field, currentText, "Option " + i + " of " + count + ": " + prompt));
		}
		return suggestions;
	}
}
