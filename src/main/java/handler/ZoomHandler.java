package handler;

import mainwindow.DrawingPanel;
import mainwindow.ShowBarJPanel;

public class ZoomHandler {
    private double imageScale = 1.0;
    private final double MIN_SCALE = 0.5;
    private final double MAX_SCALE = 8.0;
    private final DrawingPanel drawingPanel;

    public ZoomHandler(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    public void reset(){
        imageScale = 1;
        imageScale = Math.round(imageScale * 10) / 10.0;
        drawingPanel.updatePreferredSize(imageScale);
        drawingPanel.revalidate();
        drawingPanel.repaint();
        ShowBarJPanel.setProportion((int) (Math.round(imageScale * 100)));
    }

    public void zoomIn(double zoomFactor) {
        imageScale = Math.min(imageScale * zoomFactor, MAX_SCALE);
        imageScale = Math.round(imageScale * 10) / 10.0;
        drawingPanel.updatePreferredSize(imageScale);
        drawingPanel.revalidate();
        drawingPanel.repaint();
        ShowBarJPanel.setProportion((int) (Math.round(imageScale * 100)));
    }

    public void zoomOut(double zoomFactor) {
        imageScale = Math.max(imageScale / zoomFactor, MIN_SCALE);
        imageScale = Math.round(imageScale * 10) / 10.0;
        drawingPanel.updatePreferredSize(imageScale);
        drawingPanel.revalidate();
        drawingPanel.repaint();
        ShowBarJPanel.setProportion((int) (Math.round(imageScale * 100)));
    }

    public double getImageScale() {
        return imageScale;
    }
}
