import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.File;
import java.io.IOException;

public class audioSample {
    // Get array of bytes from audio file:
    // Trying to read bytes and info from audio file

    private String filePath = "audio/output.wav";
    private File file = new File(filePath);
    private int numSamples = 8000;
    private int stepSize = 1;
    private int[] samples = new int[0];

    public int[] getSamples() {
        return samples;
    }

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public void setFile(String filePath) {
        this.filePath = filePath;
        this.file = new File(filePath);
    }

    public void computeSamples() {

        // Update samples[] to the right length
        samples = new int[numSamples];

        try(AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = audioStream.getFormat();
            System.out.println("Audio Format: " + format);
    
            // Calculate the size of the byte array needed to hold the audio data
            byte[] bytes = new byte[(int) (audioStream.getFrameLength() * format.getFrameSize())];
            
            // Read the audio data into the byte array
            audioStream.read(bytes);
    
            // Get user input for number of samples and step size
            //Scanner scanner = new Scanner(System.in);
            //System.out.print("Enter the number of samples to extract: ");
            //numSamples = scanner.nextInt();
    
            //System.out.print("Enter the step size (frames between samples): ");
            //stepSize = scanner.nextInt();
            //scanner.close();
    
            //System.out.println("\nExtracted Samples (Left Channel):");
    
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
            // Print how many samples was extracted
            System.out.println("Extracted " + samples.length + " samples from left track of " + filePath);
        }
        catch(UnsupportedAudioFileException e){
            System.out.println("Audio file is not supported");
        }
        catch(IOException e){
            System.out.println("Something went wrong");
        }
    }

    // Print samples method
    public void printSamples() {
        for (int i = 0; i < samples.length; i++) {
            System.out.println(samples[i]);
        }
    }

    public int getMaxSamples() {
        try(AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            // Print number of frames
            int numFrames = (int) audioStream.getFrameLength();
            System.out.println("Number of frames: " + numFrames);
            return numFrames;

        }
        catch(UnsupportedAudioFileException e){
            System.out.println("Audio file is not supported");
            return 0;
        }
        catch(IOException e){
            System.out.println("Something went wrong");
            return 0;
        }
    }

    public double getLength() {
        try(AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = audioStream.getFormat();
            // Print number of frames
            int numFrames = (int) audioStream.getFrameLength();
            // Print duration
            double duration = numFrames / format.getFrameRate();
            return duration;
        }
        catch(UnsupportedAudioFileException e){
            System.out.println("Audio file is not supported");
            return 0;
        }
        catch(IOException e){
            System.out.println("Something went wrong");
            return 0;
        }
    }
}
