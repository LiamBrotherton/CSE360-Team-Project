package lessonManagement;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import database.Database;
import entityClasses.Lesson;
import entityClasses.LessonList;

/*******
 * <p> Title: HW2Main Class. </p>
 *
 * <p> Description: The HW2 application: a console front end for the lessons learned Create, Read,
 * Update, and Delete (CRUD) operations.  It exists to let a person exercise every operation and
 * every error message by hand, and to show that lessons persist from one execution to the next.
 * It is not meant to replace the JavaFX pages the team project will eventually need.
 *
 * This class contains no rules.  Every operation goes through ControllerLessons, which validates
 * the input and enforces who may do what, so what appears here is what a JavaFX page built later
 * would also get.  The only thing this class decides is how to show the result.
 *
 * The person types the username of an existing account to act as.  There is no password prompt:
 * logging in is the existing login page's job and is not part of this homework, and what is
 * being demonstrated is what a user may do once signed in.
 *
 * The application uses the same H2 database file as the rest of the project, so the usernames
 * accepted here are the accounts that already exist there.  Only one program can have that file
 * open at a time, so the main application must be closed while this runs.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-04 Initial version
 */
public class HW2Main {

	private static final Database database = applicationMain.FoundationsMain.database;
	private static final Scanner in = new Scanner(System.in);

	/**
	 * Default constructor is not used.  This class only holds the main method.
	 */
	public HW2Main() {
	}

	/**********
	 * <p> Method: main(String[] args) </p>
	 *
	 * <p> Description: Connect to the database, ask who is acting, and run the menu until the
	 * person chooses to quit. </p>
	 *
	 * @param args the command line parameters.  These are not used.
	 */
	public static void main(String[] args) {
		try {
			database.connectToDatabase();
		} catch (SQLException e) {
			System.out.println("*** ERROR *** The database could not be opened: " + e.getMessage());
			System.out.println("If the main application is running, close it and try again.");
			return;
		}

		System.out.println("========================================");
		System.out.println("HW2: LESSONS LEARNED CRUD");
		System.out.println("========================================");
		System.out.println("Lessons already stored: " + database.getNumberOfLessons());

		String user = askForUser();
		if (user != null) runMenu(user);

		database.closeConnection();
		System.out.println("Goodbye.");
	}

	/*
	 * Keeps asking for a username until an existing account is named, or the person gives up by
	 * entering nothing.  Returns null when they give up.
	 */
	private static String askForUser() {
		while (true) {
			String name = prompt("\nUsername to act as (blank to quit)");
			if (name.isEmpty()) return null;
			if (database.doesUserExist(name)) return name;
			System.out.println("*** ERROR *** There is no user named \"" + name + "\".");
		}
	}

	/*
	 * Shows the menu and carries out the chosen operation, repeatedly.
	 */
	private static void runMenu(String user) {
		boolean running = true;
		while (running) {
			System.out.println("\n--- Signed in as " + user + " ---");
			System.out.println(" Contributor:  1 Add a lesson");
			System.out.println("               2 List my lessons");
			System.out.println("               3 View one of my lessons");
			System.out.println("               4 Update one of my lessons");
			System.out.println("               5 Delete one of my lessons");
			System.out.println(" Viewer:       6 List all lessons");
			System.out.println("               7 Search lessons by keyword");
			System.out.println("               8 View any lesson");
			System.out.println("               0 Quit");
			switch (prompt("Choice")) {
			case "1": addLesson(user); break;
			case "2": printList("Your lessons", ControllerLessons.listMyLessons(user)); break;
			case "3": view(ControllerLessons.viewMyLesson(user, askForId())); break;
			case "4": updateLesson(user); break;
			case "5": deleteLesson(user); break;
			case "6": printList("All lessons", ControllerLessons.listAllLessons()); break;
			case "7": search(); break;
			case "8": view(ControllerLessons.viewLesson(askForId())); break;
			case "0": running = false; break;
			default: System.out.println("*** ERROR *** Enter a number from the menu.");
			}
		}
	}

