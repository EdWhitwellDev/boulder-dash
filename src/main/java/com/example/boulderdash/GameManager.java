package com.example.boulderdash;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Falling.FallingObject;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.util.List;

public class GameManager extends Application {
    private Timeline tickTimeline;
    private Level level = new Level();
    private Player player;
    private Scene scene;
    private GridPane grid = new GridPane();
    private boolean dead = false;

    @Override
    public void start(Stage primaryStage) {

        player = level.getPlayer();

        GameState.setupSate(level, player, this);


        grid.setHgap(0);  // horizontal gap between cells
        grid.setVgap(0);
        grid.setPadding(new Insets(50));

        scene = new Scene(grid, 1500, 1000);  // width: 400, height: 400
        scene.setOnKeyPressed(this::processKeyEvent);
        scene.setOnKeyReleased(event -> player.setDirection(Direction.STATIONARY));

        tickTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> tick()));
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

    public void drawGame(){
        grid.getChildren().clear(); // Clears the grid first

        List<List<Tile>> tiles = level.getTiles();
        int rows = level.getRows();
        int columns = level.getCols();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                // Create an ImageView for the image
                Tile tile = tiles.get(row).get(col);
                StackPane stackPane = new StackPane();
                ImageView imageView = new ImageView(tile.getImage());

                // Optionally, resize the image to fit the grid cells
                imageView.setFitWidth(100);  // Resize width
                imageView.setFitHeight(100); // Resize height

                stackPane.getChildren().add(imageView);

                if (tile.isOccupied()) {
                    ImageView actorImageView = new ImageView(tile.getOccupier().getImage());
                    actorImageView.setFitHeight(80);
                    actorImageView.setFitWidth(80);
                    stackPane.getChildren().add(actorImageView);
                }
                // Add the ImageView to the grid at the specified row and column
                grid.add(stackPane, col, row);
            }
        }
    }

    public void processKeyEvent(KeyEvent event) {
        // We change the behaviour depending on the actual key that was pressed.
        switch (event.getCode()) {
            case RIGHT:
                // Right key was pressed. So move the player right by one cell.
                player.setDirection(Direction.RIGHT);
                break;
            case LEFT:
                // Right key was pressed. So move the player right by one cell.
                player.setDirection(Direction.LEFT);
                break;
            case UP:
                // Right key was pressed. So move the player right by one cell.
                player.setDirection(Direction.UP);
                break;
            case DOWN:
                // Right key was pressed. So move the player right by one cell.
                player.setDirection(Direction.DOWN);
                break;
            default:
                // Do nothing for all other keys.
                player.setDirection(Direction.STATIONARY);
                break;
        }
        // Consume the event. This means we mark it as dealt with. This stops other GUI nodes (buttons etc) responding to it.
        event.consume();
    }

    public void tick() {
        for (Actor actor : level.getActors()) {
            if (actor instanceof FallingObject) {
                FallingObject fallingObject = (FallingObject) actor;
                fallingObject.fall();
            }
        }
        drawGame();
        if (!dead) {
            for (Actor actor: level.getActors())
            {
                actor.move();
            }
        }

    }

    public Timeline getTickTimeline(){
        return tickTimeline;
    }

    public void looseGame(){
        dead = true;
    }

    public void winGame(){
        // do something
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}
