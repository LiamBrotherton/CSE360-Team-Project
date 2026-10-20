package testing;

/*******
 * <p> Title: ContributorLessonManualTests Class. </p>
 *
 * <p> Description: Manual tests for one Contributor user story:
 *
 * "As a contributor, I can add new lessons learned to the system, specifying core information.
 * Once a lesson has been added to the system, I can see and manage it.  Other roles may update or
 * delete the information I have added.  Other roles may lock one or more fields, so I am unable to
 * alter them.  They may delete the lessons learned I have added.  I cannot see or alter any
 * additional information those roles may add to the lessons."
 *
 * ContributorLessonTestingAutomation checks the rules behind the pages.  These tests check what
 * only the screen can show: the empty list, the message on the Add a lesson page, the missing Edit
 * button on a locked field, the delete question, the refusal to delete a locked lesson, and that
 * no other role's home page leads to these pages.  Run main to print the same checklist.
 *
 * Before starting: run the application from FoundationsMain and log in as a person whose role is
 * Contributor.  An Admin can make one by inviting a person with the Contributor role.  The lesson
 * console runs in the Eclipse console at the same time, and is used in M3 and M5 to play a
 * Curator or Admin who locks a field, because the screens for those roles belong to a later
 * phase.  Its option 2 lists every lesson with its ID.
 *
 * <p>M1. The empty list (R2).
 * Steps: log in as a Contributor who has not added a lesson.  On Contributor Home press
 * "My lessons".
 * Expected: the My lessons page opens and the list says "You have not added any lessons yet."</p>
 *
 * <p>M2. A blank title is refused (R1).
 * Steps: on Contributor Home press "Add a lesson".  Leave Title empty, type "Design" in Category
 * and a sentence in Body.  Press Save.
 * Expected: "Title cannot be blank." shows in red above the buttons, the page stays open, and
 * Category and Body still hold what was typed.  Fill in a title and press Save: My lessons opens
 * and the new lesson is in the list.</p>
 *
 * <p>M3. A locked field has no Edit button (R3, R4, R6).
 * Steps: add a lesson as in M2.  In the lesson console choose 2 to find its ID, then choose 6,
 * enter that ID, the field "category", and "l" to lock it.  In the application press
 * "My lessons".
 * Expected: the lesson's row ends with "1 field locked".  Choose it and press Open: the Category
 * row says "Locked" and has no Edit button; Title and Body still have Edit.  Press Edit on Title,
 * change it and press OK: the new title shows.</p>
 *
 * <p>M4. Delete asks first, and Cancel keeps the lesson (R5).
 * Steps: open a lesson with no locked field and press "Delete lesson".
 * Expected: a dialog asks "Delete this lesson?" and says "This cannot be undone."  Press Cancel:
 * the dialog closes and the lesson is still shown.  Press "Delete lesson" again and press OK:
 * My lessons opens and the lesson is no longer in the list.</p>
 *
 * <p>M5. A lesson with a locked field cannot be deleted (R5, R6).
 * Steps: open the lesson whose category was locked in M3 and press "Delete lesson".
 * Expected: no dialog appears; the page says in red that the lesson is locked and cannot be
 * deleted, and the lesson is still in My lessons.</p>
 *
 * <p>M6. Only a Contributor can reach these pages.
 * Steps: log in as a person whose only role is Viewer, then as one whose only role is Curator.
 * Expected: neither home page has a "My lessons" or an "Add a lesson" button, so neither role can
 * reach these pages.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-20 Initial version
 */
public class ContributorLessonManualTests {

	/** Creates a new ContributorLessonManualTests. */
	public ContributorLessonManualTests() {}

	/** The manual tests, each as its name, its requirements, its steps, and what is expected. */
	private static final String[][] TESTS = {
		{ "M1. The empty list (R2)",
			"Log in as a Contributor who has not added a lesson.  On Contributor Home press "
			+ "\"My lessons\".",
			"The My lessons page opens and the list says \"You have not added any lessons yet.\"" },
		{ "M2. A blank title is refused (R1)",
			"On Contributor Home press \"Add a lesson\".  Leave Title empty, type \"Design\" in "
			+ "Category and a sentence in Body.  Press Save.",
			"\"Title cannot be blank.\" shows in red, the page stays open, and Category and Body "
			+ "still hold what was typed.  Fill in a title and press Save: My lessons opens and the "
			+ "new lesson is in the list." },
		{ "M3. A locked field has no Edit button (R3, R4, R6)",
			"Add a lesson as in M2.  In the lesson console choose 2 to find its ID, then 6, that ID, "
			+ "\"category\", and \"l\".  In the application press \"My lessons\".",
			"The row ends with \"1 field locked\".  Open it: Category says \"Locked\" with no Edit "
			+ "button; Title and Body have Edit.  Edit the title and press OK: the new title shows." },
		{ "M4. Delete asks first, and Cancel keeps the lesson (R5)",
			"Open a lesson with no locked field and press \"Delete lesson\".",
			"A dialog asks \"Delete this lesson?\" and says \"This cannot be undone.\"  Cancel keeps "
			+ "the lesson on screen.  Press \"Delete lesson\" again and OK: My lessons opens without "
			+ "the lesson." },
		{ "M5. A lesson with a locked field cannot be deleted (R5, R6)",
			"Open the lesson locked in M3 and press \"Delete lesson\".",
			"No dialog appears; the page says in red that the lesson is locked and cannot be "
			+ "deleted, and the lesson is still in My lessons." },
		{ "M6. Only a Contributor can reach these pages",
			"Log in as a person whose only role is Viewer, then as one whose only role is Curator.",
			"Neither home page has a \"My lessons\" or an \"Add a lesson\" button." },
	};

	/**********
	 * <p> Method: main(String[] args) </p>
	 *
	 * <p> Description: Prints the manual tests as a checklist to follow while using the
	 * application. </p>
	 *
	 * @param args	not used
	 */
	public static void main(String[] args) {
		System.out.println("========================================");
		System.out.println("CONTRIBUTOR LESSONS MANUAL TESTS");
		System.out.println("========================================");
		System.out.println("Run the application, log in as a Contributor, and keep the lesson");
		System.out.println("console open to lock a field in M3 and M5.");
		for (String[] test : TESTS) {
			System.out.println();
			System.out.println(test[0]);
			System.out.println("  Steps:    " + test[1]);
			System.out.println("  Expected: " + test[2]);
			System.out.println("  Result:   [ ] PASS   [ ] FAIL");
		}
	}
}
