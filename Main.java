import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {

        String[] fileNames = {
                            // "piano-audio-test.wav",
                            // "jonasMusic.wav",
                            // "sugarv2.wav",
                            // "smilev2.wav",
                            // "lover-loverv2.wav",
                            // "broken-heartv2.wav"
                            "uptown-funkv2.wav"
                            // "uptown-funkcut1.wav",
                            // "uptown-funkcut2.wav",
                            // "uptown-funkcut3.wav"
                        };

        // String filePath = "audio/" + fileName;

        String[] filePaths = new String[fileNames.length];

        // Append to each file name the audio/ path
        for (int i = 0; i < fileNames.length; i++) {
            filePaths[i] = "audio/" + fileNames[i];
        }

        // Split up fft into bins nd only keep the highest value of each of those bins
        int bins = 0;

        int fourierQuality = 4;
        int pixelHeight = 128*3;
        int pixelWidth = 1024*2;

        // for each file name
        for (int i = 0; i < fileNames.length; i++) {
            // Print status
            System.out.println("Processing file: " + fileNames[i]);
            // Get file length in seconds and set the pixelWidth to 20 times the length
            audioSample a = new audioSample();
            a.setFile(filePaths[i]);
            double audioLength = a.getLength();
            pixelWidth = (int) (audioLength * 8);

            processFile(filePaths[i], fileNames[i], bins, fourierQuality, pixelHeight, pixelWidth);
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
    public static void processFile(String filePath, String fileName, int bins, int fourierQuality, int pixelHeight, int pixelWidth) {
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

        if (bins != 0) {
            int binSize = pixelHeight / bins;
            // for each bin
            for (int i = 0; i < pixelWidth; i++) {
                for (int j = 0; j < pixelHeight; j += binSize) {
                // For a bin save max and turn all pixels to 0
                int maxBin = 0;
                int maxBinIndex = j;
                for (int k = 0; k < binSize; k++) {
                    if (j + k < pixelHeight) {
                    if (fft[j + k][i] > maxBin) {
                        maxBin = fft[j + k][i];
                        maxBinIndex = j + k;
                    }
                    fft[j + k][i] = 0;
                    }
                }
                // Set max value to bin
                fft[maxBinIndex][i] = maxBin;
                }
            }
        }

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

        // get perfentile of the fft and set it as the max value and turn the rest down to 0,25 percent of the max value
        int[] values = new int[pixelHeight * pixelWidth];
        int index = 0;
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                values[index] = fft[j][i];
                index++;
            }
        }

        // Sort the values
        java.util.Arrays.sort(values);

        // Get the 98th percentile and the 50th percentile
        int percentile1 = values[(int) (0.98 * values.length)];
        int percentile2 = values[(int) (0.9 * values.length)];
        int percentile3 = values[(int) (0.75 * values.length)];


        // Set the values over max2 to 255 and the rest to times 0.25 of their value
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                if (fft[j][i] > percentile1) {
                    fft[j][i] = (int) (fft[j][i] / (double) percentile1 * 255);
                } else if (fft[j][i] > percentile2 && fft[j][i] <= percentile1) {
                    fft[j][i] = (int) (fft[j][i] / (double) percentile1 * 255 * 0.99);
                } else if (fft[j][i] > percentile3 && fft[j][i] <= percentile2) {
                    fft[j][i] = (int) (fft[j][i] / (double) percentile2 * 255 * 0.2);
                } else {
                    fft[j][i] = 0;
                }
            }
        }


        // // Normalize to 255
        // for (int y = 0; y < pixelHeight; y++) {
        //     for (int x = 0; x < pixelWidth; x++) {
        //         fft[y][x] = (int) (fft[y][x] / maxMagnitude * 255);
        //         fft[y][x] = Math.min(255, fft[y][x]);
        //     }
        // }

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


        // File name, original + fourierQuality
        String imageName = "output/" + fileName.substring(0, fileName.length() - 4) + "-Q" + fourierQuality + "fft" + bins + "B-F" + frameLength + "S" + shift + ".PNG";

        File file2 = new File(imageName);
        try {
            ImageIO.write(bufferedImage, "png", file2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print status
        System.out.println("Image created!");
    }
}
