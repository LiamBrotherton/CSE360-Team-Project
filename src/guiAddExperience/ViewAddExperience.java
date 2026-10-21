package guiAddExperience;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import entityClasses.User;
import guiTools.GuiStyle;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * <p>Title: ViewAddExperience Class.</p>
 * <p>Contributor Story 3: loads existing experience, displays the saved fields,
 * and allows a contributor to add or replace the experience on their own lesson.
 * This page uses the team's shared blue-gradient and white-card GUI style.</p>
 *
 * @author Hassan Mohamed
 * @version 1.10 2026-10-20 Selected-lesson navigation and existing-value display
 */
public class ViewAddExperience {
    private static final double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static final double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    private static ViewAddExperience theView;
    protected static Stage theStage;
    protected static User theUser;
    /** Last loaded lesson, or -1 for no lesson selection. */
    protected static int theLessonId = -1;
    private static Scene theScene;

    // Internal ID remains compatible with the existing Model/Controller validation.
    // Users choose a lesson by its title instead of typing the ID.
    protected static TextField field_LessonId = new TextField();
    protected static ComboBox<LessonsLearned> combo_Lessons = new ComboBox<>();
    protected static TextArea area_WhatWasDone = new TextArea();
    protected static TextArea area_HowItWasDone = new TextArea();
    protected static TextField field_Duration = new TextField();
    protected static TextField field_Effort = new TextField();
    protected static Label label_Status = new Label();
    private static Label label_Purpose = new Label();
    private static Button button_Load = new Button("Load lesson");
    private static Button button_Save = new Button("Save experience");

    /** Opens the experience form and refreshes the contributor's lesson choices. */
    public static void displayAddExperience(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        if (theView == null) theView = new ViewAddExperience();

        theLessonId = -1;
        field_LessonId.clear();
        clearExperienceFields();
        label_Status.setText("");
        label_Purpose.setText("Choose one of your lessons, then select Load lesson.");
        button_Load.setDisable(true);
        button_Save.setDisable(true);
        combo_Lessons.getItems().clear();
        combo_Lessons.getSelectionModel().clearSelection();
        combo_Lessons.setValue(null);

        if (user != null && user.getContributorRole()) {
            LessonsLearnedList lessons = applicationMain.FoundationsMain.database
                    .getLessonsAddedBy(user.getUserName());
            combo_Lessons.getItems().addAll(lessons.getLessons());
            if (combo_Lessons.getItems().isEmpty()) {
                label_Status.setText("You have no lessons yet. Create one under Add a lesson.");
            }
        } else {
            label_Status.setText("Contributor permission is required.");
        }
        display();
    }

    /** Opens the form with the specified owned lesson selected and loaded. */
    public static void displayAddExperience(Stage ps, User user, int lessonId) {
        displayAddExperience(ps, user);
        if (user == null || !user.getContributorRole()) return;
        LessonsLearned lesson = ModelAddExperience.lessonFor(
                applicationMain.FoundationsMain.database, user.getUserName(), lessonId);
        if (lesson == null) {
            label_Status.setText(ModelAddExperience.NOT_AVAILABLE);
            return;
        }
        for (LessonsLearned candidate : combo_Lessons.getItems()) {
            if (candidate.getId() == lessonId) {
                combo_Lessons.getSelectionModel().select(candidate);
                break;
            }
        }
        loadLesson(lesson);
    }

    /**
     * Loads and displays a lesson's saved experience fields. Called only after
     * ModelAddExperience has checked that the lesson belongs to this user.
     */
    protected static void loadLesson(LessonsLearned lesson) {
        theLessonId = lesson.getId();
        field_LessonId.setText(Integer.toString(theLessonId));
        // Record which lesson was explicitly loaded before allowing a save.
        button_Load.setDisable(false);
        button_Save.setDisable(false);
        label_Purpose.setText("Lesson " + theLessonId + ": " + lesson.getTitle());
        clearExperienceFields();
        if (lesson.hasExperienceInformation()) {
            area_WhatWasDone.setText(nonNull(lesson.getWhatWasDone()));
            area_HowItWasDone.setText(nonNull(lesson.getHowItWasDone()));
            field_Duration.setText(nonNull(lesson.getDuration()));
            field_Effort.setText(nonNull(lesson.getEffort()));
            label_Status.setText("Saved experience loaded. Edit any field and Save to replace it.");
        } else {
            label_Status.setText("No experience saved yet. Complete the fields, then Save.");
        }
    }

    /** Clears the experience fields so a different user's data cannot remain visible. */
    protected static void clearExperienceFields() {
        area_WhatWasDone.clear();
        area_HowItWasDone.clear();
        field_Duration.clear();
        field_Effort.clear();
    }

    /** Text shown for each selectable contributor-owned lesson. */
    private static String lessonLabel(LessonsLearned lesson) {
        return lesson.getTitle() + " (" + lesson.getCategory() + ") — ID " + lesson.getId();
    }

    private static String nonNull(String value) { return value == null ? "" : value; }

    private static void display() {
        theStage.setTitle("CSE 360 Foundation Code: Add Experience Information");
        theStage.setScene(theScene);
        theStage.show();
    }

