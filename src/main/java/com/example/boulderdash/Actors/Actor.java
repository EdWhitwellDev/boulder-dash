package com.example.boulderdash.Actors;

import com.example.boulderdash.Actors.Falling.Diamond;
import com.example.boulderdash.Tiles.Tile;
import com.example.boulderdash.enums.Direction;
import javafx.scene.image.Image;

public class Actor {
    protected Tile position;
    protected Image image = new Image("diamond.png");
    protected int tickCoolDown;
    protected int tickCoolDownReset;
    protected Direction currentDirection;

    public Actor(Tile startPosition){
        position = startPosition;
        position.setOccupier(this);
    }

    public Image getImage(){
        return image;
    }
    public Direction getCurrentDirection(){
        return currentDirection;
    }
    protected void changePos(Tile nextPos){
        tickCoolDown = tickCoolDownReset;
        position.setOccupier(null);
        position = nextPos;

        position.setOccupier(this);
    }

    public void move(){}

    //Hello

}
