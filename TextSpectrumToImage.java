import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Color;

public class TextSpectrumToImage {
    public static void FileToImage(String filePath) {

        // Get file information
        File file = new File(filePath);
        String fileName = file.getName();
        String fileDir = file.getParent();
        filePath = file.getAbsolutePath();

        int numRows = GetRowsFile(file);
        int numCols = GetColumnsFile(file);
        
        // Create a BufferedImage to store the image
        BufferedImage image = new BufferedImage(numCols, numRows, BufferedImage.TYPE_INT_RGB);
        
        // Get array from file
        int[][] array = GetArrayFromFile(file);
        // Fill the image with colors based on the array values
        DrawToImage(array, image);

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

    public static void ArrayToImage(int[][] array, File file) {
        // Create a BufferedImage to store the image
        int numRows = array.length;
        int numCols = array[0].length;
        BufferedImage image = new BufferedImage(numCols, numRows, BufferedImage.TYPE_INT_RGB);

        // Fill the image with colors based on the array values
        DrawToImage(array, image);
        
        // Save the image to the file
        String imageFilePath = file.getAbsolutePath();
        try {
            ImageIO.write(image, "png", new File(imageFilePath));
        } catch (IOException e) {
            System.out.println("Error saving image: " + imageFilePath);
            return;
        }

        System.out.println("Image saved: " + imageFilePath);
    }

    public static void ArrayToImageWhite(int[][] array, File file) {
        // Create a BufferedImage to store the image
        int numRows = array.length;
        int numCols = array[0].length;
        BufferedImage image = new BufferedImage(numCols, numRows, BufferedImage.TYPE_INT_RGB);

        // Fill the image with white where it isn't 0
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int value = array[row][col];
                if (value != 0) {
                    image.setRGB(col, row, Color.WHITE.getRGB());
                } else {
                    image.setRGB(col, row, Color.BLACK.getRGB());
                }
            }
        }
        
        // Save the image to the file
        String imageFilePath = file.getAbsolutePath();
        try {
            ImageIO.write(image, "png", new File(imageFilePath));
        } catch (IOException e) {
            System.out.println("Error saving image: " + imageFilePath);
            return;
        }

        System.out.println("Image saved: " + imageFilePath);
    }

    private static int GetRowsFile(File file) {
        int rows = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.trim().isEmpty()) {
                    rows++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.getAbsolutePath());
        }
        return rows;
    }

    private static int GetColumnsFile(File file) {
        int columns = 0;
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                columns = parts.length;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.getAbsolutePath());
        }
        return columns;
    }

    private static int[][] GetArrayFromFile(File file) {
        int rows = GetRowsFile(file);
        int columns = GetColumnsFile(file);
        int[][] array = new int[rows][columns];
        try (Scanner scanner = new Scanner(file)) {
            for (int row = 0; row < rows; row++) {
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(" ");
                    for (int col = 0; col < columns; col++) {
                        array[row][col] = Integer.parseInt(parts[col]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.getAbsolutePath());
        }
        return array;
    }

    private static void DrawToImage(int[][] array, BufferedImage image) {
        int numRows = array.length;
        int numCols = array[0].length;

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int value = array[row][col];
                int colorValue = (int) (Math.min(Math.max(value, 0), 255)); // Clamp value to [0, 255]

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
            }
        }
    }
}