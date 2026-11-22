package testing;

import aiWritingHelper.AlternativesResult;
import aiWritingHelper.ExamplesResult;
import aiWritingHelper.ModelAiWritingHelper;
import aiWritingHelper.StubWritingAssistant;
import aiWritingHelper.SuggestionResult;
import aiWritingHelper.WritingAssistant;
import database.Database;
import entityClasses.LessonsLearned;
import inputRecognizer.LessonValidator;

/*******
 * <p> Title: AiWritingHelperTestingAutomation Class. </p>
 *
 * <p> Description: Automated testing for the Contributor AI writing help aspect of TP3 (the Contributor
 * "AI-based interactive writing, brainstorming, and searching" epic).  The AI itself is not
 * built in this project, so these tests run the Model against fake assistants whose replies the tests
 * control.  That is what lets the tests ask the hard question the epic raises: what does the system
 * do when an assistant gives back something unusable?</p>
 *
 * <p> The aspect is broken into seven requirements, and every test names the requirement it checks:
 * <br>R1, only the Contributor who added a lesson can ask for help with it;
 * <br>R2, help is only given for a title, body, or category that is not locked;
 * <br>R3, the request text must be present and no longer than the limit;
 * <br>R4, whatever the assistant returns is cleaned and checked by the same rules as typed text, and a
 * failing or missing assistant never crashes the page;
 * <br>R5, a suggestion is never saved until the Contributor accepts it;
 * <br>R6, accepting re-checks ownership, locks, and the text, because they may have changed since
 * the suggestion was made;
 * <br>R7, suggested text is stored as plain data, never run as part of a database command;
 * <br>R8, a Contributor can ask for one to five alternatives, each cleaned and checked, and any that
 * fail are dropped;
 * <br>R9, searching for examples finds only the Contributor's own lessons.</p>
 *
 * <p> The tests run against the real database, which may already hold lessons.  They create their own
 * Contributors and lessons with unique names and remove only those.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-11-09 Initial version, written before ModelAiWritingHelper (test first)
 * @version 1.10		2026-11-19 Added R8 (brainstorming) and R9 (search for examples), also written
 * 							before the code they test
 */
public class AiWritingHelperTestingAutomation {

	/** The database every test reads and changes. */
	private static Database database;
	/** Counts the checks that passed. */
	private static int passed = 0;
	/** Counts the checks that failed. */
	private static int failed = 0;

	/** The Contributor who owns the test lessons. */
	private static String owner;
	/** A second Contributor who owns none of them. */
	private static String other;
	/** An unlocked lesson owned by owner. */
	private static int lessonId = -1;
	/** A lesson owned by owner whose title is locked. */
	private static int lockedLessonId = -1;
	/** A lesson owned by other, with a title no other test lesson shares. */
	private static int otherLessonId = -1;
	/** The one word that only other's lesson contains. */
	private static String otherWord;

	/**
	 * Default constructor is not used.  Every method in this class is static.
	 */
	public AiWritingHelperTestingAutomation() {
	}

	/*****
	 * <p> Title: FakeAssistant Class. </p>
	 *
	 * <p> Description: A stand-in assistant whose reply is set by each test.  It records how many
	 * times it was asked, so a test can prove the assistant was never reached.  Without this the
	 * tests would only be able to see the final answer, not that a refused request did no work.</p>
	 */
	private static class FakeAssistant implements WritingAssistant {
		/** What suggest returns. */
		String reply = "A fine suggestion";
		/** When set, suggest throws this instead of returning. */
		RuntimeException toThrow = null;
		/** How many times suggest was called. */
		int calls = 0;
		/** Replies to give, one per call and in order, before falling back to reply.  Lets a test
		 *  make an assistant that gives different answers to the same request. */
		java.util.ArrayDeque<String> queue = new java.util.ArrayDeque<>();

		/**
		 * Default constructor.  A test sets the fields it needs after creating the assistant.
		 */
		FakeAssistant() {
		}

