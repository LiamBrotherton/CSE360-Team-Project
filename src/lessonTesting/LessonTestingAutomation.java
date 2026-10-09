package lessonTesting;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.Lesson;
import entityClasses.LessonList;
import entityClasses.User;
import lessonManagement.ControllerLessons;

/*******
 * <p> Title: LessonTestingAutomation Class. </p>
 *
 * <p> Description: Automated testing for the HW2 lessons learned Create, Read, Update, and Delete
 * (CRUD) user stories and their input validation.  Each test case in Test Cases.pdf is numbered
 * TC01 to TC29, and the tests below carry the same numbers.  A test case that checks more than
 * one thing is reported as TC09a, TC09b, and so on, so a failure points at the exact check.
 *
 * Every operation is invoked through ControllerLessons, LessonRecognizer, LessonList, and
 * Database, the same methods the interactive HW2 application calls, so nothing is tested against
 * a copy of the logic.
 *
 * A negative test checks more than that the operation was refused.  It also checks that the
 * message names the field, states the rule, and gives the limit or position where there is one.
 * A message that merely said "invalid" would be refused by the code and still be useless to the
 * person reading it, so a bare refusal is not enough to pass.
 *
 * IMPORTANT: this runs against the real FoundationDatabase, which contains real accounts and
 * possibly real lessons.  It therefore never assumes the lesson table is empty and never asserts
 * an absolute row count.  Counts are always compared with a baseline taken just before the
 * action.  Every search uses a word that carries a number unique to this run, so a lesson that
 * already existed cannot be mistaken for one created here.  The disposable accounts and lessons
 * are removed again in the finally block.
 *
 * Only one program may open the H2 database file at a time, so close the main application
 * before running this.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-04 Initial version
 */
public class LessonTestingAutomation {

	private static Database database = applicationMain.FoundationsMain.database;

	private static int passed = 0;		// Counter of the number of passed tests
	private static int failed = 0;		// Counter of the number of failed tests

	private static String ann;			// Disposable Contributor, owns most of the lessons
	private static String ben;			// Disposable Contributor, used to test ownership
	private static String run;			// Digits unique to this run, put into searchable words

	// Every lesson id this automation creates, so the finally block can remove them
	private static final List<Integer> created = new ArrayList<Integer>();

	/**
	 * Default constructor is not used.  This class only holds the main method.
	 */
	public LessonTestingAutomation() {
	}

