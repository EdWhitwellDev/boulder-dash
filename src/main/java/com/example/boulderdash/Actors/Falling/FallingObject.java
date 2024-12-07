package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Enemies.Enemy;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.MagicWall;
import com.example.boulderdash.Tiles.Tile;

/**
 * Handles the actions of any falling objects.
 * @author Viraj Shah
 * @version 1.2
 */
public abstract class FallingObject extends Actor {

    protected boolean exploded = false;
    protected boolean isFalling = false;
    private int fallDelay = 0;
    private int rollDelay = 0;

    /**
     * Constructor for a FallingObject at a specific starting tile.
     * @param startPosition is the initial {@link Tile} position of the falling object.
     */
    public FallingObject(Tile startPosition) {
        super(startPosition);
    }

    /**
     * Defines the object's falling behaviour.
     * If the object can fall, the position is updated to the tile below until it can no longer fall.
     */
    public void fall() {
        if (exploded) {
            GameState.manager.killActor(this);
        }
        else {
            if (fallDelay > 0) {
                fallDelay--;
                return;
            }
            fallDelay = 2;
            Tile underTile = position.getDown();
            if (isAbleToFall(underTile)) {
                setPosition(underTile);
                isFalling = true;
            } else {
                isFalling = false;
                onPath(underTile);
            }
            if (!isFalling) {
                roll();
            }
        }

    }

    /**
     * Defines how a falling objects transforms when interacting with a {@link MagicWall}.
     * Overridden by subclasses.
     */
    public abstract void transform();

    /**
     * Updates the new position of an object.
     * @param newTile is the new {@link Tile} to move to.
     */
    public void setPosition(Tile newTile) {
        if (position != null) {
            position.setOccupier(null);
        }
        position = newTile;
        if (newTile != null) {
            newTile.setOccupier(this);
            if (newTile instanceof MagicWall) {
                transform();
            }
        }
    }

    /**
     * Enables an object to explode and removes it from the level.
     */
    public void explode() {
        exploded = true;
    }

    /**
     * Defines the object's rolling behaviour.
     * If an object can roll, it's position is updated either left or right.
     */
    private void roll() {
        if (rollDelay > 0) {
            rollDelay--;
            return;
        }
        rollDelay = 3;
        Tile leftTile = position.getLeft();
        Tile rightTile = position.getRight();

        if (isAbleToRollTo(leftTile)) {
            setPosition(leftTile);
        } else if (isAbleToRollTo(rightTile)) {
            setPosition(rightTile);
        }
    }

    /**
     * Handles the rolling behaviour
     * @param tile is the {@link Tile} to check if it can be occupied.
     * @return {@code True} if the object can roll to the tile.
     */
    private boolean isAbleToRollTo(Tile tile) {
        return tile != null && tile.isPath() && !tile.isOccupied() && tile.getDown() != null
                && tile.getDown().isPath() && !tile.getDown().isOccupied();
    }

    /**
     * Handles the falling behaviour.
     * @param underTile is the {@link Tile} below the specific object.
     * @return {@code True} if the object can fall
     */
    private boolean isAbleToFall(Tile underTile) {
        if (underTile == null) {
            return false;
        }
        if (underTile.isPath() || underTile instanceof MagicWall){
            Actor occupant = underTile.getOccupier();
            if (occupant == null) {
                return true;
            }
            if (isFalling){
                if (occupant instanceof Enemy) {
                    ((Enemy) occupant).crush();
                    explode();
                    return true;
                }
                if (occupant instanceof Player) {
                    GameState.manager.looseGame("Crushed by " + this.getClass().getSimpleName());
                    return true;
                }
            }
        }
        return false; // Checks if the tile under is a tile and is empty
    }

    private void onPath(Tile underTile) {

    }
}
