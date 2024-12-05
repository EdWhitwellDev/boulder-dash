package com.example.boulderdash.Tiles;

import com.example.boulderdash.enums.KeyColours;
import javafx.scene.image.Image;

import java.util.Map;

public class LockedDoor extends Floor{
    private static final Map<KeyColours, Image> colours = Map.of(
            KeyColours.RED, new Image("red_door.png"),
            KeyColours.BLUE, new Image("blue_door.png"),
            KeyColours.GREEN, new Image("green_door.png"),
            KeyColours.YELLOW, new Image("yellow_door.png")
    );

    private final KeyColours colour;
    public LockedDoor(int row, int col, KeyColours doorColour){
        super(row, col, false);
        image = colours.get(doorColour);
        colour = doorColour;
    }

    public KeyColours getColour(){
        return colour;
    }
}
