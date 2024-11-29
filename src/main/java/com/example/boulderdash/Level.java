package com.example.boulderdash;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Amoeba;
import com.example.boulderdash.Actors.Enemies.Fly;
import com.example.boulderdash.Actors.Enemies.Frog;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Tiles.*;
import com.example.boulderdash.enums.Direction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Level {
    private List<List<Tile>> tiles;
    private List<Actor> actors;
    private Player player;
    private Amoeba amoeba;
    private int rows;
    private int cols;

    public Level() {
        tiles = new ArrayList<>();
        actors = new ArrayList<>();

        readTiles();

        rows = tiles.size();
        cols = tiles.get(0).size();

        setNeighbors();

        player = new Player(tiles.get(1).get(1));

        //Amoeba
        amoeba = new Amoeba(tiles.get(3).get(5), 0);
        actors.add(amoeba);

        actors.add(player);

        //Fly buttery = new Fly(tiles.get(6).get(6), true, true, Direction.UP);
        //actors.add(buttery);

        Frog frogy = new Frog(tiles.get(6).get(5), player);
        actors.add(frogy);
    }

    private void readTiles() {
        int rowIndex = 0;
        int colIndex = 0;
        try (Scanner in = new Scanner(new File("src/main/resources/Level1.txt"))) {
            while (in.hasNextLine()) {
                List<Tile> row = new ArrayList<>();
                String rowSymbols = in.nextLine();
                String[] tileSymbols = rowSymbols.split(",");
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
        } catch (FileNotFoundException e) {
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