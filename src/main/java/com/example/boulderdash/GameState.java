package com.example.boulderdash;

import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.enums.Direction;
import javafx.application.Application;
import javafx.stage.Stage;

public class GameState{
    public static GameManager manager;

    public static Level level;
    public static Player player;

    //public static void main (String[] args){
    //    manager.run(args);
    //}

    public static void setupSate(Level currentLevel, Player currentPlayer, GameManager management){
        level = currentLevel;
        player = currentPlayer;
        manager = management;
    }

}