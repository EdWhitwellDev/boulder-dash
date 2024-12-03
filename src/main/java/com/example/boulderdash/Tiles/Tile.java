package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.GameState;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;
import com.example.boulderdash.GameState;

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
    protected boolean isKey = false;
    protected boolean isLockedDoor = false;
    private String type;


    public Tile(int row, int col, boolean isPath){
        this.row = row;
        this.column = col;
        this.isPath = isPath;
        this.type = "Tile";
        this.occupier = null;
    }

    public Image getImage(){
        return image;
    }
    public void setImage(Image image) {
        this.image = image;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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
        occupied = occupier != null;
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
            if (up.isPath() && !up.isOccupied()){
                paths.add(up);
            }
        }
        if (down != null){
            if (down.isPath()  && !down.isOccupied()){
                paths.add(down);
            }
        }
        if (left != null){
            if (left.isPath()  && !left.isOccupied()){
                paths.add(left);
            }
        }
        if (right != null){
            if (right.isPath()  && !right.isOccupied()){
                paths.add(right);
            }
        }
        return paths;
    }

    public List<Tile> getAdjacentHorizontal(){
        return List.of(new Tile[]{left, this, right});
    }
    public List<Tile> get3x3(){
        List<Tile> surrounding = new ArrayList<>(this.getAdjacentHorizontal());
        if (up != null) {
            surrounding.addAll(up.getAdjacentHorizontal());
        }
        if (down != null) {
            surrounding.addAll(down.getAdjacentHorizontal());
        }
        return surrounding;
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
    public Tile destroy() {
        if (occupier != null) {
            GameState.manager.killActor(occupier);
        }

        Tile remains = new Floor(row, column, true);
        GameState.level.replaceTile(remains, this);
        return remains;
    }

    public void setPath(boolean isPath) {
        this.isPath = isPath;
    }
}
