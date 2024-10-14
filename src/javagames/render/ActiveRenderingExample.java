package javagames.render;

import javagames.util.FrameRate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class ActiveRenderingExample extends JFrame implements Runnable {
    private final FrameRate frameRate = new FrameRate();
    private BufferStrategy bs;
    private volatile boolean running;
    private Thread gameThread;

    public static void main(String[] args) {
        final ActiveRenderingExample app = new ActiveRenderingExample();
        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.windowClosing();
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
        Canvas canvas = new Canvas();
        canvas.setSize(320, 240);
        canvas.setBackground(Color.BLACK);
        canvas.setIgnoreRepaint(true);
        add(canvas);
        setTitle("Active Rendering");
        setIgnoreRepaint(true);
        pack();

        setSize(320, 240);
        setTitle("Render Thread");
        setVisible(true);
        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void windowClosing() {
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
