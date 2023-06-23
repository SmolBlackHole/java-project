package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JTextField tTest;
    private JTextField tTest2;
    private JButton btnOK;
    private JButton btnClear;
    private JLabel lbTest;
    private JPanel mainPanel;

    public MainFrame() {
        setContentPane(mainPanel);
        setTitle("Test Titel");
        setSize(450, 300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String test1 = tTest.getText();
                String test2 = tTest2.getText();
                lbTest.setText("Test Nachricht: " + test1 + " " + test2);
            }
        });
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tTest.setText("");
                tTest2.setText("");
                lbTest.setText("");
            }
        });
    }

    public static void main(String[] args){
        MainFrame myFrame = new MainFrame();

    }
}
