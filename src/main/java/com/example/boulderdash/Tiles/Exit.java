package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.GameState;
import javafx.scene.image.Image;

/**
 * This is the class to represent an Exit Tile. This tile only allows the player
 * to exit the level/game if they've obtained the required number of diamonds
 * for the exit tile, in the stated time limit.
 *
 * @author Ed Whitwell
 */
public class Exit extends Floor{

    /**
     * This is the sole constructor of this class. It sets the tile's row, column and the
     * isPath variable is given a default value 'false'. It also sets the Exit Tile's image.
     *
     * @param row An integer representing the Grid Row that the tile is in.
     * @param col An integer representing the Grid Column that the tile  is in.
     */
    public Exit(int row, int col){
        super(row, col, false);
        image = new Image("Tile Images/Exit.png");
    }

    /**
     * This method allows the */
    @Override
    public void setOccupier(Actor occupant) {
        super.setOccupier(occupant);
        GameState.manager.winGame();
    }
    public String toString(){
        return "E";
    }
}
