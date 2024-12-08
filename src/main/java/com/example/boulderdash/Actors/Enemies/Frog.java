package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Frog extends Enemy {

    /**
     * This is the number of ticks, after moving, before the Frog can
     * move again.
     * */
    private static final int TICK_COOL_DOWN_RESET = 16;
    /**
     * The Player that is being targeted by this Frog.
     * */
    private Player player;
    /**
     * The number of ticks before the Frog can move again.
     * */
    private int tickCoolDown = 1;
    /**
     * Constructor for a Frog *with* a Targeted Player. Set's the
     * Frog's starting position Tile, it's target and it's image.
     *
     * @param startPosition The tile on which the Frog is initially
     *                      located.
     * @param targetedPlayer The player that the Frog is targeting.
     * */
    public Frog(final Tile startPosition, final Player targetedPlayer) {
        super(startPosition);
        this.player = targetedPlayer;
        this.setImage(new Image("Actor Images/Frog/frog.png"));
    }
    /**
     * Constructor for a Frog *without* a Targeted Player. Set's the
     * Frog's starting position Tile and it's image.
     *
     * @param startPosition The tile on which the Frog is initially
     *                      located.
     * */
    public Frog(final Tile startPosition) {
        super(startPosition);
        this.player = null;
        this.setImage(new Image("Actor Images/Frog/frog.png"));
    }

    /**
     * Mutator method to set the Player being targeted by this Frog.
     *
     * @param newTargetedPlayer The Player that the Frog going to target.
     * */
    public void setPlayer(final Player newTargetedPlayer) {
        this.player = newTargetedPlayer;
    }

    /**
     * Method to move the frog to the player by using the shortest route,
     * also accounting for explosions.
     * */
    public void move() {
        if (isCrushed()) {
            explode();
        }
        if (tickCoolDown > 0) {
            tickCoolDown--;
        } else {
            tickCoolDown = TICK_COOL_DOWN_RESET;
            if (!biDirectionalSearch(player.getPosition())) {
                Random random = new Random();
                List<Tile> options = getPosition().adjacentPaths();
                if (!options.isEmpty()) {
                    Tile nextMove = null;
                    do {
                        int randomPath = random.nextInt(options.size());
                        if (!options.get(randomPath).isOccupied()) {
                            nextMove = options.get(randomPath);
                        }
                        options.remove(randomPath);
                    } while (nextMove == null && !options.isEmpty());

                    if (nextMove != null) {
                        setCurrentDirection(changeDirection(nextMove));
                        changePos(nextMove);
                    }

                }
            }
        }
    }

    /**
     * Implements a bidirectional search algorithm to find the shortest path
     * from the current tile to the target tile.
     * @param target is the tile which a path needs to be found.
     * @return {@code True} if a path is found, updating the position.
     * */
    public boolean biDirectionalSearch(final Tile target) {
        Queue<Tile> playerQueue = new LinkedList<>();
        Queue<Tile> frogQueue = new LinkedList<>();

        Dictionary<Tile, Tile> frogParents = new Hashtable<>();
        Dictionary<Tile, Tile> playerParents = new Hashtable<>();

        frogParents.put(this.getPosition(), this.getPosition());
        playerParents.put(target, target);

        frogQueue.add(this.getPosition());
        playerQueue.add(target);

        Tile nextStep = null;

        while (!frogQueue.isEmpty() && !playerQueue.isEmpty()) {
            Tile currentFrogTile = frogQueue.poll();

            Tile potNextStep = traverse(
                    currentFrogTile, frogQueue,
                    frogParents, playerParents);
            if (potNextStep != null) {
                frogParents.put(potNextStep, currentFrogTile);
                nextStep = getNextStep(frogParents, potNextStep);
                break;
            }

            if (playerQueue.isEmpty()) {
                break;
            }
            Tile currentPlayerTile = playerQueue.poll();
            potNextStep = traverse(
                    currentPlayerTile, playerQueue,
                    playerParents, frogParents);
            if (potNextStep != null) {
                nextStep = getNextStep(frogParents, potNextStep);
                break;
            }
        }

        if (nextStep != null) {
            this.changePos(nextStep);
            return true;
        }
        return false;
    }

    /**
     * This is a method to represent a Frog object in the desired
     * string format.
     *
     * @return A string in the format :
     *             R,v1,v2 (where v1 = RowNumber and v2 = ColumnNumber)
     * */
    public String toString() {
        return "R" + ","
                + getPosition().getRow()
                + "," + getPosition().getColumn();
    }

    /**
     * Finds the adjacent paths from the current tile.
     * @param currentPath is the current tile.
     * @param queue for search traversal.
     * @param thisParents is the parent dictionary for the current search side.
     * @param otherParents is the parent dictionary for the other search side.
     * @return the intersecting tile if both searches meet.
     */
    private Tile traverse(final Tile currentPath,
                          final Queue<Tile> queue,
                          final Dictionary<Tile, Tile> thisParents,
                          final Dictionary<Tile, Tile> otherParents) {

        List<Tile> nextPaths = currentPath.adjacentPaths();
        for (Tile nextPath : nextPaths) {
            if (otherParents.get(nextPath) != null) {
                return nextPath;
            } else if (thisParents.get(nextPath) == null) {
                thisParents.put(nextPath, currentPath);
                queue.add(nextPath);
            }

        }
        return null;
    }

    /**
     * Determines the next step in the path.
     * @param frogPath is the path traced by the frog.
     * @param intersection is where both searches meet.
     * @return the next tile to move to.
     */
    private Tile getNextStep(final Dictionary<Tile, Tile> frogPath,
                             final Tile intersection) {

        Tile nextStep = intersection;
        while (frogPath.get(nextStep) != this.getPosition()) {
            nextStep = frogPath.get(nextStep);
        }

        setCurrentDirection(changeDirection(nextStep));
        return nextStep;
    }

    /**
     * Determines which direction to move to based on the next tile.
     * @param nextTile is the tile to move to.
     * @return the direction to face.
     */
    private Direction changeDirection(final Tile nextTile) {
        if (getPosition().getUp() == nextTile) {
            return Direction.UP;
        }
        if (getPosition().getDown() == nextTile) {
            return Direction.DOWN;
        }
        if (getPosition().getRight() == nextTile) {
            return Direction.RIGHT;
        }
        if (getPosition().getLeft() == nextTile) {
            return Direction.LEFT;
        }
        return Direction.STATIONARY;
    }
}
