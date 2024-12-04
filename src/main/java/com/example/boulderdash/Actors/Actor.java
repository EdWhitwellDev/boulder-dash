package com.example.boulderdash.Actors;

import com.example.boulderdash.Actors.Enemies.Enemy;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Handles the actions of any Actor. An actor being an entity that can move on the grid and interact with other actors.
 * @author Viraj Shah & Ed Whitwell
 * @version 1.3
 */

public class Actor {

    protected Tile position;
    protected int tickCoolDown;
    protected Direction currentDirection;
    protected Image image;

    /**
     * Constructor for an Actor at a specific starting tile.
     * @param startPosition is the initial {@link Tile} position of the Actor.
     */
    public Actor(Tile startPosition) {
        position = startPosition;
        if (position != null) {
            position.setOccupier(this);
        }
    }

    public Image getImage() {
        return image;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public Tile getPosition() {
        return position;
    }

    /**
     * Specifies how the actor moves on the grid.
     * Overridden by subclasses for specific movement.
     */
    public void move() {

    }

    /**
     * Changes the position of an Actor to a new tile and detects any collisions.
     * @param nextPos is the next {@link Tile} to move to.
     */
    protected void changePos(Tile nextPos) {
        if (position != null) {
            position.setOccupier(null);
        }

        position = nextPos;

        if (position != null) {
            position.setOccupier(this);
        }

        checkCollisions();
    }

    /**
     * Detects collisions with other actors in neighbouring tiles.
     */
    protected void checkCollisions() {
        List<Actor> collisionOther = position.checkAdjacent();
        if (!collisionOther.isEmpty()) {
            for (Actor collider : collisionOther) {
                if (collider instanceof Enemy && this instanceof Player) {
                    GameState.manager.looseGame();
                } else if (this instanceof Enemy && collider instanceof Player) {
                    GameState.manager.looseGame();
                }
            }
        }
    }
}
