import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Color;

public class TextSpectrumToImage {
    public static void FileToImage(String filePath) {
        // Text Location
        // String text = "FourierLists/FFT320_Win200_frq200.0-2000.0_ovr50/cool artist - ayo yo_sugarv2.wav_stft.txt";
        // String text = "FourierLists/FFT320_Win200_frq200.0-2000.0_ovr50/cool artist - ayo yo_king-of-anythingv2.wav_stft.txt";
        // String text = "FourierLists/FFT640_Win20_frq0.0-5000.0_ovr75/cool artist - ayo yo_king-of-anythingv2.wav_stft.txt";

        // Get file information
        File file = new File(filePath);
        String fileName = file.getName();
        String fileDir = file.getParent();
        filePath = file.getAbsolutePath();
        
        // Extract file information, columns are defined by spaces and rows are line breaks
        // So create scanner to read the file
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
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

        // Create a BufferedImage to store the image
        BufferedImage image = new BufferedImage(numCols, numRows, BufferedImage.TYPE_INT_RGB);
        // Read the file again to fill the image
        try {
            scanner = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
            return;
        }
        
        int row = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.trim().isEmpty()) {
                String[] parts = line.split(" ");
                for (int col = 0; col < numCols; col++) {
                    // Parse the value and map it to a color
                    double value = Double.parseDouble(parts[col]);
                    int colorValue = (int) (Math.min(Math.max(value, 0), 255)); // Clamp value to [0, 255]
                    // // make low values less visible
                    // if (colorValue < 100) {
                    //     colorValue = 0;
                    // }
                    // Color color = new Color(colorValue, colorValue, colorValue);

                    // map value to colors going from low: blue (dark to light), mid: green, high: red (light to dark)
                    // For example blue would be from 0 to 80 and then normalize those to 0 to 255

                    if (value < 80) {
                        colorValue = (int) (value * 3.1875); // Normalize to 0-255
                        Color color = new Color(0, 0, colorValue);
                        image.setRGB(col, row, color.getRGB());
                    } else if (value < 160) {
                        colorValue = (int) ((value - 80) * 3.1875); // Normalize to 0-255
                        Color color = new Color(0, colorValue, 255 - colorValue);
                        image.setRGB(col, row, color.getRGB());
                    } else if (value < 240) {
                        colorValue = (int) ((value - 160) * 3.1875); // Normalize to 0-255
                        Color color = new Color(colorValue, 255 - colorValue, 0);
                        image.setRGB(col, row, color.getRGB());
                    } else {
                        colorValue = (int) ((value - 240) * 3.1875); // Normalize to 0-255
                        Color color = new Color(255 - colorValue, 0, 0);
                        image.setRGB(col, row, color.getRGB());
                    }

                    // image.setRGB(col, row, color.getRGB());
                }
                row++;
            }
        }
        scanner.close();
        // Save the image to a new file with same name but different extension
        String imageFilePath = fileDir + File.separator + fileName.replace(".txt", ".png");
        try {
            ImageIO.write(image, "png", new File(imageFilePath));
        } catch (IOException e) {
            System.out.println("Error saving image: " + imageFilePath);
            return;
        }
        System.out.println("Image saved: " + imageFilePath);
    }
}