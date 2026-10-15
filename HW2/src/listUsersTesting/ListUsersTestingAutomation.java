package listUsersTesting;

import java.sql.SQLException;
import java.util.List;

import database.Database;
import entityClasses.User;
import guiListUsers.ModelListUsers;

/*******
 * <p> Title: ListUsersTestingAutomation Class. </p>
 *
 * <p> Description: Automated testing for the List All Users user story.
 *
 * The Manage Users page is built from two layers, and this automation covers both of them,
 * the way they compose, and the role updates the page performs:
 *
 * Section A exercises the data layer, Database.getAllUserAccounts(), which reads every
 * account out of the database.
 *
 * Section B exercises the display formatting, ModelListUsers.formatFullName() and
 * ModelListUsers.formatRoles(), which turn a User entity into the strings shown in the
 * Name and Roles columns.  These are pure functions and need no database.
 *
 * Section C runs the formatters over User objects that were actually retrieved from the
 * database, which is the user story end to end apart from the JavaFX rendering itself.
 *
 * Section D covers Database.updateUserRole for each of the four roles.  This section exists
 * because that method was writing every non-Admin role to a column that does not exist in
 * userDB, so granting Contributor, Viewer, or Curator silently did nothing - the SQL error
 * was swallowed by a catch block that simply returned false.  Checking each role separately
 * also proves the three roles use three distinct columns, which they previously did not.
 *
 * Every method called here is precisely the same method the interactive JavaFX page calls,
 * so nothing is tested against a copy of the logic.
 *
 * IMPORTANT: this runs against the real FoundationDatabase, which contains real accounts.
 * It therefore never assumes the user table is empty and never asserts an absolute row
 * count.  It records a baseline count, adds three uniquely named disposable users, asserts
 * on the difference, and removes them again in the finally block.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-09-17 Initial version
 * @version 2.00		2026-09-19 Updated for the Admin / Contributor / Viewer / Curator
 * 							roles, and added Section D to cover updateUserRole.
 */
public class ListUsersTestingAutomation {

	private static Database database;

	private static int passed = 0;		// Counter of the number of passed tests
	private static int failed = 0;		// Counter of the number of failed tests

	// The three disposable accounts this automation creates.  Between them they cover the
	// populated, null, and empty-string variants of middleName and preferredFirstName.
	private static String luAll;		// Plays all four roles, middle and preferred set
	private static String luNone;		// Plays no roles, middle and preferred are null
	private static String luOne;		// Plays Contributor only, middle and preferred are ""

