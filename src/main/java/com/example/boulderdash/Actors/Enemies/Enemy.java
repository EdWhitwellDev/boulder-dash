package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Explosion;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.Tiles.TitaniumWall;

import java.util.List;

/**
 *
 * Class to represent all types of Enemy in the game, subclass of Actor.
 * It inherits basic movement and position handling from the Actor class.
 * Enemies can interact with their surroundings.
 */
public class Enemy extends Actor {

    /** */
    private boolean crushed = false;

    /** */
    private boolean buttery = false;

    /**
     * Creates a new Enemy at the specified starting position.
     *
     * @param startPosition The tile where the enemy will initially appear.
     */
    public Enemy(final Tile startPosition) {
        super(startPosition);
    }

    //Accessor Methods for buttery and crushed
    /**
     * This is an accessor method to check if the Enemy has been crushed.
     *
     * @return If the Enemy has been crushed, or not.
     * */
    public boolean isCrushed() {
        return crushed;
    }

    /**
     * This is an accessor method to check if a 'Fly' Enemy is a Firefly or
     * a Butterfly.
     *
     * @return If the Fly Enemy is a Butterfly or not, or not.
     * */
    public boolean isButtery() {
        return buttery;
    }

    //Mutator methods for buttery and crushed
    /**
     * This is a mutator method to set whether the Enemy has been crushed.
     *
     * @param ifIsCrushed Whether the Enemy has been crushed.
     * */
    protected void setCrushed(final boolean ifIsCrushed) {
        this.crushed = ifIsCrushed;
    }

    /**
     * This is a mutator method to set whether the Fly Enemy is a Butterfly.
     *
     * @param ifIsButtery Whether the Fly Enemy is a Butterfly.
     * */
    protected void setButtery(final boolean ifIsButtery) {
        this.buttery = ifIsButtery;
    }

    /**
     * This method 'crushes' the Enemy by changing the value of the crushed
     * attribute.
     * */
    public void crush() {
        crushed = true;
    }

    /**
     * This method causes the Enemy to explode. It retrieves all surrounding
     * tiles, destroys any tiles that aren't Titanium walls and leaves
     * diamonds in the remaining space.
     */
    public void explode() {
        GameState.getManager().killActor(this);

        List<Tile> surroundingTiles = getPosition().get3x3();
        for (Tile tile : surroundingTiles) {
            if (!(tile instanceof TitaniumWall)) {
                Tile remains = tile.destroy();
                Explosion explosion = new Explosion(remains, buttery);
                GameState.getManager().addActor(explosion);
            }
        }
    }

    /**
     * This method explodes this Enemy object. It creates an explosion,
     * (that results in a change in image) and kills the actor. removing
     * it from the screen.
     * */
    public void explodeSingle() {
        Explosion explosion = new Explosion(getPosition(), false);
        GameState.getManager().addActor(explosion);
        GameState.getManager().killActor(this);
    }
}
