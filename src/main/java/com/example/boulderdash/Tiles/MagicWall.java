package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

public class MagicWall extends Tile{
    public MagicWall(int row, int col){
        super(row, col);
        image = new Image("magic_wall.png");
    }
}
