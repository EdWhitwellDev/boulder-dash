package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Audio;
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
public class Boulder extends FallingObject {

    /**
     * Constructor for a boulder at a specific tile.
     * @param startPosition is the initial {@link Tile} position of the boulder.
     */
    public Boulder(final Tile startPosition) {
        super(startPosition);
        setImage(new Image("Actor Images/boulder.png"));
    }

    /**
     * Pushes the boulder in the specified direction.
     * @param direction the {@link Direction} to push the boulder to.
     * @return {@code True} if the boulder has been pushed.
     */
    public boolean push(final Direction direction) {
        if (direction != Direction.LEFT && direction != Direction.RIGHT) {
            return false;
        }
        // Gets a direction to push to.
        Tile nextTile = getPosition().getNeighbour(direction);

        // Checks if the next tile is a path and sets the position of boulder
        // to that path.
        if (isAbleToPushTo(nextTile)) {
            setPosition(nextTile);
            Audio.getInstance().playSoundEffect(
                    "/Music/BoulderFall.mp3",
                    1.0);
            return true;
        }
        return false;
    }

    /**
     * Transforms and removes the boulder into a {@link Diamond} when
     * interacting with a {@link MagicWall}.
     */
    public void transform() {
        GameState.manager.killActor(this);
        GameState.manager.addActor(new Diamond(getPosition()));
    }

    /**
     * Handles the falling and rolling of the boulder.
     */
    public void move() {
        super.fall();
    }

    /**
     * Handles the pushing behaviour.
     *
     * @param tile is the {@link Tile} to check if it can be occupied.
     * @return {@code True} if the boulder can be pushed to the tile.
     */
    private boolean isAbleToPushTo(final Tile tile) {
        return tile != null && tile.isPath() && !tile.isOccupied();
    }

    /**
     * This is a method to represent a Boulder object in the desired
     * string format.
     *
     * @return A string in the format :
     *             B,v1,v2 (where v1 = RowNumber and v2 = ColumnNumber)
     * */
    public String toString() {
        return "B" + ","
                + getPosition().getRow() + ","
                + getPosition().getColumn();
    }
}
