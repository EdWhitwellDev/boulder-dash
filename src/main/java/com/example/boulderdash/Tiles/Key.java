package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

public class Key extends Floor{
     private Colour keyColour;

     enum Colour {
        RED, GREEN, YELLOW, BLUE
    }

    public Colour getKeyColour() {
        return keyColour;
    }
    
    public Key(int row, int col){
        super(row, col, true);
        image = new Image("red_key.png");
    }
}
