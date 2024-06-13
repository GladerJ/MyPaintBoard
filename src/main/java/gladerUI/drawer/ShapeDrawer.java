package gladerUI.drawer;

import java.awt.*;

public abstract class ShapeDrawer {
    protected boolean drawing = false;
    protected int startX, startY, endX, endY;
    protected Color color;
    protected int strokeWidth;

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public void startDrawing(int x, int y, Color color, int strokeWidth) {
        drawing = true;
        startX = x;
        startY = y;
        endX = x;
        endY = y;
        this.color = color;
        this.strokeWidth = strokeWidth;
    }

    public void previewDrawing(int startX, int startY, int endX, int endY) {
        this.endX = endX;
        this.endY = endY;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public abstract void finishDrawing(int x, int y, Graphics2D g2d);
    public abstract void drawPreview(Graphics2D g2d);
}
