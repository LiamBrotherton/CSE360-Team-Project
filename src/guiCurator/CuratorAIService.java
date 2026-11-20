package guiCurator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*******
 * <p> Title: CuratorAIService Class. </p>
 * 
 * <p> Description: Provides AI-assisted tools for Curators to generate search terms
 * and refine lesson content. </p>
 */
public class CuratorAIService {

    /**
     * Generates a list of suggested search terms based on lesson title and content.
     * Replace the body with your LLM API call (e.g., OpenAI/Gemini HTTP request) 
     * or local NLP keyword extraction logic.
     */
    public static List<String> generateSearchTerms(String lessonTitle, String lessonBody) {
        if (lessonBody == null || lessonBody.isBlank()) {
            return List.of();
        }
        
        // TODO: Replace stub logic with actual AI API integration
        String combined = (lessonTitle + " " + lessonBody).toLowerCase();
        return Arrays.stream(combined.split("\\W+"))
                .filter(word -> word.length() > 4)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Suggests grammar, clarity, and structural refinements for a lesson.
     */
    public static String refineLessonText(String originalText, String instruction) {
        if (originalText == null || originalText.isBlank()) {
            return "";
        }

        // TODO: Replace stub logic with actual AI API integration
        return "[AI Refined (" + instruction + ")]:\n" + originalText.trim();
    }
}