package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Actors.Explosion;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.Map;

/**
 * Represents a fly in the game, either a butterfly or a firefly.
 * @author Ed Whitewell
 */
public class Fly extends Enemy{
    public static final Direction[] CARDINAL_DIRECTIONS = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
    private static final Map<Direction, Image> orientationButterFly = Map.of(
            Direction.STATIONARY, new Image("Actor Images/Butterfly/butterfly.png"),
            Direction.UP, new Image("Actor Images/Butterfly/butterfly.png"),
            Direction.DOWN, new Image("Actor Images/Butterfly/butterfly_down.png"),
            Direction.LEFT, new Image("Actor Images/Butterfly/butterfly_left.png"),
            Direction.RIGHT, new Image("Actor Images/Butterfly/butterfly_right.png")
    );
    private static final Map<Direction, Image> orientationFireFly = Map.of(
            Direction.STATIONARY, new Image("Actor Images/Firefly/firefly.png"),
            Direction.UP, new Image("Actor Images/Firefly/firefly.png"),
            Direction.DOWN, new Image("Actor Images/Firefly/firefly_down.png"),
            Direction.LEFT, new Image("Actor Images/Firefly/firefly_left.png"),
            Direction.RIGHT, new Image("Actor Images/Firefly/firefly_right.png")
    );
    private static final int TICK_COOL_DOWN_RESET = 3;
    private final boolean rightHanded;
    private Direction handSide;
    private int tickCoolDown = 1;
    private int consecutiveTurning = 0;

    /**
     * Constructor for a fly at the specified location.
     * @param startPosition the starting {@link Tile} for the fly.
     * @param turnRight {@code True} if the fly should follow the right hand side. {@code False} if left hand side.
     * @param butter {@code True} if the fly is a butterfly. {@code False} if it is a firefly.
     * @param startDirection is the initial {@link Direction} of the fly.
     */
    public Fly(Tile startPosition, boolean turnRight, boolean butter, Direction startDirection) {
        super(startPosition);
        this.rightHanded = turnRight;
        this.buttery = butter;
        this.currentDirection = startDirection;
        this.handSide = findHand(rightHanded);
        this.image = butter ? new Image("Actor Images/Butterfly/butterfly.png") : new Image("Actor Images/Firefly/firefly.png");
    }

    /**
     * Handles the movement logic of the fly.
     * If the fly gets crushed, it will explode. Otherwise, after the tick cooldown it will continue to move.
     */
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
                explodeSingle();
            } else {
                Tile side = findTile(handSide);
                if (side != null && isAbleToMoveToTile(side)) {
                    changeDirection(handSide);
                    handSide = findHand(rightHanded);
                    turnFlag = true;
                }
                Tile forward = findTile(currentDirection);
                if (forward != null && isAbleToMoveToTile(forward)) {
                    changePos(forward);
                    tickCoolDown = TICK_COOL_DOWN_RESET;
                } else {
                    changeDirection(findHand(!rightHanded));
                    handSide = findHand(rightHanded);
                }
                consecutiveTurning = turnFlag ? consecutiveTurning + 1 : 0;
            }
        }
    }

    /**
     * Changes the fly's direction and updates the animation.
     * @param newDirection is the new {@link Direction} to face.
     */
    private void changeDirection(Direction newDirection){
        currentDirection = newDirection;
        if (buttery){
            image = orientationButterFly.get(newDirection);
        }
        else {
            image = orientationFireFly.get(newDirection);
        }
    }

    /**
     * Checks if the fly can move to a specific tile.
     * @param tile is the {@link Tile} to be checked.
     * @return {@code True} if the fly can move to the tile.
     */
    private boolean isAbleToMoveToTile(Tile tile) {
        if (!tile.isPath()) {
            return false;
        }
        return !tile.isOccupied();
    }

    /**
     * Finds the tile in the specified direction relative to the fly's position.
     * @param direction is the {@link Direction} to be checked.
     * @return the {@link Tile} in the specified direction, or {@code null} if no tile found.
     */
    private Tile findTile(Direction direction){
        return switch (direction) {
            case UP -> position.getUp();
            case DOWN -> position.getDown();
            case LEFT -> position.getLeft();
            case RIGHT -> position.getRight();
            default -> null;
        };
    }

    /**
     * Determines the fly's hand direction.
     * @param turnRight {@code True} if the fly follows right hand side. {@code False} if left hand side.
     * @return
     */
    private Direction findHand(boolean turnRight){
        int currentDirIndex = getDirectionIndex(currentDirection);
        currentDirIndex += turnRight ? 1 : -1;
        return CARDINAL_DIRECTIONS[(currentDirIndex + CARDINAL_DIRECTIONS.length)%CARDINAL_DIRECTIONS.length];
    }

    /**
     * Retrieves the index of the specified direction in the array.
     * @param direction is the {@link Direction} to find.
     * @return the index of the direction, or 0 if not found.
     */
    private int getDirectionIndex(Direction direction){
        for (int index = 0; index < CARDINAL_DIRECTIONS.length; index++){
            if (CARDINAL_DIRECTIONS[index] == direction){
                return index;
            }
        }
        return 0;
    }

    public String toString(){
        String symbol = buttery ? "BF" : "F";
        return symbol + "," + position.getRow() + "," + position.getColumn() + "," + buttery + "," + currentDirection.toString();
    }
}
