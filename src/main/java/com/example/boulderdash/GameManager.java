package com.example.boulderdash;

import com.example.boulderdash.Actors.Actor;

import javafx.scene.control.Label;


import com.example.boulderdash.enums.KeyColours;
import javafx.animation.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.util.*;


/**
 * Main controller for the game. This class handles game initialisation,
 * user input, game state management, and the main game loop.
 */
public class GameManager extends Application {
    private List<Actor> deadActors = new ArrayList<>();
    private List<Actor> newBorns = new ArrayList<>();
    private Timeline tickTimeline;
    private Level level;
    private Player player;
    private Scene scene;
    private Pane levelCompleteMenu;
    private Pane gameOverMenu;
    private VBox pauseMenu;
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
    private static final ImageView diamondCountIcon = new ImageView(new Image("Actor Images/diamond.png"));
    private static final ImageView clockIcon = new ImageView(new Image("clock.png"));
    private static final ImageView keyIconBlue = new ImageView(new Image("Key Icon Images/blue_key_icon.png"));
    private static final ImageView keyIconRed = new ImageView(new Image("Key Icon Images/red_key_icon.png"));
    private static final ImageView keyIconGreen = new ImageView(new Image("Key Icon Images/green_key_icon.png"));
    private static final ImageView keyIconYellow = new ImageView(new Image("Key Icon Images/yellow_key_icon.png"));
    private float timeElapsed;
    private final float tickTime = 0.1f;
    private int tileSize = 80;
    private boolean dead = false;
    private boolean isPaused = false;
    private Stage primaryStage;
    private Scene homeScene;
    private VBox homeScreen;
    private Label titleLabel;
    private List<Integer> highScores;


