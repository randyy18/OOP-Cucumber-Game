import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;

public class AudioManager {
    private static AudioManager instance;
    private Clip backgroundClip;
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;

    public enum SoundType {
        COLLECT(800, 150),
        CAUGHT(200, 400),
        WIN(600, 500),
        FOOTSTEP(400, 50);

        private final int frequency;
        private final int duration;

        SoundType(int frequency, int duration) {
            this.frequency = frequency;
            this.duration = duration;
        }

        public int getFrequency() {
            return frequency;
        }

        public int getDuration() {
            return duration;
        }
    }

    private AudioManager() {
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playBackgroundMusic() {
        if (!musicEnabled)
            return;

        try {
            if (backgroundClip != null && backgroundClip.isRunning()) {
                return;
            }

            byte[] musicData = generateBackgroundMusic();
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            AudioInputStream audioStream = new AudioInputStream(
                    new ByteArrayInputStream(musicData),
                    format,
                    musicData.length / format.getFrameSize());

            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);

            FloatControl volumeControl = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-20.0f);

            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Could not play background music: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundClip != null) {
            backgroundClip.stop();
            backgroundClip.close();
            backgroundClip = null;
        }
    }

    public void playSound(SoundType type) {
        if (!soundEnabled)
            return;

        new Thread(() -> {
            try {
                byte[] soundData = generateTone(type.getFrequency(), type.getDuration());
                AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
                AudioInputStream audioStream = new AudioInputStream(
                        new ByteArrayInputStream(soundData),
                        format,
                        soundData.length / format.getFrameSize());

                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (Exception e) {
                System.err.println("Could not play sound: " + e.getMessage());
            }
        }).start();
    }

    public void playCollectSound() {
        playSound(SoundType.COLLECT);
    }

    public void playCaughtSound() {
        playSound(SoundType.CAUGHT);
    }

    public void playWinSound() {
        playSound(SoundType.WIN);
    }

    private byte[] generateTone(int frequency, int durationMs) {
        int sampleRate = 44100;
        int numSamples = (sampleRate * durationMs) / 1000;
        byte[] data = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double time = (double) i / sampleRate;
            double envelope = 1.0 - ((double) i / numSamples);
            double sample = Math.sin(2 * Math.PI * frequency * time) * envelope * 0.8;

            short value = (short) (sample * Short.MAX_VALUE);
            data[i * 2] = (byte) (value & 0xFF);
            data[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
        }

        return data;
    }

    private byte[] generateBackgroundMusic() {
        int sampleRate = 44100;
        int durationSeconds = 4;
        int numSamples = sampleRate * durationSeconds;
        byte[] data = new byte[numSamples * 2];

        int[] bassNotes = { 110, 110, 130, 110, 146, 110, 130, 110 };
        int[] melodyNotes = { 220, 261, 293, 329, 293, 261, 220, 196 };
        int beatLength = numSamples / 16;

        for (int i = 0; i < numSamples; i++) {
            double time = (double) i / sampleRate;
            int beatIndex = (i / beatLength) % 8;
            int subBeat = i % beatLength;

            double pulseEnvelope = (subBeat < beatLength / 4) ? 1.0 : 0.6;

            double bass = Math.sin(2 * Math.PI * bassNotes[beatIndex] * time) * 0.35 * pulseEnvelope;

            double melody = Math.sin(2 * Math.PI * melodyNotes[beatIndex] * time) * 0.15;
            melody += Math.sin(2 * Math.PI * melodyNotes[beatIndex] * 1.5 * time) * 0.08;

            double kick = 0;
            if (subBeat < beatLength / 8) {
                double kickEnv = 1.0 - ((double) subBeat / (beatLength / 8));
                kick = Math.sin(2 * Math.PI * 60 * time * kickEnv) * 0.4 * kickEnv;
            }

            double hihat = 0;
            if ((i / (beatLength / 2)) % 2 == 1 && subBeat % (beatLength / 2) < beatLength / 16) {
                hihat = (Math.random() * 2 - 1) * 0.1;
            }

            double sample = bass + melody + kick + hihat;
            sample = Math.max(-0.9, Math.min(0.9, sample));

            short value = (short) (sample * Short.MAX_VALUE);
            data[i * 2] = (byte) (value & 0xFF);
            data[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
        }

        return data;
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}
