package guiAddExperience;

import java.util.Optional;

import database.Database;
import entityClasses.Lesson;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/*******
 * <p> Title: ControllerAddExperience Class. </p>
 *
 * <p> Description: Handles the Add Experience Information page actions. The
 * View collects input, the Model validates it, and the Database saves it.</p>
 *
 * @author Hassan Mohamed
 * @version 1.00 2026-10-20 Initial version for TP2 Contributor Story 3
 */
public class ControllerAddExperience {

    private ControllerAddExperience() { }

    /*******
     * <p> Validates fields and saves experience information to a lesson.</p>
     * <p> An existing experience entry must be confirmed before replacement.</p>
     */
    protected static void performSave() {
        if (ViewAddExperience.theUser == null
                || !ViewAddExperience.theUser.getContributorRole()) {
            ViewAddExperience.label_Status.setText("Contributor permission is required.");
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
        Lesson lesson = db.getLessonById(id);
        if (lesson == null) {
            ViewAddExperience.label_Status.setText("Lesson " + id + " was not found.");
            return;
        }

        // Database currently supports a single experience entry for each lesson.
        if (lesson.hasExperienceInformation()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Replace Experience Information");
            confirm.setHeaderText("Lesson " + id + " already contains experience information.");
            confirm.setContentText("Saving will replace its existing experience information. Continue?");
            Optional<ButtonType> answer = confirm.showAndWait();
            if (!answer.isPresent() || answer.get() != ButtonType.OK) return;
        }

        error = ModelAddExperience.save(db, idText, what, how, duration, effort);
        ViewAddExperience.label_Status.setText(
                error.isEmpty() ? "Experience information saved for lesson " + id + "." : error);
    }

    /*******
     * <p> Returns to the Contributor Home Page with the same user and Stage.</p>
     */
    protected static void performReturn() {
        guiContributor.ViewContributorHome.displayContributorHome(
                ViewAddExperience.theStage, ViewAddExperience.theUser);
    }
}
