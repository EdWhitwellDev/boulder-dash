package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

public class Exit extends Floor {

    //example diamonds needed
    private static final int DIAMONDS_NEEDED = 3;

    public Exit(int row, int col) {
        super(row, col, false);
        image = new Image("exit.png");
    }

    public boolean canExit(int diamondsCollected) {
        return diamondsCollected >= DIAMONDS_NEEDED;
    }
}
