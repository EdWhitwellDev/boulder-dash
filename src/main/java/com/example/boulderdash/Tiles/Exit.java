package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

public class Exit extends Floor{
    public Exit(int row, int col){
        super(row, col, false);
        image = new Image("exit.png");
    }
}
