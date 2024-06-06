package gladerUI.drawer;

import java.awt.*;

public class LineDrawer extends ShapeDrawer {

    @Override
    public void finishDrawing(int x, int y, Graphics2D g2d) {
        if (drawing) {
            g2d.setColor(color); // 设置线条颜色为当前选择的颜色
            g2d.setStroke(new BasicStroke(strokeWidth)); // 设置线条粗细
            g2d.drawLine(startX, startY, x, y);
            drawing = false;
        }
    }

    @Override
    public void drawPreview(Graphics2D g2d) {
        if (drawing) {
            g2d.setColor(color); // 设置线条颜色为当前选择的颜色
            g2d.setStroke(new BasicStroke(strokeWidth)); // 设置线条粗细
            g2d.drawLine(startX, startY, endX, endY);
        }
    }
}
