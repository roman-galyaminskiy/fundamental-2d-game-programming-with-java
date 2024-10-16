package javagames.render;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;

public class DisplayModeTest extends JFrame {
    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private final GraphicsDevice device = ge.getDefaultScreenDevice();
    private int displayModeIndex = 0;

    public static void main(String[] args) {
        DisplayModeTest app = new DisplayModeTest();
        SwingUtilities.invokeLater(app::createAndShowGUI);
    }

    public JButton buildButton(String label, Supplier<Boolean> isEnabled, MouseAdapter mouseAdapter) {
        JButton component = new JButton(label);

        component.addMouseListener(mouseAdapter);
        component.setEnabled(isEnabled());
        return component;
    }

    public JComponent buildComboBox() {
        JComboBox component = new JComboBox(device.getDisplayModes());
        component.setSelectedIndex(displayModeIndex);
        component.addActionListener((ActionEvent e) -> {
            JComboBox source = (JComboBox) e.getSource();
            displayModeIndex = source.getSelectedIndex();
        });
        return component;
    }

    public void createAndShowGUI() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 50));
        panel.setBackground(Color.WHITE);
        panel.add(buildComboBox());
        panel.add(buildButton("Enter Full Screen", device::isFullScreenSupported, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    device.setDisplayMode(device.getDisplayModes()[displayModeIndex]);
                } catch (UnsupportedOperationException ex) {}
                device.setFullScreenWindow(SwingUtilities.windowForComponent(panel));
            }
        }));
        panel.add(buildButton("Exit Full Screen", device::isFullScreenSupported, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    device.setDisplayMode(device.getDisplayModes()[displayModeIndex]);
                } catch (UnsupportedOperationException ex) {}
                device.setFullScreenWindow(null);
            }
        }));
        add(panel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
