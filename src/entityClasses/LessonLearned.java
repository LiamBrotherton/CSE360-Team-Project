package entityClasses;

/*******
 * <p> Title: LessonLearned Class </p>
 *
 * <p> Description: This class represents a lesson entity in the system.
 * It contains lesson details such as lesson names, descriptions, and
 * their creator. It also provides validation for lesson input. </p>
 *
 */

public class LessonLearned {

    public static final int MAX_TITLE_LENGTH = 255;
    public static final int MAX_DESCRIPTION_LENGTH = 5000;

    private int lessonID;
    private String title;
    private String description;
    private String creatorUsername;


    /*****
     * <p> Method: LessonLearned() </p>
     *
     * <p> Description: Default constructor. </p>
     */
    public LessonLearned() {

    }


    /*****
     * <p> Method: LessonLearned(int lessonID, String title,
     * String description, String creatorUsername) </p>
     *
     * <p> Description: Constructor used to establish LessonLearned
     * entity objects. </p>
     *
     * @param lessonID specifies which lesson is being referenced
     * @param title specifies the lesson's title
     * @param description specifies the lesson's description
     * @param creatorUsername specifies who made the lesson
     */
    public LessonLearned(int lessonID, String title,
            String description, String creatorUsername) {

        this.lessonID = lessonID;
        this.title = title;
        this.description = description;
        this.creatorUsername = creatorUsername;
    }


    /*****
     * <p> Method: validate() </p>
     *
     * <p> Description: Validates the lesson's required input fields
     * and maximum field lengths. The returned message is intended to
     * provide a useful explanation of why the input is invalid. </p>
     *
     * @return an empty string when the lesson is valid; otherwise,
     *         a context-specific validation error message.
     */
    public String validate() {

        if (title == null || title.trim().length() == 0) {
            return "Lesson title is required. Please enter a title.";
        }

        if (title.length() > MAX_TITLE_LENGTH) {
            return "Lesson title must be "
                    + MAX_TITLE_LENGTH
                    + " characters or fewer.";
        }

        if (description == null || description.trim().length() == 0) {
            return "Lesson description is required. Please enter a description.";
        }

        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            return "Lesson description must be "
                    + MAX_DESCRIPTION_LENGTH
                    + " characters or fewer.";
        }

        return "";
    }


    /*****
     * <p> Method: isValid() </p>
     *
     * <p> Description: Determines whether this lesson contains valid
     * input. </p>
     *
     * @return true if the lesson is valid; false otherwise.
     */
    public boolean isValid() {
        return validate().isEmpty();
    }


    /*****
     * <p> Method: int getLessonID() </p>
     *
     * @return the lesson ID
     */
    public int getLessonID() {
        return lessonID;
    }


    /*****
     * <p> Method: void setLessonID(int lessonID) </p>
     *
     * @param lessonID is the ID number to assign
     */
    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }


    /*****
     * <p> Method: String getTitle() </p>
     *
     * @return the lesson title
     */
    public String getTitle() {
        return title;
    }


    /*****
     * <p> Method: void setTitle(String title) </p>
     *
     * @param title is the title to assign to the lesson
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /*****
     * <p> Method: String getDescription() </p>
     *
     * @return the lesson description
     */
    public String getDescription() {
        return description;
    }


    /*****
     * <p> Method: void setDescription(String description) </p>
     *
     * @param description is the description to assign to the lesson
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /*****
     * <p> Method: String getCreatorUsername() </p>
     *
     * @return the creator's username
     */
    public String getCreatorUsername() {
        return creatorUsername;
    }


    /*****
     * <p> Method: void setCreatorUsername(String creatorUsername) </p>
     *
     * @param creatorUsername is the creator's username
     */
    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }
}