	/**********
	 * <p> Method: main(String[] args) </p>
	 *
	 * <p> Description: This mainline displays a header to the console, performs the test cases in
	 * the order of the Test Cases.pdf, displays a summary of the results, and removes the
	 * disposable data. </p>
	 *
	 * @param args the command line parameters.  These are not used.
	 */
	public static void main(String[] args) {

		System.out.println("========================================");
		System.out.println("LESSONS LEARNED CRUD AUTOMATED TESTING");
		System.out.println("========================================");

		try {
			database.connectToDatabase();

			long number = System.currentTimeMillis() % 100000;
			run = String.valueOf(number);
			ann = "HW2Ann" + number;
			ben = "HW2Ben" + number;
			database.register(new User(ann, "Test123!", "Ann", "", "Tester", "",
					"ann@example.com", false, true, false, false));
			database.register(new User(ben, "Test123!", "Ben", "", "Tester", "",
					"ben@example.com", false, true, false, false));

			System.out.println("\nLessons already in the database: " + database.getNumberOfLessons());
			System.out.println("Disposable Contributors: " + ann + ", " + ben);

			/*-*************************************************************************
			 * CREATE: positive tests
			 */
			section("CREATE: acceptable lessons are stored");

			String t1 = "JavaFX runtime components are missing when launching from Eclipse";
			String s1 = "Running the application from Eclipse failed with \"JavaFX runtime components "
					+ "are missing\" even though the JavaFX library was on the build path.";
			String l1 = "Add the JavaFX lib folder to the VM arguments with --module-path and "
					+ "--add-modules javafx.controls. The build path alone is not enough, because "
					+ "the launcher needs the module path at run time.";
			int id1 = add(ann, t1, s1, l1, "Implementation", "javafx, eclipse ,setup");

			checkTest("Test TC01 - A Valid Lesson Is Added And Gets A Positive Id", id1 > 0, true);

			Lesson r1 = database.getLesson(id1);
			boolean read = r1 != null;
			checkTest("Test TC02 - The Stored Lesson Can Be Read Back", read, true);
			if (read) {
				checkTest("Test TC02a - Title Round-Trips", r1.getTitle(), t1);
				checkTest("Test TC02b - Situation Round-Trips", r1.getSituation(), s1);
				checkTest("Test TC02c - Lesson Text Round-Trips", r1.getLessonText(), l1);
				checkTest("Test TC02d - Category Round-Trips", r1.getCategory(), "Implementation");
				checkTest("Test TC02e - Tags Round-Trip Normalized", r1.getTags(),
						"javafx,eclipse,setup");
				checkTest("Test TC02f - Author Round-Trips", r1.getAuthor(), ann);
				checkTest("Test TC02g - Creation Time Was Assigned", r1.getCreatedAt() > 0, true);
			} else {
				System.out.println("\nTests TC02a - TC02g skipped: the lesson could not be read.");
				failed = failed + 7;
			}

			// Tags are optional, and are stored in one form whatever spacing the author used
			int idNoTags = add(ann, "A lesson with no tags at all",
					"The team never wrote down which tags to use.", "Tags are optional, so leave "
					+ "them empty rather than inventing one.", "Teamwork", "");
			Lesson noTags = database.getLesson(idNoTags);
			checkTest("Test TC03a - Empty Tags Are Accepted And Stored As Empty",
					noTags != null ? noTags.getTags() : "<not stored>", "");
			int idSpaced = add(ann, "A lesson with untidy tags",
					"The author typed spaces around the commas.", "Spacing is removed so the same "
					+ "tags always have the same stored form.", "Other", "java, h2 ,Database");
			Lesson spaced = database.getLesson(idSpaced);
			checkTest("Test TC03b - Spaces Around Commas Are Removed From Stored Tags",
					spaced != null ? spaced.getTags() : "<not stored>", "java,h2,Database");

			// Boundary values and line breaks
			int idMin = add(ann, "abc", "0123456789", "0123456789", "Design", "ab");
			checkTest("Test TC04a - The Shortest Acceptable Lesson Is Accepted", idMin > 0, true);
			int idMax = add(ann, "t".repeat(100), "s".repeat(2000), "l".repeat(2000), "Design",
					"a".repeat(25));
			checkTest("Test TC04b - The Longest Acceptable Lesson Is Accepted", idMax > 0, true);
			String twoLines = "First line of the situation.\nSecond line of the situation.";
			int idLines = add(ann, "A lesson spanning several lines", twoLines,
					"The situation above has a line break, which must survive storage.", "Other", "");
			Lesson lines = database.getLesson(idLines);
			checkTest("Test TC04c - Line Breaks In The Situation Are Preserved",
					lines != null ? lines.getSituation() : "<not stored>", twoLines);

			/*-*************************************************************************
			 * CREATE: negative tests.  Each one must be refused with a message that names the
			 * field and states the rule.  The count is taken first so TC13 can prove that none
			 * of these refusals stored anything.
			 */
			section("CREATE: unacceptable lessons are refused with helpful messages");

			int baseline = database.getNumberOfLessons();
			String okS = "A situation that is long enough to pass.";
			String okL = "A lesson that is long enough to pass.";

			int r = ControllerLessons.addLesson(ann, "", okS, okL, "Design", "");
			checkRefused("Test TC05a - An Empty Title Is Refused", r == -1,
					ControllerLessons.lastErrorMessage, "Title", "required");

			r = ControllerLessons.addLesson(ann, "     ", okS, okL, "Design", "");
			checkRefused("Test TC05b - A Title Of Only Spaces Is Refused", r == -1,
					ControllerLessons.lastErrorMessage, "Title", "required");

			r = ControllerLessons.addLesson(ann, "t".repeat(101), okS, okL, "Design", "");
			checkRefused("Test TC06 - A Title Over The Limit Is Refused With Count And Limit",
					r == -1, ControllerLessons.lastErrorMessage, "Title", "too long", "101", "100");

			r = ControllerLessons.addLesson(ann, "Two\nlines", okS, okL, "Design", "");
			checkRefused("Test TC07 - A Title With A Line Break Is Refused With The Position",
					r == -1, ControllerLessons.lastErrorMessage, "Title", "single line", "position 3");

			r = ControllerLessons.addLesson(ann, "A fine title", "too short", okL, "Design", "");
			checkRefused("Test TC08a - A Situation Under The Minimum Is Refused", r == -1,
					ControllerLessons.lastErrorMessage, "Situation", "too short", "10");

			r = ControllerLessons.addLesson(ann, "A fine title", okS, "l".repeat(2001), "Design", "");
			checkRefused("Test TC08b - A Lesson Text Over The Limit Is Refused", r == -1,
					ControllerLessons.lastErrorMessage, "Lesson", "too long", "2001", "2000");

			r = ControllerLessons.addLesson(ann, "A fine title", okS, okL, "Gardening", "");
			checkRefused("Test TC09a - An Unknown Category Is Refused And The Choices Are Listed",
					r == -1, ControllerLessons.lastErrorMessage, "Gardening", "not recognized",
					"Teamwork");

			r = ControllerLessons.addLesson(ann, "A fine title", okS, okL, "", "");
			checkRefused("Test TC09b - A Missing Category Is Refused", r == -1,
					ControllerLessons.lastErrorMessage, "Category", "required");

			r = ControllerLessons.addLesson(ann, "A fine title", okS, okL, "Design", "good,bad tag!");
			checkRefused("Test TC10a - A Tag With An Invalid Character Is Refused", r == -1,
					ControllerLessons.lastErrorMessage, "Tag 2", "bad tag!", "' '");

			r = ControllerLessons.addLesson(ann, "A fine title", okS, okL, "Design", "one,,three");
			checkRefused("Test TC10b - An Empty Tag Between Commas Is Refused", r == -1,
					ControllerLessons.lastErrorMessage, "Tag 2", "empty");

			r = ControllerLessons.addLesson(ann, "A fine title", okS, okL, "Design", "a".repeat(26));
			checkRefused("Test TC10c - A Tag Over The Limit Is Refused", r == -1,
					ControllerLessons.lastErrorMessage, "Tag 1", "too long", "26", "25");

			r = ControllerLessons.addLesson(ann, "A fine title", okS, okL, "Design",
					"t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11");
			checkRefused("Test TC11 - More Than Ten Tags Are Refused", r == -1,
					ControllerLessons.lastErrorMessage, "Too many tags", "11", "10");

			r = ControllerLessons.addLesson("NoSuchUser" + run, "A fine title", okS, okL, "Design", "");
			checkRefused("Test TC12 - An Author Who Is Not A User Is Refused", r == -1,
					ControllerLessons.lastErrorMessage, "NoSuchUser" + run, "not a user");

			checkTest("Test TC13 - None Of The Refused Lessons Was Stored",
					database.getNumberOfLessons() == baseline, true);

			/*-*************************************************************************
			 * READ
			 */
			section("READ: viewing and listing lessons");

			int idBen = add(ben, "Invitation codes were never consumed",
					"After a new user created an account the same invitation code still worked.",
					"Pass the code the page was opened with when removing it, not a field that is "
					+ "never filled in.", "Implementation", "invitations,security");

			Lesson mine = ControllerLessons.viewMyLesson(ann, id1);
			checkTest("Test TC14a - A Contributor Can View Their Own Lesson",
					mine != null && mine.getId() == id1, true);

			Lesson missing = ControllerLessons.viewMyLesson(ann, -1);
			checkRefused("Test TC14b - A Lesson That Does Not Exist Gives A Message",
					missing == null, ControllerLessons.lastErrorMessage, "no lesson number -1");

			Lesson anyone = ControllerLessons.viewLesson(idBen);
			checkTest("Test TC14c - A Viewer Can View Any Lesson",
					anyone != null && anyone.getAuthor().equals(ben), true);

			Lesson others = ControllerLessons.viewMyLesson(ann, idBen);
			checkRefused("Test TC14d - A Contributor Cannot View Another Contributor's Lesson",
					others == null, ControllerLessons.lastErrorMessage, "among the lessons you added");

			LessonList annList = ControllerLessons.listMyLessons(ann);
			boolean onlyAnn = true;
			for (Lesson l : annList.asList()) if (!l.getAuthor().equals(ann)) onlyAnn = false;
			checkTest("Test TC15a - My Lessons Holds Only My Lessons",
					onlyAnn && annList.getById(id1) != null && annList.getById(idBen) == null, true);

			LessonList none = ControllerLessons.listMyLessons("Nobody" + run);
			checkTest("Test TC15b - A Contributor With No Lessons Gets An Empty List, Not An Error",
					none != null && none.isEmpty() && ControllerLessons.lastErrorMessage.isEmpty(), true);

			LessonList all = ControllerLessons.listAllLessons();
			checkTest("Test TC15c - All Lessons Includes Every Contributor's Lessons",
					all.getById(id1) != null && all.getById(idBen) != null
							&& all.size() == database.getNumberOfLessons(), true);

			int idNewest = add(ann, "Newest lesson in this run",
					"Added last so it must come first.", "The list is ordered newest first.",
					"Other", "");
			checkTest("Test TC15d - The Newest Lesson Is Listed First",
					ControllerLessons.listMyLessons(ann).get(0).getId() == idNewest, true);

			/*-*************************************************************************
			 * SEARCH.  Each lesson carries a word made unique by this run's number, so a
			 * search can only find lessons this automation created.
			 */
			section("SEARCH: keyword search returns the matching subset");

			int idST = add(ann, "Zebratitle" + run + " in the heading", "Plain situation text here.",
					"Plain lesson text here.", "Testing", "");
			int idSS = add(ann, "A lesson matched by its situation", "Situation says zebrasit" + run
					+ " in the middle.", "Plain lesson text here.", "Testing", "");
			int idSL = add(ann, "A lesson matched by its lesson text", "Plain situation text here.",
					"Lesson says zebralesson" + run + " at the end.", "Testing", "");
			int idSG = add(ann, "A lesson matched by its tag", "Plain situation text here.",
					"Plain lesson text here.", "Testing", "zebratag" + run);

			checkTest("Test TC16a - A Keyword In The Title Finds The Lesson",
					found("zebratitle" + run, idST), true);
			checkTest("Test TC16b - A Keyword In The Situation Finds The Lesson",
					found("zebrasit" + run, idSS), true);
			checkTest("Test TC16c - A Keyword In The Lesson Text Finds The Lesson",
					found("zebralesson" + run, idSL), true);
			checkTest("Test TC16d - A Keyword In The Tags Finds The Lesson",
					found("zebratag" + run, idSG), true);

			checkTest("Test TC17a - Search Ignores Upper And Lower Case",
					found("ZEBRATITLE" + run, idST), true);

			int idPhrase = add(ann, "A lesson matched by a phrase", "We held a quiet requirements "
					+ "review " + run + " before coding.", "Review first.", "Requirements", "");
			checkTest("Test TC17b - A Phrase Of Several Words Finds The Lesson",
					found("Quiet Requirements Review " + run, idPhrase), true);

			LessonList noMatch = ControllerLessons.searchLessons("nothingmatchesthis" + run);
			checkTest("Test TC18 - A Search With No Match Gives An Empty List And No Error",
					noMatch != null && noMatch.isEmpty() && ControllerLessons.lastErrorMessage.isEmpty(),
					true);

			LessonList emptyKw = ControllerLessons.searchLessons("   ");
			checkRefused("Test TC19a - An Empty Keyword Is Refused", emptyKw.isEmpty(),
					ControllerLessons.lastErrorMessage, "Search keyword", "required");

			LessonList oneChar = ControllerLessons.searchLessons("z");
			checkRefused("Test TC19b - A One Character Keyword Is Refused", oneChar.isEmpty(),
					ControllerLessons.lastErrorMessage, "Search keyword", "too short", "2");

			int idPct = add(ann, "Cut the backlog", "Backlog " + run + " was growing.",
					"Reduced backlog " + run + " by 100% after agreeing on a template.", "Teamwork", "");
			int idThousand = add(ann, "Reviewed many pages", "Backlog " + run + " again.",
					"Reviewed backlog " + run + " by 1000 pages without a template.", "Teamwork", "");
			LessonList pct = ControllerLessons.searchLessons("backlog " + run + " by 100%");
			checkTest("Test TC20 - A Percent Sign Is Matched Literally, Not As A Wildcard",
					pct.getById(idPct) != null && pct.getById(idThousand) == null, true);

			for (int i = 1; i <= 25; i++)
				add(ann, "Bulk lesson number " + i, "Bulk situation " + i + ".",
						"Carries the word bulkword" + run + " for the search.", "Other", "");
			LessonList bulk = ControllerLessons.searchLessons("bulkword" + run);
			checkTest("Test TC21a - A Search Can Return Many Lessons", bulk.size(), 25);
			checkTest("Test TC21b - The Result Is A Smaller Subset Of All Lessons",
					bulk.size() < ControllerLessons.listAllLessons().size(), true);

			/*-*************************************************************************
			 * UPDATE
			 */
			section("UPDATE: owners change their own lessons");

			Lesson before = database.getLesson(id1);
			boolean updated = ControllerLessons.updateLesson(ann, id1, "A corrected title",
					"A corrected situation that is long enough.",
					"A corrected lesson that is long enough.", "Testing", "fixed,reviewed");
			Lesson after = database.getLesson(id1);
			checkTest("Test TC22a - An Owner Can Update Every Editable Field",
					updated && after != null && after.getTitle().equals("A corrected title")
							&& after.getSituation().equals("A corrected situation that is long enough.")
							&& after.getLessonText().equals("A corrected lesson that is long enough.")
							&& after.getCategory().equals("Testing")
							&& after.getTags().equals("fixed,reviewed"), true);
			checkTest("Test TC22b - Update Leaves Id, Author, And Creation Time Alone",
					before != null && after != null && after.getId() == before.getId()
							&& after.getAuthor().equals(before.getAuthor())
							&& after.getCreatedAt() == before.getCreatedAt(), true);
			ControllerLessons.updateLesson(ann, id1, "A corrected title",
					"A corrected situation that is long enough.",
					"A corrected lesson that is long enough.", "Testing", "");
			checkTest("Test TC22c - Update Can Clear The Tags",
					database.getLesson(id1).getTags(), "");

			Lesson stable = database.getLesson(id1);
			boolean badTitle = ControllerLessons.updateLesson(ann, id1, "", "A corrected situation "
					+ "that is long enough.", "A corrected lesson that is long enough.", "Testing", "");
			String badTitleMsg = ControllerLessons.lastErrorMessage;
			checkRefused("Test TC23a - An Update With An Invalid Title Is Refused", !badTitle,
					badTitleMsg, "Title", "required");
			boolean badCat = ControllerLessons.updateLesson(ann, id1, "A corrected title", "A "
					+ "corrected situation that is long enough.", "A corrected lesson that is long "
					+ "enough.", "Gardening", "");
			checkRefused("Test TC23b - An Update With An Unknown Category Is Refused", !badCat,
					ControllerLessons.lastErrorMessage, "Gardening", "not recognized");
			checkTest("Test TC23c - Refused Updates Left The Stored Lesson Unchanged",
					sameFields(stable, database.getLesson(id1)), true);

			boolean notOwner = ControllerLessons.updateLesson(ben, id1, "Ben's takeover title",
					"Ben's situation that is long enough.", "Ben's lesson that is long enough.",
					"Testing", "");
			checkRefused("Test TC23d - Another Contributor Cannot Update The Lesson", !notOwner,
					ControllerLessons.lastErrorMessage, "another user", "update");
			checkTest("Test TC23e - The Refused Update Changed Nothing",
					sameFields(stable, database.getLesson(id1)), true);

			boolean gone = ControllerLessons.updateLesson(ann, -1, "A fine title", okS, okL,
					"Testing", "");
			checkRefused("Test TC23f - Updating A Lesson That Does Not Exist Gives A Message",
					!gone, ControllerLessons.lastErrorMessage, "no lesson number -1");

			/*-*************************************************************************
			 * DELETE
			 */
			section("DELETE: owners remove their own lessons");

			int idDel = add(ann, "A lesson about to be deleted zebradel" + run, "This lesson exists "
					+ "only to be deleted.", "It should vanish everywhere.", "Other", "");
			int countBefore = database.getNumberOfLessons();
			boolean deleted = ControllerLessons.deleteLesson(ann, idDel);
			checkTest("Test TC24a - An Owner Can Delete Their Lesson", deleted, true);
			checkTest("Test TC24b - The Deleted Lesson Can No Longer Be Read",
					database.getLesson(idDel) == null, true);
			checkTest("Test TC24c - The Lesson Count Dropped By One",
					database.getNumberOfLessons() == countBefore - 1, true);
			checkTest("Test TC24d - The Deleted Lesson Is Gone From The Lists And Search",
					ControllerLessons.listAllLessons().getById(idDel) == null
							&& ControllerLessons.listMyLessons(ann).getById(idDel) == null
							&& !found("zebradel" + run, idDel), true);

			boolean delNotOwner = ControllerLessons.deleteLesson(ann, idBen);
			checkRefused("Test TC25a - Another Contributor Cannot Delete The Lesson", !delNotOwner,
					ControllerLessons.lastErrorMessage, "another user", "delete");
			checkTest("Test TC25b - The Refused Delete Left The Lesson In Place",
					database.getLesson(idBen) != null, true);

			boolean delMissing = ControllerLessons.deleteLesson(ann, -1);
			checkRefused("Test TC25c - Deleting A Lesson That Does Not Exist Gives A Message",
					!delMissing, ControllerLessons.lastErrorMessage, "no lesson number -1");

			boolean delTwice = ControllerLessons.deleteLesson(ann, idDel);
			checkRefused("Test TC25d - Deleting The Same Lesson Twice Is Refused The Second Time",
					!delTwice, ControllerLessons.lastErrorMessage, "already");

			/*-*************************************************************************
			 * PERSISTENCE
			 */
			section("PERSISTENCE: lessons survive closing the database");

			Lesson beforeClose = database.getLesson(id1);
			int countBeforeClose = database.getNumberOfLessons();
			database.closeConnection();
			database.connectToDatabase();
			Lesson afterOpen = database.getLesson(id1);
			checkTest("Test TC26a - A Lesson Is Still There After The Database Was Closed And Reopened",
					afterOpen != null && sameFields(beforeClose, afterOpen), true);
			checkTest("Test TC26b - The Lesson Count Is The Same After Reopening",
					database.getNumberOfLessons() == countBeforeClose, true);

			/*-*************************************************************************
			 * LESSONLIST: the class that holds all lessons or any subset of them
			 */
			section("LESSONLIST: holding all lessons or any subset");

			LessonList list = new LessonList();
			checkTest("Test TC27a - A New List Is Empty",
					list.isEmpty() && list.size() == 0, true);

			Lesson a = new Lesson(1, "Alpha title", "Alpha situation", "Alpha lesson", "Design",
					"", "x", 1L);
			Lesson b = new Lesson(2, "Beta title", "Beta situation", "Beta lesson", "Testing",
					"", "x", 2L);
			list.add(a);
			checkTest("Test TC27b - A List Can Hold Exactly One Lesson",
					list.size() == 1 && !list.isEmpty(), true);

			list.add(null);
			checkTest("Test TC27c - Adding Null Is Ignored", list.size(), 1);

			list.add(b);
			checkTest("Test TC27d - A Lesson Is Found By Its Id, And A Missing Id Gives Null",
					list.getById(2) == b && list.getById(99) == null, true);
			checkTest("Test TC27e - A Position Outside The List Gives Null",
					list.get(-1) == null && list.get(2) == null && list.get(0) == a, true);
			checkTest("Test TC27f - Removing A Lesson Works Once And Then Reports That It Is Gone",
					list.removeById(1) && !list.removeById(1) && list.size() == 1, true);

			LessonList mixed = new LessonList();
			mixed.add(a);
			mixed.add(b);
			mixed.add(new Lesson(3, "Gamma title", "Gamma situation", "Gamma lesson", "Design",
					"", "x", 3L));
			LessonList designs = mixed.filterByCategory("Design");
			checkTest("Test TC28a - A Category Filter Returns The Subset In That Category",
					designs.size() == 2 && designs.getById(1) != null && designs.getById(3) != null,
					true);
			checkTest("Test TC28b - Filtering Leaves The Original List Unchanged", mixed.size(), 3);
			checkTest("Test TC28c - A Category With No Lessons Gives An Empty List, Not Null",
					mixed.filterByCategory("Teamwork").isEmpty(), true);

			LessonList large = new LessonList();
			for (int i = 0; i < 5000; i++)
				large.add(new Lesson(i, "Title " + i, "Situation " + i, "Lesson " + i, "Other", "",
						"x", i));
			checkTest("Test TC29a - A List Holds Thousands Of Lessons",
					large.size() == 5000 && large.getById(4999) != null, true);
			boolean readOnly = false;
			try {
				large.asList().clear();
			} catch (UnsupportedOperationException e) {
				readOnly = true;
			}
			checkTest("Test TC29b - The Read-Only View Cannot Be Used To Change The List",
					readOnly && large.size() == 5000, true);

			System.out.println("\n========================================");
			System.out.println("TEST SUMMARY");
			System.out.println("========================================");
			System.out.println("Passed: " + passed);
			System.out.println("Failed: " + failed);
			System.out.println("Total:  " + (passed + failed));
			System.out.println("========================================");

		} catch (SQLException e) {
			System.out.println("Database error while running tests:");
			e.printStackTrace();
		} finally {
			/*
			 * CLEANUP
			 *
			 * Remove every lesson this automation created, by id and then by author as a second
			 * net, and then the two disposable accounts, so the database is left as it was found.
			 */
			try {
				for (int id : created) database.deleteLesson(id);
				for (String who : new String[] { ann, ben }) {
					if (who == null) continue;
					for (Lesson l : database.getLessonsByAuthor(who).asList())
						database.deleteLesson(l.getId());
					if (database.doesUserExist(who)) database.deleteUser(who);
				}
			} catch (Exception e) {
				System.out.println("Warning: cleanup failed. Lessons or accounts named HW2Ann/HW2Ben "
						+ "may remain in the database.");
			}
			database.closeConnection();
		}
	}

