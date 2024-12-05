package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Explosion;
import com.example.boulderdash.GameManager;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.Tiles.TitaniumWall;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Class to represent all types of Enemy in the game, subclass of Actor. It inherits basic movement and
 * position handling from the Actor class. Enemies can interact with their surroundings.
 */

public class Enemy extends Actor {
    protected boolean crushed = false;
    protected boolean buttery = false;

    /**
     * Creates a new Enemy at the specified starting position.
     *
     * @param startPosition The tile where the enemy will initially appear.
     */
    public Enemy(Tile startPosition){
        super(startPosition);
    }

    /**
     * This method 'crushes' the Enemy by changing the value of the crushed attribute.
     * */
    public void crush(){
        crushed = true;
    }

    /**
     * This method causes the Enemy to explode. It retrieves all surrounding tiles,
     * destroys any tiles that aren't Titanium walls and leaves diamonds in the remaining space.
     */
    public void explode(){
        GameState.manager.killActor(this);

        List<Tile> surroundingTiles = position.get3x3();
        for (Tile tile : surroundingTiles){
            if (!(tile instanceof TitaniumWall)) {
                Tile remains = tile.destroy();
                Explosion explosion = new Explosion(remains, buttery);
                GameState.manager.addActor(explosion);
            }
        }
    }
}
