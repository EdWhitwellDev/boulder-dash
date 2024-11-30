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

public class Player extends Actor {
    private Direction currentDirection = Direction.STATIONARY;
    private int tickCoolDown = 0;
    private int tickCoolDownReset = 2;
    private int diamondsCollected = 0;

    public Player(Tile startingTile){
        super(startingTile);
        image = new Image("player.png");
    }

    public void setDirection(Direction direction){
        currentDirection = direction;
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
        switch (direction) {
            case UP:
                return position.getUp();
            case DOWN:
                return position.getDown();
            case LEFT:
                return position.getLeft();
            case RIGHT:
                return position.getRight();
            default:
                return null;
        }

    }

    private void processMove(Tile nextTile) {
        if (nextTile instanceof Floor) {
            if (nextTile.isOccupied()) {
                Actor occupier = nextTile.getOccupier();

                if (occupier instanceof Diamond) {
                    diamondsCollected++;
                    occupier.setPosition(null);
                } else if (occupier instanceof Boulder) {
                    Boulder boulder = (Boulder) occupier;
                    if (!boulder.push(currentDirection)) {
                        return;
                    }
                }
            }
            changePos(nextTile);
        }
    }


    private void validateMove(Tile nextPos){
        if (nextPos != null){
            if (nextPos instanceof Floor){
                if (nextPos.isOccupied()){
                    if (nextPos.getOccupier() instanceof Diamond){
                        diamondsCollected++;
                        GameState.level.removeActor(nextPos.getOccupier());
                    }
                }
                changePos(nextPos);
            }
        }
    }

}