	/*-*******************************************************************************************

	Helper methods used to minimize the number of lines of code needed above

	*/

	/**********
	 * Adds a lesson through the controller and remembers its id so the cleanup can remove it.
	 * A refused add returns -1 and is not remembered.  This is only for adds that are expected to
	 * succeed; the refusal tests call the controller directly so they can read its message.
	 */
	private static int add(String author, String title, String situation, String lessonText,
			String category, String tags) {
		int id = ControllerLessons.addLesson(author, title, situation, lessonText, category, tags);
		if (id > 0) created.add(id);
		else System.out.println("\n*** SETUP PROBLEM *** A lesson that should be acceptable was "
				+ "refused: " + ControllerLessons.lastErrorMessage);
		return id;
	}

	/**********
	 * Searches for a keyword and reports whether the lesson with the given id is in the result.
	 */
	private static boolean found(String keyword, int id) {
		return ControllerLessons.searchLessons(keyword).getById(id) != null;
	}

	/**********
	 * Compares every editable field of two lessons, tolerating null so a missing lesson is a
	 * mismatch and not a crash.
	 */
	private static boolean sameFields(Lesson x, Lesson y) {
		if (x == null || y == null) return false;
		return x.getId() == y.getId() && x.getTitle().equals(y.getTitle())
				&& x.getSituation().equals(y.getSituation())
				&& x.getLessonText().equals(y.getLessonText())
				&& x.getCategory().equals(y.getCategory()) && x.getTags().equals(y.getTags())
				&& x.getAuthor().equals(y.getAuthor()) && x.getCreatedAt() == y.getCreatedAt();
	}

