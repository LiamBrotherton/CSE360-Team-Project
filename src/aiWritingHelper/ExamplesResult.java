package aiWritingHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import entityClasses.LessonsLearned;

/*******
 * <p> Title: ExamplesResult Class. </p>
 *
 * <p> Description: The answer to a search for examples: either the Contributor's own lessons that
 * match, which may be none, or the message explaining why the search could not be done.  A search
 * that finds nothing is a success with an empty list, not an error, because "nothing matched" is an
 * answer the Contributor can act on.  It follows the convention of SuggestionResult, that an empty
 * error means success.</p>
 *
 * <p> Tested by: AiWritingHelperTestingAutomation, tests R9.1 to R9.9.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-19 Initial version
 */
public class ExamplesResult {

	private final List<LessonsLearned> lessons;		// The matching lessons, or empty
	private final String error;						// Why the search was refused, or ""

	// Private so the only ways to make a result are the two factory methods below
	private ExamplesResult(List<LessonsLearned> lessons, String error) {
		this.lessons = lessons;
		this.error = error;
	}

	/**********
	 * <p> Method: ExamplesResult success(List&lt;LessonsLearned&gt; lessons) </p>
	 *
	 * <p> Description: Makes a result that carries the matching lessons. </p>
	 *
	 * @param lessons the matching lessons, possibly none
	 *
	 * @return a result with those lessons and no error
	 */
	public static ExamplesResult success(List<LessonsLearned> lessons) {
		return new ExamplesResult(Collections.unmodifiableList(new ArrayList<>(lessons)), "");
	}

	/**********
	 * <p> Method: ExamplesResult failure(String error) </p>
	 *
	 * <p> Description: Makes a result that carries only a reason. </p>
	 *
	 * @param error the message explaining why the search was refused
	 *
	 * @return a result with no lessons and that error
	 */
	public static ExamplesResult failure(String error) {
		return new ExamplesResult(Collections.emptyList(), error);
	}

	/**********
	 * <p> Method: boolean isOk() </p>
	 *
	 * <p> Description: Tells whether the search could be done. </p>
	 *
	 * @return true if the search was done, whether or not it found anything
	 */
	public boolean isOk() {
		return error.isEmpty();
	}

	/**********
	 * <p> Method: List&lt;LessonsLearned&gt; getLessons() </p>
	 *
	 * <p> Description: Gets the matching lessons. </p>
	 *
	 * @return the matching lessons, or an empty list when none matched or the search was refused
	 */
	public List<LessonsLearned> getLessons() {
		return lessons;
	}

	/**********
	 * <p> Method: String getError() </p>
	 *
	 * <p> Description: Gets the message explaining why the search was refused. </p>
	 *
	 * @return the error message, or the empty String when the search was done
	 */
	public String getError() {
		return error;
	}
}
