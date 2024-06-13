package mainwindow;

import gladerUI.DraggedImageJPanel;
import gladerUI.DraggedTextArea;
import gladerUI.drawer.*;
import handler.MouseHandler;
import handler.ZoomHandler;
import utils.ClipboardUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class DrawingPanel extends JPanel {
    private int prevX, prevY, currX, currY;
    private BufferedImage bufferImage;
    private Graphics2D bufferGraphics;
    private static ZoomHandler zoomHandler;
    private int panelWidth;
    private int panelHeight;
    private boolean resizing = false;
    private int resizeHandleSize = 10;
    private Cursor defaultCursor = Cursor.getDefaultCursor();
    private Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
    private DraggedImageJPanel draggedImageJPanel;

    private static DraggedTextArea draggedTextArea;

    public static ZoomHandler getZoomHandler() {
        return zoomHandler;
    }

    public static void setZoomHandler(ZoomHandler zoomHandler) {
        DrawingPanel.zoomHandler = zoomHandler;
    }

    private Point originalTextAreaPosition;

    public ZoomHandler getZoomTool() {
        return zoomHandler;
    }

    public DrawingPanel(int panelWidth, int panelHeight) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.zoomHandler = new ZoomHandler(this);

        setLayout(null);
        setPreferredSize(new Dimension((int) (panelWidth * zoomHandler.getImageScale()), (int) (panelHeight * zoomHandler.getImageScale())));
        setBackground(Color.GRAY);

        MouseHandler mouseHandler = new MouseHandler(this);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown() && !ShapePanel.getCurrentFigure().equals("裁剪")) {
                    if (e.getWheelRotation() < 0) {
                        zoomHandler.zoomIn(1.1);
                    } else {
                        zoomHandler.zoomOut(1.1);
                    }
                }
            }
        });

        // Initialize DraggedTextArea
        draggedTextArea = null;
        originalTextAreaPosition = null;


        addUndoRedoKeyListener();

    }

    public static DraggedTextArea getDraggedTextArea() {
        return draggedTextArea;
    }

    public void textAreaToString() {
        Point textAreaLocation = draggedTextArea.getLocation();
        String text = draggedTextArea.getText();

        double imageScale = zoomHandler.getImageScale();

        int actualX, actualY;

        if (imageScale >= 1.0) {
            // 缩放比例大于或等于100%，使用原始计算方式
            actualX = (int) (textAreaLocation.x / imageScale);
            actualY = (int) (textAreaLocation.y / imageScale);
        } else {
            // 缩放比例小于100%，考虑画板居中情况
            Dimension panelSize = getSize();
            Dimension imageSize = new Dimension((int) (panelSize.width * imageScale), (int) (panelSize.height * imageScale));

            // 计算画板在容器中的偏移量
            int offsetX = (panelSize.width - imageSize.width) / 2;
            int offsetY = (panelSize.height - imageSize.height) / 2;

            // 计算文本框在画板中的实际位置
            actualX = (int) ((textAreaLocation.x - offsetX) / imageScale);
            actualY = (int) ((textAreaLocation.y - offsetY) / imageScale);
        }

        // 将文本绘制到画板上
        float scaledFontSize = (float) (draggedTextArea.getFont().getSize() / imageScale);
        bufferGraphics.setFont(draggedTextArea.getFont().deriveFont(scaledFontSize));
        bufferGraphics.setColor(draggedTextArea.getForeground());

        // 分割文本为多行
        String[] lines = text.split("\n");
        FontMetrics fontMetrics = bufferGraphics.getFontMetrics();
        int lineHeight = fontMetrics.getHeight();

        // 逐行绘制文本
        for (int i = 0; i < lines.length; i++) {
            bufferGraphics.drawString(lines[i], actualX, actualY + fontMetrics.getAscent() + i * lineHeight);
        }

        remove(draggedTextArea);
        draggedTextArea = null;
        revalidate();
        repaint();
    }


    public int getPanelWidth() {
        return panelWidth;
    }

    public int getPanelHeight() {
        return panelHeight;
    }

    public void createDraggedTextArea(int x, int y) {
        // 检查点是否在画板范围内
        if (x < 0 || x >= panelWidth || y < 0 || y >= panelHeight) {
            return; // 如果不在范围内，直接返回
        }

        // 如果存在已经创建的文本框，先将其内容绘制到画板上
        if (draggedTextArea != null) {
            textAreaToString();
            // 移除拖动文本框
            return;
        }

        // 创建新的拖动文本框
        draggedTextArea = new DraggedTextArea(zoomHandler.getImageScale());
        draggedTextArea.setForeground(ColorSelectJPanel.getCurrentSelectedColor());
        draggedTextArea.setLocation((int) (x * zoomHandler.getImageScale()), (int) (y * zoomHandler.getImageScale()));
        add(draggedTextArea);
        revalidate();
        repaint();
        // 保存初始位置
        originalTextAreaPosition = new Point(x, y);
    }

    public boolean isInResizeHandle(Point point) {
        int x = point.x;
        int y = point.y;
        int w = getWidth();
        int h = getHeight();
        int handleSize = resizeHandleSize;

        int imgX = (w - (int) (panelWidth * zoomHandler.getImageScale())) / 2;
        int imgY = (h - (int) (panelHeight * zoomHandler.getImageScale())) / 2;
        int imgWidth = (int) (panelWidth * zoomHandler.getImageScale());
        int imgHeight = (int) (panelHeight * zoomHandler.getImageScale());

        return x > imgX + imgWidth - handleSize && y > imgY + imgHeight - handleSize;
    }

    public void resizePanel(Point point) {
        int newWidth = (int) (point.x / zoomHandler.getImageScale());
        int newHeight = (int) (point.y / zoomHandler.getImageScale());

        newWidth = Math.max(newWidth, 50);
        newHeight = Math.max(newHeight, 50);

        BufferedImage newBufferImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D newBufferGraphics = newBufferImage.createGraphics();
        newBufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 启用抗锯齿
        newBufferGraphics.setColor(Color.WHITE);
        newBufferGraphics.fillRect(0, 0, newWidth, newHeight);
        newBufferGraphics.drawImage(bufferImage, 0, 0, null);

        bufferImage = newBufferImage;
        bufferGraphics = newBufferGraphics;

        panelWidth = newWidth;
        panelHeight = newHeight;

        updatePreferredSize(zoomHandler.getImageScale());
        repaint();
    }

    public Point unscalePoint(Point point) {
        int x = (int) ((point.x - (getWidth() - panelWidth * zoomHandler.getImageScale()) / 2) / zoomHandler.getImageScale());
        int y = (int) ((point.y - (getHeight() - panelHeight * zoomHandler.getImageScale()) / 2) / zoomHandler.getImageScale());
        return new Point(x, y);
    }

    public void updateMousePosition(Point screenPoint) {
        int imgX = (getWidth() - (int) (panelWidth * zoomHandler.getImageScale())) / 2;
        int imgY = (getHeight() - (int) (panelHeight * zoomHandler.getImageScale())) / 2;

        int relativeX = (int) ((screenPoint.x - imgX) / zoomHandler.getImageScale());
        int relativeY = (int) ((screenPoint.y - imgY) / zoomHandler.getImageScale());

        if (relativeX >= 0 && relativeX < panelWidth && relativeY >= 0 && relativeY < panelHeight) {
            ShowBarJPanel.setPos(relativeX, relativeY);
        } else {
            //ShowBarJPanel.clearPos();
        }
    }

    public void updatePreferredSize(double imageScale) {
        setPreferredSize(new Dimension((int) (panelWidth * imageScale), (int) (panelHeight * imageScale)));
        revalidate();
    }

    public void scrollToCenter() {
        JViewport viewport = (JViewport) getParent();
        int x = (getWidth() - viewport.getWidth()) / 2;
        int y = (getHeight() - viewport.getHeight()) / 2;
        viewport.setViewPosition(new Point(x, y));
    }

    public int getPrevX() {
        return prevX;
    }

    public void setPrevX(int prevX) {
        this.prevX = prevX;
    }

    public int getPrevY() {
        return prevY;
    }

    public void setPrevY(int prevY) {
        this.prevY = prevY;
    }

    public int getCurrX() {
        return currX;
    }

    public void setCurrX(int currX) {
        this.currX = currX;
    }

    public int getCurrY() {
        return currY;
    }

    public void setCurrY(int currY) {
        this.currY = currY;
    }

    public BufferedImage getBufferImage() {
        return bufferImage;
    }

    public Graphics2D getBufferGraphics() {
        return bufferGraphics;
    }

    public boolean isResizing() {
        return resizing;
    }

    public void setResizing(boolean resizing) {
        this.resizing = resizing;
    }

    public Cursor getDefaultCursor() {
        return defaultCursor;
    }

    public Cursor getResizeCursor() {
        return resizeCursor;
    }

    public Color getColorAtPoint(Point point) {
        if (bufferImage != null) {
            int x = point.x;
            int y = point.y;
            if (x >= 0 && x < bufferImage.getWidth() && y >= 0 && y < bufferImage.getHeight()) {
                return new Color(bufferImage.getRGB(x, y));
            }
        }
        return Color.WHITE; // 默认返回白色
    }

    // 添加这些字段到 DrawingPanel 类
    // 添加 LineDrawer 实例
    private final LineDrawer lineDrawer = new LineDrawer();

    // 修改 startLineDrawing 方法
    public void startLineDrawing(int x, int y) {
        lineDrawer.startDrawing(x, y, ColorSelectJPanel.getCurrentSelectedColor(), ToolPanel.getToolWidth());
    }

    // 修改 previewLineDrawing 方法
    public void previewLineDrawing(int startX, int startY, int endX, int endY) {
        lineDrawer.previewDrawing(startX, startY, endX, endY);
        repaint();
    }

    // 修改 finishLineDrawing 方法
    public void finishLineDrawing(int x, int y) {
        if (lineDrawer.isDrawing()) {
            Graphics2D g2d = bufferImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 启用抗锯齿
            lineDrawer.finishDrawing(x, y, g2d);
            g2d.dispose();
            repaint();
        }
    }

    // 修改 isDrawingLine 方法
    public boolean isDrawingLine() {
        return lineDrawer.isDrawing();
    }

    // 修改 DrawingPanel 类中的 paintComponent 方法
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bufferImage == null) {
            bufferImage = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
            bufferGraphics = bufferImage.createGraphics();
            bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 启用抗锯齿
            bufferGraphics.setColor(Color.WHITE);
            bufferGraphics.fillRect(0, 0, panelWidth, panelHeight);
        }

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 启用抗锯齿
        int x = (getWidth() - (int) (panelWidth * zoomHandler.getImageScale())) / 2;
        int y = (getHeight() - (int) (panelHeight * zoomHandler.getImageScale())) / 2;
        g2d.translate(x, y);
        g2d.scale(zoomHandler.getImageScale(), zoomHandler.getImageScale());
        g2d.drawImage(bufferImage, 0, 0, null);

        // Draw preview line if drawingLine is true
        if (isDrawingLine()) {
            g2d.setColor(ColorSelectJPanel.getCurrentSelectedColor());
            g2d.setStroke(new BasicStroke(ToolPanel.getToolWidth()));
            lineDrawer.drawPreview(g2d);
        }

        if (isDrawingTriangle()) {
            g2d.setColor(ColorSelectJPanel.getCurrentSelectedColor());
            g2d.setStroke(new BasicStroke(ToolPanel.getToolWidth()));
            triangleDrawer.drawPreview(g2d);
        }

        if (isDrawingEllipse()) {
            g2d.setColor(ColorSelectJPanel.getCurrentSelectedColor());
            g2d.setStroke(new BasicStroke(ToolPanel.getToolWidth()));
            ellipseDrawer.drawPreview(g2d);
        }

        // 绘制矩形预览
        if (isDrawingRectangle()) {
            g2d.setColor(ColorSelectJPanel.getCurrentSelectedColor());
            g2d.setStroke(new BasicStroke(ToolPanel.getToolWidth()));
            rectangleDrawer.drawPreview(g2d);
        }

        // 绘制五边形预览
        if (isDrawingPentagon()) {
            g2d.setColor(ColorSelectJPanel.getCurrentSelectedColor());
            g2d.setStroke(new BasicStroke(ToolPanel.getToolWidth()));
            pentagonDrawer.drawPreview(g2d);
        }

        // 绘制五角星预览
        if (isDrawingStar()) {
            g2d.setColor(ColorSelectJPanel.getCurrentSelectedColor());
            g2d.setStroke(new BasicStroke(ToolPanel.getToolWidth()));
            starDrawer.drawPreview(g2d);
        }

        //绘制选区区域的矩形绘制
        if (selectRectangle.isDrawing()) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            selectRectangle.drawPreview(g2d);
        }

        g2d.dispose();

        // Update DraggedTextArea position and size
        if (draggedTextArea != null && originalTextAreaPosition != null) {
            int newX = (int) (originalTextAreaPosition.x * zoomHandler.getImageScale()) + x;
            int newY = (int) (originalTextAreaPosition.y * zoomHandler.getImageScale()) + y;
            draggedTextArea.setLocation(newX, newY);
            draggedTextArea.adjustSize();
            draggedTextArea.setFont(draggedTextArea.getFont().deriveFont((float) (DraggedTextArea.getOriginalFontSize() * zoomHandler.getImageScale())));
        }

    }


    //绘制三角形
    private final TriangleDrawer triangleDrawer = new TriangleDrawer();

    public boolean isDrawingTriangle() {
        return triangleDrawer.isDrawing();
    }

    public void startTriangleDrawing(int x, int y) {
        triangleDrawer.startDrawing(x, y, ColorSelectJPanel.getCurrentSelectedColor(), ToolPanel.getToolWidth());
    }

    public void finishTriangleDrawing(int x, int y) {
        triangleDrawer.finishDrawing(x, y, bufferGraphics);
        repaint();
    }

    public void previewTriangleDrawing(int startX, int startY, int x, int y) {
        triangleDrawer.previewDrawing(startX, startY, x, y);
        repaint();
    }

    //绘制椭圆
    private final EllipseDrawer ellipseDrawer = new EllipseDrawer();

    public boolean isDrawingEllipse() {
        return ellipseDrawer.isDrawing();
    }

    public void startEllipseDrawing(int x, int y) {
        int width = ToolPanel.getToolWidth();
        ellipseDrawer.startDrawing(x, y, ColorSelectJPanel.getCurrentSelectedColor(), width);
    }

    public void finishEllipseDrawing(int x, int y) {
        ellipseDrawer.finishDrawing(x, y, bufferGraphics);
        repaint();
    }

    public void previewEllipseDrawing(int startX, int startY, int x, int y) {
        ellipseDrawer.previewDrawing(startX, startY, x, y);
        repaint();
    }

    // 绘制矩形
    private final RectangleDrawer rectangleDrawer = new RectangleDrawer();

    public boolean isDrawingRectangle() {
        return rectangleDrawer.isDrawing();
    }


    public void startRectangleDrawing(int x, int y) {
        rectangleDrawer.startDrawing(x, y, ColorSelectJPanel.getCurrentSelectedColor(), ToolPanel.getToolWidth());
    }

    public void finishRectangleDrawing(int x, int y) {
        rectangleDrawer.finishDrawing(x, y, bufferGraphics);
        repaint();
    }

    public void previewRectangleDrawing(int startX, int startY, int x, int y) {
        rectangleDrawer.previewDrawing(startX, startY, x, y);
        repaint();
    }

    //绘制选区区域的矩形对象
    private final RectangleDrawer selectRectangle = new RectangleDrawer();

    public boolean isDrawingSelectRectangle() {
        return selectRectangle.isDrawing();
    }

    public void startSelectRectangleDrawing(int x, int y) {
        selectRectangle.startDrawing(x, y, Color.BLACK, 1);
    }

    // 创建一个选择后的框可用于拖动
    public void createDraggedImageJPanel(int x, int y, int width, int height,BufferedImage image) {
        if (x < 0 || x >= panelWidth || y < 0 || y >= panelHeight) {
            return; // 如果不在范围内，直接返回
        }

        int scale = (int) zoomHandler.getImageScale();
        // 创建一个新的 BufferedImage
        BufferedImage croppedImage = null;
        if(image == null){
            croppedImage = new BufferedImage(width * scale, height * scale, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = croppedImage.createGraphics();
            g2d.drawImage(bufferImage, 0, 0, width * scale, height * scale, x * scale, y * scale, (x + width) * scale, (y + height) * scale, null);
            g2d.dispose();
        }
        else{
            croppedImage = image;
        }
        draggedImageJPanel = new DraggedImageJPanel(croppedImage);
        draggedImageJPanel.setBounds(x * scale, y * scale, width * scale, height * scale);
        add(draggedImageJPanel);

        revalidate();
        repaint();
    }


    public void setDraggedImageJPanel(DraggedImageJPanel draggedImageJPanel) {
        this.draggedImageJPanel = draggedImageJPanel;
    }

    public DraggedImageJPanel getDraggedImageJPanel() {
        return draggedImageJPanel;
    }

    public void finishSelectRectangleDrawing(int x, int y) {
        // selectRectangle.finishDrawing(x, y, bufferGraphics);
        int x1 = selectRectangle.getStartX(), x2 = selectRectangle.getEndX();
        int y1 = selectRectangle.getStartY(), y2 = selectRectangle.getEndY();
        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (y1 > y2) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
        if (x2 - x1 <= 0 || y2 - y1 <= 0) return;
        createDraggedImageJPanel(x1, y1, x2 - x1, y2 - y1,null);
        selectRectangle.reset(); // 重置选区矩形的绘制状态
        OperatorPanel.getConfirm().setEnabled(true);
        clearRectangle(x1, y1, x2 - x1, y2 - y1);
        repaint();
    }

    public void previewSelectRectangleDrawing(int startX, int startY, int x, int y) {
        if (draggedImageJPanel != null) return;
        selectRectangle.previewDrawing(startX, startY, x, y);
        repaint();
    }


    // 绘制五边形
    private final PentagonDrawer pentagonDrawer = new PentagonDrawer();

    public boolean isDrawingPentagon() {
        return pentagonDrawer.isDrawing();
    }

    public void startPentagonDrawing(int x, int y) {
        pentagonDrawer.startDrawing(x, y, ColorSelectJPanel.getCurrentSelectedColor(), ToolPanel.getToolWidth());
    }

    public void finishPentagonDrawing(int x, int y) {
        pentagonDrawer.finishDrawing(x, y, bufferGraphics);
        repaint();
    }

    public void previewPentagonDrawing(int startX, int startY, int x, int y) {
        pentagonDrawer.previewDrawing(startX, startY, x, y);
        repaint();
    }

    // 绘制五角星
    private final StarDrawer starDrawer = new StarDrawer();

    public boolean isDrawingStar() {
        return starDrawer.isDrawing();
    }

    public void startStarDrawing(int x, int y) {
        starDrawer.startDrawing(x, y, ColorSelectJPanel.getCurrentSelectedColor(), ToolPanel.getToolWidth());
    }

    public void finishStarDrawing(int x, int y) {
        starDrawer.finishDrawing(x, y, bufferGraphics);
        repaint();
    }

    public void previewStarDrawing(int startX, int startY, int x, int y) {
        starDrawer.previewDrawing(startX, startY, x, y);
        repaint();
    }


    public void flipImageHorizontally() {
        if (bufferImage != null) {
            int width = bufferImage.getWidth();
            int height = bufferImage.getHeight();
            BufferedImage flippedImage = new BufferedImage(width, height, bufferImage.getType());
            Graphics2D g2d = flippedImage.createGraphics();
            g2d.drawImage(bufferImage, 0, 0, width, height, width, 0, 0, height, null);
            g2d.dispose();
            bufferImage = flippedImage;
            repaint();
        }
    }

    public void flipImageVertically() {
        if (bufferImage != null) {
            int width = bufferImage.getWidth();
            int height = bufferImage.getHeight();
            BufferedImage flippedImage = new BufferedImage(width, height, bufferImage.getType());
            Graphics2D g2d = flippedImage.createGraphics();
            g2d.drawImage(bufferImage, 0, 0, width, height, 0, height, width, 0, null);
            g2d.dispose();
            bufferImage = flippedImage;
            repaint();
        }
    }

    public void flipImageBoth() {
        if (bufferImage != null) {
            int width = bufferImage.getWidth();
            int height = bufferImage.getHeight();
            BufferedImage flippedImage = new BufferedImage(width, height, bufferImage.getType());
            Graphics2D g2d = flippedImage.createGraphics();
            g2d.drawImage(bufferImage, 0, 0, width, height, width, height, 0, 0, null);
            g2d.dispose();
            bufferImage = flippedImage;
            repaint();
        }
    }

    public BufferedImage getCanvasImage() {
        int width = this.getWidth();
        int height = this.getHeight();
        BufferedImage canvasImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvasImage.createGraphics();
        this.paint(g2d);
        g2d.dispose();
        return canvasImage;
    }

    public void setImage(BufferedImage image) {
        this.bufferImage = image;
        this.bufferGraphics = bufferImage.createGraphics();
        repaint();
    }

    //撤销功能
    // 添加撤销栈
    private Stack<BufferedImage> undoStack = new Stack<>();
    private Stack<BufferedImage> redoStack = new Stack<>();
    private final int MAX_UNDO = 30;

    public void saveStateToUndoStack() {
        if (bufferImage != null) {
            BufferedImage imageCopy = new BufferedImage(bufferImage.getWidth(), bufferImage.getHeight(), bufferImage.getType());
            Graphics2D g2d = imageCopy.createGraphics();
            g2d.drawImage(bufferImage, 0, 0, null);
            g2d.dispose();

            if (undoStack.size() >= MAX_UNDO) {
                undoStack.remove(0); // 移除最早的状态
            }
            undoStack.push(imageCopy);
        }
        if (redoStack.size() != 0) {
            OperatorPanel.getRedoButton().setEnabled(true);
        } else {
            OperatorPanel.getRedoButton().setEnabled(false);
        }
        if (undoStack.size() != 0) {
            OperatorPanel.getUndoButton().setEnabled(true);
        } else {
            OperatorPanel.getUndoButton().setEnabled(false);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(bufferImage); // 将当前状态保存到撤销栈
            bufferImage = redoStack.pop();
            bufferGraphics = bufferImage.createGraphics();
            bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 启用抗锯齿
            repaint();
        }
        if (redoStack.size() != 0) {
            OperatorPanel.getRedoButton().setEnabled(true);
        } else {
            OperatorPanel.getRedoButton().setEnabled(false);
        }
        if (undoStack.size() != 0) {
            OperatorPanel.getUndoButton().setEnabled(true);
        } else {
            OperatorPanel.getUndoButton().setEnabled(false);
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(bufferImage); // 将当前状态保存到重做栈
            bufferImage = undoStack.pop();
            bufferGraphics = bufferImage.createGraphics();
            bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 启用抗锯齿
            repaint();
        }
        if (redoStack.size() != 0) {
            OperatorPanel.getRedoButton().setEnabled(true);
        } else {
            OperatorPanel.getRedoButton().setEnabled(false);
        }
        if (undoStack.size() != 0) {
            OperatorPanel.getUndoButton().setEnabled(true);
        } else {
            OperatorPanel.getUndoButton().setEnabled(false);
        }
    }

    public void clear() {
        if (bufferImage != null) {
            // 保存当前状态到撤销栈
            saveStateToUndoStack();

            // 清空画布，将所有像素设为白色
            bufferGraphics.setColor(Color.WHITE);
            bufferGraphics.fillRect(0, 0, bufferImage.getWidth(), bufferImage.getHeight());

            // 触发重绘
            repaint();
        }
    }


    private void addUndoRedoKeyListener() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z"), "undo");
        getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Y"), "redo");
        getActionMap().put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });


        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control X"), "delete");
        getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (draggedImageJPanel != null) {
                    saveStateToUndoStack();
                    BufferedImage image = draggedImageJPanel.getImage();
                    ClipboardUtil.copyImageToClipboard(image);
                    remove(draggedImageJPanel);
                    draggedImageJPanel = null;
                    OperatorPanel.getConfirm().setEnabled(false);
                    repaint();
                }
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control V"), "paste");
        getActionMap().put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(draggedImageJPanel != null) return;
                try {
                    BufferedImage image = ClipboardUtil.toBufferedImage(ClipboardUtil.getImageFromClipboard());
                    if (image != null) {
                        OperatorPanel.getConfirm().setEnabled(true);
                        createDraggedImageJPanel(0, 0, image.getWidth(), image.getHeight(),image);
                        repaint();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    //填入左上角坐标，长和宽，将指定区域的图片进行截取
    public BufferedImage captureRectangleImage(int x, int y, int width, int height) throws IOException {
        int scale = (int) zoomHandler.getImageScale();
        BufferedImage croppedImage = bufferImage.getSubimage(x * scale, y * scale, width * scale, height * scale);
        return croppedImage;
    }

    //删除这个区域里的数据
    public void clearRectangle(int x, int y, int width, int height) {
        if (bufferImage != null) {
            saveStateToUndoStack();
            Graphics2D g2d = bufferImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 启用抗锯齿
            g2d.setColor(Color.WHITE); // 可根据需求更改颜色
            g2d.fillRect(x, y, width, height);
            g2d.dispose();
            repaint();
        }
    }

    //在指定区域绘制image
    public void drawImageInRectangle(int x, int y, int width, int height, BufferedImage image) {
        if (bufferImage != null && image != null) {
            saveStateToUndoStack();
            Graphics2D g2d = bufferImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 启用抗锯齿
            g2d.drawImage(image, x, y, width, height, null);
            g2d.dispose();
            repaint();
        }
    }


}