	private static void addLesson(String user) {
		System.out.println("Categories: " + String.join(", ", Lesson.CATEGORIES));
		String title = prompt("Title");
		String situation = prompt("Situation (what happened)");
		String lessonText = prompt("Lesson (what was learned)");
		String category = prompt("Category");
		String tags = prompt("Tags, comma separated (optional)");
		int id = ControllerLessons.addLesson(user, title, situation, lessonText, category, tags);
		if (id < 0) System.out.println("*** NOT ADDED *** " + ControllerLessons.lastErrorMessage);
		else System.out.println("Lesson added as number " + id + ".");
	}

	private static void updateLesson(String user) {
		int id = askForId();
		Lesson current = ControllerLessons.viewMyLesson(user, id);
		if (current == null) {
			System.out.println("*** NOT UPDATED *** " + ControllerLessons.lastErrorMessage);
			return;
		}
		System.out.println("Press Enter on any line to keep the value shown in brackets.");
		String title = promptKeep("Title", current.getTitle());
		String situation = promptKeep("Situation", current.getSituation());
		String lessonText = promptKeep("Lesson", current.getLessonText());
		String category = promptKeep("Category", current.getCategory());
		String tags = promptKeep("Tags", current.getTags());
		if (ControllerLessons.updateLesson(user, id, title, situation, lessonText, category, tags))
			System.out.println("Lesson " + id + " updated.");
		else System.out.println("*** NOT UPDATED *** " + ControllerLessons.lastErrorMessage);
	}

	private static void deleteLesson(String user) {
		int id = askForId();
		Lesson current = ControllerLessons.viewMyLesson(user, id);
		if (current == null) {
			System.out.println("*** NOT DELETED *** " + ControllerLessons.lastErrorMessage);
			return;
		}
		if (!prompt("Delete \"" + current.getTitle() + "\"? Type Yes to confirm").equals("Yes")) {
			System.out.println("Cancelled. Nothing was deleted.");
			return;
		}
		if (ControllerLessons.deleteLesson(user, id)) System.out.println("Lesson " + id + " deleted.");
		else System.out.println("*** NOT DELETED *** " + ControllerLessons.lastErrorMessage);
	}

	private static void search() {
		String keyword = prompt("Keyword or phrase");
		LessonList results = ControllerLessons.searchLessons(keyword);
		if (!ControllerLessons.lastErrorMessage.isEmpty())
			System.out.println("*** NO SEARCH *** " + ControllerLessons.lastErrorMessage);
		else printList("Lessons matching \"" + keyword.trim() + "\"", results);
	}

	/*
	 * Reads a lesson number.  A value that is not a number becomes -1, which no lesson has, so
	 * the controller reports it as "no such lesson" in its usual words.
	 */
	private static int askForId() {
		try {
			return Integer.parseInt(prompt("Lesson number"));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private static void view(Lesson l) {
		if (l == null) {
			System.out.println("*** ERROR *** " + ControllerLessons.lastErrorMessage);
			return;
		}
		System.out.println("\nLesson " + l.getId() + ": " + l.getTitle());
		System.out.println("  Category:  " + l.getCategory());
		System.out.println("  Tags:      " + (l.getTags().isEmpty() ? "(none)" : l.getTags()));
		System.out.println("  Added by:  " + l.getAuthor() + " on "
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(l.getCreatedAt())));
		System.out.println("  Situation: " + l.getSituation());
		System.out.println("  Lesson:    " + l.getLessonText());
	}

	private static void printList(String heading, LessonList list) {
		System.out.println("\n" + heading + ": " + list.size());
		if (list.isEmpty()) System.out.println("  (no lessons)");
		for (Lesson l : list.asList())
			System.out.println("  " + l.getId() + "  [" + l.getCategory() + "]  " + l.getTitle()
					+ "  (" + l.getAuthor() + ")");
	}

	private static String prompt(String label) {
		System.out.print(label + ": ");
		return in.hasNextLine() ? in.nextLine().trim() : "";
	}

	private static String promptKeep(String label, String current) {
		String entered = prompt(label + " [" + current + "]");
		return entered.isEmpty() ? current : entered;
	}
}
