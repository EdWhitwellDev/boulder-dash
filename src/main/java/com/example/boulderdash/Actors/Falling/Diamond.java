package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import javafx.scene.image.Image;
import com.example.boulderdash.Tiles.MagicWall;

/**
 * Represents an instance of a Diamond.
 * Diamonds are falling objects that can be collected by the player.
 * @author Viraj Shah
 * @version 1.2
 */
public class Diamond extends FallingObject {

    /**
     * Boolean to check whether this diamond has been collected during a certain
     * tick.
     * */
    private boolean hasBeenCollectedThisTick = false;
    /**
     * Constructor for a Diamond at a specific starting tile.
     * @param startPosition is the initial {@link Tile} position of the diamond.
     */
    public Diamond(final Tile startPosition) {
        super(startPosition);
        setImage(new Image("Actor Images/diamond.png"));
    }

    /**
     * Indicates whether an item has been collected during the current tick.
     * @param ifHasBeenCollectedThisTick {@code True} if the item has
     *                                            been collected this tick.
     */
    public void setHasBeenCollectedThisTick(
            final boolean ifHasBeenCollectedThisTick) {

        this.hasBeenCollectedThisTick = ifHasBeenCollectedThisTick;
    }

    /**
     * Transforms and removes the diamond into a {@link Boulder} when
     * interacting with a {@link MagicWall}.
     */
    public void transform() {
        GameState.getManager().killActor(this);
        GameState.getManager().addActor(new Boulder(getPosition()));
    }

    /**
     * Handles the falling and rolling of the diamond.
     */
    public void move() {
        if (hasBeenCollectedThisTick) {
            return;
        }
        super.fall();
    }
    /**
     * This is a method to represent a Diamond object in the desired
     * string format.
     *
     * @return A string in the format :
     *             D,v1,v2 (where v1 = RowNumber and v2 = ColumnNumber)
     * */
    public String toString() {
        return "D" + ","
                + getPosition().getRow() + ","
                + getPosition().getColumn();
    }
}
