package testing;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import database.Database;
import entityClasses.LessonsLearned;
import guiAddLesson.ModelAddLesson;
import guiMyLessons.ModelMyLessons;
import guiLessonDetail.ModelLessonDetail;
import inputRecognizer.LessonValidator;

/*******
 * <p> Title: ContributorLessonTestingAutomation Class. </p>
 *
 * <p> Description: Automated testing for one Contributor user story:
 *
 * "As a contributor, I can add new lessons learned to the system, specifying core information.
 * Once a lesson has been added to the system, I can see and manage it.  Other roles may update or
 * delete the information I have added.  Other roles may lock one or more fields, so I am unable to
 * alter them.  They may delete the lessons learned I have added.  I cannot see or alter any
 * additional information those roles may add to the lessons."
 *
 * The story is broken into seven requirements, R1 to R7, and every test names the requirement it
 * checks:
 * R1 add a lesson with its core information; R2 see a list of the lessons I added, and only those;
 * R3 open a lesson and see its core information and which fields are locked; R4 change an
 * unlocked field; R5 delete my lesson unless a field is locked; R6 changes, locks and deletions
 * made by a Curator or Admin are what I see next; R7 I cannot open or change another
 * Contributor's lesson.
 *
 * The tests call the same Model methods the pages call, so nothing is tested against a copy of
 * the logic.  A Curator or Admin is played by calling the Database directly, because the screens
 * for those roles belong to a later phase.
 *
 * How to read the output: each test prints its number, name and requirement, then each of its
 * checks as three lines: "Check:" says what is checked, "Expected:" the result the requirement
 * calls for, and "Actual:" the result the code produced, each value between angle brackets.  A
 * check whose actual result differs from the expected one is listed again as a "Failed:" line,
 * followed by "Result: FAIL"; a test with no failed check prints "Result: PASS".  The run ends with the number passed and failed, and exits with
 * status 1 if any test failed.
 *
 * The tests run against the real database, which may already hold lessons.  They never empty
 * the lesson table.  They work only with two Contributor usernames made up for this run, and
 * the finally block removes every lesson the run created.  No user account is made: the pages
 * decide ownership by username alone, so none is needed, and on an empty database an account
 * left behind would stop the application from offering First Admin setup.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-20 Initial version
 */
public class ContributorLessonTestingAutomation {

	/** Creates a new ContributorLessonTestingAutomation. */
	public ContributorLessonTestingAutomation() {}

	private static Database database;

	private static int passed = 0;		// Counter of the number of passed tests
	private static int failed = 0;		// Counter of the number of failed tests

	// The checks of the current test that failed, one line each.  Empty means the test passed.
	private static StringBuilder problems = new StringBuilder();

	// The two Contributor usernames for this run (no accounts are made for them).  Ann owns most lessons; Ben is the "other"
	// Contributor used to show that one Contributor cannot reach another's lessons.
	private static String ann;
	private static String ben;

	// Every lesson ID this run created, so the finally block can remove them all
	private static List<Integer> created = new ArrayList<Integer>();

	/*
	 * This mainline displays a header, runs every test in requirement order, displays the
	 * totals, and removes everything the run created.
	 */
	/**********
	 * <p> Method: main(String[] args) </p>
	 *
	 * <p> Description: Runs every test for the Contributor lessons story and prints the totals.
	 * </p>
	 *
	 * @param args the command line parameters.  These are not used.
	 */
	public static void main(String[] args) {
		System.out.println("========================================");
		System.out.println("CONTRIBUTOR LESSONS AUTOMATED TESTING");
		System.out.println("========================================");

		try {
			database = new Database();
			database.connectToDatabase();

			long number = System.currentTimeMillis() % 100000;
			ann = "CLAnn" + number;
			ben = "CLBen" + number;

			testAddedByIsStored();
			testOnlyMyLessonsAreReturned();
			testAddValidLesson();
			testAddBlankTitleRefused();
			testAddTooLongBodyRefused();
			testAddKeepsQuotes();
			testMyLessonsOnlyMine();
			testMyLessonsEmpty();
			testRowTextShowsLockCount();
			testOpenOwnLesson();
			testOpenMissingLesson();
			testRenameTitle();
			testUpdateCategoryAndBody();
			testUpdateLockedFieldRefused();
			testUpdateBlankValueRefused();
			testDeleteOwnLesson();
			testDeleteWithLockedFieldRefused();
			testOtherRoleEditIsSeen();
			testOtherRoleLockIsEnforced();
			testOtherRoleDeleteThenEdit();
			testOtherContributorCannotOpen();
			testOtherContributorCannotChange();

			System.out.println();
			System.out.println("========================================");
			System.out.println("Passed: " + passed);
			System.out.println("Failed: " + failed);
			System.out.println("========================================");
		} catch (SQLException e) {
			System.out.println("*** ERROR *** The database could not be used: " + e.getMessage());
			failed++;
		} finally {
			cleanUp();
		}
		System.exit(failed == 0 ? 0 : 1);
	}

