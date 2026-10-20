package guiAddExperience;

import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewAddExperience Class. </p>
 *
 * <p> Description: JavaFX page for Contributor Story 3. A contributor enters a
 * lesson ID and describes what was done, how it was done, the duration, and
 * the effort used. The Controller handles validation and persistence.</p>
 *
 * @author Hassan Mohamed
 * @version 1.00 2026-10-20 Initial version for TP2 Contributor Story 3
 */
public class ViewAddExperience {

    private static final double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static final double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    private static ViewAddExperience theView;
    protected static Stage theStage;
    protected static User theUser;
    private static Scene theScene;

    protected static TextField field_LessonId = new TextField();
    protected static TextArea area_WhatWasDone = new TextArea();
    protected static TextArea area_HowItWasDone = new TextArea();
    protected static TextField field_Duration = new TextField();
    protected static TextField field_Effort = new TextField();
    protected static Label label_Status = new Label();

    /*******
     * <p> Displays the experience form for the currently signed-in contributor.</p>
     *
     * @param ps the application's JavaFX Stage
     * @param user the currently signed-in user
     */
    public static void displayAddExperience(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        if (theView == null) theView = new ViewAddExperience();

        // Reset values so one session's unsaved text is not shown to the next user.
        field_LessonId.clear();
        area_WhatWasDone.clear();
        area_HowItWasDone.clear();
        field_Duration.clear();
        field_Effort.clear();
        label_Status.setText("");

        theStage.setTitle("CSE 360 Foundations: Add Experience Information");
        theStage.setScene(theScene);
        theStage.show();
    }

    /*******
     * <p> Builds the form's input widgets, actions, and layout.</p>
     */
    private ViewAddExperience() {
        Pane pane = new Pane();
        theScene = new Scene(pane, width, height);

        Label title = new Label("Add Experience Information");
        title.setFont(Font.font("Arial", 26));
        title.setLayoutX(22);
        title.setLayoutY(14);

        Line top = new Line(20, 73, width - 20, 73);

        Label idLabel = new Label("Lesson ID:");
        idLabel.setLayoutX(28);
        idLabel.setLayoutY(99);
        field_LessonId.setLayoutX(215);
        field_LessonId.setLayoutY(94);
        field_LessonId.setPrefWidth(500);
        field_LessonId.setPromptText("Enter the ID of an existing lesson");

        Label whatLabel = new Label("What was done:");
        whatLabel.setLayoutX(28);
        whatLabel.setLayoutY(151);
        area_WhatWasDone.setLayoutX(215);
        area_WhatWasDone.setLayoutY(142);
        area_WhatWasDone.setPrefSize(500, 75);
        area_WhatWasDone.setWrapText(true);
        area_WhatWasDone.setPromptText("Describe the activity or attempt");

        Label howLabel = new Label("How it was done:");
        howLabel.setLayoutX(28);
        howLabel.setLayoutY(242);
        area_HowItWasDone.setLayoutX(215);
        area_HowItWasDone.setLayoutY(231);
        area_HowItWasDone.setPrefSize(500, 75);
        area_HowItWasDone.setWrapText(true);
        area_HowItWasDone.setPromptText("Describe the steps or approach used");

        Label durationLabel = new Label("Duration:");
        durationLabel.setLayoutX(28);
        durationLabel.setLayoutY(338);
        field_Duration.setLayoutX(215);
        field_Duration.setLayoutY(331);
        field_Duration.setPrefWidth(500);
        field_Duration.setPromptText("Example: 2 hours");

        Label effortLabel = new Label("Effort:");
        effortLabel.setLayoutX(28);
        effortLabel.setLayoutY(390);
        field_Effort.setLayoutX(215);
        field_Effort.setLayoutY(383);
        field_Effort.setPrefWidth(500);
        field_Effort.setPromptText("Example: 3 team members");

        label_Status.setLayoutX(25);
        label_Status.setLayoutY(437);
        label_Status.setPrefWidth(width - 50);
        label_Status.setWrapText(true);

        Line bottom = new Line(20, 505, width - 20, 505);
        Button save = new Button("Save Experience");
        save.setPrefWidth(200);
        save.setAlignment(Pos.CENTER);
        save.setLayoutX(215);
        save.setLayoutY(530);
        save.setOnAction((_) -> ControllerAddExperience.performSave());

        Button back = new Button("Return to Contributor Home");
        back.setPrefWidth(235);
        back.setAlignment(Pos.CENTER);
        back.setLayoutX(435);
        back.setLayoutY(530);
        back.setOnAction((_) -> ControllerAddExperience.performReturn());

        pane.getChildren().addAll(title, top, idLabel, field_LessonId,
                whatLabel, area_WhatWasDone, howLabel, area_HowItWasDone,
                durationLabel, field_Duration, effortLabel, field_Effort,
                label_Status, bottom, save, back);
    }
}
