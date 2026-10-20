package modifiedGuiContributor;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import entityClasses.Lesson;

/*******
 * <p> Title: ModifiedViewContributorHome Class. </p>
 *   
 * <p> Description: The Java/FX-based Modified Contributor Home Page View. Incorporates a split layout
 * displaying all contributor lessons, interactive inspection fields, and action protection states. </p>
 *   
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *   
 * @author Lynn Robert Carter
 *   
 * @version 1.02        2026-10-20 Modified architecture for complete requirements execution
 *    
 */
public class ModifiedViewContributorHome {
    
    // Application window measurements
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // GUI Area 1 Widgets
    protected static Label label_PageTitle = new Label();
    protected static Label label_UserDetails = new Label();
    protected static Button button_UpdateThisUser = new Button("Account Update");
    protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

    // GUI Area 2 Widgets (Populated requirements space)
    protected static ListView<Lesson> listView_Lessons = new ListView<>();
    protected static Label label_LessonDetailsTitle = new Label("Lesson Examination");
    protected static Label label_LockStatus = new Label("Status: No Lesson Selected");
    protected static TextField textField_LessonTitle = new TextField();
    protected static TextField textField_LessonCategory = new TextField();
    protected static TextArea textArea_LessonBody = new TextArea();
    protected static TextArea textArea_FullMetadataDump = new TextArea();
    protected static Button button_DeleteLesson = new Button("Delete Lesson");

    // GUI Area 3 Widgets
    protected static Line line_Separator4 = new Line(20, 525, width-20, 525);
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");

    // Context reference attributes
    private static ModifiedViewContributorHome theView;     
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    protected static Stage theStage;            
    protected static Pane theRootPane;          
    protected static User theUser;              
    private static Scene theViewContributorHomeScene;   
    protected static final int theRole = 2;     

    /**
     * Singleton single entry initialization hook.
     */
    public static void displayContributorHome(Stage ps, User user) { 
        theStage = ps; 
        theUser = user; 
        
        if (theView == null) {
            theView = new ModifiedViewContributorHome();
        }
        
        theDatabase.getUserAccountDetails(user.getUserName()); 
        applicationMain.FoundationsMain.activeHomePage = theRole; 
        
        label_UserDetails.setText("User: " + theUser.getUserName()); 
        refreshLessonList();

        theStage.setTitle("CSE 360 Foundations: Modified Contributor Home Page"); 
        theStage.setScene(theViewContributorHomeScene); 
        theStage.show(); 
    }
    
    /**
     * Refreshes the display information of lessons and resets selection inputs cleanly.
     */
    protected static void refreshLessonList() {
        ModifiedModelContributorHome.loadLessons(theUser.getUserName());
        listView_Lessons.getItems().clear();
        listView_Lessons.getItems().addAll(ModifiedModelContributorHome.getContributorLessons());
        
        textField_LessonTitle.clear();
        textField_LessonCategory.clear();
        textArea_LessonBody.clear();
        textArea_FullMetadataDump.clear();
        label_LockStatus.setText("Status: No Lesson Selected");
        button_DeleteLesson.setDisable(true);
    }

    /**
     * Class instantiation constructor. Setup layouts, dimensions, styles and actions.
     */
    private ModifiedViewContributorHome() { 
        theRootPane = new Pane(); 
        theViewContributorHomeScene = new Scene(theRootPane, width, height);    
        
        // GUI Area 1 Configuration
        label_PageTitle.setText("Contributor Home Page"); 
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);  
        
        label_UserDetails.setText("User: " + theUser.getUserName()); 
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55); 
        
        setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45); 
        button_UpdateThisUser.setOnAction((_) -> { ModifiedControllerContributorHome.performUpdate(); }); 
        
        // GUI Area 2 Configuration
        listView_Lessons.setLayoutX(20);
        listView_Lessons.setLayoutY(110);
        listView_Lessons.setPrefSize(250, 390);
        
        // Inline cell factory to list titles along with visual status padlocks cleanly
        listView_Lessons.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Lesson item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + (item.hasLockedField() ? " 🔒" : ""));
                }
            }
        });
        
        listView_Lessons.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            ModifiedControllerContributorHome.handleLessonSelection(newSelection);
        });

        setupLabelUI(label_LessonDetailsTitle, "Arial", 20, 300, Pos.BASELINE_LEFT, 290, 110);
        setupLabelUI(label_LockStatus, "Arial", 14, 300, Pos.BASELINE_LEFT, 290, 135);
        
        textField_LessonTitle.setLayoutX(290);
        textField_LessonTitle.setLayoutY(165);
        textField_LessonTitle.setPrefWidth(220);
        textField_LessonTitle.setPromptText("Title");

        textField_LessonCategory.setLayoutX(530);
        textField_LessonCategory.setLayoutY(165);
        textField_LessonCategory.setPrefWidth(230);
        textField_LessonCategory.setPromptText("Category");

        textArea_LessonBody.setLayoutX(290);
        textArea_LessonBody.setLayoutY(205);
        textArea_LessonBody.setPrefSize(470, 130);
        textArea_LessonBody.setPromptText("Body Content Description...");
        
        textArea_FullMetadataDump.setLayoutX(290);
        textArea_FullMetadataDump.setLayoutY(350);
        textArea_FullMetadataDump.setPrefSize(470, 110);
        textArea_FullMetadataDump.setEditable(false);
        textArea_FullMetadataDump.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        
        setupButtonUI(button_DeleteLesson, "Dialog", 16, 150, Pos.CENTER, 290, 475);
        button_DeleteLesson.setOnAction((_) -> { ModifiedControllerContributorHome.handleDeleteLesson(); });
        button_DeleteLesson.setDisable(true); 
        
        // GUI Area 3 Configuration
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540); 
        button_Logout.setOnAction((_) -> { ModifiedControllerContributorHome.performLogout(); }); 
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540); 
        button_Quit.setOnAction((_) -> { ModifiedControllerContributorHome.performQuit(); }); 

        // Bind all interactive variables to core window node layer list
        theRootPane.getChildren().addAll( 
            label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1, 
            listView_Lessons, label_LessonDetailsTitle, label_LockStatus, 
            textField_LessonTitle, textField_LessonCategory, textArea_LessonBody, textArea_FullMetadataDump, 
            button_DeleteLesson, line_Separator4, button_Logout, button_Quit); 
    }
    
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){ 
        l.setFont(Font.font(ff, f)); 
        l.setMinWidth(w); 
        l.setAlignment(p); 
        l.setLayoutX(x); 
        l.setLayoutY(y);         
    } 
    
    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){ 
        b.setFont(Font.font(ff, f)); 
        b.setMinWidth(w); 
        b.setAlignment(p); 
        b.setLayoutX(x); 
        b.setLayoutY(y);         
    } 
}