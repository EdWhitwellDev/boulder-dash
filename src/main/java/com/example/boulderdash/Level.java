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
import java.util.ArrayList;
import java.util.List;

public class Level {
    private final List<List<Tile>> tiles;
    private final List<Actor> actors;
    private final Player player;
    private final int rows;
    private final int cols;
    private int diamondsRequired = 3;
    private Boulder boulder;

    public Level() {
        tiles = new ArrayList<>();
        actors = new ArrayList<>();

        readTiles();

        rows = tiles.size();
        cols = tiles.get(0).size();

        setNeighbors();

        player = new Player(tiles.get(1).get(1));


        //Amoeba
        //amoeba = new Amoeba(tiles.get(3).get(5), 10);
        //actors.add(amoeba);

        //diamond = new Diamond(tiles.get(2).get(1));
        boulder = new Boulder(tiles.get(2).get(5));


        actors.add(boulder);
        actors.add(player);


        Fly buttery = new Fly(tiles.get(6).get(6), true, true, Direction.UP);
        actors.add(buttery);

        Frog frogy = new Frog(tiles.get(6).get(5), player);
        actors.add(frogy);

        Fly firey = new Fly(tiles.get(2).get(7), false, false, Direction.RIGHT);
        actors.add(firey);

        //actors.add(diamond);
        actors.add(new Actor(tiles.get(2).get(2)));


    }

    private void readTiles() {
        int rowIndex = 0;
        int colIndex = 0;
        try (InputStream inputStream = Level.class.getClassLoader().getResourceAsStream("Level1.txt")) {
            if (inputStream == null){
                throw new IllegalArgumentException("File not found");
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                String line;
                while ((line = in.readLine()) != null) {
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
        } catch (IOException e) {
            System.out.println("Error wtf");
            e.printStackTrace();
        }
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