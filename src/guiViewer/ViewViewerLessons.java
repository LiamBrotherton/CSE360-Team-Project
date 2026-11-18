package guiViewer;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import database.Database;
import entityClasses.LessonsLearnedList;
import entityClasses.User;
import guiTools.GuiStyle;

/**
 * JavaFX View that displays all available lessons to a Viewer.
 *
 * @author Alexis
 * @version 1.00 2026-11-11 Initial prototype.
 * @version 1.01 2026-11-12 Added internal comments explaining why the page is rebuilt
 *                          once but refreshed on every visit.
 */
public class ViewViewerLessons {

    /** Standard application window dimensions. */
    private static final double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static final double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    /** List displaying all available lessons. */
    protected static ListView<String> list_Lessons = new ListView<>();

    /** Current application stage. */
    protected static Stage theStage;

    /** Currently authenticated Viewer. */
    protected static User theUser;

    private static final Label label_PageTitle = new Label("All Lessons");

    private static final Label label_Purpose = new Label("Browse all lessons shared in the system.");

    private static final Button button_BackToHome = new Button("Back to home");

    private static final Pane theRootPane = new Pane();

    private static final Scene theScene = new Scene(theRootPane, width, height);

    private static final Database theDatabase = applicationMain.FoundationsMain.database;

    private static boolean initialized = false;

    /**
     * Displays all lessons for the current Viewer.
     *
     * @param stage the application stage
     * @param user the currently authenticated Viewer
     */
    public static void displayAllLessons(Stage stage, User user) {

        theStage = stage;
        theUser = user;

        // Build the widgets only once. Every later visit reuses them, which keeps
        // the page consistent with the other singleton pages in the application.
        if (!initialized) {
            initializePage();
            initialized = true;
        }

        // Re-read the database on every visit instead of caching, so lessons added by
        // Contributors since the last visit always appear to the Viewer.
        LessonsLearnedList lessons = ModelViewerLessons.lessonsFor(theDatabase);

        // setAll replaces the old rows in one step. Appending would duplicate every
        // lesson each time the Viewer returned to this page.
        list_Lessons.getItems().setAll(ModelViewerLessons.rowsFor(lessons));

        theStage.setTitle("CSE 360 Foundations: All Lessons");
        theStage.setScene(theScene);
        theStage.show();

        GuiStyle.startUnfocused(theRootPane);
    }

    /**
     * Initializes the page layout, widgets, and event handlers.
     */
    private static void initializePage() {

        GuiStyle.applyBackground(theRootPane);

        label_PageTitle.setLayoutX(70);
        label_PageTitle.setLayoutY(104);
        GuiStyle.stylePageTitle(label_PageTitle);

        label_Purpose.setLayoutX(70);
        label_Purpose.setLayoutY(142);
        GuiStyle.styleMutedLabel(label_Purpose);

        list_Lessons.setLayoutX(70);
        list_Lessons.setLayoutY(172);
        list_Lessons.setPrefSize(660, 270);

        Label emptyMessage = new Label(ModelViewerLessons.EMPTY_MESSAGE);

        GuiStyle.styleMutedLabel(emptyMessage);
        list_Lessons.setPlaceholder(emptyMessage);
        GuiStyle.styleList(list_Lessons);

        button_BackToHome.setMinWidth(160);
        button_BackToHome.setAlignment(Pos.CENTER);
        button_BackToHome.setLayoutX(570);
        button_BackToHome.setLayoutY(490);

        GuiStyle.styleSecondaryButton(button_BackToHome);

        button_BackToHome.setOnAction(
                (_) -> ControllerViewerLessons.performBackToHome());

        theRootPane.getChildren().addAll(
                GuiStyle.createCard(40, 40, 720, 520),
                GuiStyle.createLogo(70, 66),
                label_PageTitle,
                label_Purpose,
                list_Lessons,
                button_BackToHome);
    }
}