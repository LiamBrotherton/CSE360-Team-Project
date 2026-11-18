package testing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.User;

/*******
 * <p> Title: DatabaseSecurityTestingAutomation Class. </p>
 *
 * <p> Description: Boundary value and coverage testing of the Database class for three of the
 * common weaknesses the team allocated to this tester: Missing Authentication for Critical
 * Function, Authorization Bypass Through User-Controlled Key, and Security Misconfiguration.</p>
 *
 * <p> Two kinds of check are reported, because the assignment asks for both a test run and a list of
 * defects found:
 * <br>A CHECK is a protection that must hold.  It prints PASS or FAIL, and a FAIL is a test failure.
 * <br>A DEFECT PROBE states the secure behavior the weakness calls for.  If the Database does it, the
 * probe prints PASS (no defect).  If not, it prints DEFECT FOUND with an explanation and adds the
 * probe to the list printed at the end.  A defect is a finding, not a failed test, so it does not
 * change the exit status.</p>
 *
 * <p> The tests run against the real database, which holds real accounts.  They create their own
 * users, lessons, and invitations with unique names and remove only those.  They never assume the
 * tables are empty and never touch the founding administrator.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-09 Initial version
 */
public class DatabaseSecurityTestingAutomation {

	/** The database every test reads and changes. */
	private static Database database;

	/** Checks and probes that passed. */
	private static int passed = 0;
	/** Checks that failed. */
	private static int failed = 0;
	/** Probes where a defect was found. */
	private static final List<String> defects = new ArrayList<>();

	/** A disposable Contributor, owns lessonA. */
	private static String userA;
	/** A second disposable Contributor, owns nothing. */
	private static String userB;
	/** The password given to every test account. */
	private static final String PASSWORD = "Test123!";
	/** The same database file the application opens. */
	private static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	/** A lesson owned by userA. */
	private static int lessonA = -1;
	/** A lesson owned by userA with a locked title. */
	private static int lockedLesson = -1;
	/** A lesson owned by userA with a locked body. */
	private static int lockedForDelete = -1;
	/** Invitation codes to remove at the end. */
	private static final List<String> invitationCodes = new ArrayList<>();

	/**
	 * Default constructor is not used.  Every method in this class is static.
	 */
	public DatabaseSecurityTestingAutomation() {
	}

	/*****
	 * <p> Method: void main(String[] args) </p>
	 *
	 * <p> Description: Runs every section, prints the totals and the list of defects found, and
	 * exits with status 1 only if a protection check failed. </p>
	 *
	 * @param args command-line arguments, not used
	 */
	public static void main(String[] args) {
		System.out.println("========================================");
		System.out.println("DATABASE SECURITY AUTOMATED TESTING");
		System.out.println("========================================");

		try {
			database = new Database();
			database.connectToDatabase();

			String suffix = String.valueOf(System.currentTimeMillis() % 1000000);
			userA = "SecA" + suffix;
			userB = "SecB" + suffix;
			database.register(new User(userA, PASSWORD, "Sec", "", "Alpha", "", userA + "@example.com",
					false, true, false, false));
			database.register(new User(userB, PASSWORD, "Sec", "", "Beta", "", userB + "@example.com",
					false, true, false, false));

			lessonA = database.addLessonForContributor("Security lesson A", "Body A", "Testing", userA);
			lockedLesson = database.addLessonForContributor("Locked title", "Body", "Testing", userA);
			lockedForDelete = database.addLessonForContributor("Locked body", "Body", "Testing", userA);
			database.setLessonFieldLocked(lockedLesson, LessonsLearned.FIELD_TITLE, true);
			database.setLessonFieldLocked(lockedForDelete, LessonsLearned.FIELD_BODY, true);

			sectionAuthentication();
			sectionInvitations();
			sectionKeyBypass();
			sectionMisconfiguration();
		} catch (Exception e) {
			failed++;
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		} finally {
			cleanUp();
			System.out.println();
			System.out.println("========================================");
			System.out.println("TEST SUMMARY");
			System.out.println("Passed: " + passed);
			System.out.println("Failed: " + failed);
			System.out.println("Defects found: " + defects.size());
			for (String d : defects) System.out.println("  - " + d);
			System.out.println("========================================");
			if (failed > 0) System.exit(1);
		}
	}

