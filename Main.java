import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.*;

public class Main {

    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        System.out.println("Hello, world!");

        Scanner scanner = new Scanner(System.in);
        
        File file = new File("test.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

        AudioFormat format = audioStream.getFormat();
        System.out.println("Audio Format: " + format);

        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();

        String response = scanner.next();
        
    }
}