	/*-*******************************************************************************************

	The tests, in requirement order

	*/

	/**********
	 * <p> Test: testAddedByIsStored (R1, R2) </p>
	 *
	 * <p> Requirement: a lesson records the Contributor who added it, which is what makes "the
	 * lessons I added" possible. </p>
	 *
	 * <p> How the code satisfies it: Database.addLesson with four arguments stores the username
	 * in the addedBy column, and getLessonById reads it back into LessonsLearned.getAddedBy. </p>
	 *
	 * <p> How to read the output: PASS means the lesson read back carries Ann's username. </p>
	 */
	private static void testAddedByIsStored() {
		startTest("testAddedByIsStored", "R1, R2");
		int id = database.addLesson("A stored author", "Body text.", "Testing", ann);
		created.add(id);
		LessonsLearned read = database.getLessonById(id);
		expectEqual("the lesson is saved with an ID", "yes", yesNo(id > 0));
		expectEqual("the author stored with the lesson", ann, read == null ? "no lesson" : read.getAddedBy());
		endTest();
	}

	/**********
	 * <p> Test: testOnlyMyLessonsAreReturned (R2, R7) </p>
	 *
	 * <p> Requirement: the list of a Contributor's lessons holds the lessons that Contributor
	 * added, and no one else's.  A lesson saved without an author belongs to no one. </p>
	 *
	 * <p> How the code satisfies it: Database.getLessonsAddedBy selects only the rows whose
	 * addedBy equals the username. </p>
	 *
	 * <p> How to read the output: PASS means Ann's list has exactly her two lessons, Ben's has
	 * exactly his one, and the lesson with no author is in neither. </p>
	 */
	private static void testOnlyMyLessonsAreReturned() {
		startTest("testOnlyMyLessonsAreReturned", "R2, R7");
		int a1 = database.addLesson("Ann first", "Body.", "Testing", ann);
		int a2 = database.addLesson("Ann second", "Body.", "Testing", ann);
		int b1 = database.addLesson("Ben only", "Body.", "Testing", ben);
		int none = database.addLesson("No author", "Body.", "Testing");
		created.add(a1); created.add(a2); created.add(b1); created.add(none);

		List<LessonsLearned> annList = database.getLessonsAddedBy(ann).getLessons();
		List<LessonsLearned> benList = database.getLessonsAddedBy(ben).getLessons();
		// Ann already owns one lesson from the previous test, so count only these
		int annCount = 0;
		boolean onlyAnn = true;
		for (LessonsLearned l : annList) {
			if (!ann.equals(l.getAddedBy())) onlyAnn = false;
			if (l.getId() == a1 || l.getId() == a2) annCount++;
			if (l.getId() == none) onlyAnn = false;
		}
		expectEqual("how many of Ann's two new lessons are in her list", 2, annCount);
		expectEqual("every lesson in Ann's list is hers", "yes", yesNo(onlyAnn));
		expectEqual("the IDs in Ben's list", List.of(b1), benList.stream().map(LessonsLearned::getId).toList());
		endTest();
	}

	/**********
	 * <p> Test: testAddValidLesson (R1, positive) </p>
	 *
	 * <p> Requirement: a Contributor can add a lesson by giving its core information. </p>
	 *
	 * <p> How the code satisfies it: ModelAddLesson.addLesson, the method the Add a lesson page
	 * calls, validates the three fields and saves the lesson with the Contributor as its author.
	 * </p>
	 *
	 * <p> How to read the output: PASS means no error message was returned and the stored lesson
	 * has the title, category, body and author that were entered. </p>
	 */
	private static void testAddValidLesson() {
		startTest("testAddValidLesson", "R1");
		String message = ModelAddLesson.addLesson(database, ann, "Valid title", "Testing",
				"A valid body.");
		created.add(ModelAddLesson.lastAddedId);
		LessonsLearned read = database.getLessonById(ModelAddLesson.lastAddedId);
		expectEqual("the message returned", "", message);
		expectEqual("the stored lesson (title | category | body | author)",
				"Valid title | Testing | A valid body. | " + ann, describe(read));
		endTest();
	}

