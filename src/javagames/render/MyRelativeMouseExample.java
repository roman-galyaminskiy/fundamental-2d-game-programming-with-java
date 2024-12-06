package javagames.render;

import javagames.util.FrameRate;
import javagames.util.MouseInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.logging.Logger;

import static javagames.util.MouseInput.MOUSE_BUTTON_NAMES;
import static javagames.util.MouseInput.MOUSE_LEFT_BUTTON;

public class MyRelativeMouseExample extends JFrame implements Runnable {
    private static final Logger logger = Logger.getLogger("javagames.render.RelativeMouseMovementExample");
    private volatile boolean running;
    private BufferStrategy bs;
    private Thread gameThread;
    private Robot robot;
    private final FrameRate frameRate = new FrameRate();
    private MouseInput mouseInput = new MouseInput();
    private ArrayList<Point> currentLine = new ArrayList<>();
    private ArrayList<ArrayList<Point>> lines = new ArrayList<>();
    private String mouseButtonPressInfo = "Mouse buttons pressed: ";

    public MyRelativeMouseExample() {
        try {
            robot = new Robot();
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        final MyRelativeMouseExample app = new MyRelativeMouseExample();
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
    private boolean isDrawing = false;

    @Override
    public void run() {
        frameRate.initialize();
        running = true;

        while (running) {
            processInput();
            centerMouseCursor();
            renderFrame();
            sleep(10L);
        }
    }

    private Point getComponentCenter() {
        return new Point(this.getWidth()/2, this.getHeight());
    }

    private void centerMouseCursor() {
        Point center = getComponentCenter();
        SwingUtilities.convertPointToScreen( center, this );
        robot.mouseMove(center.x, center.y);
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

        if (mouseInput.isPressedOnce(MOUSE_LEFT_BUTTON)) {
            isDrawing = true;
            System.out.println(mouseInput.getDragPosition());
        }

        if (mouseInput.isPressed(MOUSE_LEFT_BUTTON)) {
            Point position = mouseInput.getDragPosition();
            if (position != null) {
                if (currentLine.isEmpty() || !position.equals(currentLine.getLast())) {
                    currentLine.add(mouseInput.getDragPosition());
                }
            }
        } else {
            if (isDrawing) {
                lines.add(currentLine);
                currentLine = new ArrayList<>();
                lastDrawnPointIndex = 0;
                // System.out.println("stop");
                // System.out.println(lines.size());
                // System.out.println(currentLine);
                isDrawing = false;
            }
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

        for (var line : lines) {
            for (int i = 0; i < line.size() - 1; i++) {
                g.drawLine(line.get(i).x, line.get(i).y, line.get(i + 1).x, line.get(i + 1).y);
            }
        }

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
