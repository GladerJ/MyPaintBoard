package gladerUI.drawer;

import java.awt.*;

public class EllipseDrawer extends ShapeDrawer {

    @Override
    public void finishDrawing(int x, int y, Graphics2D g2d) {
        if (drawing) {
            int width = Math.abs(x - startX);
            int height = Math.abs(y - startY);
            int topLeftX = Math.min(startX, x);
            int topLeftY = Math.min(startY, y);

            g2d.setColor(color); // 设置线条颜色为当前选择的颜色
            g2d.setStroke(new BasicStroke(strokeWidth)); // 设置线条粗细
            g2d.drawOval(topLeftX, topLeftY, width, height);
            drawing = false;
        }
    }

    @Override
    public void drawPreview(Graphics2D g2d) {
        if (drawing) {
            int width = Math.abs(endX - startX);
            int height = Math.abs(endY - startY);
            int topLeftX = Math.min(startX, endX);
            int topLeftY = Math.min(startY, endY);

            g2d.setColor(color); // 设置线条颜色为当前选择的颜色
            g2d.setStroke(new BasicStroke(strokeWidth)); // 设置线条粗细
            g2d.drawOval(topLeftX, topLeftY, width, height);
        }
    }
}
