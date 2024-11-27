package com.example.boulderdash.Actors;

import com.example.boulderdash.Tiles.Tile;
import javafx.scene.image.Image;

public class Actor {
    protected Tile position;
    protected Image image = new Image("diamond.png");

    public Actor(Tile startPosition){
        position = startPosition;
        position.setOccupier(this);
    }

    public Image getImage(){
        return image;
    }

}
