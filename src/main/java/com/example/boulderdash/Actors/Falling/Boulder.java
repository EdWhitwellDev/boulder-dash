package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Tiles.Tile;

public class Boulder extends FallingObject{
    private boolean isPushed = false;

    public Boulder(Tile startPosition) {
        super(startPosition);
    }
    public void pushed(Direction direction, Tile[][] grid) {
        Tile nextTile = null; // Need a method to get any tile !!!

        if (nextTile != null && nextTile.getCanBeOccupied()) {
            setPosition(nextTile);
            isPushed = true;
        }

    }

    public Diamond transformToDiamond() {
        return new Diamond(position);
    }

    public void fall(Tile[][] grid) {
        super.fall(grid);
    }
}
