package javagames.render;

import javagames.util.SimpleKeyboardInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class SimpleKeyboardExample extends JFrame implements Runnable {
    private volatile boolean running;
    private Thread gameThread;
    private SimpleKeyboardInput keys = new SimpleKeyboardInput();
    private Map<Integer, JButton> directionButtons = new HashMap<>();

    public SimpleKeyboardExample() {
        directionButtons.put(KeyEvent.VK_LEFT, new JButton("Left"));
        directionButtons.put(KeyEvent.VK_RIGHT, new JButton("Right"));
        directionButtons.put(KeyEvent.VK_UP, new JButton("Up"));
        directionButtons.put(KeyEvent.VK_DOWN, new JButton("Down"));

        for (var button: directionButtons.values()) {
            button.setBorder(null);
        }
    }

    public static void main(String[] args) {
        final SimpleKeyboardExample app = new SimpleKeyboardExample();
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
        running = true;

        while (running) {
            gameLoop();
        }
    }

    public void gameLoop() {
        for (var key: directionButtons.keySet()) {
            if (keys.isPressed(key)) {
                directionButtons.get(key).setBorder(BorderFactory.createLoweredBevelBorder());
            } else {
                directionButtons.get(key).setBorder(null);
            }
        }

        sleep(10);
    }

    public void createAndShowGUI() {
        JPanel panel = new JPanel();
        panel.setPreferredSize( new Dimension( 320, 240 ) );;
        panel.setBackground(Color.BLACK);

        for (var button: directionButtons.values()) {
            panel.add(button);
        }
        setFocusable(true);
        requestFocus();
        addKeyListener(keys);
        add(panel);
        setTitle("Simple Keyboard Example");

        // setIgnoreRepaint(true);
        pack();

        setVisible(true);
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