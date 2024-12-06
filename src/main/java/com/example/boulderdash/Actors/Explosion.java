package com.example.boulderdash.Actors;

import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import javafx.scene.image.Image;

import java.util.Random;

/**
 * Represents an explosion which destroys the current tile upon completion.
 * @author Ed Whitwell
 */
public class Explosion extends Actor {
    private final static int LIFE_TIME = 9;
    private final boolean dropDiamond;
    private final static Image[] EXPLOSION_IMGS = new Image[]{
            new Image("Actor Images/Explosions/explosion_m.png"),
            new Image("Actor Images/Explosions/explosion_l.png"),
            new Image("Actor Images/Explosions/explosion_s.png"),
            new Image("Actor Images/Explosions/explosion_m_2.png"),
            new Image("Actor Images/Explosions/explosion_l_2.png"),
            new Image("Actor Images/Explosions/explosion_s_2.png")
    };
    private int ticksOld;

    /**
     * Constructor for an explosion at the specified location.
     * @param startPosition is the {@link Tile} where the explosion occurs.
     * @param dropsDiamond {@code True} if the explosion should leave a diamond after the explosion.
     */
    public Explosion(Tile startPosition, boolean dropsDiamond){
        super(startPosition);
        dropDiamond = dropsDiamond;
        this.image = getExplosionVariant();
        this.ticksOld = 0;
    }

    /**
     * Updates the state of the explosion.
     */
    public void move(){
        ticksOld++;
        if (ticksOld == LIFE_TIME) {
            GameState.manager.killActor(this);
            if (dropDiamond){
                Tile remains = position.destroy();
                Diamond drops = new Diamond(remains);
                GameState.manager.addActor(drops);
            }
        }
        image = getExplosionVariant();
    }

    /**
     * Retrieves a random explosion image for the animation.
     * @return a random {@link Image}.
     */
    private Image getExplosionVariant(){
        Random random = new Random();
        int randomNum = random.nextInt(EXPLOSION_IMGS.length);
        return EXPLOSION_IMGS[randomNum];
    }
}
