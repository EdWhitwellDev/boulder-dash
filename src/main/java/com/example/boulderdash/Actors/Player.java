package com.example.boulderdash.Actors;

import com.example.boulderdash.Actors.Enemies.Enemy;
import com.example.boulderdash.Actors.Falling.Boulder;
import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.GameManager;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Floor;
import com.example.boulderdash.Tiles.Key;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.Dictionary;
import java.util.HashMap;
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
    private final Map<String, Integer> keyInventory = new HashMap<>();

    public Player(Tile startingTile){
        super(startingTile);
        image = orientation.get(currentDirection);
    }

    public void collectKey(Key key) {
        String colour = key.getColour().toLowerCase();
        if (!keyInventory.containsKey(colour)) {
            keyInventory.put(colour, 0);
        }
        keyInventory.put(colour, keyInventory.get(colour) + 1);
        key.setOccupier(null);
        key.setImage(new Image("path.png"));
    }

    public boolean hasKey(String colour) {
        return keyInventory.containsKey(colour.toLowerCase()) && keyInventory.get(colour.toLowerCase()) > 0;
    }

    public boolean useKey(String keyColour, String doorColour) {
        String newKeyColour = keyColour.toLowerCase();
        String newDoorColour = doorColour.toLowerCase();

        if (hasKey(newKeyColour) && keyColour.equals(newDoorColour)) {
            int newCount = keyInventory.get(newKeyColour) - 1;

            /* makes sure that the keyInventory takes out the key of the colour
            and when count of the inventory is more than one than update the key
            inventory with current number
             */
            if (newCount <= 0) {
                keyInventory.remove(newKeyColour);
            } else {
                keyInventory.put(newDoorColour, newCount);
            }
            return true;
        }
        return false;
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
        diamondsCollected++;
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

    private void collectDiamond(Diamond diamond) {
        collectedDiamond();
        diamond.setPosition(null);
        GameState.level.removeActor(diamond);
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
                    collectDiamond((Diamond) occupier);
                } else if (occupier instanceof Boulder boulder) {
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
                tickCoolDown = tickCoolDownReset;
            }
        }
    }
}
