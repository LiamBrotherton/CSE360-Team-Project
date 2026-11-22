package guiWritingHelp;

import java.util.ArrayList;
import java.util.List;
import aiWritingHelper.AlternativesResult;
import aiWritingHelper.AssistantProvider;
import aiWritingHelper.ExamplesResult;
import aiWritingHelper.ModelAiWritingHelper;
import aiWritingHelper.SuggestionResult;
import entityClasses.LessonsLearned;

/*******
 * <p> Title: ControllerWritingHelp Class. </p>
 *
 * <p> Description: The Java/FX-based Writing Help page controller.  It provides the actions for the
 * buttons defined by the View class.  Each action reads what the Contributor typed, hands it to
 * ModelAiWritingHelper, which makes every check and every call to the assistant, and shows what the
 * Model returned.  The controller makes no decisions of its own about what is allowed.</p>
 *
 * <p> The class has been written assuming that the View is the only class that can invoke these
 * methods.  This is why each has been declared "protected".  Do not change any of these methods to
 * public.</p>
 *
 * <p> Tested by: the rules behind each action are tested by AiWritingHelperTestingAutomation.  The
 * actions themselves are covered by the manual tests of the Writing Help page.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-19 Initial version
 */

public class ControllerWritingHelp {

	/*-********************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View, which is a singleton instantiated object.

	 */

	/** How many ideas Brainstorm asks for.  Three is enough to compare without being a long list. */
	private static final int IDEA_COUNT = 3;

	/** The longest a row in the options list may be before it is cut short with an ellipsis.  It is
	 *  set by the width of the list, so a row never needs a sideways scroll bar. */
	private static final int ROW_LENGTH = 30;

	/**
	 * Default constructor is not used.
	 */
	public ControllerWritingHelp() {
	}

	/**********
	 * <p> Method: performSuggest() </p>
	 *
	 * <p> Description: This method asks the assistant for text for the chosen field and shows it in
	 * the suggestion box, where the Contributor can change it.  Nothing is saved (R5).  If the
	 * request is refused, the reason is shown and the suggestion box is left as it was. </p>
	 *
	 */
	protected static void performSuggest() {
		SuggestionResult result = ModelAiWritingHelper.requestSuggestion(
				ViewWritingHelp.theDatabase, AssistantProvider.get(),
				ViewWritingHelp.theUser.getUserName(), ViewWritingHelp.theLessonId,
				chosenField(), ViewWritingHelp.text_Request.getText());

		if (!result.isOk()) {
			ViewWritingHelp.showMessage(result.getError(), true);
			return;
		}
		ViewWritingHelp.area_Suggestion.setText(result.getText());
		ViewWritingHelp.showMessage("Edit the suggestion if you like, then press Use this text.", false);
	}

	/**********
	 * <p> Method: performBrainstorm() </p>
	 *
	 * <p> Description: This method asks the assistant for several different ideas for the chosen
	 * field and lists them.  Choosing one copies it into the suggestion box.  If every idea is
	 * refused, the reason is shown (R8). </p>
	 *
	 */
	protected static void performBrainstorm() {
		AlternativesResult result = ModelAiWritingHelper.requestAlternatives(
				ViewWritingHelp.theDatabase, AssistantProvider.get(),
				ViewWritingHelp.theUser.getUserName(), ViewWritingHelp.theLessonId,
				chosenField(), ViewWritingHelp.text_Request.getText(), IDEA_COUNT);

		if (!result.isOk()) {
			ViewWritingHelp.showMessage(result.getError(), true);
			return;
		}

		List<String> rows = new ArrayList<>();
		for (String idea : result.getTexts()) rows.add(shorten(idea));
		ViewWritingHelp.showOptions(false, rows, new ArrayList<>(result.getTexts()), new ArrayList<>());
		ViewWritingHelp.showMessage("Choose an idea to copy it into the suggestion box.", false);
	}

