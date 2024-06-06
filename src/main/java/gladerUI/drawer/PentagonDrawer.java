package gladerUI.drawer;

import java.awt.*;

public class PentagonDrawer extends ShapeDrawer {

    @Override
    public void finishDrawing(int x, int y, Graphics2D g2d) {
        if (drawing) {
            int[] xPoints = new int[5];
            int[] yPoints = new int[5];
            calculatePentagonPoints(startX, startY, x, y, xPoints, yPoints);

            g2d.setColor(color); // 设置线条颜色为当前选择的颜色
            g2d.setStroke(new BasicStroke(strokeWidth)); // 设置线条粗细
            g2d.drawPolygon(xPoints, yPoints, 5);
            drawing = false;
        }
    }

    @Override
    public void drawPreview(Graphics2D g2d) {
        if (drawing) {
            int[] xPoints = new int[5];
            int[] yPoints = new int[5];
            calculatePentagonPoints(startX, startY, endX, endY, xPoints, yPoints);

            g2d.setColor(color); // 设置线条颜色为当前选择的颜色
            g2d.setStroke(new BasicStroke(strokeWidth)); // 设置线条粗细
            g2d.drawPolygon(xPoints, yPoints, 5);
        }
    }

    private void calculatePentagonPoints(int startX, int startY, int endX, int endY, int[] xPoints, int[] yPoints) {
        double angle = Math.toRadians(72);
        double radius = Math.hypot(endX - startX, endY - startY) / 2;
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;

        for (int i = 0; i < 5; i++) {
            xPoints[i] = (int) (centerX + radius * Math.cos(angle * i - Math.PI / 2));
            yPoints[i] = (int) (centerY + radius * Math.sin(angle * i - Math.PI / 2));
        }
    }
}
