package com.example.boulderdash;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Player;

import java.util.List;

/**
 * Maintains the current state of the game, including the current active level,
 * player, and game manager.
 */
public class GameState {

    /**
     * Controls game loop and rendering.
     */
    private static GameManager manager;
    /**
     * Current level being played.
     */
    private static Level level;
    /**
     * Player character in the game.
     */
    private static Player player;
    /**
     * Global list of actors.
     */
    private static List<Actor> actors;

    /**
     * Sets up the game state with the current level, player, and game manager.
     * This method sets the static references to ensure shared access.
     * across different parts of the program.
     *
     * @param currentLevel - current level being played.
     * @param currentPlayer  - player character for this session.
     * @param management - game manager controlling the session.
     */
    public static void setupSate(final Level currentLevel,
                                 final Player currentPlayer,
                                 final GameManager management) {
        level = currentLevel;
        player = currentPlayer;
        manager = management;
    }

    /**
     * Retrieves the instance of the GameManager.
     * @return the GameManager instance.
     */
    public static GameManager getManager() {
        return manager;
    }

    /**
     * Retrieves the current level.
     * @return the current Level instance.
     */
    public static Level getLevel() {
        return level;
    }

    /**
     * Retrieves the current player.
     * @return the Player instance.
     */
    public static Player getPlayer() {
        return player;
    }

    /**
     * Sets the list of actors.
     * @param actorList is the list of Actor instances.
     */
    public static void setActors(final List<Actor> actorList) {
        actors = actorList;
    }

    /**
     * Retrieves the list of actors.
     * @return the list of Actor instances.
     */
    public static List<Actor> getActors() {
        return actors;
    }
}
