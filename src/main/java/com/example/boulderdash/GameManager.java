package com.example.boulderdash;

import com.example.boulderdash.Actors.Actor;
import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Cursor;
import com.example.boulderdash.enums.KeyColours;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Pos;
import javafx.stage.Screen;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import static javafx.scene.control.PopupControl.USE_PREF_SIZE;

/**
 * Main controller for the game. This class handles game initialisation,
 * user input, game state management, and the main game loop.
 */
public class GameManager extends Application {

    // Constants
    /**
     * Icon used to represent the player's diamond count.
     * */
    private static final ImageView DIAMOND_COUNT_ICON =
            new ImageView(new Image("Actor Images/diamond.png"));
    /**
     * Icon used to represent the player's time countdown.
     * */
    private static final ImageView CLOCK_ICON =
            new ImageView(new Image("clock.png"));
    /**
     * Icon used to represent the player's blue-key count.
     * */
    private static final ImageView KEY_ICON_BLUE =
            new ImageView(new Image("Key Icon Images/blue_key_icon.png"));
    /**
     * Icon used to represent the player's red-key count.
     * */
    private static final ImageView KEY_ICON_RED =
            new ImageView(new Image("Key Icon Images/red_key_icon.png"));
    /**
     * Icon used to represent the player's green-key count.
     * */
    private static final ImageView KEY_ICON_GREEN =
            new ImageView(new Image("Key Icon Images/green_key_icon.png"));
    /**
     * Icon used to represent the player's yellow-key count.
     * */
    private static final ImageView KEY_ICON_YELLOW =
            new ImageView(new Image("Key Icon Images/yellow_key_icon.png"));
    /**
     * The Number of levels available in the game.
     * */
    private static final int NUMBER_OF_LEVELS = 5;
    /**
     * How much of the tile any object, placed inside it, should
     * occupy.
     * */
    private static final double TILE_SIZE_SCALE_FACTOR = 0.8;
    /**
     * The number of all the user scores saved that is displayed on the
     * home-screen.
     */
    private static final int MAX_SCORES_AMOUNT = 10;
    /**
     * Variable used to reference the Arial font used.
     * */
    private static final String FONT_ARIAL = "Arial";
    /**
     * The size of the Font used to Display the 'Game Over' message.
     * */
    private static final int FONT_SIZE_GAME_OVER = 75;
    /**
     * The size of the Font used to Display the game's Title.
     * */
    private static final int FONT_SIZE_TITLE = 40;
    /**
     * The size of the Font used to Display the name of the game's
     * current User.
     * */
    private static final int FONT_SIZE_CURRENT_USER = 20;
    /**
     * The size of the Font used to Display the 'High Score' message.
     * */
    private static final int FONT_SIZE_HIGH_SCORE = 25;
    /**
     * The size of the Font used to Display each Level's label.
     * */
    private static final int FONT_SIZE_LEVELS_LABEL = 20;
    /**
     * The size of the Font used for the User Menu.
     * */
    private static final int FONT_SIZE_USER_MENU = 30;
    /**
     * The size of the Font used to display when there are No
     * Scores available.
     * */
    private static final int FONT_SIZE_NO_SCORES = 18;
    /**
     * The size of the Font used to display a User's Name and Score.
     * */
    private static final int FONT_SIZE_SCORE_LABEL = 18;
    /**
     * The spacing for Vertical Boxes on the stage.
     * */
    private static final int VBOX_SPACING = 20;
    /**
     * The spacing for Horizontal Boxes on the stage.
     * */
    private static final int HBOX_SPACING = 10;
    /**
     * The height of the game's logo.
     * */
    private static final int LOGO_HEIGHT = 200;
    /**
     * The width of the game's logo.
     * */
    private static final int LOGO_WIDTH = 400;
    private static final int LEVELS_LIST_WIDTH = 400;
    private static final int LEVELS_LIST_HEIGHT = 300;
    /**
     * How big, in comparison to the tile's size, the Icons displayed are.
     * (to show the counts)
     * */
    private static final double ICON_SCALE = 0.5;
    /**
     * How big, in comparison to the tile's size, the clock icon's width is.
     * */
    private static final double ICON_WIDTH_SCALE_CLOCK = 0.4;
    /**
     * The height of the info bar (containing icons and key,diamond,time counts)
     * on the screen when the game is being played.
     * */
    private static final double INFO_BAR_HEIGHT_RATIO = 0.7;
    private static final int PAUSE_MENU_LAYOUT_OFFSET = 200;
    /**
     * The largest width that the Pause menu can get to.
     * */
    private static final int PAUSE_MENU_MAX_WIDTH = 250;
    /**
     * The largest height that the Pause menu can get to.
     * */
    private static final int PAUSE_MENU_MAX_HEIGHT = 200;
    /**
     * The width of the buttons in the game.
     * */
    private static final int BUTTON_WIDTH = 150;
    /**
     * The number of points added to User's score per diamond
     * collected.
     * */
    private static final int SCORE_MULTIPLIER_DIAMONDS = 10;
    private static final int USER_LIST_ITEM_HEIGHT = 35;
    /**
     * The spacing for the game's Pause menu.
     * */
    private static final int PAUSE_MENU_SPACING = 5;
    private static final int KEY_AMOUNT = 4;
    private static final int KEY_IMAGE_SIZE = 50;
    // Attributes
    /**
     * The reason the player died in the game. Displayed to the user
     * when they lose the round.
     * */
    private String deathCause = "";
    /**
     * The list of all the actors that have died in the User's game.
     * */
    private List<Actor> deadActors = new ArrayList<>();
    /**
     * The list of actors to be added to the User's game.*/
    private List<Actor> newBorns = new ArrayList<>();
    private Timeline tickTimeline;
    /**
     * Represents a level in the Game. This includes the Conditions that
     * must be met to win, the tile layout and the positions of all the
     * actors (both Enemy and Player) in the game.
     * */
    private Level level;
    /**
     * The game's Player object present in each level.
     * */
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
    private StackPane stackPane = new StackPane();
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
    /**
     * The size of each tile on the game stage.
     * */
    private int tileSize;
    /**
     * The Level currently being played by a User.*/
    private int currentLevel = 1;
    private boolean dead = false;
    private boolean isPaused = false;

