package guiAddExperience;

import database.Database;
import entityClasses.LessonsLearned;
import inputRecognizer.LessonValidator;

/*******
 * <p> Title: ModelAddExperience Class. </p>
 *
 * <p> Description: Validates the fields used when a contributor adds experience
 * information to an existing lesson. This model reuses the same rules as the
 * Lessons Console and stores data through the existing Database class.</p>
 *
 * @author Hassan Mohamed
 * @version 1.00 2026-10-20 Initial version for TP2 Contributor Story 3
 */
public class ModelAddExperience {

    private ModelAddExperience() { }

    /*******
     * <p> Checks the lesson ID and all four required experience fields.</p>
     *
     * @param lessonId text entered for the lesson identifier
     * @param whatWasDone description of the work completed
     * @param howItWasDone description of the approach used
     * @param duration amount of time spent, expressed as text
     * @param effort amount of effort used, expressed as text
     * @return an empty String for valid input, or the first validation error
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

    /*******
     * <p> Validates and saves experience information to an existing lesson.</p>
     * <p> The existing database stores one set of experience information per
     * lesson. Repeating this operation replaces the previous set.</p>
     *
     * @param db the existing Foundations database
     * @param lessonId the lesson identifier entered as text
     * @param whatWasDone description of the work completed
     * @param howItWasDone description of the approach used
     * @param duration amount of time spent
     * @param effort effort used
     * @return an empty String when saved, otherwise an explanation of the failure
     */
    public static String save(Database db, String lessonId, String whatWasDone,
            String howItWasDone, String duration, String effort) {
        String error = validate(lessonId, whatWasDone, howItWasDone, duration, effort);
        if (!error.isEmpty()) return error;

        int id = Integer.parseInt(lessonId.trim());
        LessonsLearned lesson = db.getLessonById(id);
        error = LessonValidator.checkLessonFound(lesson, id);
        if (!error.isEmpty()) return error;

        // Keep the same data storage behavior as LessonConsole.addExperience().
        boolean saved = db.addExperienceInformation(id, whatWasDone, howItWasDone,
                duration.trim(), effort.trim());
        return saved ? "" : "The experience information could not be saved.";
    }
}
