package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.GameState;
import javafx.scene.image.Image;

public class Exit extends Floor{
    public Exit(int row, int col){
        super(row, col, false);
        image = new Image("exit.png");
    }

    @Override
    public void setOccupier(Actor occupant) {
        super.setOccupier(occupant);
        GameState.manager.winGame();
    }
}
