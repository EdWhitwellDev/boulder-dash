package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Player;
import com.example.boulderdash.GameManager;
import com.example.boulderdash.GameState;
import com.example.boulderdash.Level;
import javafx.scene.image.Image;

public class Exit extends Floor {
    //example diamonds needed
    private static final int DIAMONDS_NEEDED = 10;

    public Exit(int row, int col) {
        super(row, col, false);
        image = new Image("exit.png");
    }

    public void exitLevel(Player player, Level level) {
        if (player.getCollectedDiamonds() >= DIAMONDS_NEEDED) {
            level.updateLevelStatus(this);
            GameState.manager.winGame();
        } else {
            System.out.println("You need more diamonds to exit!");
        }

    }
}