		/**
		 * Records the call, then throws or returns what the test set.
		 *
		 * @param field			the lesson field, not used
		 * @param currentText	the current text, not used
		 * @param prompt		the request, not used
		 *
		 * @return the reply the test set
		 */
		@Override
		public String suggest(String field, String currentText, String prompt) {
			calls++;
			if (toThrow != null) throw toThrow;
			if (!queue.isEmpty()) return queue.poll();
			return reply;
		}
	}

	/*****
	 * <p> Method: void main(String[] args) </p>
	 *
	 * <p> Description: Runs every test, prints the totals, and exits with status 1 if any check
	 * failed so a script can tell.</p>
	 *
	 * @param args command-line arguments, not used
	 */
	public static void main(String[] args) {
		System.out.println("========================================");
		System.out.println("CONTRIBUTOR AI WRITING HELP AUTOMATED TESTING");
		System.out.println("========================================");

		try {
			database = new Database();
			database.connectToDatabase();

			// Unique names so repeated runs never collide with each other or with real accounts
			String suffix = String.valueOf(System.currentTimeMillis() % 1000000);
			owner = "AiOwner" + suffix;
			other = "AiOther" + suffix;

			lessonId = database.addLessonForContributor("AI test lesson", "Original body text.",
					"Testing", owner);
			lockedLessonId = database.addLessonForContributor("Locked title lesson", "Body.",
					"Testing", owner);
			database.setLessonFieldLocked(lockedLessonId, LessonsLearned.FIELD_TITLE, true);

			testR1Ownership();
			testR2Fields();
			testR3Prompt();
			testR4AssistantOutput();
			testR5NotSavedUntilAccepted();
			testR6AcceptRechecks();
			testR7PlainData();

			// The word is unique to this run so no real lesson can contain it by chance
			otherWord = "Quokka" + suffix;
			otherLessonId = database.addLessonForContributor("Notes about " + otherWord,
					"Body of the other Contributor's lesson.", "Private", other);
			testR8Alternatives();
			testR9Examples();
		} catch (Exception e) {
			failed++;
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Remove only what this run created.  deleteLesson ignores locks, which is why it is used here.
			if (database != null) {
				if (lessonId > 0) database.deleteLesson(lessonId);
				if (lockedLessonId > 0) database.deleteLesson(lockedLessonId);
				if (otherLessonId > 0) database.deleteLesson(otherLessonId);
				database.closeConnection();
			}
			System.out.println();
			System.out.println("========================================");
			System.out.println("TEST SUMMARY");
			System.out.println("Passed: " + passed);
			System.out.println("Failed: " + failed);
			System.out.println("========================================");
			if (failed > 0) System.exit(1);
		}
	}

	/*****
	 * R1: only the owning Contributor gets help.  Boundary IDs are used because the lesson ID is the
	 * value a person controls, and the Model must treat every ID that is not theirs the same way.
	 */
	private static void testR1Ownership() {
		section("R1: ownership");
		FakeAssistant ai = new FakeAssistant();

		checkTest("Test R1.1 - Owner Gets A Suggestion",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_BODY, "make it clearer").isOk(), true);
		checkTest("Test R1.2 - Another Contributor Is Refused",
				ModelAiWritingHelper.requestSuggestion(database, ai, other, lessonId,
						LessonsLearned.FIELD_BODY, "make it clearer").getError(),
				ModelAiWritingHelper.NOT_AVAILABLE);

		int[] badIds = { 0, -1, Integer.MAX_VALUE, Integer.MIN_VALUE };
		for (int id : badIds) {
			checkTest("Test R1.3 - Lesson ID " + id + " Is Refused",
					ModelAiWritingHelper.requestSuggestion(database, ai, owner, id,
							LessonsLearned.FIELD_BODY, "help").getError(),
					ModelAiWritingHelper.NOT_AVAILABLE);
		}

