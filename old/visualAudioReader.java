import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.*;

// import javax.swing.*;
// import java.awt.*;

public class visualAudioReader {

    public static void main(String[] args) {
        // Trying to read bytes and info from audio file

        String filePath = "audio/test.wav";
        
        File file = new File(filePath);

        try(AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = audioStream.getFormat();
            System.out.println("Audio Format: " + format);

            // Calculate the size of the byte array needed to hold the audio data
            byte[] bytes = new byte[(int) (audioStream.getFrameLength() * format.getFrameSize())];
            
            // Read the audio data into the byte array
            audioStream.read(bytes);

            // Get user input for number of samples and step size
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the number of samples to extract: ");
            int numSamples = scanner.nextInt();

            System.out.print("Enter the step size (frames between samples): ");
            int stepSize = scanner.nextInt();
            scanner.close();

            System.out.println("\nExtracted Samples (Left Channel):");

            // Loop through the byte array and extract samples based on step size
            int extractedSamples = 0;
            for (int i = 0; i < bytes.length / 4; i += stepSize) { // 4 bytes per frame (2 channels)
                if (extractedSamples >= numSamples) break;

                // Extract the left channel (first 2 bytes of the frame)
                int sample = (bytes[4 * i + 1] << 8) | (bytes[4 * i] & 0xFF); // Little-endian
                System.out.println("Sample " + extractedSamples + ": " + sample);

                extractedSamples++;
            }

        }

        catch(UnsupportedAudioFileException e){
            System.out.println("Audio file is not supported");
        }

        catch(IOException e){
            System.out.println("Something went wrong");
        }
    }
    
}