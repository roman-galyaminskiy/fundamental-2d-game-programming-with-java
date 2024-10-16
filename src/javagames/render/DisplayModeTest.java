package javagames.render;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Supplier;

public class DisplayModeTest extends JFrame {
    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private final GraphicsDevice device = ge.getDefaultScreenDevice();
    private final DisplayMode mode = device.getDisplayMode();
    private final DisplayMode[] displayModes = new DisplayMode[]{
            new DisplayMode(640, 480, 32, 59),
            new DisplayMode(640, 480, 32, 60),
            new DisplayMode(640, 480, 32, 75),
            new DisplayMode(640, 480, 32, 59),
            new DisplayMode(640, 480, 32, 60),
            new DisplayMode(1920, 1080, 32, 60),
    };

    public static void main(String[] args) {
        DisplayModeTest app = new DisplayModeTest();
        // # proper exit on window close
        // app.addWindowListener(new WindowAdapter() {
        //     @Override
        //     public void windowClosing(WindowEvent e) {
        //         app.windowClosing();
        //     }
        // });
        SwingUtilities.invokeLater(app::createAndShowGUI);
    }

    public JButton buildButton(String label, Supplier<Boolean> isEnabled, MouseAdapter mouseAdapter) {
        JButton component = new JButton(label);

        component.addMouseListener(mouseAdapter);
        component.setEnabled(isEnabled());
        return component;
    }

    public JComponent buildComboBox() {
        int index = -1;

        for (int i = 0; i < displayModes.length; i++) {
            if (displayModes[i].equals(mode)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return new JLabel(String.format("mode %s is not available", mode));
        }

        JComboBox component = new JComboBox(displayModes);
        component.setSelectedIndex(index);
        component.addActionListener((ActionEvent e) -> {
            JComboBox source = (JComboBox) e.getSource();
            source.getSelectedIndex();
        });
        return component;
    }

    public void createAndShowGUI() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 50));
        // panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        panel.add(buildComboBox());
        panel.add(buildButton("Enter Full Screen", device::isFullScreenSupported, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                device.setFullScreenWindow(SwingUtilities.windowForComponent(panel));
                // finally {
                //     device.setFullScreenWindow(null);
                // }
            }
        }));
        panel.add(buildButton("Exit Full Screen", device::isFullScreenSupported, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                device.setFullScreenWindow(null);
            }
        }));
        add(panel);
        // setTitle("DisplayModeTest");
        pack();
        setVisible(true);
    }
}
