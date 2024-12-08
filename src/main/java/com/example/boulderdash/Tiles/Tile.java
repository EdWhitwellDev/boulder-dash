package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.GameState;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a tile, the basic unit of space in the game. All
 * square and same size, and can be occupied by Actors (e.g. Player, Butterfly)
 * and affected by events (e.g. Explosion).
 *
 * @author Ed Whitwell
 */
public class Tile {

    /**
     * Reference to the tile to the left of the current tile.
     * */
    private Tile left;
    /**
     * Reference to the tile to the right of the current tile.
     * */
    private Tile right;
    /**
     * Reference to the tile above the current tile.
     * */
    private Tile up;
    /**
     * Reference to the tile below the current tile.
     * */
    private Tile down;

    /**
     * Attribute to hold the Image for the tile.
     * */
    private Image image;

    /**
     * The row that the tile is in.
     * */
    private int row;
    /**
     * The column that the tile is in.
     * */
    private int column;

    /**
     * Indicates if the tile is a path
     * in Floor class, but set to default value of 'false'.
     * */
    private boolean isPath;

    /**
     * Attribute to hold the Actor object currently occupying the tile.
     * */
    private Actor occupier;

    /**
     * This is the constructor for a Tile. It takes in integer values for
     * a tile's row and column, and a boolean value for whether it is a
     * Path Tile. It also sets the tile to contain no actors upon
     * instantiation.
     *
     * @param newRow An integer representing the Grid Row that the tile is in.
     * @param newCol An integer representing the Grid Column that the tile is
     *               in.
     * @param ifIsPath The boolean value representing whether a tile is a Path
     *               tile.
     */
    public Tile(final int newRow, final int newCol, final boolean ifIsPath) {
        this.row = newRow;
        this.column = newCol;
        this.isPath = ifIsPath;
        this.occupier = null;
    }

    //Getters
    /**
     * Accessor method for the tile object's Image.
     * @return The Image representing the Tile.
     * */
    public Image getImage() {
        return image;
    }
    /**
     * Accessor method for the tile's occupier.
     * @return The Actor occupying the tile.
     * */
    public Actor getOccupier() {
        return occupier;
    }
    /**
     * Accessor method for whether the tile is occupied.
     * @return Whether the tile is occupied.
     * */
    public boolean isOccupied() {
        return occupier != null;
    }
    /**
     * Accessor method for the tile's row.
     * @return The number of the row which the tile is in
     * */
    public int getRow() {
        return row;
    }
    /**
     * Accessor method for the tile's column.
     * @return The number of the column which the tile is in
     * */
    public int getColumn() {
        return column;
    }
    /**
     * Accessor method for whether the tile is a Path.
     * @return Whether the tile is a Path.
     * */
    public boolean isPath() {
        return isPath;
    }
    /**
     * Accessor method for the tile above the current tile.
     * @return The Tile above a given tile.
     * */
    public Tile getUp() {
        return up;
    }
    /**
     * Accessor method for the tile below the current tile.
     * @return The Tile below a given tile.
     * */
    public Tile getDown() {
        return down;
    }
    /**
     * Accessor method for the tile to the left of the current tile.
     * @return The Tile left of a given tile.
     * */
    public Tile getLeft() {
        return left;
    }
    /**
     * Accessor method for the tile to the right of the current tile.
     * @return The Tile right of a given tile.
     * */
    public Tile getRight() {
        return right;
    }

    //Setters
    /**
     * Mutator methods to set the tile's Image.
     * @param newImage The image to be set to represent the tile.
     * */
    public void setImage(final Image newImage) {
        this.image = newImage;
    }
    /**
     * Mutator methods to set the tile below the current tile.
     * @param downTile The Tile to be referenced below a given tile.
     * */
    public void setDown(final Tile downTile) {
        this.down = downTile;
    }
    /**
     * Mutator methods to set the tile left of the current tile.
     * @param leftTile The Tile to be referenced to the left of a
     *                 given tile.
     * */
    public void setLeft(final Tile leftTile) {
        this.left = leftTile;
    }
    /**
     * Mutator methods to set the tile right of the current tile.
     * @param rightTile The Tile to be referenced to the left of a
     *                   given tile.
     * */
    public void setRight(final Tile rightTile) {
        this.right = rightTile;
    }
    /**
     * Mutator methods to set the tile above the current tile.
     * @param upTile The Tile to be referenced above a given tile.
     * */
    public void setUp(final Tile upTile) {
        this.up = upTile;
    }
    /**
     * Mutator methods to set whether the current tile is a Path tile.
     * @param ifIsPath Whether a given tile is a Path tile.
     * */
    public void setIsPath(final boolean ifIsPath) {
        this.isPath = ifIsPath;
    }
    /**
     * This method sets a tile's occupier to the one inputted, and sets the
     * tile to be occupied if there is an actor in it.
     *
     * @param newOccupier The actor occupying the tile
     */
    public void setOccupier(final Actor newOccupier) {
        this.occupier = newOccupier;
    }