    /**
     * Starts the application, sets up home screen and displays it.
     * @param newPrimaryStage is the main stage of the application.
     */
    @Override
    public void start(final Stage newPrimaryStage) {
        this.primaryStage = newPrimaryStage;
        primaryStage.setTitle("Boulder Dash");

        getPlayerProfile();
        currentUser = playerProfileObj.get("CurrentUser").toString();
        userProfileObj = (JSONObject) playerProfileObj.get(currentUser);
        currentLevel = userProfileObj.get("CurrentLevel") != null
                ? Integer.parseInt(userProfileObj.get("CurrentLevel").
                        toString()) : 1;
        this.primaryStage = newPrimaryStage;
        highScores = new HashMap<>();

        setupHomeScreen();

        primaryStage.setScene(homeScene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        // Set stage to fill the screen
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

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
    public void processKeyEvent(final KeyEvent event) {
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
    public void looseGame(final String cause) {
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
     * @param score achieved by the player in the current level.
     */
    public void saveScore(final int score) {
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
    public void killActor(final Actor actor) {
        deadActors.add(actor);
    }

    /**
     * Adds an actor to the game.
     * @param actor to be added.
     */
    public void addActor(final Actor actor) {
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

    public static void main(final String[] args) {
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

        Object completedTheGame = userProfileObj.get("CompletedTheGame");

        if (completedTheGame instanceof Boolean && (Boolean) completedTheGame) {
            Label completedGameLabel = new Label(
                    "You have completed the game!");
            completedGameLabel.setFont(
                    new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
            completedGameLabel.setStyle("-fx-text-fill: white;");
            homeScreen.getChildren().add(completedGameLabel);
        } else {
            // Optionally, handle the case where the game isn't completed
            System.out.println(
                    "The game is not completed or the value is missing.");
        }

        HBox buttonBox = new HBox(VBOX_SPACING);
        buttonBox.setStyle("-fx-alignment: center;");
        String buttonStyleHome = "-fx-background-color: #3a3a3a;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 5;"
                + "-fx-padding: 8 15 8 15;";


        // Start Game button
        Button startButton = new Button("Start");
        startButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        startButton.setOnAction(e -> startNewGame());
        startButton.setStyle(buttonStyleHome);

        // Load Game button
        Button loadButton = new Button("Load Game");
        loadButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        loadButton.setOnAction(e -> showSavedGamesScreen());
        loadButton.setStyle(buttonStyleHome);

        // User Menu button
        Button userMenuButton = new Button("User Menu");
        userMenuButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        userMenuButton.setOnAction(e -> userMenu());
        userMenuButton.setStyle(buttonStyleHome);

        // Load unlocked levels
        Button levelsButton = new Button("Levels");
        levelsButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        levelsButton.setOnAction(e -> levelsMenu());
        levelsButton.setStyle(buttonStyleHome);

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
        levelsScreen.setStyle("-fx-padding: 20;" + "-fx-alignment: center;"
                + "-fx-background-color: #222;" + "-fx-background-radius: 10;"
                + "-fx-border-color: #333;" + "-fx-border-width: 1;"
                + "-fx-border-radius: 10;");

        Label levelsLabel = new Label("Select a Level to Play");
        levelsLabel.setFont(new Font(FONT_ARIAL, FONT_SIZE_LEVELS_LABEL));
        levelsLabel.setStyle("-fx-text-fill: white;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #222; "
                + "-fx-background-color: transparent; "
                + "-fx-border-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        HBox levelsContainer = new HBox(VBOX_SPACING);
        levelsContainer.setStyle("-fx-background-color"
                + ": transparent; -fx-padding: 20;");
        levelsContainer.setAlignment(Pos.CENTER);

        StringProperty selectedLevel = new SimpleStringProperty(null);

        JSONArray completedLevels = (JSONArray)
                userProfileObj.get("CompletedLevels");
        List<Integer> completedLevelsList = new ArrayList<>();
        for (Object doneLevel : completedLevels) {
            completedLevelsList.add(Integer.parseInt(doneLevel.toString()));
        }
        String[] levelImages = {
                "/Tile Images/dirt.png",
                "/Tile Images/normal_wall.png",
                "/Tile Images/titanium_wall.png",
                "/Tile Images/magic_wall.png",
                "/Tile Images/exit.png"
        };

        for (int i = 1; i <= NUMBER_OF_LEVELS; i++) {
            final int levelNum = i;
            VBox levelCard = new VBox(HBOX_SPACING);
            levelCard.setStyle("-fx-background-color: #3a3a3a;"
                    + "-fx-background-radius: 10;" + "-fx-padding: 15;"
                    + "-fx-min-width: 150;" + "-fx-max-width: 150;"
                    + "-fx-min-height: 150;" + "-fx-alignment: center;");

            ImageView levelImage = new ImageView(new Image(
                    getClass().getResourceAsStream(levelImages[i - 1])));
            levelImage.setFitWidth(60);
            levelImage.setFitHeight(60);
            levelImage.setPreserveRatio(true);

            Label numberLabel = new Label("Level " + i);
            numberLabel.setStyle("-fx-text-fill: white;"
                    + " -fx-font-size: 24; -fx-font-weight: bold;");

            Label statusLabel = new Label(completedLevelsList.contains(i)
                    ? "(Completed)" : "(Locked)");
            statusLabel.setStyle("-fx-text-fill: "
                    + (completedLevelsList.contains(i)
                    ? "#90EE90" : "#FF6B6B") + "; -fx-font-size: 14;");

            levelCard.getChildren().addAll(
                    levelImage, numberLabel, statusLabel);

            if (completedLevelsList.contains(i)) {
                levelCard.setOnMouseEntered(e -> {
                    levelCard.setStyle("-fx-background-color: #4a4a4a;"
                            + "-fx-background-radius: 10;" + "-fx-padding: 15;"
                            + "-fx-min-width: 150;" + "-fx-max-width: 150;"
                            + "-fx-min-height: 150;" + "-fx-alignment: center;"
                            + "-fx-scale-x: 1.1;" + "-fx-scale-y: 1.1;");
                    levelCard.setCursor(Cursor.HAND);
                });

                levelCard.setOnMouseExited(e -> {
                    if (selectedLevel.get() == null
                            || !selectedLevel.get().equals("Level "
                            + levelNum)) {
                        levelCard.setStyle("-fx-background-color: #3a3a3a;"
                                + "-fx-background-radius: 10;"
                                + "-fx-padding: 15;"
                                + "-fx-min-width: 150;" + "-fx-max-width: 150;"
                                + "-fx-min-height: 150;"
                                + "-fx-alignment: center;"
                                + "-fx-scale-x: 1;" + "-fx-scale-y: 1;");
                    }
                });

                levelCard.setOnMouseClicked(e -> {
                    levelsContainer.getChildren().forEach(node -> {
                        if (node instanceof VBox) {
                            node.setStyle("-fx-background-color: #3a3a3a;"
                                    + "-fx-background-radius: 10;"
                                    + "-fx-padding: 15;" + "-fx-min-width: 150;"
                                    + "-fx-max-width: 150;"
                                    + "-fx-min-height: 150;"
                                    + "-fx-alignment: center;");
                        }
                    });
                    levelCard.setStyle("-fx-background-color: #4a4a4a;"
                            + "-fx-background-radius: 10;" + "-fx-padding: 15;"
                            + "-fx-min-width: 150;" + "-fx-max-width: 150;"
                            + "-fx-min-height: 150;"
                            + "-fx-alignment: center;");
                    selectedLevel.set("Level " + levelNum);
                });
            } else {
                levelCard.setStyle(levelCard.getStyle() + "-fx-opacity: 0.6;");
            }

            levelsContainer.getChildren().add(levelCard);
        }

        scrollPane.setContent(levelsContainer);
        scrollPane.setPrefHeight(200);
        scrollPane.setMaxWidth(USE_PREF_SIZE);

        String buttonStyle = "-fx-background-color: #3a3a3a;"
                + "-fx-text-fill: white;" + "-fx-background-radius: 5;"
                + "-fx-padding: 8 15 8 15;";

        Button playLevelButton = new Button("Play Selected Level");
        playLevelButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        playLevelButton.setStyle(buttonStyle);
        playLevelButton.setOnAction(e -> {
            if (selectedLevel.get() != null) {
                int levelNumber = Integer.parseInt(selectedLevel.get().
                        split(" ")[1]);
                if (completedLevelsList.contains(levelNumber)) {
                    loadLevel(levelNumber);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Level Locked");
                    alert.setContentText(
                            "Please complete previous levels "
                                    + "to unlock this one.");
                    alert.showAndWait();
                }
            }
        });

        Button backButton = new Button("Back");
        backButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        backButton.setStyle(buttonStyle);
        backButton.setOnAction(e -> primaryStage.setScene(homeScene));

        HBox buttonBox = new HBox(VBOX_SPACING, playLevelButton, backButton);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 15 0 0 0");

        levelsScreen.getChildren().addAll(levelsLabel, scrollPane, buttonBox);

        Scene levelsScene = new Scene(levelsScreen);
        primaryStage.setScene(levelsScene);
    }
    /**
     * Loads a specified level.
     * @param levelNumber The level number to load.
     */
    private void loadLevel(final int levelNumber) {
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
                + "-fx-alignment: center;"
                + "-fx-background-color: #222;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #333;"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 10;"
        );

        Label userMenuLabel = new Label("User Menu");
        userMenuLabel.setFont(new Font(FONT_ARIAL, FONT_SIZE_USER_MENU));
        userMenuLabel.setStyle("-fx-text-fill: white;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #222; "
                + "-fx-background-color: transparent; "
                + "-fx-border-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        HBox profilesContainer = new HBox(VBOX_SPACING);
        profilesContainer.setStyle("-fx-background-color"
                + ": transparent; -fx-padding: 20;");
        profilesContainer.setAlignment(Pos.CENTER);

        StringProperty selectedUser = new SimpleStringProperty(null);

        JSONArray users = (JSONArray) playerProfileObj.get("Users");
        if (users != null) {
            int[] userCount = {1}; // Array to allow modification in lambda
            users.forEach(user -> {
                VBox profileCard = new VBox(HBOX_SPACING);
                profileCard.setStyle("-fx-background-color: #3a3a3a;"
                        + "-fx-background-radius: 10;"
                        + "-fx-padding: 15;"
                        + "-fx-min-width: 150;"
                        + "-fx-max-width: 150;"
                        + "-fx-min-height: 150;"
                        + "-fx-alignment: center;");

                Label numberLabel = new Label(String.valueOf(userCount[0]));
                numberLabel.setStyle("-fx-text-fill: white;"
                        + " -fx-font-size: 32; -fx-font-weight: bold;");

                Label nameLabel = new Label(user.toString());
                nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

                profileCard.getChildren().addAll(numberLabel, nameLabel);

                profileCard.setOnMouseEntered(e -> {
                    profileCard.setStyle("-fx-background-color: #4a4a4a;"
                            + "-fx-background-radius: 10;"
                            + "-fx-padding: 15;"
                            + "-fx-min-width: 150;"
                            + "-fx-max-width: 150;"
                            + "-fx-min-height: 150;"
                            + "-fx-alignment: center;"
                            + "-fx-scale-x: 1.1;"
                            + "-fx-scale-y: 1.1;");
                    profileCard.setCursor(Cursor.HAND);
                });

                profileCard.setOnMouseExited(e -> {
                    if (selectedUser.get() == null
                            || !selectedUser.get().equals(user.toString())) {
                        profileCard.setStyle("-fx-background-color: #3a3a3a;"
                                + "-fx-background-radius: 10;"
                                + "-fx-padding: 15;"
                                + "-fx-min-width: 150;"
                                + "-fx-max-width: 150;"
                                + "-fx-min-height: 150;"
                                + "-fx-alignment: center;"
                                + "-fx-scale-x: 1;"
                                + "-fx-scale-y: 1;");
                    }
                });

                profileCard.setOnMouseClicked(e -> {
                    profilesContainer.getChildren().forEach(node -> {
                        if (node instanceof VBox) {
                            node.setStyle("-fx-background-color: #3a3a3a;"
                                    + "-fx-background-radius: 10;"
                                    + "-fx-padding: 15;"
                                    + "-fx-min-width: 150;"
                                    + "-fx-max-width: 150;"
                                    + "-fx-min-height: 150;"
                                    + "-fx-alignment: center;");
                        }
                    });
                    profileCard.setStyle("-fx-background-color: #4a4a4a;"
                            + "-fx-background-radius: 10;"
                            + "-fx-padding: 15;"
                            + "-fx-min-width: 150;"
                            + "-fx-max-width: 150;"
                            + "-fx-min-height: 150;"
                            + "-fx-alignment: center;");
                    selectedUser.set(user.toString());
                });

                profilesContainer.getChildren().add(profileCard);
                userCount[0]++;
            });
        }

        scrollPane.setContent(profilesContainer);
        scrollPane.setPrefHeight(200);
        scrollPane.setMaxWidth(USE_PREF_SIZE);

        String buttonStyle = "-fx-background-color: #3a3a3a;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 5;"
                + "-fx-padding: 8 15 8 15;";

        Button addUserButton = new Button("Add User");
        addUserButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        addUserButton.setStyle(buttonStyle);
        addUserButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add User");
            dialog.setHeaderText("Add a new user");
            dialog.setContentText("Enter username:");

            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setStyle("-fx-background-color: #333;");
            dialogPane.lookup(".content.label").
                    setStyle("-fx-text-fill: white;");
            dialogPane.lookup(".header-panel").
                    setStyle("-fx-background-color: #222;");

            String newUser = dialog.showAndWait().orElse("").trim();
            if (!newUser.isEmpty() && !users.contains(newUser)) {
                int newNumber = profilesContainer.getChildren().size() + 1;
                VBox profileCard = new VBox(HBOX_SPACING);
                profileCard.setStyle("-fx-background-color: #3a3a3a;"
                        + "-fx-background-radius: 10;"
                        + "-fx-padding: 15;"
                        + "-fx-min-width: 150;"
                        + "-fx-max-width: 150;"
                        + "-fx-min-height: 150;"
                        + "-fx-alignment: center;");

                Label numberLabel = new Label(String.valueOf(newNumber));
                numberLabel.setStyle("-fx-text-fill: white;"
                        + " -fx-font-size: 32; -fx-font-weight: bold;");

                Label nameLabel = new Label(newUser);
                nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

                profileCard.getChildren().addAll(numberLabel, nameLabel);

                profileCard.setOnMouseEntered(ev -> {
                    profileCard.setStyle("-fx-background-color: #4a4a4a;"
                            + "-fx-background-radius: 10;"
                            + "-fx-padding: 15;"
                            + "-fx-min-width: 150;"
                            + "-fx-max-width: 150;"
                            + "-fx-min-height: 150;"
                            + "-fx-alignment: center;"
                            + "-fx-scale-x: 1.1;"
                            + "-fx-scale-y: 1.1;");
                    profileCard.setCursor(Cursor.HAND);
                });

                profileCard.setOnMouseExited(ev -> {
                    if (selectedUser.get() == null
                            || !selectedUser.get().equals(newUser)) {
                        profileCard.setStyle("-fx-background-color: #3a3a3a;"
                                + "-fx-background-radius: 10;"
                                + "-fx-padding: 15;"
                                + "-fx-min-width: 150;"
                                + "-fx-max-width: 150;"
                                + "-fx-min-height: 150;"
                                + "-fx-alignment: center;"
                                + "-fx-scale-x: 1;"
                                + "-fx-scale-y: 1;");
                    }
                });

                profileCard.setOnMouseClicked(ev -> {
                    profilesContainer.getChildren().forEach(node -> {
                        if (node instanceof VBox) {
                            node.setStyle("-fx-background-color: #3a3a3a;"
                                    + "-fx-background-radius: 10;"
                                    + "-fx-padding: 15;"
                                    + "-fx-min-width: 150;"
                                    + "-fx-max-width: 150;"
                                    + "-fx-min-height: 150;"
                                    + "-fx-alignment: center;");
                        }
                    });
                    profileCard.setStyle("-fx-background-color: #4a4a4a;"
                            + "-fx-background-radius: 10;"
                            + "-fx-padding: 15;"
                            + "-fx-min-width: 150;"
                            + "-fx-max-width: 150;"
                            + "-fx-min-height: 150;"
                            + "-fx-alignment: center;");
                    selectedUser.set(newUser);
                });

                users.add(newUser);
                JSONObject newUserObj = new JSONObject();
                newUserObj.put("HighScores", new JSONObject());
                newUserObj.put("SavedLevels", new JSONObject());
                newUserObj.put("CurrentLevel", 1);
                newUserObj.put("CompletedLevels", new JSONArray());
                playerProfileObj.put(newUser, newUserObj);
                savePlayerProfile();

                profilesContainer.getChildren().add(profileCard);
            }
        });

        Button removeUserButton = new Button("Remove User");
        removeUserButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        removeUserButton.setStyle(buttonStyle);
        removeUserButton.setOnAction(e -> {
            if (selectedUser.get() != null) {
                if (selectedUser.get().equals(currentUser)) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Current user cannot be removed!");
                    errorAlert.setContentText(
                            "Please switch to a different user and try again.");
                    errorAlert.showAndWait();
                } else {
                    profilesContainer.getChildren().removeIf(node -> {
                        if (node instanceof VBox) {
                            Label label = (Label)
                                    ((VBox) node).getChildren().get(1);
                            return label.getText().equals(selectedUser.get());
                        }
                        return false;
                    });
                    users.remove(selectedUser.get());
                    playerProfileObj.remove(selectedUser.get());
                    savePlayerProfile();
                    selectedUser.set(null);

                    // Renumber remaining profiles
                    int[] newCount = {1};
                    profilesContainer.getChildren().forEach(node -> {
                        if (node instanceof VBox) {
                            Label numberLabel = (Label)
                                    ((VBox) node).getChildren().get(0);
                            numberLabel.setText(String.valueOf(newCount[0]++));
                        }
                    });
                }
            }
        });

        Button selectUserButton = new Button("Select User");
        selectUserButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        selectUserButton.setStyle(buttonStyle);
        selectUserButton.setOnAction(e -> {
            if (selectedUser.get() != null) {
                changeCurrentUser(selectedUser.get());
                setupHomeScreen();
                primaryStage.setScene(homeScene);
            }
        });

        Button backButton = new Button("Back");
        backButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        backButton.setStyle(buttonStyle);
        backButton.setOnAction(e -> primaryStage.setScene(homeScene));

        HBox buttonBox = new HBox(VBOX_SPACING, addUserButton,
                removeUserButton, selectUserButton, backButton);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 15 0 0 0");

        userMenuScreen.getChildren().addAll(userMenuLabel,
                scrollPane, buttonBox);

        Scene userMenuScene = new Scene(userMenuScreen);
        primaryStage.setScene(userMenuScene);
    }

    private void changeCurrentUser(final String newUser) {
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
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #222; "
                + "-fx-background-color: transparent; "
                + "-fx-border-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        HBox savesContainer = new HBox(VBOX_SPACING);
        savesContainer.setStyle("-fx-background-color"
                + ": transparent; -fx-padding: 20;");
        savesContainer.setAlignment(Pos.CENTER);

        StringProperty selectedGame = new SimpleStringProperty(null);

        String[] keyImages = {"blue", "red",
                "green", "yellow"};

        int[] saveCount = {1}; // Array to allow modification in lambda
        savedGames.forEach(save -> {
            VBox gameCard = new VBox(HBOX_SPACING);
            gameCard.setStyle("-fx-background-color: #3a3a3a;"
                    + "-fx-background-radius: 10;"
                    + "-fx-padding: 15;"
                    + "-fx-min-width: 150;"
                    + "-fx-max-width: 150;"
                    + "-fx-min-height: 150;"
                    + "-fx-alignment: center;");
            ImageView keyImage = (saveCount[0] % KEY_AMOUNT)
                    == 0 ? KEY_ICON_BLUE
                    : (saveCount[0] % KEY_AMOUNT) == 1 ? KEY_ICON_RED
                    : (saveCount[0] % KEY_AMOUNT) == 2
                    ? KEY_ICON_GREEN : KEY_ICON_YELLOW;
            keyImage.setFitHeight(KEY_IMAGE_SIZE);
            keyImage.setFitWidth(KEY_IMAGE_SIZE);
            Label nameLabel = new Label(save);
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
            gameCard.getChildren().addAll(keyImage, nameLabel);
            gameCard.setOnMouseEntered(e -> {
                gameCard.setStyle("-fx-background-color: #4a4a4a;"
                        + "-fx-background-radius: 10;"
                        + "-fx-padding: 15;"
                        + "-fx-min-width: 150;"
                        + "-fx-max-width: 150;"
                        + "-fx-min-height: 150;"
                        + "-fx-alignment: center;"
                        + "-fx-scale-x: 1.1;"
                        + "-fx-scale-y: 1.1;");
                gameCard.setCursor(Cursor.HAND);
            });
            gameCard.setOnMouseExited(e -> {
                if (selectedGame.get() == null
                        || !selectedGame.get().equals(save)) {
                    gameCard.setStyle("-fx-background-color: #3a3a3a;"
                            + "-fx-background-radius: 10;"
                            + "-fx-padding: 15;"
                            + "-fx-min-width: 150;"
                            + "-fx-max-width: 150;"
                            + "-fx-min-height: 150;"
                            + "-fx-alignment: center;"
                            + "-fx-scale-x: 1;"
                            + "-fx-scale-y: 1;");
                }
            });
            gameCard.setOnMouseClicked(e -> {
                savesContainer.getChildren().forEach(node -> {
                    if (node instanceof VBox) {
                        node.setStyle("-fx-background-color: #3a3a3a;"
                                + "-fx-background-radius: 10;"
                                + "-fx-padding: 15;"
                                + "-fx-min-width: 150;"
                                + "-fx-max-width: 150;"
                                + "-fx-min-height: 150;"
                                + "-fx-alignment: center;");
                    }
                });
                gameCard.setStyle("-fx-background-color: #4a4a4a;"
                        + "-fx-background-radius: 10;"
                        + "-fx-padding: 15;"
                        + "-fx-min-width: 150;"
                        + "-fx-max-width: 150;"
                        + "-fx-min-height: 150;"
                        + "-fx-alignment: center;");
                selectedGame.set(save);
            });
            savesContainer.getChildren().add(gameCard);
            saveCount[0]++;
        });
        scrollPane.setContent(savesContainer);
        scrollPane.setPrefHeight(PAUSE_MENU_MAX_HEIGHT);
        scrollPane.setMaxWidth(USE_PREF_SIZE);

        // Load selected game button
        Button loadSelectedButton = new Button("Load Selected Game");
        loadSelectedButton.setFont(new Font(FONT_ARIAL,
                FONT_SIZE_CURRENT_USER));
        loadSelectedButton.setOnAction(e -> {
            System.out.println("Loading selected game: "
                    + selectedGame.toString());
            loadSelectedGame(selectedGame.get());
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setFont(new Font(FONT_ARIAL, FONT_SIZE_CURRENT_USER));
        backButton.setOnAction(e -> primaryStage.setScene(homeScene));

        savedGamesScreen.getChildren().addAll(savedGamesLabel,
                scrollPane, loadSelectedButton, backButton);

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
    private void loadSelectedGame(final String gameName) {
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

    private void storyScreen(final int chapter) {
        /* create a starwars line scene to displaying the story.
           it should consist of a block of text that will
           scroll up the screen and get smaller as it goes up.
           the text should be in a font that looks like the starwars font.
         */
        StackPane storyPane = new StackPane();
        Text storyText = new Text();
        storyText.setText(readChapter(chapter));
        storyText.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        storyText.setFill(Paint.valueOf("white"));
        storyText.setTextAlignment(TextAlignment.CENTER);
        BackgroundImage backgroundImageView =
                new BackgroundImage(new Image("StoryImage.png"),
                        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                        BackgroundPosition.DEFAULT,
                        new BackgroundSize(100, 100, true, true, false, true));
        Background background = new Background(backgroundImageView);
        storyPane.setBackground(background);
        storyPane.getChildren().add(storyText);
        Scene storyScene = new Scene(storyPane);
        // add a button to the scene to go to next scene
        Button nextButton = new Button("Next");
        nextButton.setOnAction(e -> startNewGame());
        storyPane.getChildren().add(nextButton);

        // put the button in the bottom right corner and make it transparent
        StackPane.setAlignment(nextButton, Pos.BOTTOM_RIGHT);
        nextButton.setStyle("-fx-background-color: transparent; "
                + "-fx-text-fill: white; -fx-font-size: 20;");


        primaryStage.setScene(storyScene);


        // create a timeline that will move the text up the screen
        // the text should move up the screen and get smaller as it goes up
        Timeline storyTimeline =
                new Timeline(new KeyFrame(Duration.seconds(0.05), event -> {
            storyText.setTranslateY(storyText.getTranslateY() - 1);
            storyText.setFont(Font.font("Arial", FontWeight.BOLD,
                    storyText.getFont().getSize() - 0.05));
        }));
        storyTimeline.setCycleCount(1000);
        storyTimeline.play();



    }

    private String readChapter(int chapter) {
        // read the text from the file that corresponds to the chapter
        // return the text
        try (InputStream inputStream =
                GameManager.class.getClassLoader().
                        getResourceAsStream("Chapter" + chapter + ".txt")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found");
            }
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(
                    inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
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
            List<String> byUser = new ArrayList<>(
                    this.highScores.values());

            for (int i = 0;
                    i < Math.min(highScoresInt.size(), MAX_SCORES_AMOUNT);
                    i++) {

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
        loadButton.setOnAction(e -> {
            if (tickTimeline != null) {
                tickTimeline.stop();
                tickTimeline = null;
            }
            showSavedGamesScreen();
            togglePause();
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
    private void loadGame(final String gameName) {
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

            HBox deathCauseBox = new HBox(HBOX_SPACING);
            deathCauseBox.setAlignment(Pos.CENTER);

            VBox gameOverBox = new VBox(VBOX_SPACING);
            double buttonWidth = BUTTON_WIDTH;
            gameOverBox.setAlignment(Pos.CENTER);

            Label messageLabel = new Label("Game Over!");
            messageLabel.setStyle("-fx-text-fill: red;"
                    + " -fx-font-size: 48; "
                    + "-fx-font-family: monospace;");

            ImageView deathCauseImage = new
                    ImageView(new Image(deathCause + ".png"));

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
                    causeLabel, scoreBoard, restartButton,
                    gameOverMainMenuButton, exitButton);
            gameOverBox.setLayoutX(scene.getWidth() / 2
                    - gameOverBox.getPrefWidth() / 2);
            gameOverBox.setLayoutY(scene.getHeight() / 2
                    - gameOverBox.getPrefHeight() / 2);
            deathCauseBox.getChildren().addAll(deathCauseImage, gameOverBox);
            gameOverMenu.getChildren().add(deathCauseBox);
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
                    scoreLabel, scoreBoard, nextLevelButton, mainMenuButton,
                    exitButton);

            levelCompleteMenu.getChildren().add(levelCompleteBox);
        }

        levelCompleteMenu.setPrefSize(scene.getWidth(), scene.getHeight());
        stackPane.getChildren().add(levelCompleteMenu);
    }

    /**
     * Loads the next level once current level has been completed.
     */
    private void loadNextLevel() {
        storyScreen(currentLevel);
        if (currentLevel == NUMBER_OF_LEVELS) {
            currentLevel = 1;
            userProfileObj.put("CompletedTheGame", true);
            updateCurrentLevel();
            restartGame();

            primaryStage.setScene(homeScene);
        } else {

            currentLevel++;
            updateCurrentLevel();
            level = new Level(currentLevel);
            player = level.getPlayer();
            dead = false;
            timeElapsed = 0;
            GameState.setupSate(level, player, this);
            if (!tickTimeline.getStatus().equals(Timeline.Status.RUNNING)) {
                tickTimeline.play();
            }
            stackPane.getChildren().remove(levelCompleteMenu);
            drawGame();
        }
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
