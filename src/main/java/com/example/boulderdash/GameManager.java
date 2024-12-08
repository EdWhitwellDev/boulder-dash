package com.example.boulderdash;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Falling.FallingObject;
import com.example.boulderdash.enums.KeyColours;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Pos;
import javafx.stage.Screen;
import javafx.util.Duration;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Comparator;

/**
 * Main controller for the game. This class handles game initialisation,
 * user input, game state management, and the main game loop.
 */
public class GameManager extends Application {
    // Constants
    private static final ImageView DIAMOND_COUNT_ICON =
            new ImageView(new Image("Actor Images/diamond.png"));
    private static final ImageView CLOCK_ICON =
            new ImageView(new Image("clock.png"));
    private static final ImageView KEY_ICON_BLUE =
            new ImageView(new Image("Key Icon Images/blue_key_icon.png"));
    private static final ImageView KEY_ICON_RED =
            new ImageView(new Image("Key Icon Images/red_key_icon.png"));
    private static final ImageView KEY_ICON_GREEN =
            new ImageView(new Image("Key Icon Images/green_key_icon.png"));
    private static final ImageView KEY_ICON_YELLOW =
            new ImageView(new Image("Key Icon Images/yellow_key_icon.png"));
    private static final int NUMBER_OF_LEVELS = 5;
    private static final double TILE_SIZE_SCALE_FACTOR = 0.8;
    private static final int MAX_SCORES_AMOUNT = 10;
    private static final String FONT_ARIAL = "Arial";
    private static final int FONT_SIZE_GAME_OVER = 75;
    private static final int FONT_SIZE_TITLE = 40;
    private static final int FONT_SIZE_CURRENT_USER = 20;
    private static final int FONT_SIZE_HIGH_SCORE = 25;
    private static final int FONT_SIZE_LEVELS_LABEL = 20;
    private static final int FONT_SIZE_USER_MENU = 30;
    private static final int FONT_SIZE_NO_SCORES = 18;
    private static final int FONT_SIZE_SCORE_LABEL = 18;
    private static final int VBOX_SPACING = 20;
    private static final int HBOX_SPACING = 10;
    private static final int LOGO_HEIGHT = 200;
    private static final int LOGO_WIDTH = 400;
    private static final int LEVELS_LIST_WIDTH = 400;
    private static final int LEVELS_LIST_HEIGHT = 300;
    private static final int USER_LIST_MAX_WIDTH = 500;
    private static final double ICON_SCALE = 0.5;
    private static final double ICON_WIDTH_SCALE_CLOCK = 0.4;
    private static final double INFO_BAR_HEIGHT_RATIO = 0.7;
    private static final int PAUSE_MENU_LAYOUT_OFFSET = 200;
    private static final int PAUSE_MENU_MAX_WIDTH = 250;
    private static final int PAUSE_MENU_MAX_HEIGHT = 200;
    private static final int BUTTON_WIDTH = 150;
    private static final int SCORE_MULTIPLIER_DIAMONDS = 10;
    private static final int USER_LIST_ITEM_HEIGHT = 35;
    private static final int PAUSE_MENU_SPACING = 5;

    // Attributes
    private String deathCause = "";
    private List<Actor> deadActors = new ArrayList<>();
    private List<Actor> newBorns = new ArrayList<>();
    private Timeline tickTimeline;
    private Level level;
    private Player player;
    private Scene scene;
    private Scene homeScene;
    private Pane levelCompleteMenu;
    private Pane gameOverMenu;
    private VBox pauseMenu;
    private String currentUser;
    private Stage primaryStage;
    private Label currentUserLabel;
    private Map<Integer, String> highScores;
    private JSONObject playerProfileObj;
    private JSONObject userProfileObj;
    StackPane stackPane = new StackPane();
    private final GridPane grid = new GridPane();
    private final Pane transitionPane = new Pane();
    private final HBox infoBar = new HBox(20);
    private final Label timeLabel = new Label();
    private final Label diamondsLabel = new Label();
    private final Label keyLabelBlue = new Label();
    private final Label keyLabelRed = new Label();
    private final Label keyLabelGreen = new Label();
    private final Label keyLabelYellow = new Label();
    private final float tickTime = 0.1f;
    private float timeElapsed;
    private int tileSize;
    private int currentLevel = 1;
    private boolean dead = false;
    private boolean isPaused = false;

