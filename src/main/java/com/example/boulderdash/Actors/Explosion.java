package com.example.boulderdash.Actors;

import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Random;

public class Explosion extends Actor {
    private final static int LIFE_TIME = 9;
    private final boolean dropDiamond;
    private final static Image[] EXPLOSION_IMGS = new Image[]{
            new Image("explosion_m.png"),
            new Image("explosion_l.png"),
            new Image("explosion_s.png"),
            new Image("explosion_m_2.png"),
            new Image("explosion_l_2.png"),
            new Image("explosion_s_2.png")
    };
    private int ticksOld;
    public Explosion(Tile startPosition, boolean dropsDiamond){
        super(startPosition);
        dropDiamond = dropsDiamond;
        this.image = getExplosionVariant();
        this.ticksOld = 0;
    }

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

    private Image getExplosionVariant(){
        Random random = new Random();
        int randomNum = random.nextInt(EXPLOSION_IMGS.length);
        return EXPLOSION_IMGS[randomNum];
    }
}
