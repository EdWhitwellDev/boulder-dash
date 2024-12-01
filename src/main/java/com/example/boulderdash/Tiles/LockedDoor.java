package com.example.boulderdash.Tiles;

import javafx.scene.image.Image;

public class LockedDoor extends Tile{
    public LockedDoor(int row, int col){
        super(row, col, false)
;        image = new Image("red_door.png");
    }

   public void unlockDoor(Key key) {
        if ( key.getColour() == this.colour) {
            isLocked = false;
            System.out.println("Door unlocked successfully!");
        } else if (!isLocked) {
            System.out.println("The door is already unlocked.");
        } else {
            System.out.println("Key color does not match the door's color.");
        }
    }


    public boolean isLocked() {
        return isLocked;
    }

    public Colour getColour() {
        return colour;
    }
}