    @Override
    public void start(Stage primaryStage) {
        // Set the title of the window
        primaryStage.setTitle("Boulder Dash");

        this.primaryStage = primaryStage;
        highScores = new ArrayList<>();

        setupHomeScreen();

        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    private void setupHomeScreen() {
        homeScreen = new VBox(20);
        homeScreen.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #222;");

        // Add a logo
        ImageView logo = new ImageView(new Image(getClass().getResource("/logo.png").toExternalForm()));
        logo.setFitHeight(200);
        logo.setFitWidth(400);

        // Add a title label
        titleLabel = new Label("PRESS START TO PLAY");
        titleLabel.setFont(new Font("Ariel", 40));
        titleLabel.setStyle("-fx-text-fill: white;");

        // Start Game button
        Button startButton = new Button("Start");
        startButton.setFont(new Font("Arial", 20));
        startButton.setOnAction(e -> startNewGame());

        // Load Game button
        Button loadButton = new Button("Load Game");
        loadButton.setFont(new Font("Arial", 20));

        // High Score Table
        Label highScoreLabel = new Label("High Scores:");
        highScoreLabel.setFont(new Font("Arial", 25));
        highScoreLabel.setStyle("-fx-text-fill: white;");

        VBox highScoreBoard = createHighScoreBoard();

        homeScreen.getChildren().addAll(logo, titleLabel, startButton, loadButton, highScoreLabel, highScoreBoard);
        homeScene = new Scene(homeScreen, 600, 600);
    }

    private void UIsetUp() {
        grid.setHgap(0);
        grid.setVgap(0);

        diamondCountIcon.setFitHeight(50);
        diamondCountIcon.setFitWidth(50);
        clockIcon.setFitHeight(50);
        clockIcon.setFitWidth(40);
        keyIconBlue.setFitHeight(50);
        keyIconBlue.setFitWidth(50);
        keyIconRed.setFitHeight(50);
        keyIconRed.setFitWidth(50);
        keyIconGreen.setFitHeight(50);
        keyIconGreen.setFitWidth(50);
        keyIconYellow.setFitHeight(50);
        keyIconYellow.setFitWidth(50);
        infoBar.getChildren().addAll(clockIcon, timeLabel, diamondCountIcon,
                diamondsLabel, keyIconBlue, keyLabelBlue,
                keyIconRed, keyLabelRed, keyIconGreen, keyLabelGreen,
                keyIconYellow, keyLabelYellow);
        infoBar.setPrefHeight(70);
        infoBar.setAlignment(javafx.geometry.Pos.CENTER);
        infoBar.setStyle("-fx-padding: 5; -fx-background-color: #333; -fx-text-fill: white;");
        timeLabel.setStyle("-fx-text-fill: white;");
        diamondsLabel.setStyle("-fx-text-fill: white;");
        keyLabelBlue.setStyle("-fx-text-fill: white;");
        keyLabelRed.setStyle("-fx-text-fill: white;");
        keyLabelGreen.setStyle("-fx-text-fill: white;");
        keyLabelYellow.setStyle("-fx-text-fill: white;");
        diamondCountIcon.setStyle("-fx-padding: 10;");

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(infoBar);

        stackPane.getChildren().addAll(grid, transitionPane, borderPane);

        scene = new Scene(stackPane, 600, 600);  // width: 400, height: 400
        scene.setOnKeyPressed(this::processKeyEvent);
        scene.setOnKeyReleased(event -> player.setDirection(Direction.STATIONARY));

        tickTimeline = new Timeline(new KeyFrame(Duration.seconds(tickTime), event -> tick()));
        tickTimeline.setCycleCount(Animation.INDEFINITE);
    }

    // High scoreboard
    private VBox createHighScoreBoard() {
        VBox highScoreBoard = new VBox(10); // Vertical spacing between scores
        highScoreBoard.setStyle("-fx-padding: 10; -fx-alignment: center;");

        if (highScores.isEmpty()) {
            Label noScoresLabel = new Label("No score yet!");
            noScoresLabel.setFont(new Font("Arial", 18));
            noScoresLabel.setStyle("-fx-text-fill: grey;");
            highScoreBoard.getChildren().add(noScoresLabel);
        } else {
            for (int i = 0; i < highScores.size(); i++) {
                Label scoreLabel = new Label((i + 1) + ". " + highScores.get(i));
                scoreLabel.setFont(new Font("Arial", 18));
                scoreLabel.setStyle("-fx-text-fill: white;");
                highScoreBoard.getChildren().add(scoreLabel);
            }
        }

        return highScoreBoard;
    }

    private void startNewGame() {
        level = new Level();
        player = level.getPlayer();
        timeElapsed = 0;
        tileSize = level.getTileSize();

        UIsetUp();

        GameState.setupSate(level, player, this);
        tickTimeline.play();

        drawGame();
        primaryStage.setScene(scene);
        primaryStage.setHeight(level.getRows() * tileSize);
        primaryStage.setWidth(level.getCols() * tileSize);
    }

    /**
     * Marks an actor for removal from the game
     */
    public void killActor(Actor actor) {
        deadActors.add(actor);
    }
    public void addActor(Actor actor) {
        newBorns.add(actor);
    }

    /**
     * Removes all actors that have been marked for removal in the current game tick
     */
    private void removeActors() {
        for (Actor actor : deadActors) {
            level.removeActor(actor);
        }
        deadActors = new ArrayList<>();
    }
    private void createNewActors(){
        level.addActors(newBorns);
        newBorns = new ArrayList<>();
    }

    public void drawGame(){
        timeLabel.setText((int)(level.getTimeLimit() - timeElapsed) + "s");
        diamondsLabel.setText(player.getDiamondsCollected() + "/" + level.getDiamondsRequired());
        keyLabelBlue.setText("x" + String.valueOf(player.getKeys().get(KeyColours.BLUE)));
        keyLabelRed.setText("x" + String.valueOf(player.getKeys().get(KeyColours.RED)));
        keyLabelGreen.setText("x" + String.valueOf(player.getKeys().get(KeyColours.GREEN)));
        keyLabelYellow.setText("x" + String.valueOf(player.getKeys().get(KeyColours.YELLOW)));

        grid.getChildren().clear(); // Clears the grid first


        // Retrieve the level's tiles and dimensions
        List<List<Tile>> tiles = level.getTiles();
        int rows = level.getRows();
        int columns = level.getCols();


        Map<ImageView, Actor> actorsToAnimate= new HashMap<>();

        // Iterate through each tile and render it

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Tile tile = tiles.get(row).get(col);
                StackPane stackPane = new StackPane(); // Allows stacking multiple visuals
                ImageView imageView = new ImageView(tile.getImage()); // Tile background

                // Scale tile images to match grid size
                imageView.setFitWidth(tileSize);
                imageView.setFitHeight(tileSize);

                stackPane.getChildren().add(imageView);

                // If a tile is occupied, draw the actor occupying it
                if (tile.isOccupied()) {
                    Actor occupier = tile.getOccupier();
                    ImageView actorImageView = new ImageView(occupier.getImage());
                    actorImageView.setFitHeight(tileSize*0.8);
                    actorImageView.setFitWidth(tileSize*0.8);
                    if (occupier.getIsTransferring()){
                        // if the actor is transferring animate the transfer
                        actorsToAnimate.put(actorImageView, occupier);   // add the actor to the offset map
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

        for (Map.Entry<ImageView, Actor> entry : actorsToAnimate.entrySet()){
            ImageView actorImageView = entry.getKey();
            Actor actor = entry.getValue();
            Tile previousPosition = actor.getPreviousPosition();
            Tile currentPosition = actor.getPosition();
            actorImageView.setTranslateX(previousPosition.getColumn() * tileSize); // Set the initial X position
            actorImageView.setTranslateY(previousPosition.getRow() * tileSize);
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), actorImageView);
            translateTransition.setFromX(previousPosition.getColumn() * tileSize);
            translateTransition.setFromY(previousPosition.getRow() * tileSize);
            translateTransition.setToX(currentPosition.getColumn() * tileSize);
            translateTransition.setToY(currentPosition.getRow() * tileSize);

            translateTransition.setOnFinished(e -> {
                actor.checkCollisions();
            });

            translateTransition.play();
            transitionPane.getChildren().add(actorImageView);
        }
    }

    /**
     * Processes user input to control the player or state
     *
     * @param event the KeyEvent triggered by a key press
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
        event.consume(); // Prevents further handling of this event by other UI elements
    }


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
     * Displays the pause menu on the screen
     */
    private void showPauseMenu() {
        if (pauseMenu == null) {
            createPauseMenu(); // Initialize the pause menu if it doesn't exist
        }
        if (!stackPane.getChildren().contains(pauseMenu)) {
            pauseMenu.setLayoutX((scene.getWidth() - 200) / 2);
            pauseMenu.setLayoutY((scene.getHeight() - 200) / 2);
            stackPane.getChildren().add(pauseMenu); // Add the pause menu to the grid
        }
    }

    /**
     * Hides the pause menu
     */
    private void hidePauseMenu() {
        if (pauseMenu != null) {
            stackPane.getChildren().remove(pauseMenu); // Remove the pause menu from the grid
        }
    }

    /**
     * Creates the pause menu
     */
    private void createPauseMenu() {
        pauseMenu = new VBox();
        pauseMenu.setStyle("-fx-padding: 20;");
        pauseMenu.setMaxSize(200,200);
        pauseMenu.setAlignment(javafx.geometry.Pos.CENTER);


        String buttonStyle =  "-fx-border-color: white darkgrey darkgrey white;" +
                "-fx-border-width: 4; -fx-text-fill: black; -fx-font-family: monospace; -fx-font-size: 12; -fx-cursor: hand;";

        Button resumeButton = new Button("Resume");
        Button saveButton = new Button("Save Game");
        Button loadButton = new Button("Load Game");
        Button exitButton = new Button("Exit Game");

        resumeButton.setStyle(buttonStyle);
        saveButton.setStyle(buttonStyle);
        loadButton.setStyle(buttonStyle);
        exitButton.setStyle(buttonStyle);

        resumeButton.setLayoutX(60);
        resumeButton.setLayoutY(20);
        saveButton.setLayoutX(60);
        saveButton.setLayoutY(60);
        loadButton.setLayoutX(60);
        loadButton.setLayoutY(100);
        exitButton.setLayoutX(60);
        exitButton.setLayoutY(140);

        resumeButton.setOnAction(e -> togglePause());
        saveButton.setOnAction(e -> saveGame());
        loadButton.setOnAction(e -> loadGame());
        exitButton.setOnAction(e -> exitGame());

        pauseMenu.setStyle("-fx-background-color: #333; -fx-padding: 20;");

        pauseMenu.getChildren().addAll(resumeButton, saveButton, loadButton, exitButton);
    }



    /**
     * Saves the current game state
     */
    private void saveGame() {
        System.out.println("Game saved!");
    }

    /**
     * Loads a previously saved game state
     */
    private void loadGame() {
        System.out.println("Game loaded!");
    }

    /**
     * Exits the game, saving the state before closing
     */
    private void exitGame() {
        saveGame();
        Stage stage = (Stage) grid.getScene().getWindow();
        stage.close();
    }


  /**
     * Updates the game state, processes actors' actions, and redraws the game screen
     * at each tick.
     */
    public void tick() {
        timeElapsed += tickTime;
        removeActors();// Remove any dead actors
        createNewActors();
        drawGame();// Redraw the grid

        if (!dead) {
            for (Actor actor : level.getActors()) {
                actor.move(); // Move all active actors
            }
        }

        //if (player.getDiamondsCollected() >= level.getDiamondsRequired()) {
        //    winGame();
        //}

        if (timeElapsed > level.getTimeLimit()){
            looseGame();
        }
    }

    /**
     * Ends the game, marked it as a loss
     */
    public void looseGame() {
        Text gameOverText = new Text("Game Over");
        gameOverText.setFont(new Font("Arial", 75));
        dead = true;
        tickTimeline.stop();
        showGameOverScreen();

    }

    private void showGameOverScreen() {
        drawGame();
        if (gameOverMenu == null) {
            gameOverMenu = new Pane();
            gameOverMenu.setStyle("-fx-background-color: #333;");
            gameOverMenu.setMaxSize(200, 200);

            Label messageLabel = new Label("Game Over!");
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16; -fx-font-family: monospace;");
            messageLabel.setLayoutX(60);
            messageLabel.setLayoutY(40);

            Button exitButton = new Button("Exit Game");
            exitButton.setStyle("-fx-background-color: grey; -fx-border-color: white darkgrey darkgrey white; " +
                    "-fx-border-width: 4; -fx-text-fill: white; -fx-font-family: monospace; -fx-font-size: 12;" +
                    "-fx-cursor: hand;");
            exitButton.setLayoutX(60);
            exitButton.setLayoutY(80);
            exitButton.setOnAction(e -> exitGame());

            gameOverMenu.getChildren().addAll(messageLabel, exitButton);
        }

        gameOverMenu.setLayoutX((scene.getWidth() - 200) / 2);
        gameOverMenu.setLayoutY((scene.getHeight() - 200) / 2);

        stackPane.getChildren().add(gameOverMenu);
    }

    /**
     * Ends the current level
     */
    public void winGame() {
        dead = true;
        drawGame();
        showLevelCompleteScreen();
    }

    private void showLevelCompleteScreen() {
        tickTimeline.stop();
        if (levelCompleteMenu == null) {
            levelCompleteMenu = new Pane();
            levelCompleteMenu.setStyle("-fx-background-color: #333;");
            levelCompleteMenu.setMaxSize(200, 200);

            Label messageLabel = new Label("Level Won!");
            messageLabel.setStyle("-fx-text-fill: lightgreen; -fx-font-size: 16; -fx-font-family: monospace;");
            messageLabel.setLayoutX(60);
            messageLabel.setLayoutY(40);

            Button nextLevelButton = new Button("Next Level");
            nextLevelButton.setStyle("-fx-background-color: grey; -fx-border-color: white darkgrey darkgrey white; " +
                    "-fx-border-width: 4; -fx-text-fill: white; -fx-font-family: monospace; -fx-font-size: 12; -fx-cursor: hand;");
            nextLevelButton.setLayoutX(60);
            nextLevelButton.setLayoutY(80);


            Button exitButton = new Button("Exit Game");
            exitButton.setStyle("-fx-background-color: grey; -fx-border-color: white darkgrey darkgrey white; " +
                    "-fx-border-width: 4; -fx-text-fill: white; -fx-font-family: monospace; -fx-font-size: 12; -fx-cursor: hand;");
            exitButton.setLayoutX(60);
            exitButton.setLayoutY(120);

            nextLevelButton.setOnAction(e -> loadNextLevel());
            exitButton.setOnAction(e -> exitGame());

            levelCompleteMenu.getChildren().addAll(messageLabel, nextLevelButton, exitButton);
        }

        levelCompleteMenu.setLayoutX((scene.getWidth() - 200) / 2);
        levelCompleteMenu.setLayoutY((scene.getHeight() - 200) / 2);
        stackPane.getChildren().add(levelCompleteMenu);
    }

    private void loadNextLevel() {
        System.out.println("Loading next level!");
        level = new Level();
        player = level.getPlayer();
        timeElapsed = 0;
        GameState.setupSate(level, player, this);
    }

    /**
     * Main method to launch the program
     *
     * @param args command-line arguments
     */

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}
