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

        while (!frogQueue.isEmpty() && !playerQueue.isEmpty()){
            Tile currentFrogTile = frogQueue.poll();

            nextStep = traverse(currentFrogTile, frogQueue, frogParents, playerParents);
            if (nextStep != null){
                frogParents.put(nextStep, currentFrogTile);
                nextStep = getNextStep(frogParents, nextStep);
                break;
            }

            if (playerQueue.isEmpty()) {
                break;
            }
            Tile currentPlayerTile = playerQueue.poll();
            nextStep = traverse(currentPlayerTile, playerQueue, playerParents, frogParents);
            if (nextStep != null){
                nextStep = getNextStep(frogParents, nextStep);
                break;
            }
        }

        if (nextStep != null){
            this.changePos(nextStep);
        }
    }

    private Tile traverse(Tile currentPath, Queue<Tile> queue,
                          Dictionary<Tile, Tile> thisParents, Dictionary<Tile, Tile> otherParents) {
        List<Tile> nextPaths = currentPath.adjacentPaths();
        for (Tile nextPath : nextPaths){
            if (otherParents.get(nextPath) != null) {
                return nextPath;
            } else if (thisParents.get(nextPath) == null) {
                thisParents.put(nextPath, currentPath);
                queue.add(nextPath);
            }

        }
        return null;
    }


    private Tile getNextStep(Dictionary<Tile, Tile> frogPath, Tile intersection){
        Tile nextStep = intersection;
        while (frogPath.get(nextStep) != this.position){
            nextStep = frogPath.get(nextStep);
        }

        currentDirection = changeDirection(nextStep);
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
