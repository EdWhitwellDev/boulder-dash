package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

public class Key extends Floor{
    private final String colour;
    
    public Key(int row, int col, String colour){
        super(row, col, true);
        this.colour = colour;
        this.isKey = true;
        image = new Image(colour.toLowerCase() + "_key.png");
    }
    public String getColor() {
        return colour;
    }
}
