package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.*;

public class Frog extends Enemy{
    private static final int TICK_COOL_DOWN_RESET = 16;
    private final Player player;
    private Direction currentDirection;
    private boolean hasPath;
    //private Queue<Tile> playerQueue = new LinkedList<>();
    //private Queue<Tile> frogQueue = new LinkedList<>();
    //private Dictionary<Tile, Tile> frogParents;
    //private Dictionary<Tile, Tile> playerParents;


    private int tickCoolDown = 10;


    public Frog(Tile startPosition, Player player) {
        super(startPosition);
        this.player = player;
        this.image = new Image("frog.png");
    }

    public void move(){
        if (tickCoolDown > 0){
            tickCoolDown--;
        }
        else {
            biDirectionalSearch(player.getPosition());
        }
    }

    public void biDirectionalSearch(Tile target){
        Queue<Tile> playerQueue = new LinkedList<>();
        Queue<Tile> frogQueue = new LinkedList<>();

        Dictionary<Tile, Tile> frogParents = new Hashtable<>();
        Dictionary<Tile, Tile> playerParents = new Hashtable<>();

        frogParents.put(this.position, this.position);
        playerParents.put(target, target);

        frogQueue.add(this.position);
        playerQueue.add(target);

        Tile nextStep = null;

        hasPath = false;

        while (!frogQueue.isEmpty() && !playerQueue.isEmpty() && !hasPath){
            Tile currentFrogTile = frogQueue.poll();
            
            List<Tile> nextPaths = currentFrogTile.adjacentPaths();
            for (Tile nextPath : nextPaths){
                if (playerParents.get(nextPath) != null) {
                    frogParents.put(nextPath, currentFrogTile);
                    nextStep = getNextStep(frogParents, nextPath);
                } else if (frogParents.get(nextPath) == null) {
                    frogParents.put(nextPath, currentFrogTile);
                    frogQueue.add(nextPath);
                }

            }
            if (playerQueue.isEmpty() || hasPath) {
                break;
            }
            Tile currentPlayerTile = playerQueue.poll();
            nextPaths = currentPlayerTile.adjacentPaths();
            for (Tile nextPath : nextPaths){
                if (frogParents.get(nextPath) != null) {
                    nextStep = getNextStep(frogParents, nextPath);
                } else if (playerParents.get(nextPath) == null) {
                    playerParents.put(nextPath, currentPlayerTile);
                    playerQueue.add(nextPath);
                }

            }
        }

        if (nextStep != null){
            this.changePos(nextStep);
        }
    }


    public Tile getNextStep(Dictionary<Tile, Tile> frogPath, Tile intersection){
        Tile nextStep = intersection;
        while (frogPath.get(nextStep) != this.position){
            System.out.println(Integer.toString(nextStep.getRow()) + ", " + Integer.toString(nextStep.getColumn()));
            nextStep = frogPath.get(nextStep);
        }

        currentDirection = changeDirection(nextStep);
        hasPath = true;
        return nextStep;

    }

    private Direction changeDirection(Tile nextTile){
        if (position.getUp() == nextTile){
            return Direction.UP;
        }
        if (position.getDown() == nextTile){
            return Direction.DOWN;
        }
        if (position.getRight() == nextTile){
            return Direction.RIGHT;
        }
        if (position.getLeft() == nextTile){
            return Direction.LEFT;
        }
        return Direction.STATIONARY;
    }

}
