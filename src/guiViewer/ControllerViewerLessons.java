package guiViewer;

/**
 * Provides the controller actions for the Viewer lessons page.
 *
 * @author Alexis
 * @version 1.00 2026-11-11 Initial prototype.
 */
public class ControllerViewerLessons {

    /**
     * Returns the current Viewer to the Viewer home page.
     */
    protected static void performBackToHome() {
        ViewViewerHome.displayViewerHome(
                ViewViewerLessons.theStage,
                ViewViewerLessons.theUser);
    }
}