	/**********
	 * <p> Test: testAddBlankTitleRefused (R1, negative) </p>
	 *
	 * <p> Requirement: core information is required; a lesson without a title is not added. </p>
	 *
	 * <p> How the code satisfies it: ModelAddLesson.addLesson runs LessonValidator.checkTitle
	 * first and returns its message before anything is saved. </p>
	 *
	 * <p> How to read the output: the printed message must say the title cannot be blank, and
	 * PASS means the number of lessons did not change. </p>
	 */
	private static void testAddBlankTitleRefused() {
		startTest("testAddBlankTitleRefused", "R1");
		int before = database.getNumberOfLessons();
		String message = ModelAddLesson.addLesson(database, ann, "   ", "Testing", "Body.");
		expectMessage(message, "Title cannot be blank.");
		expectEqual("the number of lessons stored", before, database.getNumberOfLessons());
		expectEqual("the new lesson ID reported", -1, ModelAddLesson.lastAddedId);
		endTest();
	}

	/**********
	 * <p> Test: testAddTooLongBodyRefused (R1, negative) </p>
	 *
	 * <p> Requirement: core information must fit the system's limits. </p>
	 *
	 * <p> How the code satisfies it: ModelAddLesson.addLesson runs LessonValidator.checkBody,
	 * which refuses a body longer than LessonValidator.MAX_BODY_LENGTH. </p>
	 *
	 * <p> How to read the output: the printed message must say the body is too long, and PASS
	 * means the number of lessons did not change. </p>
	 */
	private static void testAddTooLongBodyRefused() {
		startTest("testAddTooLongBodyRefused", "R1");
		int before = database.getNumberOfLessons();
		String message = ModelAddLesson.addLesson(database, ann, "A fine title", "Testing",
				"x".repeat(LessonValidator.MAX_BODY_LENGTH + 1));
		expectMessage(message, "Body is too long");
		expectEqual("the number of lessons stored", before, database.getNumberOfLessons());
		endTest();
	}

	/**********
	 * <p> Test: testAddKeepsQuotes (R1, positive) </p>
	 *
	 * <p> Requirement: the core information is stored exactly as entered, including apostrophes
	 * and quotation marks. </p>
	 *
	 * <p> How the code satisfies it: the lesson is saved through a prepared statement, so the
	 * characters are stored as data and never read as part of the database command. </p>
	 *
	 * <p> How to read the output: PASS means the title read back is identical to the one entered.
	 * </p>
	 */
	private static void testAddKeepsQuotes() {
		startTest("testAddKeepsQuotes", "R1");
		String title = "Don't trust \"fast\" fixes";
		String message = ModelAddLesson.addLesson(database, ann, title, "Testing", "Body.");
		created.add(ModelAddLesson.lastAddedId);
		LessonsLearned read = database.getLessonById(ModelAddLesson.lastAddedId);
		expectEqual("the message returned", "", message);
		expectEqual("the title read back", title, read == null ? "no lesson" : read.getTitle());
		endTest();
	}

	/**********
	 * <p> Test: testMyLessonsOnlyMine (R2, R7, positive) </p>
	 *
	 * <p> Requirement: once a Contributor has added lessons, that Contributor can see them, and
	 * sees no other Contributor's lessons. </p>
	 *
	 * <p> How the code satisfies it: ModelMyLessons.lessonsFor, which the My lessons page calls,
	 * reads only the lessons whose author is the Contributor. </p>
	 *
	 * <p> How to read the output: PASS means the lesson Ann just added is in her list, Ben's lesson
	 * is not, and every lesson in her list is hers. </p>
	 */
	private static void testMyLessonsOnlyMine() {
		startTest("testMyLessonsOnlyMine", "R2, R7");
		ModelAddLesson.addLesson(database, ann, "Ann's list lesson", "Testing", "Body.");
		int annId = ModelAddLesson.lastAddedId;
		created.add(annId);
		ModelAddLesson.addLesson(database, ben, "Ben's list lesson", "Testing", "Body.");
		int benId = ModelAddLesson.lastAddedId;
		created.add(benId);

		boolean hasMine = false, hasBens = false, allMine = true;
		for (LessonsLearned l : ModelMyLessons.lessonsFor(database, ann).getLessons()) {
			if (l.getId() == annId) hasMine = true;
			if (l.getId() == benId) hasBens = true;
			if (!ann.equals(l.getAddedBy())) allMine = false;
		}
		expectEqual("Ann's list holds the lesson she just added", "yes", yesNo(hasMine));
		expectEqual("Ann's list holds Ben's lesson", "no", yesNo(hasBens));
		expectEqual("every lesson in Ann's list is hers", "yes", yesNo(allMine));
		endTest();
	}

