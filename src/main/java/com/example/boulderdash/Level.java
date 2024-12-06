package com.example.boulderdash;

import com.example.boulderdash.Actors.*;
import com.example.boulderdash.Actors.Enemies.*;
import com.example.boulderdash.Actors.Falling.*;
import com.example.boulderdash.Tiles.*;
import com.example.boulderdash.enums.Direction;
import com.example.boulderdash.enums.KeyColours;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.*;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.*;
import org.json.simple.JSONObject;

/**
 * This class is responsible for getting the level's tiles,
 * actors, and player, as well as managing the relationships between tiles.
 */
public class Level {

    private final List<List<Tile>> tiles;// Grid of tiles that make up the level
    private final List<Actor> actors;// List of all active actors in the level
    private Player player;// The player character
    private final int rows;// Number of rows in the level
    private final int cols;// Number of columns in the level
    private int timeLimit;
    private int diamondsRequired;
    private int tileSize;
    private Boulder boulder;



    /**
     * Constructor for the Level class. Sets tiles, actors, and player and
     * reads the tile layout from a file and sets up actor positions
     */
    public Level(int levelNum) {
        tiles = new ArrayList<>();
        actors = new ArrayList<>();

        readLevelFile(levelNum);
        rows = tiles.size();
        cols = tiles.get(0).size();

        setNeighbors();


    }

    public Level(String user, String saveFile) {
        System.out.println("Loading level for user: " + user + " and save file: " + saveFile);
        tiles = new ArrayList<>();
        actors = new ArrayList<>();

        loadLevel(user, saveFile);
        rows = tiles.size();
        cols = tiles.get(0).size();

        setNeighbors();
    }

    private List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        for (Object obj : jsonArray) {
            list.add(obj.toString());
        }
        return list;
    }

    public void loadLevel(String user, String saveFile) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject PlayerProfileObj = (JSONObject) parser.parse(new FileReader("PlayerProfile.json"));
            JSONObject userObj = (JSONObject) PlayerProfileObj.get(user);
            JSONObject savedLevelsObj = (JSONObject) userObj.get("SavedLevels");
            JSONObject levelObj = (JSONObject) savedLevelsObj.get(saveFile);

            JSONArray actorsArrayJson = (JSONArray) levelObj.get("Actors");
            JSONArray tilesArrayJson = (JSONArray) levelObj.get("Tiles");

            diamondsRequired = Integer.parseInt(levelObj.get("DiamondsRequired").toString());
            timeLimit = Integer.parseInt(levelObj.get("TimeRemaining").toString());
            tileSize = Integer.parseInt(levelObj.get("TileSize").toString());

            List<String> actorsArray = jsonArrayToList(actorsArrayJson);
            List<String> tilesArray = jsonArrayToList(tilesArrayJson);

            readTiles(tilesArray);
            readActors(actorsArray);

            int collectedDiamonds = Integer.parseInt(levelObj.get("DiamondsCollected").toString());
            player.setDiamondsCollected(collectedDiamonds);

            Map<KeyColours, Integer> keys = new HashMap<>();
            JSONObject keysObj = (JSONObject) levelObj.get("KeysCollected");
            for (KeyColours keyColour : KeyColours.values()) {
                keys.put(keyColour, Integer.parseInt(keysObj.get(keyColour.toString()).toString()));
            }
            player.setKeys(keys);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            System.err.println("Error reading level file");
            e.printStackTrace();
        }

        readTiles(levelSections.get("TILES"));
        readActors(levelSections.get("ACTORS"));
        String[] winConditions = levelSections.get("WIN CONDITIONS").get(0).split(",");
        diamondsRequired = Integer.parseInt(winConditions[0]);
        timeLimit = Integer.parseInt(winConditions[1]);
        tileSize = Integer.parseInt(winConditions[2]);
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
    public int getDiamondsRequired(){
        return diamondsRequired;
    }
    public int getTimeLimit(){
        return timeLimit;
    }
    public int getTileSize(){
        return tileSize;
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

    public void saveLevel(String user, String saveFile) {
        JSONObject levelObj = new JSONObject();
        JSONArray actorsArrayJson = new JSONArray();
        JSONArray tilesArrayJson = new JSONArray();

        for (Actor actor : actors) {
            String actorString = actor.toString();
            actorsArrayJson.add(actorString);
        }

        for (List<Tile> row : tiles) {
            StringBuilder rowString = new StringBuilder();
            for (Tile tile : row) {
                rowString.append(tile.toString()).append(",");
            }
            tilesArrayJson.add(rowString.toString());
        }

        levelObj.put("Actors", actorsArrayJson);
        levelObj.put("Tiles", tilesArrayJson);
        levelObj.put("DiamondsRequired", diamondsRequired);
        levelObj.put("TimeRemaining", GameState.manager.timeRemaining());
        levelObj.put("TileSize", tileSize);

        JSONObject keysObj = new JSONObject();
        for (KeyColours keyColour : KeyColours.values()) {
            keysObj.put(keyColour.toString(), player.getKeys().get(keyColour));
        }
        levelObj.put("KeysCollected", keysObj);
        levelObj.put("DiamondsCollected", player.getDiamondsCollected());

        JSONObject userObj = new JSONObject();
        JSONObject savedLevelsObj = new JSONObject();
        savedLevelsObj.put(saveFile, levelObj);
        userObj.put("SavedLevels", savedLevelsObj);

        try {
            JSONParser parser = new JSONParser();
            JSONObject PlayerProfileObj = (JSONObject) parser.parse(new FileReader("PlayerProfile.json"));
            JSONObject userObjOld = (JSONObject) PlayerProfileObj.get(user);
            JSONObject savedLevelsObjOld = (JSONObject) userObjOld.get("SavedLevels");
            savedLevelsObjOld.put(saveFile, levelObj);

            FileWriter file = new FileWriter("PlayerProfile.json");
            file.write(PlayerProfileObj.toJSONString());
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
