package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Enemies.Enemy;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.MagicWall;
import com.example.boulderdash.Tiles.Tile;

public abstract class FallingObject extends Actor {

    protected boolean isFalling = false;
    private int fallDelay = 0;
    private final int fallDelayReset = 2;
    private int rollDelay = 0;
    private final int rollDelayReset = 3;
    protected boolean exploded = false;

    public FallingObject(Tile startPosition) {
        super(startPosition);
    }

    public void fall() {
        if (exploded) {
            GameState.manager.killActor(this);
        }
        else {
            if (fallDelay > 0) {
                fallDelay--;
                return;
            }
            fallDelay = fallDelayReset;
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
    private void roll() {
        if (rollDelay > 0) {
            rollDelay--;
            return;
        }
        rollDelay = rollDelayReset;
        Tile leftTile = position.getLeft();
        Tile rightTile = position.getRight();

        if (isAbleToRollTo(leftTile)) {
            setPosition(leftTile);
        } else if (isAbleToRollTo(rightTile)) {
            setPosition(rightTile);
        }
    }
    private boolean isAbleToRollTo(Tile tile) {
        return tile != null && tile.isPath() && !tile.isOccupied() && tile.getDown() != null
                && tile.getDown().isPath() && !tile.getDown().isOccupied();
    }

    public abstract void transform();

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

    public void explode() {
        exploded = true;

    }

    public boolean isFalling() {
        return isFalling;
    }

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
                    GameState.manager.looseGame();
                    return true;
                }
            }
        }
        return false; // Checks if the tile under is a tile and is empty
    }

    private void onPath(Tile underTile) {

    }
}
