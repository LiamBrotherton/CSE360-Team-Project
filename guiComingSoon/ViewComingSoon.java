package guiComingSoon;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import entityClasses.User;
import guiTools.GuiStyle;

/*******
 * <p> Title: ViewComingSoon Class. </p>
 *
 * <p> Description: The Java/FX-based Coming Soon Page.  This is the placeholder every TP2 lesson
 * feature button opens until that feature has been built.  It says which feature was asked for,
 * says that it is coming soon, and offers a Back button that returns to the home page of the role
 * the user is playing.
 *
 * One page serves every placeholder.  The name of the feature is passed in each time, so adding
 * another placeholder needs no new page.  When a feature is built, its home page button is pointed
 * at the real page and nothing here changes.
 *
 * The class has been written using a singleton design pattern and is the View portion of the
 * Model, View, Controller pattern.  All access starts by invoking the static method
 * displayComingSoon.  No other method should attempt to instantiate this class as that is
 * controlled by displayComingSoon.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-16 Initial version
 * @version 1.10		2026-10-17 Polish: a drawn clock icon, a hint below the message, centered
 * 							text, and the shared font and type scale.
 *
 */

public class ViewComingSoon {

	/*-*******************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// These are the widget attributes for the GUI.  The page is one card that holds the logo, the
	// message, and the Back button.
	private static Label label_Message = new Label();
	private static Label label_Hint = new Label("This feature is still being built.");
	private static Button button_Back = new Button("Back");

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewComingSoon theView;		// Used to determine if instantiation of the class
												// is needed

	/** The Stage that JavaFX has established for us. */
	protected static Stage theStage;
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	/** The current logged in User. */
	protected static User theUser;

	private static Scene theComingSoonScene;	// The shared Scene each invocation populates

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayComingSoon(Stage ps, User user, String featureName) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Coming Soon page to be displayed.
	 *
	 * It first sets up the shared attributes so we don't have to pass parameters.  It then checks
	 * to see if the page has been setup.  If not, it instantiates the class, which initializes all
	 * the static aspects of the GUI widgets.  After the instantiation, it fills in the one element
	 * that changes, the message naming the feature, sets the Scene onto the stage, and makes it
	 * visible to the user.</p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 *
	 * @param user specifies the User for this GUI and it's methods
	 *
	 * @param featureName specifies the name of the feature that is not yet available
	 *
	 */
	public static void displayComingSoon(Stage ps, User user, String featureName) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewComingSoon();

		// Populate the dynamic aspect of the GUI: which feature this is
		label_Message.setText(featureName + " is coming soon");

		// Set the title for the window, display the page, and wait for the user to go back
		theStage.setTitle("CSE 360 Foundation Code: Coming Soon");
		theStage.setScene(theComingSoonScene);
		theStage.show();
		GuiStyle.startUnfocused(theRootPane);	// No button looks pressed on arrival
	}

	/**********
	 * <p> Method: ViewComingSoon() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and event handlers for each GUI
	 * object.</p>
	 *
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * message using the displayComingSoon method.</p>
	 *
	 */
	private ViewComingSoon() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theComingSoonScene = new Scene(theRootPane, width, height);
		GuiStyle.applyBackground(theRootPane);

		// The card is centered in the window, and everything else sits on it
		double cardW = 400, cardH = 300;
		double cardX = (width - cardW) / 2, cardY = (height - cardH) / 2;
		double innerX = cardX + 40, innerW = cardW - 80;
		
		// The message, centered on the card below the icon.  It wraps onto a second line when the
		// feature's name is long, rather than running off the card.
		setupLabelUI(label_Message, "Arial", 18, innerW, Pos.CENTER, 0, 0);
		label_Message.setPrefWidth(innerW);
		label_Message.setMaxWidth(innerW);
		label_Message.setWrapText(true);
		label_Message.setTextAlignment(TextAlignment.CENTER);
		GuiStyle.styleCardTitle(label_Message);
		
		// A line below it saying why the page is here.  The two are stacked in a column so the hint
		// always sits just below the message, whether the message takes one line or two.
		setupLabelUI(label_Hint, "Arial", 13, innerW, Pos.CENTER, 0, 0);
		label_Hint.setPrefWidth(innerW);
		label_Hint.setMaxWidth(innerW);
		label_Hint.setWrapText(true);
		label_Hint.setTextAlignment(TextAlignment.CENTER);
		GuiStyle.styleMutedLabel(label_Hint);
		
		// The Back button returns to the role's home page
		setupButtonUI(button_Back, "Dialog", 15, 200, Pos.CENTER, (width - 200) / 2, cardY + cardH - 40 - 36);
		GuiStyle.stylePrimaryButton(button_Back);
		button_Back.setOnAction((_) -> {ControllerComingSoon.performBack(); });
		
		// Place all of the widget items into the Root Pane's list of children, the card first so it
		// is drawn behind the rest
		theRootPane.getChildren().addAll(
				GuiStyle.createCard(cardX, cardY, cardW, cardH),
				GuiStyle.createLogo(innerX, cardY + 28),
				GuiStyle.createComingSoonIcon(width / 2, cardY + 108),
				textColumn(innerX, cardY + 150, innerW), button_Back);
	}

	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	/**********
	 * Private local method that stacks the message and the hint in a centered column, with a small
	 * gap between them.
	 * 
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 * @param w		The width of the column
	 * 
	 * @return the column holding the two labels
	 */
	private static VBox textColumn(double x, double y, double w) {
		VBox column = new VBox(8, label_Message, label_Hint);
		column.setAlignment(Pos.TOP_CENTER);
		column.setLayoutX(x);
		column.setLayoutY(y);
		column.setPrefWidth(w);
		return column;
	}

	/**********
	 * Private local method to initialize the standard fields for a label
	 *
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Label
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	/**********
	 * Private local method to initialize the standard fields for a button
	 *
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}