    /**
     * Starts the application, sets up home screen and displays it.
     * @param primaryStage is the main stage of the application.
     */
    @Override
    public void start(Stage primaryStage) {
        // Set the title of the window
        primaryStage.setTitle("Boulder Dash");

        getPlayerProfile();
        currentUser = playerProfileObj.get("CurrentUser").toString();
        userProfileObj = (JSONObject) playerProfileObj.get(currentUser);
        currentLevel = userProfileObj.get("CurrentLevel") != null
                ? Integer.parseInt(userProfileObj.get("CurrentLevel").
                        toString()) : 1;
        this.primaryStage = primaryStage;
        highScores = new HashMap<>();

        setupHomeScreen();

        primaryStage.setScene(homeScene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        // Set stage to fill the screen
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        //primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    /**
     * Updates the game grid.
     * This includes the level tiles, actors, and the movement updates.
     * Handles the timer, diamonds collected, and key counts.
     */
    public void drawGame() {
        calcTileSize();

        timeLabel.setText((int) (level.getTimeLimit() - timeElapsed) + "s");
        diamondsLabel.setText(player.getDiamondsCollected() + "/"
                + level.getDiamondsRequired());
        keyLabelBlue.setText("x" + (player.getKeys().get(KeyColours.BLUE)));
        keyLabelRed.setText("x" + (player.getKeys().get(KeyColours.RED)));
        keyLabelGreen.setText("x" + (player.getKeys().get(KeyColours.GREEN)));
        keyLabelYellow.setText("x" + (player.getKeys().get(KeyColours.YELLOW)));

        grid.getChildren().clear(); // Clears the grid first

        // Retrieve the level's tiles and dimensions
        List<List<Tile>> tiles = level.getTiles();
        int rows = level.getRows();
        int columns = level.getCols();

        Map<ImageView, Actor> actorsToAnimate = new HashMap<>();

        // Iterate through each tile and render it

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Tile tile = tiles.get(row).get(col);
                // Allows stacking multiple visuals
                StackPane stackPane = new StackPane();
                // Tile background
                ImageView imageView = new ImageView(tile.getImage());

                // Scale tile images to match grid size
                imageView.setFitWidth(tileSize);
                imageView.setFitHeight(tileSize);

                stackPane.getChildren().add(imageView);

                // If a tile is occupied, draw the actor occupying it
                if (tile.isOccupied()) {
                    Actor occupier = tile.getOccupier();
                    ImageView actorImageView =
                            new ImageView(occupier.getImage());
                    actorImageView.setFitHeight(tileSize
                            * TILE_SIZE_SCALE_FACTOR);
                    actorImageView.setFitWidth(tileSize
                            * TILE_SIZE_SCALE_FACTOR);
                    // if the actor is transferring animate the transfer
                    if (occupier.getIsTransferring()) {
                        // add the actor to the offset map
                        actorsToAnimate.put(actorImageView, occupier);
                        occupier.stopTransferring();

                    } else {
                        stackPane.getChildren().add(actorImageView);
                    }
                }

                // Place the visual representation in the grid
                grid.add(stackPane, col, row);
            }
        }
        transitionPane.getChildren().clear();

        for (Map.Entry<ImageView, Actor> entry : actorsToAnimate.entrySet()) {
            ImageView actorImageView = entry.getKey();
            Actor actor = entry.getValue();
            Tile previousPosition = actor.getPreviousPosition();
            Tile currentPosition = actor.getPosition();

            double widthGrid = tileSize * columns;
            double heightGrid = tileSize * rows;
            double widthStage = primaryStage.getWidth();
            double heightStage = primaryStage.getHeight();

            double x = (widthStage - widthGrid) / 2;
            double y = (heightStage - heightGrid) / 2;

            actorImageView.setTranslateX(previousPosition.getColumn()
                    * tileSize + x); // Set the initial X position
            actorImageView.setTranslateY(previousPosition.getRow()
                    * tileSize + y); // Set the initial Y position
            TranslateTransition translateTransition =
                    new TranslateTransition(Duration.seconds(tickTime),
                            actorImageView);
            translateTransition.setFromX(previousPosition.getColumn()
                    * tileSize + x);
            translateTransition.setFromY(previousPosition.getRow()
                    * tileSize + y);
            translateTransition.setToX(currentPosition.getColumn()
                    * tileSize + x);
            translateTransition.setToY(currentPosition.getRow()
                    * tileSize + y);

            translateTransition.setOnFinished(e -> actor.checkCollisions());

            translateTransition.play();
            transitionPane.getChildren().add(actorImageView);
        }
    }

    /**
     * Processes user input to control the player or state.
     *
     * @param event the KeyEvent triggered by a key press.
     */
    public void processKeyEvent(KeyEvent event) {
        switch (event.getCode()) {
            case RIGHT:
                player.setDirection(Direction.RIGHT);
                break;
            case LEFT:
                player.setDirection(Direction.LEFT);
                break;
            case UP:
                player.setDirection(Direction.UP);
                break;
            case DOWN:
                player.setDirection(Direction.DOWN);
                break;
            case ESCAPE:
                togglePause(); // Pauses or resumes the game
                break;
            default:
                player.setDirection(Direction.STATIONARY);
                break;
        }
        event.consume(); // prevents other UI elements from handling this event.
    }

    /**
     * Updates the game state, processes actors' actions,
     * and redraws the game screen.
     * at each tick.
     */
    public void tick() {
        timeElapsed += tickTime;
        removeActors(); // Remove any dead actors
        createNewActors();
        drawGame(); // Redraw the grid

        if (!dead) {
            for (Actor actor : level.getActors()) {
                actor.move(); // Move all active actors
            }
        }

        if (timeElapsed > level.getTimeLimit()) {
            looseGame("Time Limit Reached!");
        }
    }

    /**
     * Ends the game, marked it as a loss.
     * @param cause is the reason of death.
     */
    public void looseGame(String cause) {
        if (!dead) {
            Text gameOverText = new Text("Game Over");
            gameOverText.setFont(new Font(FONT_ARIAL, FONT_SIZE_GAME_OVER));
            dead = true;
            deathCause = cause;
            tickTimeline.stop();
            showGameOverScreen();
        }
    }

    /**
     * Ends the current level.
     */
    public void winGame() {
        dead = true;
        drawGame();
        showLevelCompleteScreen();
        Audio.getInstance().playSoundEffect("/Music/Victory.mp3", 1);
    }

