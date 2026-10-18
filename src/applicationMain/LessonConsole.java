package applicationMain;

import java.util.Scanner;

import database.Database;
import entityClasses.Lesson;
import entityClasses.LessonList;
import inputRecognizer.LessonValidator;

/**
 * The LessonConsole class provides a command-line interface (CLI) for users 
 * to interact with the lesson database. It handles menu options, user input, 
 * validation, and database operations.
 */
public class LessonConsole {

	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private LessonConsole() {}

	/**
	 * Starts the console interface on a separate daemon thread so it runs 
	 * concurrently with the main application without blocking program shutdown.
	 * 
	 * @param db The database instance used for lesson operations.
	 */
	public static void start(Database db) {
		Thread consoleThread = new Thread(() -> run(db));
		consoleThread.setDaemon(true);
		consoleThread.start();
	}

	/**
	 * Displays the menu loop and routes user choices to the correct handler methods.
	 * 
	 * @param db The database instance being accessed and modified.
	 */
	private static void run(Database db) {
		Scanner in = new Scanner(System.in);	
		while (true) {
			System.out.println();
			System.out.println("1. Add lesson");
			System.out.println("2. View all lessons");
			System.out.println("3. Examine one lesson");
			System.out.println("4. Update title / body / category");
			System.out.println("5. Add experience information");
			System.out.println("6. Lock or unlock a field");
			System.out.println("7. Delete lesson");
			System.out.println("8. Clear all lessons (testing only)");
			System.out.println("9. Exit console");
			
			String choice = ask(in, "Enter choice: ");
			if (choice == null) return;

			// Switch statement routes the user's menu choice to the appropriate helper method
			switch (choice.trim()) {
				case "1": addLesson(in, db); break;
				case "2": viewAll(db); break;
				case "3": examine(in, db); break;
				case "4": update(in, db); break;
				case "5": addExperience(in, db); break;
				case "6": lockUnlock(in, db); break;
				case "7": delete(in, db); break;
				case "8": db.clearAllLessons(); System.out.println("All lessons cleared."); break;
				case "9": System.out.println("Console closed."); return;
				default: System.out.println("Please enter a number from 1 to 9.");
			}
		}
	}

	/**
	 * Helper method to print a prompt and safely read a line of input.
	 * 
	 * @param in     The Scanner object used for reading input.
	 * @param prompt The message displayed to the user.
	 * @return       The entered string, or null if the input stream has ended.
	 */
	private static String ask(Scanner in, String prompt) {
		System.out.print(prompt);
		return in.hasNextLine() ? in.nextLine() : null;
	}

	/**
	 * Prompts for a lesson ID, validates format and existence, and fetches the lesson.
	 * 
	 * @param in The Scanner object for reading input.
	 * @param db The database instance to query.
	 * @return   The retrieved Lesson object, or null if validation fails.
	 */
	private static Lesson askForLesson(Scanner in, Database db) {
		String idText = ask(in, "Enter lesson ID: ");
		if (idText == null) return null;
		
		// Validate that the input is a proper ID format
		String err = LessonValidator.checkLessonIdText(idText);
		if (!err.isEmpty()) { System.out.println(err); return null; }
		
		int id = Integer.parseInt(idText.trim());
		Lesson lesson = db.getLessonById(id);
		
		// Verify the lesson actually exists in the database
		err = LessonValidator.checkLessonFound(lesson, id);
		if (!err.isEmpty()) { System.out.println(err); return null; }
		return lesson;
	}

	/**
	 * Prompts the user to specify a lockable field name.
	 * 
	 * @param in The Scanner object for reading input.
	 * @return   The lowercased field name string.
	 */
	private static String askForField(Scanner in) {
		String f = ask(in, "Field (title, body, category): ");
		if (f == null) return null;
		return f.trim().toLowerCase();
	}

	/**
	 * Guides the user through adding a new lesson, validating inputs before saving.
	 * 
	 * @param in The Scanner object for reading input.
	 * @param db The database instance where the lesson will be saved.
	 */
	private static void addLesson(Scanner in, Database db) {
		String title = ask(in, "Enter title: ");
		String body = ask(in, "Enter body: ");
		String category = ask(in, "Enter category: ");
		if (title == null || body == null || category == null) return;

		// Validate fields sequentially using the LessonValidator utility
		String err = LessonValidator.checkTitle(title);
		if (err.isEmpty()) err = LessonValidator.checkBody(body);
		if (err.isEmpty()) err = LessonValidator.checkCategory(category);
		if (!err.isEmpty()) { System.out.println(err); return; }

		int id = db.addLesson(title, body, category);
		if (id < 0) System.out.println("The lesson could not be saved.");
		else System.out.println("Lesson saved to database with ID " + id);
	}

