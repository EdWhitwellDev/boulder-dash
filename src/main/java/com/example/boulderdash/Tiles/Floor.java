package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Actor;
import com.example.boulderdash.Actors.Player;
import javafx.scene.image.Image;

public class Floor extends Tile{
    public Floor(int row, int col, boolean isPath) {
        super(row, col, isPath);
        this.isPath = isPath;
        image = isPath ? new Image("path.png") : new Image("dirt.png");
    }

    @Override
    public void setOccupier(Actor occupant) {
        super.setOccupier(occupant);
        if (!isPath && occupant instanceof Player){
            isPath = true;
            image = new Image("path.png");
        }
    }

    public boolean getIsPath(){
        return isPath;
    }
}
