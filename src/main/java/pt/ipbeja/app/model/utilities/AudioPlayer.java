package pt.ipbeja.app.model.utilities;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer {

    private Clip clip;

    public void play(String filename) {
        try {
            stop(); // para parar se estiver a tocar algo

            InputStream audioSrc = getClass().getResourceAsStream("/audios/" + filename);
            if (audioSrc == null) {
                System.err.println("Audio not found: " + filename);
                return;
            }

            // Necessário para que o AudioSystem leia corretamente o stream
            InputStream bufferedIn = new BufferedInputStream(audioSrc);

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();  // toca uma vez

            // Para loop infinito, substitui a linha acima por:
            // clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}