import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {

        // List for song database
        File folder = new File("songList");
        File[] listOfFiles = folder.listFiles();
        String[] fileNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            fileNames[i] = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length() - 4);
        }

        // List for snippet database
        folder = new File("snippetList");
        listOfFiles = folder.listFiles();
        String[] snippetNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            snippetNames[i] = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length() - 4);
        }

        // set them to empty
        fileNames = new String[0];
        snippetNames = new String[0];

        String[] filePaths = new String[fileNames.length];

        String[] snippetFilePaths = new String[snippetNames.length];


        // Append to each file name the audio/ path
        for (int i = 0; i < fileNames.length; i++) {
            filePaths[i] = "songList/" + fileNames[i] + ".wav";
        }

        // Append to each file name the snippetList/ path
        for (int i = 0; i < snippetNames.length; i++) {
            snippetFilePaths[i] = "snippetList/" + snippetNames[i] + ".wav";
        }

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
                System.out.println("Processing file: " + fileNames[ii] + " Quality: " + fourierQualityList[i]);
                // Get file length in seconds and set the pixelWidth to 20 times the length
                audioSample a = new audioSample();
                a.setFile(filePaths[ii]);
                double audioLength = a.getLength();
                pixelWidth = (int) (audioLength * secondLength);

                // Output folder for vectorlist as vectorLists
                String outputFolder = "vectorLists/";
    
                processFile(filePaths[ii], fileNames[ii], bins, fourierQualityList[i], pixelHeight, pixelWidth, secondLength, outputFolder, targetZone);
            }
        }

        // Create input database
        // For each fourierQuality
        for (int i = 0; i < fourierQualityList.length; i++) {
            // for each file name
            for (int ii = 0; ii < snippetNames.length; ii++) {
                // Print status
                System.out.println("Processing file: " + snippetNames[ii] + " Quality: " + fourierQualityList[i]);
                // Get file length in seconds and set the pixelWidth to 20 times the length
                audioSample a = new audioSample();
                a.setFile(snippetFilePaths[ii]);
                double audioLength = a.getLength();
                pixelWidth = (int) (audioLength * secondLength);

                // Output folder for vectorlist as vectorLists
                String outputFolder = "vectorListsInput/";
    
                processFile(snippetFilePaths[ii], snippetNames[ii], bins, fourierQualityList[i], pixelHeight, pixelWidth, secondLength, outputFolder, targetZone);
            }
        }

        // Take every file in vectorListsInput
        folder = new File("vectorListsInput");
        listOfFiles = folder.listFiles();
        String[] textFileNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            textFileNames[i] = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length() - 11);
        }
        
        String[] textFilePaths = new String[textFileNames.length];

        for (int i = 0; i < textFileNames.length; i++) {
            textFilePaths[i] = "vectorListsInput/" + textFileNames[i] + "Vectors.txt";
        }

        // Compare vector files for each file in textFileNames
        for (int i = 0; i < textFileNames.length; i++) {
            // Print status
            System.out.println("Processing file: " + textFileNames[i]);
            // Compare vectors
            compareVectors(textFilePaths[i]);
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
        logorithmic_bins(fft, bins, pixelHeight, pixelWidth, logBase);
        // linear_bins(fft, bins, pixelHeight, pixelWidth);

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

    public static ArrayList<int[]> kMeans(ArrayList<int[]> points, int k) {
        if (points.isEmpty()) return new ArrayList<>();
        Random rand = new Random();

        // Initialize cluster centers randomly
        ArrayList<int[]> centers = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            centers.add(points.get(rand.nextInt(points.size())));
        }

        boolean changed = true;
        while (changed) {
            // Assign each point to the nearest center
            ArrayList<ArrayList<int[]>> clusters = new ArrayList<>();
            for (int i = 0; i < k; i++) clusters.add(new ArrayList<>());

            for (int[] point : points) {
                int closestIndex = 0;
                double closestDist = Double.MAX_VALUE;

                for (int i = 0; i < k; i++) {
                    double dist = distance(point, centers.get(i));
                    if (dist < closestDist) {
                        closestDist = dist;
                        closestIndex = i;
                    }
                }

                clusters.get(closestIndex).add(point);
            }

            // Update centers to the mean of each cluster
            changed = false;
            for (int i = 0; i < k; i++) {
                if (!clusters.get(i).isEmpty()) {
                    int[] newCenter = meanPoint(clusters.get(i));
                    if (!equalPoints(newCenter, centers.get(i))) {
                        centers.set(i, newCenter);
                        changed = true;
                    }
                }
            }
        }

        return centers;
    }

    public static double distance(int[] a, int[] b) {
        return Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2));
    }

    public static int[] meanPoint(ArrayList<int[]> cluster) {
        int sumX = 0, sumY = 0;
        for (int[] point : cluster) {
            sumX += point[0];
            sumY += point[1];
        }
        return new int[]{sumX / cluster.size(), sumY / cluster.size()};
    }

    public static boolean equalPoints(int[] a, int[] b) {
        return a[0] == b[0] && a[1] == b[1];
    }

    public static void compareVectors(String filePath) {
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

                // Divide by 2
                int step = (int) offsetDifference / 2;

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
                for (int i = 0; i < 4; i++) {
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

        // Print results
        for (Object[] result : results2) {
            // If not the same file
            if (!result[0].equals(filePath.substring(0, filePath.length() - 12))) {
                // System.out.println(result[0] + " - Found vec: " + result[1] + " Out of: " + result[2] + " " + result[3] + "% LinearCount: " + result[4]);
                
                // Print results where each result gets 15 chars of length
                System.out.printf("%-22s Found vec: %-8s Out of: %-10s %% %-4s LinearCount: %-8s\n", result[0], result[1], result[2], result[3], result[4]);

                // // print file name and percentage of how sure they are with the name always being 25 chars and percent is over 4
                // if ((int) result[5] > 6) {
                //     System.out.printf("%-22s %s%%\n", result[0], result[5]);
                // }
            }
        }
    }

    public static void linear_bins(int[][] fft, int bins, int pixelHeight, int pixelWidth) {
        // Linear bins
        if (bins != 0) {
            int binSize = Math.max(1, pixelHeight / bins); // Ensure at least 1 pixel per bin

            // Iterate over columns
            for (int i = 0; i < pixelWidth; i++) {
                int j = 0; // Start at the top of the column

                while (j < pixelHeight) {
                    int maxBin = Integer.MIN_VALUE; // Start with the smallest value
                    int maxBinIndex = j;

                    // Find max value within this bin
                    for (int k = 0; k < binSize && (j + k) < pixelHeight; k++) {
                        if (fft[j + k][i] > maxBin) {
                            maxBin = fft[j + k][i];
                            maxBinIndex = j + k;
                        }
                    }

                    // Set all pixels in the bin to 0 except the max
                    for (int k = 0; k < binSize && (j + k) < pixelHeight; k++) {
                        int currentIndex = j + k;
                        if (currentIndex != maxBinIndex) {
                            fft[currentIndex][i] = 0; // Zero out other pixels
                        }
                    }

                    // Move to the next bin
                    j += binSize;
                }
            }
        }
    }

    public static void logorithmic_bins(int[][] fft, int bins, int pixelHeight, int pixelWidth, double logBase) {
        // Logarithmic bins
        if (bins != 0) {

            // Iterate over columns
            for (int i = 0; i < pixelWidth; i++) {
                int binIndex = 0; // Track bin number
                int j = 0; // Start at the top of the column

                while (j < pixelHeight) {
                    int maxBin = Integer.MIN_VALUE; // Start with the smallest value
                    int maxBinIndex = j;

                    // Determine bin size (ensure at least 1 to prevent infinite loops)
                    int binSize = Math.max(1, (int) Math.pow(logBase, binIndex));
                    binSize = Math.min(binSize, pixelHeight - j); // Ensure within bounds

                    // Find max value within this bin
                    for (int k = 0; k < binSize; k++) {
                        if (fft[j + k][i] > maxBin) {
                            maxBin = fft[j + k][i];
                            maxBinIndex = j + k;
                        }
                    }

                    // Set all pixels in the bin to 0 except the max
                    for (int k = 0; k < binSize; k++) {
                        int currentIndex = j + k;
                        if (currentIndex != maxBinIndex) {
                            fft[currentIndex][i] = 0; // Zero out other pixels
                        }
                    }

                    // Move to the next bin
                    j += binSize;
                    binIndex++;
                }
            }
        }
    }
}
