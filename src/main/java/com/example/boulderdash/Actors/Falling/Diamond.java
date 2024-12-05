package com.example.boulderdash.Actors.Falling;


import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import javafx.scene.image.Image;


public class Diamond extends FallingObject {
    public Diamond(Tile startPosition) {
        super(startPosition);
        image = new Image("diamond.png");
    }

    public void transform() {
        GameState.manager.killActor(this);
        GameState.manager.addActor(new Boulder(position));
    }

    public void move() {
        super.fall();
    }
}