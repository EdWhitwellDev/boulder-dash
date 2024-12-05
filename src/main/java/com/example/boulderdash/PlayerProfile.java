package com.example.boulderdash;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class PlayerProfile {
    private String name;
    private int level;
    private int highScore;

    public PlayerProfile(String name, int level, int highScore) {
        this.name = name;
        this.level = level;
        this.highScore = highScore;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }


    public class PlayerProfileScreen {

        private static PlayerProfile playerProfile;

        public PlayerProfileScreen(PlayerProfile playerProfile) {
            this.playerProfile = playerProfile;
        }

        public void show(Stage stage) {
            Label nameLabel = new Label("Player Name:");
            TextField nameField = new TextField(playerProfile.getName());

            Label levelLabel = new Label("Level: " + playerProfile.getLevel());
            Label highScoreLabel = new Label("High Score: " + playerProfile.getHighScore());

            Button saveButton = new Button("Save");
            saveButton.setOnAction(event -> {
                playerProfile.setName(nameField.getText());
                // Save profile changes to file or database (if applicable)
                System.out.println("Player Profile Updated!");
            });

            Button backButton = new Button("Back");
            backButton.setOnAction(event -> {
                // Go back to the main game screen
                GameManager mainGame = new GameManager();
                try {
                    mainGame.start(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            VBox layout = new VBox(10, nameLabel, nameField, levelLabel, highScoreLabel, saveButton, backButton);
            layout.setAlignment(Pos.CENTER);

            Scene profileScene = new Scene(layout, 400, 300);
            stage.setScene(profileScene);
            stage.setTitle("Player Profile");
        }
    }

}
