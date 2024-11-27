package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Tiles.Tile;

public class Diamond extends FallingObject {
    public Diamond(Tile startPosition) {
        super(startPosition);
    }

    public Boulder transformToBoulder() {
        return new Boulder(position);
    }

    public void collect(Player player) {
        player.collectedDiamond(); // Called from Player class (needs to be implemented)
        position.setOccupant(null); // removes the diamond (needs to be implemented)

    }

    public void fall(Tile[][] grid) {
        super.fall(grid);
    }
}
