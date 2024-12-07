package com.example.boulderdash;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Player;

import java.util.List;

/**
 * Maintains the current state of the game, including the current active level,
 * player, and game manager.
 */
public class GameState {

    // Responsible for controlling the game loop and rendering
    public static GameManager manager;

    // Current level being played
    public static Level level;

    // Player character in the game
    public static Player player;

    public static void setActors(List<Actor> actorList) {
        actors = actorList;
    }
    public static List<Actor> getActors() {
        return actors;
    }
    private static List<Actor> actors; // Global list of actors

    /**
     * Sets up the game state with the current level, player, and game manager
     * This method sets the static references to ensure shared access
     * across different parts of the program
     *
     * @param currentLevel - current level being played
     * @param currentPlayer  - player character for this session
     * @param management - game manager controlling the session
     */
    public static void setupSate(Level currentLevel, Player currentPlayer, GameManager management) {
        level = currentLevel;
        player = currentPlayer;
        manager = management;
    }
}