	/*****
	 * SECTION A: Missing Authentication for Critical Function.  Login is the one authentication
	 * check in the Database, so it is tested at its boundaries (wrong, empty, null, hostile input),
	 * and then the critical functions are probed to see whether they ask who is calling.
	 */
	private static void sectionAuthentication() {
		section("SECTION A: authentication");

		check("Test A1 - Correct Password Logs In As Contributor",
				database.loginContributor(new User(userA, PASSWORD, "", "", "", "", "", false, true, false, false)), true);
		check("Test A2 - Wrong Password Is Refused",
				database.loginContributor(new User(userA, "wrong", "", "", "", "", "", false, true, false, false)), false);
		check("Test A3 - Empty Password Is Refused",
				database.loginContributor(new User(userA, "", "", "", "", "", "", false, true, false, false)), false);
		check("Test A4 - Null Password Is Refused Without An Exception",
				database.loginContributor(new User(userA, null, "", "", "", "", "", false, true, false, false)), false);
		check("Test A5 - Empty Username Is Refused",
				database.loginContributor(new User("", PASSWORD, "", "", "", "", "", false, true, false, false)), false);
		check("Test A6 - SQL Text As Username Is Refused",
				database.loginContributor(new User("' OR '1'='1", "' OR '1'='1", "", "", "", "", "", false, true, false, false)), false);
		check("Test A7 - SQL Comment Trick Is Refused",
				database.loginContributor(new User(userA + "' --", "x", "", "", "", "", "", false, true, false, false)), false);
		check("Test A8 - A Contributor Cannot Log In As Admin",
				database.loginAdmin(new User(userA, PASSWORD, "", "", "", "", "", true, false, false, false)), false);

		// Critical functions take no caller identity, so nothing inside the Database can refuse them.
		// The probe changes a disposable account's password with no credentials at all.
		database.updatePassword(userB, "Hijacked1!");
		probe("Probe A9 - updatePassword Needs To Know Who Is Asking",
				database.loginContributor(new User(userB, "Hijacked1!", "", "", "", "", "", false, true, false, false)),
				false, "updatePassword changed another account's password with no authentication; "
				+ "any code holding a Database reference can take over any account");

		// Same question for granting the Admin role
		database.updateUserRole(userB, "Admin", "true");
		database.getUserAccountDetails(userB);
		boolean escalated = database.getCurrentAdminRole();
		probe("Probe A10 - updateUserRole Needs To Know Who Is Asking", escalated, false,
				"updateUserRole made a Contributor an Admin with no authentication (privilege escalation)");
		database.updateUserRole(userB, "Admin", "false");

		// A failed lookup should not leave the previous account's details loaded
		database.getUserAccountDetails(userA);
		boolean found = database.getUserAccountDetails("NoSuchUser" + System.nanoTime());
		check("Test A11 - Lookup Of An Unknown User Reports Failure", found, false);
		probe("Probe A12 - Failed Lookup Clears The Previous User's Details",
				userA.equals(database.getCurrentUsername()), false,
				"after a failed lookup getCurrentUsername still returns the previous user, so code that "
				+ "forgets to check the return value acts as that user");
	}

