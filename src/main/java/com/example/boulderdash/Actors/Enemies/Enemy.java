package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Explosion;
import com.example.boulderdash.GameManager;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.Tiles.TitaniumWall;

import java.util.ArrayList;
import java.util.List;

public class Enemy extends Actor {
    protected boolean crushed = false;
    protected boolean buttery = false;
    public Enemy(Tile startPosition){
        super(startPosition);
    }

    public void crush(){
        crushed = true;
    }

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
