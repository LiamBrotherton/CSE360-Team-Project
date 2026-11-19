package guiViewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;

/**
 * Model for retrieving, searching, and opening lessons for the Viewer page.
 *
 * @author Alexis
 * @version 2.0 2026-11-11 added the remaining viewer functionality
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

        // Viewers may read every lesson regardless of who wrote it, so no owner filter.
        return db.getAllLessons();
    }

    /**
     * Searches all lessons for a keyword in the title, category, or body.
     * Matching is not case-sensitive and uses partial-word matching for now.
     *
     * @param db : the database connection
     * @param keyword : the keyword to search for
     * @return lessons that contain the keyword
     * @throws IllegalArgumentException if db is null
     */
    public static LessonsLearnedList searchLessons(Database db, String keyword) {
        if (db == null) {
            throw new IllegalArgumentException("Database must not be null.");
        }

        return filterLessons(db.getAllLessons(), keyword);
    }
    
    /**
     * Filters an lessons using a keyword.
     *
     * @param lessons : the list to search
     * @param keyword : the keyword used to search
     * @return a new list containing matching lessons
     * @throws IllegalArgumentException if lessons is null
     */
    public static LessonsLearnedList filterLessons(
            LessonsLearnedList lessons, String keyword) {

        if (lessons == null) {
            throw new IllegalArgumentException("Lesson collection must not be null.");
        }

        // An empty search is treated as a request to show every lesson instead of crashing.
        if (keyword == null || keyword.trim().isEmpty()) {
            return lessons;
        }

        String searchTerm = keyword.trim().toLowerCase(Locale.ROOT);
        List<LessonsLearned> matches = new ArrayList<>();

        // Check the three core lesson fields. The same lesson is added only once
        for (LessonsLearned lesson : lessons.getLessons()) {
            if (containsKeyword(lesson.getTitle(), searchTerm) || containsKeyword(lesson.getCategory(), searchTerm) || containsKeyword(lesson.getBody(), searchTerm)) {

                matches.add(lesson);
            }
        }

        return new LessonsLearnedList(matches);
    }

    /**
     * Checks whether a field contains the search term.
     *
     * @param value : the field value (title, category, or body)
     * @param keyword : the (edited) search term
     * @return true if the field contains the term
     */
    private static boolean containsKeyword(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
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