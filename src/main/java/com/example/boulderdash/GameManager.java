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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class GameManager extends Application {

    private VBox gameOverMenu;
    private List<Actor> deadActors = new ArrayList<>();
    private List<Actor> newBorns = new ArrayList<>();
    private Timeline tickTimeline;
    private Level level;
    private Player player;
    private Scene scene;
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

    @Override
    public void start(Stage primaryStage) {
        level = new Level();
        player = level.getPlayer();
        timeElapsed = 0;


        GameState.setupSate(level, player, this);

        grid.setHgap(0);  // horizontal gap between cells
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

    public void drawGame(){
        timeLabel.setText((int)(level.getTimeLimit() - timeElapsed) + "s");
        diamondsLabel.setText(player.getDiamondsCollected() + "/" + level.getDiamondsRequired());
        keyLabelBlue.setText("x" + String.valueOf(player.getKeys().get(KeyColours.BLUE)));
        keyLabelRed.setText("x" + String.valueOf(player.getKeys().get(KeyColours.RED)));
        keyLabelGreen.setText("x" + String.valueOf(player.getKeys().get(KeyColours.GREEN)));
        keyLabelYellow.setText("x" + String.valueOf(player.getKeys().get(KeyColours.YELLOW)));

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
            // centres the pause menu ( total dimensions / 2 )
            pauseMenu.setTranslateX((scene.getWidth() - 200) / 2);
            pauseMenu.setTranslateY((scene.getHeight() - 200) / 2);
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
        pauseMenu = new VBox(15);
        // background color of the pause menu
        pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10;");

        Button resumeButton = new Button("Resume");
        Button saveButton = new Button("Save Game");
        Button loadButton = new Button("Load Game");
        Button exitButton = new Button("Exit Game");

        resumeButton.setOnAction(e -> togglePause());
        saveButton.setOnAction(e -> saveGame());
        loadButton.setOnAction(e -> loadGame());
        exitButton.setOnAction(e -> exitGame());

        pauseMenu.getChildren().addAll(resumeButton, saveButton, loadButton, exitButton);
        pauseMenu.setTranslateX(scene.getWidth()/ 2 );
        pauseMenu.setTranslateY(scene.getHeight()/ 2 );

        GridPane.setColumnSpan(pauseMenu, 1);
        GridPane.setRowSpan(pauseMenu, 2);
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
        timeElapsed += tickTime;
        removeActors();
        createNewActors();
        drawGame();

        if (!dead) {
            for (Actor actor: level.getActors())
            {
                actor.move();
            }
        }
        if (timeElapsed > level.getTimeLimit()){
            looseGame();
        }
    }

    public void looseGame(){
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



    public void winGame(){
        dead = true;
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}