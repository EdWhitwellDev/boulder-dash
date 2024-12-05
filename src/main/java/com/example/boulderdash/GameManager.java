package com.example.boulderdash;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Falling.FallingObject;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Main controller for the game. This class handles game initialisation,
 * user input, game state management, and the main game loop.
 */
public class GameManager extends Application {

    private List<Actor> deadActors = new ArrayList<>(); // Stores actors marked for removal
    private Timeline tickTimeline; // Controls the game loop
    private Level level = new Level(); // Represents the current level
    private Player player; // Reference to the player object
    private Scene scene; // Main game scene
    private GridPane grid = new GridPane(); // Grid layout for rendering tiles and actors
    private boolean dead = false; // Checks if the player is dead
    private boolean isPaused = false; // Checks if the game is paused
    private VBox pauseMenu; // Pause UI

    @Override
    public void start(Stage primaryStage) {

        // Initialise player and level state
        player = level.getPlayer();
        GameState.setupSate(level, player, this);

        // Create grid layout for the game board
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(new Insets(50));

        // Create the main game scene
        scene = new Scene(grid, 1500, 1000);
        scene.setOnKeyPressed(this::processKeyEvent); // Handles key presses
        scene.setOnKeyReleased(event -> player.setDirection(Direction.STATIONARY));

        tickTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> tick()));
        tickTimeline.setCycleCount(Animation.INDEFINITE);
        tickTimeline.play();

        primaryStage.setTitle("Boulder Dash");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Marks an actor for removal from the game
     */
    public void killActor(Actor actor) {
        deadActors.add(actor);
    }

    /**
     * Removes all actors that have been marked for removal in the current game tick
     */
    private void removeActors() {
        for (Actor actor : deadActors) {
            level.removeActor(actor);
        }
        deadActors.clear();
    }

    /**
     * Draws the current state of the game on the grid
     */
    public void drawGame() {
        grid.getChildren().clear(); // Clear the grid to redraw it

        // Retrieve the level's tiles and dimensions
        List<List<Tile>> tiles = level.getTiles();
        int rows = level.getRows();
        int columns = level.getCols();

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
                    ImageView actorImageView = new ImageView(tile.getOccupier().getImage());
                    actorImageView.setFitHeight(80);
                    actorImageView.setFitWidth(80);
                    stackPane.getChildren().add(actorImageView);
                }

                // Place the visual representation in the grid
                grid.add(stackPane, col, row);
            }
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
        removeActors(); // Remove any dead actors
        drawGame(); // Redraw the grid

        if (!dead) {
            for (Actor actor : level.getActors()) {
                actor.move(); // Move all active actors
            }
        }
    }

    /**
     * Ends the game, marked it as a loss
     */
    public void loseGame() {
        Text gameOverText = new Text("Game Over");
        gameOverText.setFont(new Font("Arial", 75));
        dead = true;
    }

    /**
     * Ends the current level
     */
    public void winGame() {
        Text gameOverText = new Text("Level Complete");
        gameOverText.setFont(new Font("Arial", 75));
    }

    /**
     * Main method to launch the program
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
