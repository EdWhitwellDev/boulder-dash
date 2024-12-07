package com.example.boulderdash;

import com.example.boulderdash.Actors.Actor;

import javafx.geometry.Pos;
import javafx.scene.control.*;


import com.example.boulderdash.enums.KeyColours;
import javafx.animation.*;
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
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    private Scene homeScene;
    private Pane levelCompleteMenu;
    private Pane gameOverMenu;
    private VBox pauseMenu;
    private String currentUser;
    private Stage primaryStage;
    private VBox homeScreen;
    private Label titleLabel;
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
    private static final ImageView diamondCountIcon = new ImageView(new Image("Actor Images/diamond.png"));
    private static final ImageView clockIcon = new ImageView(new Image("clock.png"));
    private static final ImageView keyIconBlue = new ImageView(new Image("Key Icon Images/blue_key_icon.png"));
    private static final ImageView keyIconRed = new ImageView(new Image("Key Icon Images/red_key_icon.png"));
    private static final ImageView keyIconGreen = new ImageView(new Image("Key Icon Images/green_key_icon.png"));
    private static final ImageView keyIconYellow = new ImageView(new Image("Key Icon Images/yellow_key_icon.png"));
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
        currentLevel = userProfileObj.get("CurrentLevel") != null ?
                Integer.parseInt(userProfileObj.get("CurrentLevel").toString()) : 1;
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
    public void drawGame(){
        calcTileSize();

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

            double widthGrid = tileSize * columns;
            double heightGrid = tileSize * rows;
            double widthStage = primaryStage.getWidth();
            double heightStage = primaryStage.getHeight();

            double x = (widthStage - widthGrid) / 2;
            double y = (heightStage - heightGrid) / 2;

            actorImageView.setTranslateX(previousPosition.getColumn() * tileSize + x); // Set the initial X position
            actorImageView.setTranslateY(previousPosition.getRow() * tileSize + y); // Set the initial Y position
            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(tickTime), actorImageView);
            translateTransition.setFromX(previousPosition.getColumn() * tileSize + x);
            translateTransition.setFromY(previousPosition.getRow() * tileSize + y);
            translateTransition.setToX(currentPosition.getColumn() * tileSize + x);
            translateTransition.setToY(currentPosition.getRow() * tileSize + y);

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

    /**
     * Ends the current level
     */
    public void winGame() {
        dead = true;
        drawGame();
        showLevelCompleteScreen();
    }

    public void saveScore(int score) {
        JSONObject highScoresObj = (JSONObject) userProfileObj.get("HighScores");
        JSONArray completedLevels = (JSONArray) userProfileObj.get("CompletedLevels");

        // check if the level has been completed before
        if (!completedLevels.contains(currentLevel)){
            completedLevels.add(currentLevel);
        }

        // get the scores for the current level
        List<Long> scores = (List<Long>) highScoresObj.get(String.valueOf("Level"+ currentLevel));
        if (scores == null){
            scores = new ArrayList<>();
        }
        scores.add((long) score);

        // sort the scores in descending order
        scores.sort(Collections.reverseOrder());
        // keep only the top 10 scores
        if (scores.size() > 10){
            scores = scores.subList(0, 10);
        }



        highScoresObj.put(String.valueOf("Level"+ currentLevel), scores);

        // update the high scores in the player profile
        try {
            JSONParser parser = new JSONParser();
            JSONObject PlayerProfileObj = (JSONObject) parser.parse(new FileReader("PlayerProfile.json"));
            JSONObject userObjOld = (JSONObject) PlayerProfileObj.get(currentUser);
            FileWriter file = new FileWriter("PlayerProfile.json");
            userObjOld.put("HighScores", highScoresObj);
            userObjOld.put("CompletedLevels", completedLevels);
            file.write(PlayerProfileObj.toJSONString());
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
     * Shows the remaining time.
     * @return the remaining time in seconds.
     */
    public int timeRemaining(){
        return (int)(level.getTimeLimit() - timeElapsed);
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
        homeScreen = new VBox(20);
        homeScreen.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #222;");

        // Add a logo
        ImageView logo = new ImageView(new Image(getClass().getResource("/logo.png").toExternalForm()));
        logo.setFitHeight(200);
        logo.setFitWidth(400);

        // Add a title label
        titleLabel = new Label("PRESS START TO PLAY");
        titleLabel.setFont(new Font("Arial", 40));
        titleLabel.setStyle("-fx-text-fill: white;");

        currentUserLabel = new Label("Current User: " + currentUser);
        currentUserLabel.setFont(new Font("Arial", 20));
        currentUserLabel.setStyle("-fx-text-fill: white;");

        HBox buttonBox = new HBox(20);
        buttonBox.setStyle("-fx-alignment: center;");

        // Start Game button
        Button startButton = new Button("Start");
        startButton.setFont(new Font("Arial", 20));
        startButton.setOnAction(e -> startNewGame());

        // Load Game button
        Button loadButton = new Button("Load Game");
        loadButton.setFont(new Font("Arial", 20));
        loadButton.setOnAction(e -> showSavedGamesScreen());

        // User Menu button
        Button userMenuButton = new Button("User Menu");
        userMenuButton.setFont(new Font("Arial", 20));
        userMenuButton.setOnAction(e -> userMenu());

        // High Score Table
        Label highScoreLabel = new Label("Level " + currentLevel + " Highest Scores:");
        highScoreLabel.setFont(new Font("Arial", 25));
        highScoreLabel.setStyle("-fx-text-fill: white;");

        VBox highScoreBoard = createHighScoreBoard();

        buttonBox.getChildren().addAll(startButton, loadButton, userMenuButton);

        homeScreen.getChildren().addAll(logo, titleLabel, currentUserLabel, buttonBox, highScoreLabel, highScoreBoard);
        homeScene = new Scene(homeScreen);
    }

    /**
     * Displays the User Menu screen, allowing users to add, remove, or select users.
     */
    private void userMenu() {
        VBox userMenuScreen = new VBox(20);
        userMenuScreen.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #222;");

        Label userMenuLabel = new Label("User Menu");
        userMenuLabel.setFont(new Font("Arial", 30));
        userMenuLabel.setStyle("-fx-text-fill: white;");

        // ListView to display users
        ListView<String> userList = new ListView<>();
        JSONArray users = (JSONArray) playerProfileObj.get("Users");
        if (users != null) {
            users.forEach(user -> userList.getItems().add(user.toString()));
        }

        // Adjust ListView height based on the number of users
        int userCount = users != null ? users.size() : 0;
        userList.setPrefHeight(Math.min(userCount * 35, 300));
        userList.setMaxWidth(500);

        // Add User button
        Button addUserButton = new Button("Add User");
        addUserButton.setFont(new Font("Arial", 20));
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
                userList.setPrefHeight(Math.min(users.size() * 35, 300));
            }
        });

        // Remove User button
        Button removeUserButton = new Button("Remove User");
        removeUserButton.setFont(new Font("Arial", 20));
        removeUserButton.setOnAction(e -> {
            String selectedUser = userList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                if (selectedUser.equals(currentUser)) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Current user cannot be removed!");
                    errorAlert.setContentText("Please switch to a different user and try again.");
                    errorAlert.showAndWait();
                } else {
                    userList.getItems().remove(selectedUser);
                    users.remove(selectedUser);
                    playerProfileObj.remove(selectedUser);
                    savePlayerProfile();

                    // Update ListView height after removing a user
                    userList.setPrefHeight(Math.min(users.size() * 35, 300));
                }
            }
        });

        // Select User button
        Button selectUserButton = new Button("Select User");
        selectUserButton.setFont(new Font("Arial", 20));
        selectUserButton.setOnAction(e -> {
            String selectedUser = userList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                changeCurrentUser(selectedUser);
                primaryStage.setScene(homeScene);
            }
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setFont(new Font("Arial", 20));
        backButton.setOnAction(e -> primaryStage.setScene(homeScene));

        HBox buttonBox = new HBox(10, addUserButton, removeUserButton, selectUserButton, backButton);
        buttonBox.setStyle("-fx-alignment: center;");

        userMenuScreen.getChildren().addAll(userMenuLabel, userList, buttonBox);

        Scene userMenuScene = new Scene(userMenuScreen);
        primaryStage.setScene(userMenuScene);
    }

    private void changeCurrentUser(String newUser) {
        currentUser = newUser;
        userProfileObj = (JSONObject) playerProfileObj.get(currentUser);
        currentUserLabel.setText("Current User: " + currentUser);

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
     * Displays the saved games to the user, allowing them to choose between the save files.
     */
    private void showSavedGamesScreen() {
        VBox savedGamesScreen = new VBox(20);
        savedGamesScreen.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #222;");

        Label savedGamesLabel = new Label("Select a Saved Game");
        savedGamesLabel.setFont(new Font("Arial", 30));
        savedGamesLabel.setStyle("-fx-text-fill: white;");

        List<String> savedGames = loadSavedGames();

        if (savedGames.isEmpty()) {
            Label noSavesLabel = new Label("No saved games found!");
            noSavesLabel.setFont(new Font("Arial", 18));
            noSavesLabel.setStyle("-fx-text-fill: grey;");
            savedGamesScreen.getChildren().add(noSavesLabel);
            Button backButton = new Button("Back");
            backButton.setFont(new Font("Arial", 20));
            backButton.setOnAction(e -> primaryStage.setScene(homeScene));
            savedGamesScreen.getChildren().add(backButton);
            Scene savedGamesScene = new Scene(savedGamesScreen);
            primaryStage.setScene(savedGamesScene);
            return;
        }

        // ListView to display saved games
        ListView<String> savedGamesList = new ListView<>();
        savedGamesList.setPrefSize(400, 300);
        savedGamesList.getItems().addAll(savedGames); // loadSavedGames() returns a list of saved games

        // Load selected game button
        Button loadSelectedButton = new Button("Load Selected Game");
        loadSelectedButton.setFont(new Font("Arial", 20));
        loadSelectedButton.setOnAction(e -> {
            String selectedGame = savedGamesList.getSelectionModel().getSelectedItem();
            if (selectedGame != null) {
                System.out.println("Loading selected game: " + selectedGame);
                loadSelectedGame(selectedGame);
            }
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setFont(new Font("Arial", 20));
        backButton.setOnAction(e -> primaryStage.setScene(homeScene));

        savedGamesScreen.getChildren().addAll(savedGamesLabel, savedGamesList, loadSelectedButton, backButton);

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
    private void UIsetUp() {
        grid.setHgap(0);
        grid.setVgap(0);

        diamondCountIcon.setFitHeight(tileSize*0.5);
        diamondCountIcon.setFitWidth(tileSize*0.5);
        clockIcon.setFitHeight(tileSize*0.5);
        clockIcon.setFitWidth(tileSize*0.4);
        keyIconBlue.setFitHeight(tileSize*0.5);
        keyIconBlue.setFitWidth(tileSize*0.5);
        keyIconRed.setFitHeight(tileSize*0.5);
        keyIconRed.setFitWidth(tileSize*0.5);
        keyIconGreen.setFitHeight(tileSize*0.5);
        keyIconGreen.setFitWidth(tileSize*0.5);
        keyIconYellow.setFitHeight(tileSize*0.5);
        keyIconYellow.setFitWidth(tileSize*0.5);
        infoBar.getChildren().addAll(clockIcon, timeLabel, diamondCountIcon,
                diamondsLabel, keyIconBlue, keyLabelBlue,
                keyIconRed, keyLabelRed, keyIconGreen, keyLabelGreen,
                keyIconYellow, keyLabelYellow);
        infoBar.setPrefHeight(tileSize*0.7);
        infoBar.setAlignment(javafx.geometry.Pos.CENTER);
        infoBar.setStyle("-fx-padding: 5; -fx-background-color: #333; -fx-text-fill: white;");
        timeLabel.setStyle("-fx-text-fill: white;");
        diamondsLabel.setStyle("-fx-text-fill: white;");
        keyLabelBlue.setStyle("-fx-text-fill: white;");
        keyLabelRed.setStyle("-fx-text-fill: white;");
        keyLabelGreen.setStyle("-fx-text-fill: white;");
        keyLabelYellow.setStyle("-fx-text-fill: white;");
        diamondCountIcon.setStyle("-fx-padding: 10;");

        stackPane.setStyle("-fx-background-color: #333;");

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(infoBar);

        stackPane.getChildren().addAll(grid, transitionPane, borderPane);

        scene = new Scene(stackPane);  // width: 400, height: 400
        scene.setOnKeyPressed(this::processKeyEvent);
        scene.setOnKeyReleased(event -> player.setDirection(Direction.STATIONARY));

        tickTimeline = new Timeline(new KeyFrame(Duration.seconds(tickTime), event -> tick()));
        tickTimeline.setCycleCount(Animation.INDEFINITE);
    }

    private void getHighScores() {
        highScores = new HashMap<>();
        JSONArray users = (JSONArray) playerProfileObj.get("Users");
        // get the high scores for the current level from all users
        for (Object user : users) {
            JSONObject userObj = (JSONObject) playerProfileObj.get(user);
            JSONObject highScoresObj = (JSONObject) userObj.get("HighScores");
            List<Long> scores = (List<Long>) highScoresObj.get(String.valueOf("Level"+ currentLevel));
            System.out.println(scores);
            if (scores != null){
                for (long scoreLong : scores){
                    Integer score = (int) (long) scoreLong;
                    if (highScores.containsKey(score)){
                        highScores.put(score, highScores.get(score) + ", " + user.toString());
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
                .forEachOrdered(x -> sortedHighScores.put(x.getKey(), x.getValue()));
        highScores = sortedHighScores;
    }
    /**
     * Creates the high score board.
     * @return a VBox of the high score board.
     */
    private VBox createHighScoreBoard() {
        getHighScores();

        VBox highScoreBoard = new VBox(10); // Vertical spacing between scores
        highScoreBoard.setStyle("-fx-padding: 10; -fx-alignment: center;");
        highScoreBoard.setAlignment(Pos.CENTER);

        if (highScores.isEmpty()) {
            Label noScoresLabel = new Label("No score yet!");
            noScoresLabel.setFont(new Font("Arial", 18));
            noScoresLabel.setStyle("-fx-text-fill: grey;");
            highScoreBoard.getChildren().add(noScoresLabel);
        } else {
            List<Integer> highScoresInt = new ArrayList<>(this.highScores.keySet());
            List<String> byUser = new ArrayList<>(this.highScores.values());
            for (int i = 0; i < Math.min(highScoresInt.size(), 10); i++) {
                Label scoreLabel = new Label(highScoresInt.get(i).toString());
                Label userLabel = new Label((i + 1) + ". " + byUser.get(i) + "  - ");
                scoreLabel.setFont(new Font("Arial", 18));
                scoreLabel.setStyle("-fx-text-fill: white;");
                userLabel.setFont(new Font("Arial", 18));
                userLabel.setStyle("-fx-text-fill: white;");

                HBox scoreBox = new HBox(10);
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
        currentLevel = userProfileObj.get("CurrentLevel") != null ?
                Integer.parseInt(userProfileObj.get("CurrentLevel").toString()) : 1;

        level = new Level(currentLevel);
        player = level.getPlayer();
        timeElapsed = 0;
        calcTileSize();

        UIsetUp();

        GameState.setupSate(level, player, this);
        tickTimeline.play();

        drawGame();

        primaryStage.setScene(scene);
        // center the scene on the screen

    }

    /**
     * Loads the player's profile from a JSON file.
     */
    private void getPlayerProfile(){
        JSONParser parser = new JSONParser();
        try {
            playerProfileObj = (JSONObject) parser.parse(new FileReader("PlayerProfile.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * Toggles the state of the game, either paused or unpaused.
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
        pauseMenu = new VBox(5);
        pauseMenu.setStyle("-fx-padding: 20;");
        pauseMenu.setMaxSize(250,200);
        pauseMenu.setAlignment(javafx.geometry.Pos.CENTER);

        double buttonWidth = 150;
        String buttonStyle =  "-fx-border-color: white darkgrey darkgrey white;" +
                "-fx-border-width: 4; -fx-text-fill: black; " +
                "-fx-font-family: monospace; -fx-font-size: 12; " +
                "-fx-cursor: hand;";

        Button resumeButton = new Button("Resume");
        Button saveButton = new Button("Save Game");
        Button loadButton = new Button("Load Game");
        Button exitButton = new Button("Exit Game");

        resumeButton.setStyle(buttonStyle);
        saveButton.setStyle(buttonStyle);
        loadButton.setStyle(buttonStyle);
        exitButton.setStyle(buttonStyle);

        resumeButton.setPrefWidth(buttonWidth);
        saveButton.setPrefWidth(buttonWidth);
        loadButton.setPrefWidth(buttonWidth);
        exitButton.setPrefWidth(buttonWidth);

        resumeButton.setOnAction(e -> togglePause());
        saveButton.setOnAction(e -> saveGame());
        exitButton.setOnAction(e -> exitGame());

        pauseMenu.setStyle("-fx-background-color: rgba(51, 51, 51, 0.9);" +
                "-fx-padding: 20;");

        pauseMenu.getChildren().addAll(resumeButton, saveButton, loadButton, exitButton);

    }

    /**
     * Saves the current game state
     */
    private void saveGame() {
        // textbox for save name input
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Save Game");
        dialog.setHeaderText("Save Your Progress");
        dialog.setContentText("Enter a name for your save:");

        // set the background color of the dialog and the header to match the game
        dialog.getDialogPane().setStyle("-fx-background-color: #333;");
        dialog.getDialogPane().lookup(".content .label").setStyle("-fx-text-fill: white;");
        dialog.getDialogPane().lookup(".header-panel").setStyle("-fx-background-color: #333;");
        dialog.getDialogPane().lookup(".header-panel .label").setStyle("-fx-text-fill: white;");
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
     * Loads a previously saved game state
     */
    private void loadGame(String gameName) {
        if (!Objects.equals(gameName, "")) {
            level = new Level(currentUser, gameName);
            player = level.getPlayer();
            timeElapsed = 0;
            calcTileSize();

            UIsetUp();

            GameState.setupSate(level, player, this);
            tickTimeline.play();

            drawGame();
            primaryStage.setScene(scene);
            primaryStage.setHeight(level.getRows() * tileSize);
            primaryStage.setWidth(level.getCols() * tileSize);
        }
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
     * Shows the game over screen to the user and provides options to restart or exit application.
     */
    private void showGameOverScreen() {
        drawGame();
        if (gameOverMenu == null) {
            gameOverMenu = new StackPane();
            gameOverMenu.setStyle("-fx-background-color: rgba(51, 51, 51, 0.9);");
            gameOverMenu.setPrefSize(scene.getWidth(), scene.getHeight());

            VBox gameOverBox = new VBox(20);
            double buttonWidth = 150;
            gameOverBox.setAlignment(Pos.CENTER);

            Label messageLabel = new Label("Game Over!");
            messageLabel.setStyle("-fx-text-fill: red;" +
                    " -fx-font-size: 48; " +
                    "-fx-font-family: monospace;");

            VBox scoreBoard = createHighScoreBoard();
            scoreBoard.setStyle("-fx-padding: 20;");


            Button exitButton = new Button("Exit Game");
            exitButton.setStyle("-fx-background-color: grey; " +
                    "-fx-border-color: white darkgrey darkgrey white; " +
                    "-fx-border-width: 4; -fx-text-fill: white; " +
                    "-fx-font-family: monospace; -fx-font-size: 16;" +
                    "-fx-cursor: hand;");

            Button restartButton = new Button("Restart Game");
            restartButton.setStyle("-fx-background-color: grey; " +
                    "-fx-border-color: white darkgrey darkgrey white; " +
                    "-fx-border-width: 4; -fx-text-fill: white; " +
                    "-fx-font-family: monospace; -fx-font-size: 16;" +
                    "-fx-cursor: hand;");

            restartButton.setPrefWidth(buttonWidth);
            exitButton.setPrefWidth(buttonWidth);

            exitButton.setOnAction(e -> exitGame());
            restartButton.setOnAction(e -> restartGame());

            gameOverBox.getChildren().addAll(messageLabel, scoreBoard, restartButton, exitButton);
            gameOverBox.setLayoutX(scene.getWidth() / 2 - gameOverBox.getPrefWidth() / 2);
            gameOverBox.setLayoutY(scene.getHeight() / 2 - gameOverBox.getPrefHeight() / 2);

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
        timeElapsed = 0;
        level = new Level(currentLevel);
        player = level.getPlayer();  // maintain current level / player prof

        GameState.setupSate(level, player, this);


        drawGame();
        tickTimeline.play();

    }

    /**
     * Shows the level complete screen when the player completes a level.
     * Stops the game loop to display the screen, shows the high score board, and provides option for moving
     * onto the next level or exit application.
     */
    private void showLevelCompleteScreen() {
        tickTimeline.stop();
        int score = player.getDiamondsCollected() * 10 + timeRemaining()*2;
        if (levelCompleteMenu == null) {
            levelCompleteMenu = new StackPane();
            levelCompleteMenu.setStyle("-fx-background-color: rgba(51, 51, 51, 0.9);");
            levelCompleteMenu.setPrefSize(scene.getWidth(), scene.getHeight());

            VBox levelCompleteBox = new VBox(20);
            levelCompleteBox.setAlignment(Pos.CENTER);

            Label messageLabel = new Label("Level Complete!");
            messageLabel.setStyle("-fx-text-fill: lightgreen; -fx-font-size: 48;" +
                    " -fx-font-family: monospace;");

            Label scoreLabel = new Label("You Scored: " + score);
            scoreLabel.setStyle("-fx-text-fill: lightgreen; -fx-font-size: 48;" +
                    " -fx-font-family: monospace;");

            saveScore(score);

            VBox scoreBoard = createHighScoreBoard();
            scoreBoard.setStyle("-fx-padding: 20;");

            Button nextLevelButton = new Button("Next Level");
            nextLevelButton.setStyle("-fx-background-color: grey; " +
                    "-fx-border-color: white darkgrey darkgrey white; " +
                    "-fx-border-width: 4; -fx-text-fill: white;" +
                    " -fx-font-family: monospace; -fx-font-size: 16;" +
                    "-fx-cursor: hand;");

            Button exitButton = new Button("Exit Game");
            exitButton.setStyle("-fx-background-color: grey; " +
                    "-fx-border-color: white darkgrey darkgrey white; " +
                    "-fx-border-width: 4; -fx-text-fill: white;" +
                    "-fx-font-family: monospace; -fx-font-size: 16;" +
                    "-fx-cursor: hand;");

            nextLevelButton.setOnAction(e -> loadNextLevel());
            exitButton.setOnAction(e -> exitGame());

            levelCompleteBox.getChildren().addAll(messageLabel, scoreLabel, scoreBoard, nextLevelButton, exitButton);

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
        timeElapsed = 0;
        GameState.setupSate(level, player, this);
    }
    private void updateCurrentLevel(){
        userProfileObj.put("CurrentLevel", currentLevel);
        try {
            JSONParser parser = new JSONParser();
            JSONObject PlayerProfileObj = (JSONObject) parser.parse(new FileReader("PlayerProfile.json"));
            FileWriter file = new FileWriter("PlayerProfile.json");
            PlayerProfileObj.put(currentUser, userProfileObj);
            file.write(PlayerProfileObj.toJSONString());
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}