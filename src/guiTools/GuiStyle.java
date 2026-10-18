package guiTools;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/*******
 * <p> Title: GuiStyle Class. </p>
 *
 * <p> Description: The one place that decides how the non-Admin pages look.  It supplies the
 * colors, the fonts, the full-page gradient background, the white card that holds a page's
 * widgets, the header bar of the home pages, the logo, the feature cards, and the look of buttons,
 * text fields, and labels.  The pages that use it keep doing their own layout: each View still
 * places its own widgets by hand and calls this class to dress them.  Changing a color, a font
 * size, or the logo here changes every page.
 *
 * Fonts.  Every restyled page uses one font family and a small type scale: a page title, a card
 * title, body text, and small text.  The style methods below set the font themselves, so a page
 * cannot drift from the scale by choosing its own size.  This is also why the font is never put in
 * a style string: the style methods set it with setFont, and the style string only carries colors,
 * borders, and padding.
 *
 * Sizes.  Text fields, drop-downs, and buttons on a card are all the same height, so rows of
 * controls line up on every page.  Quit is a small, quiet button in the same corner of every page,
 * because it ends the program and should be easy to find but hard to hit by accident.
 *
 * All of the look is set with inline JavaFX style strings, not a style sheet.  A style sheet is a
 * resource that has to be found at run time, and that is one more thing that can go wrong in a
 * project that runs as a Java module.  An inline style does replace the style sheet's own drawing
 * of keyboard focus, hover, and pressed buttons, so this class puts those states back itself.
 *
 * The class is not instantiated.  Every method is static.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Dhruv
 *
 * @version 1.00		2026-10-16 Initial version
 * @version 1.10		2026-10-17 Polish: one font family and a type scale, consistent control
 * 							heights, the home page header bar, feature cards, the quiet Quit button,
 * 							the logo for dark backgrounds, and the Coming Soon icon.
 */

public class GuiStyle {

	/*-********************************************************************************************

	The colors used by every restyled page

	 */

	/** The darkest blue, used at the top-left of the background and for strong text. */
	public static final String COLOR_DARK = "#1e3a5f";

	/** The middle blue of the background gradient. */
	public static final String COLOR_MID = "#3b6ea5";

	/** The light blue at the bottom-right of the background gradient. */
	public static final String COLOR_LIGHT = "#8fb8de";

	/** The blue used for primary buttons, the outline of secondary buttons, and the logo mark. */
	public static final String COLOR_PRIMARY = "#2d6cdf";

	/** The color of ordinary text on a white card. */
	public static final String COLOR_TEXT = "#222222";

	/** The color of secondary text on a white card, such as a hint or a current value. */
	public static final String COLOR_MUTED = "#667085";

	/** The color of the small labels that name a field, such as "Username". */
	public static final String COLOR_LABEL = "#344054";

	/*-********************************************************************************************

	The sizes used by every restyled page

	 */

	/** The height of every text field, drop-down, and button on a card. */
	public static final double CONTROL_HEIGHT = 40;

	/** The height of the header bar across the top of a home page. */
	public static final double HEADER_HEIGHT = 64;

	/** The width of a feature card on a home page. */
	public static final double FEATURE_CARD_WIDTH = 220;

	/** The height of a feature card on a home page. */
	public static final double FEATURE_CARD_HEIGHT = 196;

	/** The top of the row of feature cards on a home page. */
	public static final double FEATURE_ROW_Y = 180;

	/** The width of the quiet Quit button. */
	private static final double QUIT_WIDTH = 80;

	/** The height of the quiet Quit button. */
	private static final double QUIT_HEIGHT = 30;

	// The font family, chosen once the first time a font is needed.  Segoe UI is the Windows system
	// font and reads well at small sizes; Arial is the fall-back on a machine that does not have it.
	private static String fontFamily = null;

	/**
	 * Default constructor is not used.  Every method in this class is static.
	 */
	public GuiStyle() {
	}

	/*-********************************************************************************************

	Fonts: one family and a small type scale

	 */

