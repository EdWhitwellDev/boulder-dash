package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Tiles.Tile;

/**
 * Class to represent the Butterfly, a subclass of Enemy.
 * The Butterfly is a type of enemy that inherits basic behaviour from
 * the Enemy class.
 *
 * @author Ed Whitwell
 */

public class Butterfly extends Enemy {

    /**
     * Creates a new Butterfly Enemy at the specified starting position.
     *
     * @param startPosition The tile where the Butterfly will initially appear.
     */
    public Butterfly(final Tile startPosition) {
        super(startPosition);
    }
}