	/**********
	 * <p> Method: performExamples() </p>
	 *
	 * <p> Description: This method searches the Contributor's own lessons for what was typed in the
	 * request box, and lists the lessons that match.  A search that finds nothing says so, and one
	 * that is refused shows the reason (R9). </p>
	 *
	 */
	protected static void performExamples() {
		ExamplesResult result = ModelAiWritingHelper.findExamples(ViewWritingHelp.theDatabase,
				ViewWritingHelp.theUser.getUserName(), ViewWritingHelp.text_Request.getText());

		if (!result.isOk()) {
			ViewWritingHelp.showMessage(result.getError(), true);
			return;
		}

		List<String> rows = new ArrayList<>();
		for (LessonsLearned lesson : result.getLessons()) {
			rows.add(shorten(lesson.getId() + ": " + lesson.getTitle()));
		}
		ViewWritingHelp.showOptions(true, rows, new ArrayList<>(), new ArrayList<>(result.getLessons()));

		ViewWritingHelp.showMessage(result.getLessons().isEmpty()
				? "None of your lessons match that."
				: "Choose a lesson to read how you wrote it.", false);
	}

	/**********
	 * <p> Method: performOptionChosen() </p>
	 *
	 * <p> Description: This method runs when a row in the options list is chosen.  An idea is copied
	 * into the suggestion box.  An example is shown in the message line, and is not copied, because
	 * what is in the suggestion box is what Use this text would save. </p>
	 *
	 */
	protected static void performOptionChosen() {
		int row = ViewWritingHelp.list_Options.getSelectionModel().getSelectedIndex();
		if (row < 0) return;	// The list was emptied, so no row is chosen

		if (ViewWritingHelp.showingExamples) {
			LessonsLearned lesson = ViewWritingHelp.exampleLessons.get(row);
			ViewWritingHelp.showMessage(lesson.getTitle() + " (" + lesson.getCategory() + "): "
					+ shorten(lesson.getBody(), 130), false);
		} else {
			ViewWritingHelp.area_Suggestion.setText(ViewWritingHelp.ideaTexts.get(row));
			ViewWritingHelp.showMessage("Idea copied.  Edit it if you like, then press Use this text.", false);
		}
	}

	/**********
	 * <p> Method: performAccept() </p>
	 *
	 * <p> Description: This method saves what is in the suggestion box into the chosen field of the
	 * lesson.  The Model checks everything again (R6), because the field may have been locked, or
	 * the text changed by the Contributor, since the suggestion was made.  The reason is shown if
	 * the save is refused. </p>
	 *
	 */
	protected static void performAccept() {
		String problem = ModelAiWritingHelper.acceptSuggestion(ViewWritingHelp.theDatabase,
				ViewWritingHelp.theUser.getUserName(), ViewWritingHelp.theLessonId, chosenField(),
				ViewWritingHelp.area_Suggestion.getText());

		if (!problem.isEmpty()) {
			ViewWritingHelp.showMessage(problem, true);
			return;
		}
		ViewWritingHelp.showMessage("Saved.  The lesson now holds this text.", false);
	}

	/**********
	 * <p> Method: performBack() </p>
	 *
	 * <p> Description: This method returns the Contributor to the Lesson Details page for the lesson,
	 * which reads the lesson again, so a change that was saved is shown there. </p>
	 *
	 */
	protected static void performBack() {
		guiLessonDetail.ViewLessonDetail.displayLessonDetail(ViewWritingHelp.theStage,
				ViewWritingHelp.theUser, ViewWritingHelp.theLessonId);
	}

	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	/**********
	 * Private local method to turn the field chosen in the drop-down into the name the Model uses.
	 * With nothing chosen it gives the empty String, which the Model refuses as not a field.
	 *
	 * @return LessonsLearned.FIELD_TITLE, FIELD_BODY, or FIELD_CATEGORY, or the empty String
	 */
	private static String chosenField() {
		String chosen = ViewWritingHelp.combo_Field.getValue();
		if (chosen == null) return "";
		return chosen.toLowerCase();	// "Title" is title, and so on, which is how the constants are spelled
	}

	/**********
	 * Private local method to cut a long text short for a row in a list, ending it with an ellipsis,
	 * and to show a multi-line text on one line.
	 *
	 * @param text	the text to shorten
	 * @param limit	the most characters to keep
	 *
	 * @return the text on one line, no longer than the limit plus the ellipsis
	 */
	private static String shorten(String text, int limit) {
		String oneLine = text.replace('\n', ' ').replace('\t', ' ');
		return oneLine.length() <= limit ? oneLine : oneLine.substring(0, limit) + "...";
	}

	/**********
	 * Private local method to shorten a text to the length of a row in the options list.
	 *
	 * @param text	the text to shorten
	 *
	 * @return the text as a row
	 */
	private static String shorten(String text) {
		return shorten(text, ROW_LENGTH);
	}
}
