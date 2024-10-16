package javagames.render;

import javagames.util.FrameRate;

import javax.swing.*;
import java.awt.*;

public class PassiveRenderingExample extends JFrame {
    private final FrameRate frameRate;

    public PassiveRenderingExample() {
        frameRate = new FrameRate();
    }

    protected void createAndShowGUI() {
        GamePanel gamePanel = new GamePanel();
        gamePanel.setBackground( Color.BLACK );
        gamePanel.setPreferredSize( new Dimension( 320, 240 ) );
        add( gamePanel );
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setTitle( "Hello World!" );
        pack();
        frameRate.initialize();
        setVisible( true );
    }

    private class GamePanel extends JPanel {
        public void paint(Graphics g) {
            super.paint(g);
            frameRate.calculate();
            g.setColor(Color.WHITE);
            g.drawString(frameRate.getFrameRate(), 30, 30);
            repaint();
        }
    }

    public static void main( String[] args ) {
        final PassiveRenderingExample app = new PassiveRenderingExample();
        SwingUtilities.invokeLater(app::createAndShowGUI);
    }
}
