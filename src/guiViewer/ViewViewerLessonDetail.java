
package guiViewer;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.User;
import guiTools.GuiStyle;

/**
 * Read-only lesson detail page for Viewers.
 *
 * @author Alexis
 * @version 1.00 2026-11-19 Initial Viewer detail page.
 */
public class ViewViewerLessonDetail {

    private static final double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static final double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    private static final Database theDatabase = applicationMain.FoundationsMain.database;

    private static Stage theStage;
    private static User theUser;

    private static final Pane theRootPane = new Pane();
    private static final Scene theScene = new Scene(theRootPane, width, height);

    private static final Label label_PageTitle = new Label("Lesson Details");

    private static final Label label_TitleName = new Label("Title");

    private static final Label label_TitleValue = new Label();

    private static final Label label_CategoryName = new Label("Category");

    private static final Label label_CategoryValue = new Label();

    private static final Label label_BodyName = new Label("Description");

    private static final TextArea text_Body = new TextArea();

    private static final Label label_Message = new Label();

    private static final Button button_Back = new Button("Back to lessons");

    private static boolean initialized = false;

    /**
     * Displays the requested lesson without allowing edits.
     *
     * @param stage : the application stage
     * @param user : the currently authenticated Viewer
     * @param lessonId : the ID of the lesson to display
     */
    public static void displayLessonDetail(Stage stage, User user, int lessonId) {

        theStage = stage;
        theUser = user;

        if (!initialized) {
            initializePage();
            initialized = true;
        }

        // Retrieve the lesson again in case it was updated.
        LessonsLearned lesson = theDatabase.getLessonById(lessonId);

        if (lesson == null) {
            ViewViewerLessons.showMessage("That lesson is no longer available.");
            ViewViewerLessons.returnToResults(stage, user);
            return;
        }

        label_TitleValue.setText(lesson.getTitle());
        label_CategoryValue.setText(lesson.getCategory());
        text_Body.setText(lesson.getBody());

        theStage.setTitle("CSE 360 Foundations: Lesson Details");
        theStage.setScene(theScene);
        theStage.show();

        GuiStyle.startUnfocused(theRootPane);
    }

    /**
     * Initializes the read-only detail page.
     */
    private static void initializePage() {

        GuiStyle.applyBackground(theRootPane);

        label_PageTitle.setLayoutX(70);
        label_PageTitle.setLayoutY(104);
        GuiStyle.stylePageTitle(label_PageTitle);

        label_TitleName.setLayoutX(70);
        label_TitleName.setLayoutY(160);
        GuiStyle.styleFieldLabel(label_TitleName);

        label_TitleValue.setLayoutX(70);
        label_TitleValue.setLayoutY(185);
        label_TitleValue.setPrefWidth(660);
        GuiStyle.styleCardLabel(label_TitleValue);

        label_CategoryName.setLayoutX(70);
        label_CategoryName.setLayoutY(225);
        GuiStyle.styleFieldLabel(label_CategoryName);

        label_CategoryValue.setLayoutX(70);
        label_CategoryValue.setLayoutY(250);
        label_CategoryValue.setPrefWidth(660);
        GuiStyle.styleCardLabel(label_CategoryValue);

        label_BodyName.setLayoutX(70);
        label_BodyName.setLayoutY(290);
        GuiStyle.styleFieldLabel(label_BodyName);

        text_Body.setLayoutX(70);
        text_Body.setLayoutY(315);
        text_Body.setPrefSize(660, 130);
        text_Body.setEditable(false);
        text_Body.setWrapText(true);
        GuiStyle.styleTextArea(text_Body);

        label_Message.setLayoutX(70);
        label_Message.setLayoutY(455);
        GuiStyle.styleErrorLabel(label_Message);

        button_Back.setLayoutX(570);
        button_Back.setLayoutY(490);
        button_Back.setMinWidth(160);
        GuiStyle.styleSecondaryButton(button_Back);

        button_Back.setOnAction(
                (_) -> ViewViewerLessons.returnToResults(theStage, theUser));

        theRootPane.getChildren().addAll(
                GuiStyle.createCard(40, 40, 720, 520),
                GuiStyle.createLogo(70, 66),
                label_PageTitle,
                label_TitleName,
                label_TitleValue,
                label_CategoryName,
                label_CategoryValue,
                label_BodyName,
                text_Body,
                label_Message,
                button_Back);
    }
}
