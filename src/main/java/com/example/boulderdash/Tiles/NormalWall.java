package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

/**
 * Class to represent the Normal Wall Tile, a subclass of Tile. This tile
 * blocks all Actor(Player and Enemy) movement and can be destroyed by an
 * explosion.
 *
 * @author Ed Whitwell
 */
public class NormalWall extends Tile {

    /**
     * This is the constructor for a Normal Wall Tile. It sets the tile's
     * row and column and sets the isPath variable has a default value 'false'.
     * It also sets the Normal Wall Tile's image.
     *
     * @param row An integer representing the Grid Row that the tile is in.
     * @param col An integer representing the Grid Column that the tile is in.
     */
    public NormalWall(final int row, final int col) {
        super(row, col, false);
        setImage(new Image("Tile Images/normal_wall.png"));
    }

    /**
     * Returns a string of the tile.
     * This is set to "N".
     *
     * @return The string "N".
     */
    public String toString() {
        return "N";
    }
}