	/**********
	 * Prints a section heading so the console output can be scanned quickly.
	 */
	private static void section(String title) {
		System.out.println("\n----------------------------------------");
		System.out.println(title);
		System.out.println("----------------------------------------");
	}

	/**********
	 * Checks a negative test.  It passes only if the operation was refused AND the message
	 * contains every expected fragment.  The fragments are the words that make the message
	 * helpful: the field name, the rule that was broken, and the limit or position.  The message
	 * is printed so the person watching can read it and judge it for themselves.
	 */
	private static void checkRefused(String testName, boolean refused, String message,
			String... fragments) {
		boolean helpful = message != null;
		StringBuilder missingParts = new StringBuilder();
		for (String f : fragments) {
			if (message == null || !message.contains(f)) {
				helpful = false;
				missingParts.append(" <").append(f).append(">");
			}
		}
		System.out.println();
		System.out.println(testName);
		System.out.println("Expected: refused, with a message containing " + show(String.join("; ",
				fragments)));
		System.out.println("Actual:   " + (refused ? "refused" : "NOT refused") + ", message " + show(message));
		if (refused && helpful) {
			System.out.println("Result:   PASS");
			passed++;
		} else {
			if (!missingParts.toString().isEmpty())
				System.out.println("Missing from the message:" + missingParts);
			System.out.println("Result:   FAIL");
			failed++;
		}
	}