    /**
     * This method stores a list of all the actor objects occupying
     * the tiles adjacent to the current tile.
     *
     * @return The list of all the Actors in the adjacent tiles
     */
    public List<Actor> checkAdjacent() {

        //Create a new ArrayList of Actors to add to.
        List<Actor> adjacentActors = new ArrayList<>();

        // For each adjacent tile reference ,checks if empty.
        // check if referenced tile is occupied. If occupied, we add the
        // occupant.
        if (up != null) {
            if (up.isOccupied()) {
                 adjacentActors.add(up.getOccupier());
            }
        }
        if (down != null) {
            if (down.isOccupied()) {
                adjacentActors.add(down.getOccupier());
            }
        }
        if (left != null) {
            if (left.isOccupied()) {
                adjacentActors.add(left.getOccupier());
            }
        }
        if (right != null) {
            if (right.isOccupied()) {
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
     */
    public List<Tile> adjacentPaths() {
        //Create a new ArrayList of Tiles to add to.
        List<Tile> paths = new ArrayList<>();

        if (up != null) {
            if (up.isPath() && !up.isOccupied()) {
                paths.add(up);
            }
        }
        if (down != null) {
            if (down.isPath()  && !down.isOccupied()) {
                paths.add(down);
            }
        }
        if (left != null) {
            if (left.isPath()  && !left.isOccupied()) {
                paths.add(left);
            }
        }
        if (right != null) {
            if (right.isPath()  && !right.isOccupied()) {
                paths.add(right);
            }
        }
        return paths;
    }

    /**
     * This method creates a list of the Tiles on either side of the current
     * tile, as well ass the current tile itself.
     * (leftTile - thisTile - rightTile)
     *
     * @return An immutable list of Tiles horizontally adjacent to the current
     *          tile.
     */
    public List<Tile> getAdjacentHorizontal() {
        return List.of(new Tile[]{left, this, right});
    }

    /**
     * This method allows one to get the 3x3 block of tiles containing the
     * current tile at its epicentre. It adds each row of the block to one
     * big list.
     *
     * @return An immutable list of Tiles lists, each containing a centre
     * tile and those horizontally adjacent to it.
     */
    public List<Tile> get3x3() {

        // Create the Big list and add to it, a smaller list of the current
        // tile and those horizontally adjacent to it.
        List<Tile> surrounding = new ArrayList<>(this.getAdjacentHorizontal());

        if (up != null) {
            surrounding.addAll(up.getAdjacentHorizontal());
        }
        if (down != null) {
            surrounding.addAll(down.getAdjacentHorizontal());
        }

        return surrounding;
    }

    /**
     * This method retrieves the tile in a certain direction from the current
     * tile. It makes use of the Direction enum.
     *
     * @param direction This is the direction of the desired tile from the
     *                  current tile.(Direction Enum only)
     * @return The desired Tile
     */
    public Tile getNeighbour(final Direction direction) {
        return switch (direction) {
            case UP -> up;
            case DOWN -> down;
            case LEFT -> left;
            case RIGHT -> right;
            default -> null;
        };
    }

    /**
     * This method is to turn a tile (either occupied or unoccupied) into a
     * path. Can be called when an explosion occurs.
     *
     * @return The new path tile replacing the destroyed tile.
     */
    public Tile destroy() {
        //If the tile is occupied, kills the occupant.
        if (occupier != null) {
            if (occupier instanceof Player) {
                GameState.getManager().looseGame("Killed by Explosion");
            }
            GameState.getManager().killActor(occupier);
        }

        // Creates a new Path tile, replaces the current tile with this new
        // tile and returns the new tile.
        Tile remains = new Floor(row, column, true);
        GameState.getLevel().replaceTile(remains, this);
        return remains;
    }


}
