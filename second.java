import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.image.BufferedImage;


public class second extends JFrame implements ActionListener {

    private Timer animator;
    private int delay = 0, totalFrames = 1000, currentFrame = 0;
    private int quality = 1;
    private int height = 200;
    private double scale = 10.0;
    private int sampleRate = 44100;
    
    private static JLabel label;
    private static JTextField qualityTextField;
    private static JTextField qualityText;
    private static JTextField heightTextField;
    private static JTextField heightText;
    private static JTextField speedTextField;
    private static JTextField speedText;
    private static JTextField scaleTextField;
    private static JTextField scaleText;
    private JButton button;

    private BufferedImage buffer;

    public second(int[] samples) {

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
        
        qualityTextField = new JTextField("" + quality);
        qualityText = new JTextField("" + quality);
        heightTextField = new JTextField("" + height);
        heightText = new JTextField("" + height);
        speedTextField = new JTextField("" + delay);
        speedText = new JTextField("" + delay + "ms");
        scaleTextField = new JTextField("" + scale);
        scaleText = new JTextField("" + scale);

        button = new JButton("Update");
        setLayout(null);
        Dimension size1 = qualityTextField.getPreferredSize();
        Dimension size2 = qualityText.getPreferredSize();
        Dimension size3 = heightTextField.getPreferredSize();
        Dimension size4 = heightText.getPreferredSize();
        Dimension size5 = speedTextField.getPreferredSize();
        Dimension size6 = speedText.getPreferredSize();
        Dimension size7 = scaleTextField.getPreferredSize();
        Dimension size8 = scaleText.getPreferredSize();

        Dimension sizeButton = button.getPreferredSize();
        qualityText.setEditable(false);
        heightText.setEditable(false);
        speedText.setEditable(false);
        scaleText.setEditable(false);
        qualityTextField.setBounds(20, 50, 100, size1.height);
        heightTextField.setBounds(140, 50, 100, size3.height);
        qualityText.setBounds(20, 100, 100, size2.height);
        heightText.setBounds(140, 100, 100, size4.height);
        speedTextField.setBounds(260, 50, 100, size5.height);
        speedText.setBounds(260, 100, 100, size6.height);
        scaleTextField.setBounds(380, 50, 100, size7.height);
        scaleText.setBounds(380, 100, 100, size8.height);

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

        add(button);

        // Create panel
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Look at this graph on frame: "));
        topPanel.setBounds(20, 200, 300, 30);

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
                    double ii = (i*scale+currentFrame)/quality;
                    double iii = ((i+1)*scale+currentFrame)/quality;
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

        //label = new JLabel("This is a label");
        //setLayout(new FlowLayout());
        setSize(900,600);
        // set place of the window to the center of the screen
        setLocationRelativeTo(null);
        setTitle("First Component");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //add(label);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            // Check if text is a number
            try {
                quality = Integer.parseInt(qualityTextField.getText());
                height = Integer.parseInt(heightTextField.getText());
                delay = Integer.parseInt(speedTextField.getText());
                scale = Double.parseDouble(scaleTextField.getText());
            } catch (NumberFormatException ex) {
                return;
            }
            // Get text field and set it to what was written
            qualityText.setText(qualityTextField.getText());
            heightText.setText(heightTextField.getText());
            speedText.setText(speedTextField.getText() + "ms");
            scaleText.setText(scaleTextField.getText());

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
}