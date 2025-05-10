public class ShortTimeFourierTransform {
    private int sampleRate; // Sample rate in Hz
    // private int fourierQuality; // Size of FFT, dynamically calculated for frequency resolution
    private int windowSize; // Window size in milliseconds
    private int windowOverlap; // Overlap percentage between windows
    private int[] inputSignal; // Raw audio signal
    private int[][] stftResult; // Spectrogram result: frequency bins x time frames
    private int width; // Time dimension of spectrogram
    private int height; // Frequency dimension of spectrogram
    private double minFreq; // Minimum frequency to display
    private double maxFreq; // Maximum frequency to display
    private int desiredFrequencyBins; // Desired number of frequency bins between min and max freq

    public ShortTimeFourierTransform(int sampleRate, int desiredFrequencyBins, int windowSize, int windowOverlap, double minFreq, double maxFreq) {
        this.sampleRate = sampleRate;
        this.desiredFrequencyBins = desiredFrequencyBins;
        this.windowSize = windowSize;
        this.windowOverlap = windowOverlap;
        this.minFreq = minFreq;
        this.maxFreq = maxFreq;
    }

    public void setSignal(int[] inputSignal) {
        this.inputSignal = inputSignal;
    }

    public int[][] computeSTFT() {
        // Convert window size from milliseconds to samples
        int windowSizeSamples = (int) (windowSize * sampleRate / 1000.0);

        // Compute the number of samples to move for each frame (based on overlap)
        int frameShiftSamples = (int) (windowSizeSamples * (1 - windowOverlap / 100.0));

        // Last sample to start a full window
        int lastSample = inputSignal.length - windowSizeSamples;

        // Number of frames (horizontal resolution of spectrogram)
        this.width = (int) Math.ceil((double) inputSignal.length / windowSizeSamples);

        // Number of frequency bins (vertical resolution of spectrogram)
        this.height = desiredFrequencyBins;

        // Initialize spectrogram matrix
        this.stftResult = new int[height][width];

        for (int currentFrame = 0; currentFrame < lastSample; currentFrame += frameShiftSamples) {
            // Create FFT input window, zero-padded to fourierQuality
            Complex[] window = new Complex[windowSizeSamples];
            for (int i = 0; i < windowSizeSamples; i++) {
                int index = currentFrame + i;
                double sample = (index < inputSignal.length) ? inputSignal[index] : 0;
                window[i] = new Complex(sample, 0);
            }

            // Perform FFT
            Complex[] fftResult = computeFFT(window, sampleRate, minFreq, maxFreq, desiredFrequencyBins);
            
            // Calculate magnitude and store in spectrogram
            for (int i = 0; i < desiredFrequencyBins; i++) {
                double magnitude = fftResult[i].magnitude();
                stftResult[desiredFrequencyBins-i-1][currentFrame / windowSizeSamples] = (int) (magnitude * 255);
            }
        }

        // Normalize the spectrogram values to fit in the range of 0-255
        for (int i = 0; i < height; i++) {
            int maxVal = 0;
            for (int j = 0; j < width; j++) {
                if (stftResult[i][j] > maxVal) {
                    maxVal = stftResult[i][j];
                }
            }
            for (int j = 0; j < width; j++) {
                stftResult[i][j] = (int) ((stftResult[i][j] / (double) maxVal) * 255);
            }
        }

        // Return the spectrogram result
        return stftResult;
    }

    public static Complex[] computeFilteredFFT(Complex[] x, double sampleRate, double minFreq, double maxFreq, int desiredFrequencyBins) {
        int N = x.length;
        if (N <= 1) {
            return new Complex[]{(x.length > 0 && x[0] != null) ? x[0] : new Complex(0, 0)};
        }
    
        // Ensure valid arrays
        Complex[] even = new Complex[N / 2];
        Complex[] odd = new Complex[N / 2];
    
        for (int i = 0; i < N / 2; i++) {
            even[i] = (x[i * 2] != null) ? x[i * 2] : new Complex(0, 0);
            odd[i] = (x[i * 2 + 1] != null) ? x[i * 2 + 1] : new Complex(0, 0);
        }
    
        Complex[] fftEven = computeFilteredFFT(even, sampleRate, minFreq, maxFreq, desiredFrequencyBins);
        Complex[] fftOdd = computeFilteredFFT(odd, sampleRate, minFreq, maxFreq, desiredFrequencyBins);
    
        Complex[] result = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            Complex t = Complex.exp(-2 * Math.PI * k / N).multiply(fftOdd[k]);
            result[k] = fftEven[k].add(t);
            result[k + N / 2] = fftEven[k].subtract(t);
        }
    
        // Post-process to filter specific frequencies
        Complex[] filteredResult = new Complex[desiredFrequencyBins];
        double frequencyResolution = sampleRate / N; // Frequency step per bin
        for (int i = 0; i < desiredFrequencyBins; i++) {
            double freq = i * frequencyResolution;
            if (freq >= minFreq && freq <= maxFreq) {
                filteredResult[i] = result[i];
            } else {
                filteredResult[i] = new Complex(0, 0); // Zero out frequencies outside the range
            }
        }
    
        return filteredResult;
    }

    public static Complex[] computeFFT(Complex[] x, double sampleRate, double minFreq, double maxFreq, int desiredFrequencyBins) {
        // Calculate required FFT size N so frequency resolution can support desired bins
        double frequencyResolution = (maxFreq - minFreq) / desiredFrequencyBins;
        int requiredN = (int) Math.ceil(sampleRate / frequencyResolution);
    
        // Round N up to next power of 2
        int N = 1;
        while (N < requiredN) N *= 2;
    
        // Pad input array with zeros if necessary
        Complex[] padded = new Complex[N];
        for (int i = 0; i < N; i++) {
            if (i < x.length) {
                padded[i] = x[i];
            } else {
                padded[i] = new Complex(0, 0);
            }
        }
    
        // Run full FFT
        Complex[] fft = fftRecursive(padded);
    
        // Select desired frequency bins
        Complex[] freqBins = new Complex[desiredFrequencyBins];
        for (int i = 0; i < desiredFrequencyBins; i++) {
            double targetFreq = minFreq + i * frequencyResolution;
            int k = (int) Math.round(targetFreq * N / sampleRate);
            if (k < fft.length) {
                freqBins[i] = fft[k];
            } else {
                freqBins[i] = new Complex(0, 0);
            }
        }
    
        return freqBins;
    }
    
    // Standard recursive Cooley-Tukey FFT
    private static Complex[] fftRecursive(Complex[] x) {
        int N = x.length;
        if (N == 1) return new Complex[] { x[0] };
    
        Complex[] even = new Complex[N / 2];
        Complex[] odd = new Complex[N / 2];
    
        for (int i = 0; i < N / 2; i++) {
            even[i] = x[2 * i];
            odd[i] = x[2 * i + 1];
        }
    
        Complex[] fftEven = fftRecursive(even);
        Complex[] fftOdd = fftRecursive(odd);
    
        Complex[] combined = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            double angle = -2 * Math.PI * k / N;
            Complex exp = new Complex(Math.cos(angle), Math.sin(angle));
            combined[k] = fftEven[k].add(exp.multiply(fftOdd[k]));
            combined[k + N / 2] = fftEven[k].subtract(exp.multiply(fftOdd[k]));
        }
    
        return combined;
    }
    
    static class Complex {
        private final double re;
        private final double im;
    
        public double getRe() {
            return re;
        }
    
        public double getIm() {
            return im;
        }
    
        public Complex(double real, double imag) {
            this.re = real;
            this.im = imag;
        }
    
        public double magnitude() {
            return Math.sqrt(re * re + im * im);
        }
    
        public Complex add(Complex other) {
            return new Complex(this.re + other.re, this.im + other.im);
        }
    
        public Complex subtract(Complex other) {
            return new Complex(this.re - other.re, this.im - other.im);
        }
    
        public Complex multiply(Complex other) {
            if (other == null) {
                throw new IllegalArgumentException("Cannot multiply by null");
            }
            return new Complex(this.re * other.re - this.im * other.im, this.re * other.im + this.im * other.re);
        }
    
        public static Complex exp(double theta) {
            return new Complex(Math.cos(theta), Math.sin(theta));
        }
    }
}