	/**********
	 * <p> Test: testMyLessonsEmpty (R2, negative) </p>
	 *
	 * <p> Requirement: a Contributor who has added nothing sees an empty list and is told so,
	 * rather than seeing other people's lessons. </p>
	 *
	 * <p> How the code satisfies it: ModelMyLessons.lessonsFor returns an empty list for a
	 * Contributor with no lessons, and ModelMyLessons.EMPTY_MESSAGE is the text the page shows.
	 * </p>
	 *
	 * <p> How to read the output: PASS means the list for a username with no lessons is empty and
	 * the empty-list message is the agreed text. </p>
	 */
	private static void testMyLessonsEmpty() {
		startTest("testMyLessonsEmpty", "R2");
		expectEqual("the number of lessons listed for a Contributor with none", 0,
				ModelMyLessons.lessonsFor(database, "NoLessons" + ann).size());
		expectEqual("the empty-list message", "You have not added any lessons yet.",
				ModelMyLessons.EMPTY_MESSAGE);
		endTest();
	}

	/**********
	 * <p> Test: testRowTextShowsLockCount (R3, positive and negative) </p>
	 *
	 * <p> Requirement: a Contributor can see which of their lessons have locked fields; another
	 * role may lock fields so the Contributor cannot alter them. </p>
	 *
	 * <p> How the code satisfies it: ModelMyLessons.rowText builds the line the list shows for a
	 * lesson, ending with "N field(s) locked" when any field is locked and with nothing about
	 * locks when none is. </p>
	 *
	 * <p> How to read the output: the three rows are printed; PASS means no lock gives no lock
	 * text, one lock gives "1 field locked", and two give "2 fields locked". </p>
	 */
	private static void testRowTextShowsLockCount() {
		startTest("testRowTextShowsLockCount", "R3");
		ModelAddLesson.addLesson(database, ann, "Lock count lesson", "Testing", "Body.");
		int id = ModelAddLesson.lastAddedId;
		created.add(id);

		String none = ModelMyLessons.rowText(database.getLessonById(id));
		database.setLessonFieldLocked(id, LessonsLearned.FIELD_CATEGORY, true);		// a Curator locks one field
		String one = ModelMyLessons.rowText(database.getLessonById(id));
		database.setLessonFieldLocked(id, LessonsLearned.FIELD_BODY, true);			// and then a second
		String two = ModelMyLessons.rowText(database.getLessonById(id));
		expectEqual("the row with no locked field", "Lock count lesson  (Testing)", none);
		expectEqual("the row with one locked field", "Lock count lesson  (Testing)  -  1 field locked", one);
		expectEqual("the row with two locked fields", "Lock count lesson  (Testing)  -  2 fields locked", two);
		endTest();
	}

	/**********
	 * <p> Test: testOpenOwnLesson (R3, positive) </p>
	 *
	 * <p> Requirement: a Contributor can open a lesson they added and see its core information
	 * and which fields are locked. </p>
	 *
	 * <p> How the code satisfies it: ModelLessonDetail.lessonFor, which the lesson page calls,
	 * returns the stored lesson to its author, with its lock flags as stored. </p>
	 *
	 * <p> How to read the output: PASS means the lesson came back with the same title, category
	 * and body, and with the category shown as locked after a Curator locked it. </p>
	 */
	private static void testOpenOwnLesson() {
		startTest("testOpenOwnLesson", "R3");
		int id = annLesson("Open me", "Design", "Open body.");
		database.setLessonFieldLocked(id, LessonsLearned.FIELD_CATEGORY, true);	// a Curator locks it
		LessonsLearned l = ModelLessonDetail.lessonFor(database, ann, id);
		expectEqual("the lesson Ann opens (title | category | body | author)",
				"Open me | Design | Open body. | " + ann, describe(l));
		expectEqual("the fields shown as locked", "category", lockedFields(l));
		endTest();
	}

