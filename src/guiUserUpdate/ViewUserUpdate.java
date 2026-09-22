package guiUserUpdate;

import java.util.Optional;

import database.Database;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;

public class ViewUserUpdate {

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	private static Label label_ApplicationTitle = new Label("Update a User's Account Details");
	private static Label label_Purpose = new Label(" Use this page to define or update your account information."); 
    
	private static Label label_Username = new Label("Username:");
	private static Label label_Password = new Label("Password:");
	private static Label label_FirstName = new Label("First Name:");
	private static Label label_MiddleName = new Label("Middle Name:");
	private static Label label_LastName = new Label("Last Name:");
	private static Label label_PreferredFirstName = new Label("Preferred First Name:");
	private static Label label_EmailAddress = new Label("Email Address:");
	
	private static Label label_CurrentUsername = new Label();
	private static Label label_CurrentPassword = new Label();
	private static Label label_CurrentFirstName = new Label();
	private static Label label_CurrentMiddleName = new Label();
	private static Label label_CurrentLastName = new Label();
	private static Label label_CurrentPreferredFirstName = new Label();
	private static Label label_CurrentEmailAddress = new Label();
	
	private static Button button_UpdateUsername = new Button("Update Username");
	private static Button button_UpdatePassword = new Button("Update Password");
	private static Button button_UpdateFirstName = new Button("Update First Name");
	private static Button button_UpdateMiddleName = new Button("Update Middle Name");
	private static Button button_UpdateLastName = new Button("Update Last Name");
	private static Button button_UpdatePreferredFirstName = new Button("Update Preferred First Name");
	private static Button button_UpdateEmailAddress = new Button("Update Email Address");

	private static Button button_ProceedToUserHomePage = new Button("Proceed to the User Home Page");
	
	private static TextInputDialog dialogUpdateFirstName;
	private static TextInputDialog dialogUpdateMiddleName;
	private static TextInputDialog dialogUpdateLastName;
	private static TextInputDialog dialogUpdatePreferredFirstName;
	private static TextInputDialog dialogUpdateEmailAddresss;

	// UI Alert for validation errors
	public static Alert alertUpdateError = new Alert(AlertType.ERROR);
	
	private static ViewUserUpdate theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	private static Stage theStage;
	private static Pane theRootPane;
	private static User theUser;

	public static Scene theUserUpdateScene = null;
	private static Optional<String> result;

	public static void displayUserUpdate(Stage ps, User user) {
		theUser = user;
		theStage = ps;
		
		if (theView == null) theView = new ViewUserUpdate();
		
		String s = theUser.getUserName();
		System.out.println("*** Fetching account data for user: " + s);
		if (s == null || s.length() < 1) label_CurrentUsername.setText("<none>");
		else label_CurrentUsername.setText(s);
		
		s = theUser.getPassword();
		if (s == null || s.length() < 1) label_CurrentPassword.setText("<none>");
		else label_CurrentPassword.setText(s);
		
		s = theUser.getFirstName();
		if (s == null || s.length() < 1) label_CurrentFirstName.setText("<none>");
		else label_CurrentFirstName.setText(s);
       
		s = theUser.getMiddleName();
		if (s == null || s.length() < 1) label_CurrentMiddleName.setText("<none>");
		else label_CurrentMiddleName.setText(s);
        
		s = theUser.getLastName();
		if (s == null || s.length() < 1) label_CurrentLastName.setText("<none>");
		else label_CurrentLastName.setText(s);
        
		s = theUser.getPreferredFirstName();
		if (s == null || s.length() < 1) label_CurrentPreferredFirstName.setText("<none>");
		else label_CurrentPreferredFirstName.setText(s);
        
		s = theUser.getEmailAddress();
		if (s == null || s.length() < 1) label_CurrentEmailAddress.setText("<none>");
		else label_CurrentEmailAddress.setText(s);

		theStage.setTitle("CSE 360 Foundation Code: Update User Account Details");
		theStage.setScene(theUserUpdateScene);
		theStage.show();
	}

