package guiViewer;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;

/**
 * Model for retrieving and preparing lessons for the Viewer page.
 *
 * @author Alexis
 * @version 1.00 2026-11-11 
 */
public class ModelViewerLessons {

    /** Message displayed when no lessons are available. */
    public static final String EMPTY_MESSAGE = "No lessons found.";

    /**
     * Retrieves all lessons from the database.
     *
     * @param db : the database connection
     * @return all lessons in database order
     * @throws IllegalArgumentException if db is null
     */
    public static LessonsLearnedList lessonsFor(Database db) {
        if (db == null) {
            throw new IllegalArgumentException("Database must not be null.");
        }

        // Viewers may read every lesson regardless of who wrote it, so no owner filter
        // is applied here (unlike the Contributor's My Lessons page).
        return db.getAllLessons();
    }

    /**
     * Converts a lesson into the text displayed in the Viewer list.
     *
     * @param lesson : the lesson to format
     * @return the lesson title and category
     * @throws IllegalArgumentException if lesson is null
     */
    public static String rowText(LessonsLearned lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson must not be null.");
        }

        // Show only the title and category. The body is withheld until the Viewer opens
        // the lesson, which keeps the list short and scannable.
        return lesson.getTitle() + "  (" + lesson.getCategory() + ")";
    }

    /**
     * Creates one display row for every lesson in the collection.
     *
     * @param lessons : the collection of lessons
     * @return formatted lesson rows in the original order
     * @throws IllegalArgumentException if lessons is null
     */
    public static List<String> rowsFor(LessonsLearnedList lessons) {
        if (lessons == null) {
            throw new IllegalArgumentException(
                    "Lesson collection must not be null.");
        }

        List<String> rows = new ArrayList<>();

        // Create one display row for each lesson without changing the source list.
        // The loop preserves database order so the row index matches the lesson index,
        // which the later "open a lesson" feature relies on.
        for (LessonsLearned lesson : lessons.getLessons()) {
            rows.add(rowText(lesson));
        }

        return rows;
    }
}