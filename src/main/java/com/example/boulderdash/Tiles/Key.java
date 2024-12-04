package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.enums.Direction;
import com.example.boulderdash.enums.KeyColours;
import javafx.scene.image.Image;

import java.util.Map;

public class Key extends Floor{

    private static Map<KeyColours, Image> colours = Map.of(
            KeyColours.RED, new Image("red_key.png"),
            KeyColours.BLUE, new Image("blue_key.png"),
            KeyColours.GREEN, new Image("green_key.png"),
            KeyColours.YELLOW, new Image("yellow_key.png")
    );

    private final KeyColours colour;
    private boolean collected;
    public Key(int row, int col, KeyColours keyColour){
        super(row, col, false);
        image = colours.get(keyColour);
        colour = keyColour;
        collected = false;
    }

    @Override
    public void setOccupier(Actor occupant) {
        super.setOccupier(occupant);
        if (occupant instanceof Player && !collected){
            ((Player) occupant).collectKey(colour);
            collected = true;
        }
    }
}
