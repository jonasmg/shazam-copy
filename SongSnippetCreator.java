import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SongSnippetCreator {

    public static void main(String[] args) {
        String songDatabase = "SongList/";
        int length = 60; // Seconds
        String snippetListFolder = "snippetListGenerated" + length +"/";

        List<Object[]> fileNames = FileProcessor.getFiles2(songDatabase);

        if (fileNames.isEmpty()) {
            System.out.println("No files found in the specified directory.");
            return;
        }

        for (int i = 0; i < 5; i++) {
            int randomIndex = (int) (Math.random() * fileNames.size());
            Object[] fileInfo = fileNames.get(randomIndex);
            String fileName = (String) fileInfo[0];
            String path = (String) fileInfo[1];
            String genreTuple = (String) fileInfo[2];

            audioSample audioFile = new audioSample();
            audioFile.setFile(path);
            int numSamples = audioFile.getMaxSamples();
            int sampleRate = audioFile.getSampleRate();
            audioFile.setNumSamples(numSamples);
            int samplesMax = sampleRate * length;

            // Print samples and sample rate
            System.out.println("Num Samples: " + numSamples);
            System.out.println("Sample Rate: " + sampleRate);
            System.out.println("Samples Max: " + samplesMax);

            audioFile.computeSamplesStereo();
            int[] originalSamples = audioFile.getSamples();
            // print originalSamples length
            System.out.println("Original Samples Length: " + originalSamples.length);

            // Get create name of file with album and track
            String[] pathParts = path.split("/");
            String albumName = pathParts[pathParts.length - 2];

            String finalFileName = albumName + "_" + fileName;

            if (originalSamples.length <= samplesMax) {
                System.out.println("Skipping " + finalFileName + " — not enough mono samples: " + numSamples);
                continue;
            } else {
                System.out.println("Processing " + finalFileName + " — enough mono samples: " + numSamples);
            }

            int randomSampleIndex = (int) (Math.random() * (numSamples - samplesMax));
            int[] snippetSamples = new int[samplesMax];

            for (int j = 0; j < samplesMax; j++) {
                snippetSamples[j] = originalSamples[randomSampleIndex + j];
            }


            // Made by ChatGPT
            byte[] byteBuffer = new byte[snippetSamples.length * 2];
            for (int j = 0; j < snippetSamples.length; j++) {
                int x = snippetSamples[j];
                if (x > 32767) x = 32767;
                if (x < -32768) x = -32768;
                byteBuffer[2 * j] = (byte) (x & 0xff);
                byteBuffer[2 * j + 1] = (byte) ((x >> 8) & 0xff);
            }

            AudioFormat format = new AudioFormat((float) sampleRate, 16, 1, true, false);
            ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, snippetSamples.length);
            // End of ChatGPT

            // Create the output directory if it doesn't exist
            File snippetDir = new File(snippetListFolder);
            if (!snippetDir.exists()) {
                snippetDir.mkdirs();
            }

            // Filename without ".wav"
            String fileNameWithoutExtension = finalFileName.substring(0, finalFileName.lastIndexOf('.'));

            String snippetFileName = snippetListFolder + fileNameWithoutExtension + "_GenSnip_" + randomSampleIndex + "_" + length + ".wav";
            File out = new File(snippetFileName);

            try {
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
                audioInputStream.close();
                System.out.println("Generated Snippet: " + snippetFileName);
            } catch (IOException e) {
                System.err.println("Failed to write snippet: " + e.getMessage());
            }

            System.out.println("Random File: " + finalFileName + ", Path: " + path + ", Genre: " + genreTuple + ", Num Samples: " + numSamples + ", Sample Rate: " + sampleRate);
        }
    }
}
