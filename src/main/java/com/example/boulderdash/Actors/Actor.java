package com.example.boulderdash.Actors;


import com.example.boulderdash.Actors.Enemies.Enemy;
import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.List;

public class Actor {
    protected Tile position;
    protected int tickCoolDown;
    protected int TICK_COOL_DOWN_RESET;
    protected Direction currentDirection;
    protected Image image;

    public Actor(Tile startPosition){
        position = startPosition;
        if (position != null) {
            position.setOccupier(this);
        }
    }

    public Image getImage() {
        return image;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public Tile getPosition() {
        return position;
    }

    protected void changePos(Tile nextPos) {
        position.setOccupier(null);
        position = nextPos;

        position.setOccupier(this);

        checkCollisions();
    }

    public void move(){}

    protected void checkCollisions(){
        List<Actor> collisionOther = position.checkAdjacent();
        if (!collisionOther.isEmpty()) {
            for (Actor collider : collisionOther){
                if (collider instanceof Enemy && this instanceof Player){
                    GameState.manager.loseGame();
                } else if (this instanceof Enemy && collider instanceof Player) {
                    GameState.manager.loseGame();
                }
            }
        }
    }

    public void setPosition(Tile newTile) {
        if (position != null) {
            position.setOccupier(null);
        }
        position = newTile;
        if (newTile != null) {
            newTile.setOccupier(this);
        }
    }

    //Hello

}