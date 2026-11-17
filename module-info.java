module FoundationsF26 {
	requires javafx.controls;
	requires java.sql;

	// JUnit 5, used by the JUnit test classes in the testing package
	requires org.junit.jupiter.api;

	opens applicationMain to javafx.graphics, javafx.fxml;
	opens fPasswordEvaluationTestbedMain to javafx.graphics, javafx.fxml;

	// Lets JUnit find and run the tests in the testing package
	opens testing to org.junit.platform.commons;
}
