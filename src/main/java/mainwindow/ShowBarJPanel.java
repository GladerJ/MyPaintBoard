package mainwindow;

import javax.swing.*;
import java.awt.*;

public class ShowBarJPanel extends JPanel {
    private static JLabel tip1, tip2;

    public static void setPos(int x, int y) {
        tip1.setText("当前位置:" + x + "," + y);
    }

    public static void setProportion(int proportion) {
        tip2.setText("当前比例:" + proportion + "%");
    }

    public ShowBarJPanel() {
        tip1 = new JLabel();
        tip2 = new JLabel();
        setPos(0, 0);
        setProportion(100);
        setLayout(null);
        tip1.setBounds(10, 10, MainWindow.getWindowWidth() / 2, 40);
        tip2.setBounds(10 + MainWindow.getWindowWidth() / 2 + 10, 10, MainWindow.getWindowWidth() / 2, 40);
        add(tip1);
        add(tip2);
    }
}
