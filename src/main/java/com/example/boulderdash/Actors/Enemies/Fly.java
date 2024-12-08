package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.Map;

/**
 * Represents a fly in the game, either a butterfly or a firefly.
 * @author Ed Whitewell
 */
public class Fly extends Enemy {
    //TODO Set Comments for TICK_COOL_DOWN_RESET, handSide, tickCoolDown
    // and consecutive turning.

    /**
     * An array of the possible directions that the fly can move in,
     * used to determine the orientation of the Fly's image.
     */
    public static final Direction[] CARDINAL_DIRECTIONS =
            {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};

    /**
     * A Map of each direction a Butterfly is moving onto the Image with
     * the right orientation.
     */
    private static final Map<Direction, Image> ORIENTATION_BUTTERFLY = Map.of(

            Direction.STATIONARY,
                new Image("Actor Images/Butterfly/butterfly.png"),
            Direction.UP,
                new Image("Actor Images/Butterfly/butterfly.png"),
            Direction.DOWN,
                new Image("Actor Images/Butterfly/butterfly_down.png"),
            Direction.LEFT,
                new Image("Actor Images/Butterfly/butterfly_left.png"),
            Direction.RIGHT,
                new Image("Actor Images/Butterfly/butterfly_right.png")
    );

    /**
     * A Map of each direction a Firefly is moving onto the Image with
     * the right orientation.
     */
    private static final Map<Direction, Image> ORIENTATION_FIREFLY = Map.of(

            Direction.STATIONARY,
                new Image("Actor Images/Firefly/firefly.png"),
            Direction.UP,
                new Image("Actor Images/Firefly/firefly.png"),
            Direction.DOWN,
                new Image("Actor Images/Firefly/firefly_down.png"),
            Direction.LEFT,
                new Image("Actor Images/Firefly/firefly_left.png"),
            Direction.RIGHT,
                new Image("Actor Images/Firefly/firefly_right.png")
    );
    private static final int TICK_COOL_DOWN_RESET = 3;
    private static final int MAX_TURN = 5;

    /**
     * Boolean to check if the 'Fly' is moving Right or Left.
     * */
    private final boolean rightHanded;
    private Direction handSide;
    private int tickCoolDown = 1;
    private int consecutiveTurning = 0;

    /**
     * Constructor for a fly at the specified location.
     *
     * @param startPosition the starting {@link Tile} for the fly.
     * @param turnRight {@code True} if the fly should follow the right hand
     *                             side. {@code False} if left hand side.
     * @param butter {@code True} if the fly is a butterfly. {@code False}
     *                             if it is a firefly.
     * @param startDirection is the initial {@link Direction} of the fly.
     */
    public Fly(final Tile startPosition, final boolean turnRight,
               final boolean butter, final Direction startDirection) {

        super(startPosition);
        this.rightHanded = turnRight;

        //Boolean checking if the 'Fly' is a Butterfly or a Firefly
        //If butter==true, then it is a Butterfly otherwise, it is a Firefly.
        this.setButtery(butter);
        setCurrentDirection(startDirection);
        this.handSide = findHand(rightHanded);

        //Sets the Fly's image based on which type it is
        this.setImage(
                butter ? new Image("Actor Images/Butterfly/butterfly.png")
                : new Image("Actor Images/Firefly/firefly.png"));
    }

    /**
     * Handles the movement logic of the fly.
     * If the fly gets crushed, it will explode. Otherwise, after the tick
     * cool down it will continue to move.
     */
    public void move() {
        if (isCrushed()) {
            explode();
        }
        if (tickCoolDown > 0) {
            tickCoolDown--;
        } else {
            boolean turnFlag = false;
            if (consecutiveTurning > MAX_TURN) {
                //TODO Make into a Constant, not a Magic Number!
                explodeSingle();
            } else {
                Tile side = findTile(handSide);
                if (side != null && isAbleToMoveToTile(side)) {
                    changeDirection(handSide);
                    handSide = findHand(rightHanded);
                    turnFlag = true;
                }
                Tile forward = findTile(getCurrentDirection());
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
    private void changeDirection(final Direction newDirection) {
        setCurrentDirection(newDirection);
        if (isButtery()) {
            setImage(ORIENTATION_BUTTERFLY.get(newDirection));
        } else {
            setImage(ORIENTATION_FIREFLY.get(newDirection));
        }
    }

    /**
     * Checks if the fly can move to a specific tile.
     * @param tile is the {@link Tile} to be checked.
     * @return {@code True} if the fly can move to the tile.
     */
    private boolean isAbleToMoveToTile(final Tile tile) {
        if (!tile.isPath()) {
            return false;
        }
        return !tile.isOccupied();
    }

    /**
     * Finds the tile in the specified direction relative to the fly's
     * position.
     *
     * @param direction is the {@link Direction} to be checked.
     * @return the {@link Tile} in the specified direction, or
     * {@code null} if no tile found.
     */
    private Tile findTile(final Direction direction) {
        return switch (direction) {
            case UP -> getPosition().getUp();
            case DOWN -> getPosition().getDown();
            case LEFT -> getPosition().getLeft();
            case RIGHT -> getPosition().getRight();
            default -> null;
        };
    }

    /**
     * Determines the fly's hand direction.
     *
     * @param turnRight {@code True} if the fly follows right hand side.
     * {@code False} if left hand side.
     * @return The Fly's hand direction.
     */
    private Direction findHand(final boolean turnRight) {
        int currentDirIndex = getDirectionIndex(getCurrentDirection());
        currentDirIndex += turnRight ? 1 : -1;
        return CARDINAL_DIRECTIONS[
                (currentDirIndex + CARDINAL_DIRECTIONS.length)
                        % CARDINAL_DIRECTIONS.length];
    }

    /**
     * Retrieves the index of the specified direction in the array.
     * @param direction is the {@link Direction} to find.
     * @return the index of the direction, or 0 if not found.
     */
    private int getDirectionIndex(final Direction direction) {
        for (int index = 0; index < CARDINAL_DIRECTIONS.length; index++) {
            if (CARDINAL_DIRECTIONS[index] == direction) {
                return index;
            }
        }
        return 0;
    }

    /**
     * This is the method to write the object's details in the desired
     * string format.
     *
     * @return The Fly's details. */
    public String toString() {
        String symbol = isButtery() ? "BF" : "F";
        return
                symbol + ","
                        + getPosition().getRow()
                        + "," + getPosition().getColumn()
                        + "," + isButtery() + ","
                        + getCurrentDirection().toString();
    }
}
