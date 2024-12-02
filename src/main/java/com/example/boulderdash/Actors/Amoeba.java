package com.example.boulderdash.Actors;

import com.example.boulderdash.Tiles.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Amoeba class represents an amoeba entity in the Boulder Dash game.
 * Amoebas can grow into adjacent tiles, following specific rules and growth delay.
 */
public class Amoeba extends Actor {
    private Random random;
    private ImageView imageView;
    private int growthDelay; // Controls how often amoeba grows
    private int growthCounter; // Counts ticks to determine when to grow
    private static final int MAX_GROWTH_SIZE = 100; // Limit on how many amoebas can exist
    private List<Amoeba> connectedAmoebas; // Keeps track of connected amoebas

    /**
     * Constructs an Amoeba object at the given start position.
     * @param startPosition the initial position of the amoeba.
     * @param connectedAmoebas list of all connected amoebas.
     */
    public Amoeba(Tile startPosition, List<Amoeba> connectedAmoebas) {
        super(startPosition);
        this.random = new Random();
        this.growthDelay = 5; // Growth frequency delay
        this.growthCounter = 0;
        this.connectedAmoebas = connectedAmoebas;

        // Load the amoeba image from resources
        this.image = new Image("amoeba.png");
        this.imageView = new ImageView(image);
        updateImageViewPosition(); // Initial positioning

        // Set this tile to occupied and add this amoeba to the connected list
        startPosition.setOccupier(this);
        connectedAmoebas.add(this);
    }

    /**
     * Constructs an Amoeba object at the given start position.
     * @param startPosition the initial position of the amoeba.
     */
    public Amoeba(Tile startPosition) {
        this(startPosition, new ArrayList<>());
    }

    /**
     * Handles the amoeba's movement, including growth.
     */
    @Override
    public void move() {
        if (growthCounter >= growthDelay) {
            grow();
            growthCounter = 0;
        } else {
            growthCounter++;
        }
    }

    /**
     * Grows the amoeba by expanding into a random adjacent valid tile.
     * Limits growth to one tile per cycle to prevent runaway expansion.
     */
    private void grow() {
        List<Tile> availableTiles = getPossibleExpansionTiles();

        if (!availableTiles.isEmpty() && connectedAmoebas.size() < MAX_GROWTH_SIZE) {
            // Select a random tile to grow into
            Tile selectedTile = availableTiles.get(random.nextInt(availableTiles.size()));
            createNewAmoeba(selectedTile);
        }
    }

    /**
     * Gets the possible tiles where the amoeba can expand.
     * @return a list of valid expansion tiles.
     */
    private List<Tile> getPossibleExpansionTiles() {
        List<Tile> moves = new ArrayList<>();
        addIfValid(moves, position.getUp());
        addIfValid(moves, position.getDown());
        addIfValid(moves, position.getLeft());
        addIfValid(moves, position.getRight());
        return moves;
    }

    /**
     * Adds a tile to the list of possible moves if it is valid for expansion.
     * @param moves the list of possible moves.
     * @param tile the tile to be checked and potentially added.
     */
    private void addIfValid(List<Tile> moves, Tile tile) {
        if (tile != null && (tile.isPath() || tile.isDirt()) && !tile.isOccupied()) {
            moves.add(tile);
        }
    }

    /**
     * Creates a new amoeba at the specified target tile.
     * @param targetTile the tile where the new amoeba should be created.
     */
    private void createNewAmoeba(Tile targetTile) {
        if (!targetTile.isOccupied() && connectedAmoebas.size() < MAX_GROWTH_SIZE) {
            Amoeba newAmoeba = new Amoeba(targetTile, connectedAmoebas);
            targetTile.setOccupier(newAmoeba);
            newAmoeba.updateImageViewPosition(); // Set the new amoeba's image position correctly
            connectedAmoebas.add(newAmoeba);
        }
    }

    /**
     * Moves the amoeba to a new position.
     * @param targetTile the target tile to move to.
     */
    private void moveTo(Tile targetTile) {
        if (position != null) {
            position.setOccupier(null);
        }
        targetTile.setOccupier(this);
        this.position = targetTile;
        updateImageViewPosition();
    }

    /**
     * Updates the position of the ImageView to reflect the new coordinates.
     */
    private void updateImageViewPosition() {
        imageView.setX(position.getColumn() * 32); // Assuming each tile is 32x32 pixels
        imageView.setY(position.getRow() * 32);
    }

    /**
     * Gets the ImageView representing the amoeba.
     * @return the ImageView of the amoeba.
     */
    public ImageView getImageView() {
        return imageView;
    }
}
