import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Hello, world!");

        Scanner scanner = new Scanner(System.in);
        
        String filePath = "audio/test.wav";
        File file = new File(filePath);
        try(AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = audioStream.getFormat();
            System.out.println("Audio Format: " + format);

            AudioFormat baseFormat = audioStream.getFormat();

            // Create a new AudioFormat with PCM_SIGNED encoding
            AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED, // Encoding to PCM_SIGNED
                //baseFormat.getSampleRate(),      // Sample rate remains the same as the base format
                44100,      // Sample rate remains the same as the base format
                16,                              // Sample size in bits (16 bits)
                baseFormat.getChannels(),        // Number of channels (same as base format)
                baseFormat.getChannels() * 2,    // Frame size (number of channels * 2 bytes per sample)
                //baseFormat.getSampleRate(),      // Frame rate (same as sample rate)
                //baseFormat.isBigEndian()         // Use the same byte order as the base format
                44100,                           // Frame rate (same as sample rate)
                false                            // Use little-endian byte order
            );

            AudioInputStream decodedAudioStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);

            Clip clip = AudioSystem.getClip();
            clip.open(decodedAudioStream);
            clip.start();

            String response = scanner.next();
        }
        catch(LineUnavailableException e) {
            System.out.println("Audio line is unavailable");
        }
        catch(UnsupportedAudioFileException e) {
            System.out.println("Audio file is not supported");
        }
        catch(IOException e) {
            System.out.println("Something went wrong");
        }

        
    }
}