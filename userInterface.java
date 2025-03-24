import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
// import java.util.Arrays;
import javax.swing.*;

public class userInterface extends JFrame implements ActionListener, KeyListener, MouseWheelListener {

    private Timer animator;
    private int delay = 0, currentFrame = 0;
    private final int totalFrames = 10000;
    private int quality = 1;
    private int height = 350;
    private double scale = 1;
    private int sampleRate = 44100;
    private double freq = 0;
    private boolean ctrlPressed = false;
    private int fourierQuality = 1;
    
    // private static JLabel label;
    private static JTextField qualityTextField;
    private static JTextField qualityText;
    private static JTextField heightTextField;
    private static JTextField heightText;
    private static JTextField speedTextField;
    private static JTextField speedText;
    private static JTextField scaleTextField;
    private static JTextField scaleText;
    private static JTextField freqTextField;
    private static JTextField freqText;
    private static JTextField fourierQualityTextField;
    private static JTextField fourierQualityText;

    private JButton button;

    public userInterface(int[] samples, String file) {
        // double[] doubleSamples = Arrays.stream(samples).asDoubleStream().toArray();

        // Add key listener
        addKeyListener(this); // add key listener to the frame
        setFocusable(true); // make sure the frame is focused
        setFocusTraversalKeysEnabled(false); // make sure the frame is focused

        // Add mouse wheel listener
        addMouseWheelListener(this);

        // Label with text: "quality"
        JLabel qualityLabel = new JLabel("Quality: ");
        qualityLabel.setBounds(20, 20, 100, 30);
        add(qualityLabel);

        // Label with text: "height"
        JLabel heightLabel = new JLabel("Height: ");
        heightLabel.setBounds(140, 20, 100, 30);
        add(heightLabel);

        // Label with text: "Speed"
        JLabel scrollLabel = new JLabel("Scroll speed: ");
        scrollLabel.setBounds(260, 20, 100, 30);
        add(scrollLabel);

        // Label with text: "scale"
        JLabel scaleLabel = new JLabel("Scale: ");
        scaleLabel.setBounds(380, 20, 100, 30);
        add(scaleLabel);

        // Label with text: "frequency"
        JLabel freqLabel = new JLabel("Frequency: ");
        freqLabel.setBounds(500, 20, 100, 30);
        add(freqLabel);

        // Label with text: "fourier quality"
        JLabel fourierQualityLabel = new JLabel("Fourier Quality: ");
        fourierQualityLabel.setBounds(620, 20, 100, 30);
        add(fourierQualityLabel);

        // Text field for fourier quality
        fourierQualityTextField = new JTextField("" + fourierQuality);
        fourierQualityTextField.addKeyListener(this);
        fourierQualityText = new JTextField("" + fourierQuality);
        fourierQualityTextField.setBounds(620, 50, 100, 30);
        fourierQualityText.setBounds(620, 100, 100, 30);
        add(fourierQualityTextField);
        fourierQualityText.setEditable(false);
        add(fourierQualityText);
        
        qualityTextField = new JTextField("" + quality);
        qualityTextField.addKeyListener(this);
        qualityText = new JTextField("" + quality);
        heightTextField = new JTextField("" + height);
        heightTextField.addKeyListener(this);
        heightText = new JTextField("" + height);
        speedTextField = new JTextField("" + delay);
        speedTextField.addKeyListener(this);
        speedText = new JTextField("" + delay + "ms");
        scaleTextField = new JTextField("" + scale);
        scaleTextField.addKeyListener(this);
        scaleText = new JTextField("" + scale);
        freqTextField = new JTextField("" + freq);
        freqTextField.addKeyListener(this);
        freqText = new JTextField("" + freq + "Hz");

        button = new JButton("Update");
        button.addKeyListener(this);

        setLayout(null);
        Dimension size1 = qualityTextField.getPreferredSize();
        Dimension size2 = qualityText.getPreferredSize();
        Dimension size3 = heightTextField.getPreferredSize();
        Dimension size4 = heightText.getPreferredSize();
        Dimension size5 = speedTextField.getPreferredSize();
        Dimension size6 = speedText.getPreferredSize();
        Dimension size7 = scaleTextField.getPreferredSize();
        Dimension size8 = scaleText.getPreferredSize();
        Dimension size9 = freqTextField.getPreferredSize();
        Dimension size10 = freqText.getPreferredSize();

        Dimension sizeButton = button.getPreferredSize();
        qualityText.setEditable(false);
        heightText.setEditable(false);
        speedText.setEditable(false);
        scaleText.setEditable(false);
        freqText.setEditable(false);
        qualityTextField.setBounds(20, 50, 100, size1.height);
        heightTextField.setBounds(140, 50, 100, size3.height);
        qualityText.setBounds(20, 100, 100, size2.height);
        heightText.setBounds(140, 100, 100, size4.height);
        speedTextField.setBounds(260, 50, 100, size5.height);
        speedText.setBounds(260, 100, 100, size6.height);
        scaleTextField.setBounds(380, 50, 100, size7.height);
        scaleText.setBounds(380, 100, 100, size8.height);
        freqTextField.setBounds(500, 50, 100, size9.height);
        freqText.setBounds(500, 100, 100, size10.height);

        button.setBounds(20, 150, sizeButton.width, sizeButton.height);
        button.addActionListener(this);

        add(qualityTextField);
        add(heightTextField);
        add(qualityText);
        add(heightText);
        add(speedTextField);
        add(speedText);
        add(scaleTextField);
        add(scaleText);
        add(freqTextField);
        add(freqText);

        add(button);

        // Create panel
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Look at this graph from " + file));
        topPanel.setBounds(20, 200, 400, 30);

        // Add panel to the frame
        add(topPanel);

        animator = new Timer(delay, this);

        // Add timer
        if (delay < 1) {
            animator.stop();
        } else {
            animator.start();
        }

        // Custom panel to draw a line
        JPanel drawPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                int offX = 50;
                int offY = 50;
                Graphics2D g2 = (Graphics2D) g;
                // draw rectangle behind lines 800 wide and 200 tall
                g2.setColor(Color.LIGHT_GRAY);
                g2.fill(new Rectangle2D.Double(0+offX, 0+offY, 800, 200));
                // set thickness of the line
                g2.setStroke(new BasicStroke(2));
                // set color of the line to dark gray
                g2.setColor(Color.DARK_GRAY);
                // draw coordinate lines
                g2.draw(new Line2D.Double(0+offX, 100+offY, 800+offX, 100+offY));
                g2.draw(new Line2D.Double(400+offX, 0+offY, 400+offX, 200+offY));

                // set thickness of the line
                g2.setStroke(new BasicStroke(2));
                // set color of the line to black
                g2.setColor(Color.BLUE);
                // Set alpha to half
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

                // Extra Y offset
                int offY2 = 100+offY;

                // Draw samples:
                for (int i = 0; i < 800*quality; i++) {
                    double ii = (i*scale+currentFrame*quality)/quality;
                    double iii = ((i+1)*scale+currentFrame*quality)/quality;
                    if (iii < samples.length) {
                        double x1 = (i/quality)+offX;
                        double y1 = samples[(int)(ii)]/height+offY2;
                        double x2 = (i/quality)+1+offX;
                        double y2 = samples[(int)(iii)]/height+offY2;
                        g2.draw(new Line2D.Double(x1, y1, x2, y2));
                    }
                }

                g2.setColor(Color.BLACK);
                // Set alpha back to full
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

                // Draw frame number:
                g2.drawString(Integer.toString(currentFrame), 0+offX, 10);

                // Shown first sample
                int firstSample = (int) (currentFrame);
                if (firstSample > samples.length) {
                    firstSample = samples.length;
                }

                // Shown last sample
                int lastSample = (int) (currentFrame+800*scale);
                if (lastSample > samples.length) {
                    lastSample = samples.length;
                }

                g2.drawString(String.format("First sample: %d", firstSample), 320+offX, 10);
                g2.drawString(String.format("Last sample: %d", lastSample), 320+offX, 25);

                // Calculate integral of shown graph/samples with quality
                int integral = 0;
                for (int i = firstSample; i < lastSample; i++) {
                    integral += samples[i];
                }
                
                // Print integral shown with spaces every 3 digits from the back
                String integralStr = String.format("%,d", integral).replace(',', ' ');
                g2.drawString("Integral: " + integralStr, 70+offX, 10);

                // Calculate time
                double time0 = (double) ((0*scale+currentFrame)) / sampleRate;
                double time1 = (double) ((400*scale+currentFrame)) / sampleRate;
                double time2 = (double) ((800*scale+currentFrame)) / sampleRate;
                
                // Drawing measures of time
                if (scale <= 5) {
                    g2.drawString(String.format("%.4f sec", time0), 0+offX, 40);
                    g2.drawString(String.format("%.4f sec", time1), 400+offX-35, 40);
                    g2.drawString(String.format("%.4f sec", time2), 800+offX-70, 40);
                } else if (scale <= 40) {
                    g2.drawString(String.format("%.3f sec", time0), 0+offX, 40);
                    g2.drawString(String.format("%.3f sec", time1), 400+offX-35, 40);
                    g2.drawString(String.format("%.3f sec", time2), 800+offX-70, 40);
                } else {
                    g2.drawString(String.format("%.2f sec", time0), 0+offX, 40);
                    g2.drawString(String.format("%.2f sec", time1), 400+offX-35, 40);
                    g2.drawString(String.format("%.2f sec", time2), 800+offX-70, 40);
                }

                // Draw measures of amplitude
                g2.drawString("1", 0+offX-20, 0+offY);
                g2.drawString("0", 0+offX-20, 100+offY);
                g2.drawString("-1", 0+offX-20, 200+offY);

                // draw around rectangle
                g2.setColor(Color.BLACK);
                // set thickness of the line
                g2.setStroke(new BasicStroke(4));
                // lines of box
                g2.draw(new Line2D.Double(0+offX, 0+offY, 800+offX, 0+offY));
                g2.draw(new Line2D.Double(800+offX, 0+offY, 800+offX, 200+offY));
                g2.draw(new Line2D.Double(800+offX, 200+offY, 0+offX, 200+offY));
                g2.draw(new Line2D.Double(0+offX, 200+offY, 0+offX, 0+offY));
            }
        };
        drawPanel.setBounds(0, 250, 900, 300);
        add(drawPanel);

        // Custom panel to draw around circle
        JPanel drawPanel2 = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (freq != 0) {
                    int offX = 50;
                    int offY = 50;
                    Graphics2D g2 = (Graphics2D) g;
                    // Draw rectangle in entire space
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.fill(new Rectangle2D.Double(0+offX, 0+offY, 200, 200));
                    
                    // Draw cross inside rectangle with dark gray color
                    g2.setColor(Color.DARK_GRAY);
                    g2.setStroke(new BasicStroke(2));
                    g2.draw(new Line2D.Double(100+offX, 0+offY, 100+offX, 200+offY));
                    g2.draw(new Line2D.Double(0+offX, 100+offY, 200+offX, 100+offY));
    
                    // Do the funky fourier transform stuff here
                    // Shown first sample
                    int firstSample = (int) (currentFrame);
                    if (firstSample > samples.length) {
                        firstSample = samples.length;
                    }
    
                    // Shown last sample
                    int lastSample = (int) (currentFrame+800*scale);
                    if (lastSample > samples.length) {
                        lastSample = samples.length;
                    }
    
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(2));
    
                    double realFreq = sampleRate/freq;
                    // Draw samples around circle
                    for (int i = firstSample; i < lastSample; i++) {
                        // If more than 10000 samples skip 9 out of 10
                        if (lastSample - firstSample > 10000) {
                        if (i % 10 != 0) {
                            continue;
                        }
                        }
                        // If more than 100000 samples skip 99 out of 100
                        if (lastSample - firstSample > 100000) {
                        if (i % 100 != 0) {
                            continue;
                        }
                        }
                        // If more than 1000000 samples skip 999 out of 1000
                        if (lastSample - firstSample > 1000000) {
                        if (i % 1000 != 0) {
                            continue;
                        }
                        }
                        // If more than 10000000 samples skip 9999 out of 10000
                        if (lastSample - firstSample > 10000000) {
                        if (i % 10000 != 0) {
                            continue;
                        }
                        }
                        
                        
                        int sample1 = (int) (samples[i]/(height/1.5));
                        int sample2 = (int) (samples[i+1]/(height/1.5));
                        double x1 = sample1*Math.cos((i)*2*Math.PI/realFreq)+100+offX;
                        double y1 = sample1*Math.sin((i)*2*Math.PI/realFreq)+100+offY;
                        double x2 = sample2*Math.cos((i+1)*2*Math.PI/realFreq)+100+offX;
                        double y2 = sample2*Math.sin((i+1)*2*Math.PI/realFreq)+100+offY;
                        g2.draw(new Line2D.Double(x1, y1, x2, y2));
                    }
    
                    // Draw black lines around rectangle
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(4));
                    g2.draw(new Line2D.Double(0+offX, 0+offY, 200+offX, 0+offY));
                    g2.draw(new Line2D.Double(200+offX, 0+offY, 200+offX, 200+offY));
                    g2.draw(new Line2D.Double(200+offX, 200+offY, 0+offX, 200+offY));
                    g2.draw(new Line2D.Double(0+offX, 200+offY, 0+offX, 0+offY));
                }
            }
        };
        drawPanel2.setBounds(0, 520, 300, 300);

        add(drawPanel2);

        JPanel drawPanel3 = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int offX = 50;
            int offY = 50;
            Graphics2D g2 = (Graphics2D) g;
            // Draw rectangle in entire space
            g2.setColor(Color.LIGHT_GRAY);
            g2.fill(new Rectangle2D.Double(0+offX, 0+offY, 800, 300));
            
            // Draw cross inside rectangle with dark gray color
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(2));

            g2.draw(new Line2D.Double(0+offX, 150+offY, 800+offX, 150+offY));

            // Shown first sample
            int firstSample = (int) (currentFrame);
            if (firstSample > samples.length) {
                firstSample = samples.length;
            }
            
            // Shown last sample
            int lastSample = (int) (currentFrame+800*scale);
            if (lastSample > samples.length) {
                lastSample = samples.length;
            }
            
            // Do the funky fourier transform stuff here
            // Sampling settings
            int fs = 44100;   // Sampling rate (Hz)
            int N = 1024*fourierQuality;     // Number of samples
            // double fTarget = freq;  // Target frequency (Hz)

            // Calculate the bin index for 1 Hz and 5000 Hz
            int binStart = (int) Math.round(1.0 * N / fs);   // Corresponding to 1 Hz
            int binEnd = (int) Math.round(5000.0 * N / fs);  // Corresponding to 5000 Hz

            // Frequency range (1 Hz to 5000 Hz)
            double minFreq = 100.0;
            double maxFreq = 5000.0;
            double pixelWidth = 800.0;  // Width in pixels

            // Scale factor for the X-coordinate to map 1 Hz - 5000 Hz to 0-800 pixels
            double scaleFactorX = pixelWidth / (maxFreq - minFreq);

            // Scale factor for better visualization
            double scaleFactor = 100.0 / (N * 1000.0);

            // Compute DFT and plot results
            for (int i = binStart; i <= binEnd; i++) {  // Loop only through bins corresponding to 1-5000 Hz
                Complex X_i = computeDFT(samples, i, N, currentFrame);
                
                double magnitude = X_i.magnitude() * scaleFactor; // Proper scaling

                // Map the frequency bin index to pixel space for 1-5000 Hz range
                double frequency = i * fs / (double) N;  // Calculate frequency corresponding to the bin
                double xPosition = (frequency - minFreq) * scaleFactorX + offX;  // Map to X position within 800px range

                // Draw visualization (only for 1-5000 Hz)
                g2.draw(new Line2D.Double(
                xPosition,  // X coordinate (mapped)
                150 + offY + 150,  // Y start
                xPosition,  // X coordinate (same as start)
                150 + 150 + offY - magnitude // Y end (scaled)
                ));
            }

            // Draw vertical lines for specific frequencies: 1, 10, 100, 1000, and 5000 Hz
            int[] frequencyLabels = {100, 500, 1000, 2000, 3000, 4000, 5000};
            for (int i = 0; i < frequencyLabels.length; i++) {
                double f = frequencyLabels[i];
                int bin = (int) Math.round(f / fs * N);  // Map frequency to DFT bin
                double frequency = bin * fs / (double) N;  // Calculate the frequency
                double xPosition = (frequency - minFreq) * scaleFactorX + offX;  // Map to X position

                // Draw vertical line for the frequency
                g2.draw(new Line2D.Double(xPosition, 0 + offY, xPosition, 300 + offY));

                // Draw frequency label
                g2.drawString(Integer.toString(frequencyLabels[i]), (int)(xPosition - 15), 10 + offY - 20); // Adjust label position
            }

            // Draw black lines around rectangle
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(4));
            g2.draw(new Line2D.Double(0+offX, 0+offY, 800+offX, 0+offY));
            g2.draw(new Line2D.Double(800+offX, 0+offY, 800+offX, 300+offY));
            g2.draw(new Line2D.Double(800+offX, 300+offY, 0+offX, 300+offY));
            g2.draw(new Line2D.Double(0+offX, 300+offY, 0+offX, 0+offY));
            }
        };
        drawPanel3.setBounds(0, 820, 900, 400);

        JButton resizeButton = new JButton("Move Fourier Up");
        resizeButton.addActionListener(e -> {
            drawPanel3.setBounds(0, 530, 900, 400);
            drawPanel3.revalidate();
            drawPanel3.repaint();
            drawPanel2.setBounds(0,0,0,0);
        });

        JButton resizeButton2 = new JButton("Move Fourier Down");
        resizeButton2.addActionListener(e -> {
            drawPanel3.setBounds(0, 820, 900, 400);
            drawPanel3.revalidate();
            drawPanel3.repaint();
            drawPanel2.setBounds(0, 520, 300, 300);
        });


        Dimension sizeButton2 = resizeButton.getPreferredSize();
        Dimension sizeButton3 = resizeButton2.getPreferredSize();
        resizeButton2.setBounds(340, 150, sizeButton3.width, sizeButton3.height);
        resizeButton.setBounds(170, 150, sizeButton2.width, sizeButton2.height);
        add(resizeButton);
        add(resizeButton2);

        add(drawPanel3);

        //label = new JLabel("This is a label");
        //setLayout(new FlowLayout());
        setSize(900,1200);
        // set place of the window to the center of the screen
        setLocationRelativeTo(null);
        setTitle("First Component");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //add(label);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            // Check if text is a number
            try {
                quality = Integer.parseInt(qualityTextField.getText());
                height = Integer.parseInt(heightTextField.getText());
                delay = Integer.parseInt(speedTextField.getText());
                scale = Double.parseDouble(scaleTextField.getText());
                freq = Double.parseDouble(freqTextField.getText());
                fourierQuality = Integer.parseInt(fourierQualityTextField.getText());
            } catch (NumberFormatException ex) {
                return;
            }
            // Get text field and set it to what was written
            qualityText.setText(qualityTextField.getText());
            heightText.setText(heightTextField.getText());
            speedText.setText(speedTextField.getText() + "ms");
            scaleText.setText(scaleTextField.getText());
            freqText.setText(freqTextField.getText() + "Hz");
            fourierQualityText.setText(fourierQualityTextField.getText());

            repaint();

            // Start or stop animation
            animator = new Timer(delay, this);

            if (delay < 1) {
                animator.stop();
            } else {
                animator.start();
            }
        }
        if (e.getSource() == animator) {
            currentFrame += (int) (1 + scale);
            if (currentFrame >= totalFrames*scale) {
                currentFrame = 0;
            }
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Print test
        //System.out.println("Key typed");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Print test
        //System.out.println("Key pressed");
        // If enter pressed call update
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, null));
        }
        // If ctrl is pressed save info for mouse wheel
        if (e.isControlDown()) {
            ctrlPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Print test
        //System.out.println("Key released");

        // If ctrl is released save info for mouse wheel
        if (!e.isControlDown()) {
            ctrlPressed = false;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // Print test
        //System.out.println("Mouse wheel moved");

        // Get the number of notches the mouse wheel was rotated
        int notches = e.getWheelRotation();
        
        if (!ctrlPressed) {
            // Move audio sample
            if (notches > 0) {
                currentFrame += 25 * scale;
            } else {
                currentFrame -= 25 * scale;
                if (currentFrame < 0) {
                    currentFrame = 0;
                }
            }
        } else {
            // scale
            if (notches > 0) {
                scale *= 1.1;
            } else {
                scale /= 1.1;
            }
        }

        // Set scale to integer if higher than 10
        if (scale > 10) {
            scale = (int) scale;
        }
        // If scale is higher than 1 round numbers off to 2 decimals
        if (scale > 1) {
            scale = Math.round(scale*100.0)/100.0;
        } else {
            scale = Math.round(scale*1000.0)/1000.0;
        }

        scaleTextField.setText("" + scale);
        scaleText.setText("" + scale);
        repaint();
    }


    public static Complex computeDFT(int[] x, int k, int N, int currentSample) {
        double real = 0;
        double imag = 0;
    
        // Ensure we don't go beyond the array length
        int endSample = Math.min(currentSample + N, x.length);
    
        for (int n = currentSample; n < endSample; n++) {
            double angle = -2 * Math.PI * k * (n - currentSample) / N; // Adjusted index
            real += x[n] * Math.cos(angle);
            imag += x[n] * Math.sin(angle);
        }
        
        return new Complex(real, imag);
    }

    // getResolution
    public int getResolution() {
        return 1024*fourierQuality;
    }
}


// Helper class to represent complex numbers
class Complex {
    private final double re;  // Real part
    private final double im;  // Imaginary part

    public Complex(double real, double imag) {
        this.re = real;
        this.im = imag;
    }

    public double magnitude() {
        return Math.sqrt(re * re + im * im);
    }

    public double phase() {
        return Math.atan2(im, re);
    }
}