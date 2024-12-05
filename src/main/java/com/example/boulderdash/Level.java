package com.example.boulderdash;

import com.example.boulderdash.Actors.*;
import com.example.boulderdash.Actors.Enemies.*;
import com.example.boulderdash.Actors.Falling.*;
import com.example.boulderdash.Tiles.*;
import com.example.boulderdash.enums.Direction;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for getting the level's tiles,
 * actors, and player, as well as managing the relationships between tiles.
 */
public class Level {
    private List<List<Tile>> tiles; // Grid of tiles that make up the level
    private List<Actor> actors; // List of all active actors in the level
    private Player player; // The player character
    private Amoeba amoeba; // Amoeba actor
    private int rows; // Number of rows in the level
    private int cols; // Number of columns in the level
    private Diamond diamond; // Diamond actor
    private Boulder boulder; // Boulder actor

    /**
     * Constructor for the Level class. Sets tiles, actors, and player and
     * reads the tile layout from a file and sets up actor positions
     */
    public Level() {
        tiles = new ArrayList<>();
        actors = new ArrayList<>();

        // Set the level's tile layout
        readTiles();

        rows = tiles.size();
        cols = tiles.get(0).size();

        setNeighbors();

        player = new Player(tiles.get(1).get(1));

        amoeba = new Amoeba(tiles.get(3).get(5));
        actors.add(amoeba);

        diamond = new Diamond(tiles.get(2).get(1));
        boulder = new Boulder(tiles.get(2).get(5));

        actors.add(boulder);
        actors.add(player);

        Fly buttery = new Fly(tiles.get(6).get(6), true, true, Direction.UP);
        actors.add(buttery);

        Frog frogy = new Frog(tiles.get(6).get(5), player);
        actors.add(frogy);

        Fly firey = new Fly(tiles.get(2).get(8), false, false, Direction.UP);
        actors.add(firey);

        // Add other actors
        actors.add(diamond);
        actors.add(new Actor(tiles.get(2).get(2)));
    }

    private void readTiles() {
        int rowIndex = 0;
        int colIndex = 0;

        try (InputStream inputStream = Level.class.getClassLoader().getResourceAsStream("Level1.txt")) {
            if (inputStream == null) {
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
            System.err.println("Error reading level file");
            e.printStackTrace();
        }
    }

    private void setNeighbors() {
        int rows = tiles.size();
        int cols = tiles.get(0).size();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Tile current = tiles.get(i).get(j);

                if (i > 0) {
                    current.setUp(tiles.get(i - 1).get(j));
                }
                if (i < rows - 1) {
                    current.setDown(tiles.get(i + 1).get(j));
                }
                if (j > 0) {
                    current.setLeft(tiles.get(i).get(j - 1));
                }
                if (j < cols - 1) {
                    current.setRight(tiles.get(i).get(j + 1));
                }
            }
        }
    }

    // Getters for accessing level properties

    /**
     * Returns the player character
     *
     * @return the player instance
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the diamond actor in the level
     *
     * @return the diamond instance
     */
    public Diamond getDiamond() {
        return diamond;
    }

    /**
     * Returns the number of columns in the level
     *
     * @return the column count
     */
    public int getCols() {
        return cols;
    }

    /**
     * Returns the number of rows in the level
     *
     * @return the row count
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the list of all active actors in the level
     *
     * @return the list of actors
     */
    public List<Actor> getActors() {
        return actors;
    }

    /**
     * Removes an actor from the level
     *
     * @param actorToRemove the actor to remove
     */
    public void removeActor(Actor actorToRemove) {
        actors.remove(actorToRemove);
    }

    /**
     * Returns the grid of tiles that make up the level
     *
     * @return the tile grid
     */
    public List<List<Tile>> getTiles() {
        return tiles;
    }
}
