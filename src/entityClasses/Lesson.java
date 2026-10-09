package entityClasses;

/*******
 * <p> Title: Lesson Class </p>
 *
 * <p> Description: This Lesson class represents a lesson learned entity in the system.  It holds
 * the "core information" a Contributor supplies when adding a lesson: a title, a description of
 * the situation that gave rise to the lesson, the lesson itself, a category, optional search
 * tags, the username of the Contributor who added it, and when it was added.
 *
 * The attributes were chosen by looking at what a question-and-answer thread on Ed Discussion
 * carries (a title, a body, a category, tags, an author, and a date) and then splitting the
 * single free-text body in two.  A lesson learned is only useful to a later reader when it says
 * both what happened and what to do about it, and a single "body" field lets an author supply
 * one without the other.
 *
 * This is a plain entity object, like the User class.  It does no validation and has no database
 * knowledge.  The rules about what a legal value looks like live in
 * inputRecognizer.LessonRecognizer, and persistence lives in database.Database.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-04 Initial version
 */
public class Lesson {

	/**
	 * The categories a lesson may be filed under.  The list is fixed, rather than free text, so a
	 * later search or filter by category can rely on a small, known set of values.
	 */
	public static final String[] CATEGORIES = { "Requirements", "Design", "Implementation",
			"Testing", "Project Management", "Teamwork", "Other" };

	/*
	 * These are the private attributes for this entity object
	 */
	private int id;					// Assigned by the database; 0 until the lesson has been stored
	private String title;
	private String situation;		// What happened.  The context that gave rise to the lesson
	private String lessonText;		// What was learned, and what to do differently next time
	private String category;
	private String tags;			// Comma separated; may be empty
	private String author;			// The username of the Contributor who added the lesson
	private long createdAt;			// Milliseconds since the epoch; 0 until the lesson is stored

	/*****
	 * <p> Method: Lesson() </p>
	 *
	 * <p> Description: This default constructor is not used in this system.  It exists so the
	 * class follows the same pattern as User. </p>
	 */
	public Lesson() {
	}

	/*****
	 * <p> Method: Lesson(String title, String situation, String lessonText, String category,
	 * String tags, String author) </p>
	 *
	 * <p> Description: This constructor builds a lesson that has not yet been stored.  The id and
	 * the creation time are left unset because the database assigns both when the lesson is
	 * created. </p>
	 *
	 * @param title specifies the short title of the lesson
	 *
	 * @param situation specifies what happened, the context that gave rise to the lesson
	 *
	 * @param lessonText specifies what was learned
	 *
	 * @param category specifies the category, which must be one of CATEGORIES
	 *
	 * @param tags specifies the comma separated search tags, which may be empty
	 *
	 * @param author specifies the username of the Contributor adding the lesson
	 */
	public Lesson(String title, String situation, String lessonText, String category,
			String tags, String author) {
		this.title = title;
		this.situation = situation;
		this.lessonText = lessonText;
		this.category = category;
		this.tags = tags;
		this.author = author;
	}

	/*****
	 * <p> Method: Lesson(int id, String title, String situation, String lessonText,
	 * String category, String tags, String author, long createdAt) </p>
	 *
	 * <p> Description: This constructor builds a lesson read back from the database, so it
	 * carries the id and creation time the database assigned. </p>
	 *
	 * @param id specifies the id the database assigned to this lesson
	 *
	 * @param title specifies the short title of the lesson
	 *
	 * @param situation specifies what happened, the context that gave rise to the lesson
	 *
	 * @param lessonText specifies what was learned
	 *
	 * @param category specifies the category
	 *
	 * @param tags specifies the comma separated search tags, which may be empty
	 *
	 * @param author specifies the username of the Contributor who added the lesson
	 *
	 * @param createdAt specifies when the lesson was added, in milliseconds since the epoch
	 */
	public Lesson(int id, String title, String situation, String lessonText, String category,
			String tags, String author, long createdAt) {
		this(title, situation, lessonText, category, tags, author);
		this.id = id;
		this.createdAt = createdAt;
	}

	/*****
	 * <p> Method: int getId() </p>
	 *
	 * <p> Description: Get this lesson's database id.</p>
	 *
	 * @return the id, or 0 if the lesson has not been stored
	 */
	public int getId() { return id; }

	/*****
	 * <p> Method: String getTitle() </p>
	 *
	 * <p> Description: Get this lesson's title.</p>
	 *
	 * @return the title
	 */
	public String getTitle() { return title; }

	/*****
	 * <p> Method: String getSituation() </p>
	 *
	 * <p> Description: Get the description of the situation that gave rise to this lesson.</p>
	 *
	 * @return the situation text
	 */
	public String getSituation() { return situation; }

	/*****
	 * <p> Method: String getLessonText() </p>
	 *
	 * <p> Description: Get the text of what was learned.</p>
	 *
	 * @return the lesson text
	 */
	public String getLessonText() { return lessonText; }

	/*****
	 * <p> Method: String getCategory() </p>
	 *
	 * <p> Description: Get this lesson's category.</p>
	 *
	 * @return the category
	 */
	public String getCategory() { return category; }

	/*****
	 * <p> Method: String getTags() </p>
	 *
	 * <p> Description: Get this lesson's comma separated search tags.</p>
	 *
	 * @return the tags, which may be empty
	 */
	public String getTags() { return tags; }

	/*****
	 * <p> Method: String getAuthor() </p>
	 *
	 * <p> Description: Get the username of the Contributor who added this lesson.</p>
	 *
	 * @return the author's username
	 */
	public String getAuthor() { return author; }

	/*****
	 * <p> Method: long getCreatedAt() </p>
	 *
	 * <p> Description: Get when this lesson was added.</p>
	 *
	 * @return milliseconds since the epoch, or 0 if the lesson has not been stored
	 */
	public long getCreatedAt() { return createdAt; }

	/*****
	 * <p> Method: void setId(int id) </p>
	 *
	 * <p> Description: Set this lesson's database id.</p>
	 *
	 * @param id specifies the new id
	 */
	public void setId(int id) { this.id = id; }

	/*****
	 * <p> Method: void setTitle(String title) </p>
	 *
	 * <p> Description: Set this lesson's title.</p>
	 *
	 * @param title specifies the new title
	 */
	public void setTitle(String title) { this.title = title; }

	/*****
	 * <p> Method: void setSituation(String situation) </p>
	 *
	 * <p> Description: Set the description of the situation that gave rise to this lesson.</p>
	 *
	 * @param situation specifies the new situation text
	 */
	public void setSituation(String situation) { this.situation = situation; }

	/*****
	 * <p> Method: void setLessonText(String lessonText) </p>
	 *
	 * <p> Description: Set the text of what was learned.</p>
	 *
	 * @param lessonText specifies the new lesson text
	 */
	public void setLessonText(String lessonText) { this.lessonText = lessonText; }

	/*****
	 * <p> Method: void setCategory(String category) </p>
	 *
	 * <p> Description: Set this lesson's category.</p>
	 *
	 * @param category specifies the new category
	 */
	public void setCategory(String category) { this.category = category; }

	/*****
	 * <p> Method: void setTags(String tags) </p>
	 *
	 * <p> Description: Set this lesson's comma separated search tags.</p>
	 *
	 * @param tags specifies the new tags
	 */
	public void setTags(String tags) { this.tags = tags; }
}
