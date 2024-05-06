package mainwindow;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DrawingPanel extends JPanel {
    private int prevX, prevY, currX, currY;
    private Image bufferImage;
    private Graphics2D bufferGraphics;

    public DrawingPanel() {
        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                currX = e.getX();
                currY = e.getY();

                // 在缓冲图像上绘制线条
                bufferGraphics.setColor(ColorSelectJPanel.getCurrentSelectedColor());
                bufferGraphics.drawLine(prevX, prevY, currX, currY);

                prevX = currX;
                prevY = currY;

                // 重新绘制面板
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 创建缓冲图像
        if (bufferImage == null) {
            bufferImage = createImage(getWidth(), getHeight());
            bufferGraphics = (Graphics2D) bufferImage.getGraphics();
            bufferGraphics.setColor(Color.WHITE);
            bufferGraphics.fillRect(0, 0, getWidth(), getHeight());
        }

        // 将缓冲图像绘制到面板上
        g.drawImage(bufferImage, 0, 0, null);
    }
}
