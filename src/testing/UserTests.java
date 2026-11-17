package testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import entityClasses.User;

/*******
 * <p> Title: UserTest Class. </p>
 *
 * <p> Description: JUnit 5 tests for entityClasses.User.  Each test method covers one of the
 * requirements (R1 to R7) in the TP2 Test Designs outline, plus the boundary values listed there.
 * The tests need no database or JavaFX, so they run on their own.</p>
 *
 * @author Liam Brotherton
 *
 * @version 1.00
 */
public class UserTests {

	// A password with a known value, so the tests can look for it in other output
	private static final String PASSWORD = "Password1!";

	// Builds a User with every field filled in and the given roles
	private User makeUser(boolean admin, boolean contributor, boolean viewer, boolean curator) {
		return new User("jdoe", PASSWORD, "John", "Q", "Doe", "Johnny", "jdoe@asu.edu",
				admin, contributor, viewer, curator);
	}

	/** R1: The constructor stores every field and each getter returns it. */
	@Test
	public void r1_constructorStoresFields() {
		User u = makeUser(true, false, true, false);
		assertEquals("jdoe", u.getUserName());
		assertEquals(PASSWORD, u.getPassword());
		assertEquals("John", u.getFirstName());
		assertEquals("Q", u.getMiddleName());
		assertEquals("Doe", u.getLastName());
		assertEquals("Johnny", u.getPreferredFirstName());
		assertEquals("jdoe@asu.edu", u.getEmailAddress());
		assertTrue(u.getAdminRole());
		assertFalse(u.getContributorRole());
		assertTrue(u.getViewerRole());
		assertFalse(u.getCuratorRole());
	}

	/** R2: Each setter changes only its own field. */
	@Test
	public void r2_settersChangeOnlyTheirField() {
		User u = makeUser(false, false, false, false);
		u.setUserName("newName");
		assertEquals("newName", u.getUserName());
		assertEquals(PASSWORD, u.getPassword());          // other fields unchanged
		assertEquals("John", u.getFirstName());

		u.setFirstName("Jane");
		u.setMiddleName("R");
		u.setLastName("Roe");
		u.setPreferredFirstName("Janie");
		u.setEmailAddress("jane@asu.edu");
		assertEquals("Jane", u.getFirstName());
		assertEquals("R", u.getMiddleName());
		assertEquals("Roe", u.getLastName());
		assertEquals("Janie", u.getPreferredFirstName());
		assertEquals("jane@asu.edu", u.getEmailAddress());
		assertEquals("newName", u.getUserName());          // still the earlier change
	}

	/** R3: Each role can be set on its own without changing the others. */
	@Test
	public void r3_rolesAreIndependent() {
		User u = makeUser(false, false, false, false);

		u.setAdminRole(true);
		assertTrue(u.getAdminRole());
		assertFalse(u.getContributorRole());
		assertFalse(u.getViewerRole());
		assertFalse(u.getCuratorRole());

		u.setContributorUser(true);
		assertTrue(u.getContributorRole());
		assertFalse(u.getViewerRole());
		assertFalse(u.getCuratorRole());

		u.setViewerUser(true);
		assertTrue(u.getViewerRole());
		assertFalse(u.getCuratorRole());

		u.setCuratorUser(true);
		assertTrue(u.getCuratorRole());

		// Boundary: set a role to true and then back to false
		u.setAdminRole(false);
		assertFalse(u.getAdminRole());
		assertTrue(u.getContributorRole());
	}

	/** R4: getNumRoles counts the true roles (boundary: 0, 1, 2, 3, and 4 roles). */
	@Test
	public void r4_numRolesCountsTrueRoles() {
		assertEquals(0, makeUser(false, false, false, false).getNumRoles());
		assertEquals(1, makeUser(true, false, false, false).getNumRoles());
		assertEquals(2, makeUser(true, true, false, false).getNumRoles());
		assertEquals(3, makeUser(true, true, true, false).getNumRoles());
		assertEquals(4, makeUser(true, true, true, true).getNumRoles());
	}

	/** R5: Printing a User does not expose the password. */
	@Test
	public void r5_toStringDoesNotContainPassword() {
		User u = makeUser(true, true, true, true);
		assertFalse(u.toString().contains(PASSWORD));
	}

	/** R6: Only getPassword returns the password; no other getter does. */
	@Test
	public void r6_onlyGetPasswordReturnsPassword() {
		User u = makeUser(true, false, false, false);
		assertEquals(PASSWORD, u.getPassword());
		assertFalse(PASSWORD.equals(u.getUserName()));
		assertFalse(PASSWORD.equals(u.getFirstName()));
		assertFalse(PASSWORD.equals(u.getMiddleName()));
		assertFalse(PASSWORD.equals(u.getLastName()));
		assertFalse(PASSWORD.equals(u.getPreferredFirstName()));
		assertFalse(PASSWORD.equals(u.getEmailAddress()));
	}

	/** R7: setPassword replaces the old password completely. */
	@Test
	public void r7_setPasswordReplacesOldPassword() {
		User u = makeUser(false, false, false, false);
		u.setPassword("Another2@");
		assertEquals("Another2@", u.getPassword());
		assertFalse(PASSWORD.equals(u.getPassword()));
	}

	/** R7 boundary: password lengths of 0, 1, 255, and 256 characters. */
	@Test
	public void r7_passwordLengthBoundaries() {
		User u = makeUser(false, false, false, false);
		String[] passwords = { "", "a", "a".repeat(255), "a".repeat(256) };
		for (String p : passwords) {
			u.setPassword(p);
			// No length limit is enforced by User, so every value comes back unchanged.
			// (The 256-character case is a finding: it is larger than the database column.)
			assertEquals(p, u.getPassword());
		}
	}

	/** Boundary: null values are returned as null and nothing throws an exception. */
	@Test
	public void nullValuesAreReturnedAsNull() {
		User u = new User(null, null, null, null, null, null, null, false, false, false, false);
		assertNull(u.getUserName());
		assertNull(u.getPassword());
		assertNull(u.getFirstName());
		assertNull(u.getMiddleName());
		assertNull(u.getLastName());
		assertNull(u.getPreferredFirstName());
		assertNull(u.getEmailAddress());
	}

	/** Coverage: the default constructor leaves every field empty and every role false. */
	@Test
	public void defaultConstructorLeavesFieldsEmpty() {
		User u = new User();
		assertNull(u.getUserName());
		assertNull(u.getPassword());
		assertEquals(0, u.getNumRoles());
	}
}