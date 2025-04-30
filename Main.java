import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;

import org.w3c.dom.Text;

import java.awt.image.BufferedImage;
import java.awt.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static final String[] SONG = {"FourierLists", "VectorLists"};
    public static final String[] SNIPPET = {"FourierListsInput", "vectorListsInput"};
    
    public static void main(String[] args) {
        // First is fileName, second is path, third is genreTouple
        List<Object[]> fileNames = new ArrayList<>();
        List<Object[]> snippetNames = new ArrayList<>();

        // Get files of audio and snippets
        fileNames = FileProcessor.getFiles2("songList");
        snippetNames = FileProcessor.getSnippetFileNames("snippetList");

        // Print fileNames
        for (int i = 0; i < fileNames.size(); i++) {
            Object[] fileInfo = (Object[]) fileNames.get(i);
            String fileName = (String) fileInfo[0];
            String filePath = (String) fileInfo[1];
            String[] genres = ((String) fileInfo[2]).split(" ");
            System.out.println("File: " + fileName + " Path: " + filePath + " Genres: " + String.join(", ", genres));
        }

        int[] fourierQalityList = {(256 + 512), (512 + 512)};
        int[] windowSizeList = {250, 400};
        int[] overlapList = {0};
        double[] minFreqList = {200.0};
        double[] maxFreqList = {2000.0};

        int[] binsList = {4, 8};
        int[] targetHeightList = {30};
        int[] targetLengthList = {50};
        int[] lengthToTargetList = {4, 10};

        // Create arraylist object[] for all settings
        ArrayList<Object[]> settings = new ArrayList<>();

        // Function to add lists to settings
        addToSettings(settings, fourierQalityList, windowSizeList, overlapList,
                            minFreqList, maxFreqList, binsList, targetHeightList,
                            targetLengthList, lengthToTargetList);

        // Create folders if they dont exist for song and snippet
        File songFolder = new File(SONG[0]);
        File snippetFolder = new File(SNIPPET[0]);
        if (!songFolder.exists()) {
            songFolder.mkdirs();
            System.out.println("Created folder: " + songFolder);
        }
        if (!snippetFolder.exists()) {
            snippetFolder.mkdirs();
            System.out.println("Created folder: " + snippetFolder);
        }

        // Create song database file for each file in songList
        for (int iii = 0; iii < settings.size(); iii++) {
            for (int ii = 0; ii < fileNames.size(); ii++) {
                calculateFFTAndCreateVectorList(fileNames, ii, settings, iii, SONG);
            }
            for (int ii = 0; ii < snippetNames.size(); ii++) {
                calculateFFTAndCreateVectorList(snippetNames, ii, settings, iii, SNIPPET);
            }
        }

        // For every folder in fourierLists, create a database with corrosponding names
        String[] folders = FileProcessor.getFolders(SONG[0]);

        // For each folder get the folders inside vectorLists and create a database
        for (String folder : folders) {
            // Go inside the VectorLists folder
            File vectorListsFolder = new File(SONG[0] + "/" + folder + "/" + SONG[1]);
            // Get all folders inside vectorLists
            File[] vectorLists = vectorListsFolder.listFiles();
            // Check if vectorLists is not null
            if (vectorLists == null) {
                System.out.println("No vector lists found in " + folder + " With: " + vectorListsFolder);
                continue;
            }

            // For each folder in vectorLists, create a database
            for (File vectorList : vectorLists) {
                // Get the name of the folder
                String folderName = vectorList.getName();
                // Create an in memory database with the name of the folder in workspace/VectorListOutput
                String databaseName = folder + folderName;
                // For every text file in the folder, add to database
                File[] files = vectorList.listFiles();
                // Check if files is not null
                if (files == null) {
                    System.out.println("No files found in " + folderName + " With: " + vectorList);
                    continue;
                }

                System.out.println("______________________________________________________________");
                System.out.println("Creating darabase for " + databaseName);
                System.out.println("______________________________________________________________");

                ArrayList<Object[]> databaseVectors = new ArrayList<>();

                // Create arraylist for summary results
                ArrayList<Object[]> summaryResults = new ArrayList<>();

                // For each file in the folder, write to the database
                for (File file : files) {
                    // Get the name of the file
                    String fileName = file.getName();
                            
                    // If it's a text file open and write all lines to the database with the name of the file added to the start
                    if (fileName.endsWith(".txt")) {
                        // Open the file
                        Scanner scanner = null;
                        try {
                            scanner = new Scanner(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        // Write the name of the file to the database
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            String[] parts = line.split(" ");
                            int vectorX = Integer.parseInt(parts[0]);
                            int vectorY = Integer.parseInt(parts[1]);
                            int vectorZ = Integer.parseInt(parts[2]);
                            int vectorW = Integer.parseInt(parts[3]);
                            databaseVectors.add(new Object[]{vectorX, vectorY, vectorZ, vectorW, fileName});
                        }
                        // Close the scanner
                        scanner.close();
                    }
                }

                // Sort the database vectors by the first element, 2nd and 3rd
                databaseVectors = (ArrayList<Object[]>) databaseVectors.stream()
                    .sorted((a, b) -> {
                        int cmp = Integer.compare((int) a[0], (int) b[0]);
                        if (cmp != 0) return cmp;
                        cmp = Integer.compare((int) a[1], (int) b[1]);
                        if (cmp != 0) return cmp;
                        return Integer.compare((int) a[2], (int) b[2]);
                    })
                    .collect(Collectors.toList());


                // Now compare vectors to all files in snippetVectors of the same type
                String snippetVectorFolder = SNIPPET[0] + "/" + folder + "/" + SNIPPET[1] + "/" + folderName;

                // Make list of all text files in the folder
                File snippetVectorList = new File(snippetVectorFolder);
                File[] snippetVectorFiles = snippetVectorList.listFiles();
                // Check if snippetVectorFiles is not null
                if (snippetVectorFiles == null) {
                    System.out.println("No snippet vector files found in " + folderName + " With: " + snippetVectorList);
                    continue;
                }
                // For each file in the folder remove if not txt file
                List<File> snippetVectorFilesList = new ArrayList<>();
                for (File snippetVectorFile : snippetVectorFiles) {
                    if (snippetVectorFile.getName().endsWith(".txt")) {
                        snippetVectorFilesList.add(snippetVectorFile);
                    }
                }

                // Make results list
                ArrayList<Object[]> results = new ArrayList<>();

                // For each file in the folder compare the vectors to the database with compareVectorsv2
                for (File snippetVectorFile : snippetVectorFilesList) {
                    // Get the name of the file
                    String snippetFileName = snippetVectorFile.getName();

                    // compare with compareVectorsv2
                    // Get the file path
                    String snippetFilePath = snippetVectorFile.getAbsolutePath();
                    Object comparisonResult = compareVectorsv2(snippetFilePath, databaseVectors);

                    // Pull out the filename and then count and then add to results with input filename
                    ArrayList<Object[]> comparisonResults = (ArrayList<Object[]>) comparisonResult;
                    results.add(new Object[]{snippetFilePath, comparisonResults});
                }

                // Print results
                summaryResults = printResults(results);
                
                // Print summary results by counting how many successes vs failures
                
                // Count successes and failures
                int successes = 0;
                int kinda = 0;
                int failures = 0;
                for (Object[] summaryResult : summaryResults) {
                    if ((int) summaryResult[1] == 0) {
                    successes++;
                } else if ((int) summaryResult[1] == 1 || (int) summaryResult[1] == 2) {
                    kinda++;
                } else {
                    failures++;
                }
            }
            // Print summary results
            System.out.println("\nSummary results for " + folder + folderName + ":");
            System.out.println("Successes: " + successes);
            System.out.println("Kinda: " + kinda);
            System.out.println("Failures: " + failures + "\n");
            }
        }

    }

    public static String createFourierTransform(Object[] fileInfo, int fourierQuality, int windowSize, int overlap, double[] freqRange, String outputFolder) {
        // Get file name and path
        String fileName = (String) fileInfo[0];
        String filePath = (String) fileInfo[1];
        String[] genres = ((String) fileInfo[2]).split(" ");

        // Get create name of file with album and track
        String[] pathParts = filePath.split("/");
        String albumName = pathParts[pathParts.length - 2];

        String fourierFileName = albumName + "_" + fileName;

        // Create output folder if it doesn't exist
        File folder = new File(outputFolder);
        if (!folder.exists()) {
            folder.mkdirs();
            // Print create folder
            System.out.println("Created folder: " + outputFolder);
        }

        // Ready file name for stft
        String stftFileName = outputFolder + fourierFileName + "_stft.txt";
        File stftFile = new File(stftFileName);

        // If file already exists print such
        if (stftFile.exists()) {
            System.out.println("File already exists: " + stftFileName);
            return stftFile.getAbsolutePath();
        }

        audioSample a = new audioSample();
        a.setFile(filePath);
        int numSamples = a.getMaxSamples();
        double audioLength = a.getLength();
        System.out.printf("Audio length: %.2f seconds\n", audioLength);
        a.setNumSamples(numSamples);
        a.setStepSize(1);
        a.computeSamples();
        int[] samples = a.getSamples();
        int sampleRate = a.getSampleRate();

        // Print update
        System.out.println("Number of samples: " + numSamples);
        System.out.println("SampleRate: " + sampleRate);

        // Setup ShortTimeFourierTransform
        ShortTimeFourierTransform stft = new ShortTimeFourierTransform(sampleRate, fourierQuality, windowSize, overlap, freqRange[0], freqRange[1]);
        stft.setSignal(samples);
        int[][] stftResult = stft.computeSTFT();
        System.out.println("STFT calculated!");

        // Write to text file, each line is a row of the stftResult
        try {
            FileWriter writer = new FileWriter(stftFile);
            for (int i = 0; i < stftResult.length; i++) {
                for (int j = 0; j < stftResult[i].length; j++) {
                    writer.write(stftResult[i][j] + " ");
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Print status
        System.out.println("STFT file created!");

        // Return file path of new file
        return stftFile.getAbsolutePath();
    }

    public static void createVectorList(Object[] fileInfo, int bins, int[] targetZone, String fourierOutputFolder, String vectorOutputFolder, String newFilePath) {
        // Get file name and path
        String fileName = (String) fileInfo[0];
        String filePath = (String) fileInfo[1];
        String[] genres = ((String) fileInfo[2]).split(" ");

        // Create output folder if it doesn't exist
        File folder = new File(vectorOutputFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Get stft file info and name
        File stftFile = new File(newFilePath);
        String stftPath = stftFile.getAbsolutePath();
        String stftFileName = stftFile.getName();

        // Create file for vector list
        String vectorFileName = vectorOutputFolder + stftFileName + "_vectors.txt";
        File vectorFile = new File(vectorFileName);
        // Create the file if it doesn't exist
        try {
            vectorFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Extract file information, columns are defined by spaces and rows are line breaks
        // So create scanner to read the file
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(newFilePath));
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + newFilePath);
            return;
        }
        // Read the first line to get the number of columns
        String firstLine = scanner.nextLine();
        String[] firstLineParts = firstLine.split(" ");
        int numCols = firstLineParts.length;
        // Read the rest of the file to get the number of rows
        int numRows = 1; // Start from 1 to count the first line
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.trim().isEmpty()) {
                numRows++;
            }
        }
        scanner.close();

        // Create a 2D array to store the stft result
        int[][] stftResult = new int[numRows][numCols];
        // Read the file again to fill the array
        try {
            scanner = new Scanner(new File(newFilePath));
            int row = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(" ");
                    for (int col = 0; col < numCols; col++) {
                        stftResult[row][col] = Integer.parseInt(parts[col]);
                    }
                    row++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + newFilePath);
            return;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        // Create 2D array for proccessed stft
        int[][] proccessedFFT = new int[numRows][numCols];

        // Print status
        System.out.println("Creating vector list...");

        int columns = 4; // columns is used for size of sensitivity

        // Iterate over columns first
        for (int i = 0; i < numCols; i++) {
            ArrayList<Integer> valuesList = new ArrayList<>();

            // Gather values from the 4 columns before, 4 columns after, and the current column
            for (int k = -columns; k <= columns; k++) {
                if (i + k >= 0 && i + k < numCols) {
                    for (int j = 0; j < numRows; j++) {
                        valuesList.add(stftResult[j][i + k]); 
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
            for (int j = 0; j < numRows; j++) {
                if (stftResult[j][i] > percentile1) {
                    proccessedFFT[j][i] = (int) ((stftResult[j][i] / (double) percentile1) * 255);
                } else if (stftResult[j][i] > percentile2) {
                    proccessedFFT[j][i] = (int) ((stftResult[j][i] / (double) percentile1) * 255 * 0.95);
                } else if (stftResult[j][i] > percentile3) {
                    proccessedFFT[j][i] = (int) ((stftResult[j][i] / (double) percentile2) * 255 * 0.90);
                } else {
                    proccessedFFT[j][i] = 0;
                }
            }
        }

        // Maybe iterate over columns like before but only keep highest value in time frames of 4 columns

        // Define the logarithmic scale factor
        double logBase = 2; // Adjust this base for different growth rates

        // Run logorithmic bins on proccessedFFT
        BinCalculations.logorithmic_bins(proccessedFFT, bins, numRows, numCols, logBase);
        // BinCalculations.linear_bins(proccessedFFT, bins, numRows, numCols);

        // Save processed file
        // _______________________________________________________
        

        // // Create new file for processed stft image png file
        // String processedFileName = vectorOutputFolder + stftFileName + "_processed.png";
        // File processedFile = new File(processedFileName);
        // // Create the file if it doesn't exist
        // try {
        //     processedFile.createNewFile();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // // Write to text file using TextSpectrumToImage
        // TextSpectrumToImage.ArrayToImageWhite(proccessedFFT, processedFile);

        // Set target zone
        int targetHeight = targetZone[0];
        int targetLength = targetZone[1];
        int lengthToTarget = targetZone[2];

        // Create vector list
        ArrayList<int[]> vectors = new ArrayList<>();

        // Extra threshold for the target zone
        int extraThreshold = 140;

        // Iterate through the width and height of fft
        for (int i = 0; i < numCols; i++) {
            for (int j = 0; j < numRows; j++) {
                // If the pixel is nonzero
                if (proccessedFFT[j][i] != 0) {
                    // Define target zone boundaries
                    int startK = Math.max(i + lengthToTarget, 0);
                    int endK = Math.min(i + lengthToTarget + targetLength, numCols);
                    int startL = Math.max(j - targetHeight, 0);
                    int endL = Math.min(j + targetHeight, numRows);

                    // Search target zone
                    for (int k = startK; k < endK; k++) {
                        for (int l = startL; l < endL; l++) {
                            if (proccessedFFT[l][k] > extraThreshold) {
                                // Store numRows1, numRows2, numCols difference and pixelwidth position of pixel1
                                vectors.add(new int[]{j, l, k - i, i});
                            }
                        }
                    }
                }
            }
        }

        // Write to vector text file
        try {
            FileWriter writer = new FileWriter(vectorFile);
            for (int[] vector : vectors) {
                writer.write(vector[0] + " " + vector[1] + " " + vector[2] + " " + vector[3] + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    // Now look for [1]
                    if ((int) databaseVectors.get(mid)[1] < vector[1]) {
                        low = mid + 1;
                    } else if ((int) databaseVectors.get(mid)[1] > vector[1]) {
                        high = mid - 1;
                    } else {
                        // Now look for [2]
                        if ((int) databaseVectors.get(mid)[2] < vector[2]) {
                            low = mid + 1;
                        } else if ((int) databaseVectors.get(mid)[2] > vector[2]) {
                            high = mid - 1;
                        } else {
                            placement = mid;
                            break;
                        }
                    }
                }
            }

            // Find first instance of [0] from the placement
            if (placement != -1) {
                while (placement > 0 && (int) databaseVectors.get(placement - 1)[2] == vector[2]) {
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

    public static void compareResults(String[][] textFileNames, ArrayList<Object[]> databaseVectors, ArrayList<Object[]> results) {
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
    }

    public static ArrayList<Object[]> printResults(ArrayList<Object[]> results) {
        // If results is empty say so
        if (results.isEmpty()) {
            System.out.println("No results found.");
            return null;
        }

        // Create empty ArrayList for results
        ArrayList<Object[]> resultsList = new ArrayList<>();

        // Print results
        for (Object[] result : results) {
            String fileName = (String) result[0];
            // Extract useful filename by first splitting String into /, selecting last part, deleting the snippetList_ from the start
            String[] fileNameParts = fileName.split("/");
            String fileNameNew = fileNameParts[fileNameParts.length - 1];
            // Now split file with _ and select the 2nd and 3rd part
            String[] fileNameParts2 = fileNameNew.split("_");
            String fileNameNew2 = fileNameParts2[1] + "_" + fileNameParts2[2];
            // // Print input filename
            // System.out.println("\nInput file: " + fileNameNew2 + "_" + fileNameParts2[3]);
            // // Print results where each result gets 15 chars of length, so print song and count
            // for (int i = 1; i < 6; i++) {
            //     if (i < ((ArrayList<Object[]>) result[1]).size()) {
            //         Object[] songResult = ((ArrayList<Object[]>) result[1]).get(i - 1);
            //         String songName = (String) songResult[0];
            //         int count = (int) songResult[1];
            //         // Print song name and count
            //         System.out.printf("%-22s Found vectors: %d", songName, count);
            //         // If the fileNameNew2 match the song with the songname substring as long as the fileNameNew2, print "success"
            //         if (songName.substring(0, fileNameNew2.length()).equals(fileNameNew2)) {
            //             System.out.println(" - Correct");
            //         } else {
            //             System.out.println();
            //         }
            //     }
            // }

            int songPos = 0;

            // For each result in the results
            for (int j = 1; j < ((ArrayList<Object[]>) result[1]).size(); j++) {
                Object[] songResult = ((ArrayList<Object[]>) result[1]).get(j);
                String songName = (String) songResult[0];
                // If it's the correct song, add to resultsList save position
                if (songName.substring(0, fileNameNew2.length()).equals(fileNameNew2)) {
                    songPos = j;
                    break;
                }
            }

            Object[] resultString = new Object[2];
            resultString[0] = fileNameNew2 + "_" + fileNameParts2[3];
            resultString[1] = songPos;
            resultsList.add(resultString);
        }

        return resultsList;
    }

    // fileNames, ii, settings, iii, inputType
    public static void calculateFFTAndCreateVectorList(List<Object[]> fileNames, int ii, ArrayList<Object[]> settings, int iii, String[] inputType) {

        // Get settings
        Object[] setting = settings.get(iii);
        int fourierQuality = (int) setting[0];
        int windowSize = (int) setting[1];
        int overlap = (int) setting[2];
        double minFreq = (double) setting[3];
        double maxFreq = (double) setting[4];
        int bins = (int) setting[5];
        int targetHeight = (int) setting[6];
        int targetLength = (int) setting[7];
        int lengthToTarget = (int) setting[8];

        // Set target zone
        int[] targetZone = {targetHeight, targetLength, lengthToTarget};

        // Get frequency range
        double[] freqRange = new double[2];
        freqRange[0] = minFreq;
        freqRange[1] = maxFreq;

        // Get file name and path
        Object[] fileInfo = (Object[]) fileNames.get(ii);
        String fileName = (String) fileInfo[0];
        String filePath = (String) fileInfo[1];
        String[] genres = ((String) fileInfo[2]).split(" ");
        
        // Print status
        System.out.println("Processing file: " + filePath + " Quality: " + fourierQuality + " WindowSize: " + windowSize);

        // Output folder for vectorlist as vectorLists
        String fourierOutputFolder = inputType[0] + "/"
                                + "FFT" + fourierQuality
                                + "_Win" + windowSize
                                + "_frq" + minFreq + "-" + maxFreq
                                + "_ovr" + overlap
                                + "/";

        // Output folder for vectorlist as vectorLists
        String vectorOutputFolder = fourierOutputFolder + inputType[1] + "/"
                                + "_Bin" + bins
                                + "_Zne" + targetHeight + "-" + targetLength + "-" + lengthToTarget
                                + "/";

        String newFilePath = createFourierTransform(fileInfo, fourierQuality, windowSize, overlap, freqRange, fourierOutputFolder);
        // // Create image of spectrum
        // TextSpectrumToImage.FileToImage(newFilePath);
        
        createVectorList(fileInfo, bins, targetZone, fourierOutputFolder, vectorOutputFolder, newFilePath);
    }

    public static void addToSettings(ArrayList<Object[]> settings, int[] fourierQualityList, int[] windowSizeList, int[] overlapList, double[] minFreqList, double[] maxFreqList, int[] binsList, int[] targetHeightList, int[] targetLengthList, int[] lengthToTargetList) {
        // For each combination add to settings list
        for (int i = 0; i < fourierQualityList.length; i++) {
            for (int j = 0; j < windowSizeList.length; j++) {
                for (int k = 0; k < overlapList.length; k++) {
                    for (int l = 0; l < minFreqList.length; l++) {
                        for (int m = 0; m < maxFreqList.length; m++) {
                            for (int n = 0; n < binsList.length; n++) {
                                for (int o = 0; o < targetHeightList.length; o++) {
                                    for (int p = 0; p < targetLengthList.length; p++) {
                                        for (int q = 0; q < lengthToTargetList.length; q++) {
                                            settings.add(new Object[]{
                                                fourierQualityList[i],
                                                windowSizeList[j],
                                                overlapList[k],
                                                minFreqList[l],
                                                maxFreqList[m],
                                                binsList[n],
                                                targetHeightList[o],
                                                targetLengthList[p],
                                                lengthToTargetList[q]
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