	/**********
	 * <p> Method: String family() </p>
	 *
	 * <p> Description: The one font family every restyled page uses: Segoe UI when the machine has
	 * it, otherwise Arial.  The answer is worked out once and kept. </p>
	 *
	 * @return the name of the font family
	 */
	public static String family() {
		if (fontFamily == null)
			fontFamily = Font.getFamilies().contains("Segoe UI") ? "Segoe UI" : "Arial";
		return fontFamily;
	}

	/**********
	 * <p> Method: Font fontPageTitle() </p>
	 *
	 * <p> Description: The largest step of the type scale, for the title of a page. </p>
	 *
	 * @return a bold 26 point font
	 */
	public static Font fontPageTitle() { return Font.font(family(), FontWeight.BOLD, 26); }

	/**********
	 * <p> Method: Font fontCardTitle() </p>
	 *
	 * <p> Description: The second step of the type scale, for the title of a card. </p>
	 *
	 * @return a bold 18 point font
	 */
	public static Font fontCardTitle() { return Font.font(family(), FontWeight.BOLD, 18); }

	/**********
	 * <p> Method: Font fontBody() </p>
	 *
	 * <p> Description: The third step of the type scale, for ordinary text and buttons. </p>
	 *
	 * @return a 15 point font
	 */
	public static Font fontBody() { return Font.font(family(), FontWeight.NORMAL, 15); }

	/**********
	 * <p> Method: Font fontSmall() </p>
	 *
	 * <p> Description: The smallest step of the type scale, for hints and field names. </p>
	 *
	 * @return a 13 point font
	 */
	public static Font fontSmall() { return Font.font(family(), FontWeight.NORMAL, 13); }

	/**********
	 * <p> Method: Font fontSemiBold(double size) </p>
	 * 
	 * <p> Description: A medium-weight font for button text and field names: heavier than body
	 * text, lighter than a title.  Segoe UI keeps this weight as a separate family, so it is asked
	 * for by name; where it is missing, the bold weight of the family is used. </p>
	 * 
	 * @param size	The size of the font
	 * 
	 * @return the font
	 */
	public static Font fontSemiBold(double size) {
		if (family().equals("Segoe UI") && Font.getFamilies().contains("Segoe UI Semibold"))
			return Font.font("Segoe UI Semibold", size);
		return Font.font(family(), FontWeight.BOLD, size);
	}

	/*-********************************************************************************************

	Backgrounds and shapes

	 */

