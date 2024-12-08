package com.example.boulderdash;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.Actors.Amoeba;
import com.example.boulderdash.Actors.Enemies.Fly;
import com.example.boulderdash.Actors.Enemies.Frog;
import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.Actors.Falling.Boulder;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.Tiles.MagicWall;
import com.example.boulderdash.Tiles.Floor;
import com.example.boulderdash.Tiles.Exit;
import com.example.boulderdash.Tiles.LockedDoor;
import com.example.boulderdash.Tiles.TitaniumWall;
import com.example.boulderdash.Tiles.NormalWall;
import com.example.boulderdash.Tiles.Key;
import com.example.boulderdash.enums.Direction;
import com.example.boulderdash.enums.KeyColours;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Dictionary;
import java.util.Hashtable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * This class is responsible for getting the level's tiles,
 * actors, and player, as well as managing the relationships between tiles.
 */
public class Level {

    private static final int COLS_INDEX = 3;
    private static final int AMOEBA_GROWTH_RATE_INDEX = 4;
    private static final int AMOEBA_MAX_SIZE_INDEX = 5;
    private static final int FLY_ACTIVE_INDEX = 3;
    private static final int FLY_DIRECTION_INDEX = 4;
    private final List<List<Tile>> tiles; // Grid of tiles to create the level
    private final List<Actor> actors; // List of all active actors in the level
    private Player player; // The player character
    private int rows; // Number of rows in the level
    private int cols; // Number of columns in the level
    private int timeLimit;
    private int diamondsRequired;
    private int tileSize;
    private int amoebaGrowthRate;
    private int amoebaMaxSize;


    /**
     * Constructor for the Level class. Sets tiles, actors, and player and
     * reads the tile layout from a file and sets up actor positions
     * @param levelNum is the level to be read.
     */
    public Level(int levelNum) {
        tiles = new ArrayList<>();
        actors = new ArrayList<>();

        readLevelFile(levelNum);

        setNeighbors();
    }

    /**
     * Constructor for a level.
     * Loads data from the user and save file.
     * @param user is the user associated with the save file.
     * @param saveFile contains the level data.
     */
    public Level(String user, String saveFile) {
        System.out.println("Loading level for user: "
                + user + " and save file: " + saveFile);
        tiles = new ArrayList<>();
        actors = new ArrayList<>();

        loadLevel(user, saveFile);

        setNeighbors();
    }

    /**
     * Loads the level data from the user and save file.
     * @param user is the user associated with the save file.
     * @param saveFile contains the level data.
     */
    public void loadLevel(String user, String saveFile) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject playerProfileObj = (JSONObject)
                    parser.parse(new FileReader("PlayerProfile.json"));
            JSONObject userObj = (JSONObject) playerProfileObj.get(user);
            JSONObject savedLevelsObj = (JSONObject) userObj.get("SavedLevels");
            JSONObject levelObj = (JSONObject) savedLevelsObj.get(saveFile);

            JSONArray actorsArrayJson = (JSONArray) levelObj.get("Actors");
            JSONArray tilesArrayJson = (JSONArray) levelObj.get("Tiles");

            diamondsRequired = Integer.parseInt(levelObj.
                    get("DiamondsRequired").toString());
            timeLimit = Integer.parseInt(levelObj.
                    get("TimeRemaining").toString());
            tileSize = Integer.parseInt(levelObj.
                    get("TileSize").toString());
            amoebaGrowthRate = Integer.parseInt(levelObj.
                    get("AmoebaGrowthRate").toString());
            amoebaMaxSize = Integer.parseInt(levelObj.
                    get("AmoebaMaxSize").toString());

            List<String> actorsArray = jsonArrayToList(actorsArrayJson);
            List<String> tilesArray = jsonArrayToList(tilesArrayJson);

            readTiles(tilesArray);
            readActors(actorsArray);

            int collectedDiamonds = Integer.parseInt(levelObj.
                    get("DiamondsCollected").toString());
            player.setDiamondsCollected(collectedDiamonds);

