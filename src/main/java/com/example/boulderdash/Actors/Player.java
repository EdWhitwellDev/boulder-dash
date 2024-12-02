package com.example.boulderdash.Actors;

import com.example.boulderdash.Actors.Enemies.Enemy;
import com.example.boulderdash.Actors.Falling.Boulder;
import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.GameManager;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Floor;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.Dictionary;
import java.util.Map;

public class Player extends Actor {
    private static Map<Direction, Image> orientation = Map.of(
            Direction.STATIONARY, new Image("player_down.png"),
            Direction.UP, new Image("player_up.png"),
            Direction.DOWN, new Image("player_down.png"),
            Direction.LEFT, new Image("player_left.png"),
            Direction.RIGHT, new Image("player_right.png")
    );
    private Direction currentDirection = Direction.STATIONARY;
    private int tickCoolDown = 0;
    private int tickCoolDownReset = 2;
    private int diamondsCollected = 0;

    public Player(Tile startingTile){
        super(startingTile);
        image = orientation.get(currentDirection);
    }

    public void setDirection(Direction direction){
        currentDirection = direction;
        if (currentDirection != Direction.STATIONARY) {
            image = orientation.get(currentDirection);
        }

    }

    public int getDiamondsCollected(){
        return diamondsCollected;
    }

    public void collectedDiamond(){

    }

    public void move(){
        if (tickCoolDown > 0){
            tickCoolDown--;
        }
        else {
            Tile nextTile = getNextTile(currentDirection);
            if (nextTile != null) {
                processMove(nextTile);
            }
        }
    }

    private Tile getNextTile(Direction direction) {
        return switch (direction) {
            case UP -> position.getUp();
            case DOWN -> position.getDown();
            case LEFT -> position.getLeft();
            case RIGHT -> position.getRight();
            default -> null;
        };

    }


    private void processMove(Tile nextTile) {
        if (nextTile instanceof Floor) {
            if (nextTile.isOccupied()) {
                Actor occupier = nextTile.getOccupier();

                if (occupier instanceof Diamond) {
                    diamondsCollected++;
                    GameState.manager.killActor(occupier);
                } else if (occupier instanceof Boulder boulder) {
                    if (!boulder.push(currentDirection)) {
                        return;
                    }
                }

            }
            changePos(nextTile);
        }
    }


}
