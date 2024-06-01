package mainwindow;

import com.jidesoft.swing.JideButton;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ToolPanel extends JPanel {
    private JLabel selectedToolLabel;
    private JLabel selectedToolName;
    private static String currentTool = ""; // 静态变量保存当前所选工具
    private static JSlider sizeSlider;
    private JideButton selectedButton; // 用于保存当前选中的按钮

    public ToolPanel() {
        setLayout(new BorderLayout());

        // 左侧工具面板
        JPanel toolPanel = new JPanel(new GridLayout(2, 3,5,5));
        JideButton pencilButton = addToolButton(toolPanel, "铅笔", "image/铅笔.png");
        addToolButton(toolPanel, "填充", "image/填充.png");
        addToolButton(toolPanel, "文本", "image/文本.png");
        addToolButton(toolPanel, "橡皮擦", "image/橡皮擦.png");
        addToolButton(toolPanel, "颜色提取", "image/颜色提取.png");
        addToolButton(toolPanel, "放大镜", "image/放大镜.png");

        // 中间显示面板
        JPanel displayPanel = new JPanel(new BorderLayout());
        selectedToolLabel = new JLabel();
        selectedToolLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedToolLabel.setVerticalAlignment(SwingConstants.CENTER);
        selectedToolName = new JLabel("", SwingConstants.CENTER);

        displayPanel.add(selectedToolLabel, BorderLayout.CENTER);
        displayPanel.add(selectedToolName, BorderLayout.SOUTH);

        // 默认选中铅笔
        selectedToolLabel.setIcon(new ImageIcon("image/铅笔.png"));
        selectedToolName.setText("铅笔");
        currentTool = "铅笔"; // 更新当前所选工具

        // 设置铅笔按钮为选中状态
        selectedButton = pencilButton;
        selectedButton.setBorderPainted(true);
        selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // 下方进度条面板
        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.add(new JLabel("宽度:"), BorderLayout.WEST);
        sizeSlider = new JSlider(1, 50, 1);
        sizeSlider.setOrientation(SwingConstants.HORIZONTAL);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.setMajorTickSpacing(10);
        sizeSlider.setMinorTickSpacing(1);
        sizeSlider.setDoubleBuffered(true); // 启用双缓冲技术
        sizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sizeSlider.repaint(); // 强制重绘滑动条
            }
        });
        sizePanel.add(sizeSlider, BorderLayout.CENTER);

        // 添加到主面板
        add(toolPanel, BorderLayout.WEST);
        add(displayPanel, BorderLayout.CENTER);
        add(sizePanel, BorderLayout.EAST);
    }

    private JideButton addToolButton(JPanel panel, String toolName, String iconPath) {
        JideButton button = new JideButton();
        button.setIcon(new ImageIcon(iconPath));
        button.setPreferredSize(new Dimension(33, 33)); // 设置按钮大小为33x33
        button.setContentAreaFilled(false); // 去掉按钮默认背景颜色
        button.setBorderPainted(false); // 去掉按钮默认边框

        button.setToolTipText(toolName);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedToolLabel.setIcon(new ImageIcon(iconPath));
                selectedToolName.setText(toolName);
                currentTool = toolName; // 更新当前所选工具

                if (selectedButton != null) {
                    selectedButton.setBorderPainted(false);
                }
                selectedButton = button;
                selectedButton.setBorderPainted(true);
                selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        });

        // 添加鼠标事件监听器
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBorderPainted(true);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != selectedButton) {
                    button.setBorderPainted(false);
                }
            }
        });

        panel.add(button);
        return button;
    }

    // 静态方法返回当前所使用的工具
    public static String getCurrentTool() {
        return currentTool;
    }

    // 获取当前所选工具的宽度
    public static int getToolWidth() {
        return sizeSlider.getValue();
    }
}
