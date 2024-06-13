package gladerUI;

import utils.ImageUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class DraggedImageJPanel extends JPanel {
    private BufferedImage image;
    private Point initialClick;

    public BufferedImage getImage() {
        return image;
    }

    public DraggedImageJPanel(BufferedImage image) {
        this.image = image;
        this.setBorder(new LineBorder(Color.BLACK)); // 设置黑色边框
        this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)); // 初始光标设为移动光标

        // 添加鼠标监听器
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)); // 鼠标进入变成十字光标
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)); // 鼠标退出变回移动光标
            }

            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint(); // 记录鼠标按下位置
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e.getX(), e.getY());
                }
            }
        });

        // 添加鼠标拖动监听器
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 获取父容器位置
                JComponent parent = (JComponent) getParent();
                int parentX = parent.getLocation().x;
                int parentY = parent.getLocation().y;

                // 获取当前面板位置
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                // 鼠标拖动距离
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // 新位置
                int X = thisX + xMoved;
                int Y = thisY + yMoved;

                // 设置面板的新位置
                setLocation(X, Y);
                parent.repaint(); // 重绘父容器
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void showPopupMenu(int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem horizontalFlipItem = new JMenuItem("水平翻转");
        horizontalFlipItem.addActionListener((ActionEvent e) -> {
            image = ImageUtil.horizontalFlip(image);
            repaint();
        });
        popupMenu.add(horizontalFlipItem);

        JMenuItem verticalFlipItem = new JMenuItem("垂直翻转");
        verticalFlipItem.addActionListener((ActionEvent e) -> {
            image = ImageUtil.verticalFlip(image);
            repaint();
        });
        popupMenu.add(verticalFlipItem);

        JMenuItem invertColorsItem = new JMenuItem("反色");
        invertColorsItem.addActionListener((ActionEvent e) -> {
            image = ImageUtil.invertColors(image);
            repaint();
        });
        popupMenu.add(invertColorsItem);

        JMenuItem rotateItem = new JMenuItem("旋转...");
        rotateItem.addActionListener((ActionEvent e) -> {
            String angleStr = JOptionPane.showInputDialog(this, "请输入旋转角度（顺时针为正，逆时针为负）：");
            try {
                int angle = Integer.parseInt(angleStr);
                image = ImageUtil.rotate(image, angle);
                repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的整数角度！");
            }
        });
        popupMenu.add(rotateItem);

        popupMenu.show(this, x, y);
    }
}
