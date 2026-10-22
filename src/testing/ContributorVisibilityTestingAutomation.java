package testing;

import java.util.Objects;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: ContributorVisibilityTestingAutomation Class. </p>
 *
 * <p> Description: Automated testing for the Contributor Visibility user story. </p>
 *
 * <p> These tests verify that contributors can retrieve their own lessons, cannot retrieve another contributor's 
 * lessons, and prepares for future test after the Curator role is implemented. Test lessons are removed after the tests finish.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Alexis
 *
 * @version 1.00		2026-10-19 Initial version
 * 
 */
public class ContributorVisibilityTestingAutomation {
    private static Database database;

    private static int passed = 0;		// Counts the number of passed tests
	private static int failed = 0;		// Counts the number of failed tests

    private static String contributorA; //First Contributor for testing visibility 
    private static String contributorB; //Second Contributor for testing visibility 

    //Contributors future lesson IDs, A belongs to Contributor A and B to B.
    private static int lessonA1 = -1;
    private static int lessonA2 = -1;
    private static int lessonB1 = -1;

    /**
     * Runs the contributor visibility tests.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("CONTRIBUTOR VISIBILITY AUTOMATED TESTING");
        System.out.println("========================================\n");

        try {
            database = new Database();
            database.connectToDatabase();

			// Create unique disposable names so repeated runs never collide
            String suffix = String.valueOf(System.currentTimeMillis());

            contributorA = "VisibilityA" + suffix;
            contributorB = "VisibilityB" + suffix;

            System.out.println("Contributor A: " + contributorA);
            System.out.println("Contributor B: " + contributorB);

            testEmptyContributorList();
            testSeparateContributorLessonStorage();
            testIndividualLessonVisibility();
            testAccessAfterCuratorEdit();

        } catch (Exception e) {
            failed++;
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();

        } finally {

            // Remove only the lessons created by this test.
            if (database != null) {
                if (lessonA1 >= 0) {
                    database.deleteLessonForContributor(lessonA1, contributorA);
                }

                if (lessonA2 >= 0) {
                    database.deleteLessonForContributor(lessonA2, contributorA);
                }

                if (lessonB1 >= 0) {
                    database.deleteLessonForContributor(lessonB1, contributorB);
                }

                database.closeConnection();
            }

            System.out.println();
            System.out.println("========================================");
            System.out.println("TEST SUMMARY");
            System.out.println("Passed: " + passed);
            System.out.println("Failed: " + failed);
            System.out.println("========================================");
        }
    }

    /**
     * Verifies a contributor with no lessons receives an empty list.
     */
    private static void testEmptyContributorList() {

        section("A: Empty contributor list");

        LessonsLearnedList lessons = database.getLessonsForContributor(contributorA);

        checkTest("Test A1 - New Contributor Has No Lessons",
                lessons != null && lessons.isEmpty(), true);
    }

    /**
     * Verifies that each contributor receives only their own lessons. 
     * This makes sure they saved properly without focusing on checking the visibility. 
     */
    private static void testSeparateContributorLessonStorage() {

        section("B: Contributor-specific lesson lists");

        lessonA1 = database.addLessonForContributor(
                "Visibility LessonsLearned A1",
                "First lesson belonging to contributor A.",
                "Testing",
                contributorA);

        lessonA2 = database.addLessonForContributor(
                "Visibility LessonsLearned A2",
                "Second lesson belonging to contributor A.",
                "Testing",
                contributorA);

        lessonB1 = database.addLessonForContributor(
                "Visibility LessonsLearned B1",
                "LessonsLearned belonging to contributor B.",
                "Testing",
                contributorB);

        checkTest("Test B1  - First LessonsLearned Saved", lessonA1 >= 0, true);

        checkTest("Test B2  - Second LessonsLearned Saved", lessonA2 >= 0, true);

        checkTest("Test B3  - Third LessonsLearned Saved", lessonB1 >= 0, true);

        LessonsLearnedList lessonsA = database.getLessonsForContributor(contributorA);

        LessonsLearnedList lessonsB = database.getLessonsForContributor(contributorB);

        checkTest("Test B4  - Contributor A Has Two Lessons",
                lessonsA != null && lessonsA.size() == 2, true);

        checkTest("Test B5  - Contributor B Has One Lesson",
                lessonsB != null && lessonsB.size() == 1, true);

        if (lessonsA != null) {
            checkTest("Test B6  - A's List Contains A1",
                    containsLesson(lessonsA, lessonA1), true);

            checkTest("Test B7  - A's List Contains A2",
                    containsLesson(lessonsA, lessonA2), true);

        }

        if (lessonsB != null) {
            checkTest("Test B9  - B's List Contains B1",
                    containsLesson(lessonsB, lessonB1), true);

            checkTest("Test B10 - B's List Excludes A1",
                    containsLesson(lessonsB, lessonA1), false);

        }
    }

