package com.example.boulderdash;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class Audio {
    private static Audio instance;
    private MediaPlayer mediaPlayer;

    // Private constructor to enforce singleton
    private Audio() {}

    // Get the single instance of MusicManager
    public static Audio getInstance() {
        if (instance == null) {
            instance = new Audio();
        }
        return instance;
    }

    public void playMusic(String filePath, boolean loop, double volume) {
        try {
            String music = getClass().getResource(filePath).toExternalForm();
            Media media = new Media(music);

            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume);


            if (loop) {
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }

            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    public void playSoundEffect(String filePath, double volume) {
        try {
            String sound = getClass().getResource(filePath).toExternalForm();
            Media media = new Media(sound);
            MediaPlayer soundEffectPlayer = new MediaPlayer(media);

            soundEffectPlayer.setVolume(volume);

            soundEffectPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
        }
    }
}
