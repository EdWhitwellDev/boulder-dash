package com.example.boulderdash;

import com.example.boulderdash.Actors.Actor;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class GameManager extends Application {

    private List<Actor> deadActors = new ArrayList<>();
    private List<Actor> newBorns = new ArrayList<>();
    private Timeline tickTimeline;
    private final Level level = new Level();
    private Player player;
    private Scene scene;
    private final GridPane grid = new GridPane();
    private final Pane transitionPane = new Pane();
    private boolean dead = false;
    private boolean isPaused = false;

    @Override
    public void start(Stage primaryStage) {

        player = level.getPlayer();


        GameState.setupSate(level, player, this);

        grid.setHgap(0);  // horizontal gap between cells
        grid.setVgap(0);
        grid.setPadding(javafx.geometry.Insets.EMPTY);




        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(grid);
        stackPane.getChildren().add(transitionPane);
        stackPane.setAlignment(Pos.CENTER);
        stackPane.setPadding(javafx.geometry.Insets.EMPTY);



        int rows = level.getRows();
        int columns = level.getCols();

        scene = new Scene(stackPane, columns*100, rows*100);  // width: 400, height: 400

        scene.setOnKeyPressed(this::processKeyEvent);
        scene.setOnKeyReleased(event -> player.setDirection(Direction.STATIONARY));

        tickTimeline = new Timeline(new KeyFrame(Duration.millis(150), event -> tick()));
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

    public void killActor(Actor actor){
        deadActors.add(actor);
    }
    public void addActor(Actor actor) {
        newBorns.add(actor);
    }

    private void removeActors(){
        for (Actor actor : deadActors){
            level.removeActor(actor);
        }
        deadActors = new ArrayList<>();
    }
    private void createNewActors(){
        level.addActors(newBorns);
        newBorns = new ArrayList<>();
    }

    private PlayerProfile playerProfile = new PlayerProfile("Player1", 1, 0);

    private void openPlayerProfile(Stage stage) {
        PlayerProfileScreen profileScreen = new PlayerProfileScreen(playerProfile);
        profileScreen.show(stage);
    }

    public void drawGame(){
        grid.getChildren().clear(); // Clears the grid first

        List<List<Tile>> tiles = level.getTiles();
        int rows = level.getRows();
        int columns = level.getCols();

        Map<ImageView, Actor> actorsToAnimate= new HashMap<>();


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
                // Add the ImageView to the grid at the specified row and column
                grid.add(stackPane, col, row);

            }
        }
        transitionPane.getChildren().clear();
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

            translateTransition.play();
            transitionPane.getChildren().add(actorImageView);
            transitionPane.setPadding(javafx.geometry.Insets.EMPTY);

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
            case ESCAPE:
                // Escape key was pressed, so the game will be paused.
                togglePause();
                break;
            case P:
                openPlayerProfile((Stage) grid.getScene().getWindow());
                break;
                default:
                // Do nothing for all other keys.
                player.setDirection(Direction.STATIONARY);
                break;
        }
        // Consume the event. This means we mark it as dealt with. This stops other GUI nodes (buttons etc.) responding to it.
        event.consume();
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            tickTimeline.pause();  // pause game loop
            showPauseMenu();

        } else {
            tickTimeline.play();   // resume game loop
            hidePauseMenu();
        }
    }
    private void showPauseMenu() {
        if (pauseMenu == null) {
            createPauseMenu();
        }
        if (!grid.getChildren().contains(pauseMenu)) {
            // Dynamically calculate the position to center the menu
            pauseMenu.setTranslateX((scene.getWidth() - pauseMenu.getWidth()) / 2);
            pauseMenu.setTranslateY((scene.getHeight() - pauseMenu.getHeight()) / 2);
            grid.getChildren().add(pauseMenu);

        }
    }



    private void hidePauseMenu() {
        if (pauseMenu != null) {
            grid.getChildren().remove(pauseMenu);
        }
    }

    private VBox pauseMenu;

    private void createPauseMenu() {
        pauseMenu = new VBox(3);
        // background color of the pause menu
        pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10;");

        Button resumeButton = new Button("Resume");
        Button profileButton = new Button("Player Profile");
        Button saveButton = new Button("Save Game");
        Button loadButton = new Button("Load Game");
        Button exitButton = new Button("Exit Game");


        resumeButton.setOnAction(e -> togglePause());
        saveButton.setOnAction(e -> saveGame());
        loadButton.setOnAction(e -> loadGame());
        profileButton.setOnAction(e -> openPlayerProfile((Stage) grid.getScene().getWindow()));
        exitButton.setOnAction(e -> exitGame());

        //centres buttons to pauseMenu Vbox
        pauseMenu.getChildren().addAll(resumeButton, saveButton, loadButton, profileButton, exitButton);
        pauseMenu.setTranslateX(scene.getWidth()/ 2 );
        pauseMenu.setTranslateY(scene.getHeight()/ 2 );

        // this changes background pane size of pauseMenu
        GridPane.setColumnSpan(pauseMenu, 2);
        GridPane.setRowSpan(pauseMenu, 1);
        GridPane.setHalignment(pauseMenu, javafx.geometry.HPos.CENTER);
        GridPane.setValignment(pauseMenu, javafx.geometry.VPos.CENTER);

    }

    private void saveGame() {
        // saveGame code ...
        System.out.println("Game saved!");
    }

    private void loadGame() {
        // loadGame code ...
        System.out.println("Game loaded!");
    }

    private void exitGame() {
        // save before exit
        saveGame();
        Stage stage = (Stage) grid.getScene().getWindow();
        stage.close();
    }

    public void tick() {
        removeActors();
        createNewActors();
        drawGame();

        if (!dead) {
            for (Actor actor: level.getActors())
            {
                actor.move();
            }
        }

    }

    public void looseGame(){
        dead = true;
    }

    public void winGame(){
        dead = true;
    }


    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}