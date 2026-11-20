package guiCurator;

import database.Database;
import entityClasses.User;

/**
 * Curator locking prototype for CSE 360 HW3.
 *
 * <p>A curator can lock or unlock a lesson's title, body, or category.
 * The model validates the requested field and the active user's role before
 * asking the shared Database class to change the stored lock flag.</p>
 *
 * <p>This is a prototype, not the final security boundary: the existing
 * Database.setLessonFieldLocked method can also be called directly. For a
 * production implementation, role validation should be enforced at the
 * database/service boundary using the authenticated session.</p>
 *
 * @author Hassan Mohamed
 * @version 1.0 2026-11-17
 */
public final class ModelCuratorLock {

    /** Returned when the user does not have the curator role. */
    public static final String ACCESS_DENIED = "Only a curator can change lesson locks.";
    /** Returned when a lesson ID does not identify an existing lesson. */
    public static final String LESSON_NOT_FOUND = "The lesson was not found.";
    /** Returned when the requested field is not one of the three core fields. */
    public static final String INVALID_FIELD = "Choose title, body, or category.";
    /** Returned when the database cannot save a change. */
    public static final String SAVE_FAILED = "The lock change could not be saved.";

    private ModelCuratorLock() {
        // Utility/model class: callers should use the public static methods.
    }

    /**
     * Changes the lock for a single core field.
     *
     * @param db connected lessons database
     * @param actor currently logged-in user
     * @param lessonId database ID of the selected lesson
     * @param field title, body, or category
     * @param locked true to lock; false to unlock
     * @return empty string on success; otherwise a user-readable error
     */
    public static String setFieldLock(Database db, User actor, int lessonId,
            String field, boolean locked) {
        if (actor == null || !actor.getCuratorRole()) return ACCESS_DENIED;
        if (db == null || lessonId < 1 || !db.lessonExists(lessonId)) return LESSON_NOT_FOUND;
        if (!isCoreField(field)) return INVALID_FIELD;

        // Why: use the existing validated field-to-column mapping instead of
        // accepting arbitrary SQL column names from a user interface.
        return db.setLessonFieldLocked(lessonId, field, locked) ? "" : SAVE_FAILED;
    }

    /**
     * Locks or unlocks all three editable core fields of one lesson.
     *
     * <p>This models an entire core-lesson lock for the HW3 prototype. It is
     * not yet an atomic database transaction and does not cover experience
     * data; those are future integration requirements.</p>
     *
     * @param db connected lessons database
     * @param actor currently logged-in user
     * @param lessonId database ID of the selected lesson
     * @param locked true to lock all three core fields; false to unlock them
     * @return empty string on success; otherwise a user-readable error
     */
    public static String setAllCoreFieldsLocked(Database db, User actor,
            int lessonId, boolean locked) {
        if (actor == null || !actor.getCuratorRole()) return ACCESS_DENIED;
        if (db == null || lessonId < 1 || !db.lessonExists(lessonId)) return LESSON_NOT_FOUND;

        // Why: the original TP2 database has three separate lock flags. This
        // prototype updates each one without changing the team's schema.
        if (!db.setLessonFieldLocked(lessonId, "title", locked)) return SAVE_FAILED;
        if (!db.setLessonFieldLocked(lessonId, "body", locked)) return SAVE_FAILED;
        if (!db.setLessonFieldLocked(lessonId, "category", locked)) return SAVE_FAILED;
        return "";
    }

    /**
     * Determines whether a name identifies a lockable core field.
     *
     * @param field candidate field name
     * @return true only for title, body, or category
     */
    public static boolean isCoreField(String field) {
        return "title".equals(field) || "body".equals(field) || "category".equals(field);
    }
}
