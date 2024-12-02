package com.example.boulderdash.Tiles;

import com.example.boulderdash.Actors.Falling.Boulder;
import com.example.boulderdash.Actors.Falling.Diamond;
import javafx.scene.image.Image;

public class MagicWall extends Tile{
    public MagicWall(int row, int col){
        super(row, col, false);
        image = new Image("magic_wall.png");
    }

    public void transform(Tile fallingTile) {
        if ("Boulder".equalsIgnoreCase(fallingTile.getType())) {
            fallingTile.setType("Diamond");
            fallingTile.setImage(new Image("diamond.png"));
        } else if ("Diamond".equalsIgnoreCase(fallingTile.getType())) {
            fallingTile.setType("Boulder");
            fallingTile.setImage(new Image("boulder.png"));
        }
    }
}
