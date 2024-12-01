package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Tiles.Tile;

public class FallingObject extends Actor {

    protected boolean isFalling = false;
    private int fallDelay = 0;
    private final int fallDelayReset = 8;
    private int rollDelay = 0;
    private final int rollDelayReset = 3;

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

    public void explosion() {
        isFalling = false;
        // Logic for explosion goes here
    }

    public boolean isFalling() {
        return isFalling;
    }

    private boolean isAbleToFall(Tile underTile) {
        return underTile != null && !underTile.isOccupied() && underTile.isPath(); // Checks if the tile under is a tile and is empty
    }

    private void onPath(Tile underTile) {

    }
}
