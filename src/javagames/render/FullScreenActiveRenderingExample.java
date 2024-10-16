package javagames.render;

import javagames.util.FrameRate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;


public class FullScreenActiveRenderingExample extends JFrame implements Runnable {
    private static final int DEFAULT_DISPLAY_MODE_INDEX = 0;

    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private final GraphicsDevice device = ge.getDefaultScreenDevice();

    private final FrameRate frameRate = new FrameRate();
    private BufferStrategy bs;
    private volatile boolean running;
    private Thread gameThread;

    public static void main(String[] args) {
        final FullScreenActiveRenderingExample app = new FullScreenActiveRenderingExample();
        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.close();
            }
        });
        SwingUtilities.invokeLater(app::createAndShowGUI);
    }

    @Override
    public void run() {
        frameRate.initialize();
        running = true;

        while (running) {
            gameLoop();
        }
    }

    public void gameLoop() {
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.clearRect(0, 0, getWidth(), getHeight());
                    render(g);
                    sleep(10);
                } finally {
                    if (g != null) {
                        g.dispose();
                    }
                }
            } while (bs.contentsRestored());
            bs.show();
        } while (bs.contentsLost());
    }

    private void render(Graphics g) {
        frameRate.calculate();
        g.setColor(Color.GREEN);
        g.drawString(frameRate.getFrameRate(), 30, 30);
    }

    public void createAndShowGUI() {
        setUndecorated(true);
        setBackground(Color.BLACK);

        if (device.isFullScreenSupported()) {
            device.setFullScreenWindow(this);
            try {
                device.setDisplayMode(device.getDisplayModes()[DEFAULT_DISPLAY_MODE_INDEX]);
            } catch (RuntimeException ex) {}
        } else {
            throw new IllegalStateException("Full screen is not supported");
        }
        createBufferStrategy( 2 );
        bs = getBufferStrategy();

        setIgnoreRepaint(true);
        setTitle("Full screen example");
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    close();
                }
            }
        });
        setVisible(true);
        gameThread = new Thread(this);
        gameThread.start();

    }

    public void close() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

    private void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException ex) {
        }
    }
}
