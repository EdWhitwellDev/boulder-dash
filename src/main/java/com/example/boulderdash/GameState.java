package com.example.boulderdash;

import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.enums.Direction;
import javafx.application.Application;
import javafx.stage.Stage;

public class GameState extends Application {
    public static GameManager manager = new GameManager();
    public static Level level;
    public static Player player;

    public static void main (String[] args){
        manager.run(args);
    }

    public static void setupSate(Level currentLevel, Player currentPlayer){
        level = currentLevel;
        player = currentPlayer;
    }
    @Override
    public void start(Stage stage) throws Exception {}
}
