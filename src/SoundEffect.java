import java.net.URL;
import javax.sound.sampled.*;

public enum SoundEffect {
    dog_barking("mixkit-dog-barking-twice-1.wav"),
    chick("short-chick-sound-171389.wav"),
    game_over("game-over-2-sound-effect-230463.wav"),

    game_draw("game_draw.wav");

    public static enum Volume {
        MUTE, LOW, MEDIUM, HIGH
    }

    public static Volume volume = Volume.LOW;
    private Clip clip;

    SoundEffect(String soundFileName) {
        try {
            URL url = getClass().getClassLoader().getResource(soundFileName);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (volume != Volume.MUTE) {
            if (clip.isRunning())
                clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }

    static {
        values(); // Preload all sounds
    }
}