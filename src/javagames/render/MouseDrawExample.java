package javagames.render;

import javagames.util.FrameRate;
import javagames.util.MouseInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import static javagames.util.MouseInput.MOUSE_BUTTON_NAMES;
import static javagames.util.MouseInput.MOUSE_LEFT_BUTTON;

public class MouseDrawExample extends JFrame implements Runnable {
    private volatile boolean running;
    private BufferStrategy bs;
    private Thread gameThread;
    private final FrameRate frameRate = new FrameRate();
    private MouseInput mouseInput = new MouseInput();
    private java.util.List<Point> currentLine = new ArrayList<>();
    private String mouseButtonPressInfo = "Mouse buttons pressed: ";
    public static void main(String[] args) {
        final MouseDrawExample app = new MouseDrawExample();
        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.windowClosing();
            }
        });
        SwingUtilities.invokeLater(app::createAndShowGUI);
    }
    private boolean prevLeftButtonState = false;
    private int lastDrawnPointIndex = 0;

    @Override
    public void run() {
        frameRate.initialize();
        running = true;

        while (running) {
            processInput();
            renderFrame();
            sleep(10L);
        }
    }

    private void processInput() {
        mouseInput.poll();
        StringBuilder stringBuilder = new StringBuilder("Mouse buttons pressed: ");
        for (int i = 0; i < MOUSE_BUTTON_NAMES.length; i++) {
            if (mouseInput.isPressed(i)) {
                stringBuilder.append(" ");
                stringBuilder.append(MOUSE_BUTTON_NAMES[i]);
            }
        }
        mouseButtonPressInfo = stringBuilder.toString();

        if (mouseInput.isPressed(MOUSE_LEFT_BUTTON) || (!mouseInput.isPressed(MOUSE_LEFT_BUTTON) && prevLeftButtonState)) {
            // System.out.println("test");
            if (mouseInput.getDragPosition() != null) {
                currentLine.add(mouseInput.getDragPosition());
            }
            System.out.println(currentLine.size());
        }

        prevLeftButtonState = mouseInput.isPressed(MOUSE_LEFT_BUTTON);
    }


    private void renderFrame() {
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
        g.drawString(mouseButtonPressInfo, 30, 40);

        // System.out.printf("%s %s%n", currentLine.size(), lastDrawnPointIndex);
        if (currentLine.size() > lastDrawnPointIndex) {
            for (int i = lastDrawnPointIndex; i < currentLine.size() - 1; i++) {
                g.drawLine(currentLine.get(i).x, currentLine.get(i).y, currentLine.get(i + 1).x, currentLine.get(i + 1).y);
            }
        }
    }

    public void createAndShowGUI() {
        Canvas canvas = new Canvas();
        canvas.setSize(320, 240);
        canvas.setBackground(Color.BLACK);
        canvas.setIgnoreRepaint(true);
        canvas.addMouseListener(mouseInput);
        canvas.addMouseWheelListener(mouseInput);
        canvas.addMouseMotionListener(mouseInput);
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