	/*****
	 * SECTION A2: invitation codes, the other credential the Database issues.
	 */
	private static void sectionInvitations() {
		section("SECTION A2: invitation codes");

		// Contributor is the longest role name (11 characters).  An existing database file made
		// before the role column was widened keeps the old 10 character column, because
		// CREATE TABLE IF NOT EXISTS does not change an existing table.
		String contributorCode = database.generateInvitationCode(userA + "-long@example.com", "Contributor");
		if (contributorCode != null) invitationCodes.add(contributorCode);
		probe("Probe I0 - An Invitation Can Be Issued For The Longest Role Name",
				contributorCode != null, true,
				"inviting a Contributor failed on this database file: the InvitationCodes role column is "
				+ "still VARCHAR(10) because the table existed before the fix and no ALTER TABLE migrates it");

		// The checks below use Viewer so they test the code's logic even on a database with that column
		String email = userA + "-inv@example.com";
		String code = database.generateInvitationCode(email, "Viewer");
		if (code != null) invitationCodes.add(code);

		check("Test I1 - A Code Is Issued", code != null, true);
		check("Test I2 - Code Is Six Lowercase Hex Characters",
				code != null && code.matches("[0-9a-f]{6}"), true);
		check("Test I3 - The Code Maps To Its Role",
				database.getRoleGivenAnInvitationCode(code), "Viewer");
		check("Test I4 - The Code Maps To Its Email",
				database.getEmailAddressUsingCode(code), email);

		// Fifty codes should not collide; 16^6 possible values makes a collision very unlikely.
		Set<String> seen = new HashSet<>();
		boolean unique = true;
		for (int i = 0; i < 50; i++) {
			String c = database.generateInvitationCode(userA + "-bulk" + i + "@example.com", "Viewer");
			if (c == null) { unique = false; break; }
			invitationCodes.add(c);
			if (!seen.add(c)) unique = false;
		}
		check("Test I5 - Fifty Codes Are All Different", unique, true);

		check("Test I6 - An Unknown Code Has No Role", database.getRoleGivenAnInvitationCode("zzzzzz"), "");
		check("Test I7 - An Empty Code Has No Role", database.getRoleGivenAnInvitationCode(""), "");
		check("Test I8 - SQL Text As A Code Has No Role",
				database.getRoleGivenAnInvitationCode("' OR '1'='1"), "");

		database.removeInvitationAfterUse(code);
		check("Test I9 - A Used Code Is Gone", database.getRoleGivenAnInvitationCode(code), "");
		database.removeInvitationAfterUse(code);
		check("Test I10 - Removing A Used Code Again Does Not Fail", true, true);

		String bad = database.generateInvitationCode(userA + "-bad@example.com", "Hacker");
		if (bad != null) invitationCodes.add(bad);
		probe("Probe I11 - An Invitation For A Non-Existent Role Is Refused", bad == null, true,
				"generateInvitationCode stored a code for role \"Hacker\"; ControllerNewAccount calls "
				+ "System.exit when it meets an unknown role, so using that code closes the application");
	}

	/*****
	 * SECTION B: Authorization Bypass Through User-Controlled Key.  The lesson ID is the key a
	 * person controls.  The owner-checked methods must hold at every ID; the ID-only methods are
	 * probed to show where the protection stops.
	 */
	private static void sectionKeyBypass() {
		section("SECTION B: lesson ID as a key");

		check("Test B1 - Owner Can Read Their Lesson", database.getLessonForContributor(lessonA, userA) != null, true);
		check("Test B2 - Another Contributor Cannot Read It", database.getLessonForContributor(lessonA, userB) == null, true);
		check("Test B3 - Another Contributor Cannot Change It",
				database.updateContributorLessonTitle(lessonA, userB, "Stolen"), false);
		check("Test B4 - Title Was Not Changed By The Refused Update",
				database.getLessonById(lessonA).getTitle(), "Security lesson A");
		check("Test B5 - Another Contributor Cannot Delete It",
				database.deleteLessonForContributor(lessonA, userB), false);
		check("Test B6 - The Lesson Still Exists", database.lessonExists(lessonA), true);
		check("Test B7 - Null Owner Is Refused", database.getLessonForContributor(lessonA, null) == null, true);
		check("Test B8 - Blank Owner Is Refused", database.getLessonForContributor(lessonA, "  ") == null, true);

		int[] boundary = { 0, -1, 1, Integer.MAX_VALUE, Integer.MIN_VALUE };
		for (int id : boundary) {
			if (id == lessonA) continue;	// an ID that happens to be the test lesson is not a boundary miss
			check("Test B9 - ID " + id + " Gives No Lesson To The Owner-Checked Read",
					database.getLessonForContributor(id, userB) == null, true);
		}

		// The owner-checked update refuses a locked field and the ID-only update does not.
		check("Test B10 - Owner-Checked Update Refuses A Locked Title",
				database.updateContributorLessonTitle(lockedLesson, userA, "Changed"), false);

		boolean changed = database.updateLessonTitle(lockedLesson, "Changed by ID");
		probe("Probe B11 - ID-Only Update Respects A Locked Field", changed, false,
				"updateLessonTitle(id, title) changed a locked title; the lock is enforced only in the "
				+ "owner-checked methods, so any caller using the ID-only method bypasses it");

		boolean deleted = database.deleteLesson(lockedForDelete);
		if (deleted) lockedForDelete = -1;
		probe("Probe B12 - ID-Only Delete Respects A Locked Field", deleted, false,
				"deleteLesson(id) removed a lesson with a locked body; the 'locked lessons cannot be "
				+ "deleted' rule is enforced only in deleteLessonForContributor");

		boolean tooLong = database.updateLessonTitle(lessonA, "x".repeat(300));
		probe("Probe B13 - ID-Only Update Validates Length", tooLong, false,
				"updateLessonTitle accepted a 300 character title; length is enforced by LessonValidator "
				+ "in the owner-checked path only");
	}

