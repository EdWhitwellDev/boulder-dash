package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

public class Boulder extends FallingObject{

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

    public void transform() {
        GameState.manager.killActor(this);
        GameState.manager.addActor(new Diamond(position));
    }

    public void move() {
        super.fall();
    }

    private boolean isAbleToPushTo(Tile tile) {
        return tile != null && tile.isPath() && !tile.isOccupied();
    }

}
