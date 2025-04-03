public class BinCalculations {
    public static void linear_bins(int[][] fft, int bins, int pixelHeight, int pixelWidth) {
        // Linear bins
        if (bins != 0) {
            int binSize = Math.max(1, pixelHeight / bins); // Ensure at least 1 pixel per bin

            // Iterate over columns
            for (int i = 0; i < pixelWidth; i++) {
                int j = 0; // Start at the top of the column

                while (j < pixelHeight) {
                    int maxBin = Integer.MIN_VALUE; // Start with the smallest value
                    int maxBinIndex = j;

                    // Find max value within this bin
                    for (int k = 0; k < binSize && (j + k) < pixelHeight; k++) {
                        if (fft[j + k][i] > maxBin) {
                            maxBin = fft[j + k][i];
                            maxBinIndex = j + k;
                        }
                    }

                    // Set all pixels in the bin to 0 except the max
                    for (int k = 0; k < binSize && (j + k) < pixelHeight; k++) {
                        int currentIndex = j + k;
                        if (currentIndex != maxBinIndex) {
                            fft[currentIndex][i] = 0; // Zero out other pixels
                        }
                    }

                    // Move to the next bin
                    j += binSize;
                }
            }
        }
    }

    public static void logorithmic_bins(int[][] fft, int bins, int pixelHeight, int pixelWidth, double logBase) {
        // Logarithmic bins
        if (bins != 0) {

            // Iterate over columns
            for (int i = 0; i < pixelWidth; i++) {
                int binIndex = 0; // Track bin number
                int j = 0; // Start at the top of the column

                while (j < pixelHeight) {
                    int maxBin = Integer.MIN_VALUE; // Start with the smallest value
                    int maxBinIndex = j;

                    // Determine bin size (ensure at least 1 to prevent infinite loops)
                    int binSize = Math.max(1, (int) Math.pow(logBase, binIndex));
                    binSize = Math.min(binSize, pixelHeight - j); // Ensure within bounds

                    // Find max value within this bin
                    for (int k = 0; k < binSize; k++) {
                        if (fft[j + k][i] > maxBin) {
                            maxBin = fft[j + k][i];
                            maxBinIndex = j + k;
                        }
                    }

                    // Set all pixels in the bin to 0 except the max
                    for (int k = 0; k < binSize; k++) {
                        int currentIndex = j + k;
                        if (currentIndex != maxBinIndex) {
                            fft[currentIndex][i] = 0; // Zero out other pixels
                        }
                    }

                    // Move to the next bin
                    j += binSize;
                    binIndex++;
                }
            }
        }
    }
}
