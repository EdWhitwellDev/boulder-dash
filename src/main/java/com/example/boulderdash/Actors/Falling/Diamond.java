package com.example.boulderdash.Actors.Falling;


import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Tiles.Tile;
import javafx.scene.image.Image;


public class Diamond extends FallingObject {

    public Diamond(Tile startPosition) {
        super(startPosition);
        startPosition.setType("Diamond");
        startPosition.setOccupier(this);
        image = new Image("diamond.png");
    }

    public Boulder transformToBoulder() {
        return new Boulder(position);
    }

    public void collect(Player player) {
        player.collectedDiamond(); // Called from Player class (needs to be implemented)
        position.setOccupier(null); // removes the diamond (needs to be implemented)

    }

    public void move() {
        super.fall();
        if (!isFalling) {
            roll();
        }
    }
}
