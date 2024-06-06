package gladerUI.drawer;

import java.awt.*;

public class TriangleDrawer extends ShapeDrawer {

    @Override
    public void finishDrawing(int x, int y, Graphics2D g2d) {
        if (drawing) {
            int leftX = Math.min(startX, x);
            int rightX = Math.max(startX, x);

            int[] xPoints = {startX, leftX, rightX};
            int[] yPoints = {startY, y, y};
            g2d.setColor(color); // 设置线条颜色为当前选择的颜色
            g2d.setStroke(new BasicStroke(strokeWidth)); // 设置线条粗细
            g2d.drawPolygon(xPoints, yPoints, 3);
            drawing = false;
        }
    }

    @Override
    public void drawPreview(Graphics2D g2d) {
        if (drawing) {
            int leftX = Math.min(startX, endX);
            int rightX = Math.max(startX, endX);

            int[] xPoints = {startX, leftX, rightX};
            int[] yPoints = {startY, endY, endY};
            g2d.setColor(color); // 设置线条颜色为当前选择的颜色
            g2d.setStroke(new BasicStroke(strokeWidth)); // 设置线条粗细
            g2d.drawPolygon(xPoints, yPoints, 3);
        }
    }
}
