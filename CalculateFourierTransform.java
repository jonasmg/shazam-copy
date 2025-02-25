public class CalculateFourierTransform {
    private int sampleRate = 44100;
    private int fourierQuality = 1;
    private int fourierSteps = 1024;
    private double minFreq = 1.0;
    private double maxFreq = 5000.0;
    private int pixelHeight = 800;
    private int pixelWidth = 1600;

    private int frameLength = 100; // Frame length in ms
    private int frameShift = 50;   // Frame shift in ms

    private int firstSample = 0;
    private int lastSample = 0;

    private int[] samples;
    private int sampleSize;

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    public void setSamples(int[] samples) {
        this.samples = samples;
    }

    public int getFourierQuality() {
        return fourierQuality;
    }

    public int getPixelHeight() {
        return pixelHeight;
    }

    public int getPixelWidth() {
        return pixelWidth;
    }

    public int[][] calculateDiscreteFourierTransform() {
        int statusPrints = 100;
        int[][] spectrogram = new int[pixelHeight][pixelWidth];

        int N = fourierSteps * fourierQuality;

        int binStart = (int) Math.round(minFreq * N / sampleRate);
        int binEnd = (int) Math.round(maxFreq * N / sampleRate);

        lastSample = samples.length - N;

        double maxMagnitude = 1.0; // Keep track of the highest magnitude

        for (int currentFrame = firstSample; currentFrame < lastSample; currentFrame += frameShift) {
            for (int i = binStart; i <= binEnd; i++) {
                Complex X_i = computeDFT(samples, i, N, currentFrame);
                double magnitude = X_i.magnitude();

                if (magnitude > maxMagnitude) {
                    maxMagnitude = magnitude; // Update max magnitude dynamically
                }

                double frequency = i * sampleRate / (double) N;

                int pixelX = (int) Math.round((currentFrame / (double) sampleSize) * pixelWidth);
                int pixelY = pixelHeight - 1 - (int) Math.round((frequency / maxFreq) * pixelHeight);

                if (pixelX >= 0 && pixelX < pixelWidth && pixelY >= 0 && pixelY < pixelHeight) {
                    spectrogram[pixelY][pixelX] += (int) magnitude;
                }
            }

            if (currentFrame % (sampleSize / statusPrints) == 0) {
                System.out.println("Progress: " + (int) (100 * currentFrame / (double) sampleSize) + "%");
            }
        }

        // Normalize to 255
        for (int y = 0; y < pixelHeight; y++) {
            for (int x = 0; x < pixelWidth; x++) {
                spectrogram[y][x] = (int) (spectrogram[y][x] / maxMagnitude * 255);
                spectrogram[y][x] = Math.min(255, spectrogram[y][x]); // Cap at 255
            }
        }

        return spectrogram;
    }

    public static Complex computeDFT(int[] x, int k, int N, int currentSample) {
        double real = 0;
        double imag = 0;

        int endSample = Math.min(currentSample + N, x.length);

        for (int n = currentSample; n < endSample; n++) {
            double angle = -2 * Math.PI * k * (n - currentSample) / N;
            real += x[n] * Math.cos(angle);
            imag += x[n] * Math.sin(angle);
        }

        return new Complex(real, imag);
    }
}

class Complex {
    private final double re;
    private final double im;

    public Complex(double real, double imag) {
        this.re = real;
        this.im = imag;
    }

    public double magnitude() {
        return Math.sqrt(re * re + im * im);
    }
}
