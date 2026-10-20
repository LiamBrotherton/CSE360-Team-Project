package modifiedGuiContributor;

import entityClasses.Lesson;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/*******
 * <p> Title: ModifiedControllerContributorHome Class. </p>
 *   
 * <p> Description: The Java/FX-based Modified Contributor Home Page Controller. Handles user inputs, 
 * field synchronization, edit protection rules, and data deletion checks. </p>
 *   
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *   
 * @author Lynn Robert Carter
 *   
 * @version 1.02        2026-10-20 Modified architecture for complete requirements execution
 *    
 */
public class ModifiedControllerContributorHome {

    /**
     * Default constructor is not used.
     */
    public ModifiedControllerContributorHome() {
    }

    /**
     * Directs the user to the User Update Page so the user can change the account attributes.
     */
    protected static void performUpdate() {
        guiUserUpdate.ViewUserUpdate.displayUserUpdate(ModifiedViewContributorHome.theStage, ModifiedViewContributorHome.theUser);
    }     

    /**
     * Logs out the current user and proceeds to the normal login page.
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ModifiedViewContributorHome.theStage);
    }     
    
    /**
     * Terminates the execution of the program cleanly.
     */
    protected static void performQuit() {
        System.exit(0);
    }

    /**
     * Triggered when a contributor selects a lesson from the list view panel to view details.
     */
    protected static void handleLessonSelection(Lesson selected) {
        if (selected == null) return;
        
        ModifiedModelContributorHome.setSelectedLesson(selected);
        
        // 1. Populate text fields with core text definitions from your object attributes
        ModifiedViewContributorHome.textField_LessonTitle.setText(selected.getTitle());
        ModifiedViewContributorHome.textField_LessonCategory.setText(selected.getCategory());
        ModifiedViewContributorHome.textArea_LessonBody.setText(selected.getBody());
        
        // 2. Display the complex layout string built from your custom multi-line toString() method
        ModifiedViewContributorHome.textArea_FullMetadataDump.setText(selected.toString());
        
        // 3. Apply element protection blocks depending on your field status properties
        ModifiedViewContributorHome.textField_LessonTitle.setEditable(!selected.getTitleLocked());
        ModifiedViewContributorHome.textField_LessonCategory.setEditable(!selected.getCategoryLocked());
        ModifiedViewContributorHome.textArea_LessonBody.setEditable(!selected.getBodyLocked());
        
        // 4. Ensure deletion button is disabled when any locked field flags evaluate to true
        if (selected.hasLockedField()) { 
            ModifiedViewContributorHome.button_DeleteLesson.setDisable(true); 
            ModifiedViewContributorHome.label_LockStatus.setText("Status: Locked Fields Found (Cannot Delete)");
        } else {
            ModifiedViewContributorHome.button_DeleteLesson.setDisable(false); 
            ModifiedViewContributorHome.label_LockStatus.setText("Status: Fully Editable");
        }
    }

    /**
     * Triggered when the contributor executes the Delete button action.
     */
    protected static void handleDeleteLesson() {
        boolean success = ModifiedModelContributorHome.deleteSelectedLesson();
        if (success) {
            ModifiedViewContributorHome.refreshLessonList();
        } else {
            Alert alert = new Alert(AlertType.ERROR, "This lesson has locked fields and cannot be deleted!");
            alert.showAndWait();
        }
    }
}