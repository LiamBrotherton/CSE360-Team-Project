package modifiedGuiContributor;

import java.util.ArrayList;
import java.util.List;
import entityClasses.Lesson;

/*******
 * <p> Title: ModifiedModelContributorHome Class. </p>
 *   
 * <p> Description: The Modified Contributor Home Page Model. Handles data operations 
 * and state management for lessons assigned to the current contributor. </p>
 *   
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *   
 * @author Lynn Robert Carter
 *   
 * @version 1.02        2026-10-20 Modified architecture for complete requirements execution
 *    
 */
public class ModifiedModelContributorHome {

    private static List<Lesson> contributorLessons = new ArrayList<>();
    private static Lesson selectedLesson;

    /**
     * Default constructor.
     */
    public ModifiedModelContributorHome() {
    }

    /**
     * Fetches all lessons authored by the specific contributor from the system database.
     */
    public static void loadLessons(String contributorName) {
        // Pull lessons out of the shared application database using the context instance
        contributorLessons = applicationMain.FoundationsMain.database.getLessonsByContributor(contributorName);
    }

    public static List<Lesson> getContributorLessons() {
        return contributorLessons;
    }

    public static void setSelectedLesson(Lesson lesson) {
        selectedLesson = lesson;
    }

    public static Lesson getSelectedLesson() {
        return selectedLesson;
    }

    /**
     * Deletes the currently selected lesson if it does not contain any locked fields.
     * @return true if successfully processed/removed, false if deletion is structurally blocked.
     */
    public static boolean deleteSelectedLesson() {
        if (selectedLesson != null && !selectedLesson.hasLockedField()) {
            applicationMain.FoundationsMain.database.deleteLesson(selectedLesson.getId());
            contributorLessons.remove(selectedLesson);
            selectedLesson = null;
            return true;
        }
        return false; 
    }
}