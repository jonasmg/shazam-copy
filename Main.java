import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class Main {
    public static final String[] SONG = {"FourierLists", "VectorLists"};
    public static final String[] SNIPPET = {"FourierListsInput", "vectorListsInput"};
    public static final String BLACKLIST = "INFO.txt";
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // First is fileName, second is path, third is genreTouple
        List<Object[]> fileNames = new ArrayList<>();
        List<Object[]> snippetNames = new ArrayList<>();

        // Get files of audio and snippets
        fileNames = FileProcessor.getFiles2("songList");
        snippetNames = FileProcessor.getSnippetFileNames("snippetListGenerated20");

        // Print filenames
        printFileNames(fileNames);
        printFileNames(snippetNames);

        int[] fourierQalityList = {700};
        int[] windowSizeList = {350};
        int[] overlapList = {0};
        double[] minFreqList = {200};
        double[] maxFreqList = {2000.0};

        int[] binsList = {4};
        int[] targetHeightList = {120};
        int[] targetLengthList = {40};
        int[] lengthToTargetList = {4};

        // Create arraylist object[] for all settings
        ArrayList<Object[]> settings = new ArrayList<>();

        // Function to add lists to settings
        addToSettings(settings, fourierQalityList, windowSizeList, overlapList,
                            minFreqList, maxFreqList, binsList, targetHeightList,
                            targetLengthList, lengthToTargetList);

        // Create folders if they dont exist for song and snippet
        createFolder(SONG[0]);
        createFolder(SNIPPET[0]);

        // Create song database file for each file in songList
        for (int iii = 0; iii < settings.size(); iii++) {
            // Create text file in folder if it doesn't exist
            createInfoTextFile(settings, iii, SONG);
            createInfoTextFile(settings, iii, SNIPPET);

            // Give file to calculations, they should add to them
            for (int ii = 0; ii < fileNames.size(); ii++) {
                calculateFFTAndCreateVectorList(fileNames, ii, settings, iii, SONG);
            }
            for (int ii = 0; ii < snippetNames.size(); ii++) {
                calculateFFTAndCreateVectorList(snippetNames, ii, settings, iii, SNIPPET);
            }
        }

        // Results list for summary results
        ArrayList<Object[]> finalSummaryResults = new ArrayList<>();

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
                // For every text file in the folder, add to database
                File[] files = vectorList.listFiles();
                // Check if files is not null
                if (files == null) {
                    System.out.println("No files found in " + folderName + " With: " + vectorList);
                    continue;
                }

                // Create arraylist for database vectors
                ArrayList<Object[]> databaseVectors = new ArrayList<>();

                // Create arraylist for summary results
                ArrayList<Object[]> summaryResults = new ArrayList<>();

                // Print summary results
                System.out.println("Creating vector list for: " + folder + folderName + "...");

                // For each file in the folder, write to the database
                for (File file : files) {
                    // Get the name of the file
                    String fileName = file.getName();
                            
                    // If it's a text file open and write all lines to the database with the name of the file added to the start
                    if (fileName.endsWith(".txt") && !fileName.equals(BLACKLIST)) {
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
                    if (snippetVectorFile.getName().endsWith(".txt") && !snippetVectorFile.getName().equals(BLACKLIST)) {
                        snippetVectorFilesList.add(snippetVectorFile);
                    }
                }

                // Make results list
                ArrayList<Object[]> results = new ArrayList<>();

                // Time for all vector files
                long timeAll = 0;

                // Print summary results
                System.out.println("Calculating results for" + folder + folderName + "...");

                // For each file in the folder compare the vectors to the database with compareVectorsv2
                for (File snippetVectorFile : snippetVectorFilesList) {
                    // Get the file path
                    String snippetFilePath = snippetVectorFile.getAbsolutePath();

                    // Time start normal long int
                    long startTime = System.currentTimeMillis();
                    Object comparisonResult = compareVectorsSmart(snippetFilePath, databaseVectors);
                    long endTime = System.currentTimeMillis();
                    timeAll += (endTime - startTime);

                    // Pull out the filename and then count and then add to results with input filename
                    ArrayList<Object[]> comparisonResults = (ArrayList<Object[]>) comparisonResult;
                    results.add(new Object[]{snippetFilePath, comparisonResults});
                }

                // Divide timeall by 1000 to get seconds and by the length of the snippetvector files list
                double timeAvg = (double) timeAll / snippetVectorFilesList.size();

                // Print results
                summaryResults = printResults(results);

                ArrayList<Object[]> genreStats = new ArrayList<>();

                for (Object[] result : summaryResults) {
                    int songPos = (int) result[1];
                    String[] genres = (String[]) result[2];

                    for (String genre : genres) {
                        boolean found = false;

                        // Check if genre already exists
                        for (Object[] stat : genreStats) {
                            if (stat[0].equals(genre)) {
                                if (songPos == 0) {
                                    stat[1] = (int) stat[1] + 1; // success
                                } else {
                                    stat[2] = (int) stat[2] + 1; // fail
                                }
                                found = true;
                                break;
                            }
                        }

                        // If genre not found, add a new entry with success/fail counts
                        if (!found) {
                            int success = 0;
                            int fail = 0;

                            if (songPos == 0) {
                                success = 1;
                            } else {
                                fail = 1;
                            }
                            genreStats.add(new Object[] { genre, success, fail });
                        }

                    }
                }

                // Sort genreStats by most successes
                genreStats.sort((a, b) -> (int) b[1] - (int) a[1]);

                // Count successes and failures
                int successes = 0;
                int kinda = 0;
                int failures = 0;
                for (Object[] summaryResult : summaryResults) {
                    if ((int) summaryResult[1] == 0) {
                    successes++;
                } else if ((int) summaryResult[1] >= 1 && (int) summaryResult[1] <= 6) {
                    kinda++;
                } else {
                    failures++;
                }
            }

                // Calculate success rate
                double successRate = (double) successes / (successes + kinda + failures) * 100;
                
                // Get fouriertransform folder
                String[] filePath = new String[3];
                filePath[0] = SONG[0] + "/" + folder + "/";
                filePath[1] = filePath[0] + SONG[1] + "/" + folderName + "/";
                filePath[2] = folder + folderName;

                // Add to final summary results
                finalSummaryResults.add(new Object[]{filePath, successRate, successes, kinda, failures, timeAvg, genreStats});
            }
        }

        // Sort finalSummaryResults by success rate
        finalSummaryResults.sort((a, b) -> Double.compare((double) b[1], (double) a[1]));

        // Print final summary results
        System.out.println("\nFinal summary results:");
        for (Object[] result : finalSummaryResults) {
            String[] filePath = (String[]) result[0];
            double successRate = (double) result[1];
            int successes = (int) result[2];
            int kinda = (int) result[3];
            int failures = (int) result[4];
            double timeAvg = (double) result[5];
            ArrayList<Object[]> genreStats = (ArrayList<Object[]>) result[6];

            if (successRate > -1) {
                System.out.println("File: " + filePath[2] + " Success rate: " + successRate + "%");
                System.out.println("Successes: " + successes);
                System.out.println("Kinda: " + kinda);
                System.out.println("Failures: " + failures);
                System.out.println("Time avg: " + timeAvg + "ms");
                readInfoTextFileFromLocation(filePath, SONG);
                
                for (Object[] stat : genreStats) {
                    String genre = (String) stat[0];
                    int success = (int) stat[1];
                    int fail = (int) stat[2];
                    System.out.println("Genre: " + genre + " | Success: " + success + " | Fail: " + fail);
                }
            }
        }
    }

    public static String createFourierTransform(Object[] fileInfo, int fourierQuality, int windowSize, int overlap, double[] freqRange, String outputFolder, AtomicInteger timeMs) {
        // Get file name and path
        String fileName = (String) fileInfo[0];
        String filePath = (String) fileInfo[1];
        // String[] genres = ((String) fileInfo[2]).split(" ");

        timeMs.set(0);

        // Get create name of file with album and track
        String[] pathParts = filePath.split("/");
        String albumName = pathParts[pathParts.length - 2];

        String fourierFileName = albumName + "_" + fileName;

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
        a.computeSamplesStereo();
        int[] samples = a.getSamples();
        int sampleRate = a.getSampleRate();

        // Print update
        System.out.println("Number of samples: " + numSamples);
        System.out.println("SampleRate: " + sampleRate);

        // Start time
        long startTime = System.currentTimeMillis();

        // Setup ShortTimeFourierTransform
        ShortTimeFourierTransform stft = new ShortTimeFourierTransform(sampleRate, fourierQuality, windowSize, overlap, freqRange[0], freqRange[1]);
        stft.setSignal(samples);
        int[][] stftResult = stft.computeSTFT();
        System.out.println("STFT calculated!");

        // End time
        long endTime = System.currentTimeMillis();
        timeMs.set((int)(endTime - startTime));

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

    public static String createVectorList(Object[] fileInfo, int bins, int[] targetZone, String fourierOutputFolder, String vectorOutputFolder, String newFilePath, AtomicInteger timeMs) {
        timeMs.set(0);

        // Create output folder if it doesn't exist
        File folder = new File(vectorOutputFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Get stft file info and name
        File stftFile = new File(newFilePath);
        String stftFileName = stftFile.getName();

        // Create file for vector list
        String vectorFileName = vectorOutputFolder + stftFileName + "_vectors.txt";
        File vectorFile = new File(vectorFileName);
        // If file already exists return
        if (vectorFile.exists()) {
            System.out.println("File already exists: " + vectorFileName);
            return null;
        }

        // Time start
        long startTime = System.currentTimeMillis();

        // Extract file information, columns are defined by spaces and rows are line breaks
        // So create scanner to read the file
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(newFilePath));
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + newFilePath);
            return null;
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
            return null;
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

        // Create the file if it doesn't exist
        try {
            vectorFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
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

        // Time end
        long endTime = System.currentTimeMillis();
        timeMs.set((int)(endTime - startTime));

        return vectorFile.getAbsolutePath();
    }

    public static Object compareVectorsCount(String filePath, ArrayList<Object[]> databaseVectors) {
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

        // Made by ChatGPT
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
            // End of ChatGPT

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

    public static Object compareVectorsLinear(String filePath, ArrayList<Object[]> databaseVectors) {
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

        // Create results2
        ArrayList<Object[]> results2 = new ArrayList<>();

        // for the top 10 results
        for (int i = 0; i < 10; i++) {
            // If there is no result, break
            if (i >= results.size()) {
                break;
            }
            // Gather all vectors from the song
            ArrayList<Object[]> songVectors = new ArrayList<>();
            for (Object[] dbVector : databaseVectorsFound) {
                if (dbVector[4].equals(results.get(i)[0])) {
                    songVectors.add(dbVector);
                }
            }

            // Sort songVectors by offset
            songVectors.sort((a, b) -> (int) a[3] - (int) b[3]);

            // Get smallest and largest offset of vectors
            int smallestOffset = (int) songVectors.get(0)[3];
            int largestOffset = (int) songVectors.get(songVectors.size() - 1)[3];

            // Get difference
            int offsetDifference = largestOffset - smallestOffset;

            // Divide by dividesize
            int dividesize = 4;
            int step = (int) offsetDifference / dividesize;
            
            // Map to count occurrences of each offset
            Map<Integer, Integer> offsetCounts = new HashMap<>();
            for (Object[] vector : songVectors) {
                int offset = (int) vector[3];
                offsetCounts.put(offset, offsetCounts.getOrDefault(offset, 0) + 1);
            }

            // Array of vectors (step buckets)
            ArrayList<int[]> vectors = new ArrayList<>();
            for (int ii = 0; ii < dividesize; ii++) {
                int start = smallestOffset + ii * step;
                int end = Math.min(start + step, largestOffset + 1);
                int count = 0;
                for (int offset = start; offset < end; offset++) {
                    count += offsetCounts.getOrDefault(offset, 0);
                }
                vectors.add(new int[]{start, count});
            }

            // Take 4 largest steps and plus and add to linearCount
            vectors.sort((a, b) -> b[1] - a[1]);
            int linearCount = 0;
            for (int j = 0; j < dividesize*2; j++) {
                // If vectors is not empty
                if (vectors.size() > j) {
                    linearCount += vectors.get(j)[1];
                }
            }

            // Add to results2
            results2.add(new Object[]{ results.get(i)[0], linearCount});
        }

        return results2;
    }

    public static Object compareVectorsSmart(String filePath, ArrayList<Object[]> databaseVectors) {
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

        // Create results2
        ArrayList<Object[]> results2 = new ArrayList<>();

        // for the top 8 results
        for (int i = 0; i < 8; i++) {
            // If there is no result, break
            if (i >= results.size()) {
                break;
            }
            // Gather all vectors from the song
            ArrayList<Object[]> songVectors = new ArrayList<>();
            for (Object[] dbVector : databaseVectorsFound) {
                if (dbVector[4].equals(results.get(i)[0])) {
                    songVectors.add(dbVector);
                }
            }

            // Sort songVectors by offset
            songVectors.sort((a, b) -> (int) a[3] - (int) b[3]);

            // Create arraylist for points made of offset combined from song and input
            ArrayList<int[]> points = new ArrayList<>();
            for (int j = 0; j < songVectors.size(); j++) {
                Object[] vectorObj = songVectors.get(j);
                int[] vector = new int[]{
                    (int) vectorObj[0],
                    (int) vectorObj[1],
                    (int) vectorObj[2],
                    (int) vectorObj[3]
                };
                // If it matches the 3 first values with an input vector, add point with the 2 different offsets
                for (int k = 0; k < inputVectors.size(); k++) {
                    int[] inputVector = (int[]) inputVectors.get(k);
                    if (vector[0] == inputVector[0] && vector[1] == inputVector[1] && vector[2] == inputVector[2]) {
                        points.add(new int[]{vector[3], inputVector[3]});
                    }
                }
            }

            // Sort points by offset of the song vector
            points.sort((a, b) -> (int) a[0] - (int) b[0]);

            // inputVectors sort
            inputVectors.sort((a, b) -> (int) a[3] - (int) b[3]);

            // Sort songVectors by offset
            songVectors.sort((a, b) -> (int) a[3] - (int) b[3]);

            // Get largest and smallest offset of input vector from inputVector
            int smallestOffset = (int) inputVectors.get(0)[3];
            int largestOffset = (int) inputVectors.get(inputVectors.size() - 1)[3];
            // Get largest and smallest offset of song vector from songVectors
            int smallestOffsetSong = (int) songVectors.get(0)[3];
            int largestOffsetSong = (int) songVectors.get(songVectors.size() - 1)[3];

            // Create 2D array
            int[][] offsetArray = new int[largestOffset + 1][largestOffsetSong + 1];

            // Set points to 1 (reverse y-axis)
            for (int[] point : points) {
                int y = offsetArray.length - 1 - point[1];
                int x = point[0];
                if (y >= 0 && y < offsetArray.length && x >= 0 && x < offsetArray[0].length) {
                    offsetArray[y][x] = 1;
                }
            }

            // Find diagonal (bottom-left to top-right) with most points in a width-3 band
            int diagonalSize = 2;
            int maxCount = 0;
            int maxIndex = 0; // This will be the starting x position

            for (int startCol = 0; startCol <= offsetArray[0].length - diagonalSize; startCol++) {
                int count = 0;
                for (int startRow = 0; startRow < offsetArray.length; startRow++) {
                    for (int ii = 0; ii < diagonalSize; ii++) {
                        int row = offsetArray.length - 1 - startRow;
                        int col = startCol + ii + startRow;
                        if (row >= 0 && col < offsetArray[0].length) {
                            count += offsetArray[row][col];
                        }
                    }
                }
                if (count > maxCount) {
                    maxCount = count;
                    maxIndex = startCol; // or track (startRow, startCol) if needed
                }
            }

            // // Print max count and index and name of song
            // System.out.println("Song: " + results.get(i)[0]);
            // System.out.println("Max count: " + maxCount);
            // System.out.println("Max index: " + maxIndex);

            // Create new array with the same size as offsetArray
            int[][] offsetArray2 = new int[largestOffset + 1][largestOffsetSong + 1];
            
            int startCol = maxIndex;
            // for (int startCol = maxIndex; startCol <= offsetArray[0].length; startCol++) {
                for (int startRow = 0; startRow < offsetArray.length; startRow++) {
                    for (int ii = 0; ii < diagonalSize; ii++) {
                        int row = offsetArray.length - 1 - startRow;
                        int col = startCol + ii + startRow;
                        if (row >= 0 && col < offsetArray[0].length) {
                            offsetArray2[row][col] = offsetArray[row][col];
                        }
                    }
                }
            // }

            // // Write to text file using TextSpectrumToImage
            // TextSpectrumToImage.ArrayToImageWhite(offsetArray, new File("output/" + results.get(i)[0] + "_offset.png"));
            // TextSpectrumToImage.ArrayToImageWhite(offsetArray2, new File("output/" + results.get(i)[0] + "_offsetproc.png"));

            // Add max count to results2
            results2.add(new Object[]{ results.get(i)[0], maxCount});
        }

        // Sort results2 from count
        results2.sort((a, b) -> (int) b[1] - (int) a[1]);

        return results2;
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
            String[] genres = new String[0];
            // With fileNameParts2[1], find the genres of the file
            String artistAndAlbum = fileNameParts2[1];
            String songList = "songList";
            // Initiate folder file songList
            File folder = new File(songList);
            // Get all folders in songList
            // If statement: if folder does not exist
            if (folder.exists()) {
                File[] folders = folder.listFiles(File::isDirectory);
                // For each folder, check if it contains the artistAndAlbum
                for (File folderFile : folders) {
                    // Check for each folder in the folder if it contains the artistAndAlbum
                    File[] files = folderFile.listFiles();
                    for (File file : files) {
                        if (file.getName().contains(artistAndAlbum)) {
                            genres = folderFile.getName().split("_");
                        }
                    }
                }
            }

            String fileNameNew2 = fileNameParts2[1] + "_" + fileNameParts2[2];
            // Print input filename
            System.out.println("\nInput file: " + fileNameNew2 + "_" + fileNameParts2[3]);
            // If genres is empty, print "No genres found"
            if (genres.length == 0 || (genres.length == 1 && genres[0].isEmpty())) {
                System.out.println("No genres found.");
            } else {
                System.out.print("Genres: ");
                for (String genre : genres) {
                    System.out.print(genre + " ");
                }
                System.out.println();
            }

            // Print results where each result gets 15 chars of length, so print song and count
            for (int i = 1; i < 7; i++) {
                if (i < ((ArrayList<Object[]>) result[1]).size()) {
                    Object[] songResult = ((ArrayList<Object[]>) result[1]).get(i - 1);
                    String songName = (String) songResult[0];
                    int count = (int) songResult[1];
                    // Print song name and count
                    System.out.printf("%-22s Found vectors: %d", songName, count);
                    // If the fileNameNew2 match the song with the songname substring as long as the fileNameNew2, print "success"
                    if (songName.startsWith(fileNameNew2) && songName.charAt(fileNameNew2.length()) == '.') {
                        System.out.println(" - Correct");
                    } else {
                        System.out.println();
                    }
                }
            }

            int songPos = -1;
            // For each result in the results
            for (int j = 0; j < ((ArrayList<Object[]>) result[1]).size(); j++) {
                Object[] songResult = ((ArrayList<Object[]>) result[1]).get(j);
                String songName = (String) songResult[0];
                // If it's the correct song, add to resultsList save position
                if (songName.startsWith(fileNameNew2) && songName.charAt(fileNameNew2.length()) == '.') {
                    songPos = j;
                    break;
                }
            }


            Object[] resultString = new Object[3];
            resultString[0] = fileNameNew2 + "_" + fileNameParts2[3];
            resultString[1] = songPos;
            resultString[2] = genres;

            resultsList.add(resultString);
        }

        return resultsList;
    }

    public static ArrayList<Object[]> calculateResults(ArrayList<Object[]> results) {
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

            int songPos = 0;

            // For each result in the results
            for (int j = 1; j < ((ArrayList<Object[]>) result[1]).size(); j++) {
                Object[] songResult = ((ArrayList<Object[]>) result[1]).get(j);
                String songName = (String) songResult[0];
                // If it's the correct song, add to resultsList save position
                if (songName.startsWith(fileNameNew2) && songName.charAt(fileNameNew2.length()) == '.') {
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
        // String fileName = (String) fileInfo[0];
        String filePath = (String) fileInfo[1];
        // String[] genres = ((String) fileInfo[2]).split(" ");
        
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

        // Ready time
        AtomicInteger timeMs = new AtomicInteger(0);
        String fftFilePath = createFourierTransform(fileInfo, fourierQuality, windowSize, overlap, freqRange, fourierOutputFolder, timeMs);
        if (timeMs.get() > 0) {
            // Get file size
            File file = new File(fftFilePath);
            long fileSizeFFT = file.length();
            // Get song time
            audioSample a = new audioSample();
            a.setFile(filePath);
            double audioLength = a.getLength();
            long songTimeFFT = (long) (audioLength * 1000);
            // Get number of songs
            int numberOfSongsFFT = 1;
            // add to addToInfoFile
            addInfoTextFile(settings, iii, inputType, (int) timeMs.get(), (int) songTimeFFT, (int) fileSizeFFT, numberOfSongsFFT, 0, 0, 0, 0);
        }

        // // Create image of spectrum
        // TextSpectrumToImage.FileToImage(fftFilePath);
        
        timeMs.set(0);
        String vectorFilePath = createVectorList(fileInfo, bins, targetZone, fourierOutputFolder, vectorOutputFolder, fftFilePath, timeMs);
        if (timeMs.get() > 0) {
            // Get file size
            File file = new File(vectorFilePath);
            long fileSizeVec = file.length();
            // Get song time
            audioSample a = new audioSample();
            a.setFile(filePath);
            double audioLength = a.getLength();
            long songTimeVec = (long) (audioLength * 1000);
            // Get number of songs
            int numberOfSongsVec = 1;
            // add to addToInfoFile
            addInfoTextFile(settings, iii, inputType, 0, 0, 0, 0, (int) timeMs.get(), (int) songTimeVec, (int) fileSizeVec, numberOfSongsVec);
        }
    }

    // Made by ChatGPT
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
    // End of ChatGPT

    public static void createInfoTextFile(ArrayList<Object[]> settings, int iii, String[] inputType) {
        int calcTimeFFT = 0;
        int songTimeFFT = 0;
        int fileSizeFFT = 0;
        int numberOfSongsFFT = 0;

        int calcTimeVec = 0;
        int songTimeVec = 0;
        int fileSizeVec = 0;
        int numberOfSongsVec = 0;

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

        // Create output folder if it doesn't exist
        File folder = new File(fourierOutputFolder);
        if (!folder.exists()) {
            folder.mkdirs();
            // Print create folder
            System.out.println("Created folder: " + fourierOutputFolder);
        }

        // Create INFO.txt file in fourierOutputFolder
        File infoFile = new File(fourierOutputFolder + "INFO.txt");
        // If file doesnt exist, create file
        if (!infoFile.exists()) {
            // Create file and initialise values as rows: name_int
            try {
                infoFile.createNewFile();
                // Print created file
                System.out.println("Created file: " + infoFile.getAbsolutePath());
                FileWriter writer = new FileWriter(infoFile);
                writer.write("calcTimeFFT_" + calcTimeFFT + "\n");
                writer.write("songTimeFFT_" + songTimeFFT + "\n");
                writer.write("fileSizeFFT_" + fileSizeFFT + "\n");
                writer.write("numberOfSongsFFT_" + numberOfSongsFFT + "\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Create output folder if it doesn't exist
        File folder2 = new File(vectorOutputFolder);
        if (!folder2.exists()) {
            folder2.mkdirs();
            // Print create folder
            System.out.println("Created folder: " + vectorOutputFolder);
        }

        // Create INFO.txt file in vectorOutputFolder
        File infoFile2 = new File(vectorOutputFolder + "INFO.txt");
        // If file doesnt exist, create file
        if (!infoFile2.exists()) {
            // Create file and initialise values as rows: name_int
            try {
                infoFile2.createNewFile();
                // Print created file
                System.out.println("Created file: " + infoFile2.getAbsolutePath());
                FileWriter writer = new FileWriter(infoFile2);
                writer.write("calcTimeVec_" + calcTimeVec + "\n");
                writer.write("songTimeVec_" + songTimeVec + "\n");
                writer.write("fileSizeVec_" + fileSizeVec + "\n");
                writer.write("numberOfSongsVec_" + numberOfSongsVec + "\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addInfoTextFile(ArrayList<Object[]> settings, int iii, String[] inputType, int calcTimeFFT, int songTimeFFT, int fileSizeFFT, int numberOfSongsFFT, int calcTimeVec, int songTimeVec, int fileSizeVec, int numberOfSongsVec) {
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

        int calcTimeFFTCur = 0;
        int songTimeFFTCur = 0;
        int fileSizeFFTCur = 0;
        int numberOfSongsFFTCur = 0;

        int calcTimeVecCur = 0;
        int songTimeVecCur = 0;
        int fileSizeVecCur = 0;
        int numberOfSongsVecCur = 0;

        // Try to read each value and save as current
        File infoFile = new File(fourierOutputFolder + "INFO.txt");
        try {
            Scanner scanner = new Scanner(infoFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("_");
                if (parts[0].equals("calcTimeFFT")) {
                    calcTimeFFTCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("songTimeFFT")) {
                    songTimeFFTCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("fileSizeFFT")) {
                    fileSizeFFTCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("numberOfSongsFFT")) {
                    numberOfSongsFFTCur = Integer.parseInt(parts[1]);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Calculate new values
        calcTimeFFTCur += calcTimeFFT;
        songTimeFFTCur += songTimeFFT;
        fileSizeFFTCur += fileSizeFFT;
        numberOfSongsFFTCur += numberOfSongsFFT;

        // Write new values to file
        try {
            FileWriter writer = new FileWriter(infoFile);
            writer.write("calcTimeFFT_" + calcTimeFFTCur + "\n");
            writer.write("songTimeFFT_" + songTimeFFTCur + "\n");
            writer.write("fileSizeFFT_" + fileSizeFFTCur + "\n");
            writer.write("numberOfSongsFFT_" + numberOfSongsFFTCur + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print status
        System.out.println("Added to INFO.txt file in " + fourierOutputFolder);

        // Try to read each value and save as current
        File infoFile2 = new File(vectorOutputFolder + "INFO.txt");
        try {
            Scanner scanner = new Scanner(infoFile2);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("_");
                if (parts[0].equals("calcTimeVec")) {
                    calcTimeVecCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("songTimeVec")) {
                    songTimeVecCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("fileSizeVec")) {
                    fileSizeVecCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("numberOfSongsVec")) {
                    numberOfSongsVecCur = Integer.parseInt(parts[1]);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Calculate new values
        calcTimeVecCur += calcTimeVec;
        songTimeVecCur += songTimeVec;
        fileSizeVecCur += fileSizeVec;
        numberOfSongsVecCur += numberOfSongsVec;
        // Write new values to file
        try {
            FileWriter writer = new FileWriter(infoFile2);
            writer.write("calcTimeVec_" + calcTimeVecCur + "\n");
            writer.write("songTimeVec_" + songTimeVecCur + "\n");
            writer.write("fileSizeVec_" + fileSizeVecCur + "\n");
            writer.write("numberOfSongsVec_" + numberOfSongsVecCur + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print status
        System.out.println("Added to INFO.txt file in " + vectorOutputFolder);
    }

    public static void readInfoTextFile(ArrayList<Object[]> settings, int iii, String[] inputType) {
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

        int calcTimeFFTCur = 0;
        int songTimeFFTCur = 0;
        int fileSizeFFTCur = 0;
        int numberOfSongsFFTCur = 0;
        int calcTimeVecCur = 0;
        int songTimeVecCur = 0;
        int fileSizeVecCur = 0;
        int numberOfSongsVecCur = 0;

        // Try to read each value and save as current
        File infoFile = new File(fourierOutputFolder + "INFO.txt");
        try {
            Scanner scanner = new Scanner(infoFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("_");
                if (parts[0].equals("calcTimeFFT")) {
                    calcTimeFFTCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("songTimeFFT")) {
                    songTimeFFTCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("fileSizeFFT")) {
                    fileSizeFFTCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("numberOfSongsFFT")) {
                    numberOfSongsFFTCur = Integer.parseInt(parts[1]);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Read from vectorOutputFolder
        File infoFile2 = new File(vectorOutputFolder + "INFO.txt");
        try {
            Scanner scanner = new Scanner(infoFile2);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("_");
                if (parts[0].equals("calcTimeVec")) {
                    calcTimeVecCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("songTimeVec")) {
                    songTimeVecCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("fileSizeVec")) {
                    fileSizeVecCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("numberOfSongsVec")) {
                    numberOfSongsVecCur = Integer.parseInt(parts[1]);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Made by ChatGPT
        // Print status and all values
        System.out.println("INFO.txt file in " + fourierOutputFolder);
        System.out.println("calcTimeFFT: " + calcTimeFFTCur);
        System.out.println("songTimeFFT: " + songTimeFFTCur);
        System.out.println("fileSizeFFT: " + fileSizeFFTCur);
        System.out.println("numberOfSongsFFT: " + numberOfSongsFFTCur);
        System.out.println("INFO.txt file in " + vectorOutputFolder);
        System.out.println("calcTimeVec: " + calcTimeVecCur);
        System.out.println("songTimeVec: " + songTimeVecCur);
        System.out.println("fileSizeVec: " + fileSizeVecCur);
        System.out.println("numberOfSongsVec: " + numberOfSongsVecCur);
        // Print status
        System.out.println("INFO.txt file read successfully.");
        // End of ChatGPT
    }

    public static void readInfoTextFileFromLocation(String[] filePath, String[] inputType) {
        int calcTimeFFTCur = 0;
        int songTimeFFTCur = 0;
        int fileSizeFFTCur = 0;
        int numberOfSongsFFTCur = 0;
        int calcTimeVecCur = 0;
        int songTimeVecCur = 0;
        int fileSizeVecCur = 0;
        int numberOfSongsVecCur = 0;

        // Try to read each value and save as current
        File infoFile = new File(filePath[0] + "INFO.txt");
        try {
            Scanner scanner = new Scanner(infoFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("_");
                if (parts[0].equals("calcTimeFFT")) {
                    calcTimeFFTCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("songTimeFFT")) {
                    songTimeFFTCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("fileSizeFFT")) {
                    fileSizeFFTCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("numberOfSongsFFT")) {
                    numberOfSongsFFTCur = Integer.parseInt(parts[1]);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Read from vectorOutputFolder
        File infoFile2 = new File(filePath[1] + "INFO.txt");
        try {
            Scanner scanner = new Scanner(infoFile2);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("_");
                if (parts[0].equals("calcTimeVec")) {
                    calcTimeVecCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("songTimeVec")) {
                    songTimeVecCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("fileSizeVec")) {
                    fileSizeVecCur = Integer.parseInt(parts[1]);
                } else if (parts[0].equals("numberOfSongsVec")) {
                    numberOfSongsVecCur = Integer.parseInt(parts[1]);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        // Made by ChatGPT
        // Convert song duration from ms to seconds
        double songTimeFFTSeconds = songTimeFFTCur / 1000.0;
        double songTimeVecSeconds = songTimeVecCur / 1000.0;

        // Calculate time in ms per second of audio
        double calcTimeFFTPerSecond = calcTimeFFTCur / songTimeFFTSeconds;
        double calcTimeVecPerSecond = calcTimeVecCur / songTimeVecSeconds;

        // Convert to seconds per minute of audio
        double calcTimeFFTPerMinute = calcTimeFFTPerSecond * 60.0 / 1000.0;
        double calcTimeVecPerMinute = calcTimeVecPerSecond * 60.0 / 1000.0;

        // Calculate file size in bytes per second, then KB per minute
        double fileSizeFFTPerSecond = fileSizeFFTCur / songTimeFFTSeconds;
        double fileSizeVecPerSecond = fileSizeVecCur / songTimeVecSeconds;
        double fileSizeFFTPerMinuteKB = fileSizeFFTPerSecond * 60.0 / 1024.0;
        double fileSizeVecPerMinuteKB = fileSizeVecPerSecond * 60.0 / 1024.0;

        // Print results
        System.out.printf("FFT calculation time per minute of audio: %.2f sec/min%n", calcTimeFFTPerMinute);
        System.out.printf("FFT file size per minute of audio: %.2f KB/min%n", fileSizeFFTPerMinuteKB);
        System.out.printf("Vector calculation time per minute of audio: %.2f sec/min%n", calcTimeVecPerMinute);
        System.out.printf("Vector file size per minute of audio: %.2f KB/min%n", fileSizeVecPerMinuteKB);

        // Print total length of audio and vector
        System.out.printf("Total length of audio: %.2f minutes%n", (songTimeFFTSeconds / 60));

        // Print total file size in MB
        System.out.printf("Total file size of FFT: %.2f MB%n", (fileSizeFFTCur / 1024.0 / 1024.0));
        System.out.printf("Total file size of vector: %.2f MB%n", (fileSizeVecCur / 1024.0 / 1024.0));
        // End of ChatGPT
    }

    public static void printFileNames(List<Object[]> fileNames) {
        // Print fileNames
        for (int i = 0; i < fileNames.size(); i++) {
            Object[] fileInfo = (Object[]) fileNames.get(i);
            String fileName = (String) fileInfo[0];
            String filePath = (String) fileInfo[1];
            String[] genres = ((String) fileInfo[2]).split(" ");
            System.out.println("File: " + fileName + " Path: " + filePath + " Genres: " + String.join(", ", genres));
        }
    }

    public static void createFolder(String folderPath) {
        // Create folder if it doesn't exist
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println("Created folder: " + folderPath);
        }
    }
}