            Map<KeyColours, Integer> keys = new HashMap<>();
            JSONObject keysObj = (JSONObject) levelObj.get("KeysCollected");
            for (KeyColours keyColour : KeyColours.values()) {
                keys.put(keyColour, Integer.parseInt(keysObj.
                        get(keyColour.toString()).toString()));
            }
            player.setKeys(keys);

            rows = tiles.size();
            cols = tiles.get(0).size();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes an actor from the level.
     *
     * @param actorToRemove the actor to remove.
     */
    public void removeActor(Actor actorToRemove) {
        actors.remove(actorToRemove);
        if (actorToRemove.getPosition() != null
                && actorToRemove.getPosition().getOccupier() == actorToRemove) {
            actorToRemove.getPosition().setOccupier(null);
        }
    }

    /**
     * Adds actors to the level.
     * @param actor is the list of {@link Actor} objects to be added.
     */
    public void addActors(List<Actor> actor) {
        actors.addAll(actor);
    }

    /**
     * Replaces a tile with a new one.
     * @param newTile is the tile to replace the old tile.
     * @param oldTile is the tile to be replaced.
     */
    public void replaceTile(Tile newTile, Tile oldTile) {
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

    /**
     * Saves the current level to a JSON file.
     * @param user is the user under which the file should be saved.
     * @param saveFile is the name of the save file.
     */
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
        levelObj.put("TimeRemaining", GameState.getManager().timeRemaining());
        levelObj.put("TileSize", tileSize);
        levelObj.put("AmoebaGrowthRate", amoebaGrowthRate);
        levelObj.put("AmoebaMaxSize", amoebaMaxSize);

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
            JSONObject playerProfileObj = (JSONObject) parser.
                    parse(new FileReader("PlayerProfile.json"));
            JSONObject userObjOld = (JSONObject) playerProfileObj.get(user);
            JSONObject savedLevelsObjOld =
                    (JSONObject) userObjOld.get("SavedLevels");
            savedLevelsObjOld.put(saveFile, levelObj);

            FileWriter file = new FileWriter("PlayerProfile.json");
            file.write(playerProfileObj.toJSONString());
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a JSON array into a list of strings.
     * @param jsonArray is the array to be converted.
     * @return a list of strings.
     */
    private List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        for (Object obj : jsonArray) {
            list.add(obj.toString());
        }
        return list;
    }

    // Getters for accessing level properties
    /**
     * Returns the player character.
     *
     * @return the player instance.
     */
    public Player getPlayer() {
        return player;
    }
    /**
     * Returns the diamond actor in the level.
     *
     * @return the diamond instance.
     */
    public int getDiamondsRequired() {
        return diamondsRequired;
    }

    /**
     * Gets the time limit.
     * @return the time limit.
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * Returns the number of columns in the level.
     *
     * @return the column count.
     */
    public int getCols() {
        return cols;
    }

    /**
     * Returns the number of rows in the level.
     *
     * @return the row count.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the list of all active actors in the level.
     *
     * @return the list of actors.
     */
    public List<Actor> getActors() {
        return actors;
    }

    /**
     * Returns the grid of tiles that make up the level.
     *
     * @return the tile grid.
     */
    public List<List<Tile>> getTiles() {
        return tiles;
    }

