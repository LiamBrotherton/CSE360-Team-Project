package aiWritingHelper;

/*******
 * <p> Title: SuggestionResult Class. </p>
 *
 * <p> Description: The answer to a request for writing help: either suggested text or the message
 * explaining why there is none.  It follows this project's convention that an empty error String
 * means success, so a page can show the message exactly as it shows every other validation message.
 * Exactly one of the two values is meaningful, and the constructors make a mixed result
 * impossible.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-09 Initial version
 */
public class SuggestionResult {

	private final String text;		// The suggested text, or "" when there is an error
	private final String error;		// The reason there is no suggestion, or "" when there is one

	// Private so the only ways to make a result are the two factory methods below
	private SuggestionResult(String text, String error) {
		this.text = text;
		this.error = error;
	}

	/**********
	 * <p> Method: SuggestionResult success(String text) </p>
	 *
	 * <p> Description: Makes a result that carries suggested text. </p>
	 *
	 * @param text the suggested text, already cleaned and validated
	 *
	 * @return a result with that text and no error
	 */
	public static SuggestionResult success(String text) {
		return new SuggestionResult(text, "");
	}

	/**********
	 * <p> Method: SuggestionResult failure(String error) </p>
	 *
	 * <p> Description: Makes a result that carries only a reason. </p>
	 *
	 * @param error the message explaining why there is no suggestion
	 *
	 * @return a result with no text and that error
	 */
	public static SuggestionResult failure(String error) {
		return new SuggestionResult("", error);
	}

	/**********
	 * <p> Method: boolean isOk() </p>
	 *
	 * <p> Description: Tells whether the request produced a usable suggestion. </p>
	 *
	 * @return true if there is suggested text, false if there is an error
	 */
	public boolean isOk() {
		return error.isEmpty();
	}

	/**********
	 * <p> Method: String getText() </p>
	 *
	 * <p> Description: Gets the suggested text. </p>
	 *
	 * @return the suggested text, or the empty String when the request failed
	 */
	public String getText() {
		return text;
	}

	/**********
	 * <p> Method: String getError() </p>
	 *
	 * <p> Description: Gets the message explaining why there is no suggestion. </p>
	 *
	 * @return the error message, or the empty String when the request succeeded
	 */
	public String getError() {
		return error;
	}
}
