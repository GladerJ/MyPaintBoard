package mainwindow;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.util.Stack;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.util.Stack;

class ScanSeedFill extends JFrame {
    Image image1, image2;
    int red = Color.red.getRGB();

    int[] getPixels(){
        Point p[] = new Point[8];
        p[0] = new Point(20, 20);
        p[1] = new Point(90, 10);
        p[2] = new Point(150, 40);
        p[3] = new Point(150, 100);
        p[4] = new Point(120, 120);
        p[5] = new Point(70, 120);
        p[6] = new Point(20, 80);
        p[7] = new Point(20, 20);
        int w = 160, h = 160;
        int[] pixels = new int[w * h];
        for (int i = 0; i < 7; i++) {
            float x, y, dx, dy;
            int k = Math.max(Math.abs(p[i].x - p[i + 1].x), Math.abs(p[i].y - p[i + 1].y));
            dx = (float) (p[i + 1].x - p[i].x) / k;
            dy = (float) (p[i + 1].y - p[i].y) / k;
            x = (float) p[i].x;
            y = (float) p[i].y;
            for (int j = 0; j < k; j++) {
                pixels[(int) (y + .5f) * w + (int) (x + .5f)] = red;
                x = x + dx;
                y = y + dy;
            }
        }
        return pixels;
    }

    private void drawImages(Graphics g) {
        g.drawImage(image1, 100, 200, this);
        g.drawImage(image2, 300, 200, this);
    }

    public ScanSeedFill(){
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawImages(g);
            }
        };
        getContentPane().add(panel);
        int w = 160;
        int h = 160;
        int maxx = 150, minx = 20, maxy = 120, miny = 10;
        int[] pixels1 = getPixels();
        int[] pixels2 = getPixels();

        ImageProducer ip1 = new MemoryImageSource(w, h, pixels1, 0, w);
        image1 = createImage(ip1);
        scan(pixels2, new Point(330, 230), new Color(238,238,238).getRGB(), red, w, h, red);
        ImageProducer ip2 = new MemoryImageSource(w, h, pixels2, 0, w);
        image2 = createImage(ip2);


    }

    public static void scan(int[] filledPixels, Point point, int old_color, int new_color, int w, int h, int boundary_color) {
        int x = 0, y = 0, savex = 0, xright = 0, xleft = 0;
        Point p;
        Stack<Point> stack = new Stack<>();
        stack.push(point);
        boolean span_need_fill = false;
        while (!stack.empty()) {
            p = stack.pop();
            x = p.x;
            y = p.y;
            savex = x;
            if (x < 0 || x >= w || y < 0 || y >= h) {
                continue; // 如果超出边界，跳过当前点
            }
            while (x < h && y < w && x >= 0 && y >= 0 && filledPixels[y * w + x] != boundary_color) {
                filledPixels[y * w + x] = new_color;
                x++;
            }
            xright = x - 1;
            x = savex - 1;
            while (x < h && y < w && x >= 0 && y >= 0 && filledPixels[y * w + x] != boundary_color) {
                filledPixels[y * w + x] = new_color;
                x--;
            }
            xleft = x + 1;
            x = xleft;
            y = y + 1;
            while (x < h && y < w && x >= 0 && y >= 0 && x <= xright) {
                span_need_fill = false;
                while (y < w && x < h && filledPixels[y * w + x] == old_color && x <= xright) {
                    span_need_fill = true;
                    x++;
                }
                if (span_need_fill) {
                    p = new Point(x - 1, y);
                    stack.push(p);
                    span_need_fill = false;
                }
                while (x < h && y < w && x >= 0 && y >= 0 && filledPixels[y * w + x] != old_color && x <= xright) x++;
            }
            x = xleft;
            y -= 2;
            while (x < h && y < w && x >= 0 && y >= 0 && x <= xright) {
                span_need_fill = false;
                while (x < h && y < w && x >= 0 && y >= 0 && x <= xright && filledPixels[w * y + x] == old_color) {
                    span_need_fill = true;
                    x++;
                }
                if (span_need_fill) {
                    p = new Point(x - 1, y);
                    stack.push(p);
                    span_need_fill = false;
                }
                while (x >= 0 && y >= 0 && x < h && y < w && x <= xright && filledPixels[y * w + x] != old_color) x++;
            }
        }
    }

    public static void main(String[] args) {
        ScanSeedFill b = new ScanSeedFill();
        b.setSize(800, 600);
        b.setBackground(new Color(238,238,238));
        b.setLocationRelativeTo(null);
        b.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        b.setVisible(true);
    }
}