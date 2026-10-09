package lessonsLearnedTestbedMain;

import lessonsLearned.Model;
import database.Database;
import java.util.List;

public class LessonsLearnedTestingAutomation {
    
    static int passedTests = 0;
    static int failedTests = 0;
    
    private static Database db;

    public static void main(String[] args) {
        
        System.out.println("______________________________________");
        System.out.println("\nHW2 Lessons Learned Testing Automation");
        
        try {
        	
            db = new Database();
            System.out.println("Connecting to persistent storage engine at ~/FoundationDatabase...");
            db.connectToDatabase(); 
            Model.setDatabaseInstance(db);
            System.out.println("Foundations Database bound successfully. Commencing validation checks.");

            // TC1: Positive Create
            performCRUDTestingCase(1, "CREATE", "101", "How do we use VM arguments?", "Make sure that there aren't any carriage returns or blanks after the line.", "Ed-Post", true);
            
            // TC2: Negative Create (Empty Description/Body)
            performCRUDTestingCase(2, "CREATE", "102", "Sample Title Only", "", "Ed-Post", false);
            
            // TC3: Negative Create (Empty Title)
            performCRUDTestingCase(3, "CREATE", "103", "", "Sample description with empty title.", "Ed-Post", false);
            
            // TC4: Negative Create (Boundary Error - Title > 100 Characters)
            String longTitle = "A".repeat(105);
            performCRUDTestingCase(4, "CREATE", "104", longTitle, "Valid description narrative.", "Ed-Post", false);
            
            // TC5: Positive Read (Single Entry Lookup Verification)
            performCRUDTestingCase(5, "READ", "101", null, null, null, true);
            
            // TC6: Negative Read (Non-Existent Entry Validation Check)
            performCRUDTestingCase(6, "READ", "999", null, null, null, false);
            
            // TC7: Positive Update 
            performCRUDTestingCase(7, "UPDATE", "101", "How do we use arguments in Run Configuration? - UPDATED", "Ensure no carriage returns.", "Ed-Post", true);
            
            // TC8: Positive Delete
            performCRUDTestingCase(8, "DELETE", "101", null, null, null, true);
            
            // TC9: Negative Delete (Non-Existent Entry)
            performCRUDTestingCase(9, "DELETE", "999", null, null, null, false);

            System.out.println("______________________________________");
            System.out.println("\nFINAL EXECUTION REPORT:");
            System.out.println("Passed Tests: " + passedTests);
            System.out.println("Failed Tests: " + failedTests);
            
            db.dumpLessons();

        } catch (Exception e) {
            System.err.println("CRITICAL FAILURE - Database system could not handle setup sequence: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (db != null) {
                System.out.println("Shutting down engine locks and disconnecting clean.");
                db.closeConnection();
            }
        }
    }
    
    private static void performCRUDTestingCase(int caseNum, String op, String id, String title, String description, String contextType, boolean expectedPass) {
        System.out.println("______________________________________");
        System.out.println("Test Case #" + caseNum + " | Operation: [" + op + "] | ID: " + id);
        
        boolean opStatus = false;
        String errorMessage = "";
        
        try {
            switch(op) {
                case "CREATE":
                    errorMessage = Model.evaluateAndValidateInputs(title, description);
                    if (errorMessage.equals("")) {
                        opStatus = Model.createLesson(id, title, description, contextType);
                    } else {
                        System.out.println("Caught Validation Rejection: " + errorMessage);
                        opStatus = false;
                    }
                    break;
                    
                case "READ":
                    List<Model.Lesson> subset = Model.searchLessonsById(id);
                    opStatus = (subset != null && !subset.isEmpty());
                    if(!opStatus) errorMessage = "No lessons with matching ID found in storage.";
                    break;
                    
                case "UPDATE":
                    errorMessage = Model.evaluateAndValidateInputs(title, description);
                    if (errorMessage.equals("")) {
                        opStatus = Model.updateLesson(id, title, description, contextType);
                    } else {
                        System.out.println("Caught Validation Rejection: " + errorMessage);
                        opStatus = false;
                    }
                    break;
                    
                case "DELETE":
                    opStatus = Model.deleteLesson(id);
                    if(!opStatus) errorMessage = "Target ID not found; entry cannot be deleted.";
                    break;
                default:
                    errorMessage = "Unknown operation path evaluated.";
                    opStatus = false;
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception encountered: " + e.getMessage());
            opStatus = false;
        }
        
        if (opStatus == expectedPass) {
            System.out.println(">> TEST RESULT: PASSED");
            passedTests++;
        } else {
            System.out.println(">> TEST RESULT: FAILED (Expected success: " + expectedPass + ", got: " + opStatus + "). " + errorMessage);
            failedTests++;
        }
    }
}