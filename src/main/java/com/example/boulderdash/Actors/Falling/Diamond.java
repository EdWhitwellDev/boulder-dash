package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Actors.Actor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Objects;

public class Diamond extends Actor {
    private int x;
    private int y;
    private boolean isFalling;
    private ImageView imageView;
    private char[][] gameGrid; // Game grid representing the map

    public Diamond(int startX, int startY, char[][] gameGrid) {
        this.x = startX;
        this.y = startY;
        this.isFalling = false;
        this.gameGrid = gameGrid;

        // Load the diamond image
        Image diamondImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/diamond.png")));
        this.imageView = new ImageView(diamondImage);
        updateImageViewPosition();
    }

    private void updateImageViewPosition() {
    }

    // Method to update the position of the diamond
    public void moveDown() {
        if (canMoveDown()) {
            // Update the game grid to reflect the diamond's movement
            gameGrid[y][x] = ' '; // Mark old position as empty
            y++;
            gameGrid[y][x] = 'D'; // Mark new position with Diamond
            isFalling = true;
            updateImageViewPosition();
        } else {
            isFalling = false;
        }
    }

    // Check if the diamond can move down
    private boolean canMoveDown() {
        // Ensure we don't go out of bounds
        if (y + 1 >= gameGrid.length) {
            return false; // Can't move if at the bottom of the grid
        }


    }
}