	/**********
	 * <p> Test: testOpenMissingLesson (R3, negative) </p>
	 *
	 * <p> Requirement: a lesson that does not exist cannot be opened. </p>
	 *
	 * <p> How the code satisfies it: ModelLessonDetail.lessonFor returns null when there is no
	 * lesson with that ID, and the page then shows ModelLessonDetail.NOT_AVAILABLE. </p>
	 *
	 * <p> How to read the output: PASS means nothing was returned for an ID that was never used.
	 * </p>
	 */
	private static void testOpenMissingLesson() {
		startTest("testOpenMissingLesson", "R3");
		expectEqual("the lesson returned for an ID that was never used", "none",
				found(ModelLessonDetail.lessonFor(database, ann, -5)));
		endTest();
	}

	/**********
	 * <p> Test: testRenameTitle (R4, positive) </p>
	 *
	 * <p> Requirement: a Contributor can rename a lesson they added. </p>
	 *
	 * <p> How the code satisfies it: ModelLessonDetail.updateField checks that the lesson is the
	 * Contributor's, that the title is not locked, and that the new title is valid, then saves it.
	 * </p>
	 *
	 * <p> How to read the output: PASS means no message was returned and the stored title is the
	 * new one. </p>
	 */
	private static void testRenameTitle() {
		startTest("testRenameTitle", "R4");
		int id = annLesson("Old name", "Testing", "Body.");
		String message = ModelLessonDetail.updateField(database, ann, id, LessonsLearned.FIELD_TITLE, "New name");
		expectEqual("the message returned", "", message);
		expectEqual("the stored title", "New name", database.getLessonById(id).getTitle());
		endTest();
	}

	/**********
	 * <p> Test: testUpdateCategoryAndBody (R4, positive) </p>
	 *
	 * <p> Requirement: a Contributor can update the other core information of their lesson. </p>
	 *
	 * <p> How the code satisfies it: ModelLessonDetail.updateField handles the category and the
	 * body the same way as the title, each with its own validation rule. </p>
	 *
	 * <p> How to read the output: PASS means both updates were accepted and both are stored. </p>
	 */
	private static void testUpdateCategoryAndBody() {
		startTest("testUpdateCategoryAndBody", "R4");
		int id = annLesson("Update me", "Testing", "Old body.");
		String m1 = ModelLessonDetail.updateField(database, ann, id, LessonsLearned.FIELD_CATEGORY, "Design");
		String m2 = ModelLessonDetail.updateField(database, ann, id, LessonsLearned.FIELD_BODY, "New body.");
		LessonsLearned l = database.getLessonById(id);
		expectEqual("the message for the category update", "", m1);
		expectEqual("the message for the body update", "", m2);
		expectEqual("the stored category", "Design", l.getCategory());
		expectEqual("the stored body", "New body.", l.getBody());
		endTest();
	}

	/**********
	 * <p> Test: testUpdateLockedFieldRefused (R4, negative) </p>
	 *
	 * <p> Requirement: another role may lock a field so the Contributor is unable to alter it.
	 * </p>
	 *
	 * <p> How the code satisfies it: ModelLessonDetail.updateField runs
	 * LessonValidator.checkFieldNotLocked before saving.  The page also hides the Edit button for
	 * a locked field; this test shows the rule holds even if the page were bypassed. </p>
	 *
	 * <p> How to read the output: the printed message must say the field is locked, and PASS
	 * means the stored title did not change. </p>
	 */
	private static void testUpdateLockedFieldRefused() {
		startTest("testUpdateLockedFieldRefused", "R4");
		int id = annLesson("Locked title", "Testing", "Body.");
		database.setLessonFieldLocked(id, LessonsLearned.FIELD_TITLE, true);		// a Curator locks it
		String message = ModelLessonDetail.updateField(database, ann, id, LessonsLearned.FIELD_TITLE, "Changed");
		expectMessage(message, "locked");
		expectEqual("the stored title", "Locked title", database.getLessonById(id).getTitle());
		endTest();
	}

	/**********
	 * <p> Test: testUpdateBlankValueRefused (R4, negative) </p>
	 *
	 * <p> Requirement: an update must still be valid core information. </p>
	 *
	 * <p> How the code satisfies it: ModelLessonDetail.updateField runs the same LessonValidator
	 * rule for the field as the Add a lesson page does. </p>
	 *
	 * <p> How to read the output: the printed message must say the category cannot be blank, and
	 * PASS means the stored category did not change. </p>
	 */
	private static void testUpdateBlankValueRefused() {
		startTest("testUpdateBlankValueRefused", "R4");
		int id = annLesson("Blank update", "Testing", "Body.");
		String message = ModelLessonDetail.updateField(database, ann, id, LessonsLearned.FIELD_CATEGORY, " ");
		expectMessage(message, "Category cannot be blank.");
		expectEqual("the stored category", "Testing", database.getLessonById(id).getCategory());
		endTest();
	}