	private ViewUserUpdate() {
		theRootPane = new Pane();
		theUserUpdateScene = new Scene(theRootPane, width, height);

		dialogUpdateFirstName = new TextInputDialog("");
		dialogUpdateMiddleName = new TextInputDialog("");
		dialogUpdateLastName = new TextInputDialog("");
		dialogUpdatePreferredFirstName = new TextInputDialog("");
		dialogUpdateEmailAddresss = new TextInputDialog("");

		dialogUpdateFirstName.setTitle("Update First Name");
		dialogUpdateFirstName.setHeaderText("Update your First Name");
		
		dialogUpdateMiddleName.setTitle("Update Middle Name");
		dialogUpdateMiddleName.setHeaderText("Update your Middle Name");
		
		dialogUpdateLastName.setTitle("Update Last Name");
		dialogUpdateLastName.setHeaderText("Update your Last Name");
		
		dialogUpdatePreferredFirstName.setTitle("Update Preferred First Name");
		dialogUpdatePreferredFirstName.setHeaderText("Update your Preferred First Name");
		
		dialogUpdateEmailAddresss.setTitle("Update Email Address");
		dialogUpdateEmailAddresss.setHeaderText("Update your Email Address");

		alertUpdateError.setTitle("Update Error");
		alertUpdateError.setHeaderText("Invalid Input");

		setupLabelUI(label_ApplicationTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
		setupLabelUI(label_Purpose, "Arial", 20, width, Pos.CENTER, 0, 50);
        
		setupLabelUI(label_Username, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 100);
		setupLabelUI(label_CurrentUsername, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 100);
		setupButtonUI(button_UpdateUsername, "Dialog", 18, 275, Pos.CENTER, 500, 93);
       
		setupLabelUI(label_Password, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 150);
		setupLabelUI(label_CurrentPassword, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 150);
		setupButtonUI(button_UpdatePassword, "Dialog", 18, 275, Pos.CENTER, 500, 143);
		button_UpdatePassword.setOnAction((_) -> {
			System.out.println("**** Calling doCallResetPassword");
			ControllerUserUpdate.doCallResetPassword(theStage, theUser);
		});
        
		// First Name
		setupLabelUI(label_FirstName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 200);
		setupLabelUI(label_CurrentFirstName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 200);
		setupButtonUI(button_UpdateFirstName, "Dialog", 18, 275, Pos.CENTER, 500, 193);
		button_UpdateFirstName.setOnAction((_) -> {
			result = dialogUpdateFirstName.showAndWait();
			result.ifPresent(value -> {
				if (ControllerUserUpdate.invalidName(value, "First Name")) {
					alertUpdateError.setContentText(ControllerUserUpdate.nameErrorMessage);
					alertUpdateError.showAndWait();
					return;
				}
				theDatabase.updateFirstName(theUser.getUserName(), value);
				theDatabase.getUserAccountDetails(theUser.getUserName());
				String newName = theDatabase.getCurrentFirstName();
				theUser.setFirstName(newName);
				label_CurrentFirstName.setText((newName == null || newName.length() < 1) ? "<none>" : newName);
			});
		});
                
		// Middle Name
		setupLabelUI(label_MiddleName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 250);
		setupLabelUI(label_CurrentMiddleName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 250);
		setupButtonUI(button_UpdateMiddleName, "Dialog", 18, 275, Pos.CENTER, 500, 243);
		button_UpdateMiddleName.setOnAction((_) -> {
			result = dialogUpdateMiddleName.showAndWait();
			result.ifPresent(value -> {
				if (ControllerUserUpdate.invalidName(value, "Middle Name")) {
					alertUpdateError.setContentText(ControllerUserUpdate.nameErrorMessage);
					alertUpdateError.showAndWait();
					return;
				}
				theDatabase.updateMiddleName(theUser.getUserName(), value);
				theDatabase.getUserAccountDetails(theUser.getUserName());
				String newName = theDatabase.getCurrentMiddleName();
				theUser.setMiddleName(newName);
				label_CurrentMiddleName.setText((newName == null || newName.length() < 1) ? "<none>" : newName);
			});
		});
        
		// Last Name
		setupLabelUI(label_LastName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 300);
		setupLabelUI(label_CurrentLastName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 300);
		setupButtonUI(button_UpdateLastName, "Dialog", 18, 275, Pos.CENTER, 500, 293);
		button_UpdateLastName.setOnAction((_) -> {
			result = dialogUpdateLastName.showAndWait();
			result.ifPresent(value -> {
				if (ControllerUserUpdate.invalidName(value, "Last Name")) {
					alertUpdateError.setContentText(ControllerUserUpdate.nameErrorMessage);
					alertUpdateError.showAndWait();
					return;
				}
				theDatabase.updateLastName(theUser.getUserName(), value);
				theDatabase.getUserAccountDetails(theUser.getUserName());
				String newName = theDatabase.getCurrentLastName();
				theUser.setLastName(newName);
				label_CurrentLastName.setText((newName == null || newName.length() < 1) ? "<none>" : newName);
			});
		});
        
		// Preferred First Name
		setupLabelUI(label_PreferredFirstName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 350);
		setupLabelUI(label_CurrentPreferredFirstName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 350);
		setupButtonUI(button_UpdatePreferredFirstName, "Dialog", 18, 275, Pos.CENTER, 500, 343);
		button_UpdatePreferredFirstName.setOnAction((_) -> {
			result = dialogUpdatePreferredFirstName.showAndWait();
			result.ifPresent(value -> {
				if (ControllerUserUpdate.invalidName(value, "Preferred First Name")) {
					alertUpdateError.setContentText(ControllerUserUpdate.nameErrorMessage);
					alertUpdateError.showAndWait();
					return;
				}
				theDatabase.updatePreferredFirstName(theUser.getUserName(), value);
				theDatabase.getUserAccountDetails(theUser.getUserName());
				String newName = theDatabase.getCurrentPreferredFirstName();
				theUser.setPreferredFirstName(newName);
				label_CurrentPreferredFirstName.setText((newName == null || newName.length() < 1) ? "<none>" : newName);
			});
		});
        
		// Email Address
		setupLabelUI(label_EmailAddress, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 400);
		setupLabelUI(label_CurrentEmailAddress, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 400);
		setupButtonUI(button_UpdateEmailAddress, "Dialog", 18, 275, Pos.CENTER, 500, 393);
		button_UpdateEmailAddress.setOnAction((_) -> {
			result = dialogUpdateEmailAddresss.showAndWait();
			result.ifPresent(email -> {
				if (ControllerUserUpdate.invalidEmailAddress(email)) {
					alertUpdateError.setContentText(ControllerUserUpdate.emailAddressErrorMessage);
					alertUpdateError.showAndWait();
					return;
				}
				theDatabase.updateEmailAddress(theUser.getUserName(), email);
				theDatabase.getUserAccountDetails(theUser.getUserName());
				String newEmail = theDatabase.getCurrentEmailAddress();
				theUser.setEmailAddress(newEmail);
				label_CurrentEmailAddress.setText((newEmail == null || newEmail.length() < 1) ? "<none>" : newEmail);
			});
		});
        
		// Proceed Button
		setupButtonUI(button_ProceedToUserHomePage, "Dialog", 18, 300, Pos.CENTER, width / 2 - 150, 450);
		button_ProceedToUserHomePage.setOnAction((_) -> ControllerUserUpdate.goToUserHomePage(theStage, theUser));
    	
		theRootPane.getChildren().addAll(
				label_ApplicationTitle, label_Purpose, label_Username,
				label_CurrentUsername, 
				label_Password, label_CurrentPassword, 
				button_UpdatePassword, 
				label_FirstName, label_CurrentFirstName, button_UpdateFirstName,
				label_MiddleName, label_CurrentMiddleName, button_UpdateMiddleName,
				label_LastName, label_CurrentLastName, button_UpdateLastName,
				label_PreferredFirstName, label_CurrentPreferredFirstName,
				button_UpdatePreferredFirstName, button_UpdateEmailAddress,
				label_EmailAddress, label_CurrentEmailAddress, 
				button_ProceedToUserHomePage);
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