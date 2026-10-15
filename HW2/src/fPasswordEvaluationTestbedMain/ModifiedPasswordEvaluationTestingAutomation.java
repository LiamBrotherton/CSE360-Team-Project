package fPasswordEvaluationTestbedMain;

/*******
 * <p> Title: ModifiedPasswordEvaluationTestingAutomation Class. </p>
 * 
 * <p> Description: A Java demonstration for semi-automated tests </p>
 * 
 */
public class ModifiedPasswordEvaluationTestingAutomation {
	
	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/*
	 * This mainline displays a header to the console, performs a sequence of
	 * test cases, and then displays a footer with a summary of the results
	 */
	public static void main(String[] args) {
		/************** Test cases semi-automation report header **************/
		System.out.println("______________________________________");
		System.out.println("\nTesting Automation");

		/************** Start of the test cases **************/
		
		performTestCase(1, "Ab0!1234", true);
		
		performTestCase(2, "Password321?", true);
		
		// The original case 3 was "~ cA1iginous'" marked valid, but it contains a space
		// at index 1 and the recognizer does not accept spaces.  The team confirmed the
		// recognizer is right, so the intent of the case - that unusual symbols are
		// acceptable - is kept here without the space, and case 11 below pins down the
		// rule that made the original fail.
		performTestCase(3, "~cA1iginous'", true);
		
		performTestCase(4, "abacuS2_", true);
		
		performTestCase(5, "racecar1!", false);
		
		performTestCase(6, "RACECAR1!", false);
		
		performTestCase(7, "Racecars!", false);
		
		performTestCase(8, "Racecars1", false);
		
		performTestCase(9, "Ra1!", false);
		
		performTestCase(10, "racecar", false);

		// A password containing a space is rejected outright as an invalid character,
		// not merely as a weak one.  Passphrases are therefore not accepted.
		performTestCase(11, "~ cA1iginous'", false);
		
		/************** End of the test cases **************/
		
		/************** Test cases semi-automation report footer **************/
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: "+ numPassed);
		System.out.println("Number of tests failed: "+ numFailed);
	}
	
	/*
	 * This method sets up the input value for the test from the input parameters,
	 * displays test execution information, invokes precisely the same recognizer
	 * that the interactive JavaFX mainline uses, interprets the returned value,
	 * and displays the interpreted result.
	 */
	private static void performTestCase(int testCase, String inputText, boolean expectedPass) {
				
		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");
		
		/************** Call the recognizer to process the input **************/
		String resultText= fPasswordPopUpWindow.Model.evaluatePassword(inputText);
		
		/************** Interpret the result and display that interpreted information **************/
		System.out.println();
		
		// If the resulting text is empty, the recognizer accepted the input
		if (resultText != "") {
			 // If the test case expected the test to pass then this is a failure
			if (expectedPass) {
				System.out.println("***Failure*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				numFailed++;
			}
			// If the test case expected the test to fail then this is a success
			else {			
				System.out.println("***Success*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				numPassed++;
			}
		}
		
		// If the resulting text is empty, the recognizer accepted the input
		else {	
			// If the test case expected the test to pass then this is a success
			if (expectedPass) {	
				System.out.println("***Success*** The password <" + inputText + 
						"> is valid, so this is a pass!");
				numPassed++;
			}
			// If the test case expected the test to fail then this is a failure
			else {
				System.out.println("***Failure*** The password <" + inputText + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				numFailed++;
			}
		}
		displayEvaluation();
	}
	
	private static void displayEvaluation() {
		
		if (fPasswordPopUpWindow.Model.foundUpperCase)
			System.out.println("At least one upper case letter - Satisfied");
		else
			System.out.println("At least one upper case letter - Not Satisfied");

		if (fPasswordPopUpWindow.Model.foundLowerCase)
			System.out.println("At least one lower case letter - Satisfied");
		else
			System.out.println("At least one lower case letter - Not Satisfied");
	

		if (fPasswordPopUpWindow.Model.foundNumericDigit)
			System.out.println("At least one digit - Satisfied");
		else
			System.out.println("At least one digit - Not Satisfied");

		if (fPasswordPopUpWindow.Model.foundSpecialChar)
			System.out.println("At least one special character - Satisfied");
		else
			System.out.println("At least one special character - Not Satisfied");

		if (fPasswordPopUpWindow.Model.foundLongEnough)
			System.out.println("At least 8 characters - Satisfied");
		else
			System.out.println("At least 8 characters - Not Satisfied");
	}
}
