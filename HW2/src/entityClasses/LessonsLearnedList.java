package entityClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * The LessonsLearnedList class stores a collection of LessonLearned
 * objects.
 *
 * The list may contain zero, one, or many lessons and can also represent
 * a subset of lessons returned from a search.
 */
public class LessonsLearnedList {

	private List<LessonLearned> lessons;

	public LessonsLearnedList() {
		lessons = new ArrayList<>();
	}

	public LessonsLearnedList(List<LessonLearned> lessons) {

		if (lessons == null) {
			this.lessons = new ArrayList<>();
		}
		else {
			this.lessons = new ArrayList<>(lessons);
		}
	}

	/**
	 * Adds one lesson to the list.
	 */
	public void addLesson(LessonLearned lesson) {

		if (lesson != null) {
			lessons.add(lesson);
		}
	}

	/**
	 * Removes one lesson from the list.
	 */
	public boolean removeLesson(LessonLearned lesson) {

		return lessons.remove(lesson);
	}

	/**
	 * Returns the lesson at the requested position.
	 */
	public LessonLearned getLesson(int index) {

		return lessons.get(index);
	}

	/**
	 * Returns the entire list.
	 */
	public List<LessonLearned> getLessons() {

		return new ArrayList<>(lessons);
	}

	/**
	 * Returns the number of lessons currently stored in this list.
	 */
	public int size() {

		return lessons.size();
	}

	/**
	 * Returns true when the list does not contain any lessons.
	 */
	public boolean isEmpty() {

		return lessons.isEmpty();
	}

	/**
	 * Removes all lessons from this list.
	 */
	public void clear() {

		lessons.clear();
	}

	/**
	 * Searches the current list using a keyword.
	 *
	 * The search checks the title, description, category, tags,
	 * and author fields.
	 */
	public LessonsLearnedList search(String keyword) {

		LessonsLearnedList results = new LessonsLearnedList();

		if (keyword == null || keyword.trim().isEmpty()) {
			return results;
		}

		String searchText = keyword.trim().toLowerCase();

		for (LessonLearned lesson : lessons) {

			if (containsIgnoreCase(lesson.getTitle(), searchText)
					|| containsIgnoreCase(lesson.getDescription(), searchText)
					|| containsIgnoreCase(lesson.getCategory(), searchText)
					|| containsIgnoreCase(lesson.getTags(), searchText)
					|| containsIgnoreCase(lesson.getAuthor(), searchText)) {

				results.addLesson(lesson);
			}
		}

		return results;
	}

	private boolean containsIgnoreCase(String value, String searchText) {

		if (value == null) {
			return false;
		}

		return value.toLowerCase().contains(searchText);
	}
}