package handler;

import mainwindow.ColorSelectJPanel;
import mainwindow.DrawingPanel;
import mainwindow.ShapePanel;
import mainwindow.ToolPanel;
import utils.FillAlgorithm;

import javax.swing.*;
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

            if(unscaledPoint.x < 0 || unscaledPoint.x >= drawingPanel.getPanelWidth()
            || unscaledPoint.y < 0 || unscaledPoint.y >= drawingPanel.getPanelHeight()){
                return;
            }

            String currentTool = ToolPanel.getCurrentTool();

            if ("填充".equals(currentTool)) {
                drawingPanel.saveStateToUndoStack();
                BufferedImage bufferImage = drawingPanel.getBufferImage();
                Color targetColor = new Color(bufferImage.getRGB(drawingPanel.getPrevX(), drawingPanel.getPrevY()));
                Color fillColor = ColorSelectJPanel.getCurrentSelectedColor();
                FillAlgorithm.fill(bufferImage, drawingPanel.getPrevX(), drawingPanel.getPrevY(), targetColor, fillColor);
                drawingPanel.repaint();
            } else if ("颜色提取".equals(currentTool)) {
                Color pickedColor = drawingPanel.getColorAtPoint(unscaledPoint);
                ColorSelectJPanel.setCurrentSelectedColor(pickedColor);
            } else if ("文本".equals(currentTool)) {
                drawingPanel.createDraggedTextArea(unscaledPoint.x, unscaledPoint.y);

            } else if ("放大镜".equals(currentTool)) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    DrawingPanel.getZoomHandler().zoomIn(1.1);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    DrawingPanel.getZoomHandler().zoomOut(1.1);
                }
            } else {
                drawingPanel.saveStateToUndoStack(); // 在绘图操作开始前保存状态
                String currentFigure = ShapePanel.getCurrentFigure();
                unscaledPoint = drawingPanel.unscalePoint(e.getPoint());
                if ("直线".equals(currentFigure)) {
                    drawingPanel.startLineDrawing(unscaledPoint.x, unscaledPoint.y);
                } else if ("三角形".equals(currentFigure)) {
                    drawingPanel.startTriangleDrawing(unscaledPoint.x, unscaledPoint.y);
                } else if ("椭圆".equals(currentFigure)) {
                    drawingPanel.startEllipseDrawing(unscaledPoint.x, unscaledPoint.y);
                } else if ("矩形".equals(currentFigure)) {
                    drawingPanel.startRectangleDrawing(unscaledPoint.x, unscaledPoint.y);
                } else if ("五边形".equals(currentFigure)) {
                    drawingPanel.startPentagonDrawing(unscaledPoint.x, unscaledPoint.y);
                } else if ("五角星".equals(currentFigure)) {
                    drawingPanel.startStarDrawing(unscaledPoint.x, unscaledPoint.y);
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (drawingPanel.isDrawingLine()) {
            Point unscaledPoint = drawingPanel.unscalePoint(e.getPoint());
            drawingPanel.finishLineDrawing(unscaledPoint.x, unscaledPoint.y);
          //  drawingPanel.saveStateToUndoStack(); // 保存状态
        } else if (drawingPanel.isDrawingTriangle()) {
            Point unscaledPoint = drawingPanel.unscalePoint(e.getPoint());
            drawingPanel.finishTriangleDrawing(unscaledPoint.x, unscaledPoint.y);
          //  drawingPanel.saveStateToUndoStack(); // 保存状态
        } else if (drawingPanel.isDrawingEllipse()) {
            Point unscaledPoint = drawingPanel.unscalePoint(e.getPoint());
            drawingPanel.finishEllipseDrawing(unscaledPoint.x, unscaledPoint.y);
          //  drawingPanel.saveStateToUndoStack(); // 保存状态
        } else if (drawingPanel.isDrawingRectangle()) {
            Point unscaledPoint = drawingPanel.unscalePoint(e.getPoint());
            drawingPanel.finishRectangleDrawing(unscaledPoint.x, unscaledPoint.y);
          //  drawingPanel.saveStateToUndoStack(); // 保存状态
        } else if (drawingPanel.isDrawingPentagon()) {
            Point unscaledPoint = drawingPanel.unscalePoint(e.getPoint());
            drawingPanel.finishPentagonDrawing(unscaledPoint.x, unscaledPoint.y);
         //   drawingPanel.saveStateToUndoStack(); // 保存状态
        } else if (drawingPanel.isDrawingStar()) {
            Point unscaledPoint = drawingPanel.unscalePoint(e.getPoint());
            drawingPanel.finishStarDrawing(unscaledPoint.x, unscaledPoint.y);
          //  drawingPanel.saveStateToUndoStack(); // 保存状态
        }
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
            } else if ("文本".equals(currentTool)) {
                // 文本工具拖动逻辑（如果有必要）
            } else {
                String currentFigure = ShapePanel.getCurrentFigure();
                if ("直线".equals(currentFigure)) {
                    drawingPanel.previewLineDrawing(drawingPanel.getPrevX(), drawingPanel.getPrevY(), drawingPanel.getCurrX(), drawingPanel.getCurrY());
                } else if ("三角形".equals(currentFigure)) {
                    drawingPanel.previewTriangleDrawing(drawingPanel.getPrevX(), drawingPanel.getPrevY(), drawingPanel.getCurrX(), drawingPanel.getCurrY());
                } else if ("椭圆".equals(currentFigure)) {
                    drawingPanel.previewEllipseDrawing(drawingPanel.getPrevX(), drawingPanel.getPrevY(), drawingPanel.getCurrX(), drawingPanel.getCurrY());
                } else if ("矩形".equals(currentFigure)) {
                    drawingPanel.previewRectangleDrawing(drawingPanel.getPrevX(), drawingPanel.getPrevY(), drawingPanel.getCurrX(), drawingPanel.getCurrY());
                } else if ("五边形".equals(currentFigure)) {
                    drawingPanel.previewPentagonDrawing(drawingPanel.getPrevX(), drawingPanel.getPrevY(), drawingPanel.getCurrX(), drawingPanel.getCurrY());
                } else if ("五角星".equals(currentFigure)) {
                    drawingPanel.previewStarDrawing(drawingPanel.getPrevX(), drawingPanel.getPrevY(), drawingPanel.getCurrX(), drawingPanel.getCurrY());
                }
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
