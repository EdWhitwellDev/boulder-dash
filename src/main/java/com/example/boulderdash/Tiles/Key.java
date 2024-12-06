package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.enums.KeyColours;
import javafx.scene.image.Image;

import java.util.Map;

/**
 * Class to represent a Key Tile. This tile allows the player to collect a key.
 * @author Ed Whitwell
 */
public class Key extends Floor{

    //A Map of the KeyColours (Enum) and their corresponding key image.
    //Colours: [Red, Blue, Green, Yellow]
    private static final Map<KeyColours, Image> colours = Map.of(
            KeyColours.RED, new Image("Tile Images/Keys (in-Tile)/red_key.png"),
            KeyColours.BLUE, new Image("Tile Images/Keys (in-Tile)/blue_key.png"),
            KeyColours.GREEN, new Image("Tile Images/Keys (in-Tile)/green_key.png"),
            KeyColours.YELLOW, new Image("Tile Images/Keys (in-Tile)/yellow_key.png")
    );

    //The Key's colour
    private final KeyColours colour;

    //Whether the key has been collected or not
    private boolean collected;

    /**
     * The constructor for the Key Tile. It sets the tile's row, column and sets the
     * isPath variable to a default value 'false'. It sets the Key's colour to the colour inputted,
     * the tile's image according to it's colour and for the key to be initially uncollected.
     *
     * @param row An integer representing the Grid Row that the tile is in.
     * @param col An integer representing the Grid Column that the tile is in.
     * @param keyColour The Colour of the Key
     * */
    public Key(int row, int col, KeyColours keyColour){
        super(row, col, false);
        image = colours.get(keyColour);
        colour = keyColour;
        collected = false;
    }

    /**
     * Sets this Key tile's occupant, and allows the Player to collect the key and update their key
     * count according the key's colour.
     * @param occupant The actor occupying the tile
     * */
    @Override
    public void setOccupier(Actor occupant) {
        //Sets the tile's occupant to the incoming occupant
        super.setOccupier(occupant);

        // Calls the collectKey method in the Player class to update the key count of this
        // key's colour. And updating the Key tile to show it's been collected
        if (occupant instanceof Player && !collected){
            ((Player) occupant).collectKey(colour);
            collected = true;
        }
    }
}
