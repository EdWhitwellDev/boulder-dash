package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

/**
 * Class to represent a Magic Wall Tile, a subclass of Tile. This tile blocks
 * all Actor (Player and Enemy) movement, converts boulders to diamonds and can
 * be destroyed by explosions.
 *
 * @author Ed Whitwell
 */

public class MagicWall extends Tile {

    /**
     * This is the constructor for a Magic Wall Tile. It sets the tile's row and
     * column, and sets the isPath variable has a default value 'false'. It also
     * sets the Magic Wall Tile's image.
     *
     * @param row An integer representing the Grid Row that the tile object is
     *            in.
     * @param col An integer representing the Grid Column that the tile object
     *            is in.
     */
    public MagicWall(final int row, final int col) {
        super(row, col, false);
        setImage(new Image("Tile Images/magic_wall.png"));
    }

    /**
     * Returns representation of the tile.
     * This is set to "M".
     *
     * @return The string "M".
     */
    public String toString() {
        return "M";
    }
}
