package com.example.boulderdash.Actors;


import com.example.boulderdash.Actors.Enemies.Enemy;
import com.example.boulderdash.Actors.Enemies.Fly;
import com.example.boulderdash.Audio;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Handles the actions of any Actor. An actor being an entity that
 * can move on the grid and interact with other actors.
 *
 * @author Viraj Shah & Ed Whitwell
 * @version 1.3
 */
public abstract class Actor {

    /**
     * The current Tile on which the Actor is located.
     * */
    private Tile position;


    private int tickCoolDown;

    /**
     * The current Direction of the actor, initially stationary when created.
     * */
    private Direction currentDirection = Direction.STATIONARY;

    /**
     * The Image representing the Actor.
     * */
    private Image image;
    private boolean isTransferring;

    /**
     * The Tile that the Actor was previously on.
     * */
    private Tile previousPosition;

    /**
     * Constructor for an Actor at a specific starting tile.
     * @param startPosition is the initial {@link Tile} position of the Actor.
     */
    public Actor(final Tile startPosition) {
        position = startPosition;
        if (position != null) {
            position.setOccupier(this);
        }
    }

    //Getters



    /**
     * Specifies how the actor moves on the grid.
     * Overridden by subclasses for specific movement.
     */
    public void move() { }

    /**
     * Detects collisions with other actors in neighbouring tiles.
     *
     * @return Whether there was a collision between this actor
     * and another actor in a neighbouring tile.
     */
    public boolean checkCollisions() {
        List<Actor> collisionOther = position.checkAdjacent();
        if (!collisionOther.isEmpty()) {
            for (Actor collider : collisionOther) {
                if (collider instanceof Enemy && this instanceof Player) {
                    Class<?> enemyClass = collider.getClass();
                    String enemyType = enemyClass.getSimpleName();
                    if (collider instanceof Fly fly) {
                        GameState.getManager().looseGame(
                                "Killed by a "
                                        + (fly.isButtery() ? "Butterfly"
                                        : "Firefly"));
                        Audio.getInstance().playSoundEffect(
                                "/Music/MinecraftDeath.mp3",
                                1.0);
                    } else {
                        GameState.getManager().looseGame(
                                "Killed by a "
                                        + enemyType);
                        Audio.getInstance().playSoundEffect(
                                "/Music/MinecraftDeath.mp3",
                                1.0);
                    }
                    return true;
                } else if (this instanceof Enemy
                        && collider instanceof Player) {

                    Class<?> enemyClass = this.getClass();
                    String enemyType = enemyClass.getSimpleName();
                    if (this instanceof Fly fly) {
                        GameState.getManager().looseGame(
                                "Killed by a "
                                        + (fly.isButtery() ? "Butterfly"
                                        : "Firefly"));
                        Audio.getInstance().playSoundEffect(
                                "/Music/MinecraftDeath.mp3",
                                        1.0);
                    } else {
                        GameState.getManager().looseGame(
                                "Killed by a "
                                        + enemyType);
                        Audio.getInstance().playSoundEffect(
                                "/Music/MinecraftDeath.mp3",
                                1.0);
                    }
                    return true;
                } else if (this instanceof Enemy
                        && collider instanceof Amoeba) {

                    ((Enemy) this).explodeSingle();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Stops the actor from transferring between tiles.
     */
    public void stopTransferring() {
        isTransferring = false;
    }

    //Getters

    /**
     * Accessor method for this actor's tick cool down.
     *
     * @return Actor's tick cool down.
     */
    public int getTickCoolDown() {
        return tickCoolDown;
    }

    /**
     * Accessor method for the Actor's current direction.
     *
     * @return The actor's current direction.
     */
    public Direction getCurrentDirection() {
        return currentDirection;
    }

    /**
     * Accessor method for the Image representing the Actor.
     *
     * @return The Actor's image.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Accessor method for the Actor's current position.
     *
     * @return Teh Tile which the Actor is currently occupying.
     */
    public Tile getPosition() {
        return position;
    }

    /**
     * Accessor method for whether the Actor is transferring.
     *
     * @return Whether the Actor is transferring.
     */
    public boolean getIsTransferring() {
        return isTransferring;
    }

    /**
     * Accessor method for the previous position occupied by this actor.
     *
     * @return The Tile that this actor was previously in.
     */
    public Tile getPreviousPosition() {
        return previousPosition;
    }

    //Setters
    /**
     * Mutator method to set the Actor's current position to a new
     * position.
     *
     * @param newTile The new Tile that is to be occupied by the Actor.
     * */
    public void setPosition(final Tile newTile) {
        position = newTile;
    }

    /**
     * Mutator method to set the Actor's tick cool down.
     *
     * @param newTickCoolDown The Actor's new tick cool down value.
     * */
    public void setTickCoolDown(final int newTickCoolDown) {
        this.tickCoolDown = newTickCoolDown;
    }
    /**
     * Mutator method to set the Actor's new direction.
     *
     * @param newCurrentDirection The Actor's new direction, becomes the
     *                            new 'current' value after method is called.
     * */
    public void setCurrentDirection(final Direction newCurrentDirection) {
        this.currentDirection = newCurrentDirection;
    }
    /**
     * Mutator method to set a new Image to represent the Actor.
     *
     * @param newImage The Image is to represent the Actor.
     * */
    public void setImage(final Image newImage) {
        this.image = newImage;
    }
    public void setTransferring(boolean transferring) {
        isTransferring = transferring;
    }
    public void setPreviousPosition(Tile newPreviousPosition) {
        previousPosition = newPreviousPosition;
    }

    /**
     * Changes the position of an Actor to a new tile and detects any
     * collisions.
     * @param nextPos is the next {@link Tile} to move to.
     */
    protected void changePos(final Tile nextPos) {
        if (checkCollisions()) {
            return;
        }
        position.setOccupier(null);
        previousPosition = position;
        position = nextPos;
        position.setOccupier(this);

        isTransferring = true;
    }

}
