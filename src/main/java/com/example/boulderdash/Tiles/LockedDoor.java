package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

public class LockedDoor extends Tile{
    public LockedDoor(int row, int col){
        super(row, col, false)
;        image = new Image("red_door.png");
    }
}
