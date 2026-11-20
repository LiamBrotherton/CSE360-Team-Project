package guiCurator;

import java.util.ArrayList;
import java.util.List;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;
import guiTools.GuiStyle;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * JavaFX interface for the HW3 Curator lesson-lock prototype.
 *
 * <p>A curator selects a stored lesson and changes the lock flags for
 * its title, body, or category. Lock state is reloaded after each change.
 * This is a prototype: three core-field locks are not a transaction, and
 * the underlying database methods still need authorization enforcement.</p>
 *
 * @author Hassan Mohamed
 * @version 1.0 2026-11-17
 */
public final class ViewCuratorLocks {
    private final Stage stage;
    private final User user;
    private final Database db;
    private final Pane root = new Pane();
    private final ListView<String> lessonsList = new ListView<>();
    private final List<Integer> lessonIds = new ArrayList<>();
    private final Label lockStatus = new Label("Select a lesson to see its locks.");
    private final Label message = new Label("");

    private ViewCuratorLocks(Stage stage, User user, Database db) {
        this.stage = stage;
        this.user = user;
        this.db = db;
        buildPage();
    }

    /**
     * Displays the lock manager for a curator.
     *
     * @param stage the application's existing primary stage
     * @param user the signed-in user
     */
    public static void displayCuratorLocks(Stage stage, User user) {
        if (stage == null || user == null || !user.getCuratorRole()) {
            // Why: a non-curator must not be given the locking interface.
            return;
        }
        ViewCuratorLocks page = new ViewCuratorLocks(stage, user, FoundationsMain.database);
        page.reloadLessons(-1);
        stage.setTitle("CSE 360: Curator Lesson Locks");
        stage.setScene(new Scene(page.root, FoundationsMain.WINDOW_WIDTH,
                FoundationsMain.WINDOW_HEIGHT));
        stage.show();
        GuiStyle.startUnfocused(page.root);
    }