	/**********
	 * <p> Test: testDeleteOwnLesson (R5, positive) </p>
	 *
	 * <p> Requirement: a Contributor can delete a lesson they added. </p>
	 *
	 * <p> How the code satisfies it: ModelLessonDetail.deleteLesson checks the lesson is the
	 * Contributor's and has no locked field, then deletes it.  (The page asks "Are you sure?"
	 * first; that is covered by a manual test.) </p>
	 *
	 * <p> How to read the output: PASS means no message was returned and the lesson is gone. </p>
	 */
	private static void testDeleteOwnLesson() {
		startTest("testDeleteOwnLesson", "R5");
		int id = annLesson("Delete me", "Testing", "Body.");
		String message = ModelLessonDetail.deleteLesson(database, ann, id);
		expectEqual("the message returned", "", message);
		expectEqual("the lesson still exists", "no", yesNo(database.lessonExists(id)));
		endTest();
	}

	/**********
	 * <p> Test: testDeleteWithLockedFieldRefused (R5, negative) </p>
	 *
	 * <p> Requirement: a lesson with a locked field cannot be deleted by its Contributor, since
	 * deleting it would alter fields another role locked. </p>
	 *
	 * <p> How the code satisfies it: ModelLessonDetail.deleteLesson runs the team's
	 * LessonValidator.checkCanDelete before deleting. </p>
	 *
	 * <p> How to read the output: the printed message must say the lesson cannot be deleted, and
	 * PASS means the lesson still exists. </p>
	 */
	private static void testDeleteWithLockedFieldRefused() {
		startTest("testDeleteWithLockedFieldRefused", "R5");
		int id = annLesson("Locked delete", "Testing", "Body.");
		database.setLessonFieldLocked(id, LessonsLearned.FIELD_BODY, true);			// a Curator locks it
		String message = ModelLessonDetail.deleteLesson(database, ann, id);
		expectMessage(message, "cannot be deleted");
		expectEqual("the lesson still exists", "yes", yesNo(database.lessonExists(id)));
		endTest();
	}

	/**********
	 * <p> Test: testOtherRoleEditIsSeen (R6, positive) </p>
	 *
	 * <p> Requirement: other roles may update the information a Contributor added, and the
	 * Contributor then sees the updated information. </p>
	 *
	 * <p> How the code satisfies it: the pages read the lesson from the database every time they
	 * are shown, through ModelLessonDetail.lessonFor and ModelMyLessons.lessonsFor. </p>
	 *
	 * <p> How to read the output: PASS means after a Curator renames the lesson, both the lesson
	 * page and the list show the new title. </p>
	 */
	private static void testOtherRoleEditIsSeen() {
		startTest("testOtherRoleEditIsSeen", "R6");
		int id = annLesson("Before the Curator", "Testing", "Body.");
		database.updateLessonTitle(id, "After the Curator");				// a Curator edits it
		LessonsLearned l = ModelLessonDetail.lessonFor(database, ann, id);
		boolean inList = false;
		for (LessonsLearned x : ModelMyLessons.lessonsFor(database, ann).getLessons())
			if (x.getId() == id && "After the Curator".equals(x.getTitle())) inList = true;
		expectEqual("the title on the lesson page", "After the Curator", l == null ? "no lesson" : l.getTitle());
		expectEqual("the new title is in Ann's list", "yes", yesNo(inList));
		endTest();
	}

	/**********
	 * <p> Test: testOtherRoleLockIsEnforced (R6, negative) </p>
	 *
	 * <p> Requirement: other roles may lock fields, and the Contributor is then unable to alter
	 * them. </p>
	 *
	 * <p> How the code satisfies it: a lock set by another role is stored in the lesson, and
	 * ModelLessonDetail.updateField reads the lesson fresh and refuses the locked field. </p>
	 *
	 * <p> How to read the output: the printed message must say the field is locked, and PASS
	 * means the body did not change. </p>
	 */
	private static void testOtherRoleLockIsEnforced() {
		startTest("testOtherRoleLockIsEnforced", "R6");
		int id = annLesson("Curator locks body", "Testing", "Original body.");
		database.setLessonFieldLocked(id, LessonsLearned.FIELD_BODY, true);			// an Admin locks it
		String message = ModelLessonDetail.updateField(database, ann, id, LessonsLearned.FIELD_BODY, "Changed.");
		expectMessage(message, "locked");
		expectEqual("the stored body", "Original body.", database.getLessonById(id).getBody());
		endTest();
	}

