package mainwindow;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import gladerUI.CircleButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorSelectJPanel extends JPanel {

    private JPanel leftContainer, rightContainer;
    private JPanel leftJPanel, rightJPanel;
    private static CircleButton selectedColor;

    // 静态变量来存储当前选中的颜色
    private static Color currentSelectedColor = Color.WHITE;

    private final Color[] colors = {
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE,
            Color.CYAN, Color.MAGENTA, Color.PINK, new Color(123,0,0), Color.DARK_GRAY,
            Color.WHITE, Color.BLACK, Color.GRAY, Color.CYAN.darker(), Color.MAGENTA.darker(),
            Color.ORANGE.darker(), Color.PINK.darker(), Color.BLUE.darker(), Color.GREEN.darker(),
            Color.YELLOW.darker(), Color.RED.darker()
    };

    public ColorSelectJPanel() {
        // 创建一个柔和的边框
        Border softBorder = BorderFactory.createEtchedBorder(Color.LIGHT_GRAY, Color.GRAY);
        // 为整个面板添加边框
        setBorder(BorderFactory.createCompoundBorder(softBorder, BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setLayout(new BorderLayout()); // 设置1行2列的GridLayout，用于容纳两个列
        leftContainer = new JPanel(new BorderLayout()); // 左容器使用BorderLayout
        rightContainer = new JPanel(new BorderLayout()); // 右容器使用BorderLayout

        leftJPanel = new JPanel();
        rightJPanel = new JPanel();
        rightJPanel.setLayout(new GridLayout(2, 10, 1, 3)); // 设置2行10列的GridLayout，并添加5像素的水平和垂直间隙
        selectedColor = new CircleButton("");
        selectedColor.setBackground(currentSelectedColor); // 设置选中的颜色为当前选中的颜色

        // 添加左侧按钮点击事件监听器
        leftJPanel.add(selectedColor);
        selectedColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 弹出颜色选择器
                Color newColor = JColorChooser.showDialog(ColorSelectJPanel.this, "Choose Color", currentSelectedColor);
                if (newColor != null) {
                    setCurrentSelectedColor(newColor); // 设置当前选中的颜色为用户选择的颜色
                    selectedColor.setBackground(newColor); // 设置左侧选定颜色
                }
            }
        });

        // 添加右侧按钮点击事件监听器
        ActionListener buttonListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CircleButton clickedButton = (CircleButton) e.getSource();
                Color clickedColor = clickedButton.getBackground();
                setCurrentSelectedColor(clickedColor); // 设置当前选中的颜色
                selectedColor.setBackground(clickedColor); // 设置左侧选定颜色
                if(DrawingPanel.getDraggedTextArea() != null){
                    DrawingPanel.getDraggedTextArea().setForeground(clickedColor);
                }
            }
        };

        // 在右侧面板中添加带有监听器的按钮
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 10; j++) {
                CircleButton button = new CircleButton("");
                button.setBackground(colors[i * 10 + j]);
                button.addActionListener(buttonListener); // 添加监听器
                rightJPanel.add(button);
            }
        }

        leftContainer.add(leftJPanel, BorderLayout.CENTER);
        rightContainer.add(rightJPanel, BorderLayout.CENTER);

        add(leftContainer, BorderLayout.WEST);
        add(rightContainer, BorderLayout.CENTER);
    }

    // 静态方法用于返回当前选中的颜色
    public static Color getCurrentSelectedColor() {
        return currentSelectedColor;
    }

    // 静态方法用于设置当前选中的颜色
    public static void setCurrentSelectedColor(Color color) {
        currentSelectedColor = color;
        selectedColor.setBackground(color);
    }
}
