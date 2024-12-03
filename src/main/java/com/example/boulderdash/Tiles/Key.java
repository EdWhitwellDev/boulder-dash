package com.example.boulderdash.Tiles;
import com.example.boulderdash.enums.KeyColour;
import javafx.scene.image.Image;

public class Key extends Floor {
    private final KeyColour colour;

    public Key(int row, int col, KeyColour colour) {
        super(row, col, true);
        this.colour = colour;
        this.isKey = true;
        image = new Image(colour.name().toLowerCase() + "_key.png");
    }

    public KeyColour getColour() {
        return colour;
    }
}