	/**********
	 * <p> Test: testOtherRoleDeleteThenEdit (R6, negative) </p>
	 *
	 * <p> Requirement: other roles may delete a Contributor's lesson; the Contributor then can no
	 * longer change or delete it, and is told so instead of the system failing. </p>
	 *
	 * <p> How the code satisfies it: ModelLessonDetail.updateField and deleteLesson both look the
	 * lesson up first and return ModelLessonDetail.NOT_AVAILABLE when it is gone. </p>
	 *
	 * <p> How to read the output: both printed messages must say the lesson is no longer
	 * available. </p>
	 */
	private static void testOtherRoleDeleteThenEdit() {
		startTest("testOtherRoleDeleteThenEdit", "R6");
		int id = annLesson("Deleted by Admin", "Testing", "Body.");
		database.deleteLesson(id);											// an Admin deletes it
		String m1 = ModelLessonDetail.updateField(database, ann, id, LessonsLearned.FIELD_TITLE, "Too late");
		String m2 = ModelLessonDetail.deleteLesson(database, ann, id);
		expectMessage(m1, "That lesson is no longer available.");
		expectMessage(m2, "That lesson is no longer available.");
		endTest();
	}

	/**********
	 * <p> Test: testOtherContributorCannotOpen (R7, negative) </p>
	 *
	 * <p> Requirement: a Contributor sees the lessons that Contributor added, not another
	 * Contributor's. </p>
	 *
	 * <p> How the code satisfies it: ModelLessonDetail.lessonFor returns null when the lesson's
	 * author is someone else, exactly as for a lesson that does not exist. </p>
	 *
	 * <p> How to read the output: PASS means Ben cannot open Ann's lesson while Ann can. </p>
	 */
	private static void testOtherContributorCannotOpen() {
		startTest("testOtherContributorCannotOpen", "R7");
		int id = annLesson("Ann's private lesson", "Testing", "Body.");
		expectEqual("what Ben gets when he opens Ann's lesson", "none",
				found(ModelLessonDetail.lessonFor(database, ben, id)));
		expectEqual("what Ann gets when she opens it", "lesson " + id,
				found(ModelLessonDetail.lessonFor(database, ann, id)));
		endTest();
	}

	/**********
	 * <p> Test: testOtherContributorCannotChange (R7, negative) </p>
	 *
	 * <p> Requirement: a Contributor manages the lessons that Contributor added, and no others.
	 * </p>
	 *
	 * <p> How the code satisfies it: ModelLessonDetail.updateField and deleteLesson refuse a lesson
	 * whose author is someone else, with the same message as a missing lesson so a Contributor
	 * cannot tell another person's lesson exists. </p>
	 *
	 * <p> How to read the output: both printed messages must say the lesson is no longer
	 * available, and PASS means Ann's lesson is unchanged and still exists. </p>
	 */
	private static void testOtherContributorCannotChange() {
		startTest("testOtherContributorCannotChange", "R7");
		int id = annLesson("Ann keeps this", "Testing", "Body.");
		String m1 = ModelLessonDetail.updateField(database, ben, id, LessonsLearned.FIELD_TITLE, "Ben's now");
		String m2 = ModelLessonDetail.deleteLesson(database, ben, id);
		expectMessage(m1, "That lesson is no longer available.");
		expectMessage(m2, "That lesson is no longer available.");
		LessonsLearned l = database.getLessonById(id);
		expectEqual("the title of Ann's lesson after Ben's attempts", "Ann keeps this",
				l == null ? "no lesson" : l.getTitle());
		endTest();
	}

	/*-*******************************************************************************************

	Helper methods used to minimize the number of lines of code needed above

	*/

	/**********
	 * Adds a lesson for Ann through the same method the Add a lesson page uses, and remembers it
	 * so the clean up removes it.
	 *
	 * @param title		the title
	 * @param category	the category
	 * @param body		the body
	 *
	 * @return the new lesson's ID
	 */
	private static int annLesson(String title, String category, String body) {
		ModelAddLesson.addLesson(database, ann, title, category, body);
		created.add(ModelAddLesson.lastAddedId);
		return ModelAddLesson.lastAddedId;
	}

