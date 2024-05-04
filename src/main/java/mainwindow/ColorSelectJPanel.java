package mainwindow;

import java.awt.*;
import javax.swing.*;
import mainwindow.CircleButton;

public class ColorSelectJPanel extends JPanel {
    private final Color[] colors = {
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE,
            Color.CYAN, Color.MAGENTA, Color.PINK, Color.LIGHT_GRAY, Color.DARK_GRAY,
            Color.WHITE, Color.BLACK, new Color(255, 150, 0), new Color(0, 255, 150),
            new Color(150, 0, 255), new Color(255, 255, 0), new Color(255, 0, 255),
            new Color(0, 255, 255), new Color(128, 0, 0), new Color(0, 128, 0),
            new Color(0, 0, 128), new Color(128, 128, 0), new Color(128, 0, 128),
            new Color(0, 128, 128), new Color(255, 128, 0), new Color(0, 255, 128),
            new Color(128, 255, 0), new Color(0, 128, 255), new Color(128, 0, 255),
            new Color(255, 0, 128)
    };

    public ColorSelectJPanel() {
        setLayout(null);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 10; j++) {
                CircleButton button = new CircleButton("");
                button.setBackground(colors[i * 10 + j]);
                button.setBounds(j * 25, i * 25, 20, 20);
                add(button);
            }
        }
    }
}
