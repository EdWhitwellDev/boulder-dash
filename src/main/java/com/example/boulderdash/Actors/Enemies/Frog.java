package com.example.boulderdash.Actors.Enemies;

import com.example.boulderdash.Actors.Explosion;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

import java.util.*;

public class Frog extends Enemy{
    private Player player;
    private final static int TICK_COOL_DOWN_RESET = 16;

    private int tickCoolDown = 1;


    public Frog(Tile startPosition, Player player) {
        super(startPosition);
        this.player = player;
        this.image = new Image("Actor Images/Frog/frog.png");
    }

    public Frog(Tile startPosition) {
        super(startPosition);
        this.player = null;
        this.image = new Image("Actor Images/Frog/frog.png");
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public void move(){
        if (crushed){
            explode();
        }
        if (tickCoolDown > 0){
            tickCoolDown--;
        }
        else {
            tickCoolDown = TICK_COOL_DOWN_RESET;
            if(!biDirectionalSearch(player.getPosition())){
                Random random = new Random();
                List<Tile> options = position.adjacentPaths();
                if (!options.isEmpty()){
                    Tile nextMove = null;
                    do {
                        int randomPath = random.nextInt(options.size());
                        if (!options.get(randomPath).isOccupied()){
                            nextMove = options.get(randomPath);
                        }
                        options.remove(randomPath);
                    } while (nextMove == null && !options.isEmpty());

                    if (nextMove != null) {
                        currentDirection = changeDirection(nextMove);
                        changePos(nextMove);
                    }

                }
            }
        }
    }

    public boolean biDirectionalSearch(Tile target){
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

            Tile potNextStep = traverse(currentFrogTile, frogQueue, frogParents, playerParents);
            if (potNextStep != null){
                frogParents.put(potNextStep, currentFrogTile);
                nextStep = getNextStep(frogParents, potNextStep);
                break;
            }

            if (playerQueue.isEmpty()) {
                break;
            }
            Tile currentPlayerTile = playerQueue.poll();
            potNextStep = traverse(currentPlayerTile, playerQueue, playerParents, frogParents);
            if (potNextStep != null){
                nextStep = getNextStep(frogParents, potNextStep);
                break;
            }
        }

        if (nextStep != null){
            this.changePos(nextStep);
            return true;
        }
        return false;
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

    public String toString(){
        return "R" + "," + position.getRow() + "," + position.getColumn();
    }

}
