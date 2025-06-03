package pt.ipbeja.app.model.utilities;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Martim Dias - 24290
 * Classe utilitária para tocar ficheiros de áudio do jogo.
 *
 * Os áudios devem estar na pasta `resources/audios/` e devem ser ficheiros .wav válidos.
 */

public class AudioPlayer {

    private Clip clip;

    /**
     * Dá play ao ficheiro de áudio com o nome desejado para o nivel.
     *
     * @param filename nome do ficheiro de áudio (ex: "mus1.wav")
     */
    public void play(String filename) {
        try {
            stop(); // interrompe o áudio anterior, se existir

            InputStream audioSrc = getClass().getResourceAsStream("/audios/" + filename);
            if (audioSrc == null) {
                System.err.println("Audio not found: " + filename);
                return;
            }

            // Necessário para que o AudioSystem leia o stream
            InputStream bufferedIn = new BufferedInputStream(audioSrc);

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();  // toca uma vez
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Para o áudio atual, se estiver a tocar.
     * É para interromper o audio quando necessário
     */
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}