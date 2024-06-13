package utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageUtil {

    public static BufferedImage horizontalFlip(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flippedImage = new BufferedImage(width, height, image.getType());
        Graphics2D g = flippedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, width, 0, 0, height, null);
        g.dispose();
        return flippedImage;
    }

    public static BufferedImage verticalFlip(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flippedImage = new BufferedImage(width, height, image.getType());
        Graphics2D g = flippedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, 0, height, width, 0, null);
        g.dispose();
        return flippedImage;
    }

    public static BufferedImage invertColors(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage invertedImage = new BufferedImage(width, height, image.getType());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xFF;
                int red = 255 - ((rgb >> 16) & 0xFF);
                int green = 255 - ((rgb >> 8) & 0xFF);
                int blue = 255 - (rgb & 0xFF);
                int invertedRGB = (alpha << 24) | (red << 16) | (green << 8) | blue;
                invertedImage.setRGB(x, y, invertedRGB);
            }
        }
        return invertedImage;
    }

    public static BufferedImage rotate(BufferedImage image, double angle) {
        double radians = Math.toRadians(angle);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        int width = image.getWidth();
        int height = image.getHeight();
        int newWidth = (int) Math.round(width * Math.abs(cos) + height * Math.abs(sin));
        int newHeight = (int) Math.round(width * Math.abs(sin) + height * Math.abs(cos));
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g = rotatedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.translate((newWidth - width) / 2, (newHeight - height) / 2);
        g.rotate(radians, width / 2.0, height / 2.0);
        g.drawRenderedImage(image, null);
        g.dispose();
        return rotatedImage;
    }
}
