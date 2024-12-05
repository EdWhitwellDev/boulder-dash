package com.example.boulderdash.Actors.Falling;


import com.example.boulderdash.Actors.Player;
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
    public void transform() {
        GameState.manager.killActor(this);
        GameState.manager.addActor(new Boulder(position));
    }

    /**
     * Handles the falling and rolling of the diamond.
     */
    public void move() {
        super.fall();
    }
}