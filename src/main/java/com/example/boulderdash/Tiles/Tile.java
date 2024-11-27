package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import javafx.scene.image.Image;

public class Tile {
    protected Tile left;
    protected Tile right;
    protected Tile up;
    protected Tile down;

    protected Image image = new Image("dirt.png");
    private boolean occupied = false;
    private Actor occupier;
    public Image getImage(){
        return image;
    }

    public void setOccupied(boolean occupy){
        occupied = occupy;
    }
    public void setOccupier(Actor occupant){
        occupier = occupant;
        occupied = true;
        if (occupant == null){
            occupied = false;
        }
    }
    public Actor getOccupier(){
        return occupier;
    }
    public boolean isOccupied(){
        return occupied;
    }

    public Tile getUp() {
        return up;
    }

    public Tile getDown() {
        return down;
    }

    public Tile getLeft() {
        return left;
    }

    public Tile getRight() {
        return right;
    }

    public void setDown(Tile down) {
        this.down = down;
    }

    public void setLeft(Tile left) {
        this.left = left;
    }

    public void setRight(Tile right) {
        this.right = right;
    }

    public void setUp(Tile up) {
        this.up = up;
    }
}
