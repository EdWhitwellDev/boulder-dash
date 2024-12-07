package com.example.boulderdash.Actors;

import com.example.boulderdash.Actors.Falling.Boulder;
import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.Audio;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Exit;
import com.example.boulderdash.Tiles.Floor;
import com.example.boulderdash.Tiles.LockedDoor;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import com.example.boulderdash.enums.KeyColours;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent a Player, subclass of Actor. It inherits basic movement and
 * position handling from the Actor class, and it's movement is controlled by the keyboard.
 * */
public class Player extends Actor {

    //A Map of all Directions to their corresponding Images with the correct orientation
    private static final Map<Direction, Image> orientation = Map.of(
            Direction.STATIONARY, new Image("Actor Images/Player/player_down.png"),
            Direction.UP, new Image("Actor Images/Player/player_up.png"),
            Direction.DOWN, new Image("Actor Images/Player/player_down.png"),
            Direction.LEFT, new Image("Actor Images/Player/player_left.png"),
            Direction.RIGHT, new Image("Actor Images/Player/player_right.png")
    );

    //A Map holding the key count for each key colour
    private final Map<KeyColours, Integer> keys = new HashMap<>();
    private int tickCoolDown = 0;

    //The number of diamonds collected by the player
    private int diamondsCollected = 0;

    /**
     * Creates a new Player at the specified starting position. And sets the player to have no keys (of any
     * colour) and be stationary.
     *
     * @param startingTile The tile at which the Player will initially appear
     * */
    public Player(Tile startingTile){
        super(startingTile);
        image = orientation.get(currentDirection);
        keys.put(KeyColours.RED, 0);
        keys.put(KeyColours.BLUE, 0);
        keys.put(KeyColours.GREEN, 0);
        keys.put(KeyColours.YELLOW, 0);
        currentDirection = Direction.STATIONARY;
    }

    /**
     * This method sets the Player's direction and changes the Player's Image/Orientation to match
     * the direction it wants to move in.
     * @param direction The new direction of the Player
     * */
    public void setDirection(Direction direction){
        currentDirection = direction;
        if (currentDirection != Direction.STATIONARY) {
            image = orientation.get(currentDirection);
        }
    }

    /**
     * Movement execution for the player.
     */
    public void move(){
        if (tickCoolDown > 0){
            tickCoolDown--;
        }
        else {
            Tile nextTile = getNextTile(currentDirection);
            if (nextTile != null) {
                processMove(nextTile);
                Audio.getInstance().playSoundEffect("/Music/Move.mp3", 1.0);
            }
        }
    }

    /**
     * This method allows the player to collect a Key, and update it's key count.
     * */
    public void collectKey(KeyColours keyColour){
        keys.put(keyColour, keys.get(keyColour) + 1);
    }

    /**
     * The accessor method for the number of Diamonds the Player has collected.
     *
     * @return The number of Diamonds the Player has collected.
     * */
    public int getDiamondsCollected(){
        return diamondsCollected;
    }
    public void setDiamondsCollected(int diamondsCollected){
        this.diamondsCollected = diamondsCollected;
    }
    public void setKeys(Map<KeyColours, Integer> keys){
        this.keys.putAll(keys);
    }

    /**
     * The accessor method for the Player's key count.
     *
     * @return The number of Keys of each colour that the Player has collected.
     * */
    public Map<KeyColours, Integer> getKeys(){
        return keys;
    }

    public String toString(){
        return "P" + "," + position.getRow() + "," + position.getColumn();
    }

    /**
     * This method retrieves the Tile in a specified direction from the tile currently occupied by the
     * Player.
     *
     * @param direction The direction of the Tile desired
     * @return The Tile desired
     * */
    private Tile getNextTile(Direction direction) {
        return switch (direction) {
            case UP -> position.getUp();
            case DOWN -> position.getDown();
            case LEFT -> position.getLeft();
            case RIGHT -> position.getRight();
            default -> null;
        };
    }

    /**
     * This method checks if the Player can move into a new tile, and blocks movement if certain
     * conditions aren't met. (Player doesn't have the required key, Boulder cannot be pushed in
     * the direction of the Player's movement, Player doesn't have enough Diamonds.)
     * */
    private void processMove(Tile nextTile) {
        //Checks if the desired Tile is a Floor Tile
        if (nextTile instanceof Floor) {

            //Checks if the desired Tile is occupied.
            if (nextTile.isOccupied()) {

                Actor occupier = nextTile.getOccupier();

                //If the tile holds a Diamond, the Diamond is collected (updating diamond count)
                //and the Diamond is removed from the Tile.
                if (occupier instanceof Diamond) {
                    diamondsCollected++;
                    Audio.getInstance().playSoundEffect("/Music/DiamondCollect.mp3", 1.0);
                    GameState.manager.killActor(occupier);

                    //If the tile holds a Boulder. If the Boulder cannot be pushed, the Player's
                    // movement is blocked.
                } else if (occupier instanceof Boulder boulder) {
                    if (!boulder.push(currentDirection)) {
                        return;
                    }
                } else {
                    return;
                }

            }

            //If the Tile is a Locked Door, checks if the Player has any collected any keys matching
            //the door's colour. And if they don't have any then the Player's movement is blocked.
            if (nextTile instanceof LockedDoor) {
                KeyColours requiredKey = ((LockedDoor) nextTile).getColour();
                Integer noKeys = keys.get(requiredKey);
                if (noKeys <= 0) {
                    return;
                }
                keys.put(requiredKey, keys.get(requiredKey)-1);
            }

            //If the Tile is an Exit, checks that the number of Diamonds the Player has collected
            //is greater than the number of Diamonds required to exit. Otherwise, the Player's
            //movement is blocked.
            if (nextTile instanceof Exit) {
                if (diamondsCollected < GameState.level.getDiamondsRequired()){
                    return;
                }

            }
            changePos(nextTile);
        }
    }

}
