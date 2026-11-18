package aiWritingHelper;

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
}
