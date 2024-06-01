package test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TestIcon extends JFrame {
    public TestIcon() {
        super("test");

        // 创建标签和面板
        JLabel jl = new JLabel();
        JLabel jl2 = new JLabel();
        JPanel jp1 = new JPanel();
        JPanel jp2 = new JPanel();

        // 设置图标
        jl.setIcon(new ImageIcon(loadAndScaleImage("image/放大镜.png", 1.0)));
        jl2.setIcon(new ImageIcon(loadAndScaleImage("image/颜色提取.png", 1.25)));

        // 添加标签到面板
        jp1.add(jl);
        jp2.add(jl2);

        // 设置面板的缩放比例
        setPanelScale(jp1, 1.0);  // 100% 缩放
        setPanelScale(jp2, 1.25); // 125% 缩放

        // 设置窗口属性
        setSize(600, 400);
        setLayout(new FlowLayout());
        this.add(jp1);
        this.add(jp2);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private Image loadAndScaleImage(String imagePath, double scaleFactor) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            int width = (int) (originalImage.getWidth() * scaleFactor);
            int height = (int) (originalImage.getHeight() * scaleFactor);
            BufferedImage scaledImage = new BufferedImage(width, height, originalImage.getType());
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(originalImage, 0, 0, width, height, null);
            g2d.dispose();
            return scaledImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setPanelScale(JPanel panel, double scaleFactor) {
        // 调整面板内所有组件的大小和字体
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                Font font = label.getFont();
                label.setFont(font.deriveFont((float) (font.getSize() * scaleFactor)));
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    public static void main(String[] args) {
        // 设置全局 UI 缩放比例
        System.setProperty("sun.java2d.uiScale", "1.0");
        // 启用高DPI感知
        System.setProperty("sun.java2d.dpiaware", "true");
        System.setProperty("sun.java2d.ddscale", "true");
        new TestIcon();
    }
}
