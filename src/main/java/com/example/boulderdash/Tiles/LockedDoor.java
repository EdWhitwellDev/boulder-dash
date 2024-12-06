package com.example.boulderdash.Tiles;

import com.example.boulderdash.enums.KeyColours;
import javafx.scene.image.Image;

import java.util.Map;

/**
 * Class to represent a Locked Door Tile. This tile allows the player to unlock the door, if
 * they have a key with a matching color, and transform it into a Path Tile.
 *
 * @author Ed Whitwell
 */
public class LockedDoor extends Floor{

    //A Map of the KeyColours (Enum) and their corresponding door image.
    //Colours: [Red, Blue, Green, Yellow]
    private static final Map<KeyColours, Image> colours = Map.of(
            KeyColours.RED, new Image("Tile Images/Locked Doors/red_door.png"),
            KeyColours.BLUE, new Image("Tile Images/Locked Doors/blue_door.png"),
            KeyColours.GREEN, new Image("Tile Images/Locked Doors/green_door.png"),
            KeyColours.YELLOW, new Image("Tile Images/Locked Doors/yellow_door.png")
    );

    //The Door's colour
    private final KeyColours colour;

    /**
     * This is the constructor for the Locked Door Tile. It sets the tile's row, column and
     * sets the isPath variable to a default value 'false'. It sets the Door's colour to
     * the Color inputted, and sets the tile's image according to it's colour.
     *
     * @param row An integer representing the Grid Row that the tile is in.
     * @param col An integer representing the Grid Column that the tile is in.
     * @param doorColour The Colour of the Door
     * */
    public LockedDoor(int row, int col, KeyColours doorColour){
        super(row, col, false);
        image = colours.get(doorColour);
        colour = doorColour;
    }

    /**
     * The accessor method for the Door's colour.
     *
     * @return The Door's colour (The colour of the key required to unlock it).
     * */
    public KeyColours getColour(){
        return colour;
    }
}