    /**
     * Verifies that each contributor can only retrieve their own lessons.
     * This tests the visibility issue specifically, making sure Contributors can only see what they create. 
     */
    private static void testIndividualLessonVisibility() {
    	lessonA1 = database.addLessonForContributor(
                "Visibility LessonsLearned A1",
                "First lesson belonging to contributor A.",
                "Testing",
                contributorA);

        lessonA2 = database.addLessonForContributor(
                "Visibility LessonsLearned A2",
                "Second lesson belonging to contributor A.",
                "Testing",
                contributorA);

        lessonB1 = database.addLessonForContributor(
                "Visibility LessonsLearned B1",
                "LessonsLearned belonging to contributor B.",
                "Testing",
                contributorB);

        LessonsLearnedList lessonsA = database.getLessonsForContributor(contributorA);
        LessonsLearnedList lessonsB = database.getLessonsForContributor(contributorB);
        
        section("C: Individual lesson ownership");

        if (lessonA1 < 0 || lessonB1 < 0) {
            checkTest("Test C1 - Both Test Lessons Exist", false, true);
            return;
        }

        LessonsLearned ownLesson = database.getLessonForContributor(lessonA1, contributorA);

        checkTest("Test C1 - Contributor Can Retrieve Their Own Lesson",
                ownLesson != null && ownLesson.getId() == lessonA1, true);

        checkTest("Test C2 - A's List Excludes B1",
                containsLesson(lessonsA, lessonB1), false);
        
        checkTest("Test C3 - B's List Excludes A2",
                containsLesson(lessonsB, lessonA2), false);
    }

    /**
     * Verifies that ownership does not change after a Curator edits a lesson.
     *
     * This may no longer work after Curator is implemented, since Curators can change visibility. Currently, this test just 
     * updates the lesson's name as the curator role is a stub. 
     * 
     * Future testing may allow a contributor to see another's lesson after a curator makes it visible. 
     */
    private static void testAccessAfterCuratorEdit() {

        section("D: Access after Curator edit");

        if (lessonA1 < 0) {
            checkTest("Test D1 - LessonsLearned Exists For Curator Edit", false, true);
            return;
        }

        boolean updated = database.updateLessonTitle(lessonA1, "Curator Edited Visibility Lesson");

        checkTest("Test D1 - LessonsLearned Title Updated", updated, true);

        LessonsLearned lessonAfterEdit = database.getLessonForContributor(lessonA1, contributorA);

        checkTest("Test D2 - Original Contributor Retains Access",
                lessonAfterEdit != null && lessonAfterEdit.getId() == lessonA1, true);

        checkTest("Test D3 - Updated Title Is Visible To Owner",
                lessonAfterEdit != null && "Curator Edited Visibility Lesson".equals(lessonAfterEdit.getTitle()), true);

        LessonsLearnedList lessonsAfterEdit = database.getLessonsForContributor(contributorA);

        checkTest("Test D4 - Edited LessonsLearned Remains In Owner's List",
                lessonsAfterEdit != null && containsLesson(lessonsAfterEdit, lessonA1), true);
    }

    /**
     * Checks whether a lesson ID is in a lesson list.
     *
     * @param lessons :the list to inspect
     * @param lessonId :the ID to find
     * @return true if the lesson is there
     */
    private static boolean containsLesson(LessonsLearnedList lessons, int lessonId) {

        if (lessons == null) {
            return false;
        }

        for (LessonsLearned lesson : lessons.getLessons()) {
            if (lesson.getId() == lessonId) {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper Method! Prints the section heading.
     *
     * @param name :the section name to print
     */
    private static void section(String name) {
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println(name);
        System.out.println("----------------------------------------\n");
    }

    /**
     * Helper Method! Compares the actual result with the expected result.
     *
     * @param name :the test name
     * @param actual :the observed result
     * @param expected :the expected result
     */
    private static void checkTest(String name, Object actual, Object expected) {
        boolean success = Objects.equals(actual, expected);

        System.out.println(name + ": " + (success ? "PASS" : "FAIL"));

        if (!success) {
            System.out.println("  Expected: " + expected);
            System.out.println("  Actual:   " + actual);
            failed++;
        } else {
            passed++;
        }
    }
}
