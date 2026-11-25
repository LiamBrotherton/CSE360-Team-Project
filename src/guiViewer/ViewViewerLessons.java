package guiViewer;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import entityClasses.User;
import guiTools.GuiStyle;

/**
 * JavaFX View that displays, searches, and open available lessons to a Viewer.
 *
 * @author Alexis
 * @version 2.0 finished implementing viewer user stories
 */
public class ViewViewerLessons {

    /** Standard application window dimensions. */
    private static final double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static final double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    /** List displaying all available lessons. */
    protected static ListView<String> list_Lessons = new ListView<>();

    /** Keyword entered by the Viewer. */
    protected static TextField text_Search = new TextField();
    
    /** Actual lesson objects corresponding to the visible rows. */
    protected static List<LessonsLearned> visibleLessons = new ArrayList<>();
    
    /** Current application stage. */
    protected static Stage theStage;

    /** Currently authenticated Viewer. */
    protected static User theUser;

    private static final Label label_PageTitle = new Label("All Lessons");

    private static final Label label_Purpose = new Label("Browse or search lessons shared in the system.");

    private static final Button button_Search = new Button("Search");

    private static final Button button_OpenLesson = new Button("Open lesson");
    
    private static final Button button_BackToHome = new Button("Back to home");
    
    private static final Label label_Message = new Label();

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

        if (!initialized) {
            initializePage();
            initialized = true;
        }

        text_Search.clear();
        
        // Re-read the database on every visit
        LessonsLearnedList lessons = ModelViewerLessons.lessonsFor(theDatabase);

        updateResults(lessons, "All Lessons");
        
        theStage.setTitle("CSE 360 Foundations: All Lessons");
        theStage.setScene(theScene);
        theStage.show();

        GuiStyle.startUnfocused(theRootPane);
    }

    /**
     * Searches lessons using a keyword and displays the results.
     *
     * @param keyword : the keyword entered by the Viewer
     */
    public static void displaySearchResults(String keyword) {

        LessonsLearnedList results = ModelViewerLessons.searchLessons(theDatabase, keyword);

        updateResults(results, "Search Results");

        theStage.setTitle("CSE 360 Foundations: Search Results");
        theStage.setScene(theScene);
        theStage.show();
    }
    
    /**
     * Restores the current results after the Viewer closes a lesson so the don't have to type keyword ever time
     *
     * @param stage : the application stage
     * @param user : the currently authenticated Viewer
     */
    public static void returnToResults(Stage stage, User user) {

        theStage = stage;
        theUser = user;

        theStage.setTitle("CSE 360 Foundations: Lessons");
        theStage.setScene(theScene);
        theStage.show();

        GuiStyle.startUnfocused(theRootPane);
    }
    
    /**
     * Updates the displayed rows and their corresponding lessons.
     *
     * @param lessons : the lessons needing to be displayed
     * @param title : the page title
     */
    private static void updateResults(LessonsLearnedList lessons, String title) {

        visibleLessons.clear();
        visibleLessons.addAll(lessons.getLessons());

        list_Lessons.getItems().setAll(ModelViewerLessons.rowsFor(lessons));

        label_PageTitle.setText(title);

        if (lessons.isEmpty()) {
            label_Message.setText(ModelViewerLessons.EMPTY_MESSAGE);
        } else {
            label_Message.setText("");
        }
    }
    
    /**
     * Displays a message to the Viewer.
     *
     * @param message the message to display
     */
    protected static void showMessage(String message) {
        label_Message.setText(message);
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

        text_Search.setPromptText("Enter a keyword...");
        text_Search.setLayoutX(70);
        text_Search.setLayoutY(180);
        text_Search.setPrefWidth(500);
        GuiStyle.styleField(text_Search);
        
        button_Search.setLayoutX(590);
        button_Search.setLayoutY(180);
        button_Search.setMinWidth(140);
        GuiStyle.stylePrimaryButton(button_Search);

        button_Search.setOnAction(
        		(_) -> ControllerViewerLessons.performSearch());
        text_Search.setOnAction(
                (_) -> ControllerViewerLessons.performSearch());
        
        list_Lessons.setLayoutX(70);
        list_Lessons.setLayoutY(225);
        list_Lessons.setPrefSize(660, 210);

        Label emptyMessage = new Label(ModelViewerLessons.EMPTY_MESSAGE);

        GuiStyle.styleMutedLabel(emptyMessage);
        list_Lessons.setPlaceholder(emptyMessage);
        GuiStyle.styleList(list_Lessons);
        
        label_Message.setLayoutX(70);
        label_Message.setLayoutY(445);
        label_Message.setPrefWidth(660);
        GuiStyle.styleErrorLabel(label_Message);
        
        button_OpenLesson.setLayoutX(70);
        button_OpenLesson.setLayoutY(490);
        button_OpenLesson.setMinWidth(160);
        GuiStyle.stylePrimaryButton(button_OpenLesson);

        button_OpenLesson.setOnAction(
                (_) -> ControllerViewerLessons.performOpenLesson());

        list_Lessons.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ControllerViewerLessons.performOpenLesson();
            }
        });

        button_BackToHome.setLayoutX(570);
        button_BackToHome.setLayoutY(490);
        button_BackToHome.setMinWidth(160);
        button_BackToHome.setAlignment(Pos.CENTER);
        GuiStyle.styleSecondaryButton(button_BackToHome);

        button_BackToHome.setOnAction(
                (_) -> ControllerViewerLessons.performBackToHome());

        theRootPane.getChildren().addAll(
                GuiStyle.createCard(40, 40, 720, 520),
                GuiStyle.createLogo(70, 66),
                label_PageTitle,
                label_Purpose,
                text_Search,
                button_Search,
                list_Lessons,
                label_Message,
                button_OpenLesson,
                button_BackToHome);
    }
}