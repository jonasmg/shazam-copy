import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {

        String[] fileNames = {
                            "piano-audio-test.wav",
                            "jonasMusic.wav",
                            // "sugarv2.wav",
                            // "smilev2.wav",
                            // "lover-loverv2.wav",
                            // "broken-heartv2.wav",
                            // "uptown-funkv2.wav",
                            "uptown-funkcut1.wav",
                            "uptown-funkcut2.wav",
                            "uptown-funkcut3.wav",
                        };

        // String filePath = "audio/" + fileName;

        String[] filePaths = new String[fileNames.length];

        // Append to each file name the audio/ path
        for (int i = 0; i < fileNames.length; i++) {
            filePaths[i] = "audio/" + fileNames[i];
        }

        // Split up fft into bins nd only keep the highest value of each of those bins
        int bins = 8;

        int fourierQuality = 4;
        int[] fourierQualityList = {28};
        int pixelHeight = 128*3;
        int pixelWidth = 1024*2;
        int secondLength = 8;

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
    
                processFile(filePaths[ii], fileNames[ii], bins, fourierQualityList[i], pixelHeight, pixelWidth, secondLength);
            }
        }


        // int fourierQuality = 4;
        // int pixelHeight = 1024;
        // int pixelWidth = 1024*2;

        // audioSample a = new audioSample();
        // a.setFile(filePath);
        // int numSamples = a.getMaxSamples();
        // double audioLength = a.getLength();
        // System.out.printf("Audio length: %.2f seconds\n", audioLength);
        // a.setNumSamples(numSamples);
        // a.setStepSize(1);
        // a.computeSamples();
        // int[] samples = a.getSamples();

        // // userInterface s = new userInterface(samples, filePath);
        // // s.setVisible(true);

        // // Print update
        // System.out.println("Number of samples: " + numSamples);
        // System.out.println("SampleRate: " + a.getSampleRate());

        // // Get resolution from calculateFourierTransform
        // CalculateFourierTransform c = new CalculateFourierTransform();
        // c.setSampleRate(a.getSampleRate());
        // c.setSampleSize(numSamples);
        // c.setSamples(samples);

        // // Print status
        // System.out.println("Calculating FFT with quality " + fourierQuality + "...");

        // // Quality
        // c.setFourierQuality(fourierQuality);
        // c.setPixelHeight(pixelHeight);
        // c.setPixelWidth(pixelWidth);

        // // int[][] dft = c.calculateDiscreteFourierTransform();
        // // // Print status
        // // System.out.println("DFT calculated!");

        // int[][] fft = c.calculateFastFourierTransform();
        // // Print status
        // System.out.println("FFT calculated!");

        // double maxMagnitude = c.getMaxMagnitude();
        // // Print maxMagnitude
        // System.out.println("Max magnitude: " + maxMagnitude);

        // //print max and min value in fft
        // int max = 0;
        // int min = 0;
        // for (int i = 0; i < pixelWidth; i++) {
        //     for (int j = 0; j < pixelHeight; j++) {
        //         max = Math.max(max, fft[j][i]);
        //         min = Math.min(min, fft[j][i]);
        //     }
        // }
        // System.out.println("Max value in fft: " + max);
        // System.out.println("Min value in fft: " + min);

        // // Split up fft into 10 bins and only keep the highest value of each of those bins
        // int binSize = pixelHeight / 10;
        // // for each bin
        // for (int i = 0; i < pixelWidth; i++) {
        //     for (int j = 0; j < pixelHeight; j += binSize) {
        //     // For a bin save max and turn all pixels to 0
        //     int maxBin = 0;
        //     int maxBinIndex = j;
        //     for (int k = 0; k < binSize; k++) {
        //         if (j + k < pixelHeight) {
        //         if (fft[j + k][i] > maxBin) {
        //             maxBin = fft[j + k][i];
        //             maxBinIndex = j + k;
        //         }
        //         fft[j + k][i] = 0;
        //         }
        //     }
        //     // Set max value to bin
        //     fft[maxBinIndex][i] = maxBin;
        //     }
        // }

        // // Normalize to 255
        // for (int y = 0; y < pixelHeight; y++) {
        //     for (int x = 0; x < pixelWidth; x++) {
        //         fft[y][x] = (int) (fft[y][x] / maxMagnitude * 255);
        //         fft[y][x] = Math.min(255, fft[y][x]);
        //     }
        // }

        // // // find highest value in fft
        // // int max = 0;
        // // for (int i = 0; i < pixelWidth; i++) {
        // //     for (int j = 0; j < pixelHeight; j++) {
        // //         max = Math.max(max, fft[j][i]);
        // //     }
        // // }

        // // // Normalize fft from max being 255
        // // for (int i = 0; i < pixelWidth; i++) {
        // //     for (int j = 0; j < pixelHeight; j++) {
        // //         fft[j][i] = (int) (255.0 * fft[j][i] / max);
        // //     }
        // // }

        // // // enhance contrast of image by applying power function
        // // double gamma = 0.5;
        // // for (int i = 0; i < pixelWidth; i++) {
        // //     for (int j = 0; j < pixelHeight; j++) {
        // //         fft[j][i] = (int) (255.0 * Math.pow(fft[j][i] / 255.0, gamma));
        // //     }
        // // }

        // // Create buffered image
        // BufferedImage bufferedImage = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);
        // for (int i = 0; i < pixelWidth; i++) {
        //     for (int j = 0; j < pixelHeight; j++) {
        //         int value = fft[j][i];
        //         value = Math.min(value, 255);
        //         int rgb = new Color(value, value, value).getRGB();
        //         bufferedImage.setRGB(i, j, rgb);
        //     }
        // }

        // // Print status
        // System.out.println("Creating image...");

        // // File name, original + fourierQuality
        // String imageName = "output/" + fileName.substring(0, fileName.length() - 4) + "-Q" + fourierQuality + "fft10B.png";

        // File file2 = new File(imageName);
        // try {
        //     ImageIO.write(bufferedImage, "png", file2);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // // Print status
        // System.out.println("Image created!");


        // Remember: to open xming
        // Remember: "export DISPLAY=:0"

        // sudo apt update
        // sudo apt install -y fonts-dejavu-core fonts-liberation

        // sudo apt-get install -y x11-xserver-utils

        // sudo apt-get install -y fonts-dejavu fonts-liberation

        // ssh -X user@remote-server

        // sudo apt update
        // sudo apt install fontconfig
        // sudo apt install fonts-dejavu



    }

    // helper functiong process File
    public static void processFile(String filePath, String fileName, int bins, int fourierQuality, int pixelHeight, int pixelWidth, int secondLength) {
        audioSample a = new audioSample();
        a.setFile(filePath);
        int numSamples = a.getMaxSamples();
        double audioLength = a.getLength();
        System.out.printf("Audio length: %.2f seconds\n", audioLength);
        a.setNumSamples(numSamples);
        a.setStepSize(1);
        a.computeSamples();
        int[] samples = a.getSamples();

        // userInterface s = new userInterface(samples, filePath);
        // s.setVisible(true);

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

        // // Creating text file
        // String textFileName = "output/" + fileName.substring(0, fileName.length() - 4) + "-Q" + fourierQuality + "fft" + bins + "B-F" + frameLength + "S" + shift + ".txt";
        // File file = new File(textFileName);
        // try {
        //     file.createNewFile();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // // Write to text file
        // try {
        //     FileWriter writer = new FileWriter(file);
        //     for (int i = 0; i < pixelWidth; i++) {
        //         for (int j = 0; j < pixelHeight; j++) {
        //             writer.write(fft[j][i] + " ");
        //         }
        //         writer.write("\n");
        //     }
        //     writer.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // Create new image for after being processed:

        int[][] fftCopy = new int[pixelHeight][pixelWidth];
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                fftCopy[j][i] = fft[j][i];
            }
        }

        int columns = secondLength*2;

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
            int percentileIndex1 = Math.max(0, Math.min(values.length - 1, (int) (0.996 * values.length)));
            int percentileIndex2 = Math.max(0, Math.min(values.length - 1, (int) (0.992 * values.length)));
            int percentileIndex3 = Math.max(0, Math.min(values.length - 1, (int) (0.98 * values.length)));

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

        // if (bins != 0) {
        //     int binSize = pixelHeight / bins;
        //     // for each bin
        //     for (int i = 0; i < pixelWidth; i++) {
        //         for (int j = 0; j < pixelHeight; j += binSize) {
        //         // For a bin save max and turn all pixels to 0
        //         int maxBin = 0;
        //         int maxBinIndex = j;
        //         for (int k = 0; k < binSize; k++) {
        //             if (j + k < pixelHeight) {
        //             if (fft[j + k][i] > maxBin) {
        //                 maxBin = fft[j + k][i];
        //                 maxBinIndex = j + k;
        //             }
        //             fft[j + k][i] = 0;
        //             }
        //         }
        //         // Set max value to bin
        //         fft[maxBinIndex][i] = maxBin;
        //         }
        //     }
        // }

        // Logorithmic bins
        if (bins != 0) {
            // Define the logarithmic scale factor
            double logBase = 1.5; // Adjust this base for different growth rates
            int previousBinEnd = 0; // Track where the last bin ended
        
            // Iterate over columns
            for (int i = 0; i < pixelWidth; i++) {
                int binIndex = 0; // Track bin number
                int j = 0; // Start at the top of the column
        
                while (j < pixelHeight) {
                    // Compute bin size logarithmically
                    int binSize = (int) Math.ceil(pixelHeight / (bins * Math.pow(logBase, binIndex)));
                    if (binSize < 1) binSize = 1; // Ensure minimum bin size of 1
                    int maxBin = 0;
                    int maxBinIndex = j;
        
                    // Find max value within this bin
                    for (int k = 0; k < binSize; k++) {
                        if (j + k < pixelHeight) {
                            if (fft[j + k][i] > maxBin) {
                                maxBin = fft[j + k][i];
                                maxBinIndex = j + k;
                            }
                        }
                    }
        
                    // Set all pixels in the bin to 0 except the max and its immediate neighbors
                    for (int k = 0; k < binSize; k++) {
                        int currentIndex = j + k;
                        if (currentIndex < pixelHeight) {
                            if (currentIndex == maxBinIndex || 
                                currentIndex == maxBinIndex - 1 || 
                                currentIndex == maxBinIndex + 1) {
                                // Keep max pixel and its direct neighbors
                                continue;
                            }
                            fft[currentIndex][i] = 0; // Zero out other pixels
                        }
                    }
        
                    // Move to the next bin
                    j += binSize;
                    binIndex++;
                }
            }
        }
        
        // Clean up image to only get larger blobs
        // Create copy of fft
        int clusterSizeSmallest = 4;
        int clusterSizeLargest = 8;
        int[][] fftCopy2 = new int[pixelHeight][pixelWidth];
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                fftCopy2[j][i] = fft[j][i];
            }
        }

        // Iterate over fft
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                // If the pixel is not 0
                if (fft[j][i] != 0) {
                    // Check if the pixel is part of a cluster
                    int clusterSize = 0;
                    for (int k = -clusterSizeSmallest; k <= clusterSizeSmallest; k++) {
                        for (int l = -clusterSizeSmallest; l <= clusterSizeSmallest; l++) {
                            if (i + k >= 0 && i + k < pixelWidth && j + l >= 0 && j + l < pixelHeight) {
                                if (fftCopy2[j + l][i + k] != 0) {
                                    clusterSize++;
                                }
                            }
                        }
                    }

                    // If the cluster is too small, set the pixel to 0
                    if (clusterSize < clusterSizeLargest) {
                        fft[j][i] = 0;
                    }
                }
            }
        }

        // Only keep values above 200 in fft
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                if (fft[j][i] < 200) {
                    fft[j][i] = 0;
                } else {
                    fft[j][i] = 1;
                }
            }
        }

        // Make copy of fft
        int[][] fftCopy3 = new int[pixelHeight][pixelWidth];
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                fftCopy3[j][i] = fft[j][i];
            }
        }

        // Delete points with no more than 1 neighbor in a 5x5 square
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                if (fftCopy3[j][i] != 0) {
                    int neighborCount = 0;
                    for (int k = -2; k <= 2; k++) {
                        for (int l = -2; l <= 2; l++) {
                            if (k == 0 && l == 0) continue; // Skip the point itself
                            if (i + k >= 0 && i + k < pixelWidth && j + l >= 0 && j + l < pixelHeight) {
                                if (fftCopy3[j + l][i + k] != 0) {
                                    neighborCount++;
                                }
                            }
                        }
                    }
                    if (neighborCount <= 1) {
                        fft[j][i] = 0;
                    }
                }
            }
        }

        // Extract nonzero data points into a list
        ArrayList<int[]> dataPoints = new ArrayList<>();
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                if (fft[j][i] == 1) { // Only consider nonzero points
                    dataPoints.add(new int[]{i, j}); // Store (x, y) coordinates
                }
            }
        }

        // Clustering for every secondLength * 2 columns
        int columns2 = secondLength * 2;
        for (int i = 0; i < pixelWidth; i += columns2/2) {
            // Extract data points for this column segment
            ArrayList<int[]> dataPointsColumn = new ArrayList<>();
            for (int j = 0; j < pixelHeight; j++) {
                for (int offset = 0; offset < columns2 && i + offset < pixelWidth; offset++) {
                    if (fft[j][i + offset] == 1) {
                        dataPointsColumn.add(new int[]{i + offset, j});
                    }
                }
            }

            // If no points exist, skip this segment
            if (dataPointsColumn.isEmpty()) continue;

            // Determine k dynamically based on number of points
            int k = Math.max(1, dataPointsColumn.size() / 50); // Adjust divisor based on data density

            // Run K-Means clustering
            ArrayList<int[]> clusterCentersColumn = kMeans(dataPointsColumn, k);

            // Draw points from cluster centers as 3x3 squares
            for (int[] center : clusterCentersColumn) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int x = center[0] + dx;
                        int y = center[1] + dy;
                        if (x >= 0 && x < pixelWidth && y >= 0 && y < pixelHeight) {
                            fft[y][x] = 255;
                        }
                    }
                }
            }
        }


        // // Number of clusters (adjust based on data size)
        // int k = Math.max(5, dataPoints.size() / 10); // Dynamic cluster count

        // // Run K-Means clustering
        // ArrayList<int[]> clusterCenters = kMeans(dataPoints, k);

        // // Print results
        // System.out.println("Cluster Centers:");
        // for (int[] center : clusterCenters) {
        //     System.out.println("x: " + center[0] + ", y: " + center[1]);
        // }

        // Create list of cluster centers called clusterCenters
        ArrayList<int[]> clusterCenters = new ArrayList<>();
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                if (fft[j][i] > 0) {
                    clusterCenters.add(new int[]{i, j});
                }
            }
        }

        // // Draw it to image in a new file
        // int[][] fftCluster = new int[pixelHeight][pixelWidth];

        // Draw points from cluster centers in 1/4 heigh quality and 1/2 width quality
        int[][] fftClusterLQ = new int[pixelHeight/4][pixelWidth/2];

        for (int[] center : clusterCenters) {
            int x = center[0] / 2;
            int y = center[1] / 4;
            if (x >= 0 && x < pixelWidth/2 && y >= 0 && y < pixelHeight/4) {
                fftClusterLQ[y][x] = 255;
            }
        }
        
        // // Draw points from cluster centers as 3x3 squares
        // for (int[] center : clusterCenters) {
        //     for (int dx = -1; dx <= 1; dx++) {
        //     for (int dy = -1; dy <= 1; dy++) {
        //         int x = center[0] + dx;
        //         int y = center[1] + dy;
        //         if (x >= 0 && x < pixelWidth && y >= 0 && y < pixelHeight) {
        //         fftCluster[y][x] = 255;
        //         }
        //     }
        //     }
        // }

        // Linear bins
        // if (bins != 0) {
        //     int binSize = pixelHeight / bins;
        
        //     // Iterate over columns
        //     for (int i = 0; i < pixelWidth; i++) {
        //         for (int j = 0; j < pixelHeight; j += binSize) {
        //             // Find the max value within the bin
        //             int maxBin = 0;
        //             int maxBinIndex = j;
        
        //             for (int k = 0; k < binSize; k++) {
        //                 if (j + k < pixelHeight) {
        //                     if (fft[j + k][i] > maxBin) {
        //                         maxBin = fft[j + k][i];
        //                         maxBinIndex = j + k;
        //                     }
        //                 }
        //             }
        
        //             // Set all pixels in the bin to 0 except the max and its immediate neighbors
        //             for (int k = 0; k < binSize; k++) {
        //                 int currentIndex = j + k;
        //                 if (currentIndex < pixelHeight) {
        //                     if (currentIndex == maxBinIndex || 
        //                         currentIndex == maxBinIndex - 1 || 
        //                         currentIndex == maxBinIndex + 1) {
        //                         // Keep max pixel and its direct neighbors
        //                         continue;
        //                     }
        //                     fft[currentIndex][i] = 0; // Zero out all other pixels in the bin
        //                 }
        //             }
        //         }
        //     }
        // }
        



        // // get perfentile of the fft and set it as the max value and turn the rest down to 0,25 percent of the max value
        // int[] values = new int[pixelHeight * pixelWidth];
        // int index = 0;
        // for (int i = 0; i < pixelWidth; i++) {
        //     for (int j = 0; j < pixelHeight; j++) {
        //         values[index] = fft[j][i];
        //         index++;
        //     }
        // }

        // // Sort the values
        // java.util.Arrays.sort(values);

        // // Get the 98th percentile and the 50th percentile
        // // int percentile1 = values[(int) (0.98 * values.length)];
        // // int percentile2 = values[(int) (0.9 * values.length)];
        // // int percentile3 = values[(int) (0.75 * values.length)];
        
        // int percentile1 = values[(int) (0.99 * values.length)];
        // int percentile2 = values[(int) (0.98 * values.length)];
        // int percentile3 = values[(int) (0.97 * values.length)];


        // // Set the values over max2 to 255 and the rest to times 0.25 of their value
        // for (int i = 0; i < pixelWidth; i++) {
        //     for (int j = 0; j < pixelHeight; j++) {
        //         if (fft[j][i] > percentile1) {
        //             fft[j][i] = (int) (fft[j][i] / (double) percentile1 * 255);
        //         } else if (fft[j][i] > percentile2 && fft[j][i] <= percentile1) {
        //             fft[j][i] = (int) (fft[j][i] / (double) percentile1 * 255 * 0.98);
        //         } else if (fft[j][i] > percentile3 && fft[j][i] <= percentile2) {
        //             fft[j][i] = (int) (fft[j][i] / (double) percentile2 * 255 * 0.2);
        //         } else {
        //             fft[j][i] = 0;
        //         }
        //     }
        // }


        // // Normalize to 255
        // for (int y = 0; y < pixelHeight; y++) {
        //     for (int x = 0; x < pixelWidth; x++) {
        //         fft[y][x] = (int) (fft[y][x] / maxMagnitude * 255);
        //         fft[y][x] = Math.min(255, fft[y][x]);
        //     }
        // }

        // Set all values to 0 or 255 in fft
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                if (fft[j][i] > 0) {
                    fft[j][i] = 255;
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

        //create another image for the cluster centers
        BufferedImage bufferedImageCluster = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                int value = fftCluster[j][i];
                value = Math.min(value, 255);
                int rgb = new Color(value, value, value).getRGB();
                bufferedImageCluster.setRGB(i, j, rgb);
            }
        }


        // File name, original + fourierQuality
        String imageName = "output/" + fileName.substring(0, fileName.length() - 4) + "-Q" + fourierQuality + "fft" + bins + "B-F" + frameLength + "S" + shift + "col-" + columns + "Clean.png";

        String clusterImageName = "output/" + fileName.substring(0, fileName.length() - 4) + "-Q" + fourierQuality + "fft" + bins + "B-F" + frameLength + "S" + shift + "col-" + columns + "Cluster.png";

        File file2 = new File(imageName);
        try {
            ImageIO.write(bufferedImage, "png", file2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file3 = new File(clusterImageName);
        try {
            ImageIO.write(bufferedImageCluster, "png", file3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print status
        System.out.println("Image and ClusterImage created!");
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
}
