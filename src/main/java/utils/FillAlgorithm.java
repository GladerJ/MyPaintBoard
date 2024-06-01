package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

public class FillAlgorithm {
    public static void fill(BufferedImage bufferImage, int x, int y, Color targetColor, Color fillColor) {
        if (targetColor.equals(fillColor)) {
            return;
        }

        int width = bufferImage.getWidth();
        int height = bufferImage.getHeight();

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point p = queue.remove();
            int px = p.x;
            int py = p.y;

            if (px < 0 || px >= width || py < 0 || py >= height) {
                continue;
            }

            if (new Color(bufferImage.getRGB(px, py)).equals(targetColor)) {
                int left = px;
                int right = px;

                while (left > 0 && new Color(bufferImage.getRGB(left - 1, py)).equals(targetColor)) {
                    left--;
                }

                while (right < width - 1 && new Color(bufferImage.getRGB(right + 1, py)).equals(targetColor)) {
                    right++;
                }

                for (int i = left; i <= right; i++) {
                    bufferImage.setRGB(i, py, fillColor.getRGB());
                    if (py > 0 && new Color(bufferImage.getRGB(i, py - 1)).equals(targetColor)) {
                        queue.add(new Point(i, py - 1));
                    }
                    if (py < height - 1 && new Color(bufferImage.getRGB(i, py + 1)).equals(targetColor)) {
                        queue.add(new Point(i, py + 1));
                    }
                }
            }
        }
    }
}
