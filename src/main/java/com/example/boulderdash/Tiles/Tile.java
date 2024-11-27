package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import javafx.scene.image.Image;

import java.util.List;

public class Tile {
    protected Tile left;
    protected Tile right;
    protected Tile up;
    protected Tile down;
    protected Image image;
    protected int row;
    protected int column;
    private boolean occupied = false;
    private Actor occupier;

    public Tile(int row, int col){
        this.row = row;
        this.column = col;
    }

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

    public Actor checkAdjacent(){
        if (up != null){
            if (up.isOccupied()){
                return up.getOccupier();
            }
        }
        if (down != null){
            if (down.isOccupied()){
                return down.getOccupier();
            }
        }
        if (left != null){
            if (left.isOccupied()){
                return left.getOccupier();
            }
        }
        if (right != null){
            if (right.isOccupied()){
                return right.getOccupier();
            }
        }
        return null;
    }
}
