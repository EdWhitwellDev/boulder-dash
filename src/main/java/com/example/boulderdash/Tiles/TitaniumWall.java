package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

    /**
     * Class to represent a Titanium Wall Tile, a subclass of Tile. This tile blocks all
     * Actor (Player and Enemy) movement and cannot be destroyed by an explosion.
     *
     * @author Ed Whitwell
     */
public class TitaniumWall extends Tile{

    /**
     * This is the constructor for a Titanium Wall Tile. It sets the tile's row and column
     * and sets the isPath variable has a default value 'false'. It also sets the Titanium Wall
     * Tile's image.
     *
     * @param row An integer representing the Grid Row that the tile is in.
     * @param col An integer representing the Grid Column that the tile is in.
     */
    public TitaniumWall(int row, int col){
        super(row, col, false);
        image = new Image("titanium_wall.png");
    }
}
