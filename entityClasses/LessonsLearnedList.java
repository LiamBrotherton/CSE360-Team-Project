package entityClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*******
 * <p> Title: LessonsLearnedList Class </p>
 * 
 * <p> Description: This LessonsLearnedList class represents a collection of lessonsLearneds in the system.  It 
 *  can hold every lesson in the database, or any subset of them such as the result of a search.  
 *  The list may be empty, may hold one lesson, and has no fixed upper limit. </p>
 * 
 */ 

public class LessonsLearnedList {
	
	/*
	 * These are the attributes for this entity object
	 */
    
    /** The message given for an empty list. */
    public static final String NO_LESSONS_MESSAGE = "No lessons found.";
    
    private List<LessonsLearned> lessonsLearneds;
    
    
    /*****
     * <p> Method: LessonsLearnedList() </p>
     * 
     * <p> Description: This default constructor establishes an empty list. </p>
     */
    public LessonsLearnedList() {
    	lessonsLearneds = new ArrayList<LessonsLearned>();
    }

    
    /*****
     * <p> Method: LessonsLearnedList(List&lt;LessonsLearned&gt; lessonsLearneds) </p>
     * 
     * <p> Description: This constructor is used to establish a list holding exactly the lessonsLearneds 
     * given.  A null list is treated as empty. </p>
     * 
     * @param lessonsLearneds specifies the lessonsLearneds this list is to hold
     * 
     */
    // Constructor to initialize a new LessonsLearnedList object with a copy of the lessonsLearneds given.
    public LessonsLearnedList(List<LessonsLearned> lessonsLearneds) {
    	this.lessonsLearneds = (lessonsLearneds == null) ? new ArrayList<LessonsLearned>() : new ArrayList<LessonsLearned>(lessonsLearneds);
    }

    
    /*****
     * <p> Method: int size() </p>
     * 
     * <p> Description: This getter returns the number of lessonsLearneds in this list. </p>
     * 
     * @return an int of the number of lessonsLearneds
     * 
     */
    // Gets the current number of lessonsLearneds.
    public int size() { return lessonsLearneds.size(); }

    
    /*****
     * <p> Method: boolean isEmpty() </p>
     * 
     * <p> Description: This method reports if this list holds no lessonsLearneds. </p>
     * 
     * @return a boolean that is true if the list is empty
     * 
     */
    // Checks the number of lessonsLearneds.
    public boolean isEmpty() { return lessonsLearneds.isEmpty(); }

    
    /*****
     * <p> Method: LessonsLearned get(int index) </p>
     * 
     * <p> Description: This getter returns the lesson at a position in the list. </p>
     * 
     * @param index specifies the position in the list, starting at 0
     * 
     * @return the LessonsLearned at that position
     * 
     * @throws IndexOutOfBoundsException if there is no lesson at that position
     * 
     */
    // Gets one lesson by position.
    public LessonsLearned get(int index) { return lessonsLearneds.get(index); }

    
    /*****
     * <p> Method: List&lt;LessonsLearned&gt; getLessons() </p>
     * 
     * <p> Description: This getter returns a read-only view of the lessonsLearneds, in order. </p>
     * 
     * @return a read-only List of the lessonsLearneds
     * 
     */
    // Gets all of the lessonsLearneds without letting the caller change this list.
    public List<LessonsLearned> getLessons() { return Collections.unmodifiableList(lessonsLearneds); }

    
    /*****
     * <p> Method: String getMessage() </p>
     * 
     * <p> Description: This getter returns the "no lessonsLearneds found" message for an empty list, 
     * and an empty String for any other list. </p>
     * 
     * @return NO_LESSONS_MESSAGE if the list is empty, else an empty String
     * 
     */
    // Gets the message to show a person for this list.
    public String getMessage() { return lessonsLearneds.isEmpty() ? NO_LESSONS_MESSAGE : ""; }
}
