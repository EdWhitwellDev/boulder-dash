package com.example.boulderdash;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PlayerProfileScreen {

    private final PlayerProfile playerProfile;

    public PlayerProfileScreen(PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
    }



    public void show(Stage stage) {
        // VBox layout for the profile screen
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        // Create UI components
        Label nameLabel = new Label("Player Name:");
        TextField nameField = new TextField(playerProfile.getName());

        Label levelLabel = new Label("Level: " + playerProfile.getLevel());
        Label scoreLabel = new Label("High Score: " + playerProfile.getHighScore());

        Button saveButton = new Button("Save");
        Button backButton = new Button("Back");

        // Event for saving player profile
        saveButton.setOnAction(e -> {
            playerProfile.setName(nameField.getText());
            System.out.println("Profile updated: " + playerProfile.getName());
        });

        // Event for going back to the game
        backButton.setOnAction(e -> {
            GameManager gameManager = new GameManager();
            gameManager.start(stage);
        });

        // Add components to layout
        layout.getChildren().addAll(nameLabel, nameField, levelLabel, scoreLabel, saveButton, backButton);

        // Create and show the scene
        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Player Profile");
        stage.show();
    }
}
