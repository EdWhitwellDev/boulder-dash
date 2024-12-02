package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

public class Exit extends Floor {
    //example diamonds needed
    private static final int DIAMONDS_NEEDED = 10;

    public Exit(int row, int col) {
        super(row, col, false);
        image = new Image("exit.png");
    }
}
