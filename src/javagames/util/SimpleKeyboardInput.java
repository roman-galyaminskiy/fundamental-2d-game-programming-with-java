package javagames.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SimpleKeyboardInput implements KeyListener {
    public synchronized boolean isPressed(int keyCode) {
        return keys[keyCode];
    }

    private boolean[] keys = new boolean[256];

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode <= keys.length) {
            // System.out.printf("Button %s pressed%n", keyCode);
            keys[keyCode] = true;
        }
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode <= keys.length) {
            // System.out.printf("Button %s released%n", keyCode);
            keys[keyCode] = false;
        }
    }
}
