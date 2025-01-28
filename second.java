import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.geom.Line2D.Double;


public class second extends JFrame implements ActionListener {

    private Timer animator;
    private int delay = 1, totalFrames = 1000, currentFrame = 0;
    private int quality = 1;
    private static int height = 350;
    
    private static JLabel label;
    private static JTextField qualityTextField;
    private static JTextField qualityText;
    private static JTextField heightTextField;
    private static JTextField heightText;
    private JButton button;

    // Array to hold samples:
    private int[] samples = new int[8000];

    public second() {
        // Set samples to 0
        for (int i = 0; i < 8000; i++) {
            samples[i] = 0;
        }

        // Label with text: "quality"
        JLabel qualityLabel = new JLabel("Quality: ");
        qualityLabel.setBounds(20, 20, 100, 30);
        add(qualityLabel);

        // Label with text: "height"
        JLabel heightLabel = new JLabel("Height: ");
        heightLabel.setBounds(240, 20, 100, 30);
        add(heightLabel);
        
        qualityTextField = new JTextField("" + quality);
        qualityText = new JTextField("" + quality);
        heightTextField = new JTextField("" + height);
        heightText = new JTextField("" + height);

        button = new JButton("Update");
        setLayout(null);
        Dimension size1 = qualityTextField.getPreferredSize();
        Dimension size2 = qualityText.getPreferredSize();
        Dimension size3 = heightTextField.getPreferredSize();
        Dimension size4 = heightText.getPreferredSize();
        Dimension size5 = button.getPreferredSize();
        qualityText.setEditable(false);
        heightText.setEditable(false);
        qualityTextField.setBounds(20, 50, 200, size1.height);
        heightTextField.setBounds(240, 50, 200, size3.height);
        qualityText.setBounds(20, 100, 200, size2.height);
        heightText.setBounds(240, 100, 200, size4.height);
        button.setBounds(20, 150, size5.width, size5.height);
        button.addActionListener(this);

        add(qualityTextField);
        add(heightTextField);
        add(qualityText);
        add(heightText);
        add(button);

        // Create panel
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Look at this graph on frame: "));
        topPanel.setBounds(20, 200, 300, 30);

        // Add panel to the frame
        add(topPanel);

        // Add timer
        animator = new Timer(delay, this);
        animator.start();

        // Custom panel to draw a line
        JPanel drawPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            // draw rectangle behind lines 800 wide and 200 tall
            g2.setColor(Color.LIGHT_GRAY);
            g2.fill(new Rectangle2D.Double(0, 0, 800, 200));
            // set thickness of the line
            g2.setStroke(new BasicStroke(2));
            // set color of the line to dark gray
            g2.setColor(Color.DARK_GRAY);
            // draw coordinate lines
            g2.draw(new Line2D.Double(0, 100, 800, 100));
            g2.draw(new Line2D.Double(400, 0, 400, 200));

            // set thickness of the line
            g2.setStroke(new BasicStroke(2));
            // set color of the line to black
            g2.setColor(Color.BLACK);
            // Loop for drawing a sine wave
            for (int i = 0; i < (800/quality); i += quality) {
                // draw a line from the last point to the next point
                int ii = i*quality+currentFrame;
                int iii = (i+quality)*quality+currentFrame;
                //g2.draw(new Line2D.Double(i*quality, 100 + 50 * Math.sin(ii * Math.PI / 180), (i+quality) * quality, 100 + 50 * Math.sin((iii) * Math.PI / 180)));
                
                // Draw samples:
                if (i+quality+currentFrame >= 8000) {
                    break;
                }
                g2.draw(new Line2D.Double(i*quality, 100 + samples[i+currentFrame] / height, (i+quality) * quality, 100 + samples[i+quality+currentFrame] / height));
            }

            // Draw frame number:
            g2.drawString(Integer.toString(currentFrame), 200, 20);

            // draw around rectangle
            g2.setColor(Color.BLACK);
            // set thickness of the line
            g2.setStroke(new BasicStroke(4));
            // lines of box
            g2.draw(new Line2D.Double(0, 0, 800, 0));
            g2.draw(new Line2D.Double(800, 00, 800, 200));
            g2.draw(new Line2D.Double(800, 200, 0, 200));
            g2.draw(new Line2D.Double(0, 200, 0, 0));
            }
        };
        drawPanel.setBounds(25, 250, 800, 200);
        add(drawPanel);

        //label = new JLabel("This is a label");
        //setLayout(new FlowLayout());
        setSize(850,500);
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
            } catch (NumberFormatException ex) {
                return;
            }
            try {
                height = Integer.parseInt(heightTextField.getText());
            } catch (NumberFormatException ex) {
                return;
            }
            // Get text field and set it to what was written
            qualityText.setText(qualityTextField.getText());
            heightText.setText(heightTextField.getText());
        }
        if (e.getSource() == animator) {
            currentFrame++;
            if (currentFrame == totalFrames) {
                currentFrame = 0;
            }
            repaint();
        }
    }

    public void setSamples(int[] samples) {
        this.samples = samples;
    }
}