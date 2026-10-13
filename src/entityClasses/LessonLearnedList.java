package entityClasses;

import java.util.ArrayList;

/**
 * <p> Title: LessonLearnedList Class </p>
 *
 * <p> Description: This class stores collections of LessonLearned objects.
 * It supports storing all lessons learned as well as subsets of lessons,
 * such as lessons returned from searches or filtered views. </p>
 */
public class LessonLearnedList {

    private ArrayList<LessonLearned> lessons;

    /*****
     * <p> Method: LessonLearnedList() </p>
     * 
     * <p> Description: This method constructs the list. </p>
     * 
     */
    public LessonLearnedList() {
        lessons = new ArrayList<LessonLearned>();
    }

    /*****
     * <p> Method: addLessons() </p>
     * 
     * <p> Description: This method adds a lesson to the list. </p>
     * 
     */
    public void addLesson(LessonLearned lesson) {
        lessons.add(lesson);
    }

    /*****
     * <p> Method: removeLessons() </p>
     * 
     * <p> Description: This method removes a lesson in the list. </p>
     * 
     */
    public void removeLesson(LessonLearned lesson) {
        lessons.remove(lesson);
    }

    /*****
     * <p> Method: LessonLearned getLessons() </p>
     * 
     * <p> Description: This getter returns a specific lesson. </p>
     * 
     */
    public LessonLearned getLesson(int index) {
        return lessons.get(index);
    }

    /*****
     * <p> Method: ArrayList getLessons() </p>
     * 
     * <p> Description: This getter returns the lessons currently stored in the list. </p>
     * 
     */
    public ArrayList<LessonLearned> getLessons() {
        return lessons;
    }

    /*****
     * <p> Method: int getSize() </p>
     * 
     * <p> Description: This getter returns the list's size. </p>
     * 
     */
    public int getSize() {
        return lessons.size();
    }
}
