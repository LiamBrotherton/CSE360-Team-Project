package aiWritingHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*******
 * <p> Title: AlternativesResult Class. </p>
 *
 * <p> Description: The answer to a request for several alternatives: either the alternatives, each
 * already cleaned and validated, or the message explaining why there are none.  It follows the
 * convention of SuggestionResult, that an empty error means success, so a page shows the message the
 * same way as every other validation message.  Exactly one of the two values is meaningful.</p>
 *
 * <p> Tested by: AiWritingHelperTestingAutomation, tests R8.1 to R8.9.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-19 Initial version
 */
public class AlternativesResult {

	private final List<String> texts;	// The alternatives, or empty when there is an error
	private final String error;			// The reason there are none, or "" when there are some

	// Private so the only ways to make a result are the two factory methods below
	private AlternativesResult(List<String> texts, String error) {
		this.texts = texts;
		this.error = error;
	}

	/**********
	 * <p> Method: AlternativesResult success(List&lt;String&gt; texts) </p>
	 *
	 * <p> Description: Makes a result that carries alternatives. </p>
	 *
	 * @param texts the alternatives, already cleaned and validated
	 *
	 * @return a result with those alternatives and no error
	 */
	public static AlternativesResult success(List<String> texts) {
		return new AlternativesResult(Collections.unmodifiableList(new ArrayList<>(texts)), "");
	}

	/**********
	 * <p> Method: AlternativesResult failure(String error) </p>
	 *
	 * <p> Description: Makes a result that carries only a reason. </p>
	 *
	 * @param error the message explaining why there are no alternatives
	 *
	 * @return a result with no alternatives and that error
	 */
	public static AlternativesResult failure(String error) {
		return new AlternativesResult(Collections.emptyList(), error);
	}

	/**********
	 * <p> Method: boolean isOk() </p>
	 *
	 * <p> Description: Tells whether the request produced usable alternatives. </p>
	 *
	 * @return true if there are alternatives, false if there is an error
	 */
	public boolean isOk() {
		return error.isEmpty();
	}

	/**********
	 * <p> Method: List&lt;String&gt; getTexts() </p>
	 *
	 * <p> Description: Gets the alternatives. </p>
	 *
	 * @return the alternatives, or an empty list when the request failed
	 */
	public List<String> getTexts() {
		return texts;
	}

	/**********
	 * <p> Method: String getError() </p>
	 *
	 * <p> Description: Gets the message explaining why there are no alternatives. </p>
	 *
	 * @return the error message, or the empty String when the request succeeded
	 */
	public String getError() {
		return error;
	}
}
