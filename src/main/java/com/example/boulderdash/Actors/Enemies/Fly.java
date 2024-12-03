package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Actors.Explosion;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;
public class Fly extends Enemy{
    public static final Direction[] CARDINAL_DIRECTIONS = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
    private static final int TICK_COOL_DOWN_RESET = 8;
    private final boolean rightHanded;
    private final boolean buttery;
    private Direction currentDirection;
    private Direction handSide;
    private int tickCoolDown = 60;
    private int consecutiveTurning = 0;

    public Fly(Tile startPosition, boolean turnRight, boolean butter, Direction startDirection) {
        super(startPosition);
        this.rightHanded = turnRight;
        this.buttery = butter;
        this.currentDirection = startDirection;
        this.handSide = findHand(rightHanded);
        this.image = butter ? new Image("butterfly.png") : new Image("firefly.png");
    }

    public void move(){
        if (crushed){
            explode();
        }
        if (tickCoolDown > 0){
            tickCoolDown--;
        }
        else {
            boolean turnFlag = false;
            if (consecutiveTurning > 5) {
                explode();
                GameState.manager.killActor(this);
            } else {
                Tile side = findTile(handSide);
                if (side != null && isAbleToMoveToTile(side)) {
                    currentDirection = handSide;
                    handSide = findHand(rightHanded);
                    turnFlag = true;
                }
                Tile forward = findTile(currentDirection);
                if (forward != null && isAbleToMoveToTile(forward)) {
                    changePos(forward);
                    tickCoolDown = TICK_COOL_DOWN_RESET;
                } else {
                    currentDirection = findHand(!rightHanded);
                    handSide = findHand(rightHanded);
                }
                consecutiveTurning = turnFlag ? consecutiveTurning + 1 : 0;
            }
        }
    }
    private boolean isAbleToMoveToTile(Tile tile) {
        if (!tile.isPath()) {
            return false;
        }
        return !tile.isOccupied();
    }
    private Tile findTile(Direction direction){
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
    private Direction findHand(boolean turnRight){
        int currentDirIndex = getDirectionIndex(currentDirection);
        currentDirIndex += turnRight ? 1 : -1;
        return CARDINAL_DIRECTIONS[(currentDirIndex + CARDINAL_DIRECTIONS.length)%CARDINAL_DIRECTIONS.length];
    }
    private int getDirectionIndex(Direction direction){
        for (int index = 0; index < CARDINAL_DIRECTIONS.length; index++){
            if (CARDINAL_DIRECTIONS[index] == direction){
                return index;
            }
        }
        return 0;
    }

    public void explode(){
        Explosion explosion = new Explosion(position);
        GameState.manager.addActor(explosion);
    }
}