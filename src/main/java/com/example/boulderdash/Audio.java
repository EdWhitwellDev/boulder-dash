package com.example.boulderdash;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Handles the playback of music and sound effects during the game.
 * @author Viraj Shah
 * @version 1.0
 */
public final class Audio {
    /**
     * Single instance of the Audio class.
     */
    private static Audio instance;
    /**
     * The MediaPlayer object used for audio playback.
     */
    private MediaPlayer mediaPlayer;

    /**
     * Returns the single instance of the Audio class.
     * Creates one if it does not exist.
     * @return the single instance.
     */
    public static Audio getInstance() {
        if (instance == null) {
            instance = new Audio();
        }
        return instance;
    }

    /**
     * Plays background music.
     * @param filePath is where the music is located.
     * @param loop {@code True} to loop the music.
     * @param volume the volume of the music (0.0 to 1.0).
     */
    public void playMusic(final String filePath,
                          final boolean loop, final double volume) {
        try {
            // Convert file path to a URL for the Media object.
            String music = getClass().getResource(filePath).toExternalForm();
            Media media = new Media(music);
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume);
            if (loop) {
                // Continuously play music.
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }

            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    /**
     * Plays a sound effect once.
     * @param filePath is where the music is located.
     * @param volume the volume of the music (0.0 to 1.0).
     */
    public void playSoundEffect(final String filePath, final double volume) {
        try {
            // Convert file path to a URL for the Media object.
            String sound = getClass().getResource(filePath).toExternalForm();
            Media media = new Media(sound);
            MediaPlayer soundEffectPlayer = new MediaPlayer(media);

            soundEffectPlayer.setVolume(volume);

            soundEffectPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
        }
    }

    /**
     * Private constructor for single patterns.
     * Initialises audio only once.
     */
    private Audio() {
    }
}
