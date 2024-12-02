package com.example.boulderdash.Tiles;
import javafx.scene.image.Image;

public class LockedDoor extends Tile{
    private final String colour;
    public LockedDoor(int row, int col, String colour){
        super(row, col, false);
        this.colour = colour;
        this.isLockedDoor = true;
        image = new Image(colour.toLowerCase() + "_door.png");
    }
    public String getColour() {
        return colour;
    }

    public void unlock() {
        this.isLockedDoor = false;
        this.isPath = true;
        this.setImage(new Image("path.png"));
    }
}