	/**********
	 * <p> Method: applyBackground(Pane root) </p>
	 *
	 * <p> Description: Fills a page's whole window with the diagonal blue gradient.  The gradient
	 * is the root pane's own background, so it fills the window whatever its size. </p>
	 *
	 * @param root specifies the page's root Pane
	 */
	public static void applyBackground(Pane root) {
		root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, " + COLOR_DARK
				+ " 0%, " + COLOR_MID + " 55%, " + COLOR_LIGHT + " 100%);");
	}

	/**********
	 * <p> Method: createCard(double x, double y, double w, double h) </p>
	 *
	 * <p> Description: Builds the white, rounded, softly shadowed rectangle that a page's widgets
	 * sit on.  It is only a background shape.  It must be added to the page's pane before the
	 * widgets that sit on it so they are drawn in front of it, and a page that clears and refills
	 * its pane must put it back each time. </p>
	 *
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 * @param w		The width of the card
	 * @param h		The height of the card
	 *
	 * @return the card, ready to be added to the pane
	 */
	public static Region createCard(double x, double y, double w, double h) {
		Region card = new Region();
		card.setLayoutX(x);
		card.setLayoutY(y);
		card.setMinSize(w, h);
		card.setPrefSize(w, h);
		card.setMaxSize(w, h);
		card.setStyle("-fx-background-color: white; -fx-background-radius: 12; "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 22, 0.15, 0, 8);");
		return card;
	}

	/**********
	 * <p> Method: createSidebar(double x, double y, double w, double h) </p>
	 *
	 * <p> Description: Builds the light gray panel down the left side of a card that holds a side
	 * menu, as on the Account Update page.  Its left corners are rounded to match the card it sits
	 * on.  Like the card, it is only a background shape and must be added to the pane before the
	 * widgets that sit on it. </p>
	 *
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 * @param w		The width of the panel
	 * @param h		The height of the panel
	 *
	 * @return the panel, ready to be added to the pane
	 */
	public static Region createSidebar(double x, double y, double w, double h) {
		Region bar = new Region();
		bar.setLayoutX(x);
		bar.setLayoutY(y);
		bar.setMinSize(w, h);
		bar.setPrefSize(w, h);
		bar.setMaxSize(w, h);
		bar.setStyle("-fx-background-color: #f2f4f7; -fx-background-radius: 12 0 0 12;");
		return bar;
	}

	/**********
	 * <p> Method: createHeaderBar() </p>
	 *
	 * <p> Description: Builds the band across the top of a home page.  It is a lighter tint of the
	 * gradient with a thin line under it, so the logo, the user's name, and the account buttons sit
	 * together and apart from the page itself. </p>
	 *
	 * @return the header bar, ready to be added to the pane before the widgets that sit on it
	 */
	public static Region createHeaderBar() {
		double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
		Region bar = new Region();
		bar.setLayoutX(0);
		bar.setLayoutY(0);
		bar.setMinSize(width, HEADER_HEIGHT);
		bar.setPrefSize(width, HEADER_HEIGHT);
		bar.setMaxSize(width, HEADER_HEIGHT);
		bar.setStyle("-fx-background-color: rgba(255,255,255,0.10); "
				+ "-fx-border-color: transparent transparent rgba(255,255,255,0.25) transparent; "
				+ "-fx-border-width: 0 0 1 0;");
		return bar;
	}

	/**********
	 * <p> Method: createLogo(double x, double y) </p>
	 *
	 * <p> Description: Builds the logo for use on a white card: a blue diamond followed by the
	 * words "Lessons Learned" in dark blue. </p>
	 *
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 *
	 * @return the logo, ready to be added to the pane
	 */
	public static Pane createLogo(double x, double y) {
		return createLogo(x, y, false);
	}

	/**********
	 * <p> Method: createLogo(double x, double y, boolean onDark) </p>
	 *
	 * <p> Description: Builds the logo: a diamond followed by the words "Lessons Learned".  On a
	 * white card the diamond is blue and the words are dark blue.  On the gradient, as in the
	 * header bar, both are white so the logo stays readable.  It is drawn from plain JavaFX shapes
	 * so no image file is needed. </p>
	 *
	 * @param x			The location from the left edge (x axis)
	 * @param y			The location from the top (y axis)
	 * @param onDark	True when the logo sits on the gradient rather than on a white card
	 *
	 * @return the logo, ready to be added to the pane
	 */
	public static Pane createLogo(double x, double y, boolean onDark) {
		Pane logo = new Pane();
		logo.setLayoutX(x);
		logo.setLayoutY(y);

		Polygon diamond = new Polygon(12.0, 0.0, 24.0, 12.0, 12.0, 24.0, 0.0, 12.0);
		diamond.setFill(Color.web(onDark ? "#ffffff" : COLOR_PRIMARY));

		Label name = new Label("Lessons Learned");
		name.setFont(Font.font(family(), FontWeight.BOLD, 18));
		name.setStyle("-fx-text-fill: " + (onDark ? "white" : COLOR_DARK) + ";");
		name.setLayoutX(34);
		name.setLayoutY(-1);

		logo.getChildren().addAll(diamond, name);
		return logo;
	}

	/**********
	 * <p> Method: createFeatureCard(double x, double y, String title, String description,
	 * Button action) </p>
	 *
	 * <p> Description: Builds one card of a home page: a white card with a title, one line or two
	 * of description, and the button that opens the feature.  Every card on every home page has the
	 * same size and the same arrangement, so a row of them reads as a grid.  The button stays the
	 * page's own object, with the page's own action; this method only places and dresses it.  A
	 * card with no button (null) is an information card, used where a role has no features yet. </p>
	 *
	 * @param x				The location from the left edge (x axis)
	 * @param y				The location from the top (y axis)
	 * @param title			The title of the card
	 * @param description	A short sentence saying what the feature is for
	 * @param action		The page's button for the feature, or null for an information card
	 *
	 * @return the card with its contents, ready to be added to the pane
	 */
	public static Pane createFeatureCard(double x, double y, String title, String description,
			Button action) {
		double w = FEATURE_CARD_WIDTH, h = FEATURE_CARD_HEIGHT, pad = 20;
		Pane card = new Pane();
		card.setLayoutX(x);
		card.setLayoutY(y);

		Region background = createCard(0, 0, w, h);

		// A short blue bar at the top of the card gives each card the same accent
		Region accent = new Region();
		accent.setLayoutX(pad);
		accent.setLayoutY(pad);
		accent.setPrefSize(32, 4);
		accent.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-background-radius: 2;");

		Label titleLabel = new Label(title);
		titleLabel.setLayoutX(pad);
		titleLabel.setLayoutY(pad + 14);
		styleCardTitle(titleLabel);

		Label descriptionLabel = new Label(description);
		descriptionLabel.setLayoutX(pad);
		descriptionLabel.setLayoutY(pad + 44);
		descriptionLabel.setPrefWidth(w - 2 * pad);
		descriptionLabel.setWrapText(true);
		styleMutedLabel(descriptionLabel);

		card.getChildren().addAll(background, accent, titleLabel, descriptionLabel);

		if (action != null) {
			action.setLayoutX(pad);
			action.setLayoutY(h - pad - CONTROL_HEIGHT);
			action.setMinWidth(w - 2 * pad);
			action.setPrefWidth(w - 2 * pad);
			stylePrimaryButton(action);
			card.getChildren().add(action);
		}
		return card;
	}

	/**********
	 * <p> Method: createComingSoonIcon(double centerX, double centerY) </p>
	 *
	 * <p> Description: Builds the small clock drawn on the Coming Soon page: a pale blue disc with
	 * a blue ring and two hands.  It is drawn from plain JavaFX shapes so no image file is needed.
	 * </p>
	 *
	 * @param centerX	The x coordinate of the center of the clock
	 * @param centerY	The y coordinate of the center of the clock
	 *
	 * @return the icon, ready to be added to the pane
	 */
	public static Pane createComingSoonIcon(double centerX, double centerY) {
		double r = 30;
		Pane icon = new Pane();
		icon.setLayoutX(centerX - r);
		icon.setLayoutY(centerY - r);

		Circle face = new Circle(r, r, r);
		face.setFill(Color.web("#e8f0fe"));

		Circle ring = new Circle(r, r, r - 9);
		ring.setFill(Color.TRANSPARENT);
		ring.setStroke(Color.web(COLOR_PRIMARY));
		ring.setStrokeWidth(3);

		Line hourHand = new Line(r, r, r, r - 12);
		Line minuteHand = new Line(r, r, r + 9, r);
		for (Line hand : new Line[] { hourHand, minuteHand }) {
			hand.setStroke(Color.web(COLOR_PRIMARY));
			hand.setStrokeWidth(3);
			hand.setStrokeLineCap(StrokeLineCap.ROUND);
		}

		icon.getChildren().addAll(face, ring, hourHand, minuteHand);
		return icon;
	}

	/*-********************************************************************************************

	The layout shared by the home pages

	 */

	/**********
	 * <p> Method: placeHomeHeader(Label user, Button account, Button logout) </p>
	 * 
	 * <p> Description: Places and dresses the right-hand side of a home page's header bar: the
	 * user's name, then the Account Update and Logout buttons, aligned to the right edge.  Every
	 * home page uses this, so the header is the same on each.  The logo on the left is added with
	 * createLogo(24, 20, true). </p>
	 * 
	 * @param user		The Label showing who is signed in
	 * @param account	The Account Update Button
	 * @param logout	The Logout Button
	 */
	public static void placeHomeHeader(Label user, Button account, Button logout) {
		double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
		double logoutW = 96, accountW = 150, userW = 220, gap = 10, y = 15;

		styleHeaderButton(logout);
		logout.setMinWidth(logoutW);
		logout.setPrefWidth(logoutW);
		logout.setLayoutX(width - 24 - logoutW);
		logout.setLayoutY(y);

		styleHeaderButton(account);
		account.setMinWidth(accountW);
		account.setPrefWidth(accountW);
		account.setLayoutX(logout.getLayoutX() - gap - accountW);
		account.setLayoutY(y);

		styleHeaderLabel(user);
		user.setMinWidth(userW);
		user.setPrefWidth(userW);
		user.setMaxWidth(userW);
		user.setAlignment(Pos.CENTER_RIGHT);
		user.setLayoutX(account.getLayoutX() - 12 - userW);
		user.setLayoutY(22);
	}

	/**********
	 * <p> Method: placeHomeTitle(Label title, Label subtitle) </p>
	 * 
	 * <p> Description: Places and dresses the title of a home page and the line below it, in the
	 * same place on every home page, between the header bar and the row of feature cards. </p>
	 * 
	 * @param title		The Label with the page's title
	 * @param subtitle	The Label with the line below the title
	 */
	public static void placeHomeTitle(Label title, Label subtitle) {
		styleHeaderTitle(title);
		title.setLayoutX(40);
		title.setLayoutY(92);
		styleHeaderLabel(subtitle);
		subtitle.setLayoutX(40);
		subtitle.setLayoutY(132);
	}

	/**********
	 * <p> Method: double featureCardX(int column) </p>
	 * 
	 * <p> Description: The left edge of a feature card in a home page's row of cards.  The cards sit
	 * on a three-column grid with the same gap between them on every home page. </p>
	 * 
	 * @param column	The column, counting from 0 at the left
	 * 
	 * @return the x coordinate of the card's left edge
	 */
	public static double featureCardX(int column) {
		return 40 + column * (FEATURE_CARD_WIDTH + 30);
	}

	/*-********************************************************************************************

	Text

	 */

	/**********
	 * <p> Method: stylePageTitle(Label l) </p>
	 *
	 * <p> Description: Makes a label the title of a page shown on a white card. </p>
	 *
	 * @param l		The Label object to be styled
	 */
	public static void stylePageTitle(Label l) {
		l.setFont(fontPageTitle());
		l.setStyle("-fx-text-fill: " + COLOR_DARK + ";");
	}

	/**********
	 * <p> Method: styleHeaderTitle(Label l) </p>
	 *
	 * <p> Description: Makes a label the title of a page shown directly on the gradient, as on a
	 * home page. </p>
	 *
	 * @param l		The Label object to be styled
	 */
	public static void styleHeaderTitle(Label l) {
		l.setFont(fontPageTitle());
		l.setStyle("-fx-text-fill: white;");
	}

	/**********
	 * <p> Method: styleCardTitle(Label l) </p>
	 *
	 * <p> Description: Makes a label the bold, dark title of a card or of a part of a card. </p>
	 *
	 * @param l		The Label object to be styled
	 */
	public static void styleCardTitle(Label l) {
		l.setFont(fontCardTitle());
		l.setStyle("-fx-text-fill: " + COLOR_DARK + ";");
	}

	/**********
	 * <p> Method: styleCardLabel(Label l) </p>
	 *
	 * <p> Description: Makes a label ordinary dark body text for use on a white card. </p>
	 *
	 * @param l		The Label object to be styled
	 */
	public static void styleCardLabel(Label l) {
		l.setFont(fontBody());
		l.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");
	}

	/**********
	 * <p> Method: styleFieldLabel(Label l) </p>
	 *
	 * <p> Description: Makes a label the small name shown above a text field or a value, such as
	 * "Username". </p>
	 *
	 * @param l		The Label object to be styled
	 */
	public static void styleFieldLabel(Label l) {
		l.setFont(fontSemiBold(13));
		l.setStyle("-fx-text-fill: " + COLOR_LABEL + ";");
	}

	/**********
	 * <p> Method: styleMutedLabel(Label l) </p>
	 *
	 * <p> Description: Makes a label small gray text for use on a white card, such as a hint under
	 * a title or the description on a feature card. </p>
	 *
	 * @param l		The Label object to be styled
	 */
	public static void styleMutedLabel(Label l) {
		l.setFont(fontSmall());
		l.setStyle("-fx-text-fill: " + COLOR_MUTED + ";");
	}

	/**********
	 * <p> Method: styleHeaderLabel(Label l) </p>
	 *
	 * <p> Description: Makes a label body text for use directly on the gradient, as in the header
	 * bar of a home page. </p>
	 *
	 * @param l		The Label object to be styled
	 */
	public static void styleHeaderLabel(Label l) {
		l.setFont(fontBody());
		l.setStyle("-fx-text-fill: rgba(255,255,255,0.92);");
	}

	/**********
	 * <p> Method: setValueText(Label l, String value) </p>
	 *
	 * <p> Description: Puts a value into a label that has a fixed width, and gives it a tooltip with
	 * the same text.  A value too long for the label is cut short with an ellipsis on the page, and
	 * the tooltip is how the user can still read all of it. </p>
	 *
	 * @param l			The Label object that shows the value
	 * @param value		The value to show
	 */
	public static void setValueText(Label l, String value) {
		l.setText(value);
		l.setTooltip(new Tooltip(value));
	}

	/*-********************************************************************************************

	Controls

	 */

	/**********
	 * <p> Method: stylePrimaryButton(Button b) </p>
	 *
	 * <p> Description: Gives a button the solid blue look used for the one main action on a card,
	 * such as Log In. </p>
	 *
	 * @param b		The Button object to be styled
	 */
	public static void stylePrimaryButton(Button b) {
		sizeControl(b, CONTROL_HEIGHT);
		b.setFont(fontSemiBold(15));
		String look = "-fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; "
				+ "-fx-padding: 0 16 0 16;";
		bindButtonStates(b,
				"-fx-background-color: " + COLOR_PRIMARY + "; " + look,
				"-fx-background-color: #4f86e8; " + look,
				"-fx-background-color: #1f55b8; " + look,
				"-fx-border-color: " + COLOR_DARK + "; -fx-border-width: 2; -fx-border-radius: 8;");
	}

	/**********
	 * <p> Method: styleSecondaryButton(Button b) </p>
	 *
	 * <p> Description: Gives a button the outlined look used for every other action on a card,
	 * such as Edit, so the main action stands out. </p>
	 *
	 * @param b		The Button object to be styled
	 */
	public static void styleSecondaryButton(Button b) {
		sizeControl(b, CONTROL_HEIGHT);
		b.setFont(fontBody());
		String look = "-fx-text-fill: " + COLOR_PRIMARY + "; -fx-border-color: #c7d6f5; "
				+ "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand; "
				+ "-fx-padding: 0 16 0 16;";
		bindButtonStates(b,
				"-fx-background-color: white; " + look,
				"-fx-background-color: #eef3fd; " + look,
				"-fx-background-color: #dce7fb; " + look,
				"-fx-border-color: " + COLOR_PRIMARY + "; -fx-border-width: 2;");
	}

	/**********
	 * <p> Method: styleHeaderButton(Button b) </p>
	 *
	 * <p> Description: Gives a button the look used in the header bar of a home page: white text
	 * with a faint white outline, so it reads on the gradient without competing with the page.
	 * </p>
	 *
	 * @param b		The Button object to be styled
	 */
	public static void styleHeaderButton(Button b) {
		sizeControl(b, 34);
		b.setFont(fontSemiBold(14));
		String look = "-fx-text-fill: white; -fx-border-color: rgba(255,255,255,0.55); "
				+ "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand; "
				+ "-fx-padding: 0 14 0 14;";
		bindButtonStates(b,
				"-fx-background-color: transparent; " + look,
				"-fx-background-color: rgba(255,255,255,0.15); " + look,
				"-fx-background-color: rgba(255,255,255,0.28); " + look,
				"-fx-border-color: white; -fx-border-width: 2;");
	}

	/**********
	 * <p> Method: styleQuitButton(Button b) </p>
	 *
	 * <p> Description: Gives the Quit button its small, quiet look and puts it in the same place on
	 * every page: the bottom-right corner of the window.  Quit ends the program, so it should be
	 * easy to find but should not look like the next step on the page.  Because the position is
	 * decided here, a page must not move the button after calling this. </p>
	 *
	 * @param b		The Quit Button object to be styled and placed
	 */
	public static void styleQuitButton(Button b) {
		double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
		double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
		b.setMinWidth(QUIT_WIDTH);
		b.setPrefWidth(QUIT_WIDTH);
		sizeControl(b, QUIT_HEIGHT);
		b.setLayoutX(width - QUIT_WIDTH - 16);
		b.setLayoutY(height - QUIT_HEIGHT - 14);
		b.setAlignment(Pos.CENTER);
		b.setFont(fontSmall());
		String look = "-fx-text-fill: rgba(255,255,255,0.85); -fx-background-radius: 8; "
				+ "-fx-cursor: hand; -fx-padding: 0 10 0 10;";
		bindButtonStates(b,
				"-fx-background-color: transparent; " + look,
				"-fx-background-color: rgba(255,255,255,0.15); " + look,
				"-fx-background-color: rgba(255,255,255,0.28); " + look,
				"-fx-border-color: white; -fx-border-width: 1; -fx-border-radius: 8;");
	}

	/**********
	 * <p> Method: styleMenuButton(Button b, boolean selected) </p>
	 *
	 * <p> Description: Gives a button the flat look of an item in a side menu.  The item for the
	 * page being shown is drawn selected: a tinted background and bold text. </p>
	 *
	 * @param b			The Button object to be styled
	 * @param selected	True if this is the menu item for the page being shown
	 */
	public static void styleMenuButton(Button b, boolean selected) {
		sizeControl(b, CONTROL_HEIGHT);
		b.setAlignment(Pos.CENTER_LEFT);
		b.setFont(selected ? fontSemiBold(15) : fontBody());
		String focus = "-fx-border-color: " + COLOR_PRIMARY + "; -fx-border-width: 2; -fx-border-radius: 8;";
		String look = "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 0 12 0 12; ";
		if (selected) {
			look = look + "-fx-text-fill: " + COLOR_PRIMARY + ";";
			bindButtonStates(b, "-fx-background-color: #e3ebfb; " + look, "-fx-background-color: #e3ebfb; " + look,
					"-fx-background-color: #d3e0f9; " + look, focus);
		} else {
			look = look + "-fx-text-fill: " + COLOR_TEXT + ";";
			bindButtonStates(b, "-fx-background-color: transparent; " + look, "-fx-background-color: #e6e9ef; " + look,
					"-fx-background-color: #dce0e8; " + look, focus);
		}
	}

	/**********
	 * <p> Method: styleField(TextField t) </p>
	 *
	 * <p> Description: Gives a text field, or a password field, a thin rounded outline and the
	 * standard control height.  Both are TextFields, so the one method serves them. </p>
	 *
	 * @param t		The TextField object to be styled
	 */
	public static void styleField(TextField t) {
		sizeControl(t, CONTROL_HEIGHT);
		t.setAlignment(Pos.CENTER_LEFT);
		t.setFont(fontBody());
		bindFocusRing(t, "-fx-background-color: white; -fx-background-insets: 0; "
				+ "-fx-border-color: #d0d5dd; -fx-border-radius: 8; -fx-background-radius: 8; "
				+ "-fx-text-fill: " + COLOR_TEXT + "; -fx-prompt-text-fill: #98a2b3; "
				+ "-fx-padding: 0 12 0 12;");
	}

	/**********
	 * <p> Method: styleComboBox(ComboBox c) </p>
	 *
	 * <p> Description: Gives a drop-down list the same outline, height, and font as a text field.
	 * The font goes in the style string here, unlike everywhere else, because a drop-down draws its
	 * text in an inner cell that only a style can reach.  Any style the page set before is
	 * replaced, since the font it carried is now decided here. </p>
	 *
	 * @param c		The ComboBox object to be styled
	 */
	public static void styleComboBox(ComboBox<String> c) {
		sizeControl(c, CONTROL_HEIGHT);
		bindFocusRing(c, "-fx-font-family: '" + family() + "'; -fx-font-size: 15px; "
				+ "-fx-background-color: white; -fx-background-insets: 0; "
				+ "-fx-border-color: #d0d5dd; -fx-border-radius: 8; -fx-background-radius: 8;");
	}

	/**********
	 * <p> Method: startUnfocused(Pane root) </p>
	 * 
	 * <p> Description: Opens a page with no button holding the keyboard focus.  JavaFX otherwise
	 * gives the focus to the first button on the page, which then wears its focus ring and looks
	 * as if it has already been chosen.  Pages whose first control is a field to type in do not
	 * call this, because there the ring rightly shows where typing will go.  The Tab key still
	 * moves through the page's buttons as before.  Call it after the page has been shown. </p>
	 * 
	 * @param root	The page's root Pane
	 */
	public static void startUnfocused(Pane root) {
		root.requestFocus();
	}

	/*-********************************************************************************************

	Private helpers

	 */

	/**********
	 * Private local method that fixes a control at a height, so rows of controls line up.
	 *
	 * @param c		The control
	 * @param h		The height
	 */
	private static void sizeControl(Control c, double h) {
		c.setMinHeight(h);
		c.setPrefHeight(h);
		c.setMaxHeight(h);
	}

	/**********
	 * Private local method that gives a button its look for each state.  An inline style replaces
	 * the stylesheet rules that normally draw the highlight for keyboard focus, the mouse resting
	 * on a button, and a button being pressed, so a button styled in-line would show none of them.
	 * This method puts them back: the style string is rebuilt whenever one of the three changes.
	 * A button that is styled a second time keeps its old listeners, which is harmless because the
	 * newest style strings are the ones they read.
	 *
	 * @param b				The Button object to be styled
	 * @param normal		The style when the button is idle
	 * @param hover			The style while the mouse rests on the button
	 * @param armed			The style while the button is pressed
	 * @param focusExtra	Style added on top of the others while the button has keyboard focus
	 */
	private static void bindButtonStates(Button b, String normal, String hover, String armed,
			String focusExtra) {
		String[] styles = { normal, hover, armed, focusExtra };
		Object old = b.getProperties().get(STATE_KEY);
		b.getProperties().put(STATE_KEY, styles);
		Runnable update = () -> {
			String[] s = (String[]) b.getProperties().get(STATE_KEY);
			String style = b.isArmed() ? s[2] : (b.isHover() ? s[1] : s[0]);
			if (b.isFocused()) style = style + " " + s[3];
			b.setStyle(style);
		};
		if (old == null) {
			b.hoverProperty().addListener((_, _, _) -> update.run());
			b.armedProperty().addListener((_, _, _) -> update.run());
			b.focusedProperty().addListener((_, _, _) -> update.run());
		}
		update.run();
	}

	// The key under which a button keeps its current style strings, so restyling it does not add
	// a second set of listeners
	private static final String STATE_KEY = "guiStyleStates";

	/**********
	 * Private local method that gives a text field or a drop-down a blue outline while it has
	 * keyboard focus, for the same reason bindButtonStates exists: the inline style replaces the
	 * stylesheet's own focus highlight.
	 *
	 * @param c			The control to be styled
	 * @param normal	The style when the control does not have focus
	 */
	private static void bindFocusRing(Control c, String normal) {
		String focused = normal + " -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-width: 2;";
		c.setStyle(c.isFocused() ? focused : normal);
		c.focusedProperty().addListener((_, _, isFocused) -> c.setStyle(isFocused ? focused : normal));
	}
}
