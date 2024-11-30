package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class Tile {
    protected Tile left;
    protected Tile right;
    protected Tile up;
    protected Tile down;
    protected Image image;
    protected int row;
    protected int column;
    protected boolean isPath = false;
    private boolean occupied = false;

    private Actor occupier;


    public Tile(int row, int col, boolean isPath){
        this.row = row;
        this.column = col;
        this.isPath = isPath;
        this.occupier = null;
    }

    public Image getImage(){
        return image;
    }

    public Actor getOccupier(){
        return occupier;
    }

    public boolean isOccupied(){
        return occupier != null;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
    public boolean isPath() { return isPath; }

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
    public void setOccupied(boolean occupy){
        occupied = occupy;
    }

    public void setOccupier(Actor occupier){
        this.occupier = occupier;
    }

    public List<Actor> checkAdjacent(){
        List<Actor> adjacentActors = new ArrayList<>();

        if (up != null){
            if (up.isOccupied()){
                 adjacentActors.add(up.getOccupier());
            }
        }
        if (down != null){
            if (down.isOccupied()){
                adjacentActors.add(down.getOccupier());
            }
        }
        if (left != null){
            if (left.isOccupied()){
                adjacentActors.add(left.getOccupier());
            }
        }
        if (right != null){
            if (right.isOccupied()){
                adjacentActors.add(right.getOccupier());
            }
        }
        return adjacentActors;
    }

    public List<Tile> adjacentPaths(){
        List<Tile> paths = new ArrayList<>();
        if (up != null){
            if (up.isPath()){
                paths.add(up);
            }
        }
        if (down != null){
            if (down.isPath()){
                paths.add(down);
            }
        }
        if (left != null){
            if (left.isPath()){
                paths.add(left);
            }
        }
        if (right != null){
            if (right.isPath()){
                paths.add(right);
            }
        }
        return paths;
    }

    // Returns corresponding tile (Needed to push boulder in certain directions)
    public Tile getNeighbour(Direction direction) {
        switch (direction) {
            case UP:
                return up;
            case DOWN:
                return down;
            case LEFT:
                return left;
            case RIGHT:
                return right;
            default:
                return null;
        }
    }

    // Turns tile into path (e.g when an explosion happens)
    public void destroy() {
        if (isOccupied()) {
            occupier.setPosition(null);
        }
        isPath = true;
        occupier = null;
    }
}
