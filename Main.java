import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {
        String[][] fileNames = new String[0][0]; // First is name of file, second is the path
        String[][] snippetNames = new String[0][0];

        // fileNames = getFiles("songList");
        // snippetNames = getFiles("snippetList");

        int bins = 6;
        int[] fourierQualityList = {24};
        int pixelHeight = 128*5;
        int pixelWidth;
        int secondLength = 6;

        // Obj for target zone
        int targetHeight = 20; // Height above and below point
        int targetLength = 30;
        int lengthToTarget = 2; // Length to target zone
        int[] targetZone = {targetHeight, targetLength, lengthToTarget};

        // Create database
        // For each fourierQuality
        for (int i = 0; i < fourierQualityList.length; i++) {
            // for each file name
            for (int ii = 0; ii < fileNames.length; ii++) {
                // Print status
                System.out.println("Processing file: " + fileNames[ii][0] + " Quality: " + fourierQualityList[i]);
                // Get file length in seconds and set the pixelWidth to 20 times the length
                audioSample a = new audioSample();
                a.setFile(fileNames[ii][1]);
                double audioLength = a.getLength();
                pixelWidth = (int) (audioLength * secondLength);

                // Output folder for vectorlist as vectorLists
                String outputFolder = "vectorLists/";
    
                processFile(fileNames[ii][1], fileNames[ii][0], bins, fourierQualityList[i], pixelHeight, pixelWidth, secondLength, outputFolder, targetZone);
            }
        }

        // Create input database
        // For each fourierQuality
        for (int i = 0; i < fourierQualityList.length; i++) {
            // for each file name
            for (int ii = 0; ii < snippetNames.length; ii++) {
                // Print status
                System.out.println("Processing file: " + snippetNames[ii][1] + " Quality: " + fourierQualityList[i]);
                // Get file length in seconds and set the pixelWidth to 20 times the length
                audioSample a = new audioSample();
                a.setFile(snippetNames[ii][1]);
                double audioLength = a.getLength();
                pixelWidth = (int) (audioLength * secondLength);

                // Output folder for vectorlist as vectorLists
                String outputFolder = "vectorListsInput/";
    
                processFile(snippetNames[ii][1], snippetNames[ii][0], bins, fourierQualityList[i], pixelHeight, pixelWidth, secondLength, outputFolder, targetZone);
            }
        }

        // Take every file in vectorListsInput
        String[][] textFileNames = new String[0][0];
        textFileNames = getFiles("vectorListsInput");

        // Empty obj list for results
        ArrayList<Object[]> results = new ArrayList<>();

        // VectorDatabase list sorted from [0]
        ArrayList<Object[]> databaseVectors = new ArrayList<>();
        databaseVectors = getVectorDatabase("vectorLists");

        // Compare vector files for each file in textFileNames
        for (int i = 0; i < textFileNames.length; i++) {
            // Print status
            System.out.println("Processing " + textFileNames[i][0] + "...");
            // Compare vectors and add to database with filename
            Object comparisonResult = compareVectorsv2(textFileNames[i][1], databaseVectors);
            // Pull out the filename and then count and then add to results with input filename
            ArrayList<Object[]> comparisonResults = (ArrayList<Object[]>) comparisonResult;
            results.add(new Object[]{textFileNames[i][0], comparisonResults});
        }

        // Print results
        for (Object[] result : results) {
            String fileName = (String) result[0];
            // Print input filename
            System.out.println("\nInput file: " + fileName);
            // Print results where each result gets 15 chars of length, so print song and count
            for (int i = 1; i < 6; i++) {
                if (i < ((ArrayList<Object[]>) result[1]).size()) {
                    Object[] songResult = ((ArrayList<Object[]>) result[1]).get(i - 1);
                    String songName = (String) songResult[0];
                    int count = (int) songResult[1];
                    // Print song name and count
                    System.out.printf("%-22s Found vectors: %d", songName, count);
                    // If first 5 chars match print "success"
                    if (fileName.substring(0, 5).equals(songName.substring(0, 5))) {
                        System.out.println(" - Correct");
                    } else {
                        System.out.println();
                    }
                }
            }
        }
    }

    // helper functiong process File
    public static void processFile(String filePath, String fileName, int bins, int fourierQuality, int pixelHeight, int pixelWidth, int secondLength, String outputFolder, int[] targetZone) {
        audioSample a = new audioSample();
        a.setFile(filePath);
        int numSamples = a.getMaxSamples();
        double audioLength = a.getLength();
        System.out.printf("Audio length: %.2f seconds\n", audioLength);
        a.setNumSamples(numSamples);
        a.setStepSize(1);
        a.computeSamples();
        int[] samples = a.getSamples();

        // Print update
        System.out.println("Number of samples: " + numSamples);
        System.out.println("SampleRate: " + a.getSampleRate());

        // Get resolution from calculateFourierTransform
        CalculateFourierTransform c = new CalculateFourierTransform();
        c.setSampleRate(a.getSampleRate());
        c.setSampleSize(numSamples);
        c.setSamples(samples);

        // Print status
        System.out.println("Calculating FFT with quality " + fourierQuality + "...");

        // Quality
        c.setFourierQuality(fourierQuality);
        c.setPixelHeight(pixelHeight);
        c.setPixelWidth(pixelWidth);

        int[][] fft = c.calculateFastFourierTransform();
        // Print status
        System.out.println("FFT calculated!");

        double maxMagnitude = c.getMaxMagnitude();
        // Print maxMagnitude
        System.out.println("Max magnitude: " + maxMagnitude);

        //print max and min value in fft
        int max = 0;
        int min = 0;
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                max = Math.max(max, fft[j][i]);
                min = Math.min(min, fft[j][i]);
                // Make sure its the absolute value we get
                fft[j][i] = Math.abs(fft[j][i]);
            }
        }
        System.out.println("Max value in fft: " + max);
        System.out.println("Min value in fft: " + min);

        // Get frame length and shift
        int frameLength = c.getFrameLength();
        int shift = c.getFrameShift();

        // Create new image for after being processed:
        int[][] fftCopy = new int[pixelHeight][pixelWidth];
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                fftCopy[j][i] = fft[j][i];
            }
        }

        int columns = secondLength*2; // columns is used for size of sensitivity

        // Iterate over columns first
        for (int i = 0; i < pixelWidth; i++) {
            ArrayList<Integer> valuesList = new ArrayList<>();

            // Gather values from the 4 columns before, 4 columns after, and the current column
            for (int k = -columns; k <= columns; k++) {
                if (i + k >= 0 && i + k < pixelWidth) {
                    for (int j = 0; j < pixelHeight; j++) {
                        valuesList.add(fftCopy[j][i + k]); 
                    }
                }
            }

            // Convert list to array and sort for percentile calculation
            int[] values = valuesList.stream().mapToInt(Integer::intValue).toArray();
            java.util.Arrays.sort(values);

            // Compute percentiles (ensure indices are within bounds)
            int percentileIndex1 = Math.max(0, Math.min(values.length - 1, (int) (0.99 * values.length)));
            int percentileIndex2 = Math.max(0, Math.min(values.length - 1, (int) (0.98 * values.length)));
            int percentileIndex3 = Math.max(0, Math.min(values.length - 1, (int) (0.97 * values.length)));

            int percentile1 = values[percentileIndex1];
            int percentile2 = values[percentileIndex2];
            int percentile3 = values[percentileIndex3];

            // Now iterate over the height (rows) and apply the computed percentile scaling
            for (int j = 0; j < pixelHeight; j++) {
                if (fftCopy[j][i] > percentile1) {
                    fft[j][i] = (int) ((fftCopy[j][i] / (double) percentile1) * 255);
                } else if (fftCopy[j][i] > percentile2) {
                    fft[j][i] = (int) ((fftCopy[j][i] / (double) percentile1) * 255 * 0.95);
                } else if (fftCopy[j][i] > percentile3) {
                    fft[j][i] = (int) ((fftCopy[j][i] / (double) percentile2) * 255 * 0.90);
                } else {
                    fft[j][i] = 0;
                }
            }
        }

        // Maybe iterate over columns like before but only keep highest value in time frames of 4 columns

        // Define the logarithmic scale factor
        double logBase = 2; // Adjust this base for different growth rates

        // Run logorithmic bins on fft
        BinCalculations.logorithmic_bins(fft, bins, pixelHeight, pixelWidth, logBase);
        // BinCalculations.linear_bins(fft, bins, pixelHeight, pixelWidth);

        // Set target zone
        int targetHeight = targetZone[0];
        int targetLength = targetZone[1];
        int lengthToTarget = targetZone[2];

        // Create vector list
        ArrayList<int[]> vectors = new ArrayList<>();

        // Iterate through the width and height of fft
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                // If the pixel is nonzero
                if (fft[j][i] != 0) {
                    // Define target zone boundaries
                    int startK = Math.max(i + lengthToTarget, 0);
                    int endK = Math.min(i + lengthToTarget + targetLength, pixelWidth);
                    int startL = Math.max(j - targetHeight, 0);
                    int endL = Math.min(j + targetHeight, pixelHeight);

                    // Search target zone
                    for (int k = startK; k < endK; k++) {
                        for (int l = startL; l < endL; l++) {
                            if (fft[l][k] > 140) {
                                // Store pixelheight1, pixelheight2, pixelwidth difference and pixelwidth position of pixel1
                                vectors.add(new int[]{j, l, k - i, i});
                            }
                        }
                    }
                }
            }
        }

        // Print status
        System.out.println("Creating image...");

        // Create buffered image
        BufferedImage bufferedImage = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                int value = fft[j][i];
                value = Math.min(value, 255);
                int rgb = new Color(value, value, value).getRGB();
                bufferedImage.setRGB(i, j, rgb);
            }
        }

        // File name, original + info
        String imageName = "output/" + fileName + "-Q" + fourierQuality + "fft" + bins + "B-F" + frameLength + "S" + shift + "col-" + columns + "Clean.png";
        
        File file2 = new File(imageName);
        try {
            ImageIO.write(bufferedImage, "png", file2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Print status
        System.out.println("Image created!");
        
        
        // Creating File for vectors
        String vectorFileName = outputFolder + fileName + "Vectors.txt";
        File file4 = new File(vectorFileName);
        try {
            file4.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write to text file
        try {
            FileWriter writer = new FileWriter(file4);
            for (int[] vector : vectors) {
                writer.write(vector[0] + " " + vector[1] + " " + vector[2] + " " + vector[3] + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print vector file info
        System.out.println("Vector file created with " + vectors.size() + " vectors!");
    }

    public static boolean equalPoints(int[] a, int[] b) {
        return a[0] == b[0] && a[1] == b[1];
    }

    public static ArrayList<Object[]> compareVectors(String filePath) {
        // Read vectors from file
        ArrayList<int[]> vectors = new ArrayList<>();
        try {
            File file = new File(filePath
            );
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                vectors.add(new int[]{
                                        Integer.parseInt(parts[0]),
                                        Integer.parseInt(parts[1]),
                                        Integer.parseInt(parts[2]),
                                        Integer.parseInt(parts[3])
                                    });
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Compare vectors to each file in vectorLists
        File folder = new File("vectorLists");
        File[] files = folder.listFiles();

        // Results list with information for name and vectors should be both int int and string as tuple
        ArrayList<Object[]> results = new ArrayList<>();

        for (File file : files) {
            if (file.isFile()) {
                ArrayList<int[]> vectors2 = new ArrayList<>();
                try {
                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] parts = line.split(" ");
                        vectors2.add(new int[]{
                                                Integer.parseInt(parts[0]),
                                                Integer.parseInt(parts[1]), 
                                                Integer.parseInt(parts[2]),
                                                Integer.parseInt(parts[3])
                                            });
                    }
                    scanner.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // List of vectors3
                ArrayList<int[]> vectors3 = new ArrayList<>();

                // Compare vectors
                int count = 0;
                for (int[] vector : vectors) {
                    for (int[] vector2 : vectors2) {
                        if (vector[0] == vector2[0] && vector[1] == vector2[1] && vector[2] == vector2[2]) {
                            vectors3.add(vector2);
                        }
                    }
                }

                // Remove duplicates from vectors3
                vectors3 = new ArrayList<>(vectors3.stream().distinct().collect(Collectors.toList()));

                // Set count to number of distinct of vectors 3
                count = vectors3.size();

                // Linear count
                int linearCount = 0;

                // Sort vectors3 by offset
                vectors3.sort((a, b) -> a[3] - b[3]);

                // Sort vectors by offset
                vectors.sort((a, b) -> a[3] - b[3]);

                // Get smallest and largest offset of vectors
                int smallestOffset = vectors.get(0)[3];
                int largestOffset = vectors.get(vectors.size() - 1)[3];

                // Get difference
                int offsetDifference = largestOffset - smallestOffset;

                // Divide by dividesize
                int dividesize = 4;
                int step = (int) offsetDifference / dividesize;

                // array of vectors4
                ArrayList<int[]> vectors4 = new ArrayList<>();

                // Iterate over the vectors3 and count how many vectors are in each step with step size step
                for (int i = 0; i <= vectors3.size() - step; i += step) {
                    int stepCount = 0;
                    for (int j = i; j <= i + step; j++) {
                        for (int[] vector : vectors3) {
                            if (vector[3] == j) {
                                stepCount++;
                            }
                        }
                    }
                    vectors4.add(new int[]{i, stepCount});
                }

                // Take 4 largest steps and plus and add to linearCount
                vectors4.sort((a, b) -> b[1] - a[1]);
                for (int i = 0; i < dividesize*2; i++) {
                    // If vectors4 is not empty
                    if (vectors4.size() > i) {
                        linearCount += vectors4.get(i)[1];
                    }
                }

                // Calculate percentage as int
                int percentage = (int) (100.0 * count / vectors.size());

                // File name as string:
                String fileName = file.getName().substring(0, file.getName().length() - 11);

                // Save information to results with name, count, size, percentage and linearCount
                results.add(new Object[]{fileName, count, vectors2.size(), percentage, linearCount});

            }
        }

        // Sort results from linearCount
        results.sort((a, b) -> (int) b[4] - (int) a[4]);

        // // Sort results from percentage
        // results.sort((a, b) -> (int) b[3] - (int) a[3]);

        // // Sort results from count
        // results.sort((a, b) -> (int) b[1] - (int) a[1]);

        // Give percentage of how sure it is that the file is the  by first making list
        ArrayList<Object[]> results2 = new ArrayList<>();

        // Add all linearCount together
        int linearCountTotal = 0;
        for (Object[] result : results) {
            linearCountTotal += (int) result[4];
        }

        // Calculate percentage of linearCount for each file and add it as result[5]
        for (Object[] result : results) {
            int linearCount = (int) result[4];
            int percentage = (int) (100.0 * linearCount / linearCountTotal);
            results2.add(new Object[]{result[0], result[1], result[2], result[3], result[4], percentage});
        }

        return results2;

        // // Print results
        // for (Object[] result : results2) {
        //     // If not the same file
        //     if (!result[0].equals(filePath.substring(0, filePath.length() - 12))) {
        //         // System.out.println(result[0] + " - Found vec: " + result[1] + " Out of: " + result[2] + " " + result[3] + "% LinearCount: " + result[4]);
                
        //         // Print results where each result gets 15 chars of length
        //         System.out.printf("%-22s Found vec: %-8s Out of: %-10s %% %-4s LinearCount: %-8s\n", result[0], result[1], result[2], result[3], result[4]);

        //         // // print file name and percentage of how sure they are with the name always being 25 chars and percent is over 4
        //         // if ((int) result[5] > 6) {
        //         //     System.out.printf("%-22s %s%%\n", result[0], result[5]);
        //         // }
        //     }
        // }
    }

    public static Object compareVectorsv2(String filePath, ArrayList<Object[]> databaseVectors) {
        // Read vectors from file
        ArrayList<int[]> inputVectors = new ArrayList<>();

        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                inputVectors.add(new int[]{
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])
                });
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // New object list for vectors found
        ArrayList<Object[]> databaseVectorsFound = new ArrayList<>();

        // List of distinct songs
        ArrayList<String> distinctSongs = new ArrayList<>();

        // Compare vectors to each file in vectors
        for (int[] vector : inputVectors) {
            // Binary search for the vector[0] placement in databaseVectors only checking [0]
            int low = 0;
            int high = databaseVectors.size() - 1;
            int placement = -1;
            while (low <= high) {
                int mid = (low + high) / 2;
                if ((int) databaseVectors.get(mid)[0] < vector[0]) {
                    low = mid + 1;
                } else if ((int) databaseVectors.get(mid)[0] > vector[0]) {
                    high = mid - 1;
                } else {
                    placement = mid;
                    break;
                }
            }

            // Find first instance of [0] from the placement
            if (placement != -1) {
                while (placement > 0 && (int) databaseVectors.get(placement - 1)[0] == vector[0]) {
                    placement--;
                }
            }

            // If placement is not -1, add to databaseVectorsFound
            if (placement != -1) {
                // While [0] matches keep adding vectors to databaseVectorsFound
                while (placement < databaseVectors.size() && vector[0] == (int) databaseVectors.get(placement)[0]) {
                    // Check if vector[1] and vector[2] match
                    if (vector[1] == (int) databaseVectors.get(placement)[1] && vector[2] == (int) databaseVectors.get(placement)[2]) {
                        // Add to databaseVectorsFound
                        databaseVectorsFound.add(databaseVectors.get(placement));
                        if (!distinctSongs.contains((String) databaseVectors.get(placement)[4])) {
                            distinctSongs.add((String) databaseVectors.get(placement)[4]);
                        }
                    }
                    placement++;
                }
            }
        }

        // Remove duplicates from vectors3
        databaseVectorsFound = new ArrayList<>(databaseVectorsFound.stream().distinct().collect(Collectors.toList()));

        // For each distinct song count how many vectors are in it
        ArrayList<Object[]> results = new ArrayList<>();
        for (String song : distinctSongs) {
            // Count how many vectors are in the song
            int count = 0;
            for (Object[] dbVector : databaseVectorsFound) {
                if (dbVector[4].equals(song)) {
                    count++;
                }
            }
            // Add to results
            results.add(new Object[]{song, count});
        }

        // Sort results from count
        results.sort((a, b) -> (int) b[1] - (int) a[1]);

        // // Print results
        // for (Object[] result : results) {
        //     // Print results where each result gets 15 chars of length, so print song and count
        //     System.out.printf("%-22s Found vec: %-8s\n", result[0], result[1]);
        // }

        // // Print top 5 results
        // for (int i = 0; i < 5 && i < results.size(); i++) {
        //     // Print results where each result gets 15 chars of length, so print song and count
        //     System.out.printf("%-22s Found vec: %-8s\n", results.get(i)[0], results.get(i)[1]);
        // }

        return results;
    }

    public static ArrayList<Object[]> getVectorDatabase(String folderPath) {
        // Create list of all vectors from database
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        // Results list with information for name and vectors should be both int int and string as tuple
        ArrayList<Object[]> databaseVectors = new ArrayList<>();

        for (File file : files) {
            if (file.isFile()) {
                try {
                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] parts = line.split(" ");
                        databaseVectors.add(new Object[]{
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]), 
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]),
                            file.getName().substring(0, file.getName().length() - 11)
                        });
                    }
                    scanner.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        // Sort databaseVectors [0]
        databaseVectors.sort((a, b) -> (int) a[0] - (int) b[0]);

        return databaseVectors;
    }

    public static String[][] getFiles(String folderPath) {
        // Get all files in folder
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        String[] fileNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            fileNames[i] = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length() - 4);
        }
        String[] filePaths = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            filePaths[i] = listOfFiles[i].getPath();
        }
        String[][] result = new String[listOfFiles.length][2];
        for (int i = 0; i < listOfFiles.length; i++) {
            result[i][0] = fileNames[i];
            result[i][1] = filePaths[i];
        }
        return result;
    }
}
