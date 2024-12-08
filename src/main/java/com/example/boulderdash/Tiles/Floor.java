package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Player;
import javafx.scene.image.Image;

/**
 * Class to represent a Floor Tile. This class can define either a Path
 * or Dirt tile, depending on the value of the isPath variable present
 * in the Tile Class.
 *
 * @author Ed Whitwell
 */
public class Floor extends Tile {

    /**
     * This is the constructor for the Floor Tile (either Dirt or Path).
     * It sets the tile's row and column, then determines the tile's
     * Image using the isPath value.
     *
     * @param row An integer representing the Grid Row that the tile is
     *            in.
     * @param col An integer representing the Grid Column that the tile
     *           is in.
     * @param isPath The boolean value representing whether a tile is a
     *              Path tile.
     */
    public Floor(final int row, final int col, final boolean isPath) {
        //Passing values into the Superclass(Tile) Constructor
        super(row, col, isPath);
        this.isPath = isPath;

        //Sets the Tile's image based on whether it is
        image = isPath ? new Image("Tile Images/path.png")
                : new Image("Tile Images/dirt.png");
    }

    /**
     * This method overrides the mutator method already present in the
     * Superclass. It sets the tile's occupant and converts a Dirt
     * tile to a Path tile when the player occupies the tile.
     *
     * @param occupant The Actor being occupying in the tile.
     */
    @Override
    public void setOccupier(final Actor occupant) {
        super.setOccupier(occupant);

        // Unlock the tile if it is a LockedDoor.
        if (this instanceof LockedDoor) {
            ((LockedDoor) this).unLock();
        }

        // Transform Dirt to Path if occupied by a Player.
        if (!isPath && occupant instanceof Player) {
            isPath = true;
            image = new Image("Tile Images/path.png");
        }

    }

    /**
     * The accessor method for whether the tile is a Path or not.
     * If it is true, then the tile is a Path Tile, if it is false
     * it is a Dirt Tile.
     *
     * @return Whether the tile is a Path tile or not.
     */
    public boolean getIsPath() {
        return isPath;
    }

    /**
     * Returns a string of the tile.
     * If the tile is a path, it returns "P". Otherwise, it returns "D".
     *
     * @return "P" if the tile is a path, otherwise "D".
     */
    public String toString() {
        return isPath ? "P" : "D";
    }

}
