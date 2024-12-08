package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Enemies.Enemy;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Audio;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.MagicWall;
import com.example.boulderdash.Tiles.Tile;

/**
 * Handles the actions of any falling objects.
 * @author Viraj Shah
 * @version 1.2
 */
public abstract class FallingObject extends Actor {


    //Variables
    /**
     * */
    private static final int DELAY = 3;
    /**
     * Boolean to show whether a falling object
     * explodes/causes an explosion.
     */
    private boolean exploded = false;
    /**
     * Boolean to show whether a falling object is currently falling.
     */
    private boolean isFalling = false;

    /**
     * Slows the falling speed.
     */
    private int fallDelay = 0;
    /**
     * Slows the rolling speed.
     */
    private int rollDelay = 0;
    /**
     * Constructor for a FallingObject at a specific starting tile.
     *
     * @param startPosition is the initial {@link Tile} position of
     *                      the falling object.
     */
    public FallingObject(final Tile startPosition) {
        super(startPosition);
    }

    //Getters
    /**
     * Accessor method for whether a Falling object exploded.
     *
     * @return Boolean to show whether the falling object exploded.
     */
    public boolean isExploded() {
        return exploded;
    }
    /**
     * Accessor method for whether a Falling object is falling.
     *
     * @return Boolean to show whether the falling object is falling.
     */
    public boolean isFalling() {
        return isFalling;
    }

    //Setters
    /**
     * Mutator method to set whether a Falling object exploded.
     *
     * @param ifIsExploded The value to represent whether the falling
     *                     object exploded .
     * */
    public void setExploded(final boolean ifIsExploded) {
        this.exploded = ifIsExploded;
    }
    /**
     * Mutator method to set whether a Falling object is falling.
     *
     * @param falling The value to represent whether the falling
     *                object is falling .
     * */
    public void setFalling(final boolean falling) {
        isFalling = falling;
    }

    //Methods
    /**
     * Updates the new position of an object.
     * @param newTile is the new {@link Tile} to move to.
     */
    public void setPosition(final Tile newTile) {
        setPreviousPosition(getPosition());
        setTransferring(true);

        if (getPosition() != null) {
            getPosition().setOccupier(null);
        }
        super.setPosition(newTile);
        // Handle interactions with the new tile
        if (newTile != null) {
            newTile.setOccupier(this);
            if (newTile instanceof MagicWall) {
                transform();
            }
        }
    }
    /**
     * Defines the object's falling behaviour.
     * If the object can fall, the position is updated to the tile
     * below until it can no longer fall.
     */
    public void fall() {
        if (exploded) {
            GameState.getManager().killActor(this);
        } else {
            // Delay to control speed
            if (fallDelay > 0) {
                fallDelay--;
                return;
            }
            fallDelay = 2;
            // Get the tile directly below
            Tile underTile = getPosition().getDown();
            // Check if the object can fall onto the tile below
            if (isAbleToFall(underTile)) {
                setPosition(underTile);
                isFalling = true;
            } else {
                isFalling = false;
                onPath(underTile);
            }
            if (!isFalling) {
                roll();
            }
        }
    }

    /**
     * Defines how a falling objects transforms when interacting with
     * a {@link MagicWall}.
     * Overridden by subclasses.
     */
    public abstract void transform();

    /**
     * Enables an object to explode and removes it from the level.
     */
    public void explode() {
        exploded = true;
    }

    /**
     * Defines the object's rolling behaviour.
     * If an object can roll, it's position is updated either left or right.
     */
    private void roll() {
        // Delay to control speed
        if (rollDelay > 0) {
            rollDelay--;
            return;
        }
        rollDelay = DELAY;
        // Check the tiles to the left and right
        Tile leftTile = getPosition().getLeft();
        Tile rightTile = getPosition().getRight();

        // Roll to the first available direction
        if (isAbleToRollTo(leftTile)) {
            setPosition(leftTile);
        } else if (isAbleToRollTo(rightTile)) {
            setPosition(rightTile);
        }
    }

    /**
     * Handles the rolling behaviour.
     *
     * @param tile is the {@link Tile} to check if it can be occupied.
     * @return {@code True} if the object can roll to the tile.
     */
    private boolean isAbleToRollTo(final Tile tile) {
        return tile != null && tile.isPath()
                && !tile.isOccupied() && tile.getDown() != null
                && tile.getDown().isPath()
                && !tile.getDown().isOccupied();
    }

    /**
     * Handles the falling behaviour.
     * @param underTile is the {@link Tile} below the specific object.
     * @return {@code True} if the object can fall
     */
    private boolean isAbleToFall(final Tile underTile) {
        if (underTile == null) {
            return false;
        }
        if (underTile.isPath() || underTile instanceof MagicWall) {
            if (underTile instanceof MagicWall) {
                return underTile.getDown() != null
                        && underTile.getDown().isPath()
                        && !underTile.getDown().isOccupied();
            }
            Actor occupant = underTile.getOccupier();
            if (occupant == null) {
                return true;
            }
            if (isFalling) {
                if (occupant instanceof Enemy) {
                    ((Enemy) occupant).crush();
                    explode();
                    return true;
                }
                if (occupant instanceof Player) {
                    GameState.getManager().looseGame(
                            "Crushed by a "
                                    + this.getClass().getSimpleName());
                    Audio.getInstance().playSoundEffect(
                            "/Music/MinecraftDeath.mp3",
                            1.0);
                    return true;
                }
            }
        }
        return false; // Checks if the tile under is a tile and is empty
    }

    private void onPath(final Tile underTile) {

    }
}