    /**
     * Reads the level configuration from a text file and parses the tiles
     * and actors based on the format.
     * @param levelNum the specific level to load.
     */
    private void readLevelFile(int levelNum) {
        String levelFile = "Level" + levelNum + ".txt";
        Dictionary<String, List<String>> levelSections = new Hashtable<>();
        String currentSection = "";
        try (InputStream inputStream =
                     Level.class.getClassLoader().
                             getResourceAsStream(levelFile)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found");
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    inputStream, StandardCharsets.UTF_8))) {
                String line;

                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("[") && line.endsWith("]")) {
                        currentSection = line.substring(1, line.length() - 1);
                    } else {
                        List<String> currentList =
                                levelSections.get(currentSection);
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

        String[] winConditions =
                levelSections.get("WIN CONDITIONS").get(0).split(",");
        diamondsRequired = Integer.parseInt(winConditions[0]);
        timeLimit = Integer.parseInt(winConditions[1]);
        rows = Integer.parseInt(winConditions[2]);
        cols = Integer.parseInt(winConditions[COLS_INDEX]);
        amoebaGrowthRate = Integer.parseInt(winConditions
                [AMOEBA_GROWTH_RATE_INDEX]);
        amoebaMaxSize = Integer.parseInt(winConditions[AMOEBA_MAX_SIZE_INDEX]);

        readTiles(levelSections.get("TILES"));
        readActors(levelSections.get("ACTORS"));
    }

    /**
     * Adds tiles to the level from a list of strings.
     * @param tileStrings is the list of strings representing the tiles.
     */
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
                        row.add(new LockedDoor(rowIndex,
                                colIndex, KeyColours.RED));
                        break;
                    case "G":
                        row.add(new LockedDoor(rowIndex,
                                colIndex, KeyColours.GREEN));
                        break;
                    case "B":
                        row.add(new LockedDoor(rowIndex,
                                colIndex, KeyColours.BLUE));
                        break;
                    case "Y":
                        row.add(new LockedDoor(rowIndex,
                                colIndex, KeyColours.YELLOW));
                        break;
                    case "r":
                        row.add(new Key(rowIndex,
                                colIndex, KeyColours.RED));
                        break;
                    case "g":
                        row.add(new Key(rowIndex,
                                colIndex, KeyColours.GREEN));
                        break;
                    case "b":
                        row.add(new Key(rowIndex,
                                colIndex, KeyColours.BLUE));
                        break;
                    case "y":
                        row.add(new Key(rowIndex,
                                colIndex, KeyColours.YELLOW));
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

    /**
     * Reads in actors to the level from a list of strings.
     * @param actorStrings is the list of strings representing the actors.
     */
    private void readActors(List<String> actorStrings) {
        List<Frog> frogsWithoutPlayer = new ArrayList<>();
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
                    actors.add(new Amoeba(startTile,
                            amoebaGrowthRate, amoebaMaxSize));
                    break;
                case "F":
                    actors.add(new Fly(startTile,
                            Boolean.parseBoolean(actorInfo[FLY_ACTIVE_INDEX]),
                            false, getDirectionFromString(actorInfo
                            [FLY_DIRECTION_INDEX])));
                    break;
                case "BF":
                    actors.add(new Fly(startTile,
                            Boolean.parseBoolean(actorInfo[FLY_ACTIVE_INDEX]),
                            true, getDirectionFromString(actorInfo
                            [FLY_DIRECTION_INDEX])));
                    break;
                case "D":
                    actors.add(new Diamond(startTile));
                    break;
                case "B":
                    actors.add(new Boulder(startTile));
                    break;
                case "R":
                    if (player != null) {
                        actors.add(new Frog(startTile, player));
                    } else {
                        Frog frog = new Frog(startTile);
                        actors.add(frog);
                        frogsWithoutPlayer.add(frog);
                    }
                    break;
                default:
                    break;
            }
        }
        player = (Player) actors.stream().filter(actor
                -> actor instanceof Player).findFirst().orElse(null);
        if (player == null) {
            throw new IllegalArgumentException("No player found");
        }
        for (Frog frog : frogsWithoutPlayer) {
            frog.setPlayer(player);
        }
    }

    /**
     * Converts a string representing a direction to
     * its corresponding {@link Direction} value.
     * @param direction represents the direction.
     * @return the {@link Direction} value.
     */
    private Direction getDirectionFromString(String direction) {
        return switch (direction) {
            case "UP" -> Direction.UP;
            case "DOWN" -> Direction.DOWN;
            case "LEFT" -> Direction.LEFT;
            case "RIGHT" -> Direction.RIGHT;
            default -> Direction.STATIONARY;
        };
    }

    /**
     * Sets the neighbouring tiles for each tile.
     */
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
}