	/*****
	 * SECTION C: Security Misconfiguration.  The Database opens a file in the user's home folder
	 * with H2's default account, so anything that can read the folder can open it.
	 */
	private static void sectionMisconfiguration() {
		section("SECTION C: configuration");

		boolean opened = false;
		String storedPassword = null;
		try (Connection other = DriverManager.getConnection(DB_URL, "sa", "")) {
			opened = true;
			try (PreparedStatement ps = other.prepareStatement("SELECT password FROM userDB WHERE userName = ?")) {
				ps.setString(1, userA);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) storedPassword = rs.getString(1);
			}
		} catch (Exception e) {
			System.out.println("(second connection failed: " + e.getMessage() + ")");
		}

		probe("Probe C1 - The Database Refuses The Default Account", opened, false,
				"a second connection opened with user \"sa\" and an empty password; the credentials are "
				+ "H2's defaults and are also written in Database.java");
		probe("Probe C2 - Stored Passwords Are Not Readable As Text", PASSWORD.equals(storedPassword), false,
				"the password column holds the plain text password, readable by anyone who can open the file");
	}

	/*****
	 * Removes every disposable record this run created and closes the connection.
	 */
	private static void cleanUp() {
		if (database == null) return;
		try {
			for (String c : invitationCodes) database.removeInvitationAfterUse(c);
			if (lessonA > 0) database.deleteLesson(lessonA);
			if (lockedLesson > 0) database.deleteLesson(lockedLesson);
			if (lockedForDelete > 0) database.deleteLesson(lockedForDelete);
			if (userA != null) database.deleteUser(userA);
			if (userB != null) database.deleteUser(userB);
		} catch (Exception e) {
			System.out.println("Warning: cleanup failed: " + e.getMessage());
		}
		database.closeConnection();
	}

	/*****
	 * Prints a section heading so the output can be scanned.
	 *
	 * @param title the heading to print
	 */
	private static void section(String title) {
		System.out.println("\n----------------------------------------");
		System.out.println(title);
		System.out.println("----------------------------------------");
	}

	/*****
	 * A protection that must hold: prints PASS or FAIL and counts a failure.
	 *
	 * @param name		the test's name
	 * @param actual	what the code produced
	 * @param expected	what it should have produced
	 */
	private static void check(String name, boolean actual, boolean expected) {
		System.out.println("\n" + name);
		System.out.println("Expected: " + expected);
		System.out.println("Actual:   " + actual);
		if (actual == expected) { System.out.println("Result:   PASS"); passed++; }
		else { System.out.println("Result:   FAIL"); failed++; }
	}

	/*****
	 * A protection that must hold, for a String result, null safe, values shown in angle brackets.
	 *
	 * @param name		the test's name
	 * @param actual	what the code produced
	 * @param expected	what it should have produced
	 */
	private static void check(String name, String actual, String expected) {
		boolean match = (actual == null) ? expected == null : actual.equals(expected);
		System.out.println("\n" + name);
		System.out.println("Expected: <" + expected + ">");
		System.out.println("Actual:   <" + actual + ">");
		if (match) { System.out.println("Result:   PASS"); passed++; }
		else { System.out.println("Result:   FAIL"); failed++; }
	}

	/*****
	 * A defect probe: states the secure value.  A mismatch is recorded as a defect, not a failure.
	 *
	 * @param name			the probe's name
	 * @param actual		what the code produced
	 * @param secure		what a secure implementation would produce
	 * @param explanation	why a mismatch is a defect, printed and added to the list
	 */
	private static void probe(String name, boolean actual, boolean secure, String explanation) {
		System.out.println("\n" + name);
		System.out.println("Secure behavior: " + secure);
		System.out.println("Actual:          " + actual);
		if (actual == secure) {
			System.out.println("Result:          PASS (no defect)");
			passed++;
		} else {
			System.out.println("Result:          DEFECT FOUND - " + explanation);
			defects.add(name + ": " + explanation);
		}
	}
}
