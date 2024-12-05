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

    private VBox gameOverMenu;
    private List<Actor> deadActors = new ArrayList<>();
    private List<Actor> newBorns = new ArrayList<>();
    private Timeline tickTimeline;
    private Level level;
    private Player player;
    private Scene scene;
    private VBox pauseMenu;
    private final GridPane grid = new GridPane();
    private final Pane transitionPane = new Pane();
    private final HBox infoBar = new HBox(20);
    private final Label timeLabel = new Label();
    private final Label diamondsLabel = new Label();
    private final Label keyLabelBlue = new Label();
    private final Label keyLabelRed = new Label();
    private final Label keyLabelGreen = new Label();
    private final Label keyLabelYellow = new Label();
    private static final ImageView diamondCountIcon = new ImageView(new Image("diamond.png"));
    private static final ImageView clockIcon = new ImageView(new Image("clock.png"));
    private static final ImageView keyIconBlue = new ImageView(new Image("blue_key_icon.png"));
    private static final ImageView keyIconRed = new ImageView(new Image("red_key_icon.png"));
    private static final ImageView keyIconGreen = new ImageView(new Image("green_key_icon.png"));
    private static final ImageView keyIconYellow = new ImageView(new Image("yellow_key_icon.png"));
    private float timeElapsed;
    private final float tickTime = 0.1f;
    private boolean dead = false;
    private boolean isPaused = false;
    private VBox levelCompleteMenu;

    @Override
    public void start(Stage primaryStage) {
        level = new Level();
        player = level.getPlayer();
        timeElapsed = 0;



        GameState.setupSate(level, player, this);

        // Create grid layout for the game board
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


        infoBar.getChildren().addAll(clockIcon, timeLabel, diamondCountIcon, diamondsLabel, keyIconBlue, keyLabelBlue,
                keyIconRed, keyLabelRed, keyIconGreen, keyLabelGreen, keyIconYellow, keyLabelYellow);
        infoBar.setPrefHeight(70);
        infoBar.setAlignment(javafx.geometry.Pos.CENTER);
        infoBar.setStyle("-fx-padding: 10; -fx-background-color: #333; -fx-text-fill: white;");
        timeLabel.setStyle("-fx-text-fill: white;");
        diamondsLabel.setStyle("-fx-text-fill: white;");
        keyLabelBlue.setStyle("-fx-text-fill: white;");
        keyLabelRed.setStyle("-fx-text-fill: white;");
        keyLabelGreen.setStyle("-fx-text-fill: white;");
        keyLabelYellow.setStyle("-fx-text-fill: white;");
        diamondCountIcon.setStyle("-fx-padding: 10;");


        StackPane stackPane = new StackPane();
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(infoBar);
        stackPane.getChildren().addAll(grid, transitionPane, borderPane);




        int rows = level.getRows();
        int columns = level.getCols();

        scene = new Scene(stackPane, columns*100, rows*100);  // width: 400, height: 400

        scene.setOnKeyPressed(this::processKeyEvent);

        scene.setOnKeyReleased(event -> player.setDirection(Direction.STATIONARY));

        tickTimeline = new Timeline(new KeyFrame(Duration.seconds(tickTime), event -> tick()));
        tickTimeline.setCycleCount(Animation.INDEFINITE);
        tickTimeline.play();
        //drawGame();

        // Set the title of the window
        primaryStage.setTitle("Boulder Dash");

        // Set the scene for the stage
        primaryStage.setScene(scene);

        // Show the window
        primaryStage.show();
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
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);

                stackPane.getChildren().add(imageView);

                // If a tile is occupied, draw the actor occupying it
                if (tile.isOccupied()) {
                    Actor occupier = tile.getOccupier();
                    ImageView actorImageView = new ImageView(occupier.getImage());
                    actorImageView.setFitHeight(80);
                    actorImageView.setFitWidth(80);
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
        ParallelTransition parallelTransition = new ParallelTransition();
        for (Map.Entry<ImageView, Actor> entry : actorsToAnimate.entrySet()){
            ImageView actorImageView = entry.getKey();
            Actor actor = entry.getValue();
            Tile previousPosition = actor.getPreviousPosition();
            Tile currentPosition = actor.getPosition();
            actorImageView.setTranslateX(previousPosition.getColumn() * 100); // Set the initial X position
            actorImageView.setTranslateY(previousPosition.getRow() * 100);
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), actorImageView);
            translateTransition.setFromX(previousPosition.getColumn() * 100);
            translateTransition.setFromY(previousPosition.getRow() * 100);
            translateTransition.setToX(currentPosition.getColumn() * 100);
            translateTransition.setToY(currentPosition.getRow() * 100);

            parallelTransition.getChildren().add(translateTransition);

            transitionPane.getChildren().add(actorImageView);
        }
        parallelTransition.setOnFinished(e -> {
            for (Map.Entry<ImageView, Actor> entry : actorsToAnimate.entrySet()){
                ImageView actorImageView = entry.getKey();
                Actor actor = entry.getValue();
                actor.checkCollisions();
                transitionPane.getChildren().remove(actorImageView);
            }
        });
        parallelTransition.play();
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
        if (!grid.getChildren().contains(pauseMenu)) {
            pauseMenu.setTranslateX((scene.getWidth() - 200) / 2); // Center horizontally
            pauseMenu.setTranslateY((scene.getHeight() - 200) / 2); // Center vertically
            grid.getChildren().add(pauseMenu); // Add the pause menu to the grid
        }
    }

    /**
     * Hides the pause menu
     */
    private void hidePauseMenu() {
        if (pauseMenu != null) {
            grid.getChildren().remove(pauseMenu); // Remove the pause menu from the grid
        }
    }

    /**
     * Creates the pause menu
     */
    private void createPauseMenu() {
        pauseMenu = new VBox(15); // Vertical layout with spacing between buttons
        pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10;");

        Button resumeButton = new Button("Resume");
        Button saveButton = new Button("Save Game");
        Button loadButton = new Button("Load Game");
        Button exitButton = new Button("Exit Game");

        // Set actions for each button
        resumeButton.setOnAction(e -> togglePause());
        saveButton.setOnAction(e -> saveGame());
        loadButton.setOnAction(e -> loadGame());
        exitButton.setOnAction(e -> exitGame());

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

        if (player.getDiamondsCollected() >= level.getDiamondsRequired()) {
            winGame();
        }

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
        if (gameOverMenu == null) {
            gameOverMenu = new VBox(40);
            gameOverMenu.setStyle("-fx-background-color: grey;" +
                    "-fx-alignment: center;");

            Label messageLabel = new Label("Game Over!");
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16;" +
                    "-fx-font-family: monospace;");

            Button exitButton = new Button("Exit Game");
            exitButton.setStyle(
                    "-fx-background-color: grey;" +
                            "-fx-border-color: white darkgrey darkgrey white;" +
                            "-fx-border-width: 4;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-family: monospace;" +
                            "-fx-font-size: 12;" +
                            "-fx-padding: 0 0;"

            );

            exitButton.setOnAction(e -> exitGame());


            gameOverMenu.getChildren().addAll(messageLabel, exitButton);
        }

        gameOverMenu.setTranslateX((scene.getWidth() - 200) / 2);
        gameOverMenu.setTranslateY((scene.getHeight() - 200) / 2);

        grid.getChildren().add(gameOverMenu);

    }





    /**
     * Ends the current level
     */
    public void winGame() {
        dead = true;
        showLevelCompleteScreen();
    }

    private void showLevelCompleteScreen() {
        tickTimeline.stop();
        if (levelCompleteMenu == null) {
            levelCompleteMenu = new VBox(20);
            levelCompleteMenu.setStyle("-fx-background-color: grey; " +
                    "-fx-text-fill: green; -fx-font-size: 12;" +
                    "-fx-font-family: monospace;" +
                    "-fx-alignment: center;" +
                    "-fx-border-width: 1");



            Label messageLabel = new Label("Level Won!");
            messageLabel.setStyle("-fx-text-fill: lightgreen; " +
                    "-fx-font-size: 12; -fx-font-family: monospace;");

            Button nextLevelButton = new Button("Next Level");
            Button exitButton = new Button("Exit Game");

            nextLevelButton.setOnAction(e -> loadNextLevel());
            exitButton.setOnAction(e -> exitGame());
            exitButton.setStyle("-fx-font-family: monospace");

            levelCompleteMenu.getChildren().addAll(messageLabel, nextLevelButton, exitButton);
        }

        levelCompleteMenu.setTranslateX((scene.getWidth() - 200) / 2);
        levelCompleteMenu.setTranslateY((scene.getHeight() - 200) / 2);
        grid.getChildren().add(levelCompleteMenu);
    }

    private void loadNextLevel() {
        System.out.println("Loading next level!");
        // load next level code here..
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
