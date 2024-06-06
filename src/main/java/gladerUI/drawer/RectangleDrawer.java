package gladerUI.drawer;

import java.awt.*;

public class RectangleDrawer extends ShapeDrawer {

    @Override
    public void finishDrawing(int x, int y, Graphics2D g2d) {
        if (drawing) {
            int leftX = Math.min(startX, x);
            int topY = Math.min(startY, y);
            int width = Math.abs(x - startX);
            int height = Math.abs(y - startY);

            g2d.setColor(color); // 设置线条颜色为当前选择的颜色
            g2d.setStroke(new BasicStroke(strokeWidth)); // 设置线条粗细
            g2d.drawRect(leftX, topY, width, height);
            drawing = false;
        }
    }

    @Override
    public void drawPreview(Graphics2D g2d) {
        if (drawing) {
            int leftX = Math.min(startX, endX);
            int topY = Math.min(startY, endY);
            int width = Math.abs(endX - startX);
            int height = Math.abs(endY - startY);

            g2d.setColor(color); // 设置线条颜色为当前选择的颜色
            g2d.setStroke(new BasicStroke(strokeWidth)); // 设置线条粗细
            g2d.drawRect(leftX, topY, width, height);
        }
    }
}
