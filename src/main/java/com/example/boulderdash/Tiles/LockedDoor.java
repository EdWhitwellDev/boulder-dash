package com.example.boulderdash.Tiles;
import com.example.boulderdash.Actors.Falling.Boulder;
import com.example.boulderdash.Actors.Falling.Diamond;
import javafx.scene.image.Image;

public class LockedDoor extends Tile{
    private final String colour;
    public LockedDoor(int row, int col, String colour){
        super(row, col, false);
        this.colour = colour;
        this.isLockedDoor = true;
        image = new Image(colour.toLowerCase() + "_door.png");
    }
}
