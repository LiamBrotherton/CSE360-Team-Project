package entityClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*******
 * <p> Title: LessonList Class </p>
 * 
 * <p> Description: This LessonList class represents a collection of lessons in the system.  It 
 *  can hold every lesson in the database, or any subset of them such as the result of a search.  
 *  The list may be empty, may hold one lesson, and has no fixed upper limit. </p>
 * 
 */ 

public class LessonList {
	
	/*
	 * These are the attributes for this entity object
	 */
    
    /** The message given for an empty list. */
    public static final String NO_LESSONS_MESSAGE = "No lessons found.";
    
    private List<Lesson> lessons;
    
    
    /*****
     * <p> Method: LessonList() </p>
     * 
     * <p> Description: This default constructor establishes an empty list. </p>
     */
    public LessonList() {
    	lessons = new ArrayList<Lesson>();
    }

    
    /*****
     * <p> Method: LessonList(List&lt;Lesson&gt; lessons) </p>
     * 
     * <p> Description: This constructor is used to establish a list holding exactly the lessons 
     * given.  A null list is treated as empty. </p>
     * 
     * @param lessons specifies the lessons this list is to hold
     * 
     */
    // Constructor to initialize a new LessonList object with a copy of the lessons given.
    public LessonList(List<Lesson> lessons) {
    	this.lessons = (lessons == null) ? new ArrayList<Lesson>() : new ArrayList<Lesson>(lessons);
    }

    
    /*****
     * <p> Method: int size() </p>
     * 
     * <p> Description: This getter returns the number of lessons in this list. </p>
     * 
     * @return an int of the number of lessons
     * 
     */
    // Gets the current number of lessons.
    public int size() { return lessons.size(); }

    
    /*****
     * <p> Method: boolean isEmpty() </p>
     * 
     * <p> Description: This method reports if this list holds no lessons. </p>
     * 
     * @return a boolean that is true if the list is empty
     * 
     */
    // Checks the number of lessons.
    public boolean isEmpty() { return lessons.isEmpty(); }

    
    /*****
     * <p> Method: Lesson get(int index) </p>
     * 
     * <p> Description: This getter returns the lesson at a position in the list. </p>
     * 
     * @param index specifies the position in the list, starting at 0
     * 
     * @return the Lesson at that position
     * 
     * @throws IndexOutOfBoundsException if there is no lesson at that position
     * 
     */
    // Gets one lesson by position.
    public Lesson get(int index) { return lessons.get(index); }

    
    /*****
     * <p> Method: List&lt;Lesson&gt; getLessons() </p>
     * 
     * <p> Description: This getter returns a read-only view of the lessons, in order. </p>
     * 
     * @return a read-only List of the lessons
     * 
     */
    // Gets all of the lessons without letting the caller change this list.
    public List<Lesson> getLessons() { return Collections.unmodifiableList(lessons); }

    
    /*****
     * <p> Method: String getMessage() </p>
     * 
     * <p> Description: This getter returns the "no lessons found" message for an empty list, 
     * and an empty String for any other list. </p>
     * 
     * @return NO_LESSONS_MESSAGE if the list is empty, else an empty String
     * 
     */
    // Gets the message to show a person for this list.
    public String getMessage() { return lessons.isEmpty() ? NO_LESSONS_MESSAGE : ""; }
}