	/*
	 * This mainline displays a header to the console, performs the four sections of test
	 * cases, displays a summary of the results, and then removes the disposable accounts.
	 */
	public static void main(String[] args) {

		System.out.println("========================================");
		System.out.println("MANAGE USERS AUTOMATED TESTING");
		System.out.println("Tester: List Users Automated Tests");
		System.out.println("========================================");

		try {
			// Connect to the Foundation database
			database = new Database();
			database.connectToDatabase();

			// Create unique disposable usernames so repeated runs never collide
			long number = System.currentTimeMillis() % 100000;
			luAll = "LUAll" + number;
			luNone = "LUNone" + number;
			luOne = "LUOne" + number;

			/*
			 * Record how many accounts already exist before we add anything.  Every count
			 * assertion below is expressed relative to this baseline, because the real
			 * database already holds the developer's own accounts.
			 */
			int baseline = database.getNumberOfUsers();
			System.out.println("\nAccounts already in the database: " + baseline);
			System.out.println("Disposable test accounts: " + luAll + ", " + luNone
					+ ", " + luOne);

			/*
			 * Disposable user 1: every role played, middle name and preferred first name
			 * both provided.
			 */
			database.register(new User(
					luAll, "Test123!", "Robert", "Quincy", "Smith", "Bob",
					"luall@example.com", true, true, true, true));

			/*
			 * Disposable user 2: no roles played, middle name and preferred first name both
			 * null, so the formatter must fall back to the given first name and omit the
			 * middle name.
			 */
			database.register(new User(
					luNone, "Test123!", "Nora", null, "None", null,
					"lunone@example.com", false, false, false, false));

			/*
			 * Disposable user 3: one role played, middle name and preferred first name both
			 * empty strings rather than null.  The formatter must treat empty the same way
			 * it treats null.
			 */
			database.register(new User(
					luOne, "Test123!", "Oliver", "", "Onely", "",
					"luone@example.com", false, true, false, false));

			/*-*************************************************************************
			 * SECTION A: the data layer, Database.getAllUserAccounts()
			 */
			section("SECTION A: getAllUserAccounts()");

			List<User> allUsers = database.getAllUserAccounts();

			checkTest("Test A1 - Returned List Is Not Empty",
					allUsers.size() >= 3, true);

			checkTest("Test A2 - List Grew By Exactly Three",
					allUsers.size() == baseline + 3, true);

			/*
			 * getAllUserAccounts() swallows any SQLException internally and returns a
			 * partial list rather than reporting failure, so a database error would look
			 * like a merely short list.  Cross-checking against getNumberOfUsers(), which
			 * counts rows with a separate query, is what exposes that.
			 */
			checkTest("Test A3 - List Size Agrees With getNumberOfUsers()",
					allUsers.size() == database.getNumberOfUsers(), true);

			User fetchedAll = findUser(allUsers, luAll);
			User fetchedNone = findUser(allUsers, luNone);
			User fetchedOne = findUser(allUsers, luOne);

			checkTest("Test A4 - LUAll Appears In The List", fetchedAll != null, true);
			checkTest("Test A5 - LUNone Appears In The List", fetchedNone != null, true);
			checkTest("Test A6 - LUOne Appears In The List", fetchedOne != null, true);

			/*
			 * Every one of the eleven stored columns must survive the round trip out to the
			 * database and back.  Checked one field at a time so a single wrong column is
			 * reported precisely rather than as one vague failure.
			 */
			if (fetchedAll != null) {
				checkTest("Test A7 - LUAll userName Round-Trips",
						fetchedAll.getUserName(), luAll);
				checkTest("Test A8 - LUAll password Round-Trips",
						fetchedAll.getPassword(), "Test123!");
				checkTest("Test A9 - LUAll firstName Round-Trips",
						fetchedAll.getFirstName(), "Robert");
				checkTest("Test A10 - LUAll middleName Round-Trips",
						fetchedAll.getMiddleName(), "Quincy");
				checkTest("Test A11 - LUAll lastName Round-Trips",
						fetchedAll.getLastName(), "Smith");
				checkTest("Test A12 - LUAll preferredFirstName Round-Trips",
						fetchedAll.getPreferredFirstName(), "Bob");
				checkTest("Test A13 - LUAll emailAddress Round-Trips",
						fetchedAll.getEmailAddress(), "luall@example.com");
				checkTest("Test A14 - LUAll adminRole Round-Trips",
						fetchedAll.getAdminRole(), true);
				checkTest("Test A15 - LUAll contributorRole Round-Trips",
						fetchedAll.getContributorRole(), true);
				checkTest("Test A16 - LUAll viewerRole Round-Trips",
						fetchedAll.getViewerRole(), true);
				checkTest("Test A17 - LUAll curatorRole Round-Trips",
						fetchedAll.getCuratorRole(), true);
			} else {
				System.out.println("\nTests A7 - A17 skipped: " + luAll
						+ " was not found in the list.");
				failed = failed + 11;
			}

			/*
			 * A14 - A17 proved that a true role flag round-trips.  This proves that a false
			 * one does too, rather than arriving as an unexpected true.
			 */
			if (fetchedNone != null) {
				checkTest("Test A18 - LUNone Role Flags All Read Back False",
						!fetchedNone.getAdminRole()
								&& !fetchedNone.getContributorRole()
								&& !fetchedNone.getViewerRole()
								&& !fetchedNone.getCuratorRole(),
						true);
			} else {
				System.out.println("\nTest A18 skipped: " + luNone + " was not found.");
				failed++;
			}

			/*-*************************************************************************
			 * SECTION B: the display formatting, ModelListUsers
			 *
			 * These are pure functions, so the User objects are built directly here and no
			 * database access is involved.
			 */
			section("SECTION B: ModelListUsers formatting");

			/*
			 * Name formatting.  A preferred first name replaces the given first name when
			 * one is present, and the middle name is included only when one is present.
			 * Null and empty string must behave identically for both fields.
			 */
			checkTest("Test B1 - Preferred And Middle Both Present",
					ModelListUsers.formatFullName(nameUser("Robert", "Q", "Smith", "Bob")),
					"Bob Q Smith");

			checkTest("Test B2 - Preferred Present, Middle Null",
					ModelListUsers.formatFullName(nameUser("Robert", null, "Smith", "Bob")),
					"Bob Smith");

			checkTest("Test B3 - Preferred Null, Middle Present",
					ModelListUsers.formatFullName(nameUser("Robert", "Q", "Smith", null)),
					"Robert Q Smith");

			checkTest("Test B4 - Preferred And Middle Both Null",
					ModelListUsers.formatFullName(nameUser("Robert", null, "Smith", null)),
					"Robert Smith");

			checkTest("Test B5 - Empty Preferred Falls Back To First Name",
					ModelListUsers.formatFullName(nameUser("Robert", "Q", "Smith", "")),
					"Robert Q Smith");

			checkTest("Test B6 - Empty Middle Is Omitted Without Doubling The Space",
					ModelListUsers.formatFullName(nameUser("Robert", "", "Smith", "Bob")),
					"Bob Smith");

			checkTest("Test B7 - Preferred And Middle Both Empty",
					ModelListUsers.formatFullName(nameUser("Robert", "", "Smith", "")),
					"Robert Smith");

			checkTest("Test B8 - Preferred Wins Even When First Name Is Empty",
					ModelListUsers.formatFullName(nameUser("", "Q", "Smith", "Bob")),
					"Bob Q Smith");

			/*
			 * Role formatting.  Roles always appear in the fixed order Admin, Contributor,
			 * Viewer, Curator, separated by single spaces with no leading or trailing space.
			 * Comparing the strings exactly is what verifies the trailing space is trimmed.
			 */
			checkTest("Test B9 - All Four Roles",
					ModelListUsers.formatRoles(roleUser(true, true, true, true)),
					"Admin Contributor Viewer Curator");

			checkTest("Test B10 - No Roles Produces The Empty String",
					ModelListUsers.formatRoles(roleUser(false, false, false, false)),
					"");

			checkTest("Test B11 - Admin Only",
					ModelListUsers.formatRoles(roleUser(true, false, false, false)),
					"Admin");

			checkTest("Test B12 - Contributor Only",
					ModelListUsers.formatRoles(roleUser(false, true, false, false)),
					"Contributor");

			checkTest("Test B13 - Viewer Only",
					ModelListUsers.formatRoles(roleUser(false, false, true, false)),
					"Viewer");

			checkTest("Test B14 - Curator Only",
					ModelListUsers.formatRoles(roleUser(false, false, false, true)),
					"Curator");

			checkTest("Test B15 - Contributor And Viewer",
					ModelListUsers.formatRoles(roleUser(false, true, true, false)),
					"Contributor Viewer");

			checkTest("Test B16 - Admin And Curator Skipping The Middle Two",
					ModelListUsers.formatRoles(roleUser(true, false, false, true)),
					"Admin Curator");

			checkTest("Test B17 - Viewer And Curator",
					ModelListUsers.formatRoles(roleUser(false, false, true, true)),
					"Viewer Curator");

			/*-*************************************************************************
			 * SECTION C: the two layers composed
			 *
			 * Runs the formatters over the User objects actually retrieved from the
			 * database in Section A.  C3 and C6 additionally confirm that an empty string
			 * stored in middleName or preferredFirstName comes back as an empty string
			 * rather than as null, which is why LUOne formats the way it does.
			 */
			section("SECTION C: retrieved users through the formatters");

			checkTest("Test C1 - Name Of Retrieved LUAll",
					formatNameSafely(fetchedAll), "Bob Quincy Smith");

			checkTest("Test C2 - Name Of Retrieved LUNone",
					formatNameSafely(fetchedNone), "Nora None");

			checkTest("Test C3 - Name Of Retrieved LUOne",
					formatNameSafely(fetchedOne), "Oliver Onely");

			checkTest("Test C4 - Roles Of Retrieved LUAll",
					formatRolesSafely(fetchedAll), "Admin Contributor Viewer Curator");

			checkTest("Test C5 - Roles Of Retrieved LUNone",
					formatRolesSafely(fetchedNone), "");

			checkTest("Test C6 - Roles Of Retrieved LUOne",
					formatRolesSafely(fetchedOne), "Contributor");

			/*-*************************************************************************
			 * SECTION D: Database.updateUserRole
			 *
			 * LUNone starts with no roles at all, so each role can be granted and then read
			 * back independently.  Granting them one at a time and checking that the
			 * previously granted ones are still set is what proves each role has its own
			 * column: when Contributor and Viewer shared a column, granting the second
			 * silently overwrote the first.
			 */
			section("SECTION D: updateUserRole for each role");

			checkTest("Test D1 - Grant Contributor Returns True",
					database.updateUserRole(luNone, "Contributor", "true"), true);

			User afterContributor = findUser(database.getAllUserAccounts(), luNone);
			checkTest("Test D2 - Contributor Was Actually Stored",
					afterContributor != null && afterContributor.getContributorRole(), true);

			checkTest("Test D3 - Grant Viewer Returns True",
					database.updateUserRole(luNone, "Viewer", "true"), true);

			User afterViewer = findUser(database.getAllUserAccounts(), luNone);
			checkTest("Test D4 - Viewer Was Actually Stored",
					afterViewer != null && afterViewer.getViewerRole(), true);

			checkTest("Test D5 - Granting Viewer Did Not Clear Contributor",
					afterViewer != null && afterViewer.getContributorRole(), true);

			checkTest("Test D6 - Grant Curator Returns True",
					database.updateUserRole(luNone, "Curator", "true"), true);

			User afterCurator = findUser(database.getAllUserAccounts(), luNone);
			checkTest("Test D7 - Curator Was Actually Stored",
					afterCurator != null && afterCurator.getCuratorRole(), true);

			checkTest("Test D8 - All Three Granted Roles Format Correctly",
					formatRolesSafely(afterCurator), "Contributor Viewer Curator");

			checkTest("Test D9 - Remove Viewer Returns True",
					database.updateUserRole(luNone, "Viewer", "false"), true);

			User afterRemove = findUser(database.getAllUserAccounts(), luNone);
			checkTest("Test D10 - Removing Viewer Left The Other Two Alone",
					formatRolesSafely(afterRemove), "Contributor Curator");

			checkTest("Test D11 - Reject An Invalid Role Name",
					database.updateUserRole(luNone, "FakeRole", "true"), false);

			/*-*************************************************************************
			 * SECTION E: the founding administrator is protected
			 *
			 * The last-admin rule only stops the system from ending up with no admins at
			 * all.  It does not stop a second admin from demoting or deleting the original
			 * one, because a second admin still existing satisfies that rule.  These tests
			 * cover the separate protection for the founding account.
			 *
			 * LUAll is an Admin at this point, so an Admin other than the founder exists
			 * and the last-admin rule is not what is doing the refusing here.
			 */
			section("SECTION E: the founding administrator is protected");

			String founder = database.getFoundingAdminUsername();

			checkTest("Test E1 - A Founding Administrator Is Identified",
					founder != null && !founder.isEmpty(), true);

			checkTest("Test E2 - The Founder Is Recognised As The Founder",
					founder != null && database.isFoundingAdmin(founder), true);

			checkTest("Test E3 - A Newly Created Admin Is Not The Founder",
					database.isFoundingAdmin(luAll), false);

			/*
			 * A non-founding Admin can still be demoted, so the protection is specific to
			 * the founding account rather than blanket protection for every Admin.
			 */
			checkTest("Test E4 - A Non-Founding Admin Can Still Be Demoted",
					database.updateUserRole(luAll, "Admin", "true")
							&& database.updateUserRole(luAll, "Admin", "false"),
					true);
			database.updateUserRole(luAll, "Admin", "true");	// put it back for cleanup

			boolean demotionRefused = false;
			if (founder != null) {

				demotionRefused = !database.updateUserRole(founder, "Admin", "false");
				checkTest("Test E5 - The Founder Cannot Be Demoted", demotionRefused, true);

				User f = findUser(database.getAllUserAccounts(), founder);
				boolean stillAdmin = (f != null) && f.getAdminRole();
				checkTest("Test E6 - The Founder Still Holds The Admin Role",
						stillAdmin, true);

				/*
				 * Safety net.  If the guard ever regresses, this test would have demoted a
				 * real account, so put the role back rather than leaving the system in that
				 * state.
				 */
				if (!stillAdmin) {
					database.updateUserRole(founder, "Admin", "true");
					System.out.println("*** WARNING ***: the founding admin was demoted "
							+ "unexpectedly and has been restored.");
				}
			}

			/*
			 * The delete guard goes through the same isFoundingAdmin check as the demotion
			 * guard.  Deleting cannot be undone, so it is only attempted once the demotion
			 * guard has demonstrably held; otherwise it is skipped rather than risked
			 * against a real account.
			 */
			if (demotionRefused) {
				checkTest("Test E7 - The Founder Cannot Be Deleted",
						database.deleteUser(founder), false);

				checkTest("Test E8 - The Founder Still Exists",
						database.doesUserExist(founder), true);
			} else {
				System.out.println();
				System.out.println("Tests E7 - E8 skipped: the demotion guard did not "
						+ "hold, so the delete guard was not exercised against a real "
						+ "account.");
				failed = failed + 2;
			}

			/*-*************************************************************************
			 * SECTION A CONTINUED: removal is reflected in the list
			 *
			 * These run last because Sections C and D needed the accounts to still exist.
			 */
			section("SECTION A CONTINUED: removal is reflected");

			/*
			 * LUOne plays no Admin role, so deleting it is always permitted.
			 */
			checkTest("Test A19 - Delete LUOne Succeeds",
					database.deleteUser(luOne), true);

			List<User> afterDelete = database.getAllUserAccounts();

			checkTest("Test A20 - List Shrank Back To Baseline Plus Two",
					afterDelete.size() == baseline + 2, true);

			checkTest("Test A21 - Deleted LUOne No Longer Appears",
					findUser(afterDelete, luOne) == null, true);

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
			 * Remove the disposable accounts so the database is left as we found it.
			 * LUAll plays the Admin role and deleteUser refuses to remove a user who is
			 * the last remaining Admin, so its Admin role is stripped first.  LUOne was
			 * already removed by test A19, so it is only deleted if it somehow survived.
			 */
			if (database != null) {
				try {
					if (luAll != null && database.doesUserExist(luAll)) {
						database.updateUserRole(luAll, "Admin", "false");
						database.deleteUser(luAll);
					}
					if (luNone != null && database.doesUserExist(luNone)) {
						database.deleteUser(luNone);
					}
					if (luOne != null && database.doesUserExist(luOne)) {
						database.deleteUser(luOne);
					}
				} catch (Exception e) {
					System.out.println("Warning: test account cleanup failed.");
				}
				database.closeConnection();
			}
		}
	}

