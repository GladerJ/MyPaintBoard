package gladerUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;

public class DraggedTextArea extends JTextArea {

    public static int originalFontSize;

    public static int getOriginalFontSize() {
        return originalFontSize;
    }

    public static void setOriginalFontSize(int originalFontSize) {
        DraggedTextArea.originalFontSize = originalFontSize;
    }

    private Point initialClick;

    public DraggedTextArea(double imageScale) {
        setOpaque(false);
        if (originalFontSize == 0) originalFontSize = 20;
        setFont(new Font(getFont().getName(), Font.PLAIN, (int) Math.round(originalFontSize * imageScale)));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        requestFocusInWindow();

        // 计算默认大小
        FontMetrics fontMetrics = getFontMetrics(getFont());
        int defaultHeight = fontMetrics.getHeight();
        int defaultWidth = fontMetrics.charWidth('M') * 6; // 假设每个字符宽度相同，取'M'的宽度

        // 设置默认大小
        setPreferredSize(new Dimension(defaultWidth, defaultHeight));
        setSize(new Dimension(defaultWidth, defaultHeight));

        // 添加右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem fontMenuItem = new JMenuItem("设置字体和字号");
        fontMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Frame owner = (Frame) SwingUtilities.getWindowAncestor(DraggedTextArea.this);
                FontChooser fontChooser = new FontChooser(owner);
                fontChooser.setVisible(true);

                if (fontChooser.isConfirmed()) {
                    String selectedFont = fontChooser.getSelectedFont();
                    int selectedSize = fontChooser.getSelectedSize();
                    originalFontSize = selectedSize;
                    setFont(new Font(selectedFont, Font.PLAIN, (int) Math.round(originalFontSize * imageScale)));
                    adjustSize();
                }
            }
        });
        popupMenu.add(fontMenuItem);

        // 添加鼠标监听器
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(DraggedTextArea.this, e.getX(), e.getY());
                } else {
                    initialClick = e.getPoint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 拖动文本框
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });

        // 添加文本变化监听器
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                adjustSize();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                adjustSize();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                adjustSize();
            }
        });
    }

    public void adjustSize() {
        // 获取文本内容的首选大小
        FontMetrics fontMetrics = getFontMetrics(getFont());
        int textWidth = 0;
        int lineCount = getLineCount();
        try {
            for (int i = 0; i < lineCount; i++) {
                int lineStartOffset = getLineStartOffset(i);
                int lineEndOffset = getLineEndOffset(i);
                String lineText = getText(lineStartOffset, lineEndOffset - lineStartOffset);
                int lineWidth = fontMetrics.stringWidth(lineText);
                textWidth = Math.max(textWidth, lineWidth);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // 计算最小宽度（6个字符宽度）
        int minWidth = fontMetrics.charWidth('M') * 6;

        // 确保文本宽度至少为最小宽度
        textWidth = Math.max(textWidth, minWidth);

        int textHeight = fontMetrics.getHeight() * lineCount;

        // 添加一些边距
        int margin = 10;
        textWidth += margin;
        textHeight += margin;

        // 设置文本区域的大小
        setPreferredSize(new Dimension(textWidth, textHeight));
        setSize(new Dimension(textWidth, textHeight));
        revalidate();
        repaint();
    }


    public void setFontSize(int newSize) {
        Font currentFont = getFont();
        Font newFont = currentFont.deriveFont((float) newSize);
        setFont(newFont);
        adjustSize();
    }

    public int getFontSize() {
        return getFont().getSize();
    }

    public static DraggedTextArea createDraggedTextArea(double imageScale) {
        return new DraggedTextArea(imageScale);
    }

}
