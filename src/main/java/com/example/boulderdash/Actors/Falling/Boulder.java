package com.example.boulderdash.Actors.Falling;

import com.example.boulderdash.Actors.Actor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class Boulder extends Actor {
    private int x;
    private int y;
    private boolean isFalling;
    private final ImageView imageView;

    public Boulder(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.isFalling = false;

        // Load the boulder image
        Image boulderImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/boulder.png")));
        this.imageView = new ImageView(boulderImage);
        updateImageViewPosition();
    }

    // Method to update the position of the boulder
    public void moveDown() {
        if (canMoveDown()) {
            y++;
            isFalling = true;
            updateImageViewPosition();
        } else {
            isFalling = false;
        }
    }

    // Check if the boulder can move down
    private boolean canMoveDown() {
        // Logic to check if the boulder can fall (e.g., check for empty space below)
        // This would depend on the game grid or map state
        return true; // Placeholder, implement proper check
    }

    // Update the position of the ImageView to reflect the new coordinates
    private void updateImageViewPosition() {
        imageView.setX(x * 32); // Assuming each tile is 32x32 pixels
        imageView.setY(y * 32);
    }

    // Getters and setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isFalling() {
        return isFalling;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
