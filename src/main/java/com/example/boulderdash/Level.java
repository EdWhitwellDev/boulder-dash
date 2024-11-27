package com.example.boulderdash;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private List<List<Tile>> tiles;
    private List<Actor> actors;
    private Player player;
    private int rows;
    private int cols;

    public Level(){
        tiles = new ArrayList<>();
        actors = new ArrayList<>();

        rows = 5;
        cols = 5;

        for (int i = 0; i < rows; i++) {
            List<Tile> row = new ArrayList<>();

            // Initialize each row with specified columns
            for (int j = 0; j < cols; j++) {
                row.add(new Tile());  // Default value (can be modified)
            }

            tiles.add(row);  // Add row to matrix
        }

        setNeighbors();

        player = new Player(tiles.get(0).get(0));

        actors.add(player);
        actors.add(new Actor(tiles.get(1).get(1)));
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
    public int getCols() {
        return cols;
    }
    public int getRows() {
        return rows;
    }
    public List<Actor> getActors() {
        return actors;
    }

    public List<List<Tile>> getTiles() {
        return tiles;
    }
}
