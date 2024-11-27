package com.example.boulderdash.Actors;

import com.example.boulderdash.Actors.Enemies.Enemy;
import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.GameManager;
import com.example.boulderdash.Tiles.Floor;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

public class Player extends Actor {
    private Direction currentDirection = Direction.STATIONARY;
    private int tickCoolDown = 0;
    private int tickCoolDownReset = 6;
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

    public void move(){
        if (tickCoolDown > 0){
            tickCoolDown--;
        }
        else {
            switch (currentDirection) {
                case UP:
                    validateMove(position.getUp());
                    break;
                case DOWN:
                    validateMove(position.getDown());
                    break;
                case LEFT:
                    validateMove(position.getLeft());
                    break;
                case RIGHT:
                    validateMove(position.getRight());
                    break;
                default:
                    break;
            }
        }

    }

    private void validateMove(Tile nextPos){
        if (nextPos != null){
            if (nextPos instanceof Floor){
                changePos(nextPos);
            }
        }
    }

    private void changePos(Tile nextPos){
        tickCoolDown = tickCoolDownReset;
        position.setOccupier(null);
        position = nextPos;
        if (nextPos.isOccupied()){
            if (nextPos.getOccupier() instanceof Diamond){
                diamondsCollected++;
            }
        }
        position.setOccupier(this);
    }

    private void checkCollisions(){
        Actor collisionOther = position.checkAdjacent();
        if (collisionOther != null) {
            if (collisionOther instanceof Enemy) {
                GameManager.tickTimeline.stop();
            }
        }
    }
}
