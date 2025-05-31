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
    private int[] samples = new int[0];

    public int[] getSamples() {
        return samples;
    }

    // getSampleRate
    public int getSampleRate() {
        try(AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = audioStream.getFormat();
            int sampleRate = (int) format.getSampleRate();
            return sampleRate;
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

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
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
    
            //System.out.println("\nExtracted Samples (Left Channel):");
    
            // Loop through the byte array and extract samples based on step size
            int extractedSamples = 0;

            // Read bytes per frame from file
            int bytesPerFrame = format.getFrameSize();
            if (bytesPerFrame == 4) {
                for (int i = 0; i < bytes.length / 4; i += 1) { // 4 bytes per frame (2 channels)
                    if (extractedSamples >= numSamples) break;
        
                    // Extract the left channel (first 2 bytes of the frame)
                    int sample = (bytes[4 * i + 1] << 8) | (bytes[4 * i] & 0xFF); // Little-endian
                    //System.out.println("Sample " + extractedSamples + ": " + sample);
                    // Append samples in array:
                    samples[extractedSamples] = sample;
        
                    extractedSamples++;
                }
            } else if (bytesPerFrame == 2) {
                for (int i = 0; i < bytes.length / 2; i += 1) { // 2 bytes per frame (1 channel)
                    if (extractedSamples >= numSamples) break;
        
                    // Extract the left channel (first 2 bytes of the frame)
                    int sample = (bytes[2 * i + 1] << 8) | (bytes[2 * i] & 0xFF); // Little-endian
                    //System.out.println("Sample " + extractedSamples + ": " + sample);
                    // Append samples in array:
                    samples[extractedSamples] = sample;
        
                    extractedSamples++;
                }
            } else {
                System.out.println("Unsupported audio format");
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

    public void computeSamplesStereo() {
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = audioStream.getFormat();
            System.out.println("Audio Format: " + format);
    
            int channels = format.getChannels();
            int bytesPerFrame = format.getFrameSize();
            int sampleSizeInBits = format.getSampleSizeInBits();
            boolean isBigEndian = format.isBigEndian();

            // Print isBigEndian
            System.out.println("isBigEndian: " + isBigEndian);
    
            if (sampleSizeInBits % 8 != 0) {
                System.out.println("Unsupported sample size: " + sampleSizeInBits);
                return;
            }
    
            int bytesPerSample = sampleSizeInBits / 8;
            byte[] bytes = new byte[(int) (audioStream.getFrameLength() * bytesPerFrame)];
            audioStream.read(bytes);
    
            samples = new int[numSamples];
            int extractedSamples = 0;
    
            for (int i = 0; i < bytes.length / bytesPerFrame; i += 1) {
                if (extractedSamples >= numSamples) break;
    
                int frameStart = i * bytesPerFrame;
                int sampleSum = 0;
    
                for (int ch = 0; ch < Math.min(channels, 2); ch++) {
                    int sampleStart = frameStart + ch * bytesPerSample;
                    int sample = 0;
    
                    if (isBigEndian) {
                        for (int b = 0; b < bytesPerSample; b++) {
                            sample = (sample << 8) | (bytes[sampleStart + b] & 0xFF);
                        }
                    } else {
                        for (int b = bytesPerSample - 1; b >= 0; b--) {
                            sample = (sample << 8) | (bytes[sampleStart + b] & 0xFF);
                        }
                    }
    
                    // Sign extend for samples smaller than 32 bits
                    int shift = 32 - sampleSizeInBits;
                    sample = (sample << shift) >> shift;
    
                    sampleSum += sample;
                }
    
                int averageSample = sampleSum / Math.min(channels, 2); // Average if stereo, just value if mono
                samples[extractedSamples++] = averageSample;
            }
    
            System.out.println("Extracted " + extractedSamples + " mono samples from " + filePath);
    
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Audio file is not supported");
        } catch (IOException e) {
            System.out.println("Something went wrong while reading the file");
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
