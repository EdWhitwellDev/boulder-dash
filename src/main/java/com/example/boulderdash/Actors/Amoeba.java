package com.example.boulderdash.Actors;

import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.Tiles.Tile;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Amoeba in the Boulderdash game. The Amoeba grows into adjacent tiles
 * at a specified growth rate. If blocked, it stops growing. It can transform into Diamonds.
 */
public class Amoeba extends Actor {

    /** Number of ticks required between growth attempts. */
    private int growthRate;

    /** True if the Amoeba is surrounded and cannot grow. */
    private boolean isBlocked;

    /**
     * Constructs an Amoeba with a starting tile and growth rate.
     *
     * @param startTile  The initial tile where the Amoeba starts.
     * @param growthRate The number of ticks required between growth attempts.
     */
    public Amoeba(Tile startTile, int growthRate) {
        super(startTile);  // Call the Actor constructor with the starting tile
        this.image = new Image("Actor Images/amoeba.png");  // Set the Amoeba image path
        this.growthRate = growthRate;
        this.tickCoolDown = growthRate;
        this.isBlocked = false;
    }

    /**
     * Handles the Amoeba's behavior during each game tick.
     * If the tick cooldown is complete and the Amoeba is not blocked, it attempts to grow.
     */
    @Override
    public void move() {
        if (tickCoolDown > 0) {
            tickCoolDown--;
            return;  // Wait until the cooldown reaches zero
        }

        if (!isBlocked) {
            grow();
        }

        tickCoolDown = growthRate;
    }

    /**
     * Attempts to grow the Amoeba into a random adjacent tile.
     * If no available tiles are found, marks the Amoeba as blocked.
     */
    private void grow() {
        List<Tile> availableGrowthTiles = getAvailableGrowthTiles();

        if (availableGrowthTiles.isEmpty()) {
            isBlocked = true;
            System.out.println("Amoeba is blocked and can no longer grow!");
        } else {
            Tile growthTile = availableGrowthTiles.get((int) (Math.random() * availableGrowthTiles.size()));
            Amoeba newAmoeba = new Amoeba(growthTile, this.growthRate);
            growthTile.setOccupier(newAmoeba);

            System.out.println("Amoeba grew to row " + growthTile.getRow() + ", column " + growthTile.getColumn());
        }
    }

    /**
     * Retrieves a list of adjacent tiles where the Amoeba can grow.
     *
     * @return A list of tiles that are valid for Amoeba growth.
     */
    private List<Tile> getAvailableGrowthTiles() {
        List<Tile> availableTiles = new ArrayList<>();
        Tile[] adjacentTiles = {position.getUp(), position.getDown(), position.getLeft(), position.getRight()};

        for (Tile tile : adjacentTiles) {
            if (tile != null && canGrowInto(tile)) {
                availableTiles.add(tile);
            }
        }
        return availableTiles;
    }

    /**
     * Checks if the Amoeba can grow into a specified tile.
     *
     * @param tile The tile to check.
     * @return True if the tile is valid for growth, false otherwise.
     */
    private boolean canGrowInto(Tile tile) {
        return tile != null && (!tile.isOccupied() || tile.getOccupier() instanceof Player);
    }

    /**
     * Transforms the Amoeba into a Diamond on its current tile.
     */
    public void transformToDiamonds() {
        System.out.println("Amoeba transforming into diamonds!");
        position.setOccupier(new Diamond(position));
    }

    /**
     * Overrides the changePos method from the Actor class.
     * Amoebas do not move like other actors, so this method does nothing.
     *
     * @param nextPos The tile to move to (not used).
     */
    @Override
    public void changePos(Tile nextPos) {
        // Amoeba does not "move" like normal actors; instead, it grows
    }
    public String toString(){
        return "A" + "," + position.getRow() + "," + position.getColumn();
    }
}