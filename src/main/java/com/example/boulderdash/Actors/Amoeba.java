package com.example.boulderdash.Actors;

import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.Actors.Falling.Boulder;
import com.example.boulderdash.Actors.Falling.FallingObject;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Floor;
import com.example.boulderdash.Tiles.Tile;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents an Amoeba in the Boulderdash game.
 * The Amoeba grows into adjacent tiles at a specified growth rate.
 * If it becomes surrounded, it stops growing.
 * If the cluster of Amoebas reaches a certain size, it transforms into
 * Diamonds.
 *
 * @author Dylan Court & Ed Whitwell
 * @version 1.4
 */
public class Amoeba extends Actor {

    //Variables
    /** List of all connected Amoebas in the same cluster. */
    private List<Amoeba> cluster = new ArrayList<>();

    /** Number of ticks required between growth attempts. */
    private int growthRate;

    /**
     * Indicates whether the Amoeba is blocked from further growth.
     * */
    private boolean isBlocked;

    /**
     * The maximum number of Amoebas allowed in a cluster before
     * transformation occurs.
     * */
    private int maxSize;

    /**
     * Indicates whether the cluster limit has been reached.
     * */
    private boolean clusterLimitReached = false;

    /**
     * Indicates whether the cluster has grown during the current tick.
     * */
    private boolean clusterGrown = false;

    //Constructors
    /**
     * Constructs an Amoeba with a starting tile, growth rate, and
     * maximum cluster size.
     *
     * @param startTile  The initial tile where the Amoeba starts.
     * @param newGrowthRate The number of ticks required between growth
     *                   attempts.
     * @param newMaxSize    The maximum number of Amoebas in the cluster
     *                   before transformation.
     */
    public Amoeba(final Tile startTile, final int newGrowthRate,
                  final int newMaxSize) {

        super(startTile);
        setImage(new Image("Actor Images/amoeba.png"));
        this.growthRate = newGrowthRate;
        this.maxSize = newMaxSize;
        setTickCoolDown(newGrowthRate);
        this.isBlocked = false;
    }

    //Methods
    /**
     * Handles the Amoeba's behavior during each game tick.
     * If the tick cooldown is complete and the Amoeba is not blocked,
     * it attempts to grow.
     * If the cluster reaches the maximum size, it transforms into
     * Diamonds or Boulders.
     */
    @Override
    public void move() {
        if (this.getTickCoolDown() > 0) {
            setTickCoolDown(this.getTickCoolDown() - 1);
            return;
        }

        if (clusterGrown) {
            clusterGrown = false;
            setTickCoolDown(growthRate);
            return;
        }

        cluster = getAmoebaCluster(cluster);

        if (cluster.size() >= maxSize) {
            cluster.forEach(a -> a.transform(true));
            return;
        }

        List<Amoeba> unblockedCluster = getUnblockedCluster();

        if (unblockedCluster.isEmpty()) {
            cluster.forEach(a -> a.transform(false));
            return;
        }

        growRandomAmoeba(unblockedCluster);
    }

    /**
     * Transforms the Amoeba into a Diamond or Boulder on its current
     * tile.
     *
     * @param isBoulder Indicates whether to transform into a
     *                  Boulder (true) or a Diamond (false).
     */
    public void transform(final Boolean isBoulder) {
        System.out.println("Amoeba transformed at: "
                + getPosition().getRow() + ","
                + getPosition().getColumn());

        FallingObject fallingObject =
                isBoulder ? new Boulder(getPosition())
                        : new Diamond(getPosition());
        getPosition().setIsPath(true);
        getPosition().setOccupier(fallingObject);

        GameState.getManager().addActor(fallingObject);
        GameState.getManager().killActor(this);
    }

    /**
     * Sets whether the cluster has grown during the current tick.
     *
     * @param newClusterGrown True if the cluster has grown, false otherwise.
     */
    public void setClusterGrown(final boolean newClusterGrown) {
        this.clusterGrown = newClusterGrown;
    }

