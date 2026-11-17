package testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import guiTools.TimeEffortVisibility;
import entityClasses.User;

/*******
 * <p> Title: TimeEffortVisibilityTest Class. </p>
 *
 * <p> Description: JUnit 5 tests for guiTools.TimeEffortVisibility.  The tests match the
 * test cases in "TP3 Testing Details and Rationale.pdf" for requirements R3 and R4: a
 * Contributor must not see hidden time/effort data, a Curator always sees it, and shown data
 * is visible to everyone. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Liam Brotherton
 *
 * @version 1.00		2026-11-11 Initial version
 */
public class TimeEffortVisibilityTest {

	// The values from Lesson 1 in the test plan
	private static final String DURATION = "2 hours";
	private static final String EFFORT = "Medium";

	// Builds a User who plays only the Contributor role
	private User contributor() {
		return new User("contrib", "Pass1!word", "Con", "", "Tributor", "", "con@asu.edu",
				false, true, false, false);
	}

	// Builds a User who plays only the Curator role
	private User curator() {
		return new User("curator", "Pass1!word", "Cur", "", "Ator", "", "cur@asu.edu",
				false, false, false, true);
	}

	/** Test 3: A Contributor gets empty values when the data is hidden. */
	@Test
	public void test3_contributorDoesNotSeeHiddenData() {
		assertEquals("", TimeEffortVisibility.visibleValue(DURATION, true, contributor()));
		assertEquals("", TimeEffortVisibility.visibleValue(EFFORT, true, contributor()));
	}

	/** Test 4: A Curator still gets the values when the data is hidden. */
	@Test
	public void test4_curatorSeesHiddenData() {
		assertEquals(DURATION, TimeEffortVisibility.visibleValue(DURATION, true, curator()));
		assertEquals(EFFORT, TimeEffortVisibility.visibleValue(EFFORT, true, curator()));
	}

	/** Test 5 (boundary): Null and empty values give an empty String and do not throw. */
	@Test
	public void test5_nullAndEmptyValuesGiveEmptyString() {
		// Hidden, Contributor viewer
		assertEquals("", TimeEffortVisibility.visibleValue(null, true, contributor()));
		assertEquals("", TimeEffortVisibility.visibleValue("", true, contributor()));
		// Shown, Contributor viewer
		assertEquals("", TimeEffortVisibility.visibleValue(null, false, contributor()));
		assertEquals("", TimeEffortVisibility.visibleValue("", false, contributor()));
		// Hidden, Curator viewer
		assertEquals("", TimeEffortVisibility.visibleValue(null, true, curator()));
	}

	/** Test 6: A Contributor gets the values unchanged when the data is shown. */
	@Test
	public void test6_contributorSeesShownData() {
		assertEquals(DURATION, TimeEffortVisibility.visibleValue(DURATION, false, contributor()));
		assertEquals(EFFORT, TimeEffortVisibility.visibleValue(EFFORT, false, contributor()));
	}

	/** Extra: A user who is both a Contributor and a Curator is treated as a Curator. */
	@Test
	public void bothRolesIsTreatedAsCurator() {
		User both = new User("both", "Pass1!word", "Bo", "", "Th", "", "both@asu.edu",
				false, true, false, true);
		assertEquals(DURATION, TimeEffortVisibility.visibleValue(DURATION, true, both));
	}

	/** Extra: A null viewer is not a Curator, so hidden data stays hidden (fails closed). */
	@Test
	public void nullViewerDoesNotSeeHiddenData() {
		assertEquals("", TimeEffortVisibility.visibleValue(DURATION, true, null));
	}

	/** Extra: A user with no roles does not see hidden data. */
	@Test
	public void userWithNoRolesDoesNotSeeHiddenData() {
		User none = new User("none", "Pass1!word", "No", "", "Roles", "", "none@asu.edu",
				false, false, false, false);
		assertEquals("", TimeEffortVisibility.visibleValue(DURATION, true, none));
	}
}