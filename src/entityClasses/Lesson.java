package entityClasses;

/*******
 * <p> Title: Lesson Class </p>
 * 
 * <p> Description: This Lesson class represents a lesson learned entity in the system.  It 
 *  contains the core information (title, body, category), a locked flag for each core field, 
 *  and the optional experience information (what was done, how it was done, duration, and 
 *  effort). </p>
 * 
 */ 

public class Lesson {
	
	/*
	 * These are the names of the fields that can be locked.  They are here, with the entity,
	 * because both the Database class and the LessonValidator class refer to them.
	 */
    /** Field name for the title. */
    public static final String FIELD_TITLE = "title";
    /** Field name for the body. */
    public static final String FIELD_BODY = "body";
    /** Field name for the category. */
    public static final String FIELD_CATEGORY = "category";
    
    
	/*
	 * These are the private attributes for this entity object
	 */
    private int id;
    private String title;
    private String body;
    private String category;
    private boolean titleLocked;
    private boolean bodyLocked;
    private boolean categoryLocked;
    
    // The experience attributes are all null until experience information has been added.
    private String whatWasDone;
    private String howItWasDone;
    private String duration;
    private String effort;
    
    
    /*****
     * <p> Method: Lesson(int id, String title, String body, String category, 
     * 		boolean titleLocked, boolean bodyLocked, boolean categoryLocked, 
     * 		String whatWasDone, String howItWasDone, String duration, String effort) </p>
     * 
     * <p> Description: This constructor is used to establish lesson entity objects. </p>
     * 
     * @param id specifies the database-assigned identifier for this lesson
     * 
     * @param title specifies the title for this lesson
     * 
     * @param body specifies the body for this lesson
     * 
     * @param category specifies the category for this lesson
     * 
     * @param titleLocked specifies if the title is locked (TRUE or FALSE)
     * 
     * @param bodyLocked specifies if the body is locked (TRUE or FALSE)
     * 
     * @param categoryLocked specifies if the category is locked (TRUE or FALSE)
     * 
     * @param whatWasDone specifies the experience information of what was done, or null
     * 
     * @param howItWasDone specifies the experience information of how it was done, or null
     * 
     * @param duration specifies the experience information of how long it took, or null
     * 
     * @param effort specifies the experience information of the effort used, or null
     * 
     */
    // Constructor to initialize a new Lesson object with all of its stored attributes.
    public Lesson(int id, String title, String body, String category, boolean titleLocked,
    		boolean bodyLocked, boolean categoryLocked, String whatWasDone, String howItWasDone,
    		String duration, String effort) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.category = category;
        this.titleLocked = titleLocked;
        this.bodyLocked = bodyLocked;
        this.categoryLocked = categoryLocked;
        this.whatWasDone = whatWasDone;
        this.howItWasDone = howItWasDone;
        this.duration = duration;
        this.effort = effort;
    }

    
    /*****
     * <p> Method: int getId() </p>
     * 
     * <p> Description: This getter returns the Id. </p>
     * 
     * @return an int of the Id
     * 
     */
    // Gets the current value of the Id.
    public int getId() { return id; }

    
    /*****
     * <p> Method: String getTitle() </p>
     * 
     * <p> Description: This getter returns the Title. </p>
     * 
     * @return a String of the Title
     * 
     */
    // Gets the current value of the Title.
    public String getTitle() { return title; }

    
    /*****
     * <p> Method: String getBody() </p>
     * 
     * <p> Description: This getter returns the Body. </p>
     * 
     * @return a String of the Body
     * 
     */
    // Gets the current value of the Body.
    public String getBody() { return body; }

    
    /*****
     * <p> Method: String getCategory() </p>
     * 
     * <p> Description: This getter returns the Category. </p>
     * 
     * @return a String of the Category
     * 
     */
    // Gets the current value of the Category.
    public String getCategory() { return category; }

    
    /*****
     * <p> Method: boolean getTitleLocked() </p>
     * 
     * <p> Description: This getter returns the value of the Title locked attribute. </p>
     * 
     * @return a boolean that is true if the title is locked
     * 
     */
    // Gets the current value of the Title locked attribute.
    public boolean getTitleLocked() { return titleLocked; }

    
    /*****
     * <p> Method: boolean getBodyLocked() </p>
     * 
     * <p> Description: This getter returns the value of the Body locked attribute. </p>
     * 
     * @return a boolean that is true if the body is locked
     * 
     */
    // Gets the current value of the Body locked attribute.
    public boolean getBodyLocked() { return bodyLocked; }

    
    /*****
     * <p> Method: boolean getCategoryLocked() </p>
     * 
     * <p> Description: This getter returns the value of the Category locked attribute. </p>
     * 
     * @return a boolean that is true if the category is locked
     * 
     */
    // Gets the current value of the Category locked attribute.
    public boolean getCategoryLocked() { return categoryLocked; }

    
    /*****
     * <p> Method: boolean hasLockedField() </p>
     * 
     * <p> Description: This method reports if any core field is locked.  A lesson with any 
     * locked field cannot be deleted. </p>
     * 
     * @return a boolean that is true if the title, body, or category is locked
     * 
     */
    // Checks the three locked attributes.
    public boolean hasLockedField() { return titleLocked || bodyLocked || categoryLocked; }

    
    /*****
     * <p> Method: boolean hasExperienceInformation() </p>
     * 
     * <p> Description: This method reports if experience information has been added. </p>
     * 
     * @return a boolean that is true if experience information has been added
     * 
     */
    // Experience information is all added together, so checking one attribute is enough.
    public boolean hasExperienceInformation() { return whatWasDone != null; }

    
    /*****
     * <p> Method: String getWhatWasDone() </p>
     * 
     * <p> Description: This getter returns the WhatWasDone experience information. </p>
     * 
     * @return a String of the WhatWasDone, or null if there is no experience information
     * 
     */
    // Gets the current value of the WhatWasDone.
    public String getWhatWasDone() { return whatWasDone; }

    
    /*****
     * <p> Method: String getHowItWasDone() </p>
     * 
     * <p> Description: This getter returns the HowItWasDone experience information. </p>
     * 
     * @return a String of the HowItWasDone, or null if there is no experience information
     * 
     */
    // Gets the current value of the HowItWasDone.
    public String getHowItWasDone() { return howItWasDone; }

    
    /*****
     * <p> Method: String getDuration() </p>
     * 
     * <p> Description: This getter returns the Duration experience information. </p>
     * 
     * @return a String of the Duration, or null if there is no experience information
     * 
     */
    // Gets the current value of the Duration.
    public String getDuration() { return duration; }

    
    /*****
     * <p> Method: String getEffort() </p>
     * 
     * <p> Description: This getter returns the Effort experience information. </p>
     * 
     * @return a String of the Effort, or null if there is no experience information
     * 
     */
    // Gets the current value of the Effort.
    public String getEffort() { return effort; }
    
    
    /*****
     * <p> Method: void setTitle(String title) </p>
     * 
     * <p> Description: This setter defines the Title attribute. </p>
     * 
     * @param title specifies the new value of the Title
     * 
     */
    // Sets the Title.
    public void setTitle(String title) {
    	this.title = title;
    }

    
    /*****
     * <p> Method: void setBody(String body) </p>
     * 
     * <p> Description: This setter defines the Body attribute. </p>
     * 
     * @param body specifies the new value of the Body
     * 
     */
    // Sets the Body.
    public void setBody(String body) {
    	this.body = body;
    }

    
    /*****
     * <p> Method: void setCategory(String category) </p>
     * 
     * <p> Description: This setter defines the Category attribute. </p>
     * 
     * @param category specifies the new value of the Category
     * 
     */
    // Sets the Category.
    public void setCategory(String category) {
    	this.category = category;
    }

    
    /*****
     * <p> Method: void setTitleLocked(boolean titleLocked) </p>
     * 
     * <p> Description: This setter defines the Title locked attribute. </p>
     * 
     * @param titleLocked specifies the new value of the Title locked
     * 
     */
    // Sets the Title locked.
    public void setTitleLocked(boolean titleLocked) {
    	this.titleLocked = titleLocked;
    }

    
    /*****
     * <p> Method: void setBodyLocked(boolean bodyLocked) </p>
     * 
     * <p> Description: This setter defines the Body locked attribute. </p>
     * 
     * @param bodyLocked specifies the new value of the Body locked
     * 
     */
    // Sets the Body locked.
    public void setBodyLocked(boolean bodyLocked) {
    	this.bodyLocked = bodyLocked;
    }

    
    /*****
     * <p> Method: void setCategoryLocked(boolean categoryLocked) </p>
     * 
     * <p> Description: This setter defines the Category locked attribute. </p>
     * 
     * @param categoryLocked specifies the new value of the Category locked
     * 
     */
    // Sets the Category locked.
    public void setCategoryLocked(boolean categoryLocked) {
    	this.categoryLocked = categoryLocked;
    }

    
    /*****
     * <p> Method: void setWhatWasDone(String whatWasDone) </p>
     * 
     * <p> Description: This setter defines the WhatWasDone experience information attribute. </p>
     * 
     * @param whatWasDone specifies the new value of the WhatWasDone experience information
     * 
     */
    // Sets the WhatWasDone experience information.
    public void setWhatWasDone(String whatWasDone) {
    	this.whatWasDone = whatWasDone;
    }

    
    /*****
     * <p> Method: void setHowItWasDone(String howItWasDone) </p>
     * 
     * <p> Description: This setter defines the HowItWasDone experience information attribute. </p>
     * 
     * @param howItWasDone specifies the new value of the HowItWasDone experience information
     * 
     */
    // Sets the HowItWasDone experience information.
    public void setHowItWasDone(String howItWasDone) {
    	this.howItWasDone = howItWasDone;
    }

    
    /*****
     * <p> Method: void setDuration(String duration) </p>
     * 
     * <p> Description: This setter defines the Duration experience information attribute. </p>
     * 
     * @param duration specifies the new value of the Duration experience information
     * 
     */
    // Sets the Duration experience information.
    public void setDuration(String duration) {
    	this.duration = duration;
    }

    
    /*****
     * <p> Method: void setEffort(String effort) </p>
     * 
     * <p> Description: This setter defines the Effort experience information attribute. </p>
     * 
     * @param effort specifies the new value of the Effort experience information
     * 
     */
    // Sets the Effort experience information.
    public void setEffort(String effort) {
    	this.effort = effort;
    }

    
    /*****
     * <p> Method: String toString() </p>
     * 
     * <p> Description: This method describes every attribute of this lesson, locked flags and 
     * experience information included, so one lesson can be examined closely. </p>
     * 
     * @return a multi-line String describing this lesson
     * 
     */
    // Builds the description one line at a time.
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("Lesson ").append(id).append("\n");
    	sb.append("  Title:    ").append(title).append(titleLocked ? "  [locked]" : "").append("\n");
    	sb.append("  Body:     ").append(body).append(bodyLocked ? "  [locked]" : "").append("\n");
    	sb.append("  Category: ").append(category).append(categoryLocked ? "  [locked]" : "");
    	if (hasExperienceInformation()) {
    		sb.append("\n  What was done:   ").append(whatWasDone);
    		sb.append("\n  How it was done: ").append(howItWasDone);
    		sb.append("\n  Duration: ").append(duration);
    		sb.append("\n  Effort:   ").append(effort);
    	} else {
    		sb.append("\n  (no experience information)");
    	}
    	return sb.toString();
    }
}