	/**
	 * Retrieves and displays a summary list of all lessons in the database.
	 * 
	 * @param db The database instance to query.
	 */
	private static void viewAll(Database db) {
		LessonList list = db.getAllLessons();
		if (list.isEmpty()) { System.out.println(list.getMessage()); return; }
		
		System.out.println(list.size() + " lesson(s):");
		for (Lesson l : list.getLessons()) {
			System.out.println("  [" + l.getId() + "] " + l.getTitle() + "  (" + l.getCategory() + ")"
					+ (l.hasLockedField() ? "  [has locked field]" : "")
					+ (l.hasExperienceInformation() ? "  [has experience]" : ""));
		}
	}

	/**
	 * Displays the complete details of a single lesson found by ID.
	 * 
	 * @param in The Scanner object for reading input.
	 * @param db The database instance to query.
	 */
	private static void examine(Scanner in, Database db) {
		Lesson lesson = askForLesson(in, db);
		if (lesson != null) System.out.println(lesson);
	}

	/**
	 * Updates a lesson's title, body, or category after confirming the field isn't locked.
	 * 
	 * @param in The Scanner object for reading input.
	 * @param db The database instance containing the lesson.
	 */
	private static void update(Scanner in, Database db) {
		Lesson lesson = askForLesson(in, db);
		if (lesson == null) return;
		
		String field = askForField(in);
		if (field == null) return;

		// Ensure the field is allowed to be modified
		String err = LessonValidator.checkFieldNotLocked(lesson, field);
		if (!err.isEmpty()) { System.out.println(err); return; }

		String value = ask(in, "Enter new " + field + ": ");
		if (value == null) return;

		boolean ok;
		if (Lesson.FIELD_TITLE.equals(field)) {
			err = LessonValidator.checkTitle(value);
			if (!err.isEmpty()) { System.out.println(err); return; }
			ok = db.updateLessonTitle(lesson.getId(), value);
		} else if (Lesson.FIELD_BODY.equals(field)) {
			err = LessonValidator.checkBody(value);
			if (!err.isEmpty()) { System.out.println(err); return; }
			ok = db.updateLessonBody(lesson.getId(), value);
		} else {
			err = LessonValidator.checkCategory(value);
			if (!err.isEmpty()) { System.out.println(err); return; }
			ok = db.updateLessonCategory(lesson.getId(), value);
		}
		System.out.println(ok ? "Lesson updated!" : "The lesson could not be updated.");
	}

	/**
	 * Prompts for experience metrics, validates them, and updates the lesson record.
	 * 
	 * @param in The Scanner object for reading input.
	 * @param db The database instance containing the lesson.
	 */
	private static void addExperience(Scanner in, Database db) {
		Lesson lesson = askForLesson(in, db);
		if (lesson == null) return;
		
		String what = ask(in, "What was done: ");
		String how = ask(in, "How it was done: ");
		String duration = ask(in, "Duration (number > 0): ");
		String effort = ask(in, "Effort (number >= 0): ");
		if (what == null || how == null || duration == null || effort == null) return;

		String err = LessonValidator.checkWhatWasDone(what);
		if (err.isEmpty()) err = LessonValidator.checkHowItWasDone(how);
		if (err.isEmpty()) err = LessonValidator.checkDuration(duration);
		if (err.isEmpty()) err = LessonValidator.checkEffort(effort);
		if (!err.isEmpty()) { System.out.println(err); return; }

		boolean ok = db.addExperienceInformation(lesson.getId(), what, how, duration.trim(),
				effort.trim());
		System.out.println(ok ? "Experience information saved!"
				: "The experience information could not be saved.");
	}

	/**
	 * Locks or unlocks a specific field (title, body, or category) for a lesson.
	 * 
	 * @param in The Scanner object for reading input.
	 * @param db The database instance containing the lesson.
	 */
	private static void lockUnlock(Scanner in, Database db) {
		Lesson lesson = askForLesson(in, db);
		if (lesson == null) return;
		
		String field = askForField(in);
		if (field == null) return;
		
		String which = ask(in, "Lock or unlock? (l/u): ");
		if (which == null) return;
		
		// If input starts with 'l', set boolean to true (lock), otherwise false (unlock)
		boolean lock = which.trim().toLowerCase().startsWith("l");

		boolean ok = db.setLessonFieldLocked(lesson.getId(), field, lock);
		if (ok) System.out.println("The " + field + " is now " + (lock ? "locked." : "unlocked."));
		else System.out.println("\"" + field + "\" is not a lockable field.  Use title, body, or category.");
	}

	/**
	 * Deletes a lesson from the database if deletion criteria are met.
	 * 
	 * @param in The Scanner object for reading input.
	 * @param db The database instance containing the lesson.
	 */
	private static void delete(Scanner in, Database db) {
		Lesson lesson = askForLesson(in, db);
		if (lesson == null) return;
		
		// Check business rules to ensure the lesson is allowed to be deleted
		String err = LessonValidator.checkCanDelete(lesson);
		if (!err.isEmpty()) { System.out.println(err); return; }
		
		System.out.println(db.deleteLesson(lesson.getId()) ? "Lesson deleted!"
				: "The lesson could not be deleted.");
	}
}