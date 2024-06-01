package mainwindow;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class SwingDemo {

    public static void main(String args[]) {
        new SwingDemo();
    }

    public static Color BG_COLOR = new Color(128, 128, 128);

    public SwingDemo() {
        JFrame mjf = new JFrame("图片查看");
        ImagePanle mImgeView = new ImagePanle();
        mImgeView.setPreferredSize(new Dimension(500, 500));
        mImgeView.setMinimumSize(new Dimension(500, 500));
        mImgeView.setBackground(BG_COLOR);

        JMenuBar jmb = new JMenuBar();
        JMenu meSetting = new JMenu("文件");
        JMenuItem mOpen = new JMenuItem("打开");

        mOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

                BufferedImage curBufferedImg;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
                int option = fileChooser.showOpenDialog(mjf);
                if (option == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = fileChooser.getSelectedFile();
                        curBufferedImg = ImageIO.read(new File(file.getAbsolutePath()));
                        mImgeView.updateImage(curBufferedImg);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }

            }
        });

        meSetting.add(mOpen);
        jmb.add(meSetting);

        mjf.setJMenuBar(jmb);
        mjf.add(mImgeView);

        mjf.setMinimumSize(new Dimension(800, 600));
        mjf.setVisible(true);
        mjf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    class ImagePanle extends JPanel {

        BufferedImage mSrcBuffeImg = null;
        private static final long serialVersionUID = 1L;
        private double mScale = 1.0;
        private static final boolean B_REAL_SIZE = true;
        private double mCurX = 0;
        private double mCurY = 0;
        private double mStartX = 0;
        private double mStartY = 0;
        private double mTranslateX = 0;
        private double mTranslateY = 0;

        //		记录最初原始坐标系，用于清除背景
        AffineTransform mOriginTransform;
        BufferedImage mViewBufferImg;
        Graphics2D mViewG2d;

        void refreshView() {
            clear_buffer(mViewG2d, mOriginTransform, mViewBufferImg);
            mViewG2d.drawImage(mSrcBuffeImg, 0, 0, null);
            repaint();
        }

        void clear_buffer(Graphics2D g2d, AffineTransform org, BufferedImage bufImg) {
//			将保存的测量数据，重新在经过变换后的坐标系上进行绘制
            // 先恢复一下原始状态，保证清空的坐标是全部，执行清空，然后再切会来
            AffineTransform temp = g2d.getTransform();
            g2d.setTransform(org);
            g2d.clearRect(0, 0, bufImg.getWidth(), bufImg.getHeight());
            g2d.setTransform(temp);
        }

        public void updateImage(BufferedImage srcImage) {
            mSrcBuffeImg = srcImage;
            mViewBufferImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
            System.out.println("create buff image");
            mViewG2d = mViewBufferImg.createGraphics();
            mViewG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            mViewG2d.setBackground(BG_COLOR);
            System.out.println("crate bufg2d");
            mOriginTransform = mViewG2d.getTransform();
            refreshView();
        }

        private Point internal_getImagePoint(double mouseX, double mouseY) {

            // 不管是先平移后缩放还是先缩放后平移，都以 先减 再缩放的方式可以获取正确
            double rawTranslateX = mViewG2d.getTransform().getTranslateX();
            double rawTranslateY = mViewG2d.getTransform().getTranslateY();
            // 获取当前的 Scale Transform
            double scaleX = mViewG2d.getTransform().getScaleX();
            double scaleY = mViewG2d.getTransform().getScaleY();

//        		不管是先平移后缩放还是先缩放后平移，都以 先减 再缩放的方式可以获取正确
            int imageX = (int) ((mouseX - rawTranslateX) / scaleX);
            int imageY = (int) ((mouseY - rawTranslateY) / scaleY);

            return new Point(imageX, imageY);
        }

        public ImagePanle() {

//			启用双缓存
            setDoubleBuffered(true);

            this.addMouseWheelListener((MouseWheelListener) new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (mViewG2d == null) {
                        return;
                    }
                    mCurX = e.getX();
                    mCurY = e.getY();

                    int notches = e.getWheelRotation();
                    if (notches < 0) {
                        // 滚轮向上，放大画布
                        mScale = 1.1;

                    } else {
                        // 滚轮向下，缩小画布
                        mScale = 0.9;
                    }

                    Point imagePoint = internal_getImagePoint(e.getX(), e.getY());
                    int imageX = imagePoint.x;
                    int imageY = imagePoint.y;
                    System.out.println("x:" + e.getX() + "y:" + e.getY() + ",imagex:" + imageX + "x" + imageY);

                    double tralateX = mScale * imageX - imageX;
                    double tralateY = mScale * imageY - imageY;

                    mViewG2d.scale(mScale, mScale);
                    mViewG2d.translate(-tralateX / mScale, -tralateY / mScale); // 图片方大，就需要把坐标往左移动，移动的尺度是要考虑缩放的
                    // 先恢复一下原始状态，保证清空的坐标是全部，执行清空，然后再切会来
                    AffineTransform temp = mViewG2d.getTransform();
                    mViewG2d.setTransform(mOriginTransform);
                    mViewG2d.clearRect(0, 0, mViewBufferImg.getWidth(), mViewBufferImg.getHeight());
                    mViewG2d.setTransform(temp);

                    mViewG2d.drawImage(mSrcBuffeImg, 0, 0, null);
                    repaint(); // 重新绘制画布
                }

            });

            this.addMouseListener(new MouseListener() {

                @Override
                public void mouseReleased(MouseEvent e) {
                    // TODO Auto-generated method stub
                    System.out.println("mouseReleased:" + e.getX() + "x" + e.getY());
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // TODO Auto-generated method stub
                    System.out.println("mousePressed----:" + e.getX() + "x" + e.getY());
                    mStartX = e.getX();
                    mStartY = e.getY();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    // TODO Auto-generated method stub
                    System.out.println("mouseClicked----:" + e.getX() + "x" + e.getY());

                }
            });
            this.addMouseMotionListener(new MouseAdapter() {

                @Override
                public void mouseMoved(MouseEvent e) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    // TODO Auto-generated method stub
                    if (mViewG2d == null) {
                        return;
                    }

                    mCurX = e.getX();
                    mCurY = e.getY();
                    System.out.println("mouseDragged:" + e.getX() + "x" + e.getY() + "trans:" + (mCurX - mStartX) + ":"
                            + (mCurY - mStartY));

                    // 平移坐标，也是相对于变换后的坐标系而言的，所以
                    double scaleX = mViewG2d.getTransform().getScaleX();
                    double scaleY = mViewG2d.getTransform().getScaleY();

                    // TODO mCurX - mStartX 太小，比如为2， 而scalX 比较大，比如为3 则移动的时候回发生 (int)2/3 ==0; 不移动。
                    // 解决方案，把移动 ，全部在原始坐标系上做，也就是最后绘制缓冲区的时候，drawimage(transX,transY)
                    mTranslateX = (mCurX - mStartX) / scaleX;
                    mTranslateY = (mCurY - mStartY) / scaleY;

                    // 自身就是累计的
                    mViewG2d.translate(mTranslateX, mTranslateY);

                    mStartX = mCurX;
                    mStartY = mCurY;
                    System.out.println("mouseDragged: over+++");

                    // 先恢复一下原始状态，保证清空的坐标是全部，执行清空，然后再切会来
                    AffineTransform temp = mViewG2d.getTransform();
                    mViewG2d.setTransform(mOriginTransform);
                    mViewG2d.clearRect(0, 0, mViewBufferImg.getWidth(), mViewBufferImg.getHeight());
                    mViewG2d.setTransform(temp);

                    mViewG2d.drawImage(mSrcBuffeImg, 0, 0, null);
                    repaint();
                }
            });

        }

        public void reset_scale() {
//			恢复到1.0 缩放，0,0 左上角对齐
            mCurX = 0;
            mCurY = 0;
            mScale = 1.0;
            mViewG2d.setTransform(mOriginTransform);
            mViewG2d.clearRect(0, 0, mViewBufferImg.getWidth(), mViewBufferImg.getHeight());
            mViewG2d.drawImage(mSrcBuffeImg, 0, 0, null);
            repaint(); // 重新绘制画布
        }

        @Override
        public void paintComponent(Graphics g) {

            super.paintComponent(g);
            if (mViewBufferImg == null) {
                return;
            }
//			如果有多个“图层”要注意图层的顺序
            Graphics2D g2d = ((Graphics2D) g);
            g2d.drawImage(mViewBufferImg, 0, 0, null);
            System.out.println("draw-----------:" + getWidth() + "x" + getHeight());
        }
    }

}