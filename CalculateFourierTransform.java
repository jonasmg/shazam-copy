public class CalculateFourierTransform {
    private int sampleRate = 44100;
    private int fourierQuality = 4;
    private int fourierSteps = 1024;
    private double minFreq = 200;
    private double maxFreq = 2000.0;
    private int pixelHeight = 800;
    private int pixelWidth = 1600;

    private int frameLength = 100; // Frame length in ms
    private int frameShift = 50;   // Frame shift in ms

    private int firstSample = 0;
    private int lastSample = 0;

    private int[] samples;
    private int sampleSize;

    private double maxMagnitude = 1.0;

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    public void setSamples(int[] samples) {
        this.samples = samples;
    }

    public void setFourierQuality(int fourierQuality) {
        this.fourierQuality = fourierQuality;
    }

    // Set sample rate
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setPixelHeight(int pixelHeight) {
        this.pixelHeight = pixelHeight;
    }

    public void setPixelWidth(int pixelWidth) {
        this.pixelWidth = pixelWidth;
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

    public double getMaxMagnitude() {
        return maxMagnitude;
    }

    // getter and setter for frame length and shift
    public void setFrameLength(int frameLength) {
        this.frameLength = frameLength;
    }

    public void setFrameShift(int frameShift) {
        this.frameShift = frameShift;
    }

    public int getFrameLength() {
        return frameLength;
    }

    public int getFrameShift() {
        return frameShift;
    }

    public int[][] calculateDiscreteFourierTransform() {
        int statusPrints = 100;
        int[][] spectrogram = new int[pixelHeight][pixelWidth];

        int N = fourierSteps * fourierQuality;

        int binStart = (int) Math.round(minFreq * N / sampleRate);
        int binEnd = (int) Math.round(maxFreq * N / sampleRate);

        lastSample = samples.length - frameShift;

        maxMagnitude = 1.0; // Keep track of the highest magnitude

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

    public int[][] calculateFastFourierTransform() {
        int[][] spectrogram = new int[pixelHeight][pixelWidth];
        
        int frameLengthSamples = (int) (frameLength * sampleRate / 1000.0);
        int frameShiftSamples = (int) (frameShift * sampleRate / 1000.0);
        
        // Calculate N based on fourierQuality and fourierSteps
        int N = fourierSteps * fourierQuality;  // FFT size
        
        lastSample = samples.length - frameShiftSamples;
        maxMagnitude = 1.0;
        
        for (int currentFrame = firstSample; currentFrame < lastSample; currentFrame += frameShiftSamples) {
            Complex[] signal = new Complex[N];
            for (int i = 0; i < N; i++) {
                int sampleValue = (currentFrame + i < samples.length) ? samples[currentFrame + i] : 0;
                signal[i] = new Complex(sampleValue, 0);  // Ensure no null values
            }
    
            Complex[] fftResult = computeFFT(signal);
    
            for (int i = 0; i < fftResult.length / 2; i++) { // Only use the first half of the FFT result
                double magnitude = fftResult[i].magnitude();
                if (magnitude > maxMagnitude) {
                    maxMagnitude = magnitude;
                }
    
                // Calculate the frequency for each bin
                double frequency = i * sampleRate / (double) N;
    
                // Only use frequencies between minFreq and maxFreq
                if (frequency >= minFreq && frequency <= maxFreq) {
                    // Map frequency to vertical pixel position based on the minFreq and maxFreq
                    int pixelX = (int) Math.round((currentFrame / (double) sampleSize) * pixelWidth);
                    
                    // Map the frequency range to the vertical axis of the spectrogram
                    int pixelY = pixelHeight - 1 - (int) Math.round(((frequency - minFreq) / (maxFreq - minFreq)) * pixelHeight);
    
                    // Ensure the pixel position is within bounds
                    if (pixelX >= 0 && pixelX < pixelWidth && pixelY >= 0 && pixelY < pixelHeight) {
                        spectrogram[pixelY][pixelX] += (int) magnitude;
                    }
                }
            }
        }
    
        return spectrogram;
    }
    

    public int[][] calculateFastFourierTransformOld3() {
        int[][] spectrogram = new int[pixelHeight][pixelWidth];
        
        int frameLengthSamples = (int) (frameLength * sampleRate / 1000.0);
        int frameShiftSamples = (int) (frameShift * sampleRate / 1000.0);
        // int N = frameLengthSamples; // Ensure FFT size matches the window size
        int N = fourierSteps * fourierQuality; // Ensure FFT size matches the window size
    
        lastSample = samples.length - frameShiftSamples;
        maxMagnitude = 1.0;
    
        for (int currentFrame = firstSample; currentFrame < lastSample; currentFrame += frameShiftSamples) {
            Complex[] signal = new Complex[frameLengthSamples];
            for (int i = 0; i < frameLengthSamples; i++) {
                int sampleValue = (currentFrame + i < samples.length) ? samples[currentFrame + i] : 0;
                signal[i] = new Complex(sampleValue, 0);  // Ensure no null values
            }
            
            Complex[] fftResult = computeFFT(signal);
    
            for (int i = 0; i < fftResult.length / 2; i++) { // Only use the first half
                double magnitude = fftResult[i].magnitude();
                if (magnitude > maxMagnitude) {
                    maxMagnitude = magnitude;
                }
                
                // Fix: Use `N` to map frequencies correctly
                double frequency = i * sampleRate / (double) N;
                
                int pixelX = (int) Math.round((currentFrame / (double) sampleSize) * pixelWidth);
                int pixelY = pixelHeight - 1 - (int) Math.round((frequency / maxFreq) * pixelHeight);
                
                if (pixelX >= 0 && pixelX < pixelWidth && pixelY >= 0 && pixelY < pixelHeight) {
                    spectrogram[pixelY][pixelX] += (int) magnitude;
                }
            }
        }
        
        return spectrogram;
    }    

    public int[][] calculateFastFourierTransformOld() {
        int[][] spectrogram = new int[pixelHeight][pixelWidth];
        int N = fourierSteps * fourierQuality;
        
        int frameLengthSamples = (int) (frameLength * sampleRate / 1000.0);
        int frameShiftSamples = (int) (frameShift * sampleRate / 1000.0);
        
        lastSample = samples.length - frameShiftSamples;
        maxMagnitude = 1.0;
        
        for (int currentFrame = firstSample; currentFrame < lastSample; currentFrame += frameShiftSamples) {
            // Complex[] signal = new Complex[frameLengthSamples];
            // for (int i = 0; i < frameLengthSamples; i++) {
            //     signal[i] = new Complex(currentFrame + i < samples.length ? samples[currentFrame + i] : 0, 0);
            // }

            Complex[] signal = new Complex[frameLengthSamples];
            for (int i = 0; i < frameLengthSamples; i++) {
                int sampleValue = (currentFrame + i < samples.length) ? samples[currentFrame + i] : 0;
                // Print sample value
                // System.out.println(sampleValue);
                signal[i] = new Complex(sampleValue, 0);  // Ensure no null values
            }
            
            // System.out.println("Signal length: " + signal.length);
            Complex[] fftResult = computeFFT(signal);
            // System.out.println("FFT result length: " + fftResult.length);
            
            for (int i = 0; i < fftResult.length / 2; i++) { // Only use the first half
                double magnitude = fftResult[i].magnitude();
                // Print magnitude
                // System.out.println(magnitude);
                if (magnitude > maxMagnitude) {
                    maxMagnitude = magnitude;
                }
                
                double frequency = i * sampleRate / (double) N;
                int pixelX = (int) Math.round((currentFrame / (double) sampleSize) * pixelWidth);
                int pixelY = pixelHeight - 1 - (int) Math.round((frequency / maxFreq) * pixelHeight);
                
                if (pixelX >= 0 && pixelX < pixelWidth && pixelY >= 0 && pixelY < pixelHeight) {
                    spectrogram[pixelY][pixelX] += (int) magnitude;
                }
            }
        }
        
        return spectrogram;
    }

    public int[][] calculateFastFourierTransformOldOld() {
        int[][] spectrogram = new int[pixelHeight][pixelWidth];
        int N = fourierSteps * fourierQuality;
        
        lastSample = samples.length - frameShift;
        maxMagnitude = 1.0;
        
        for (int currentFrame = firstSample; currentFrame < lastSample; currentFrame += frameShift) {
            Complex[] signal = new Complex[N];
            for (int i = 0; i < N; i++) {
                signal[i] = new Complex(currentFrame + i < samples.length ? samples[currentFrame + i] : 0, 0);
            }
            
            Complex[] fftResult = computeFFT(signal);
            
            for (int i = 0; i < fftResult.length / 2; i++) { // Only use the first half
                double magnitude = fftResult[i].magnitude();
                if (magnitude > maxMagnitude) {
                    maxMagnitude = magnitude;
                }
                
                double frequency = i * sampleRate / (double) N;
                int pixelX = (int) Math.round((currentFrame / (double) sampleSize) * pixelWidth);
                int pixelY = pixelHeight - 1 - (int) Math.round((frequency / maxFreq) * pixelHeight);
                
                if (pixelX >= 0 && pixelX < pixelWidth && pixelY >= 0 && pixelY < pixelHeight) {
                    spectrogram[pixelY][pixelX] += (int) magnitude;
                }
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

    public static Complex[] computeFFT(Complex[] x) {
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
    
        Complex[] fftEven = computeFFT(even);
        Complex[] fftOdd = computeFFT(odd);
    
        // Ensure fftEven and fftOdd never have null values
        for (int i = 0; i < fftEven.length; i++) {
            if (fftEven[i] == null) {
                fftEven[i] = new Complex(0, 0);
            }
        }
        for (int i = 0; i < fftOdd.length; i++) {
            if (fftOdd[i] == null) {
                fftOdd[i] = new Complex(0, 0);
            }
        }
    
        Complex[] result = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            Complex t = Complex.exp(-2 * Math.PI * k / N).multiply(fftOdd[k]);
            result[k] = fftEven[k].add(t);
            result[k + N / 2] = fftEven[k].subtract(t);
        }
        return result;
    }

    public static Complex[] computeFFTOld(Complex[] x) {
        int N = x.length;
        if (N <= 1) return x;

        Complex[] even = new Complex[N / 2];
        Complex[] odd = new Complex[N / 2];
        for (int i = 0; i < N / 2; i++) {
            // even[i] = x[i * 2];
            // odd[i] = x[i * 2 + 1];
            even[i] = (x[i * 2] != null) ? x[i * 2] : new Complex(0, 0);
            odd[i] = (x[i * 2 + 1] != null) ? x[i * 2 + 1] : new Complex(0, 0);
        }

        Complex[] fftEven = computeFFT(even);
        Complex[] fftOdd = computeFFT(odd);

        Complex[] result = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            Complex t = Complex.exp(-2 * Math.PI * k / N).multiply(fftOdd[k]);
            result[k] = fftEven[k].add(t);
            result[k + N / 2] = fftEven[k].subtract(t);
        }
        return result;
    }
}

class Complex {
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
