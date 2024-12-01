package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Tiles.Tile;

import java.util.ArrayList;
import java.util.List;

public class Enemy extends Actor {
    public Enemy(Tile startPosition){
        super(startPosition);
    }

    public void explode(){
        List<Tile> surroundingTiles = position.get3x3();
        for (Tile tile : surroundingTiles){
        }
    }
}
