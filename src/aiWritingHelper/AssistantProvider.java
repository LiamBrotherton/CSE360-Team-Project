package aiWritingHelper;

/*******
 * <p> Title: AssistantProvider Class. </p>
 *
 * <p> Description: The one place the application asks for the writing assistant.  Pages and
 * controllers call get() and never name a particular assistant, so replacing the stand-in with a
 * real AI service is one call to use(), made when the application starts, and no page or rule
 * changes.  Until something is chosen the provider holds the StubWritingAssistant, which needs no
 * service, so the application works on any machine.</p>
 *
 * <p> This is deliberately not a framework: it holds one assistant and hands it out.  There is no
 * configuration file and no lookup by name, because the project has one assistant to choose
 * from.</p>
 *
 * <p> Tested by: AiWritingHelperTestingAutomation, which passes its own assistants to the Model
 * directly and so does not depend on the one held here.  The provider itself is covered by the
 * manual tests of the Writing Help page.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-19 Initial version
 */
public class AssistantProvider {

	// The assistant every page uses.  Static because the whole application shares one.
	private static WritingAssistant current = new StubWritingAssistant();

	/**
	 * Default constructor is not used.  Every method in this class is static.
	 */
	public AssistantProvider() {
	}

	/**********
	 * <p> Method: WritingAssistant get() </p>
	 *
	 * <p> Description: Gives the assistant the application is using. </p>
	 *
	 * @return the current assistant, which may be null if use(null) was called, in which case the
	 * Model reports that the assistant is not available
	 */
	public static WritingAssistant get() {
		return current;
	}

	/**********
	 * <p> Method: void use(WritingAssistant assistant) </p>
	 *
	 * <p> Description: Chooses the assistant the application will use from now on. </p>
	 *
	 * @param assistant the assistant to use, or null to have none, which the Model treats as an
	 * assistant that is not available
	 */
	public static void use(WritingAssistant assistant) {
		current = assistant;
	}
}
