package com.example.boulderdash;

import javafx.application.Application;
import javafx.stage.Stage;

public class GameState extends Application {
    public static GameManager manager = new GameManager();
    public static Level level = new Level();

    public static void main (String[] args){
        manager.run(args);
    }
    @Override
    public void start(Stage stage) throws Exception {}
}