    /** Creates and connects the controls for this page. */
    private void buildPage() {
        GuiStyle.applyBackground(root);

        Label title = new Label("Manage Lesson Locks");
        title.setLayoutX(70);
        title.setLayoutY(104);
        GuiStyle.stylePageTitle(title);

        Label explanation = new Label("Select a lesson, then lock or unlock a core field.");
        explanation.setLayoutX(70);
        explanation.setLayoutY(142);
        GuiStyle.styleMutedLabel(explanation);

        lessonsList.setLayoutX(70);
        lessonsList.setLayoutY(180);
        lessonsList.setPrefSize(323, 243);
        GuiStyle.styleList(lessonsList);
        lessonsList.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldIndex, newIndex) -> refreshSelectedStatus());

        lockStatus.setLayoutX(416);
        lockStatus.setLayoutY(180);
        lockStatus.setPrefWidth(300);
        lockStatus.setWrapText(true);
        GuiStyle.styleMutedLabel(lockStatus);

        Button lockTitle = makeButton("Lock title", 416, 258, true);
        Button unlockTitle = makeButton("Unlock title", 556, 258, false);
        Button lockBody = makeButton("Lock body", 416, 302, true);
        Button unlockBody = makeButton("Unlock body", 556, 302, false);
        Button lockCategory = makeButton("Lock category", 416, 346, true);
        Button unlockCategory = makeButton("Unlock category", 556, 346, false);

        lockTitle.setOnAction(event -> changeOne("title", true));
        unlockTitle.setOnAction(event -> changeOne("title", false));
        lockBody.setOnAction(event -> changeOne("body", true));
        unlockBody.setOnAction(event -> changeOne("body", false));
        lockCategory.setOnAction(event -> changeOne("category", true));
        unlockCategory.setOnAction(event -> changeOne("category", false));

        Button lockAll = makeButton("Lock all core", 416, 400, true);
        Button unlockAll = makeButton("Unlock all core", 556, 400, false);
        lockAll.setOnAction(event -> changeAll(true));
        unlockAll.setOnAction(event -> changeAll(false));

        message.setLayoutX(70);
        message.setLayoutY(445);
        message.setPrefWidth(650);
        message.setWrapText(true);
        GuiStyle.styleErrorLabel(message);

        Button refresh = makeButton("Refresh", 70, 487, false);
        refresh.setOnAction(event -> reloadLessons(selectedLessonId()));

        Button back = makeButton("Back to Curator Home", 212, 487, false);
        back.setPrefWidth(215);
        back.setOnAction(event -> ViewCuratorHome.displayCuratorHome(stage, user));

        root.getChildren().addAll(
                GuiStyle.createCard(40, 40, 720, 520),
                GuiStyle.createLogo(70, 66),
                title, explanation, lessonsList, lockStatus,
                lockTitle, unlockTitle, lockBody, unlockBody,
                lockCategory, unlockCategory, lockAll, unlockAll,
                message, refresh, back);
    }

    /** Places and styles one action button. */
    private static Button makeButton(String text, double x, double y, boolean primary) {
        Button button = new Button(text);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefWidth(133);
        button.setMinHeight(36);
        if (primary) GuiStyle.stylePrimaryButton(button);
        else GuiStyle.styleSecondaryButton(button);
        return button;
    }

    /** Returns the selected database identifier, or -1 for no selection. */
    private int selectedLessonId() {
        int selected = lessonsList.getSelectionModel().getSelectedIndex();
        return selected >= 0 && selected < lessonIds.size() ? lessonIds.get(selected) : -1;
    }

    /** Reloads stored lessons so the interface reflects the current database. */
    private void reloadLessons(int keepId) {
        lessonIds.clear();
        lessonsList.getItems().clear();
        int selectedIndex = -1;
        // Why: read saved records fresh rather than displaying stale lock information.
        for (var lesson : db.getAllLessons().getLessons()) {
            if (lesson.getId() == keepId) selectedIndex = lessonIds.size();
            lessonIds.add(lesson.getId());
            String title = lesson.getTitle() == null ? "(untitled)" : lesson.getTitle();
            title = title.replace('\n', ' ').replace('\r', ' ');
            if (title.length() > 35) title = title.substring(0, 35) + "...";
            lessonsList.getItems().add("#" + lesson.getId() + "  " + title);
        }
        if (lessonIds.isEmpty()) {
            lockStatus.setText("No lessons are available yet. Add a lesson using the Contributor role.");
            message.setText("");
            return;
        }
        lessonsList.getSelectionModel().select(selectedIndex >= 0 ? selectedIndex : 0);
        refreshSelectedStatus();
    }

    /** Displays the three current flags for the selected lesson. */
    private void refreshSelectedStatus() {
        int id = selectedLessonId();
        if (id < 1) {
            lockStatus.setText("Select a lesson to see its locks.");
            return;
        }
        var lesson = db.getLessonById(id);
        if (lesson == null) {
            lockStatus.setText("This lesson no longer exists. Click Refresh.");
            return;
        }
        lockStatus.setText("Lesson #" + id + " lock status:\n"
                + "Title: " + (lesson.getTitleLocked() ? "LOCKED" : "Unlocked") + "\n"
                + "Body: " + (lesson.getBodyLocked() ? "LOCKED" : "Unlocked") + "\n"
                + "Category: " + (lesson.getCategoryLocked() ? "LOCKED" : "Unlocked"));
    }

    /** Changes a single field, then verifies its displayed status. */
    private void changeOne(String field, boolean locked) {
        int id = selectedLessonId();
        if (id < 1) {
            message.setText("Choose a lesson first.");
            return;
        }
        String error = ModelCuratorLock.setFieldLock(db, user, id, field, locked);
        message.setText(error.isEmpty()
                ? (field + (locked ? " locked." : " unlocked.")) : error);
        reloadLessons(id);
    }

    /** Changes all three core fields; this is not an atomic full-lesson lock. */
    private void changeAll(boolean locked) {
        int id = selectedLessonId();
        if (id < 1) {
            message.setText("Choose a lesson first.");
            return;
        }
        String error = ModelCuratorLock.setAllCoreFieldsLocked(db, user, id, locked);
        message.setText(error.isEmpty()
                ? (locked ? "All three core fields locked." : "All three core fields unlocked.")
                : error);
        reloadLessons(id);
    }
}
