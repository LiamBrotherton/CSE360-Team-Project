package lessonsLearned;

import java.util.ArrayList;
import java.util.List;
import database.Database;

public class Model {

    private static Database dbInstance;

    public static void setDatabaseInstance(Database db) {
        dbInstance = db;
    }

    public static class Lesson {
        private String id;
        private String title;
        private String body;
        private String category; 

        public Lesson(String id, String title, String body, String category) {
            this.id = id;
            this.title = title;
            this.body = body;
            this.category = category;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getBody() { return body; }
        public String getCategory() { return category; }
        public String getDescription() { return body; }
        public String getContextType() { return category; }
    }

    public static boolean foundValidTitle = false;
    public static boolean foundValidBody = false;
    public static boolean foundInDatabase = false;
    public static boolean isSubsetEmpty = false;

    private static void resetTrackingVariables() {
        foundValidTitle = false;
        foundValidBody = false;
        foundInDatabase = false;
        isSubsetEmpty = false;
    }

    public static String evaluateAndValidateInputs(String title, String body) {
        resetTrackingVariables();
        
        if (title == null || title.trim().isEmpty()) {
            return "Validation Error: Title must be more than 0 characters.";
        }
        if (title.length() > 100) {
            return "Validation Error: Title must be less than 100 characters.";
        }
        foundValidTitle = true;

        if (body == null || body.trim().isEmpty()) {
            return "Validation Error: Body content cannot be empty.";
        }
        foundValidBody = true;

        return "";
    }
    
    public static boolean createLesson(String id, String title, String body, String category) {
        String validationResult = evaluateAndValidateInputs(title, body);
        if (!validationResult.equals("")) {
            return false;
        }

        boolean success = dbExecuteUpdate("INSERT", id, title, body, category);
        if (success) {
            foundInDatabase = true;
        }
        return success;
    }

    public static List<Lesson> getAllLessons() {
        List<Lesson> completeList = new ArrayList<>();
        if (dbInstance == null) {
            System.err.println("[Model Error] Foundations Database instance reference is missing!");
            return completeList;
        }
        
        List<String[]> rows = dbInstance.getAllLessonsRecords(); 
        if (rows != null) {
            for (String[] row : rows) {
                Lesson lesson = new Lesson(row[0], row[1], row[2], row[3]);
                completeList.add(lesson);
            }
        }
        return completeList;
    }

    public static List<Lesson> searchLessonsById(String id) {
        resetTrackingVariables();
        List<Lesson> subsetList = new ArrayList<>();
        
        List<String[]> rows = dbExecuteQuery("SELECT", id, "");
        if (rows != null && !rows.isEmpty()) {
            for (String[] row : rows) {
                Lesson lesson = new Lesson(row[0], row[1], row[2], row[3]);
                subsetList.add(lesson);
            }
        }

        isSubsetEmpty = subsetList.isEmpty();
        if (!isSubsetEmpty) {
            foundInDatabase = true;
        }
        
        return subsetList;
    }

    public static boolean updateLesson(String id, String title, String body, String category) {
        String validationResult = evaluateAndValidateInputs(title, body);
        if (!validationResult.equals("")) {
            return false;
        }

        boolean success = dbExecuteUpdate("UPDATE", id, title, body, category);
        if (success) {
            foundInDatabase = true;
        }
        return success;
    }

    public static boolean deleteLesson(String id) {
        resetTrackingVariables();
        
        boolean success = dbExecuteUpdate("DELETE", id, "", "", "");
        foundInDatabase = !success; 
        return success;
    }

    private static boolean dbExecuteUpdate(String action, String id, String title, String body, String category) {
        if (dbInstance == null) {
            System.err.println("[Model Error] Foundations Database instance reference is missing!");
            return false;
        }
        
        if (action.equals("INSERT")) {
            return dbInstance.insertLesson(id, title, body, category);
        }
        if (action.equals("UPDATE")) {
            return dbInstance.updateLessonRecord(id, title, body, category);
        }
        if (action.equals("DELETE")) {
            return dbInstance.removeLesson(id);
        }
        return false;
    }

    private static List<String[]> dbExecuteQuery(String action, String id, String keyword) {
        if (dbInstance == null) {
            System.err.println("[Model Error] Foundations Database instance reference is missing!");
            return new ArrayList<>();
        }
        return dbInstance.getLessonsById(id);
    }
}