		checkTest("Test R1.4 - Null Username Is Refused",
				ModelAiWritingHelper.requestSuggestion(database, ai, null, lessonId,
						LessonsLearned.FIELD_BODY, "help").getError(),
				ModelAiWritingHelper.NOT_AVAILABLE);
		checkTest("Test R1.5 - Blank Username Is Refused",
				ModelAiWritingHelper.requestSuggestion(database, ai, "   ", lessonId,
						LessonsLearned.FIELD_BODY, "help").getError(),
				ModelAiWritingHelper.NOT_AVAILABLE);
	}

	/*****
	 * R2: only title, body, and category, and only when unlocked.  A refused request must not reach
	 * the assistant at all, so the call counter is checked.
	 */
	private static void testR2Fields() {
		section("R2: fields and locks");
		FakeAssistant ai = new FakeAssistant();

		checkTest("Test R2.1 - Title Is Allowed",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_TITLE, "shorter").isOk(), true);
		checkTest("Test R2.2 - Category Is Allowed",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_CATEGORY, "pick one").isOk(), true);
		checkTest("Test R2.3 - Unknown Field Is Refused",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						"addedBy", "help").isOk(), false);

		int before = ai.calls;
		SuggestionResult locked = ModelAiWritingHelper.requestSuggestion(database, ai, owner,
				lockedLessonId, LessonsLearned.FIELD_TITLE, "help");
		checkTest("Test R2.4 - Locked Title Is Refused", locked.isOk(), false);
		checkTest("Test R2.5 - Refusal Names The Lock",
				locked.getError().contains("locked"), true);
		checkTest("Test R2.6 - Refused Request Never Reached The Assistant",
				ai.calls == before, true);
		checkTest("Test R2.7 - Unlocked Field Of The Same Lesson Is Still Allowed",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lockedLessonId,
						LessonsLearned.FIELD_BODY, "help").isOk(), true);
	}

	/*****
	 * R3: the request text.  The limit is tested at 0, 1, the limit, and one past it, the boundaries
	 * where an off-by-one would hide.
	 */
	private static void testR3Prompt() {
		section("R3: request text");
		FakeAssistant ai = new FakeAssistant();
		int max = ModelAiWritingHelper.MAX_PROMPT_LENGTH;

		checkTest("Test R3.1 - Null Request Is Refused",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_BODY, null).isOk(), false);
		checkTest("Test R3.2 - Empty Request Is Refused",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_BODY, "").isOk(), false);
		checkTest("Test R3.3 - Whitespace Request Is Refused",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_BODY, "  \t ").isOk(), false);
		checkTest("Test R3.4 - One Character Is Allowed",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_BODY, "x").isOk(), true);
		checkTest("Test R3.5 - Exactly The Limit Is Allowed",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_BODY, "x".repeat(max)).isOk(), true);
		checkTest("Test R3.6 - One Over The Limit Is Refused",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_BODY, "x".repeat(max + 1)).isOk(), false);

		// The assistant is never asked about a request that failed this check.
		ai.calls = 0;
		ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
				LessonsLearned.FIELD_BODY, "");
		checkTest("Test R3.7 - Refused Request Never Reached The Assistant", ai.calls == 0, true);
	}

	/*****
	 * R4: the assistant's reply.  This is the heart of the prototype: the reply is untrusted input
	 * and gets the same rules as something a person typed.
	 */
	private static void testR4AssistantOutput() {
		section("R4: assistant output");
		FakeAssistant ai = new FakeAssistant();

		ai.reply = null;
		checkTest("Test R4.1 - Null Reply Is Rejected",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_BODY, "help").isOk(), false);
		ai.reply = "";
		checkTest("Test R4.2 - Empty Reply Is Rejected",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_BODY, "help").isOk(), false);
		ai.reply = "   \n  ";
		checkTest("Test R4.3 - Whitespace Reply Is Rejected",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_BODY, "help").isOk(), false);

		// Each field's limit, at the limit and one over it
		checkLimit("Title", LessonsLearned.FIELD_TITLE, LessonValidator.MAX_TITLE_LENGTH, ai);
		checkLimit("Body", LessonsLearned.FIELD_BODY, LessonValidator.MAX_BODY_LENGTH, ai);
		checkLimit("Category", LessonsLearned.FIELD_CATEGORY, LessonValidator.MAX_CATEGORY_LENGTH, ai);

		// Control characters are removed, so a reply cannot smuggle a NUL or bell into the database
		ai.reply = "Hello\u0000World\u0007!";
		SuggestionResult cleaned = ModelAiWritingHelper.requestSuggestion(database, ai, owner,
				lessonId, LessonsLearned.FIELD_BODY, "help");
		checkTest("Test R4.7 - Control Characters Are Removed", cleaned.getText(), "HelloWorld!");

		// Surrounding space is trimmed, but a line break inside the text is kept for a body
		ai.reply = "  Line one\nLine two  ";
		checkTest("Test R4.8 - Edges Trimmed, Inner Line Break Kept",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
						LessonsLearned.FIELD_BODY, "help").getText(), "Line one\nLine two");

		// An assistant that fails must not take the page down
		ai.toThrow = new IllegalStateException("service down");
		SuggestionResult down = ModelAiWritingHelper.requestSuggestion(database, ai, owner,
				lessonId, LessonsLearned.FIELD_BODY, "help");
		checkTest("Test R4.9 - Throwing Assistant Gives A Message, Not A Crash",
				down.getError(), ModelAiWritingHelper.ASSISTANT_UNAVAILABLE);
		checkTest("Test R4.10 - Missing Assistant Gives A Message, Not A Crash",
				ModelAiWritingHelper.requestSuggestion(database, null, owner, lessonId,
						LessonsLearned.FIELD_BODY, "help").getError(),
				ModelAiWritingHelper.ASSISTANT_UNAVAILABLE);

		// The built-in stand-in must always produce something the rules accept
		StubWritingAssistant stub = new StubWritingAssistant();
		checkTest("Test R4.11 - Stub Title Passes Validation",
				ModelAiWritingHelper.requestSuggestion(database, stub, owner, lessonId,
						LessonsLearned.FIELD_TITLE, "x".repeat(ModelAiWritingHelper.MAX_PROMPT_LENGTH)).isOk(), true);
		checkTest("Test R4.12 - Stub Category Passes Validation",
				ModelAiWritingHelper.requestSuggestion(database, stub, owner, lessonId,
						LessonsLearned.FIELD_CATEGORY, "x".repeat(ModelAiWritingHelper.MAX_PROMPT_LENGTH)).isOk(), true);
	}

	/*****
	 * R5: asking for help never changes the lesson.
	 */
	private static void testR5NotSavedUntilAccepted() {
		section("R5: not saved until accepted");
		FakeAssistant ai = new FakeAssistant();
		ai.reply = "Brand new body";

		String before = database.getLessonById(lessonId).getBody();
		SuggestionResult s = ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
				LessonsLearned.FIELD_BODY, "rewrite");
		checkTest("Test R5.1 - Suggestion Was Produced", s.getText(), "Brand new body");
		checkTest("Test R5.2 - Lesson Is Unchanged After The Request",
				database.getLessonById(lessonId).getBody(), before);

		checkTest("Test R5.3 - Accepting Saves It",
				ModelAiWritingHelper.acceptSuggestion(database, owner, lessonId,
						LessonsLearned.FIELD_BODY, s.getText()), "");
		checkTest("Test R5.4 - Lesson Holds The Accepted Text",
				database.getLessonById(lessonId).getBody(), "Brand new body");
	}

	/*****
	 * R6: accepting checks everything again.  A lock or a different owner can appear between asking
	 * and accepting, so trusting the earlier answer would let a locked field be changed.
	 */
	private static void testR6AcceptRechecks() {
		section("R6: accept re-checks");

		checkTest("Test R6.1 - Another Contributor Cannot Accept Into This Lesson",
				ModelAiWritingHelper.acceptSuggestion(database, other, lessonId,
						LessonsLearned.FIELD_BODY, "Hijacked"),
				ModelAiWritingHelper.NOT_AVAILABLE);
		checkTest("Test R6.2 - Lesson Was Not Changed By The Refused Accept",
				database.getLessonById(lessonId).getBody().equals("Hijacked"), false);

		checkTest("Test R6.3 - Accepting Into A Locked Field Is Refused",
				ModelAiWritingHelper.acceptSuggestion(database, owner, lockedLessonId,
						LessonsLearned.FIELD_TITLE, "New title").contains("locked"), true);
		checkTest("Test R6.4 - Locked Title Was Not Changed",
				database.getLessonById(lockedLessonId).getTitle(), "Locked title lesson");

		checkTest("Test R6.5 - Over-Long Text Is Refused At Accept Time",
				ModelAiWritingHelper.acceptSuggestion(database, owner, lessonId,
						LessonsLearned.FIELD_TITLE, "x".repeat(LessonValidator.MAX_TITLE_LENGTH + 1)).isEmpty(), false);
		checkTest("Test R6.6 - Blank Text Is Refused At Accept Time",
				ModelAiWritingHelper.acceptSuggestion(database, owner, lessonId,
						LessonsLearned.FIELD_TITLE, "  ").isEmpty(), false);

		// A lock placed after the suggestion was made, the case R6 exists for
		FakeAssistant ai = new FakeAssistant();
		ai.reply = "Late suggestion";
		SuggestionResult s = ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
				LessonsLearned.FIELD_CATEGORY, "help");
		database.setLessonFieldLocked(lessonId, LessonsLearned.FIELD_CATEGORY, true);
		checkTest("Test R6.7 - Lock Added After The Suggestion Blocks Accept",
				ModelAiWritingHelper.acceptSuggestion(database, owner, lessonId,
						LessonsLearned.FIELD_CATEGORY, s.getText()).contains("locked"), true);
		database.setLessonFieldLocked(lessonId, LessonsLearned.FIELD_CATEGORY, false);
	}

	/*****
	 * R7: text that looks like a database command is just text.
	 */
	private static void testR7PlainData() {
		section("R7: plain data");
		FakeAssistant ai = new FakeAssistant();
		String hostile = "x'); DROP TABLE lessonDB; --";
		ai.reply = hostile;

		SuggestionResult s = ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId,
				LessonsLearned.FIELD_BODY, "help");
		checkTest("Test R7.1 - Hostile Text Is Returned Unchanged", s.getText(), hostile);
		checkTest("Test R7.2 - Accepting It Succeeds",
				ModelAiWritingHelper.acceptSuggestion(database, owner, lessonId,
						LessonsLearned.FIELD_BODY, s.getText()), "");
		LessonsLearned stored = database.getLessonById(lessonId);
		checkTest("Test R7.3 - It Was Stored As Text", stored != null && hostile.equals(stored.getBody()), true);
		checkTest("Test R7.4 - The Lesson Table Still Works", database.getNumberOfLessons() > 0, true);
	}

	/*****
	 * R8: brainstorming.  The count is tested at 0, 1, 5, and 6 because those are the edges of the
	 * allowed range, and at the extreme integers because a count is the one number here that could
	 * make a loop run for a very long time.
	 */
	private static void testR8Alternatives() {
		section("R8: alternatives");
		FakeAssistant ai = new FakeAssistant();
		String f = LessonsLearned.FIELD_TITLE;
		int[] badCounts = { 0, -1, 6, Integer.MAX_VALUE, Integer.MIN_VALUE };
		for (int n : badCounts) {
			ai.calls = 0;
			AlternativesResult r = ModelAiWritingHelper.requestAlternatives(database, ai, owner,
					lessonId, f, "shorter", n);
			checkTest("Test R8.1 - Count " + n + " Is Refused", r.isOk(), false);
			checkTest("Test R8.1 - Count " + n + " Never Reached The Assistant", ai.calls == 0, true);
		}

		ai.queue.clear();
		ai.reply = "One good title";
		checkTest("Test R8.2 - Count 1 Gives One Alternative",
				ModelAiWritingHelper.requestAlternatives(database, ai, owner, lessonId, f,
						"shorter", 1).getTexts().size() == 1, true);

		for (String t : new String[] { "A", "B", "C", "D", "E" }) ai.queue.add("Title " + t);
		AlternativesResult five = ModelAiWritingHelper.requestAlternatives(database, ai, owner,
				lessonId, f, "shorter", 5);
		checkTest("Test R8.3 - Count 5 Gives Five Alternatives", five.getTexts().size() == 5, true);
		checkTest("Test R8.3 - They Are In The Order Given", five.getTexts().get(0), "Title A");

		// Two over-long titles among five: the three good ones come back and the bad ones are dropped
		ai.queue.clear();
		ai.queue.add("Good one");
		ai.queue.add("x".repeat(LessonValidator.MAX_TITLE_LENGTH + 1));
		ai.queue.add("Good two");
		ai.queue.add("x".repeat(LessonValidator.MAX_TITLE_LENGTH + 1));
		ai.queue.add("Good three");
		AlternativesResult mixed = ModelAiWritingHelper.requestAlternatives(database, ai, owner,
				lessonId, f, "shorter", 5);
		checkTest("Test R8.4 - Bad Alternatives Are Dropped, Good Ones Kept",
				mixed.getTexts().size() == 3 && mixed.isOk(), true);

		ai.queue.clear();
		ai.reply = "x".repeat(LessonValidator.MAX_TITLE_LENGTH + 1);
		AlternativesResult allBad = ModelAiWritingHelper.requestAlternatives(database, ai, owner,
				lessonId, f, "shorter", 3);
		checkTest("Test R8.5 - All Bad Alternatives Give A Message", allBad.isOk(), false);
		checkTest("Test R8.5 - The Message Says They Were Rejected",
				allBad.getError().contains("rejected"), true);

		// The same answer three times is one alternative, since the Contributor gains nothing from copies
		ai.reply = "Same title";
		checkTest("Test R8.6 - Duplicates Are Dropped",
				ModelAiWritingHelper.requestAlternatives(database, ai, owner, lessonId, f,
						"shorter", 3).getTexts().size() == 1, true);

		ai.reply = "Hello\u0000 title\u0007";
		checkTest("Test R8.7 - Control Characters Are Removed From An Alternative",
				ModelAiWritingHelper.requestAlternatives(database, ai, owner, lessonId, f,
						"shorter", 1).getTexts().get(0), "Hello title");

		ai.reply = "A fine title";
		ai.calls = 0;
		checkTest("Test R8.8 - Another Contributor Is Refused",
				ModelAiWritingHelper.requestAlternatives(database, ai, other, lessonId, f,
						"shorter", 3).getError(), ModelAiWritingHelper.NOT_AVAILABLE);
		AlternativesResult locked = ModelAiWritingHelper.requestAlternatives(database, ai, owner,
				lockedLessonId, f, "shorter", 3);
		checkTest("Test R8.8 - A Locked Field Is Refused", locked.getError().contains("locked"), true);
		checkTest("Test R8.8 - Refused Requests Never Reached The Assistant", ai.calls == 0, true);

		checkTest("Test R8.9 - Blank Request Is Refused",
				ModelAiWritingHelper.requestAlternatives(database, ai, owner, lessonId, f,
						"  ", 3).isOk(), false);

		ai.toThrow = new IllegalStateException("service down");
		checkTest("Test R8.10 - A Throwing Assistant Gives A Message",
				ModelAiWritingHelper.requestAlternatives(database, ai, owner, lessonId, f,
						"shorter", 3).getError(), ModelAiWritingHelper.ASSISTANT_UNAVAILABLE);
		checkTest("Test R8.10 - A Missing Assistant Gives A Message",
				ModelAiWritingHelper.requestAlternatives(database, null, owner, lessonId, f,
						"shorter", 3).getError(), ModelAiWritingHelper.ASSISTANT_UNAVAILABLE);

		// The stand-in must give five different, valid answers even for the shortest field
		StubWritingAssistant stub = new StubWritingAssistant();
		AlternativesResult stubbed = ModelAiWritingHelper.requestAlternatives(database, stub, owner,
				lessonId, LessonsLearned.FIELD_CATEGORY, "x".repeat(ModelAiWritingHelper.MAX_PROMPT_LENGTH), 5);
		checkTest("Test R8.11 - Stub Gives Five Different Categories",
				stubbed.isOk() && new java.util.HashSet<>(stubbed.getTexts()).size() == 5, true);

		// Asking never changes the lesson (R5 for alternatives)
		String before = database.getLessonById(lessonId).getTitle();
		ModelAiWritingHelper.requestAlternatives(database, stub, owner, lessonId, f, "shorter", 3);
		checkTest("Test R8.12 - Alternatives Are Not Saved",
				database.getLessonById(lessonId).getTitle(), before);
	}

	/*****
	 * R9: searching for examples.  The query is tested at 0, 1, the limit, and one over, and with text
	 * that would matter if it were used in a database command or a pattern, because the search is
	 * done over what the Contributor typed.
	 */
	private static void testR9Examples() {
		section("R9: examples");

		ExamplesResult byTitle = ModelAiWritingHelper.findExamples(database, owner, "ai TEST");
		checkTest("Test R9.1 - Title Match Ignores Case", containsId(byTitle, lessonId), true);
		checkTest("Test R9.2 - A Non-Matching Lesson Is Not Listed", containsId(byTitle, lockedLessonId), false);
		checkTest("Test R9.3 - Body Match",
				containsId(ModelAiWritingHelper.findExamples(database, owner, "DROP TABLE"), lessonId), true);
		checkTest("Test R9.4 - Category Match",
				containsId(ModelAiWritingHelper.findExamples(database, owner, "testing"), lessonId), true);

		// The central rule: another Contributor's lesson is never listed
		ExamplesResult theirWord = ModelAiWritingHelper.findExamples(database, owner, otherWord);
		checkTest("Test R9.5 - Another Contributor's Lesson Is Never Listed",
				theirWord.isOk() && theirWord.getLessons().isEmpty(), true);
		checkTest("Test R9.6 - The Other Contributor Can Find It",
				containsId(ModelAiWritingHelper.findExamples(database, other, otherWord), otherLessonId), true);
		boolean allOwn = true;
		for (LessonsLearned l : ModelAiWritingHelper.findExamples(database, owner, "e").getLessons()) {
			if (!owner.equals(l.getAddedBy())) allOwn = false;
		}
		checkTest("Test R9.7 - Every Result Belongs To The Searcher", allOwn, true);

		checkTest("Test R9.8 - No Match Is A Success With No Lessons",
				ModelAiWritingHelper.findExamples(database, owner, "zzzNoSuchWordzzz").isOk(), true);
		checkTest("Test R9.8 - ... And The List Is Empty",
				ModelAiWritingHelper.findExamples(database, owner, "zzzNoSuchWordzzz").getLessons().isEmpty(), true);

		int max = ModelAiWritingHelper.MAX_QUERY_LENGTH;
		checkTest("Test R9.9 - Null Query Is Refused",
				ModelAiWritingHelper.findExamples(database, owner, null).isOk(), false);
		checkTest("Test R9.9 - Empty Query Is Refused",
				ModelAiWritingHelper.findExamples(database, owner, "").isOk(), false);
		checkTest("Test R9.9 - Whitespace Query Is Refused",
				ModelAiWritingHelper.findExamples(database, owner, "  \t ").isOk(), false);
		checkTest("Test R9.10 - One Character Is Allowed",
				ModelAiWritingHelper.findExamples(database, owner, "a").isOk(), true);
		checkTest("Test R9.10 - Exactly The Limit Is Allowed",
				ModelAiWritingHelper.findExamples(database, owner, "a".repeat(max)).isOk(), true);
		checkTest("Test R9.10 - One Over The Limit Is Refused",
				ModelAiWritingHelper.findExamples(database, owner, "a".repeat(max + 1)).isOk(), false);

		// Text that is special in a database command or a pattern is just text here
		String[] special = { "' OR '1'='1", "%", "_", ".*", "\\" };
		for (String q : special) {
			ExamplesResult r = ModelAiWritingHelper.findExamples(database, owner, q);
			checkTest("Test R9.11 - Query " + q + " Is Searched As Plain Text",
					r.isOk() && r.getLessons().stream().allMatch(l -> owner.equals(l.getAddedBy())), true);
		}
		checkTest("Test R9.11 - ... And It Does Not Match Everything",
				ModelAiWritingHelper.findExamples(database, owner, "%").getLessons().size()
						< database.getLessonsForContributor(owner).getLessons().size(), true);

		checkTest("Test R9.12 - Null Username Is Refused",
				ModelAiWritingHelper.findExamples(database, null, "ai").getError(),
				ModelAiWritingHelper.NOT_AVAILABLE);
		checkTest("Test R9.12 - Blank Username Is Refused",
				ModelAiWritingHelper.findExamples(database, "  ", "ai").getError(),
				ModelAiWritingHelper.NOT_AVAILABLE);
	}

	/*****
	 * Tells whether a search found the lesson with the given ID.
	 *
	 * @param result	the search result
	 * @param id		the lesson ID to look for
	 *
	 * @return true if that lesson is among the matches
	 */
	private static boolean containsId(ExamplesResult result, int id) {
		for (LessonsLearned l : result.getLessons()) if (l.getId() == id) return true;
		return false;
	}

	/*****
	 * Checks one field's length boundary: the limit is accepted and one over is rejected.
	 *
	 * @param label	the field's name, used in the test names
	 * @param field	LessonsLearned.FIELD_TITLE, LessonsLearned.FIELD_BODY, or LessonsLearned.FIELD_CATEGORY
	 * @param limit	the longest length the field allows
	 * @param ai	the fake assistant whose reply is set to the boundary lengths
	 */
	private static void checkLimit(String label, String field, int limit, FakeAssistant ai) {
		ai.reply = "x".repeat(limit);
		checkTest("Test R4.4 - " + label + " At The Limit (" + limit + ") Is Accepted",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId, field,
						"help").isOk(), true);
		ai.reply = "x".repeat(limit + 1);
		checkTest("Test R4.5 - " + label + " One Over The Limit (" + (limit + 1) + ") Is Rejected",
				ModelAiWritingHelper.requestSuggestion(database, ai, owner, lessonId, field,
						"help").isOk(), false);
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
	 * Compares an actual boolean with the expected one and prints PASS or FAIL.
	 *
	 * @param name		the test's name
	 * @param actual	what the code produced
	 * @param expected	what it should have produced
	 */
	private static void checkTest(String name, boolean actual, boolean expected) {
		System.out.println("\n" + name);
		System.out.println("Expected: " + expected);
		System.out.println("Actual:   " + actual);
		if (actual == expected) { System.out.println("Result:   PASS"); passed++; }
		else { System.out.println("Result:   FAIL"); failed++; }
	}

	/*****
	 * Compares an actual String with the expected one, null safe, and prints PASS or FAIL.  Values
	 * are shown in angle brackets so an empty string or stray space is visible.
	 *
	 * @param name		the test's name
	 * @param actual	what the code produced
	 * @param expected	what it should have produced
	 */
	private static void checkTest(String name, String actual, String expected) {
		boolean match = (actual == null) ? expected == null : actual.equals(expected);
		System.out.println("\n" + name);
		System.out.println("Expected: <" + expected + ">");
		System.out.println("Actual:   <" + actual + ">");
		if (match) { System.out.println("Result:   PASS"); passed++; }
		else { System.out.println("Result:   FAIL"); failed++; }
	}
}