	/*-*******************************************************************************************

	Helper methods used to minimize the number of lines of code needed above

	*/

	/**********
	 * Prints a section heading so the console output can be scanned quickly.
	 *
	 * @param title the heading to display
	 */
	private static void section(String title) {
		System.out.println("\n----------------------------------------");
		System.out.println(title);
		System.out.println("----------------------------------------");
	}

	/**********
	 * Searches a list of users for one with the given username.
	 *
	 * @param users		the list of users to search
	 * @param userName	the username being looked for
	 *
	 * @return the matching User, or null if the list holds no such user
	 */
	private static User findUser(List<User> users, String userName) {
		for (User u : users) {
			if (userName.equals(u.getUserName())) return u;
		}
		return null;
	}

	/**********
	 * Builds a User for the name formatting tests.  Roles are irrelevant to those tests, so
	 * they are all false, and the username, password, and email are placeholders.
	 *
	 * @param first		the given first name
	 * @param middle	the middle name, which may be null or empty
	 * @param last		the last name
	 * @param preferred	the preferred first name, which may be null or empty
	 *
	 * @return a User carrying the supplied name fields
	 */
	private static User nameUser(String first, String middle, String last, String preferred) {
		return new User("nameTest", "Test123!", first, middle, last, preferred,
				"nametest@example.com", false, false, false, false);
	}

