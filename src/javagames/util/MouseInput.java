package javagames.util;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;

public class MouseInput extends MouseInputAdapter {
    public static final String[] MOUSE_BUTTON_NAMES = {"LEFT", "MIDDLE", "RIGHT"};
    public static final int MOUSE_LEFT_BUTTON = 0;
    public static final int MOUSE_MIDDLE_BUTTON = 1;
    public static final int MOUSE_RIGHT_BUTTON = 2;

    public synchronized boolean isPressed(int buttonIndex) {
        return keys[buttonIndex];
    }

    public synchronized Point getDragPosition() {
        return lastPolledDragPosition;
    }

    public synchronized int getRotations() {
        return lastPolledRotations;
    }

    private boolean[] keys = new boolean[MOUSE_BUTTON_NAMES.length];
    private int rotations;
    private Point dragPosition = null;

    private int lastPolledRotations;
    private Point lastPolledDragPosition = null;

    public synchronized void poll() {
        lastPolledRotations = rotations;
        rotations = 0;
        if (dragPosition != null) {
            lastPolledDragPosition = new Point(dragPosition);
        }
    }

    @Override
    public synchronized void mousePressed(MouseEvent e) {
        int button = e.getButton();
        if (button - 1 < MOUSE_BUTTON_NAMES.length) {
            keys[button - 1] = true;
        }
        // System.out.printf("mousePressed position: %s button: %s%n", e.getPoint(), e.getButton());
    }

    @Override
    public synchronized void mouseReleased(MouseEvent e) {
        int button = e.getButton();
        if (button - 1 < MOUSE_BUTTON_NAMES.length) {
            keys[button - 1] = false;
        }
        dragPosition = e.getPoint();
        // System.out.printf("mouseReleased position: %s button: %s%n", e.getPoint(), e.getButton());
    }

    @Override
    public synchronized void mouseWheelMoved(MouseWheelEvent e) {
        rotations += e.getWheelRotation();
        // System.out.printf("mouseWheelMoved rotation: %s%n", e.getWheelRotation());
    }

    @Override
    public synchronized void mouseDragged(MouseEvent e) {
        dragPosition = e.getPoint();
        // System.out.printf("mouseDragged position: %s button: %s%n", e.getPoint(), e.getButton());
    }
}
