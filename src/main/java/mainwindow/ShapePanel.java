package mainwindow;

import com.jidesoft.swing.JideButton;
import handler.ZoomHandler;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ShapePanel extends JPanel {
    private JLabel selectedToolLabel;
    private JLabel selectedToolName;
    private static String currentTool = ""; // 静态变量保存当前所选工具
    private static JideButton selectedButton; // 用于保存当前选中的按钮
    private MainWindow mainWindow;
    private DrawingPanel drawingPanel;

    public ShapePanel(MainWindow mainWindow,DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        // 创建一个柔和的边框
        Border softBorder = BorderFactory.createEtchedBorder(Color.LIGHT_GRAY, Color.GRAY);
        // 为整个面板添加边框
        setBorder(BorderFactory.createCompoundBorder(softBorder, BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setLayout(new BorderLayout());

        this.mainWindow = mainWindow;
        // 左侧工具面板
        JPanel toolPanel = new JPanel(new GridLayout(2, 4,5,5));
        JideButton button = addToolButton(toolPanel, "直线", "image/直线.png");
        addToolButton(toolPanel, "椭圆", "image/椭圆.png");
        addToolButton(toolPanel, "三角形", "image/三角形.png");
        addToolButton(toolPanel, "矩形", "image/矩形.png");
        addToolButton(toolPanel, "五边形", "image/五边形.png");
        addToolButton(toolPanel, "五角星", "image/五角星.png");
        addToolButton(toolPanel, "裁剪", "image/裁剪.png");
        selectedToolLabel = new JLabel();
        selectedToolLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedToolLabel.setVerticalAlignment(SwingConstants.CENTER);
        selectedToolName = new JLabel("", SwingConstants.CENTER);
        add(toolPanel, BorderLayout.WEST);
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

                if(toolName == "裁剪"){
                    ZoomHandler zoomTool = drawingPanel.getZoomTool();
                    zoomTool.reset();
                }

                if (selectedButton != null) {
                    selectedButton.setBorderPainted(false);
                }
                selectedButton = button;
                selectedButton.setBorderPainted(true);
                selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                mainWindow.flushText();
                ToolPanel.transport();
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
    public static String getCurrentFigure() {
        return currentTool;
    }

    public static void transport(){
        // 清除当前选中按钮的边框
        if (selectedButton != null) {
            selectedButton.setBorderPainted(false);
        }
        currentTool = "工具";
    }
}
