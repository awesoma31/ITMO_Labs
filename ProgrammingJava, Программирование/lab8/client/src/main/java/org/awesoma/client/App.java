package org.awesoma.client;

import org.awesoma.common.Environment;
import org.awesoma.common.util.CLIArgumentParser;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class App {
    private static final String TITLE = "Awesoma";
    private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private static final int SCREEN_WIDTH = 1000;
    private static final int SCREEN_HEIGHT = 800;

    public static void main(String[] args) {
//        JFrame mainFrame = initRegistrationFrame();
        CLIArgumentParser.parseArgs(args);

        new Client(Environment.HOST, Environment.PORT).run();
    }

    static class RegistrationComponent extends JComponent {
        @Override
        protected void paintComponent(Graphics g2) {
            var fontSize = 40;
            var registrationFont = new Font("Arial", Font.BOLD, fontSize);
            var g = (Graphics2D) g2;

//            var r = new Rectangle2D.Float(
//                    (float) 300,
//                    (float) 0,
//                    (float) SCREEN_WIDTH /3,
//                    (float) SCREEN_HEIGHT /10
//            );
//            g.setPaint(Color.gray);
//            g.fill(r);
//            g.draw(r);

            g.setFont(registrationFont);
            g.setPaint(Color.BLACK);
            g.drawString("Registration", SCREEN_WIDTH/3, SCREEN_HEIGHT/20);
        }
    }

    private static JFrame initRegistrationFrame() {
        JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setLocation(SCREEN_WIDTH/4, 25);
        frame.setVisible(true);
        frame.add(new RegistrationComponent());



        var registerButton = new JButton("Register");


        JPanel panel = new JPanel();
        frame.add(panel);

        panel.add(registerButton);

        return frame;
    }
}