    /**
     * Updates the player's high scores and completed levels
     * in their user profile.
     * If the current level is newly completed,
     * it gets added to the completed levels.
     * @param score
     */
    public void saveScore(int score) {
        JSONObject highScoresObj =
                (JSONObject) userProfileObj.get("HighScores");
        JSONArray completedLevels =
                (JSONArray) userProfileObj.get("CompletedLevels");

        long currentLevelLong = currentLevel;
        // check if the level has been completed before
        if (!completedLevels.contains(currentLevelLong)) {
            completedLevels.add(currentLevelLong);
        }

        // get the scores for the current level
        List<Long> scores = (List<Long>)
                highScoresObj.get("Level" + currentLevel);
        if (scores == null) {
            scores = new ArrayList<>();
        }
        scores.add((long) score);

        // sort the scores in descending order
        scores.sort(Collections.reverseOrder());
        // keep only the top 10 scores
        if (scores.size() > MAX_SCORES_AMOUNT) {
            scores = scores.subList(0, MAX_SCORES_AMOUNT);
        }

        highScoresObj.put(("Level" + currentLevel), scores);

        // update the high scores in the player profile
        try {
            JSONParser parser = new JSONParser();
            JSONObject playerProfileJSON = (JSONObject) parser.parse(
                    new FileReader("PlayerProfile.json"));
            JSONObject userObjOld = (JSONObject)
                    playerProfileJSON.get(currentUser);
            FileWriter file = new FileWriter("PlayerProfile.json");
            userObjOld.put("HighScores", highScoresObj);
            userObjOld.put("CompletedLevels", completedLevels);
            file.write(playerProfileJSON.toJSONString());
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Marks an actor for removal from the game.
     * @param actor to be removed.
     */
    public void killActor(Actor actor) {
        deadActors.add(actor);
    }

    /**
     * Adds an actor to the game.
     * @param actor to be added.
     */
    public void addActor(Actor actor) {
        newBorns.add(actor);
    }

    /**
     * Shows the remaining time.
     * @return the remaining time in seconds.
     */
    public int timeRemaining() {
        return (int) (level.getTimeLimit() - timeElapsed);
    }

    /**
     * Main method to launch the program.
     *
     * @param args command-line arguments.
     */

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }

    private void calcTileSize() {
        int cols = level.getCols();
        int rows = level.getRows();
        int windowWidth = (int) primaryStage.getWidth();
        int windowHeight = (int) primaryStage.getHeight();

        tileSize = Math.min(windowWidth / cols, windowHeight / rows);
        grid.setAlignment(Pos.CENTER);
    }

    /**
     * Sets up the home screen with the UI.
     */
    private void setupHomeScreen() {
        VBox homeScreen = new VBox(VBOX_SPACING);
        homeScreen.setStyle("-fx-padding: 20;"
                + " -fx-alignment: center; -fx-background-color: #222;");

        // Add a logo
        ImageView logo =
                new ImageView(new Image(Objects.requireNonNull(getClass()
                        .getResource("/logo.png")).toExternalForm()));
        logo.setFitHeight(LOGO_HEIGHT);
        logo.setFitWidth(LOGO_WIDTH);

        // Add a title label
        Label titleLabel = new Label("PRESS START TO PLAY");
        titleLabel.setFont(new Font(FONT_ARIAL, FONT_SIZE_TITLE));
        titleLabel.setStyle("-fx-text-fill: white;");

        currentUserLabel = new Label("Current User: " + currentUser);
        currentUserLabel.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        currentUserLabel.setStyle("-fx-text-fill: white;");

        HBox buttonBox = new HBox(VBOX_SPACING);
        buttonBox.setStyle("-fx-alignment: center;");

        // Start Game button
        Button startButton = new Button("Start");
        startButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        startButton.setOnAction(e -> startNewGame());

        // Load Game button
        Button loadButton = new Button("Load Game");
        loadButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        loadButton.setOnAction(e -> showSavedGamesScreen());

        // User Menu button
        Button userMenuButton = new Button("User Menu");
        userMenuButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        userMenuButton.setOnAction(e -> userMenu());

        // Load unlocked levels
        Button levelsButton = new Button("Levels");
        levelsButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        levelsButton.setOnAction(e -> levelsMenu());

        // High Score Table
        Label highScoreLabel =
                new Label("Level " + currentLevel + " Highest Scores:");
        highScoreLabel.setFont(new Font(FONT_ARIAL, FONT_SIZE_HIGH_SCORE));
        highScoreLabel.setStyle("-fx-text-fill: white;");

        VBox highScoreBoard = createHighScoreBoard();

        buttonBox.getChildren().addAll(startButton,
                loadButton, userMenuButton, levelsButton);

        homeScreen.getChildren().addAll(logo, titleLabel,
                currentUserLabel, buttonBox, highScoreLabel, highScoreBoard);
        homeScene = new Scene(homeScreen);
    }

    /**
     * Displays the levels screen, allowing users to select
     * and play unlocked levels.
     */
    private void levelsMenu() {
        VBox levelsScreen = new VBox(VBOX_SPACING);
        levelsScreen.setStyle("-fx-padding: 20;"
                + " -fx-alignment: center; -fx-background-color: #222;");

        Label levelsLabel = new Label("Select a Level to Play");
        levelsLabel.setFont(new Font(FONT_ARIAL, FONT_SIZE_LEVELS_LABEL));
        levelsLabel.setStyle("-fx-text-fill: white;");

        ListView<String> levelsList = new ListView<>();
        levelsList.setPrefSize(LEVELS_LIST_WIDTH, LEVELS_LIST_HEIGHT);

        JSONArray completedLevels = (JSONArray)
                userProfileObj.get("CompletedLevels");

        List<Integer> completedLevelsList = new ArrayList<>();
        for (Object level : completedLevels) {
            completedLevelsList.add(Integer.parseInt(level.toString()));
        }
        // Populate the list with unlocked levels
        for (int i = 1; i <= NUMBER_OF_LEVELS; i++) {
            String levelInfo = "Level " + i;
            if (completedLevelsList.contains(i)) {
                levelInfo += " (Completed)";
            } else {
                levelInfo += " (Locked)";
            }
            levelsList.getItems().add(levelInfo);
        }

        Button playLevelButton = new Button("Play Selected Level");
        playLevelButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        playLevelButton.setOnAction(e -> {
            String selectedLevel =
                    levelsList.getSelectionModel().getSelectedItem();
            if (selectedLevel != null) {
                int levelNumber = Integer.parseInt(selectedLevel.split(" ")[1]);
                if (completedLevelsList.contains(levelNumber)) {
                    loadLevel(levelNumber);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Level Locked");
                    alert.setContentText(
                            "Please complete this level to unlock replays");
                    alert.showAndWait();
                }
                loadLevel(levelNumber);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No Level Selected");
                alert.setContentText("Please select a level.");
                alert.showAndWait();
            }
        });

        Button backButton = new Button("Back");
        backButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        backButton.setOnAction(e -> primaryStage.setScene(homeScene));

        levelsScreen.getChildren().addAll(levelsLabel,
                levelsList, playLevelButton, backButton);

        Scene levelsScene = new Scene(levelsScreen);
        primaryStage.setScene(levelsScene);
    }

    /**
     * Loads a specified level.
     * @param levelNumber The level number to load.
     */
    private void loadLevel(int levelNumber) {
        currentLevel = levelNumber;
        level = new Level(currentLevel);
        player = level.getPlayer();
        timeElapsed = 0;
        calcTileSize();

        UISetUp();

        GameState.setupSate(level, player, this);
        tickTimeline.play();

        drawGame();
        primaryStage.setScene(scene);
        Audio.getInstance().playMusic("/Music/MinecraftChill.mp3", true, 1);
    }

    /**
     * Displays the User Menu screen, allowing users to add, remove,
     * or select users.
     */
    private void userMenu() {
        VBox userMenuScreen = new VBox(VBOX_SPACING);
        userMenuScreen.setStyle("-fx-padding: 20;"
                + " -fx-alignment: center; -fx-background-color: #222;");

        Label userMenuLabel = new Label("User Menu");
        userMenuLabel.setFont(new Font(FONT_ARIAL, FONT_SIZE_USER_MENU));
        userMenuLabel.setStyle("-fx-text-fill: white;");

        // ListView to display users
        ListView<String> userList = new ListView<>();
        JSONArray users = (JSONArray) playerProfileObj.get("Users");
        if (users != null) {
            users.forEach(user -> userList.getItems().add(user.toString()));
        }

        // Adjust ListView height based on the number of users
        int userCount = users != null ? users.size() : 0;
        userList.setPrefHeight(Math.min(userCount
                * USER_LIST_ITEM_HEIGHT, LEVELS_LIST_HEIGHT));
        userList.setMaxWidth(USER_LIST_MAX_WIDTH);

        // Add User button
        Button addUserButton = new Button("Add User");
        addUserButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        addUserButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add User");
            dialog.setHeaderText("Add a new user");
            dialog.setContentText("Enter username:");

            String newUser = dialog.showAndWait().orElse("").trim();
            if (!newUser.isEmpty() && !userList.getItems().contains(newUser)) {
                userList.getItems().add(newUser);
                users.add(newUser);
                JSONObject newUserObj = new JSONObject();
                newUserObj.put("HighScores", new JSONObject());
                newUserObj.put("SavedLevels", new JSONObject());
                newUserObj.put("CurrentLevel", 1);
                newUserObj.put("CompletedLevels", new JSONArray());
                playerProfileObj.put(newUser, newUserObj);
                savePlayerProfile();

                // Update ListView height after adding a new user
                userList.setPrefHeight(Math.min(users.size()
                        * USER_LIST_ITEM_HEIGHT, LEVELS_LIST_HEIGHT));
            }
        });

