package testing;

import java.sql.SQLException;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.User;
import guiCurator.ModelCuratorLock;
import guiLessonDetail.ModelLessonDetail;

/**
 * Automated positive, negative, and boundary tests for the HW3 curator-lock epic.
 *
 * <p>Tests use the same ModelCuratorLock and ModelLessonDetail methods as the
 * application's Curator and Contributor screens. Each test inserts one temporary
 * lesson into the team's H2 database and removes only that lesson afterward.
 * Existing accounts and lessons are never reset.</p>
 *
 * <p>Run as a Java Application in Eclipse. A PASS means every assertion in that
 * test matched the expected result. Any failed assertion is printed and causes
 * a nonzero exit code.</p>
 *
 * @author Hassan Mohamed
 * @version 1.1 2026-11-18
 */
public final class CuratorLockTestingAutomation {
    private static final String TEST_OWNER = "HW3CuratorTestOwner";
    private static Database db;
    private static int passed;
    private static int failed;

    private static final User CURATOR = makeUser(true);
    private static final User CONTRIBUTOR = makeUser(false);

    private CuratorLockTestingAutomation() { }

    /** Runs all curator lock and contributor enforcement tests. @param args unused */
    public static void main(String[] args) {
        System.out.println("=== HW3 CURATOR LOCK TESTS ===");
        try {
            db = new Database();
            db.connectToDatabase();

            run("Curator locks title", id -> {
                equal("", lock(id, LessonsLearned.FIELD_TITLE, true), "lock accepted");
                check(db.getLessonById(id).getTitleLocked(), "title lock saved in DB");
            });

            run("Curator locks body and category", id -> {
                equal("", lock(id, LessonsLearned.FIELD_BODY, true), "body lock accepted");
                equal("", lock(id, LessonsLearned.FIELD_CATEGORY, true), "category lock accepted");
                LessonsLearned lesson = db.getLessonById(id);
                check(lesson.getBodyLocked() && lesson.getCategoryLocked(), "both flags saved");
                check(!lesson.getTitleLocked(), "unrelated title remains unlocked");
            });

            run("Contributor cannot set a lock", id -> {
                equal(ModelCuratorLock.ACCESS_DENIED,
                    ModelCuratorLock.setFieldLock(db, CONTRIBUTOR, id, LessonsLearned.FIELD_BODY, true),
                    "noncurator rejected");
                check(!db.getLessonById(id).getBodyLocked(), "no change after rejection");
            });

            run("Null actor cannot change locks", id -> {
                equal(ModelCuratorLock.ACCESS_DENIED,
                    ModelCuratorLock.setFieldLock(db, null, id, LessonsLearned.FIELD_TITLE, true),
                    "null actor rejected");
                check(!db.getLessonById(id).getTitleLocked(), "title remains unlocked");
            });

            run("Invalid field rejected", id -> {
                equal(ModelCuratorLock.INVALID_FIELD, lock(id, "password", true), "unknown field rejected");
                equal(ModelCuratorLock.INVALID_FIELD, lock(id, "Title", true), "case variation rejected");
                equal(ModelCuratorLock.INVALID_FIELD, lock(id, null, true), "null field rejected");
                check(!db.getLessonById(id).hasLockedField(), "no fields changed");
            });

            run("Curator unlocks title", id -> {
                equal("", lock(id, LessonsLearned.FIELD_TITLE, true), "initial lock");
                equal("", lock(id, LessonsLearned.FIELD_TITLE, false), "unlock accepted");
                check(!db.getLessonById(id).getTitleLocked(), "unlock persisted");
            });

            run("Lock all core fields", id -> {
                equal("", ModelCuratorLock.setAllCoreFieldsLocked(db, CURATOR, id, true), "all locks accepted");
                LessonsLearned lesson = db.getLessonById(id);
                check(lesson.getTitleLocked() && lesson.getBodyLocked() && lesson.getCategoryLocked(),
                    "all three fields persisted as locked");
            });

            run("Unlock all core fields", id -> {
                equal("", ModelCuratorLock.setAllCoreFieldsLocked(db, CURATOR, id, true), "lock all");
                equal("", ModelCuratorLock.setAllCoreFieldsLocked(db, CURATOR, id, false), "unlock all");
                check(!db.getLessonById(id).hasLockedField(), "all fields unlocked in DB");
            });

            run("Noncurator cannot lock entire lesson", id -> {
                equal(ModelCuratorLock.ACCESS_DENIED,
                    ModelCuratorLock.setAllCoreFieldsLocked(db, CONTRIBUTOR, id, true),
                    "noncurator rejected");
                check(!db.getLessonById(id).hasLockedField(), "no fields locked");
            });

            run("Invalid and nonexistent lesson IDs", id -> {
                equal(ModelCuratorLock.LESSON_NOT_FOUND, lock(-1, LessonsLearned.FIELD_TITLE, true),
                    "negative ID rejected");
                equal(ModelCuratorLock.LESSON_NOT_FOUND, lock(0, LessonsLearned.FIELD_TITLE, true),
                    "zero ID rejected");
                equal(ModelCuratorLock.LESSON_NOT_FOUND,
                    lock(Integer.MAX_VALUE, LessonsLearned.FIELD_TITLE, true),
                    "nonexistent positive ID rejected");
                equal(ModelCuratorLock.LESSON_NOT_FOUND,
                    ModelCuratorLock.setAllCoreFieldsLocked(db, CURATOR, -1, true),
                    "all-fields request rejects missing lesson");
            });

            run("Locked field cannot be edited by Contributor", id -> {
                equal("", lock(id, LessonsLearned.FIELD_BODY, true), "curator locks body");
                String result = ModelLessonDetail.updateField(db, TEST_OWNER, id,
                    LessonsLearned.FIELD_BODY, "New body");
                check(!result.isEmpty(), "contributor receives a refusal");
                equal("Temporary test body", db.getLessonById(id).getBody(), "body unchanged");
                equal("", ModelLessonDetail.updateField(db, TEST_OWNER, id,
                    LessonsLearned.FIELD_TITLE, "Still editable"), "unlocked title can change");
            });

            run("Locked lesson cannot be deleted by Contributor", id -> {
                equal("", ModelCuratorLock.setAllCoreFieldsLocked(db, CURATOR, id, true), "lock whole lesson");
                String result = ModelLessonDetail.deleteLesson(db, TEST_OWNER, id);
                check(!result.isEmpty(), "contributor delete refused");
                check(db.lessonExists(id), "lesson still stored");
            });

            run("Unlock allows Contributor edit and delete", id -> {
                equal("", ModelCuratorLock.setAllCoreFieldsLocked(db, CURATOR, id, true), "initial lock");
                equal("", ModelCuratorLock.setAllCoreFieldsLocked(db, CURATOR, id, false), "unlock");
                equal("", ModelLessonDetail.updateField(db, TEST_OWNER, id,
                    LessonsLearned.FIELD_TITLE, "Editable again"), "contributor can edit");
                equal("Editable again", db.getLessonById(id).getTitle(), "edit persisted");
                equal("", ModelLessonDetail.deleteLesson(db, TEST_OWNER, id), "contributor can delete");
                check(!db.lessonExists(id), "deleted after unlock");
            });

        } catch (SQLException e) {
            failed++;
            System.out.println("FAIL database connection: " + e.getMessage());
        } catch (RuntimeException e) {
            failed++;
            e.printStackTrace();
        } finally {
            if (db != null) db.closeConnection();
        }

        System.out.println("RESULTS: " + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }

    /** Executes one independent test and cleans up only the temporary lesson it created. */
    private static void run(String name, TestBody test) {
        int id = db.addLesson("HW3 Lock Test", "Temporary test body", "Testing", TEST_OWNER);
        if (id < 1) {
            failed++;
            System.out.println("FAIL " + name + ": unable to create temporary lesson");
            return;
        }
        try {
            test.execute(id);
            passed++;
            System.out.println("PASS " + name);
        } catch (AssertionError | RuntimeException e) {
            failed++;
            System.out.println("FAIL " + name + ": " + e.getMessage());
        } finally {
            // Why: never DROP tables or clear all lessons in a shared test database.
            if (db.lessonExists(id)) db.deleteLesson(id);
        }
    }

    /** Calls the same curator model method used by the locking GUI. */
    private static String lock(int id, String field, boolean value) {
        return ModelCuratorLock.setFieldLock(db, CURATOR, id, field, value);
    }

    /** Checks an expected value; failure identifies exactly what was different. */
    private static void equal(Object expected, Object actual, String explanation) {
        if (!java.util.Objects.equals(expected, actual))
            throw new AssertionError(explanation + " - expected <" + expected + ">, got <" + actual + ">");
    }

    /** Fails the test if the required condition is false. */
    private static void check(boolean condition, String explanation) {
        if (!condition) throw new AssertionError(explanation);
    }

    /** Creates simulated role-holders without adding actual user accounts. */
    private static User makeUser(boolean curator) {
        User user = new User();
        user.setCuratorUser(curator);
        return user;
    }

    @FunctionalInterface
    private interface TestBody {
        void execute(int lessonId);
    }
}
