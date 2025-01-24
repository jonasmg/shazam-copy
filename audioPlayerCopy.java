import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.*;

public class audioPlayerCopy {

    public static void main(String[] args) {
        // How to PLAY AUDIO with Java (.wav, .au, .aiff)

        String filePath = "audio/test.wav";

        File file = new File(filePath);

        try(Scanner scanner = new Scanner(System.in);

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)){
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            String response = "";

            while(!response.equals("Q")){

                System.out.println("P = Play");
                System.out.println("S = Stop");
                System.out.println("R = Reset");
                System.out.println("Q = Quit");
                System.out.print("Enter your choice: ");

                response = scanner.next().toUpperCase();

                switch(response){
                    case "P" -> clip.start();
                    case "S" -> clip.stop();
                    case "R" -> clip.setMicrosecondPosition(0);
                    case "Q" -> clip.close();
                    default -> System.out.println("Invalid choice");
                }
            }
        }

        catch(FileNotFoundException e){
            System.out.println("Could not locate file");
        }

        catch(UnsupportedAudioFileException e){
            System.out.println("Audio file is not supported");
        }

        catch(LineUnavailableException e){
            System.out.println("Unable to access audio resource");
        }

        catch(IOException e){
            System.out.println("Something went wrong");
        }

        finally{
            System.out.println("Bye!");
        }
    }
}
