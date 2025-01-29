import javax.swing.JFrame;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.*;

public class first {
    public static void main(String[] args) {

        audioSample a = new audioSample();
        a.setFile("audio/output.wav");
        int numSamples = a.getMaxSamples();
        double audioLength = a.getLength();
        System.out.printf("Audio length: %.2f seconds\n", audioLength);
        a.setNumSamples(numSamples);
        a.setStepSize(1);
        a.computeSamples();
        int[] samples = a.getSamples();

        second s = new second(samples);
        s.setVisible(true);

        // Remember: open xming
        // Remember: "export DISPLAY =:0" before running
    }
}
