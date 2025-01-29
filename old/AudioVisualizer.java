import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;

public class AudioVisualizer extends JPanel {
    private long[] samples; // Array to store samples
    private int sampleRate;

    public AudioVisualizer(String filePath) throws UnsupportedAudioFileException, IOException {
        // Load audio file and extract samples
        File file = new File(filePath);
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = audioStream.getFormat();
            sampleRate = (int) format.getSampleRate(); // Get the sample rate

            if (format.getSampleSizeInBits() != 16 || format.getChannels() != 2 || !format.isBigEndian()) {
                System.err.println("Unexpected audio format. Adjust the code accordingly.");
                return;
            }

            byte[] bytes = new byte[(int) (audioStream.getFrameLength() * format.getFrameSize())];
            audioStream.read(bytes);

            // Store samples from the left channel
            int numSamples = bytes.length / 4; // 4 bytes per frame (2 channels)
            samples = new long[numSamples];
            for (int i = 0; i < numSamples; i++) {
                samples[i] = (bytes[4 * i + 1] << 8) | (bytes[4 * i] & 0xFF); // Left channel
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Get panel dimensions
        int width = getWidth();
        int height = getHeight();

        // Normalize samples to fit the height of the panel
        int midY = height / 2; // Middle of the panel (baseline for waveform)
        double scale = midY / 32768.0; // Scale for 16-bit samples

        // Draw waveform
        g.setColor(Color.BLUE);
        for (int i = 0; i < samples.length - 1; i++) {
            // Map samples to panel coordinates
            int x1 = i * width / samples.length;
            int y1 = midY - (int) (samples[i] * scale);
            int x2 = (i + 1) * width / samples.length;
            int y2 = midY - (int) (samples[i + 1] * scale);

            // Draw a line between two points
            g.drawLine(x1, y1, x2, y2);
        }
    }

    public static void main(String[] args) {
        String filePath = "audio/test.wav"; // Path to audio file

        try {
            // Create the visualizer and load samples
            AudioVisualizer visualizer = new AudioVisualizer(filePath);

            // Create a JFrame to display the visualization
            JFrame frame = new JFrame("Audio Visualizer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 400); // Set the window size
            frame.add(visualizer); // Add the visualizer panel
            frame.setVisible(true);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
