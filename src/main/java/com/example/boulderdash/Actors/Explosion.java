package com.example.boulderdash.Actors;

import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import javafx.scene.image.Image;

public class Explosion extends Actor {
    private final static int LIFE_TIME = 8;
    private int ticksOld;
    public Explosion(Tile startPosition) {
        super(startPosition);
        this.image = new Image("explosion.png");
        this.ticksOld = 0;
    }

    public void move(){
        ticksOld++;
        if (ticksOld == LIFE_TIME) {
            GameState.manager.killActor(this);
            Tile remains = position.destroy();
            Diamond drops = new Diamond(remains);
            GameState.manager.addActor(drops);
        }
    }
}
