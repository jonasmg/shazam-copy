import javax.swing.JFrame;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.*;

public class first {
    public static void main(String[] args) {
        // Get array of bytes from audio file:
        // Trying to read bytes and info from audio file

        String filePath = "audio/output.wav";
        
        File file = new File(filePath);

        // Array to hold samples:
        int[] samples = new int[8000];

        try(AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = audioStream.getFormat();
            System.out.println("Audio Format: " + format);

            // Calculate the size of the byte array needed to hold the audio data
            byte[] bytes = new byte[(int) (audioStream.getFrameLength() * format.getFrameSize())];
            
            // Read the audio data into the byte array
            audioStream.read(bytes);

            // Get user input for number of samples and step size
            //Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the number of samples to extract: ");
            //int numSamples = scanner.nextInt();
            int numSamples = 8000;

            System.out.print("Enter the step size (frames between samples): ");
            //int stepSize = scanner.nextInt();
            int stepSize = 4;
            //scanner.close();

            System.out.println("\nExtracted Samples (Left Channel):");

            // Loop through the byte array and extract samples based on step size
            int extractedSamples = 0;
            for (int i = 0; i < bytes.length / 4; i += stepSize) { // 4 bytes per frame (2 channels)
                if (extractedSamples >= numSamples) break;

                // Extract the left channel (first 2 bytes of the frame)
                int sample = (bytes[4 * i + 1] << 8) | (bytes[4 * i] & 0xFF); // Little-endian
                //System.out.println("Sample " + extractedSamples + ": " + sample);
                // Append samples in array:
                samples[extractedSamples] = sample;

                extractedSamples++;
            }

        }

        catch(UnsupportedAudioFileException e){
            System.out.println("Audio file is not supported");
        }

        catch(IOException e){
            System.out.println("Something went wrong");
        }

        // Print samples:
        //for (int i = 0; i < 800; i++) {
        //    System.out.println(samples[i]);
        //}

        second s = new second();
        s.setVisible(true);
        //set s array to samples
        s.setSamples(samples);

        // Remember: open xming
        // Remember: "export DISPLAY =:0" before running

        //f.add(s);
        //f.setSize(800, 500);
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //f.setVisible(true);
    }
}
