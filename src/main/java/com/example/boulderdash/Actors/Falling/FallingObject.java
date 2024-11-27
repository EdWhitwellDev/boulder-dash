package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Tiles.Tile;

public class FallingObject extends Actor {
    protected boolean isFalling = false;

    public FallingObject(Tile startPosition) {
        super(startPosition);
    }

//    public void fall(Tile[][] grid) {
//        Tile underTile = position.getDown();
//
//        if (isAbleToFall(underTile)) {
//            setPosition(underTile);
//            isFalling = true;
//        } else {
//            isFalling = false;
//        }
//    }
//
//    public void explosion() {
//        isFalling = false;
//        // Logic for explosion goes here
//    }
//
//    public boolean getIsFalling() {
//        return isFalling;
//    }
//
//    private boolean isAbleToFall(Tile underTile) {
//        return underTile != null && underTile.getCanBeOccupied(); // Checks if the tile under is a tile and is empty
//    }
}
