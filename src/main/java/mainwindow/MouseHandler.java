package mainwindow;

import utils.FillAlgorithm;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class MouseHandler extends MouseAdapter {
    private final DrawingPanel drawingPanel;

    public MouseHandler(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (drawingPanel.isInResizeHandle(e.getPoint())) {
            drawingPanel.setResizing(true);
            drawingPanel.setCursor(drawingPanel.getResizeCursor());
        } else {
            Point unscaledPoint = drawingPanel.unscalePoint(e.getPoint());
            drawingPanel.setPrevX(unscaledPoint.x);
            drawingPanel.setPrevY(unscaledPoint.y);

            String currentTool = ToolPanel.getCurrentTool();

            if ("填充".equals(currentTool)) {
                BufferedImage bufferImage = drawingPanel.getBufferImage();
                Color targetColor = new Color(bufferImage.getRGB(drawingPanel.getPrevX(), drawingPanel.getPrevY()));
                Color fillColor = ColorSelectJPanel.getCurrentSelectedColor();
                FillAlgorithm.fill(bufferImage, drawingPanel.getPrevX(), drawingPanel.getPrevY(), targetColor, fillColor);
                drawingPanel.repaint();
            } else if ("颜色提取".equals(currentTool)) {
                Color pickedColor = drawingPanel.getColorAtPoint(unscaledPoint);
                ColorSelectJPanel.setCurrentSelectedColor(pickedColor);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        drawingPanel.setResizing(false);
        drawingPanel.setCursor(drawingPanel.getDefaultCursor());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (drawingPanel.isResizing()) {
            drawingPanel.resizePanel(e.getPoint());
        } else {
            Point unscalePoint = drawingPanel.unscalePoint(e.getPoint());
            drawingPanel.setCurrX(unscalePoint.x);
            drawingPanel.setCurrY(unscalePoint.y);

            String currentTool = ToolPanel.getCurrentTool();

            BufferedImage bufferImage = drawingPanel.getBufferImage();
            Graphics2D bufferGraphics = drawingPanel.getBufferGraphics();
            bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 启用抗锯齿

            if ("铅笔".equals(currentTool)) {
                bufferGraphics.setColor(ColorSelectJPanel.getCurrentSelectedColor());
                bufferGraphics.setStroke(new BasicStroke(ToolPanel.getToolWidth()));
                bufferGraphics.drawLine(drawingPanel.getPrevX(), drawingPanel.getPrevY(), drawingPanel.getCurrX(), drawingPanel.getCurrY());
            } else if ("橡皮擦".equals(currentTool)) {
                bufferGraphics.setColor(Color.WHITE);
                bufferGraphics.setStroke(new BasicStroke(ToolPanel.getToolWidth()));
                bufferGraphics.drawLine(drawingPanel.getPrevX(), drawingPanel.getPrevY(), drawingPanel.getCurrX(), drawingPanel.getCurrY());
            }

            drawingPanel.setPrevX(drawingPanel.getCurrX());
            drawingPanel.setPrevY(drawingPanel.getCurrY());

            drawingPanel.updateMousePosition(e.getPoint());
            drawingPanel.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (drawingPanel.isInResizeHandle(e.getPoint())) {
            drawingPanel.setCursor(drawingPanel.getResizeCursor());
        } else {
            drawingPanel.setCursor(drawingPanel.getDefaultCursor());
        }
        drawingPanel.updateMousePosition(e.getPoint());
    }
}
