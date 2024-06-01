package mainwindow;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Test {

    public static void main(String[] args) {
        // 创建并设置窗口
        JFrame frame = new JFrame("Test Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 200);
        frame.setLayout(new FlowLayout());

        // 创建按钮并设置尺寸
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(22, 22));

        // 读取并设置按钮图标
        try {
            BufferedImage img = ImageIO.read(new File("image//放大镜.png"));
            button.setIcon(new ImageIcon(img));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 将按钮添加到窗口
        frame.add(button);

        // 显示窗口
        frame.setVisible(true);
    }
}
