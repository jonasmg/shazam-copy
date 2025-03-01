import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {

        String fileName = "test3.wav";
        // String fileName = "piano-audio-test.wav";
        // String fileName = "jonasMusic.wav";
        String filePath = "audio/" + fileName;

        int fourierQuality = 4;
        int pixelHeight = 1024;
        int pixelWidth = 1024*2;

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

        // Get resolution from calculateFourierTransform
        CalculateFourierTransform c = new CalculateFourierTransform();
        c.setSampleSize(numSamples);
        c.setSamples(samples);

        // Print status
        System.out.println("Calculating DFT...");

        // Quality
        c.setFourierQuality(fourierQuality);
        c.setPixelHeight(pixelHeight);
        c.setPixelWidth(pixelWidth);

        int[][] dft = c.calculateDiscreteFourierTransform();

        // Print status
        System.out.println("DFT calculated!");

        // Create buffered image
        BufferedImage bufferedImage = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < pixelWidth; i++) {
            for (int j = 0; j < pixelHeight; j++) {
                int value = dft[j][i];
                value = Math.min(value, 255);
                int rgb = new Color(value, value, value).getRGB();
                bufferedImage.setRGB(i, j, rgb);
            }
        }

        // Print status
        System.out.println("Creating image...");

        // File name, original + fourierQuality
        String imageName = "output/" + fileName.substring(0, fileName.length() - 4) + "-Q" + fourierQuality + ".png";

        File file2 = new File(imageName);
        try {
            ImageIO.write(bufferedImage, "png", file2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print status
        System.out.println("Image created!");


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
}
