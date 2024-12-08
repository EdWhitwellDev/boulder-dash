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
 * Class to represent a Player, subclass of Actor. It inherits basic
 * movement and position handling from the Actor class, and it's movement
 * is controlled by the keyboard.
 */
public class Player extends Actor {
    private static final double VOLUME = 0.5;
    /**
     * A Map of all Directions to their corresponding Images with the
     * correct orientation.
    */
    private static final Map<Direction, Image> ORIENTATION = Map.of(
            Direction.STATIONARY,
                new Image("Actor Images/Player/player_down.png"),
            Direction.UP,
                new Image("Actor Images/Player/player_up.png"),
            Direction.DOWN,
                new Image("Actor Images/Player/player_down.png"),
            Direction.LEFT,
                new Image("Actor Images/Player/player_left.png"),
            Direction.RIGHT,
                new Image("Actor Images/Player/player_right.png")
    );

    /**
     * A Map holding the key count for each key colour.
     * */
    private final Map<KeyColours, Integer> keys = new HashMap<>();
    /** */
    private int tickCoolDown = 0;

    /**
     * The number of diamonds collected by the player.
     * */
    private int diamondsCollected = 0;

    /**
     * Creates a new Player at the specified starting position.
     * And sets the player to have no keys (of any colour) and
     * be stationary.
     *
     * @param startingTile The tile at which the Player will initially
     *                     appear
     * */
    public Player(final Tile startingTile) {
        super(startingTile);
        setImage(ORIENTATION.get(getCurrentDirection()));
        keys.put(KeyColours.RED, 0);
        keys.put(KeyColours.BLUE, 0);
        keys.put(KeyColours.GREEN, 0);
        keys.put(KeyColours.YELLOW, 0);
        setCurrentDirection(Direction.STATIONARY);
    }


    //Getters
    /**
     * The accessor method for the number of Diamonds the Player has
     * collected.
     *
     * @return The number of Diamonds the Player has collected.
     * */
    public int getDiamondsCollected() {
        return diamondsCollected;
    }
    /**
     * The accessor method for the Player's key count.
     *
     * @return The number of Keys of each colour that the Player has collected.
     * */
    public Map<KeyColours, Integer> getKeys() {
        return keys;
    }
    /**
     * This method retrieves the Tile in a specified direction from the
     * tile currently occupied by the Player.
     *
     * @param direction The direction of the Tile desired
     * @return The Tile desired
     * */
    private Tile getNextTile(final Direction direction) {
        return switch (direction) {
            case UP -> getPosition().getUp();
            case DOWN -> getPosition().getDown();
            case LEFT -> getPosition().getLeft();
            case RIGHT -> getPosition().getRight();
            default -> null;
        };
    }

    //Setters
    /**
     * The mutator method to set the number of diamonds collected by this
     * Player to the number inputted.
     *
     * @param newDiamondsCollected The number of diamonds collected.
     * */
    public void setDiamondsCollected(final int newDiamondsCollected) {
        this.diamondsCollected = newDiamondsCollected;
    }
    /**
     * The mutator method to set the number of keys of each color collected
     * by this Player.
     *
     * @param newKeys The Map of keys to be set as this Player's collected keys.
     * */
    public void setKeys(final Map<KeyColours, Integer> newKeys) {
        this.keys.putAll(newKeys);
    }
    /**
     * This the mutator method sets the Player's direction and changes
     * the Player's Image/Orientation to match the direction it wants
     * to move in.
     *
     * @param direction The new direction of the Player
     * */
    public void setDirection(final Direction direction) {
        if (direction != getCurrentDirection()) {
            tickCoolDown = 0;
        }
        setCurrentDirection(direction);
        if (getCurrentDirection() != Direction.STATIONARY) {
            setImage(ORIENTATION.get(getCurrentDirection()));
        }
    }


    /**
     * Movement execution for the player.
     */
    public void move() {
        if (tickCoolDown > 0) {
            tickCoolDown--;
        } else {
            Tile nextTile = getNextTile(getCurrentDirection());
            if (nextTile != null) {
                processMove(nextTile);
                Audio.getInstance().playSoundEffect("/Music/Move.mp3", VOLUME);
            }
            tickCoolDown = 2;
        }
    }

    /**
     * This method allows the player to collect a Key, and update it's
     * key count.
     *
     * @param keyColour The colour of the Key collected by the Player.
     * */
    public void collectKey(final KeyColours keyColour) {
        keys.put(keyColour, keys.get(keyColour) + 1);
        Audio.getInstance().playSoundEffect("/Music/Key.mp3", 1);
    }

    /**
     * This method checks if the Player can move into a new tile, and
     * blocks movement if certain conditions aren't met. (Player
     * doesn't have the required key, Boulder cannot be pushed in
     * the direction of the Player's movement, Player doesn't have
     * enough Diamonds.)
     *
     * @param nextTile The Tile that the Player would like to move into.
     * */
    private void processMove(final Tile nextTile) {
        //Checks if the desired Tile is a Floor Tile
        if (nextTile instanceof Floor) {

            //Checks if the desired Tile is occupied.
            if (nextTile.isOccupied()) {

                Actor occupier = nextTile.getOccupier();

                //If the tile holds a Diamond, the Diamond is
                // collected (updating diamond count) and the
                // Diamond is removed from the Tile.
                if (occupier instanceof Diamond) {
                    diamondsCollected++;
                    Audio.getInstance().playSoundEffect(
                            "/Music/DiamondCollect.mp3",
                            1.0);
                    GameState.getManager().killActor(occupier);

                    //If the tile holds a Boulder. If the Boulder
                    // cannot be pushed, the Player's movement is
                    // blocked.
                } else if (occupier instanceof Boulder boulder) {
                    if (!boulder.push(getCurrentDirection())) {
                        return;
                    }
                } else {
                    return;
                }

            }

            //If the Tile is a Locked Door, checks if the Player has
            // any collected any keys matching the door's colour. And
            // if they don't have any then the Player's movement is
            // blocked.
            if (nextTile instanceof LockedDoor) {
                KeyColours requiredKey = ((LockedDoor) nextTile).getColour();
                Integer noKeys = keys.get(requiredKey);
                if (noKeys <= 0) {
                    return;
                }
                Audio.getInstance().playSoundEffect("/Music/DoorOpen.mp3", 1);
                keys.put(requiredKey, keys.get(requiredKey) - 1);
            }

            //If the Tile is an Exit, checks that the number of
            // Diamonds the Player has collected is greater than
            // the number of Diamonds required to exit. Otherwise,
            // the Player's movement is blocked.
            if (nextTile instanceof Exit) {
                if (diamondsCollected < GameState.getLevel().getDiamondsRequired()) {
                    return;
                }

            }
            changePos(nextTile);
        }
    }

    /**
     * This is a method to represent a Player object in the desired
     * string format.
     *
     * @return A string in the format :
     *             P,v1,v2 (where v1 = RowNumber and v2 = ColumnNumber)
     * */
    public String toString() {
        return "P" + "," + getPosition().getRow() + ","
                + getPosition().getColumn();
    }
}
