package javagames.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInput implements KeyListener {
    public synchronized boolean isPressed(int keyCode) {
        return polled[keyCode] > 0;
    }

    public synchronized boolean isPressedOnce(int keyCode) {
        return polled[keyCode] == 1;
    }

    private boolean[] keys = new boolean[256];

    // to differentiate between A and AAAAAAAAAAAAAAAA
    private int[] polled = new int[256];

    public synchronized void poll() {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i]) {
                polled[i]++;
                System.out.println(i);
            } else {
                polled[i] = 0;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode <= keys.length) {
            // System.out.println(keyCode);
            keys[keyCode] = true;
        }
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode <= keys.length) {
            keys[keyCode] = false;
        }
    }
}