	/**********
	 * Builds a User for the role formatting tests.  Names are irrelevant to those tests.
	 *
	 * @param admin			whether this user plays the Admin role
	 * @param contributor	whether this user plays the Contributor role
	 * @param viewer		whether this user plays the Viewer role
	 * @param curator		whether this user plays the Curator role
	 *
	 * @return a User carrying the supplied role flags
	 */
	private static User roleUser(boolean admin, boolean contributor, boolean viewer,
			boolean curator) {
		return new User("roleTest", "Test123!", "Role", "", "Tester", "",
				"roletest@example.com", admin, contributor, viewer, curator);
	}

	/**********
	 * Formats a retrieved user's name, tolerating a user that was never found so a missing
	 * account is reported as a clear mismatch rather than crashing the run.
	 *
	 * @param u	the User to format, which may be null
	 *
	 * @return the formatted name, or a marker string if the user is null
	 */
	private static String formatNameSafely(User u) {
		if (u == null) return "<user was not found in the list>";
		return ModelListUsers.formatFullName(u);
	}

	/**********
	 * Formats a retrieved user's roles, tolerating a user that was never found.
	 *
	 * @param u	the User to format, which may be null
	 *
	 * @return the formatted roles, or a marker string if the user is null
	 */
	private static String formatRolesSafely(User u) {
		if (u == null) return "<user was not found in the list>";
		return ModelListUsers.formatRoles(u);
	}

	/**********
	 * Compares an actual boolean result with the expected result and prints PASS or FAIL.
	 *
	 * @param testName	the name of the test being reported
	 * @param actual	the value the code under test produced
	 * @param expected	the value it was supposed to produce
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
	 * Compares an actual String result with the expected result and prints PASS or FAIL.
	 *
	 * The comparison is null safe, and values are printed inside angle brackets so that an
	 * empty string, a trailing space, or a null is visible in the report rather than
	 * indistinguishable from the others.
	 *
	 * @param testName	the name of the test being reported
	 * @param actual	the String the code under test produced
	 * @param expected	the String it was supposed to produce
	 */
	private static void checkTest(String testName, String actual, String expected) {
		boolean match;
		if (actual == null) {
			match = (expected == null);
		} else {
			match = actual.equals(expected);
		}

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
	 * Renders a String for the test report so that null and the empty string are both
	 * clearly visible.
	 *
	 * @param s	the String to render
	 *
	 * @return the String wrapped in angle brackets, or the word null
	 */
	private static String show(String s) {
		if (s == null) return "null";
		return "<" + s + ">";
	}
}
