package gladerUI.drawer;

import java.awt.*;

public class StarDrawer extends ShapeDrawer {

    @Override
    public void finishDrawing(int x, int y, Graphics2D g2d) {
        if (drawing) {
            int[] xPoints = new int[10];
            int[] yPoints = new int[10];
            calculateStarPoints(startX, startY, x, y, xPoints, yPoints);

            g2d.setColor(color); // 设置线条颜色为当前选择的颜色
            g2d.setStroke(new BasicStroke(strokeWidth)); // 设置线条粗细
            g2d.drawPolygon(xPoints, yPoints, 10);
            drawing = false;
        }
    }

    @Override
    public void drawPreview(Graphics2D g2d) {
        if (drawing) {
            int[] xPoints = new int[10];
            int[] yPoints = new int[10];
            calculateStarPoints(startX, startY, endX, endY, xPoints, yPoints);

            g2d.setColor(color); // 设置线条颜色为当前选择的颜色
            g2d.setStroke(new BasicStroke(strokeWidth)); // 设置线条粗细
            g2d.drawPolygon(xPoints, yPoints, 10);
        }
    }

    private void calculateStarPoints(int startX, int startY, int endX, int endY, int[] xPoints, int[] yPoints) {
        double angle = Math.toRadians(36);
        double radius = Math.hypot(endX - startX, endY - startY) / 2;
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;
        double innerRadius = radius * Math.sin(Math.toRadians(18)) / Math.sin(Math.toRadians(54));

        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? radius : innerRadius;
            xPoints[i] = (int) (centerX + r * Math.cos(angle * i - Math.PI / 2));
            yPoints[i] = (int) (centerY + r * Math.sin(angle * i - Math.PI / 2));
        }
    }
}