    /** Builds the shared-style experience form and wires its three actions. */
    private ViewAddExperience() {
        Pane pane = new Pane();
        theScene = new Scene(pane, width, height);
        GuiStyle.applyBackground(pane);

        Label title = new Label("Experience information");
        title.setLayoutX(70);
        title.setLayoutY(98);
        GuiStyle.stylePageTitle(title);

        label_Purpose.setLayoutX(70);
        label_Purpose.setLayoutY(135);
        label_Purpose.setPrefWidth(660);
        GuiStyle.styleMutedLabel(label_Purpose);

        Label idLabel = new Label("Choose your lesson");
        setupLabel(idLabel, 164, 70);
        combo_Lessons.setLayoutX(70);
        combo_Lessons.setLayoutY(184);
        combo_Lessons.setPrefWidth(440);
        combo_Lessons.setPromptText("Select a lesson...");
        // JavaFX ComboBox is styled to match the project's rounded white input fields.
        combo_Lessons.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; "
                + "-fx-border-radius: 7; -fx-background-radius: 7; -fx-font-size: 14px;");
        combo_Lessons.setConverter(new StringConverter<LessonsLearned>() {
            @Override public String toString(LessonsLearned lesson) {
                return lesson == null ? "" : lessonLabel(lesson);
            }
            @Override public LessonsLearned fromString(String text) { return null; }
        });
        combo_Lessons.setCellFactory(list -> new ListCell<LessonsLearned>() {
            @Override protected void updateItem(LessonsLearned lesson, boolean empty) {
                super.updateItem(lesson, empty);
                setText(empty || lesson == null ? null : lessonLabel(lesson));
            }
        });
        combo_Lessons.setOnAction(e -> {
            LessonsLearned lesson = combo_Lessons.getValue();
            theLessonId = -1;
            clearExperienceFields();
            field_LessonId.setText(lesson == null ? "" : Integer.toString(lesson.getId()));
            button_Load.setDisable(lesson == null);
            button_Save.setDisable(true);
            label_Status.setText(lesson == null ? "Choose a lesson first."
                    : "Click Load lesson to view its saved experience.");
        });

        button_Load.setLayoutX(530);
        button_Load.setLayoutY(184);
        button_Load.setMinWidth(200);
        GuiStyle.styleSecondaryButton(button_Load);
        button_Load.setOnAction((_) -> ControllerAddExperience.performLoad());

        Label whatLabel = new Label("What was done");
        setupLabel(whatLabel, 226, 70);
        setupTextArea(area_WhatWasDone, "Describe the new attempt", 245);

        Label howLabel = new Label("How it was done");
        setupLabel(howLabel, 308, 70);
        setupTextArea(area_HowItWasDone, "Describe the steps or approach", 327);

        Label durationLabel = new Label("Duration");
        setupLabel(durationLabel, 391, 70);
        setupTextField(field_Duration, "Example: 25 minutes", 411, 70, 320);

        Label effortLabel = new Label("Effort");
        setupLabel(effortLabel, 391, 410);
        setupTextField(field_Effort, "Example: 2 team members", 411, 410, 320);

        label_Status.setLayoutX(70);
        label_Status.setLayoutY(458);
        label_Status.setPrefWidth(660);
        label_Status.setPrefHeight(34);
        label_Status.setWrapText(true);
        GuiStyle.styleCardLabel(label_Status);

        setupButton(button_Save, 70, 180);
        GuiStyle.stylePrimaryButton(button_Save);
        button_Save.setOnAction((_) -> ControllerAddExperience.performSave());

        Button back = new Button("Back to lesson(s)");
        setupButton(back, 265, 200);
        GuiStyle.styleSecondaryButton(back);
        back.setOnAction((_) -> ControllerAddExperience.performReturn());

        pane.getChildren().addAll(
                GuiStyle.createCard(40, 40, 720, 520),
                GuiStyle.createLogo(70, 66),
                title, label_Purpose, idLabel, combo_Lessons, button_Load,
                whatLabel, area_WhatWasDone, howLabel, area_HowItWasDone,
                durationLabel, field_Duration, effortLabel, field_Effort,
                label_Status, button_Save, back);
    }

    private static void setupLabel(Label label, double y, double x) {
        label.setLayoutX(x);
        label.setLayoutY(y);
        GuiStyle.styleFieldLabel(label);
    }

    private static void setupTextField(TextField field, String prompt,
            double y, double x, double fieldWidth) {
        field.setPromptText(prompt);
        field.setLayoutX(x);
        field.setLayoutY(y);
        field.setPrefWidth(fieldWidth);
        GuiStyle.styleField(field);
    }

    private static void setupTextArea(TextArea area, String prompt, double y) {
        area.setPromptText(prompt);
        area.setLayoutX(70);
        area.setLayoutY(y);
        area.setPrefSize(660, 58);
        GuiStyle.styleTextArea(area);
    }

    private static void setupButton(Button button, double x, double buttonWidth) {
        button.setMinWidth(buttonWidth);
        button.setAlignment(Pos.CENTER);
        button.setLayoutX(x);
        button.setLayoutY(496);
    }
}
