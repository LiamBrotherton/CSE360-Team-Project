package inputRecognizer;

import entityClasses.Lesson;

/*******
 * <p> Title: LessonValidator Class </p>
 * 
 * <p> Description: This LessonValidator class holds every input validation rule for lessons 
 *  learned.  Each check returns an empty String when the value is acceptable, 
 *  and a descriptive error message that names the field and what is wrong.   </p>
 * 
 */ 

public class LessonValidator { 
	
	/*
	 * These are the limits.  They are public so the database table sizes and the automated tests 
	 * use the same values as the validation.
	 */
    /** Longest allowed title.  Must not exceed the lessonDB column size (255). */
    public static final int MAX_TITLE_LENGTH = 100;
    /** Longest allowed body. */
    public static final int MAX_BODY_LENGTH = 2000;
    /** Longest allowed category. */
    public static final int MAX_CATEGORY_LENGTH = 50;
    /** Longest allowed "what was done" or "how it was done". */
    public static final int MAX_EXPERIENCE_TEXT_LENGTH = 1000;
    /** Longest allowed duration text. */
    public static final int MAX_DURATION_LENGTH = 100;
    /** Longest allowed effort text. */
    public static final int MAX_EFFORT_LENGTH = 100;
    
    
    /*****
     * <p> Method: String checkTitle(String title) </p>
     * 
     * <p> Description: This method checks a title: not blank, and at most MAX_TITLE_LENGTH 
     * characters. </p>
     * 
     * @param title specifies the title to check
     * 
     * @return an empty String if the title is acceptable, else the error message
     * 
     */
    // Uses the shared text check with this field's name and limit.
    public static String checkTitle(String title) {
    	return checkText("Title", title, MAX_TITLE_LENGTH);
    }

    
    /*****
     * <p> Method: String checkBody(String body) </p>
     * 
     * <p> Description: This method checks a body: not blank, and at most MAX_BODY_LENGTH 
     * characters. </p>
     * 
     * @param body specifies the body to check
     * 
     * @return an empty String if the body is acceptable, else the error message
     * 
     */
    // Uses the shared text check with this field's name and limit.
    public static String checkBody(String body) {
    	return checkText("Body", body, MAX_BODY_LENGTH);
    }

    
    /*****
     * <p> Method: String checkCategory(String category) </p>
     * 
     * <p> Description: This method checks a category: not blank, and at most 
     * MAX_CATEGORY_LENGTH characters. </p>
     * 
     * @param category specifies the category to check
     * 
     * @return an empty String if the category is acceptable, else the error message
     * 
     */
    // Uses the shared text check with this field's name and limit.
    public static String checkCategory(String category) {
    	return checkText("Category", category, MAX_CATEGORY_LENGTH);
    }

    
    /*****
     * <p> Method: String checkWhatWasDone(String whatWasDone) </p>
     * 
     * <p> Description: This method checks the experience information of what was done: not 
     * blank, and at most MAX_EXPERIENCE_TEXT_LENGTH characters. </p>
     * 
     * @param whatWasDone specifies the text to check
     * 
     * @return an empty String if the text is acceptable, else the error message
     * 
     */
    // Uses the shared text check with this field's name and limit.
    public static String checkWhatWasDone(String whatWasDone) {
    	return checkText("What was done", whatWasDone, MAX_EXPERIENCE_TEXT_LENGTH);
    }

    
    /*****
     * <p> Method: String checkHowItWasDone(String howItWasDone) </p>
     * 
     * <p> Description: This method checks the experience information of how it was done: not 
     * blank, and at most MAX_EXPERIENCE_TEXT_LENGTH characters. </p>
     * 
     * @param howItWasDone specifies the text to check
     * 
     * @return an empty String if the text is acceptable, else the error message
     * 
     */
    // Uses the shared text check with this field's name and limit.
    public static String checkHowItWasDone(String howItWasDone) {
    	return checkText("How it was done", howItWasDone, MAX_EXPERIENCE_TEXT_LENGTH);
    }

    
    /*****
     * <p> Method: String checkDuration(String duration) </p>
     * 
     * <p> Description: This method checks a duration: not blank, and at most 
     * MAX_DURATION_LENGTH characters.  Duration is free text, such as "2 hours". </p>
     * 
     * @param duration specifies the duration text to check
     * 
     * @return an empty String if the duration is acceptable, else the error message
     * 
     */
    // Uses the shared text check with this field's name and limit.
    public static String checkDuration(String duration) {
    	return checkText("Duration", duration, MAX_DURATION_LENGTH);
    }

    
    /*****
     * <p> Method: String checkEffort(String effort) </p>
     * 
     * <p> Description: This method checks an effort: not blank, and at most MAX_EFFORT_LENGTH 
     * characters.  Effort is free text, such as "3 people". </p>
     * 
     * @param effort specifies the effort text to check
     * 
     * @return an empty String if the effort is acceptable, else the error message
     * 
     */
    // Uses the shared text check with this field's name and limit.
    public static String checkEffort(String effort) {
    	return checkText("Effort", effort, MAX_EFFORT_LENGTH);
    }

    
    /*****
     * <p> Method: String checkLessonIdText(String idText) </p>
     * 
     * <p> Description: This method checks a lesson ID as typed by a person: it must not be 
     * blank and must be a whole number of 1 or more.  Whether a lesson with that ID exists is 
     * checked separately by checkLessonFound. </p>
     * 
     * @param idText specifies the ID text to check
     * 
     * @return an empty String if the ID is acceptable, else the error message
     * 
     */
    // Blank is reported first, then non-numeric, then out of range.
    public static String checkLessonIdText(String idText) {
    	if (idText == null || idText.trim().isEmpty()) return "Lesson ID cannot be blank.";
    	int id;
    	try {
    		id = Integer.parseInt(idText.trim());
    	} catch (NumberFormatException e) {
    		return "Lesson ID must be a whole number; \"" + idText.trim() + "\" is not.";
    	}
    	if (id < 1) return "Lesson ID must be 1 or more.";
    	return "";
    }

    
    /*****
     * <p> Method: String checkLessonFound(Lesson lesson, int lessonId) </p>
     * 
     * <p> Description: This method checks that a lesson was found.  The caller passes the 
     * result of reading the database (null if there was no such lesson). </p>
     * 
     * @param lesson specifies the lesson read from the database, or null
     * 
     * @param lessonId specifies the ID that was looked up, used in the message
     * 
     * @return an empty String if the lesson is not null, else the error message
     * 
     */
    // The same wording is used for update, delete, add experience, and examine.
    public static String checkLessonFound(Lesson lesson, int lessonId) {
    	if (lesson == null) return "Lesson " + lessonId + " was not found.";
    	return "";
    }

    
    /*****
     * <p> Method: String checkFieldNotLocked(Lesson lesson, String field) </p>
     * 
     * <p> Description: This method checks that a core field may be updated.  A locked field 
     * cannot be altered. </p>
     * 
     * @param lesson specifies the lesson read from the database (not null)
     * 
     * @param field specifies Lesson.FIELD_TITLE, Lesson.FIELD_BODY, or Lesson.FIELD_CATEGORY
     * 
     * @return an empty String if the field is not locked, else the error message naming it
     * 
     */
    // An unknown field name is reported rather than treated as unlocked.
    public static String checkFieldNotLocked(Lesson lesson, String field) {
    	boolean locked;
    	if (Lesson.FIELD_TITLE.equals(field)) locked = lesson.getTitleLocked();
    	else if (Lesson.FIELD_BODY.equals(field)) locked = lesson.getBodyLocked();
    	else if (Lesson.FIELD_CATEGORY.equals(field)) locked = lesson.getCategoryLocked();
    	else return "\"" + field + "\" is not a lockable field.  Use title, body, or category.";
    	
    	if (locked) {
    		return "The " + field + " of lesson " + lesson.getId() 
    				+ " is locked and cannot be changed.";
    	}
    	return "";
    }

    
    /*****
     * <p> Method: String checkCanDelete(Lesson lesson) </p>
     * 
     * <p> Description: This method checks that a lesson may be deleted.  A lesson with any 
     * locked field cannot be deleted. </p>
     * 
     * @param lesson specifies the lesson read from the database (not null)
     * 
     * @return an empty String if the lesson may be deleted, else the error message listing 
     * the locked fields
     * 
     */
    // The message lists which fields are locked so the person knows who to ask.
    public static String checkCanDelete(Lesson lesson) {
    	if (!lesson.hasLockedField()) return "";
    	StringBuilder locked = new StringBuilder();
    	if (lesson.getTitleLocked()) locked.append("title, ");
    	if (lesson.getBodyLocked()) locked.append("body, ");
    	if (lesson.getCategoryLocked()) locked.append("category, ");
    	locked.setLength(locked.length() - 2);	// remove the final ", "
    	return "Lesson " + lesson.getId() + " is locked (" + locked + ") and cannot be deleted.";
    }

    
    /*
     * Returns an empty String if the text is acceptable, else a message naming the field and the 
     * problem.  Length is that of the text as given; nothing in this system trims what is stored.
     */
    private static String checkText(String label, String value, int maxLength) {
    	if (value == null || value.trim().isEmpty()) return label + " cannot be blank.";
    	if (value.length() > maxLength) {
    		return label + " is too long: " + value.length() + " characters, but the limit is " 
    				+ maxLength + ".";
    	}
    	return "";
    }
}
