import java.awt.*;
import javax.swing.*;

public class Macheads {

    public static void main(String args[]) {
        // Create the JFrame
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setTitle("Audio Visualizer");
        f.setSize(350, 300);
        f.setResizable(true);

        // Access the contentPane and set its background color
        f.getContentPane().setBackground(Color.RED);
        f.setBackground(Color.RED);

        // Make the frame visible
        f.setVisible(true);
    }
}