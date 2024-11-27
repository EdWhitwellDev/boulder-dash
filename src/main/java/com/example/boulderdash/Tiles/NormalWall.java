package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

public class NormalWall extends Tile{
    public NormalWall(int row, int col){
        super(row, col);
        image = new Image("normal_wall.png");
    }
}
