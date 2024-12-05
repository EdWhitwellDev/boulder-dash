package com.example.boulderdash;

import com.example.boulderdash.Actors.Actor;

import com.example.boulderdash.Actors.Amoeba;


import com.example.boulderdash.Actors.Enemies.Fly;
import com.example.boulderdash.Actors.Enemies.Frog;

import com.example.boulderdash.Actors.Falling.Boulder;
import com.example.boulderdash.Actors.Falling.Diamond;


import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Tiles.*;
import com.example.boulderdash.enums.Direction;
import com.example.boulderdash.enums.KeyColours;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Level {
    private final List<List<Tile>> tiles;
    private final List<Actor> actors;
    private Player player;
    private final int rows;
    private final int cols;
    private int timeLimit;
    private int diamondsRequired;
    private Boulder boulder;

    public Level() {
        tiles = new ArrayList<>();
        actors = new ArrayList<>();

        readLevelFile(1);

        rows = tiles.size();
        cols = tiles.get(0).size();

        setNeighbors();


    }

    private void readLevelFile(int levelNum){
        String levelFile = "Level" + levelNum + ".txt";
        Dictionary<String, List<String>> levelSections = new Hashtable<>();
        String currentSection = "";
        try (InputStream inputStream = Level.class.getClassLoader().getResourceAsStream(levelFile))
        {
            if (inputStream == null){
                throw new IllegalArgumentException("File not found");
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("[") && line.endsWith("]")) {
                        currentSection = line.substring(1, line.length() - 1);
                    } else {
                        List<String> currentList = levelSections.get(currentSection);
                        if (currentList == null) {
                            currentList = new ArrayList<>();
                        }
                        currentList.add(line);

                        levelSections.put(currentSection, currentList);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error wtf");
            e.printStackTrace();
        }

        readTiles(levelSections.get("TILES"));
        readActors(levelSections.get("ACTORS"));
        String[] winConditions = levelSections.get("WIN CONDITIONS").get(0).split(",");
        diamondsRequired = Integer.parseInt(winConditions[0]);
        timeLimit = Integer.parseInt(winConditions[1]);
        System.out.println(actors);
    }

    private void readTiles(List<String> tileStrings) {
        int rowIndex = 0;
        int colIndex = 0;

        for (String line : tileStrings) {
            List<Tile> row = new ArrayList<>();
            String[] tileSymbols = line.split(",");
            for (String symbol : tileSymbols) {
                switch (symbol) {
                    case "P":
                        row.add(new Floor(rowIndex, colIndex, true));
                        break;
                    case "D":
                        row.add(new Floor(rowIndex, colIndex, false));
                        break;
                    case "N":
                        row.add(new NormalWall(rowIndex, colIndex));
                        break;
                    case "T":
                        row.add(new TitaniumWall(rowIndex, colIndex));
                        break;
                    case "M":
                        row.add(new MagicWall(rowIndex, colIndex));
                        break;
                    case "E":
                        row.add(new Exit(rowIndex, colIndex));
                        break;
                    case "R":
                        row.add(new LockedDoor(rowIndex, colIndex, KeyColours.RED));
                        break;
                    case "G":
                        row.add(new LockedDoor(rowIndex, colIndex, KeyColours.GREEN));
                        break;
                    case "B":
                        row.add(new LockedDoor(rowIndex, colIndex, KeyColours.BLUE));
                        break;
                    case "Y":
                        row.add(new LockedDoor(rowIndex, colIndex, KeyColours.YELLOW));
                        break;
                    case "r":
                        row.add(new Key(rowIndex, colIndex, KeyColours.RED));
                        break;
                    case "g":
                        row.add(new Key(rowIndex, colIndex, KeyColours.GREEN));
                        break;
                    case "b":
                        row.add(new Key(rowIndex, colIndex, KeyColours.BLUE));
                        break;
                    case "y":
                        row.add(new Key(rowIndex, colIndex, KeyColours.YELLOW));
                        break;
                    default:
                        break;

                }
                colIndex++;
            }
            tiles.add(row);
            rowIndex++;
            colIndex = 0;
        }
    }

    private void readActors(List<String> actorStrings){
        for (String line : actorStrings) {
            String[] actorInfo = line.split(",");
            System.out.println(line);

            String actorType = actorInfo[0];
            int row = Integer.parseInt(actorInfo[1]);
            int col = Integer.parseInt(actorInfo[2]);
            Tile startTile = tiles.get(row).get(col);
            if (startTile == null) {
                continue;
            }
            if (!startTile.isPath() && !actorType.equals("P")) {
                continue;
            }
            System.out.println(line);
            switch (actorType) {
                case "P":
                    actors.add(new Player(startTile));
                    break;
                case "A":
                    actors.add(new Amoeba(startTile, Integer.parseInt(actorInfo[3])));
                    break;
                case "F":
                    actors.add(new Fly(startTile, Boolean.parseBoolean(actorInfo[3]), false, getDirectionFromString(actorInfo[4])));
                    break;
                case "BF":
                    actors.add(new Fly(startTile, Boolean.parseBoolean(actorInfo[3]), true, getDirectionFromString(actorInfo[4])));
                    break;
                case "D":
                    actors.add(new Diamond(startTile));
                    break;
                case "B":
                    actors.add(new Boulder(startTile));
                    break;
                case "R":
                    actors.add(new Frog(startTile, player));
                    break;
                default:
                    break;
            }
        }
        player = (Player) actors.stream().filter(actor -> actor instanceof Player).findFirst().orElse(null);
        if (player == null){
            throw new IllegalArgumentException("No player found");
        }
    }

    private Direction getDirectionFromString(String direction){
        return switch (direction) {
            case "UP" -> Direction.UP;
            case "DOWN" -> Direction.DOWN;
            case "LEFT" -> Direction.LEFT;
            case "RIGHT" -> Direction.RIGHT;
            default -> Direction.STATIONARY;
        };
    }

    private void setNeighbors() {
        int rows = tiles.size();
        int cols = tiles.get(0).size();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Tile current = tiles.get(i).get(j);

                // Set the 'up' neighbor
                if (i > 0) {
                    current.setUp(tiles.get(i - 1).get(j));
                }

                // Set the 'down' neighbor
                if (i < rows - 1) {
                    current.setDown(tiles.get(i + 1).get(j));
                }

                // Set the 'left' neighbor
                if (j > 0) {
                    current.setLeft(tiles.get(i).get(j - 1));
                }

                // Set the 'right' neighbor
                if (j < cols - 1) {
                    current.setRight(tiles.get(i).get(j + 1));
                }
            }
        }
    }

    public Player getPlayer(){
        return player;
    }
    public int getDiamondsRequired(){
        return diamondsRequired;
    }
    public int getCols() {
        return cols;
    }
    public int getRows() {
        return rows;
    }
    public List<Actor> getActors() {
        return actors;
    }
    public void removeActor(Actor actorToRemove) {
        actors.remove(actorToRemove);
        if (actorToRemove.getPosition() != null && actorToRemove.getPosition().getOccupier() == actorToRemove){
            actorToRemove.getPosition().setOccupier(null);
        }
    }
    public void addActors(List<Actor> actor) {
        actors.addAll(actor);
    }

    public void replaceTile(Tile newTile, Tile oldTile){
        if (oldTile.getDown() != null) {
            oldTile.getDown().setUp(newTile);
        }
        if (oldTile.getUp() != null) {
            oldTile.getUp().setDown(newTile);
        }
        if (oldTile.getLeft() != null) {
            oldTile.getLeft().setRight(newTile);
        }
        if (oldTile.getRight() != null) {
            oldTile.getRight().setLeft(newTile);
        }

        newTile.setLeft(oldTile.getLeft());
        newTile.setRight(oldTile.getRight());
        newTile.setUp(oldTile.getUp());
        newTile.setDown(oldTile.getDown());

        tiles.get(oldTile.getRow()).set(oldTile.getColumn(), newTile);
    }

    public List<List<Tile>> getTiles() {
        return tiles;
    }
}