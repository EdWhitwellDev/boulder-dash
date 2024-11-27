package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

public class Key extends Floor{
    public Key(int row, int col){
        super(row, col, true);
        image = new Image("red_key.png");
    }
}
