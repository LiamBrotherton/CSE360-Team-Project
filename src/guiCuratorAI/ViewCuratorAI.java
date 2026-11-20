package guiCuratorAI;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import entityClasses.User;
import guiTools.GuiStyle;

public class ViewCuratorAI {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    private static ViewCuratorAI theView;
    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    private static Scene theCuratorAIScene;

    private static Label label_PageTitle = new Label("Curator AI Tools");
    private static Label label_Subtitle = new Label("Generate search terms and refine lesson text.");
    private static TextArea textArea_Input = new TextArea();
    private static TextArea textArea_Output = new TextArea();
    private static Button button_GenerateTerms = new Button("Generate Search Terms");
    private static Button button_RefineText = new Button("Refine Lesson");
    private static Button button_Back = new Button("Back to Home");

    public static void displayCuratorAI(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) theView = new ViewCuratorAI();

        theStage.setTitle("CSE 360 Foundations: Curator AI Tools");
        theStage.setScene(theCuratorAIScene);
        theStage.show();
    }

    @SuppressWarnings("unused")
	private ViewCuratorAI() {
        theRootPane = new Pane();
        theCuratorAIScene = new Scene(theRootPane, width, height);
        GuiStyle.applyBackground(theRootPane);

        GuiStyle.placeHomeTitle(label_PageTitle, label_Subtitle);

        // Layout positions for input/output boxes and buttons
        textArea_Input.setPromptText("Paste lesson text here...");
        textArea_Input.setLayoutX(50);
        textArea_Input.setLayoutY(150);
        textArea_Input.setPrefSize(350, 250);
        textArea_Input.setWrapText(true);

        textArea_Output.setPromptText("AI results will appear here...");
        textArea_Output.setEditable(false);
        textArea_Output.setLayoutX(450);
        textArea_Output.setLayoutY(150);
        textArea_Output.setPrefSize(350, 250);

        button_GenerateTerms.setLayoutX(50);
        button_GenerateTerms.setLayoutY(420);
        button_GenerateTerms.setOnAction((_) -> {
            String input = textArea_Input.getText();
            if (input == null || input.isBlank()) {
                textArea_Output.setText("Please enter or paste lesson text before refining.");
                return;
            }
            var terms = guiCurator.CuratorAIService.generateSearchTerms("", textArea_Input.getText());
            textArea_Output.setText(String.join(", ", terms));
        });

        button_RefineText.setLayoutX(230);
        button_RefineText.setLayoutY(420);
        button_RefineText.setOnAction((_) -> {
            String input = textArea_Input.getText();
            if (input == null || input.isBlank()) {
                textArea_Output.setText("Please enter or paste lesson text before refining.");
                return;
            }
        	
            button_RefineText.setDisable(true);
            textArea_Output.setText("Refining lesson...");

            javafx.concurrent.Task<String> task = new javafx.concurrent.Task<>() {
                @Override
                protected String call() {
                    return guiCurator.CuratorAIService.refineLessonText(
                        textArea_Input.getText(), "Improve clarity and structure"
                    );
                }
            };

            task.setOnSucceeded(e -> {
                textArea_Output.setText(task.getValue());
                button_RefineText.setDisable(false);
            });
            
            task.setOnFailed(e -> {
                textArea_Output.setText("Error: " + task.getException().getMessage());
                button_RefineText.setDisable(false);
            });

            new Thread(task).start();
        });

        button_Back.setLayoutX(50);
        button_Back.setLayoutY(500);
        button_Back.setOnAction((_) -> {
            guiCurator.ViewCuratorHome.displayCuratorHome(theStage, theUser);
        });

        theRootPane.getChildren().addAll(
        	GuiStyle.createHeaderBar(),
        	GuiStyle.createLogo(24, 20, true),
            label_PageTitle, label_Subtitle,
            textArea_Input, textArea_Output,
            button_GenerateTerms, button_RefineText, button_Back
        );
    }
}