	/**********
	 * Starts a test: prints its number, name and the requirement it checks.
	 *
	 * @param name			the name of the test
	 * @param requirement	the requirement IDs it checks
	 */
	private static void startTest(String name, String requirement) {
		problems = new StringBuilder();
		System.out.println();
		System.out.println("Test " + (passed + failed + 1) + " - " + name + " (" + requirement + ")");
	}

	/**********
	 * Runs one check of the current test: prints what is checked, the expected result and the
	 * actual result, and records the check as failed if the two differ.
	 *
	 * @param what		what is being checked
	 * @param expected	the result the requirement calls for
	 * @param actual	the result the code produced
	 */
	private static void expectEqual(String what, Object expected, Object actual) {
		System.out.println("    Check:    " + what);
		System.out.println("    Expected: <" + expected + ">");
		System.out.println("    Actual:   <" + actual + ">");
		if (!Objects.equals(expected, actual)) {
			problems.append("    Failed: ").append(what).append("\n");
		}
	}

	/**********
	 * Runs one check on a message: prints the text it must contain and the actual message, and
	 * records the check as failed if the message is missing or lacks that text.
	 *
	 * @param message	the message the code under test produced
	 * @param fragment	text the message must contain
	 */
	private static void expectMessage(String message, String fragment) {
		System.out.println("    Check:    the message returned");
		System.out.println("    Expected: <a message containing \"" + fragment + "\">");
		System.out.println("    Actual:   <" + message + ">");
		if (message == null || !message.contains(fragment)) {
			problems.append("    Failed: the message returned").append("\n");
		}
	}

	/**********
	 * Gives "yes" or "no", so a check on a condition prints as a plain answer.
	 *
	 * @param condition	the condition
	 *
	 * @return "yes" if the condition is true, else "no"
	 */
	private static String yesNo(boolean condition) {
		return condition ? "yes" : "no";
	}

	/**********
	 * Describes a lesson's core information and author on one line, for printing and comparing.
	 *
	 * @param lessonsLearned	the lesson, or null
	 *
	 * @return "title | category | body | author", or "no lesson"
	 */
	private static String describe(LessonsLearned lessonsLearned) {
		if (lessonsLearned == null) return "no lesson";
		return lessonsLearned.getTitle() + " | " + lessonsLearned.getCategory() + " | " + lessonsLearned.getBody() + " | "
				+ lessonsLearned.getAddedBy();
	}

	/**********
	 * Names the locked core fields of a lesson.
	 *
	 * @param lessonsLearned	the lesson, or null
	 *
	 * @return the locked fields separated by commas, "none", or "no lesson"
	 */
	private static String lockedFields(LessonsLearned lessonsLearned) {
		if (lessonsLearned == null) return "no lesson";
		List<String> locked = new ArrayList<String>();
		if (lessonsLearned.getTitleLocked()) locked.add(LessonsLearned.FIELD_TITLE);
		if (lessonsLearned.getCategoryLocked()) locked.add(LessonsLearned.FIELD_CATEGORY);
		if (lessonsLearned.getBodyLocked()) locked.add(LessonsLearned.FIELD_BODY);
		return locked.isEmpty() ? "none" : String.join(", ", locked);
	}

	/**********
	 * Says which lesson, if any, a look-up returned.
	 *
	 * @param lessonsLearned	the lesson returned, or null
	 *
	 * @return "lesson N", or "none"
	 */
	private static String found(LessonsLearned lessonsLearned) {
		return lessonsLearned == null ? "none" : "lesson " + lessonsLearned.getId();
	}

	/**********
	 * Ends a test: prints PASS if no check failed, otherwise the failed checks and FAIL.
	 */
	private static void endTest() {
		if (problems.length() == 0) {
			System.out.println("    Result: PASS");
			passed++;
		} else {
			System.out.print(problems);
			System.out.println("    Result: FAIL");
			failed++;
		}
	}

	/**********
	 * Removes every lesson this run created (by ID, and by author as a second net), so the
	 * database is left as it was found.
	 */
	private static void cleanUp() {
		if (database == null) return;
		try {
			for (int id : created) if (id > 0) database.deleteLesson(id);
			for (String who : new String[] { ann, ben }) {
				if (who == null) continue;
				for (LessonsLearned l : database.getLessonsAddedBy(who).getLessons()) database.deleteLesson(l.getId());
			}
		} catch (Exception e) {
			System.out.println("Warning: clean up failed: " + e.getMessage());
		}
		database.closeConnection();
	}
}
