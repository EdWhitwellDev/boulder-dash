package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Actors.Falling.Boulder;
import com.example.boulderdash.Tiles.Floor;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class Fly extends Enemy{
    public static final Direction[] CARDINAL_DIRECTIONS = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
    private static final int TICK_COOL_DOWN_RESET = 8;
    private final boolean rightHanded;
    private final boolean buttery;
    private Direction currentDirection;
    private Direction handSide;
    private int tickCoolDown = 6;

    public Fly(Tile startPosition, boolean turnRight, boolean butter, Direction startDirection) {
        super(startPosition);
        this.rightHanded = turnRight;
        this.buttery = butter;
        this.currentDirection = startDirection;
        this.handSide = findHand(rightHanded);
        this.image = butter ? new Image("butterfly.png") : new Image("firefly.png");
    }

    public void move(){
        if (tickCoolDown > 0){
            tickCoolDown--;
        }
        else {
            Tile side = findTile(handSide);
            if (side != null && isAbleToMoveToTile(side)){
                currentDirection = handSide;
                handSide = findHand(rightHanded);

            }
            Tile forward = findTile(currentDirection);
            if (forward != null && isAbleToMoveToTile(forward)) {
                changePos(forward);
                tickCoolDown = TICK_COOL_DOWN_RESET;
            } else {
                currentDirection = findHand(!rightHanded);
                handSide = findHand(rightHanded);
            }
        }
    }

    private boolean isAbleToMoveToTile(Tile tile) {
        if (!tile.isPath()) {
            return false;
        }

        if (tile.getOccupier() instanceof Boulder && tile.isOccupied()) {
            return false;
        }
        return true;
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
}
