package guiAddExperience;

import database.Database;
import entityClasses.LessonsLearned;
import inputRecognizer.LessonValidator;

/**
 * <p>Title: ModelAddExperience Class.</p>
 * <p>Contributor Story 3: checks experience input, restricts access to the
 * contributor who created a lesson, and reads/saves the experience fields.</p>
 * <p>The database currently stores one experience record per lesson. Saving
 * another attempt replaces the previous record; callers must warn the user.</p>
 *
 * @author Hassan Mohamed
 * @version 1.10 2026-10-20 Integrate with Dhruv's author-aware lessons
 */
public class ModelAddExperience {

    /** Generic denial: never reveal whether another contributor's ID exists. */
    public static final String NOT_AVAILABLE = "That lesson is not available to your account.";
    public static final String SAVE_FAILED = "The experience information could not be saved.";

    private ModelAddExperience() { }

    /**
     * Checks lesson ID and the four required fields using the shared rules.
     *
     * @return empty string if valid, otherwise the first validation message
     */
    public static String validate(String lessonId, String whatWasDone,
            String howItWasDone, String duration, String effort) {
        String error = LessonValidator.checkLessonIdText(lessonId);
        if (error.isEmpty()) error = LessonValidator.checkWhatWasDone(whatWasDone);
        if (error.isEmpty()) error = LessonValidator.checkHowItWasDone(howItWasDone);
        if (error.isEmpty()) error = LessonValidator.checkDuration(duration);
        if (error.isEmpty()) error = LessonValidator.checkEffort(effort);
        return error;
    }

    /**
     * Gets the lesson only when it belongs to the specified contributor.
     * Uses the same access rule as Dhruv's My Lessons and Lesson Details pages.
     *
     * @param db the Foundations database
     * @param username signed-in contributor's username
     * @param lessonId database lesson ID
     * @return the lesson, or null if absent or not owned by this contributor
     */
    public static LessonsLearned lessonFor(Database db, String username, int lessonId) {
        if (db == null || username == null || username.isBlank() || lessonId < 1) return null;
        LessonsLearned lesson = db.getLessonById(lessonId);
        if (lesson == null || !username.equals(lesson.getAddedBy())) return null;
        return lesson;
    }

    /**
     * Validates and saves experience for this contributor's own lesson.
     *
     * @param db database containing the lesson
     * @param username logged-in contributor's username
     * @param lessonId ID text supplied by the form
     * @param whatWasDone activity completed
     * @param howItWasDone approach taken
     * @param duration time spent
     * @param effort effort used
     * @return empty string on success, otherwise an error message
     */
    public static String save(Database db, String username, String lessonId,
            String whatWasDone, String howItWasDone, String duration, String effort) {
        String error = validate(lessonId, whatWasDone, howItWasDone, duration, effort);
        if (!error.isEmpty()) return error;

        int id = Integer.parseInt(lessonId.trim());
        if (lessonFor(db, username, id) == null) return NOT_AVAILABLE;

        boolean saved = db.addExperienceInformation(id, whatWasDone.trim(),
                howItWasDone.trim(), duration.trim(), effort.trim());
        return saved ? "" : SAVE_FAILED;
    }
}
