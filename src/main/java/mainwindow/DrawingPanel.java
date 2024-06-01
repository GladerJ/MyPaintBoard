package mainwindow;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class DrawingPanel extends JPanel {
    private int prevX, prevY, currX, currY;
    private BufferedImage bufferImage;
    private Graphics2D bufferGraphics;
    private ZoomHandler zoomHandler;
    private int panelWidth;
    private int panelHeight;
    private boolean resizing = false;
    private int resizeHandleSize = 10;
    private Cursor defaultCursor = Cursor.getDefaultCursor();
    private Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);

    public DrawingPanel(int panelWidth, int panelHeight) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.zoomHandler = new ZoomHandler(this);

        setPreferredSize(new Dimension((int) (panelWidth * zoomHandler.getImageScale()), (int) (panelHeight * zoomHandler.getImageScale())));
        setBackground(Color.GRAY);

        MouseHandler mouseHandler = new MouseHandler(this);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    if (e.getWheelRotation() < 0) {
                        zoomHandler.zoomIn(1.1);
                    } else {
                        zoomHandler.zoomOut(1.1);
                    }
                }
            }
        });
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
        g2d.dispose();
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
}
