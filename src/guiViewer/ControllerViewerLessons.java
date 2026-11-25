
package guiViewer;

/**
 * Provides the controller actions for the Viewer lessons page.
 *
 * @author Alexis
 * @version 1.10 2026-11-19 Added search and open lesson ability.
 */
public class ControllerViewerLessons {

    /**
     * Searches lessons using the keyword entered by the Viewer.
     */
    protected static void performSearch() {
        ViewViewerLessons.displaySearchResults(ViewViewerLessons.text_Search.getText());
    }

    /**
     * Opens the lesson currently selected in the list.
     * A message is displayed if no lesson has been selected.
     */
    protected static void performOpenLesson() {
        int selectedIndex = ViewViewerLessons.list_Lessons.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0 || selectedIndex >= ViewViewerLessons.visibleLessons.size()) {

            ViewViewerLessons.showMessage("Select a lesson from the list first.");
            return;
        }

        int lessonId = ViewViewerLessons.visibleLessons.get(selectedIndex).getId();

        ViewViewerLessonDetail.displayLessonDetail(ViewViewerLessons.theStage, ViewViewerLessons.theUser, lessonId);
    }

    /**
     * Returns the current Viewer to the Viewer home page.
     */
    protected static void performBackToHome() {
        ViewViewerHome.displayViewerHome(
                ViewViewerLessons.theStage,
                ViewViewerLessons.theUser);
    }
}
