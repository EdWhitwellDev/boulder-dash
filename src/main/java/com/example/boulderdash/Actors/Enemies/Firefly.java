package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Tiles.Tile;

/**
 * Class to represent the Firefly, a subclass of Enemy.
 * The Firefly is a type of enemy that inherits behaviour and properties
 * from the enemy class.
 */
public class Firefly extends Enemy {

    /**
     * Creates a new Firefly enemy at the specified starting position.
     *
     * @param startPosition The tile where the Firefly will initially appear.
     */
    public Firefly(final Tile startPosition) {
        super(startPosition);
    }
}
