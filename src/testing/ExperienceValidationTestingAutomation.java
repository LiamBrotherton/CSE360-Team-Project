
package testing;

import guiAddExperience.ModelAddExperience;
import inputRecognizer.LessonValidator;

/**
 * <p>Title: ExperienceValidationTestingAutomation Class.</p>
 *
 * <p>Description: Semi-automated checks for Contributor Story 3 form
 * validation. The tests use the same model as the JavaFX controller, but do
 * not change real database records.</p>
 *
 * @author Hassan Mohamed
 * @version 1.00 2026-10-20 Initial version
 * @version 1.01 2026-10-20 Updated blank Lesson ID validation test
 */
public class ExperienceValidationTestingAutomation {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String title, boolean condition) {
        System.out.println((condition ? "PASS" : "FAIL") + " - " + title);
        if (condition) passed++;
        else failed++;
    }

    /** Runs positive and negative input-validation cases for experience information. */
    public static void main(String[] args) {
        String validWhat = "Reviewed the failed test output";
        String validHow = "Reran the test after changing the JavaFX VM arguments";
        String validDuration = "25 minutes";
        String validEffort = "2 people";

        check("Valid experience information", ModelAddExperience.validate(
                "1", validWhat, validHow, validDuration, validEffort).isEmpty());

        check("Blank lesson ID rejected", !ModelAddExperience.validate(
                "", validWhat, validHow, validDuration, validEffort).isEmpty());

        check("Non-numeric lesson ID rejected", ModelAddExperience.validate(
                "abc", validWhat, validHow, validDuration, validEffort)
                .contains("whole number"));

        check("Blank what-was-done rejected", ModelAddExperience.validate(
                "1", "", validHow, validDuration, validEffort)
                .contains("What was done"));

        check("Blank how-it-was-done rejected", ModelAddExperience.validate(
                "1", validWhat, "", validDuration, validEffort)
                .contains("How it was done"));

        check("Blank duration rejected", ModelAddExperience.validate(
                "1", validWhat, validHow, "", validEffort)
                .contains("Duration"));

        check("Blank effort rejected", ModelAddExperience.validate(
                "1", validWhat, validHow, validDuration, "")
                .contains("Effort"));

        check("Long duration rejected", ModelAddExperience.validate(
                "1", validWhat, validHow,
                "x".repeat(LessonValidator.MAX_DURATION_LENGTH + 1), validEffort)
                .contains("Duration"));

        check("Long effort rejected", ModelAddExperience.validate(
                "1", validWhat, validHow, validDuration,
                "x".repeat(LessonValidator.MAX_EFFORT_LENGTH + 1))
                .contains("Effort"));

        System.out.println("Passed: " + passed + " | Failed: " + failed);
    }
}