	/**********
	 * Compares an actual boolean result with the expected result and prints PASS or FAIL.
	 */
	private static void checkTest(String testName, boolean actual, boolean expected) {
		System.out.println();
		System.out.println(testName);
		System.out.println("Expected: " + expected);
		System.out.println("Actual:   " + actual);
		if (actual == expected) {
			System.out.println("Result:   PASS");
			passed++;
		} else {
			System.out.println("Result:   FAIL");
			failed++;
		}
	}

	/**********
	 * Compares an actual int result with the expected result and prints PASS or FAIL.
	 */
	private static void checkTest(String testName, int actual, int expected) {
		System.out.println();
		System.out.println(testName);
		System.out.println("Expected: " + expected);
		System.out.println("Actual:   " + actual);
		if (actual == expected) {
			System.out.println("Result:   PASS");
			passed++;
		} else {
			System.out.println("Result:   FAIL");
			failed++;
		}
	}

	/**********
	 * Compares an actual String result with the expected result and prints PASS or FAIL.  The
	 * comparison is null safe, and values are printed inside angle brackets so that an empty
	 * string or a trailing space is visible in the report.
	 */
	private static void checkTest(String testName, String actual, String expected) {
		boolean match = (actual == null) ? (expected == null) : actual.equals(expected);
		System.out.println();
		System.out.println(testName);
		System.out.println("Expected: " + show(expected));
		System.out.println("Actual:   " + show(actual));
		if (match) {
			System.out.println("Result:   PASS");
			passed++;
		} else {
			System.out.println("Result:   FAIL");
			failed++;
		}
	}

	/**********
	 * Renders a String for the report so that null and the empty string are both visible.  Long
	 * values are shortened so a 2000 character lesson does not bury the report.
	 */
	private static String show(String s) {
		if (s == null) return "null";
		if (s.length() > 150) return "<" + s.substring(0, 150) + "... (" + s.length() + " characters)>";
		return "<" + s + ">";
	}
}
