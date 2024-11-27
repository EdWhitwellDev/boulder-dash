package com.example.boulderdash;

import javafx.scene.image.Image;

public class Player extends Actor{
    private Direction currentDirection = Direction.STATIONARY;
    private int tickCoolDown = 0;
    private int tickCoolDownReset = 10;

    public Player(Tile startingTile){
        super(startingTile);
        image = new Image("player.png");
    }

    public void setDirection(Direction direction){
        currentDirection = direction;
    }

    public void move(){
        if (tickCoolDown > 0){
            tickCoolDown--;
        }
        else {
            switch (currentDirection) {
                case UP:
                    if (position.getUp() != null) {
                        changePos(position.getUp());
                    }
                    break;

                case DOWN:
                    if (position.getDown() != null) {
                        changePos(position.getDown());
                    }
                    break;

                case LEFT:
                    if (position.getLeft() != null) {
                        changePos(position.getLeft());
                    }
                    break;

                case RIGHT:
                    if (position.getRight() != null) {
                        changePos(position.getRight());
                    }
                    break;

                default:
                    break;
            }
        }

    }

    private void changePos(Tile nextPos){
        tickCoolDown = tickCoolDownReset;
        position.setOccupier(null);
        position = nextPos;
        position.setOccupier(this);
    }
}