    //toString
    /**
     * Returns a string representation of the Amoeba, including its position.
     *
     * @return A string representation of the Amoeba.
     */
    public String toString() {
        return "A," + getPosition().getRow() + "," + getPosition().getColumn();
    }

    /**
     * Attempts to grow the Amoeba into a random adjacent tile.
     */
    private void grow() {
        List<Tile> availableGrowthTiles = getAvailableGrowthTiles();

        if (availableGrowthTiles.isEmpty()) {
            isBlocked = true;
            System.out.println(
                    "Amoeba is blocked and can no longer grow!");
        } else {

            Tile growthTile = availableGrowthTiles.get((int) (
                    Math.random() * availableGrowthTiles.size()));

            Amoeba newAmoeba = new Amoeba(growthTile, growthRate, maxSize);
            growthTile.setOccupier(newAmoeba);
            GameState.getManager().addActor(newAmoeba);
        }
    }

    /**
     * Gets the list of adjacent tiles, from the current tile it's in,
     * in which the Amoeba
     * can grow.
     *
     * @return A list of tiles that are valid for Amoeba growth.
     */
    private List<Tile> getAvailableGrowthTiles() {
        List<Tile> availableTiles = new ArrayList<>();
        for (Tile tile : getAdjacentTiles()) {
            if (canGrowInto(tile)) {
                availableTiles.add(tile);
            }
        }
        return availableTiles;
    }

    /**
     * Checks if the Amoeba is blocked from further growth.
     */
    private void checkIfBlocked() {
        isBlocked = true;
        for (Tile tile : getAdjacentTiles()) {
            if (tile != null && tile.getClass() == Floor.class
                    && !tile.isOccupied()) {
                isBlocked = false;
            }
        }
    }

    /**
     * Recursively collects all Amoebas connected to this one.
     *
     * @param newCluster The current list of connected Amoebas.
     * @return The updated list of connected Amoebas.
     */
    private List<Amoeba> getAmoebaCluster(
            final List<Amoeba> newCluster) {

        if (newCluster.contains(this)) {
            return newCluster;
        }

        newCluster.add(this);
        checkIfBlocked();

        for (Tile tile : getAdjacentTiles()) {
            if (tile != null && tile.isOccupied()
                    && tile.getOccupier() instanceof Amoeba) {

                ((Amoeba) tile.getOccupier()).getAmoebaCluster(newCluster);
            }
        }
        return newCluster;
    }

    /**
     * Gets the unblocked Amoebas from the cluster.
     *
     * @return A list of unblocked Amoebas in the cluster.
     */
    private List<Amoeba> getUnblockedCluster() {
        List<Amoeba> unblockedCluster = new ArrayList<>();
        for (Amoeba amoeba : cluster) {
            if (!amoeba.isBlocked) {
                unblockedCluster.add(amoeba);
            }
        }
        return unblockedCluster;
    }

    /**
     * Checks if the Amoeba can grow into a specified tile.
     *
     * @param tile The tile to check.
     * @return True if the tile is valid for growth, false otherwise.
     */
    private boolean canGrowInto(final Tile tile) {

        return tile != null && !tile.isOccupied()
                && tile.getClass() == Floor.class;
    }

    /**
     * Retrieves the adjacent tiles for the Amoeba's current position.
     *
     * @return An array of adjacent tiles.
     */
    private Tile[] getAdjacentTiles() {
        return new Tile[]{getPosition().getUp(),
                getPosition().getDown(),
                getPosition().getLeft(),
                getPosition().getRight()};
    }

    /**
     * Randomly grows one Amoeba from the unblocked cluster.
     *
     * @param unblockedCluster The list of unblocked Amoebas.
     */
    private void growRandomAmoeba(final List<Amoeba> unblockedCluster) {
        Random random = new Random();
        Amoeba amoeba = unblockedCluster.get(
                        random.nextInt(unblockedCluster.size()));
        amoeba.grow();

        cluster.forEach(a -> a.setClusterGrown(true));
    }
}
