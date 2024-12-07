package com.example.boulderdash.Actors;


import com.example.boulderdash.Actors.Enemies.Enemy;
import com.example.boulderdash.Actors.Enemies.Fly;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Handles the actions of any Actor. An actor being an entity that can move on the grid and interact with other actors.
 * @author Viraj Shah & Ed Whitwell
 * @version 1.3
 */
public abstract class Actor {
    protected Tile position;
    protected int tickCoolDown;
    protected Direction currentDirection = Direction.STATIONARY;
    protected Image image;
    private boolean isTransferring;
    private Tile previousPosition;

    /**
     * Constructor for an Actor at a specific starting tile.
     * @param startPosition is the initial {@link Tile} position of the Actor.
     */
    public Actor(Tile startPosition){
        position = startPosition;
        if (position != null) {
            position.setOccupier(this);
        }
    }

    /**
     * Specifies how the actor moves on the grid.
     * Overridden by subclasses for specific movement.
     */
    public void move(){}

    /**
     * Detects collisions with other actors in neighbouring tiles.
     */
    public boolean checkCollisions(){
        List<Actor> collisionOther = position.checkAdjacent();
        if (!collisionOther.isEmpty()) {
            for (Actor collider : collisionOther){
                if (collider instanceof Enemy && this instanceof Player){
                    Class<?> enemyClass = collider.getClass();
                    String enemyType = enemyClass.getSimpleName();
                    if (collider instanceof Fly fly) {
                        GameState.manager.looseGame("Killed by a " + (fly.isbuttery() ? "Butterfly" : "Firefly"));
                    }
                    else {
                        GameState.manager.looseGame("Killed by a " + enemyType);
                    }
                    return true;
                } else if (this instanceof Enemy && collider instanceof Player) {
                    Class<?> enemyClass = this.getClass();
                    String enemyType = enemyClass.getSimpleName();
                    if (this instanceof Fly fly) {
                        GameState.manager.looseGame("Killed by a " + (fly.isbuttery() ? "Butterfly" : "Firefly"));
                    }
                    else {
                        GameState.manager.looseGame("Killed by a " + enemyType);
                    }
                    return true;
                } else if (this instanceof Enemy && collider instanceof Amoeba) {
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
    public void stopTransferring(){
        isTransferring = false;
    }


    /**
     *  Getters
     */
    public Image getImage() {
        return image;
    }

    public Tile getPosition() {
        return position;
    }

    public boolean getIsTransferring(){
        return isTransferring;
    }

    public Tile getPreviousPosition(){
        return previousPosition;
    }

    /**
     * Changes the position of an Actor to a new tile and detects any collisions.
     * @param nextPos is the next {@link Tile} to move to.
     */
    protected void changePos(Tile nextPos) {
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