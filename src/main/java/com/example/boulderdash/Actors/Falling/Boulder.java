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

    public boolean push(Direction direction) {
        Tile nextTile = position.getNeighbour(direction); // Gets a direction to push to

        // Checks if the next tile is a path and sets the position of boulder to that path
        if (isAbleToPushTo(nextTile)) {
            setPosition(nextTile);
            return true;
        }
        return false;
    }

    public Diamond transformToDiamond() {
        return new Diamond(position);
    }

    public void fall(Tile[][] grid) {
        super.fall(grid);
        if (!isFalling) {
            roll();
        }
    }

    private void roll() {
        Tile leftTile = position.getLeft();
        Tile rightTile = position.getRight();

        if (isAbleToRollTo(leftTile)) {
            setPosition(leftTile);
        } else if (isAbleToRollTo(rightTile)) {
            setPosition(rightTile);
        }
    }

    // Helper for push()
    private boolean isAbleToPushTo(Tile tile) {
        return tile != null && tile.isPath() && !tile.isOccupied();
    }

    // Helper for roll()
    private boolean isAbleToRollTo(Tile tile) {
        return tile != null && tile.isPath() && !tile.isOccupied() && tile.getDown() != null
                && tile.getDown().isPath() && !tile.getDown().isOccupied();
    }
}
