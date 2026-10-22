module FoundationsF26 {
	requires javafx.controls;
	requires java.sql;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
	opens fPasswordEvaluationTestbedMain to javafx.graphics, javafx.fxml;
	
    // ADD THESE LINES TO ALLOW JUNIT ACCESS:
    requires org.junit.jupiter.api; 
    
    // Allows JUnit to see and run tests inside your test package:
    opens testing to org.junit.platform.commons;
}
