package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.Tiles.MagicWall;
import javafx.scene.image.Image;

/**
 * Represents an instance of a Diamond.
 * Diamonds are falling objects that can be collected by the player.
 * @author Viraj Shah
 * @version 1.2
 */

public class Diamond extends FallingObject {

    /**
     * Constructor for a Diamond at a specific starting tile.
     * @param startPosition is the initial {@link Tile} position of the diamond.
     */
    public Diamond(Tile startPosition) {
        super(startPosition);
        image = new Image("diamond.png");
    }

    /**
     * Transforms and removes the diamond into a {@link Boulder} when interacting with a {@link MagicWall}
     */
    @Override
    public void transform() {
        GameState.manager.killActor(this);
        GameState.manager.addActor(new Boulder(position));
    }

    /**
     * Collects and removes the diamond when a player interacts with it.
     * @param player is the {@link Player} collecting the diamond.
     */
    public void collect(Player player) {
        player.collectedDiamond(); // Called from Player class (needs to be implemented)
        position.setOccupier(null); // removes the diamond (needs to be implemented)
    }

    /**
     * Handles the falling and rolling of the diamond.
     */
    public void move() {
        super.fall();
        if (!isFalling) {
            roll();
        }
    }
}