package com.example.boulderdash.Actors.Falling;


import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Tiles.Tile;
import javafx.scene.image.Image;


public class Diamond extends FallingObject {
    private int rollDelay = 0;
    private final int rollDelayReset = 8;

    public Diamond(Tile startPosition) {
        super(startPosition);
        image = new Image("diamond.png");
    }

    public Boulder transformToBoulder() {
        return new Boulder(position);
    }

    public void collect(Player player) {
        player.collectedDiamond(); // Called from Player class (needs to be implemented)
        position.setOccupier(null); // removes the diamond (needs to be implemented)

    }

    public void move() {
        super.fall();
        if (!isFalling) {
            roll();
        }
    }

    private void roll() {
        if (rollDelay > 0) {
            rollDelay--;
            return;
        }
        rollDelay = rollDelayReset;
        Tile leftTile = position.getLeft();
        Tile rightTile = position.getRight();

        if (isAbleToRollTo(leftTile)) {
            setPosition(leftTile);
        } else if (isAbleToRollTo(rightTile)) {
            setPosition(rightTile);
        }
    }

    private boolean isAbleToRollTo(Tile tile) {
        return tile != null && tile.isPath() && !tile.isOccupied() && tile.getDown() != null
                && tile.getDown().isPath() && !tile.getDown().isOccupied();
    }
}