package mainwindow;

import com.jidesoft.swing.JideButton;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class ToolPanel extends JPanel {
    private static JLabel selectedToolLabel;
    private static JLabel selectedToolName;
    private static String currentTool = ""; // 静态变量保存当前所选工具
    private static JSlider sizeSlider;
    private static JideButton selectedButton; // 用于保存当前选中的按钮
    private MainWindow mainWindow;
    private static Map<String, ImageIcon> toolIcons; // 保存工具图标的映射

    public static ImageIcon getCurrentToolIcon() {
        if(currentTool == "铅笔" || currentTool == "颜色提取")
            return toolIcons.get(currentTool + "翻转");
        if(currentTool == "图形" || currentTool == "填充") return null;
        return toolIcons.get(currentTool);
    }

    public ToolPanel(MainWindow mainWindow) {
        Border softBorder = BorderFactory.createEtchedBorder(Color.LIGHT_GRAY, Color.GRAY);
        setBorder(BorderFactory.createCompoundBorder(softBorder, BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setLayout(new BorderLayout());
        this.mainWindow = mainWindow;
        loadToolIcons(); // 加载工具图标

        // 左侧工具面板
        JPanel toolPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        addToolButton(toolPanel, "铅笔");
        addToolButton(toolPanel, "填充");
        addToolButton(toolPanel, "文本");
        addToolButton(toolPanel, "橡皮擦");
        addToolButton(toolPanel, "颜色提取");
        addToolButton(toolPanel, "放大镜");

        // 中间显示面板
        JPanel displayPanel = new JPanel(new BorderLayout());
        selectedToolLabel = new JLabel();
        selectedToolLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedToolLabel.setVerticalAlignment(SwingConstants.CENTER);
        selectedToolName = new JLabel("", SwingConstants.CENTER);
        displayPanel.add(selectedToolLabel, BorderLayout.CENTER);
        displayPanel.add(selectedToolName, BorderLayout.SOUTH);

        // 默认选中铅笔
        String defaultTool = "铅笔";
        selectedToolLabel.setIcon(toolIcons.get(defaultTool));
        selectedToolName.setText(defaultTool);
        currentTool = defaultTool;

        // 设置铅笔按钮为选中状态
        selectedButton = (JideButton) toolPanel.getComponent(0);
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
        sizeSlider.setDoubleBuffered(true);
        sizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sizeSlider.repaint();
            }
        });
        sizePanel.add(sizeSlider, BorderLayout.CENTER);

        // 添加到主面板
        add(toolPanel, BorderLayout.WEST);
        add(displayPanel, BorderLayout.CENTER);
        add(sizePanel, BorderLayout.EAST);
    }

    private void loadToolIcons() {
        toolIcons = new HashMap<>();
        toolIcons.put("铅笔", new ImageIcon("image/铅笔.png"));
        toolIcons.put("铅笔翻转", new ImageIcon("image/铅笔翻转.png"));
        toolIcons.put("填充", new ImageIcon("image/填充.png"));
        toolIcons.put("文本", new ImageIcon("image/文本.png"));
        toolIcons.put("橡皮擦", new ImageIcon("image/橡皮擦.png"));
        toolIcons.put("颜色提取", new ImageIcon("image/颜色提取.png"));
        toolIcons.put("颜色提取翻转", new ImageIcon("image/颜色提取翻转.png"));
        toolIcons.put("放大镜", new ImageIcon("image/放大镜.png"));
        toolIcons.put("图形", new ImageIcon("image/图形.png"));
    }

    private void addToolButton(JPanel panel, String toolName) {
        JideButton button = new JideButton();
        button.setIcon(toolIcons.get(toolName));
        button.setPreferredSize(new Dimension(33, 33));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setToolTipText(toolName);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedToolLabel.setIcon(toolIcons.get(toolName));
                selectedToolName.setText(toolName);
                currentTool = toolName;

                if (selectedButton != null) {
                    selectedButton.setBorderPainted(false);
                }
                selectedButton = button;
                selectedButton.setBorderPainted(true);
                selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                mainWindow.flushText();
                ShapePanel.transport();
            }
        });

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
    }

    public static String getCurrentTool() {
        return currentTool;
    }

    public static int getToolWidth() {
        return sizeSlider.getValue();
    }

    public static void transport() {
        if (selectedButton != null) {
            selectedButton.setBorderPainted(false);
        }
        selectedToolLabel.setIcon(toolIcons.get("图形"));
        selectedToolName.setText("图形");
        currentTool = "图形";
    }
}
