/**
 * Provides the main application module for FoundationsF26.
 */
module FoundationsF26 {
	/** module directives here */
	requires javafx.controls;
	requires java.sql;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
	opens fPasswordEvaluationTestbedMain to javafx.graphics, javafx.fxml;
}
