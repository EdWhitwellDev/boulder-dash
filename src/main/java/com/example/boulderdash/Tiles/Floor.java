package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Player;
import javafx.scene.image.Image;

/**
 * Class to represent a Floor Tile. This class can define either a Path or Dirt tile,
 * depending on the value of the isPath variable present in the Tile Class.
 *
 * @author Ed Whitwell
 */
public class Floor extends Tile{

    /**
     * This is the constructor for the Floor Tile (either Dirt or Path). It sets the tile's
     * row and column, then determines the tile's Image using the isPath value.
     *
     * @param row An integer representing the Grid Row that the tile is in.
     * @param col An integer representing the Grid Column that the tile is in.
     * @param isPath The boolean value representing whether a tile is a Path tile.
     */
    public Floor(int row, int col, boolean isPath) {
        //Passing values into the Superclass(Tile) Constructor
        super(row, col, isPath);
        this.isPath = isPath;

        //Sets the Tile's image based on whether it is
        // dirt (isPath==false) or path (isPath==true)
        image = isPath ? new Image("path.png") : new Image("dirt.png");
    }

    /**
     * This method overrides the mutator method already present in the Superclass.
     * It sets the tile's occupant and converts a Dirt tile to a Path tile when the player
     * occupies the tile.
     *
     * @param occupant The Actor being occupying in the tile.
     * */
    @Override
    public void setOccupier(Actor occupant) {
        //Sets the tile's occupant to the incoming occupant
        super.setOccupier(occupant);

        //This changes the isPath value and the tile's image when a Dirt tile (isPath==false)
        // is occupied by the Player.
        if (!isPath && occupant instanceof Player){
            isPath = true;
            image = new Image("path.png");
        }

    }

    /**
     * The accessor method for whether the tile is a Path or not. If it is true, then the tile
     * is a Path Tile, if it is false it is a Dirt Tile.
     *
     * @return Whether the tile is a Path tile or not.
     * */
    public boolean getIsPath(){
        return isPath;
    }
}
