package aiWritingHelper;

import entityClasses.LessonsLearned;
import inputRecognizer.LessonValidator;

/*******
 * <p> Title: StubWritingAssistant Class. </p>
 *
 * <p> Description: A stand-in for a real AI writing assistant.  The project has no AI service, so
 * this returns a simple draft built from the Contributor's request.  It exists so the Model and the
 * tests have a working assistant today; it is not meant to write well.  It always keeps its reply
 * inside the limit for the field it was asked about, so a person trying the prototype is not
 * refused for a mistake the stand-in made.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-09 Initial version
 */
public class StubWritingAssistant implements WritingAssistant {

	/**
	 * Default constructor.  The stub holds no state.
	 */
	public StubWritingAssistant() {
	}

	/**********
	 * <p> Method: String suggest(String field, String currentText, String prompt) </p>
	 *
	 * <p> Description: Returns "AI draft: " followed by the request, cut to the longest length the
	 * field allows. </p>
	 *
	 * @param field			the lesson field the draft is for
	 * @param currentText	the text the field holds now, not used by the stand-in
	 * @param prompt		what the Contributor asked for
	 *
	 * @return the draft text
	 */
	@Override
	public String suggest(String field, String currentText, String prompt) {
		String draft = "AI draft: " + prompt;

		// Cut to the field's limit rather than return something the validation would reject.
		// Category is the shortest limit, so it is the one most likely to need this.
		int limit;
		if (LessonsLearned.FIELD_TITLE.equals(field)) limit = LessonValidator.MAX_TITLE_LENGTH;
		else if (LessonsLearned.FIELD_CATEGORY.equals(field)) limit = LessonValidator.MAX_CATEGORY_LENGTH;
		else limit = LessonValidator.MAX_BODY_LENGTH;

		if (draft.length() > limit) draft = draft.substring(0, limit);
		return draft;
	}
}
