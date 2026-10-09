package entityClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*******
 * <p> Title: LessonList Class </p>
 *
 * <p> Description: This LessonList class holds a collection of Lesson objects.  The same class
 * serves for every list the system needs: all the lessons currently stored, the lessons one
 * Contributor added, or the subset of lessons a keyword search returned.
 *
 * A list may be empty, may hold one lesson, or may hold arbitrarily many, because the system
 * places no upper limit on the number of lessons.  An empty result is therefore a normal,
 * expected value.  Callers always receive a LessonList, never null, so the code that displays a
 * search result does not need a separate null check in front of its empty check.
 *
 * The backing store is an ArrayList, which grows as needed and keeps the order the lessons were
 * added in.  The database layer decides that order, so a list read back with the newest lesson
 * first stays that way.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-04 Initial version
 */
public class LessonList {

	private final List<Lesson> lessons = new ArrayList<Lesson>();

	/*****
	 * <p> Method: LessonList() </p>
	 *
	 * <p> Description: This constructor builds an empty list. </p>
	 */
	public LessonList() {
	}

	/*****
	 * <p> Method: void add(Lesson lesson) </p>
	 *
	 * <p> Description: Append a lesson to the end of this list.  A null lesson is ignored, so a
	 * failed lookup cannot put a null into the middle of a list.</p>
	 *
	 * @param lesson specifies the lesson to append
	 */
	public void add(Lesson lesson) {
		if (lesson != null) lessons.add(lesson);
	}

	/*****
	 * <p> Method: int size() </p>
	 *
	 * <p> Description: Get the number of lessons in this list.</p>
	 *
	 * @return the number of lessons
	 */
	public int size() { return lessons.size(); }

	/*****
	 * <p> Method: boolean isEmpty() </p>
	 *
	 * <p> Description: Determine whether this list holds no lessons.</p>
	 *
	 * @return true if the list is empty, else false
	 */
	public boolean isEmpty() { return lessons.isEmpty(); }

	/*****
	 * <p> Method: Lesson get(int index) </p>
	 *
	 * <p> Description: Get the lesson at a position in this list.</p>
	 *
	 * @param index specifies the zero-based position
	 *
	 * @return the lesson at that position, or null if the position is outside the list
	 */
	public Lesson get(int index) {
		if (index < 0 || index >= lessons.size()) return null;
		return lessons.get(index);
	}

	/*****
	 * <p> Method: Lesson getById(int id) </p>
	 *
	 * <p> Description: Find the lesson with a given database id.</p>
	 *
	 * @param id specifies the database id to look for
	 *
	 * @return the matching lesson, or null if this list does not hold it
	 */
	public Lesson getById(int id) {
		for (Lesson l : lessons) {
			if (l.getId() == id) return l;
		}
		return null;
	}

	/*****
	 * <p> Method: boolean removeById(int id) </p>
	 *
	 * <p> Description: Remove the lesson with a given database id from this list.  This changes
	 * only the list; it does not delete anything from the database.</p>
	 *
	 * @param id specifies the database id of the lesson to remove
	 *
	 * @return true if a lesson was removed, else false
	 */
	public boolean removeById(int id) {
		for (int i = 0; i < lessons.size(); i++) {
			if (lessons.get(i).getId() == id) {
				lessons.remove(i);
				return true;
			}
		}
		return false;
	}

	/*****
	 * <p> Method: LessonList filterByCategory(String category) </p>
	 *
	 * <p> Description: Build the subset of this list that is filed under a category.  The result
	 * is a new list and this list is left unchanged.  The subset is empty when no lesson matches.
	 * </p>
	 *
	 * @param category specifies the category to keep
	 *
	 * @return a new list holding only the lessons in that category
	 */
	public LessonList filterByCategory(String category) {
		LessonList subset = new LessonList();
		for (Lesson l : lessons) {
			if (l.getCategory() != null && l.getCategory().equals(category)) subset.add(l);
		}
		return subset;
	}

	/*****
	 * <p> Method: List&lt;Lesson&gt; asList() </p>
	 *
	 * <p> Description: Get a read-only view of the lessons, so a caller can loop over them
	 * without being able to change this list behind its back.</p>
	 *
	 * @return an unmodifiable list of the lessons
	 */
	public List<Lesson> asList() { return Collections.unmodifiableList(lessons); }
}
