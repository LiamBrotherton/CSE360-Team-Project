package guiListUsers;

import entityClasses.User;

/*******
 * <p> Title: ModelListUsers Class. </p>
 *
 * <p> Description: The List Users Page Model.  This class holds the display formatting
 * logic for the List Users page: turning a User entity's separate name fields into the
 * single name string shown in the table, and turning a User's four role flags into the
 * single roles string shown in the table.
 *
 * This logic previously lived inline inside the TableColumn cell value factory lambdas in
 * ViewListUsers.  It was moved here so that the testing automation can invoke precisely
 * the same formatting code that the interactive JavaFX page uses, rather than a copy of
 * it.  A copy could drift from the View without any test noticing.
 *
 * Both methods are declared "public static" so the testing automation in another package
 * can call them, following the same pattern as
 * fPasswordPopUpWindow.Model.evaluatePassword(), which the password evaluation testing
 * automation calls in the same way.
 *
 * This class deliberately has no JavaFX imports, so these methods can be tested without
 * starting the JavaFX runtime.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-09-17 Initial version, extracted from ViewListUsers so the
 * 							formatting logic is reachable by the testing automation
 * @version 2.00		2026-09-19 Updated for the Admin / Contributor / Viewer / Curator
 * 							roles that replaced Admin / Role1 / Role2
 */
public class ModelListUsers {

    /**
     * Default constructor is not used.  Both methods in this class are static.
     */
    public ModelListUsers() {
    }

    /**********
     * <p> Method: formatFullName(User u) </p>
     *
     * <p> Description: Combines a user's name fields into the single string displayed in
     * the Name column of the List Users table.
     *
     * If the user has specified a preferred first name, that is shown instead of their
     * given first name; otherwise the given first name is used.  The middle name is only
     * included if one was provided, so a missing middle name does not leave an extra space
     * in the displayed name.  Both the preferred first name and the middle name may be
     * null or empty if they were never set.
     *
     * Note that this is deliberately not the simpler
     * "firstName + middleName + lastName" concatenation.  That version ignores the
     * preferred first name entirely, and renders a user who never supplied a middle name
     * as "John null Smith", because getMiddleName() returns null rather than an empty
     * string for those accounts.
     *
     * The given first name and the last name are not checked for null, so a user with no
     * last name stored will still display as "First null".  That matches the behaviour of
     * the original code and is intentional: this method was extracted without changing
     * what it does.</p>
     *
     * @param u specifies the User whose name is to be formatted
     *
     * @return a String containing the name to display for this user
     *
     */
    public static String formatFullName(User u) {

        String preferredFirst = u.getPreferredFirstName();
        String firstToShow;
        if (preferredFirst != null && !preferredFirst.isEmpty()) {
            firstToShow = preferredFirst;
        } else {
            firstToShow = u.getFirstName();
        }

        String middle = u.getMiddleName();
        String fullName;
        if (middle != null && !middle.isEmpty()) {
            fullName = firstToShow + " " + middle + " " + u.getLastName();
        } else {
            fullName = firstToShow + " " + u.getLastName();
        }

        return fullName;
    }

    /**********
     * <p> Method: formatRoles(User u) </p>
     *
     * <p> Description: Builds a readable string of every role this user currently plays,
     * for display in the Roles column of the List Users table.  The roles always appear in
     * a fixed order (Admin, Contributor, Viewer, then Curator) so the column reads
     * consistently.
     *
     * A user who plays no roles at all produces the empty string.</p>
     *
     * @param u specifies the User whose roles are to be formatted
     *
     * @return a String of the space-separated role names, with no leading or trailing space
     *
     */
    public static String formatRoles(User u) {

        StringBuilder roles = new StringBuilder();
        if (u.getAdminRole()) roles.append("Admin ");
        if (u.getContributorRole()) roles.append("Contributor ");
        if (u.getViewerRole()) roles.append("Viewer ");
        if (u.getCuratorRole()) roles.append("Curator ");
        return roles.toString().trim();
    }
}
