package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import com.example.boulderdash.Tiles.MagicWall;
import javafx.scene.image.Image;

/**
 * Represents an instance of a Boulder.
 * Boulders are falling objects that can block or kill the player or enemies.
 * @author Viraj Shah
 * @version 1.3
 */
public class Boulder extends FallingObject{

    /**
     * Constructor for a boulder at a specific tile.
     * @param startPosition is the initial {@link Tile} position of the boulder.
     */
    public Boulder(Tile startPosition) {
        super(startPosition);
        image = new Image("Actor Images/boulder.png");
    }

    /**
     * Pushes the boulder in the specified direction.
     * @param direction the {@link Direction} to push the boulder to.
     * @return {@code True} if the boulder has been pushed.
     */
    public boolean push(Direction direction) {
        Tile nextTile = position.getNeighbour(direction); // Gets a direction to push to

        // Checks if the next tile is a path and sets the position of boulder to that path
        if (isAbleToPushTo(nextTile)) {
            setPosition(nextTile);
            return true;
        }
        return false;
    }

    /**
     * Transforms and removes the boulder into a {@link Diamond} when interacting with a {@link MagicWall}
     */
    public void transform() {
        GameState.manager.killActor(this);
        GameState.manager.addActor(new Diamond(position));
    }

    /**
     * Handles the falling and rolling of the boulder.
     */
    public void move() {
        super.fall();
    }

    /**
     * Handles the pushing behaviour.
     * @param tile is the {@link Tile} to check if it can be occupied.
     * @return {@code True} if the boulder can be pushed to the tile.
     */
    private boolean isAbleToPushTo(Tile tile) {
        return tile != null && tile.isPath() && !tile.isOccupied();
    }

    public String toString(){
        return "B" + "," + position.getRow() + "," + position.getColumn();
    }
}
