package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Actors.Actor;

import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

public class Boulder extends FallingObject{

    private boolean isPushed = false;
    private int rollDelay = 0;
    private final int rollDelayReset = 3;

    public Boulder(Tile startPosition) {
        super(startPosition);
        image = new Image("boulder.png");
    }

    public boolean push(Direction direction) {
        Tile nextTile = position.getNeighbour(direction); // Gets a direction to push to

        // Checks if the next tile is a path and sets the position of boulder to that path
        if (isAbleToPushTo(nextTile)) {
            setPosition(nextTile);
            return true;
        }
        return false;
    }

    public void transform() {
        GameState.manager.killActor(this);
        GameState.manager.addActor(new Diamond(position));
    }

    public void move() {
        if (!exploded) {
            //Tile underTile = position.getDown();
//
            //if (underTile != null && underTile.isOccupied() && underTile.getOccupier() instanceof Player) {
            //    if (isFalling) {
            //        kill((Player) underTile.getOccupier());
            //    } else {
            //        isFalling = false;
            //        return;
            //    }
            //}
            super.fall();
            if (!isFalling) {
                roll();
            }
        }
    }

    // Ignore for now
    //protected void onPath(Tile underTile) {
    //    if (underTile != null && underTile.isOccupied()) {
    //        Actor occupier = underTile.getOccupier();
//
    //        if (occupier instanceof Player) {
    //            kill((Player) occupier);
    //        }
    //    }
    //}

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

    // Helper for push()
    private boolean isAbleToPushTo(Tile tile) {
        return tile != null && tile.isPath() && !tile.isOccupied();
    }

    // Helper for roll()
    private boolean isAbleToRollTo(Tile tile) {
        return tile != null && tile.isPath() && !tile.isOccupied() && tile.getDown() != null
                && tile.getDown().isPath() && !tile.getDown().isOccupied();
    }

    //private void kill(Player player) {
    //    player.setPosition(null);
    //}
}
