package guiAddExperience;

import java.util.Optional;

import database.Database;
import entityClasses.LessonsLearned;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * <p>Title: ControllerAddExperience Class.</p>
 * <p>Handles loading and saving experience for Contributor Story 3, including
 * permission checks and confirmation before replacing previously saved data.</p>
 *
 * @author Hassan Mohamed
 * @version 1.11 2026-10-20 Corrected LessonsLearned type and compilation errors
 */
public class ControllerAddExperience {

    private ControllerAddExperience() { }

    /** Loads the contributor-owned lesson selected in the dropdown. */
    protected static void performLoad() {
        if (!contributorSignedIn()) return;

        LessonsLearned chosen = ViewAddExperience.combo_Lessons.getValue();
        if (chosen == null) {
            ViewAddExperience.label_Status.setText("Choose a lesson first.");
            return;
        }
        String idText = Integer.toString(chosen.getId());
        String error = inputRecognizer.LessonValidator.checkLessonIdText(idText);
        if (!error.isEmpty()) {
            ViewAddExperience.label_Status.setText(error);
            return;
        }

        int id = Integer.parseInt(idText.trim());
        Database db = applicationMain.FoundationsMain.database;
        LessonsLearned lesson = ModelAddExperience.lessonFor(
                db, ViewAddExperience.theUser.getUserName(), id);

        if (lesson == null) {
            ViewAddExperience.label_Status.setText(ModelAddExperience.NOT_AVAILABLE);
            ViewAddExperience.clearExperienceFields();
            return;
        }
        ViewAddExperience.loadLesson(lesson);
    }

    /** Validates and saves experience information for a contributor-owned lesson. */
    protected static void performSave() {
        if (!contributorSignedIn()) return;
        LessonsLearned chosen = ViewAddExperience.combo_Lessons.getValue();
        if (chosen == null || ViewAddExperience.theLessonId != chosen.getId()) {
            ViewAddExperience.label_Status.setText("Select a lesson and click Load lesson first.");
            return;
        }

        String idText = ViewAddExperience.field_LessonId.getText();
        String what = ViewAddExperience.area_WhatWasDone.getText();
        String how = ViewAddExperience.area_HowItWasDone.getText();
        String duration = ViewAddExperience.field_Duration.getText();
        String effort = ViewAddExperience.field_Effort.getText();

        String error = ModelAddExperience.validate(idText, what, how, duration, effort);
        if (!error.isEmpty()) {
            ViewAddExperience.label_Status.setText(error);
            return;
        }

        Database db = applicationMain.FoundationsMain.database;
        int id = Integer.parseInt(idText.trim());
        LessonsLearned lesson = ModelAddExperience.lessonFor(
                db, ViewAddExperience.theUser.getUserName(), id);
        if (lesson == null) {
            ViewAddExperience.label_Status.setText(ModelAddExperience.NOT_AVAILABLE);
            return;
        }

        // The database stores one experience record per lesson.
        if (lesson.hasExperienceInformation()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Replace experience information");
            confirm.setHeaderText("This lesson already has experience information.");
            confirm.setContentText("Saving will replace the existing experience. Continue?");
            Optional<ButtonType> answer = confirm.showAndWait();
            if (!answer.isPresent() || answer.get() != ButtonType.OK) return;
        }

        error = ModelAddExperience.save(
                db, ViewAddExperience.theUser.getUserName(),
                idText, what, how, duration, effort);
        if (!error.isEmpty()) {
            ViewAddExperience.label_Status.setText(error);
            return;
        }

        // Read it back from the database to show the values actually stored.
        LessonsLearned saved = ModelAddExperience.lessonFor(
                db, ViewAddExperience.theUser.getUserName(), id);
        if (saved == null) {
            ViewAddExperience.label_Status.setText(ModelAddExperience.NOT_AVAILABLE);
        } else {
            ViewAddExperience.loadLesson(saved);
            ViewAddExperience.label_Status.setText(
                    "Experience saved. These are the stored values.");
        }
    }

    /** Returns to Lesson Details if a lesson was selected; otherwise My Lessons. */
    protected static void performReturn() {
        if (ViewAddExperience.theLessonId > 0) {
            guiLessonDetail.ViewLessonDetail.displayLessonDetail(
                    ViewAddExperience.theStage,
                    ViewAddExperience.theUser,
                    ViewAddExperience.theLessonId);
        } else {
            guiMyLessons.ViewMyLessons.displayMyLessons(
                    ViewAddExperience.theStage,
                    ViewAddExperience.theUser);
        }
    }

    /** Checks the signed-in user's Contributor role. */
    private static boolean contributorSignedIn() {
        if (ViewAddExperience.theUser != null
                && ViewAddExperience.theUser.getContributorRole()) return true;
        ViewAddExperience.label_Status.setText("Contributor permission is required.");
        return false;
    }
}
