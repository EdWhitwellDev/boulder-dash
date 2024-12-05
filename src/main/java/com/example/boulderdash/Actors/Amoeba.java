package com.example.boulderdash.Actors;

import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.Tiles.Tile;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class Amoeba extends Actor {

    private int growthRate;       // Number of ticks required between growth attempts
    private boolean isBlocked;    // True if the amoeba is surrounded and cannot grow

    public Amoeba(Tile startTile, int growthRate) {
        super(startTile);  // Call the Actor constructor with the starting tile
        this.image = new Image("amoeba.png");  // Set the amoeba image path
        this.growthRate = growthRate;
        this.tickCoolDown = growthRate;
        this.isBlocked = false;
    }

    @Override
    public void move() {
        // Use tickCoolDown to control the growth rate
        if (tickCoolDown > 0) {
            tickCoolDown--;
            return;  // Wait until the cooldown reaches zero
        }

        // If cooldown is over, attempt to grow
        if (!isBlocked) {
            grow();
        }

        // Reset tick cooldown for the next growth cycle
        tickCoolDown = growthRate;
    }

    private void grow() {
        List<Tile> availableGrowthTiles = getAvailableGrowthTiles();

        if (availableGrowthTiles.isEmpty()) {
            // If no available tiles, mark the amoeba as blocked
            isBlocked = true;
            System.out.println("Amoeba is blocked and can no longer grow!");
        } else {
            // Choose a random tile to grow into
            Tile growthTile = availableGrowthTiles.get((int) (Math.random() * availableGrowthTiles.size()));

            // Create a new Amoeba on the new tile using the correct constructor
            Amoeba newAmoeba = new Amoeba(growthTile, this.growthRate);
            growthTile.setOccupier(newAmoeba);

            System.out.println("Amoeba grew to row " + growthTile.getRow() + ", column " + growthTile.getColumn());
        }
    }

    // Helper method to get all available tiles where the amoeba can grow
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

    // Determine if the amoeba can grow into a particular tile
    private boolean canGrowInto(Tile tile) {
        return !tile.isOccupied() || tile.getOccupier() instanceof Player;
    }

    // Method to transform all amoebas into diamonds
    public void transformToDiamonds() {
        System.out.println("Amoeba transforming into diamonds!");

        // Replace the amoeba in the current tile with a Diamond
        position.setOccupier(new Diamond(position));
    }

    @Override
    public void changePos(Tile nextPos) {
        // Amoeba does not "move" like normal actors; instead, it grows
        // This method should not be used directly but is inherited from Actor
    }
}