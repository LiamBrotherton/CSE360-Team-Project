package testing;

import java.sql.SQLException;
import java.util.UUID;

import database.Database;
import entityClasses.LessonsLearned;
import guiAddExperience.ModelAddExperience;

/**
 * <p>Title: ExperienceStory3DatabaseTestingAutomation.</p>
 * <p>Task 6: positive/negative Story 3 tests for adding, viewing, updating,
 * and restricting experience records to the contributor who created the lesson.</p>
 * <p>Creates one uniquely named test lesson and deletes only that test lesson
 * afterward. Does not clear or drop the database.</p>
 *
 * @author Hassan Mohamed
 * @version 1.00 2026-10-20
 */
public class ExperienceStory3DatabaseTestingAutomation {
    private static int passed = 0;
    private static int failed = 0;

    private static void check(String title, boolean condition) {
        System.out.println((condition ? "PASS" : "FAIL") + " - " + title);
        if (condition) passed++;
        else failed++;
    }

    /** Runs Story 3's database-level integration tests. Stop the JavaFX app first. */
    public static void main(String[] args) {
        Database db = new Database();
        String owner = "story3_test_" + UUID.randomUUID().toString().substring(0, 8);
        String other = owner + "_other";
        int lessonId = -1;

        try {
            db.connectToDatabase();
            lessonId = db.addLesson("Story 3 Temporary Test", "A temporary test lesson",
                    "Testing", owner);
            if (lessonId < 1) {
                System.out.println("Could not create temporary lesson; stopping.");
                return;
            }
            String id = Integer.toString(lessonId);
            check("Owner can open own lesson",
                    ModelAddExperience.lessonFor(db, owner, lessonId) != null);
            check("Different contributor cannot open lesson",
                    ModelAddExperience.lessonFor(db, other, lessonId) == null);
            check("Nonexistent lesson cannot be opened",
                    ModelAddExperience.lessonFor(db, owner, Integer.MAX_VALUE) == null);
            check("Blank duration rejected",
                    !ModelAddExperience.save(db, owner, id,
                            "Configured JavaFX", "Updated arguments", "", "2 members").isEmpty());
            check("Different contributor cannot save to lesson",
                    ModelAddExperience.NOT_AVAILABLE.equals(
                            ModelAddExperience.save(db, other, id,
                                    "Configured JavaFX", "Updated arguments", "25 minutes", "2 members")));
            check("Unauthorized save leaves experience unchanged",
                    !db.getLessonById(lessonId).hasExperienceInformation());

            String result = ModelAddExperience.save(db, owner, id,
                    "Configured JavaFX", "Updated arguments", "25 minutes", "2 members");
            check("Valid experience saved", result.isEmpty());
            LessonsLearned saved = ModelAddExperience.lessonFor(db, owner, lessonId);
            check("Saved experience visible when reopened", saved != null
                    && saved.hasExperienceInformation()
                    && "Configured JavaFX".equals(saved.getWhatWasDone())
                    && "Updated arguments".equals(saved.getHowItWasDone())
                    && "25 minutes".equals(saved.getDuration())
                    && "2 members".equals(saved.getEffort()));

            result = ModelAddExperience.save(db, owner, id,
                    "Retested setup", "Ran tests twice", "30 minutes", "3 members");
            check("Existing experience replaced when saved", result.isEmpty()
                    && "Retested setup".equals(db.getLessonById(lessonId).getWhatWasDone()));
            check("Other contributor still cannot view saved experience",
                    ModelAddExperience.lessonFor(db, other, lessonId) == null);

        } catch (SQLException e) {
            System.out.println("Could not access H2 database: " + e.getMessage());
            failed++;
        } finally {
            if (lessonId > 0) {
                boolean deleted = db.deleteLesson(lessonId);
                System.out.println("Temporary lesson cleanup: " + (deleted ? "OK" : "FAILED"));
            }
            db.closeConnection();
            System.out.println("Passed: " + passed + " | Failed: " + failed);
        }
    }
}
