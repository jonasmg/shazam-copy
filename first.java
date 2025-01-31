public class first {
    public static void main(String[] args) {

        String file = "audio/test.wav";

        audioSample a = new audioSample();
        a.setFile(file);
        int numSamples = a.getMaxSamples();
        double audioLength = a.getLength();
        System.out.printf("Audio length: %.2f seconds\n", audioLength);
        a.setNumSamples(numSamples);
        a.setStepSize(1);
        a.computeSamples();
        int[] samples = a.getSamples();

        second s = new second(samples, file);
        s.setVisible(true);

        // Remember: to open xming
        // Remember: "export DISPLAY=:0"
    }
}