        // Remove User button
        Button removeUserButton = new Button("Remove User");
        removeUserButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        removeUserButton.setOnAction(e -> {
            String selectedUser = userList.getSelectionModel().
                    getSelectedItem();
            if (selectedUser != null) {
                if (selectedUser.equals(currentUser)) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Current user cannot be removed!");
                    errorAlert.setContentText(
                            "Please switch to a different user and try again.");
                    errorAlert.showAndWait();
                } else {
                    userList.getItems().remove(selectedUser);
                    users.remove(selectedUser);
                    playerProfileObj.remove(selectedUser);
                    savePlayerProfile();

                    // Update ListView height after removing a user
                    userList.setPrefHeight(Math.min(users.size()
                            * USER_LIST_ITEM_HEIGHT, LEVELS_LIST_HEIGHT));
                }
            }
        });

        // Select User button
        Button selectUserButton = new Button("Select User");
        selectUserButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        selectUserButton.setOnAction(e -> {
            String selectedUser =
                    userList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                changeCurrentUser(selectedUser);
                setupHomeScreen();
                primaryStage.setScene(homeScene);
            }
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        backButton.setOnAction(e -> primaryStage.setScene(homeScene));

        HBox buttonBox = new HBox(VBOX_SPACING, addUserButton,
                removeUserButton, selectUserButton, backButton);
        buttonBox.setStyle("-fx-alignment: center;");

        userMenuScreen.getChildren().addAll(userMenuLabel, userList, buttonBox);

        Scene userMenuScene = new Scene(userMenuScreen);
        primaryStage.setScene(userMenuScene);
    }

    private void changeCurrentUser(String newUser) {
        currentUser = newUser;
        userProfileObj = (JSONObject) playerProfileObj.get(currentUser);
        currentUserLabel.setText("Current User: " + currentUser);
        currentLevel = userProfileObj.get("CurrentLevel") != null
                ? Integer.parseInt(userProfileObj.get("CurrentLevel").
                        toString()) : 1;

        // Update the current user in the JSON file
        try {
            playerProfileObj.put("CurrentUser", currentUser);
            savePlayerProfile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the updated player profile to the JSON file.
     */
    private void savePlayerProfile() {
        try (FileWriter file = new FileWriter("PlayerProfile.json")) {
            file.write(playerProfileObj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the saved games to the user,
     * allowing them to choose between the save files.
     */
    private void showSavedGamesScreen() {
        VBox savedGamesScreen = new VBox(VBOX_SPACING);
        savedGamesScreen.setStyle("-fx-padding: 20; "
                + "-fx-alignment: center; -fx-background-color: #222;");

        Label savedGamesLabel = new Label("Select a Saved Game");
        savedGamesLabel.setFont(new Font(FONT_ARIAL, FONT_SIZE_USER_MENU));
        savedGamesLabel.setStyle("-fx-text-fill: white;");

        List<String> savedGames = loadSavedGames();

        if (savedGames.isEmpty()) {
            Label noSavesLabel = new Label("No saved games found!");
            noSavesLabel.setFont(new Font(FONT_ARIAL, FONT_SIZE_NO_SCORES));
            noSavesLabel.setStyle("-fx-text-fill: grey;");
            savedGamesScreen.getChildren().add(noSavesLabel);
            Button backButton = new Button("Back");
            backButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
            backButton.setOnAction(e -> primaryStage.setScene(homeScene));
            savedGamesScreen.getChildren().add(backButton);
            Scene savedGamesScene = new Scene(savedGamesScreen);
            primaryStage.setScene(savedGamesScene);
            return;
        }

        // ListView to display saved games
        ListView<String> savedGamesList = new ListView<>();
        savedGamesList.setPrefSize(LEVELS_LIST_WIDTH, LEVELS_LIST_HEIGHT);
        // loadSavedGames() returns a list of saved games
        savedGamesList.getItems().addAll(savedGames);

        // Load selected game button
        Button loadSelectedButton = new Button("Load Selected Game");
        loadSelectedButton.setFont(new Font(FONT_ARIAL,
                FONT_SIZE_CURRENT_USER));
        loadSelectedButton.setOnAction(e -> {
            String selectedGame =
                    savedGamesList.getSelectionModel().getSelectedItem();
            if (selectedGame != null) {
                System.out.println("Loading selected game: " + selectedGame);
                loadSelectedGame(selectedGame);
            }
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        backButton.setOnAction(e -> primaryStage.setScene(homeScene));

        savedGamesScreen.getChildren().addAll(savedGamesLabel,
                savedGamesList, loadSelectedButton, backButton);

        Scene savedGamesScene = new Scene(savedGamesScreen);
        primaryStage.setScene(savedGamesScene);
    }

    /**
     * Loads the saved games for the user.
     * @return the list of saved games.
     */
    private List<String> loadSavedGames() {
        // Replace with logic to save actual saved games
        JSONObject savedGames = (JSONObject) userProfileObj.get("SavedLevels");
        if (savedGames == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(savedGames.keySet());
    }

    /**
     * Loads the specific save file.
     * @param gameName is the name of the save.
     */
    private void loadSelectedGame(String gameName) {
        loadGame(gameName);
    }

    /**
     * Sets up the UI for the main game.
     */
    private void UISetUp() {
        grid.setHgap(0);
        grid.setVgap(0);
        infoBar.getChildren().clear();
        stackPane.getChildren().clear();
        stackPane = new StackPane();

        DIAMOND_COUNT_ICON.setFitHeight(tileSize * ICON_SCALE);
        DIAMOND_COUNT_ICON.setFitWidth(tileSize * ICON_SCALE);
        CLOCK_ICON.setFitHeight(tileSize * ICON_SCALE);
        CLOCK_ICON.setFitWidth(tileSize * ICON_WIDTH_SCALE_CLOCK);
        KEY_ICON_BLUE.setFitHeight(tileSize * ICON_SCALE);
        KEY_ICON_BLUE.setFitWidth(tileSize * ICON_SCALE);
        KEY_ICON_RED.setFitHeight(tileSize * ICON_SCALE);
        KEY_ICON_RED.setFitWidth(tileSize * ICON_SCALE);
        KEY_ICON_GREEN.setFitHeight(tileSize * ICON_SCALE);
        KEY_ICON_GREEN.setFitWidth(tileSize * ICON_SCALE);
        KEY_ICON_YELLOW.setFitHeight(tileSize * ICON_SCALE);
        KEY_ICON_YELLOW.setFitWidth(tileSize * ICON_SCALE);
        infoBar.getChildren().addAll(CLOCK_ICON, timeLabel, DIAMOND_COUNT_ICON,
                diamondsLabel, KEY_ICON_BLUE, keyLabelBlue,
                KEY_ICON_RED, keyLabelRed, KEY_ICON_GREEN, keyLabelGreen,
                KEY_ICON_YELLOW, keyLabelYellow);
        infoBar.setPrefHeight(tileSize * INFO_BAR_HEIGHT_RATIO);
        infoBar.setAlignment(javafx.geometry.Pos.CENTER);
        infoBar.setStyle("-fx-padding: 5; "
                + "-fx-background-color: #333; -fx-text-fill: white;");
        timeLabel.setStyle("-fx-text-fill: white;");
        diamondsLabel.setStyle("-fx-text-fill: white;");
        keyLabelBlue.setStyle("-fx-text-fill: white;");
        keyLabelRed.setStyle("-fx-text-fill: white;");
        keyLabelGreen.setStyle("-fx-text-fill: white;");
        keyLabelYellow.setStyle("-fx-text-fill: white;");
        DIAMOND_COUNT_ICON.setStyle("-fx-padding: 10;");

        stackPane.setStyle("-fx-background-color: #333;");

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(infoBar);

        stackPane.getChildren().addAll(grid, transitionPane, borderPane);

        scene = new Scene(stackPane);  // width: 400, height: 400
        scene.setOnKeyPressed(this::processKeyEvent);
        scene.setOnKeyReleased(event
                -> player.setDirection(Direction.STATIONARY));

        tickTimeline = new Timeline(new KeyFrame(Duration.seconds(tickTime),
                event -> tick()));
        tickTimeline.setCycleCount(Animation.INDEFINITE);
    }

    private void getHighScores() {
        highScores = new HashMap<>();
        JSONArray users = (JSONArray) playerProfileObj.get("Users");
        // get the high scores for the current level from all users
        for (Object user : users) {
            JSONObject userObj = (JSONObject) playerProfileObj.get(user);
            JSONObject highScoresObj = (JSONObject) userObj.get("HighScores");
            List<Long> scores =
                    (List<Long>) highScoresObj.get("Level" + currentLevel);
            System.out.println(scores);
            if (scores != null) {
                for (long scoreLong : scores) {
                    Integer score = (int) scoreLong;
                    if (highScores.containsKey(score)) {
                        highScores.put(score, highScores.get(score)
                                + ", " + user.toString());
                    } else {
                        highScores.put(score, user.toString());
                    }
                }
            }
        }

        // sort the high scores in descending order
        Map<Integer, String> sortedHighScores = new LinkedHashMap<>();
        highScores.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .forEachOrdered(x
                        -> sortedHighScores.put(x.getKey(), x.getValue()));
        highScores = sortedHighScores;
    }
    /**
     * Creates the high score board.
     * @return a VBox of the high score board.
     */
    private VBox createHighScoreBoard() {
        getHighScores();

        // Vertical spacing between scores
        VBox highScoreBoard = new VBox(HBOX_SPACING);
        highScoreBoard.setStyle("-fx-padding: 10; -fx-alignment: center;");
        highScoreBoard.setAlignment(Pos.CENTER);

        if (highScores.isEmpty()) {
            Label noScoresLabel = new Label("No score yet!");
            noScoresLabel.setFont(new Font(FONT_ARIAL, FONT_SIZE_NO_SCORES));
            noScoresLabel.setStyle("-fx-text-fill: grey;");
            highScoreBoard.getChildren().add(noScoresLabel);
        } else {
            List<Integer> highScoresInt =
                    new ArrayList<>(this.highScores.keySet());
            List<String> byUser = new ArrayList<>(this.highScores.values());
            for (int i = 0; i < Math.min(highScoresInt.size(), MAX_SCORES_AMOUNT); i++) {
                Label scoreLabel = new Label(highScoresInt.get(i).toString());
                Label userLabel =
                        new Label((i + 1) + ". " + byUser.get(i) + "  - ");
                scoreLabel.setFont(new Font(FONT_ARIAL, FONT_SIZE_SCORE_LABEL));
                scoreLabel.setStyle("-fx-text-fill: white;");
                userLabel.setFont(new Font(FONT_ARIAL, FONT_SIZE_SCORE_LABEL));
                userLabel.setStyle("-fx-text-fill: white;");

                HBox scoreBox = new HBox(HBOX_SPACING);
                scoreBox.setAlignment(Pos.CENTER);
                scoreBox.getChildren().addAll(userLabel, scoreLabel);

                highScoreBoard.getChildren().addAll(scoreBox);
            }
        }

        return highScoreBoard;
    }

    /**
     * Starts a new game.
     * Loads the level and game state.
     */
    private void startNewGame() {

        if (tickTimeline != null) { // resets timeline
            tickTimeline.stop();
            tickTimeline = null;
        }

        deathCause = "";
        currentLevel = userProfileObj.get("CurrentLevel") != null
                ? Integer.parseInt(userProfileObj.get("CurrentLevel").
                toString()) : 1;

        level = new Level(currentLevel);
        player = level.getPlayer();
        timeElapsed = 0;
        calcTileSize();

        UISetUp();

        GameState.setupSate(level, player, this);
        tickTimeline.play();

        drawGame();

        primaryStage.setScene(scene);
        // center the scene on the screen

        Audio.getInstance().playMusic("/Music/MinecraftChill.mp3", true, 1.0);
    }

    /**
     * Loads the player's profile from a JSON file.
     */
    private void getPlayerProfile() {
        JSONParser parser = new JSONParser();
        try {
            playerProfileObj = (JSONObject) parser.parse(
                    new FileReader("PlayerProfile.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes all actors that have been marked for removal
     * in the current game tick.
     */
    private void removeActors() {
        for (Actor actor : deadActors) {
            level.removeActor(actor);
        }
        deadActors = new ArrayList<>();
    }

    private void createNewActors() {
        level.addActors(newBorns);
        newBorns = new ArrayList<>();
    }

    /**
     * Toggles the state of the game, either paused or un-paused.
     */
    private void togglePause() {
        // ensure you can't pause while gameOver screen is up
        if (dead) {
            return;
        }
        isPaused = !isPaused;
        if (isPaused) {
            tickTimeline.pause(); // Stops the game loop
            showPauseMenu();
        } else {
            tickTimeline.play(); // Resumes the game loop
            hidePauseMenu();
        }
    }

    /**
     * Displays the pause menu on the screen.
     */
    private void showPauseMenu() {
        if (pauseMenu == null) {
            createPauseMenu(); // Initialize the pause menu if it doesn't exist
        }
        if (!stackPane.getChildren().contains(pauseMenu)) {
            pauseMenu.setLayoutX((scene.getWidth()
                    - PAUSE_MENU_LAYOUT_OFFSET) / 2);
            pauseMenu.setLayoutY((scene.getHeight()
                    - PAUSE_MENU_LAYOUT_OFFSET) / 2);
            // Add the pause menu to the grid
            stackPane.getChildren().add(pauseMenu);
        }
    }

    /**
     * Hides the pause menu.
     */
    private void hidePauseMenu() {
        if (pauseMenu != null) {
            // Remove the pause menu from the grid
            stackPane.getChildren().remove(pauseMenu);
        }
    }

    /**
     * Creates the pause menu.
     */
    private void createPauseMenu() {
        pauseMenu = new VBox(PAUSE_MENU_SPACING);
        pauseMenu.setStyle("-fx-padding: 20;");
        pauseMenu.setMaxSize(PAUSE_MENU_MAX_WIDTH, PAUSE_MENU_MAX_HEIGHT);
        pauseMenu.setAlignment(javafx.geometry.Pos.CENTER);

        double buttonWidth = BUTTON_WIDTH;
        String buttonStyle =  "-fx-border-color: white darkgrey darkgrey white;"
                + "-fx-border-width: 4; -fx-text-fill: black; "
                + "-fx-font-family: monospace; -fx-font-size: 12; "
                + "-fx-cursor: hand;";

        Button resumeButton = new Button("Resume");
        Button saveButton = new Button("Save Game");
        Button loadButton = new Button("Load Game");
        Button exitButton = new Button("Exit Game");
        Button restartButton = new Button("Restart Game");
        Button mainMenuButton = new Button("Main Menu");

        resumeButton.setStyle(buttonStyle);
        saveButton.setStyle(buttonStyle);
        loadButton.setStyle(buttonStyle);
        exitButton.setStyle(buttonStyle);
        restartButton.setStyle(buttonStyle);
        mainMenuButton.setStyle(buttonStyle);

        resumeButton.setPrefWidth(buttonWidth);
        saveButton.setPrefWidth(buttonWidth);
        loadButton.setPrefWidth(buttonWidth);
        exitButton.setPrefWidth(buttonWidth);
        restartButton.setPrefWidth(buttonWidth);
        mainMenuButton.setPrefWidth(buttonWidth);

        resumeButton.setOnAction(e -> togglePause());
        saveButton.setOnAction(e -> saveGame());
        exitButton.setOnAction(e -> exitGame());
        restartButton.setOnAction(e -> {
            restartGame();
            togglePause();
        });
        mainMenuButton.setOnAction(e -> {
                isPaused = false;
                if (tickTimeline != null) {
                    tickTimeline.stop();
                    tickTimeline = null;
                }
                hidePauseMenu();
                primaryStage.setScene(homeScene);
    });

        pauseMenu.setStyle("-fx-background-color: rgba(51, 51, 51, 0.9);"
                + "-fx-padding: 20;");

        pauseMenu.getChildren().addAll(resumeButton, saveButton,
                loadButton, restartButton, mainMenuButton, exitButton);

    }

    /**
     * Saves the current game state.
     */
    private void saveGame() {
        // textbox for save name input
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Save Game");
        dialog.setHeaderText("Save Your Progress");
        dialog.setContentText("Enter a name for your save:");

        // set the background color of dialog & the header to match the game
        dialog.getDialogPane().setStyle("-fx-background-color: #333;");
        dialog.getDialogPane().lookup(".content .label").
                setStyle("-fx-text-fill: white;");
        dialog.getDialogPane().lookup(".header-panel").
                setStyle("-fx-background-color: #333;");
        dialog.getDialogPane().lookup(".header-panel .label").
                setStyle("-fx-text-fill: white;");
        dialog.getDialogPane().setGraphic(null);

        // does not save if nothing entered / cancel is pressed
        String saveName = dialog.showAndWait().orElse("");

        if (saveName.trim().isEmpty()) {
            System.out.println("Save name cannot be empty.");
        } else {
            level.saveLevel(currentUser, saveName.trim());
        }
    }

    /**
     * Loads a previously saved game state.
     * @param gameName the name of the game to load.
     */
    private void loadGame(String gameName) {
        if (!Objects.equals(gameName, "")) {
            level = new Level(currentUser, gameName);
            player = level.getPlayer();
            timeElapsed = 0;
            calcTileSize();

            UISetUp();

            GameState.setupSate(level, player, this);
            tickTimeline.play();

            drawGame();
            primaryStage.setScene(scene);
            primaryStage.setHeight(level.getRows() * tileSize);
            primaryStage.setWidth(level.getCols() * tileSize);
        }
    }

    /**
     * Exits the game, saving the state before closing.
     */
    private void exitGame() {
        saveGame();
        Stage stage = (Stage) grid.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows the game over screen to the user and provides options to
     * restart or exit application.
     */
    private void showGameOverScreen() {
        drawGame();
        if (gameOverMenu == null) {
            gameOverMenu = new StackPane();
            gameOverMenu.setStyle(
                    "-fx-background-color: rgba(51, 51, 51, 0.9);");
            gameOverMenu.setPrefSize(scene.getWidth(), scene.getHeight());

            VBox gameOverBox = new VBox(VBOX_SPACING);
            double buttonWidth = BUTTON_WIDTH;
            gameOverBox.setAlignment(Pos.CENTER);

            Label messageLabel = new Label("Game Over!");
            messageLabel.setStyle("-fx-text-fill: red;"
                    + " -fx-font-size: 48; "
                    + "-fx-font-family: monospace;");

            Label causeLabel = new Label(deathCause);
            causeLabel.setStyle("-fx-text-fill: darkred;"
                    + " -fx-font-size: 24; "
                    + "-fx-font-family: monospace;");

            VBox scoreBoard = createHighScoreBoard();
            scoreBoard.setStyle("-fx-padding: 20;");

            Button exitButton = new Button("Exit Game");
            exitButton.setStyle("-fx-background-color: grey; "
                    + "-fx-border-color: white darkgrey darkgrey white; "
                    + "-fx-border-width: 4; -fx-text-fill: white; "
                    + "-fx-font-family: monospace; -fx-font-size: 16;"
                    + "-fx-cursor: hand;");

            Button restartButton = new Button("Restart Game");
            restartButton.setStyle("-fx-background-color: grey; "
                    + "-fx-border-color: white darkgrey darkgrey white; "
                    + "-fx-border-width: 4; -fx-text-fill: white; "
                    + "-fx-font-family: monospace; -fx-font-size: 16;"
                    + "-fx-cursor: hand;");

            Button gameOverMainMenuButton = new Button("Main Menu");
            gameOverMainMenuButton.setStyle("-fx-background-color: grey; "
                    + "-fx-border-color: white darkgrey darkgrey white; "
                    + "-fx-border-width: 4; -fx-text-fill: white; "
                    + "-fx-font-family: monospace; -fx-font-size: 16;"
                    + "-fx-cursor: hand;");

            gameOverMainMenuButton.setPrefWidth(buttonWidth);
            restartButton.setPrefWidth(buttonWidth);
            exitButton.setPrefWidth(buttonWidth);

            exitButton.setOnAction(e -> exitGame());
            restartButton.setOnAction(e -> restartGame());
            gameOverMainMenuButton.setOnAction(e -> {
                restartGame();
                primaryStage.setScene(homeScene);
            });

            gameOverBox.getChildren().addAll(messageLabel,
                    causeLabel, scoreBoard, restartButton, gameOverMainMenuButton, exitButton);
            gameOverBox.setLayoutX(scene.getWidth() / 2
                    - gameOverBox.getPrefWidth() / 2);
            gameOverBox.setLayoutY(scene.getHeight() / 2
                    - gameOverBox.getPrefHeight() / 2);

            gameOverMenu.getChildren().add(gameOverBox);
        }

        gameOverMenu.setPrefSize(scene.getWidth(), scene.getHeight());
        stackPane.getChildren().add(gameOverMenu);
    }

    /**
     * Restarts the game.
     * Resets the game state and reinitialise the level.
     */
    private void restartGame() {
        stackPane.getChildren().remove(gameOverMenu);
        tickTimeline.stop();
        dead = false;
        deathCause = "";
        timeElapsed = 0;
        level = new Level(currentLevel);
        player = level.getPlayer();  // maintain current level / player prof
        deadActors = new ArrayList<>();
        newBorns = new ArrayList<>();

        GameState.setupSate(level, player, this);
        gameOverMenu = null;
        drawGame();
        tickTimeline.play();
    }

    /**
     * Shows the level complete screen when the player completes a level.
     * Stops the game loop to display the screen, shows the high score board,
     * and provides option for moving.
     * onto the next level or exit application.
     */
    private void showLevelCompleteScreen() {
        tickTimeline.pause();
        int score = player.getDiamondsCollected()
                * SCORE_MULTIPLIER_DIAMONDS + timeRemaining() * 2;
        if (levelCompleteMenu == null) {
            levelCompleteMenu = new StackPane();
            levelCompleteMenu.setStyle(
                    "-fx-background-color: rgba(51, 51, 51, 0.9);");
            levelCompleteMenu.setPrefSize(scene.getWidth(), scene.getHeight());

            VBox levelCompleteBox = new VBox(VBOX_SPACING);
            levelCompleteBox.setAlignment(Pos.CENTER);

            Label messageLabel = new Label("Level Complete!");
            messageLabel.setStyle("-fx-text-fill: lightgreen;"
                    + " -fx-font-size: 48;"
                    + " -fx-font-family: monospace;");

            Label scoreLabel = new Label("You Scored: " + score);
            scoreLabel.setStyle("-fx-text-fill: lightgreen; -fx-font-size: 48;"
                    + " -fx-font-family: monospace;");

            saveScore(score);

            VBox scoreBoard = createHighScoreBoard();
            scoreBoard.setStyle("-fx-padding: 20;");

            Button nextLevelButton = new Button("Next Level");
            nextLevelButton.setStyle("-fx-background-color: grey; "
                    + "-fx-border-color: white darkgrey darkgrey white; "
                    + "-fx-border-width: 4; -fx-text-fill: white;"
                    + " -fx-font-family: monospace; -fx-font-size: 16;"
                    + "-fx-cursor: hand;");

            Button exitButton = new Button("Exit Game");
            exitButton.setStyle("-fx-background-color: grey; "
                    + "-fx-border-color: white darkgrey darkgrey white; "
                    + "-fx-border-width: 4; -fx-text-fill: white;"
                    + "-fx-font-family: monospace; -fx-font-size: 16;"
                    + "-fx-cursor: hand;");

            Button mainMenuButton = new Button("Main Menu");
            mainMenuButton.setStyle("-fx-background-color: grey; "
                    + "-fx-border-color: white darkgrey darkgrey white; "
                    + "-fx-border-width: 4; -fx-text-fill: white;"
                    + "-fx-font-family: monospace; -fx-font-size: 16;"
                    + "-fx-cursor: hand;");

            nextLevelButton.setOnAction(e -> loadNextLevel());
            exitButton.setOnAction(e -> exitGame());
            mainMenuButton.setOnAction(e -> {
                restartGame();
                primaryStage.setScene(homeScene);
            });

            levelCompleteBox.getChildren().addAll(messageLabel,
                    scoreLabel, scoreBoard, nextLevelButton, mainMenuButton, exitButton);

            levelCompleteMenu.getChildren().add(levelCompleteBox);
        }

        levelCompleteMenu.setPrefSize(scene.getWidth(), scene.getHeight());
        stackPane.getChildren().add(levelCompleteMenu);
    }

    /**
     * Loads the next level once current level has been completed.
     */
    private void loadNextLevel() {
        currentLevel++;
        updateCurrentLevel();
        level = new Level(currentLevel);
        player = level.getPlayer();
        dead = false;
        timeElapsed = 0;
        GameState.setupSate(level, player, this);
        tickTimeline.play();
        stackPane.getChildren().remove(levelCompleteMenu);
        drawGame();

    }
    private void updateCurrentLevel() {
        userProfileObj.put("CurrentLevel", currentLevel);
        try {
            JSONParser parser = new JSONParser();
            JSONObject playerProfileJSON = (JSONObject)
                    parser.parse(new FileReader("PlayerProfile.json"));
            FileWriter file = new FileWriter("PlayerProfile.json");
            playerProfileJSON.put(currentUser, userProfileObj);
            file.write(playerProfileJSON.toJSONString());
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
