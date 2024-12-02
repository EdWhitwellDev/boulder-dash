package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Enemies.Enemy;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.MagicWall;
import com.example.boulderdash.Tiles.Tile;

public class FallingObject extends Actor {

    protected boolean isFalling = false;
    private int fallDelay = 0;
    private final int fallDelayReset = 3;
    private int rollDelay = 0;
    private final int rollDelayReset = 3;
    protected boolean exploded = false;

    public FallingObject(Tile startPosition) {
        super(startPosition);
    }

    public void fall() {
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

    }

    public void explode() {
        exploded = true;
        GameState.manager.killActor(this);
        // Logic for explosion goes here
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
