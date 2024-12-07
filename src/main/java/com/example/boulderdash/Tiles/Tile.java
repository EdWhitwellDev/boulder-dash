package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.GameState;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a tile, the basic unit of space in the game. All square and same size, and
 * can be occupied by Actors (e.g. Player, Butterfly) and affected by events (e.g. Explosion).
 * */
public class Tile {

    // Attributes to hold the Tiles adjacent (Up, Down, Left, Right) from the current
    // tile.
    protected Tile left;
    protected Tile right;
    protected Tile up;
    protected Tile down;

    //Attribute to hold the Image for the tile
    protected Image image;

    //Attributes holding the row and column that the tile is in. (It's coordinates)
    protected int row;
    protected int column;

    //Attribute to represent whether the tile is a path tile or not. Used as determinant
    //in Floor class, but set to default value of 'false' in all other tile classes.
    protected boolean isPath;

    //Attribute to hold the Actor object currently occupying the tile.
    private Actor occupier;

    /**
     * This is the constructor for a Tile. It takes in integer values for
     * a tile's row and column, and a boolean value for whether it is a Path Tile.
     * It also sets the tile to contain no actors upon instantiation.
     *
     * @param row An integer representing the Grid Row that the tile is in.
     * @param col An integer representing the Grid Column that the tile is in.
     * @param isPath The boolean value representing whether a tile is a Path tile.
     */
    public Tile(int row, int col, boolean isPath){
        this.row = row;
        this.column = col;
        this.isPath = isPath;
        this.occupier = null;
    }

    // Accessor methods for the tile object's Image, Occupier, isPath value,
    // Row and Column values, Tiles to the left, right, up and down
    // and to check whether the tile is occupied (boolean).
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

    // Mutator methods for the tile object's Occupier, Tiles to the
    // left, right, up and down and to check whether the tile is
    // occupied (boolean).
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

    /**
     * This method sets a tile's occupier to the one inputted, and sets the tile to be
     * occupied if there is an actor in it.
     *
     * @param occupier The actor occupying the tile
     * */
    public void setOccupier(Actor occupier){
        this.occupier = occupier;
    }

    /**
     * This method stores a list of all the actor objects occupying
     * the tiles adjacent to the current tile.
     *
     * @return The list of all the Actors in the adjacent tiles
     * */
    public List<Actor> checkAdjacent(){
        //Create a new ArrayList of Actors to add to.
        List<Actor> adjacentActors = new ArrayList<>();

        // For each adjacent tile reference (up, down, left, right), it checks if it
        // isn't empty. Then we check if the referenced tile object is occupied. If it is
        // occupied, we add the occupant of the tile to our ArrayList.
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

    /**
     * This method stores a list of all the Path tile's that are adjacent to our
     * current tile.
     *
     * @return The list of all the adjacent Tile Objects that are Path tiles.
     * */
    public List<Tile> adjacentPaths(){
        //Create a new ArrayList of Tiles to add to.
        List<Tile> paths = new ArrayList<>();

        // For each adjacent tile reference(up, down, left, right), it checks if it
        // isn't empty. Then we check if the referenced tile object is a Path tile (isPath == true)
        // and is unoccupied. If both conditions are met, we add the tile to our ArrayList.
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

    /**
     * This method creates a list of the Tiles on either side of the current tile,
     * as well ass the current tile itself. (leftTile - thisTile - rightTile)
     *
     * @return An immutable list of Tiles horizontally adjacent to the current
     * tile.
     * */
    public List<Tile> getAdjacentHorizontal(){
        return List.of(new Tile[]{left, this, right});
    }

    /**
     * This method allows one to get the 3x3 block of tiles containing the current tile
     * at its epicentre. It adds each row of the block to one big list.
     *
     * @return An immutable list of Tiles lists, each containing a centre tile and
     * those horizontally adjacent to it.
     * */
    public List<Tile> get3x3(){

        // Create the Big list and add to it, a smaller list of the current tile and
        // those horizontally adjacent to it.
        List<Tile> surrounding = new ArrayList<>(this.getAdjacentHorizontal());

        // Check if there is a tile object referenced above the current tile. If there is,
        // add the list of the 'up' tile and its horizontally adjacent tiles to the
        // Big list. Do the same for the 'down' tile.
        if (up != null) {
            surrounding.addAll(up.getAdjacentHorizontal());
        }
        if (down != null) {
            surrounding.addAll(down.getAdjacentHorizontal());
        }

        //Return a big list of three smaller lists, each containing three tile objects.
        return surrounding;
    }

    // Returns corresponding tile (Needed to push boulder in certain directions)
    /**
     * This method retrieves the tile in a certain direction from the current tile. It
     * makes use of the Direction enum.
     *
     * @param direction This is the direction of the desired tile from the current tile.
     *                 (Direction Enum only)
     * @return The desired Tile
     * */
    public Tile getNeighbour(Direction direction) {
        return switch (direction) {
            case UP -> up;
            case DOWN -> down;
            case LEFT -> left;
            case RIGHT -> right;
            default -> null;
        };
    }

    // Turns tile into path (e.g. when an explosion happens)
    /**
     * This method is to turn a tile (either occupied or unoccupied) into a path. Can be called
     * when an explosion occurs.
     *
     * @return The new path tile replacing the destroyed tile.
     * */
    public Tile destroy() {
        System.out.println("Tile destroyed at: " + row + ", " + column);
        //If the tile is occupied, kills the occupant.
        if (occupier != null) {
            if (occupier instanceof Player) {
                GameState.manager.looseGame("Killed by Explosion");
            }
            GameState.manager.killActor(occupier);
        }

        //Creates a new Path tile, replaces the current tile with this new tile and returns the new tile.
        Tile remains = new Floor(row, column, true);
        GameState.level.replaceTile(remains, this);
        return remains;
    }


}