package inputRecognizer;

/**
 * LessonLearnedValidator performs validation on the fields used
 * when creating or updating a lesson learned.
 *
 * An empty string returned from validateLesson() means that the
 * lesson data passed validation.
 */
public class LessonLearnedValidator {

	private static final int MAX_TITLE_LENGTH = 150;
	private static final int MAX_DESCRIPTION_LENGTH = 2000;
	private static final int MAX_CATEGORY_LENGTH = 100;
	private static final int MAX_TAGS_LENGTH = 250;
	private static final int MAX_AUTHOR_LENGTH = 100;

	/**
	 * Validates all values used by a lesson learned.
	 *
	 * @return an empty String when valid, otherwise a helpful
	 *         error message
	 */
	public static String validateLesson(
			String title,
			String description,
			String category,
			String tags,
			String author) {

		String result;

		result = validateTitle(title);
		if (!result.isEmpty()) {
			return result;
		}

		result = validateDescription(description);
		if (!result.isEmpty()) {
			return result;
		}

		result = validateCategory(category);
		if (!result.isEmpty()) {
			return result;
		}

		result = validateTags(tags);
		if (!result.isEmpty()) {
			return result;
		}

		result = validateAuthor(author);
		if (!result.isEmpty()) {
			return result;
		}

		return "";
	}

	public static String validateTitle(String title) {

		if (title == null || title.trim().isEmpty()) {
			return "Title cannot be empty.";
		}

		if (title.trim().length() > MAX_TITLE_LENGTH) {
			return "Title cannot be longer than "
					+ MAX_TITLE_LENGTH + " characters.";
		}

		return "";
	}

	public static String validateDescription(String description) {

		if (description == null || description.trim().isEmpty()) {
			return "Lesson description cannot be empty.";
		}

		if (description.trim().length() > MAX_DESCRIPTION_LENGTH) {
			return "Lesson description cannot be longer than "
					+ MAX_DESCRIPTION_LENGTH + " characters.";
		}

		return "";
	}

	public static String validateCategory(String category) {

		if (category == null || category.trim().isEmpty()) {
			return "Category cannot be empty.";
		}

		if (category.trim().length() > MAX_CATEGORY_LENGTH) {
			return "Category cannot be longer than "
					+ MAX_CATEGORY_LENGTH + " characters.";
		}

		return "";
	}

	public static String validateTags(String tags) {

		if (tags == null || tags.trim().isEmpty()) {
			return "Tags cannot be empty.";
		}

		if (tags.trim().length() > MAX_TAGS_LENGTH) {
			return "Tags cannot be longer than "
					+ MAX_TAGS_LENGTH + " characters.";
		}

		return "";
	}

	public static String validateAuthor(String author) {

		if (author == null || author.trim().isEmpty()) {
			return "Author cannot be empty.";
		}

		if (author.trim().length() > MAX_AUTHOR_LENGTH) {
			return "Author cannot be longer than "
					+ MAX_AUTHOR_LENGTH + " characters.";
		}

		return "";
	}
}