import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class second extends JFrame implements ActionListener {
    
    private static JLabel label;
    private static JTextField canChange;
    private static JTextField cannotChange;
    private JButton button;

    public second() {
        cannotChange = new JTextField("Empty");
        canChange = new JTextField("Write Hello");
        button = new JButton("Click me");
        setLayout(null);
        Dimension size1 = canChange.getPreferredSize();
        Dimension size2 = cannotChange.getPreferredSize();
        Dimension size3 = button.getPreferredSize();
        cannotChange.setEditable(false);
        canChange.setBounds(20, 50, 200, size1.height);
        cannotChange.setBounds(20, 100, 200, size2.height);
        button.setBounds(20, 150, size3.width, size3.height);
        button.addActionListener(this);
        add(canChange);
        add(cannotChange);
        add(button);

        //label = new JLabel("This is a label");
        //setLayout(new FlowLayout());
        setSize(350,300);
        // set place of the window to the center of the screen
        setLocationRelativeTo(null);
        setTitle("First Component");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //add(label);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            // Get text field and set it to what was written
            cannotChange.setText(canChange.getText());
        }
    }
}