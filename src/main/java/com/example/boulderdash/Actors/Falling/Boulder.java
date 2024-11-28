package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

public class Boulder extends FallingObject{
    private boolean isPushed = false;

    public Boulder(Tile startPosition) {
        super(startPosition);
        image = new Image("boulder.png");
    }
    public void pushed(Direction direction, Tile[][] grid) {
        Tile nextTile = null; // Need a method to get any tile !!!

        if (nextTile != null && nextTile.isOccupied()) {
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
