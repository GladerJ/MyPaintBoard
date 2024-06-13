package mainwindow;

import gladerUI.DraggedImageJPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.Border;

public class OperatorPanel extends JPanel {
    private static JButton undoButton, redoButton;
    private DrawingPanel drawingPanel;
    private MainWindow mainWindow;

    private static JButton confirm;

    public static JButton getConfirm() {
        return confirm;
    }

    public static JButton getUndoButton() {
        return undoButton;
    }

    public static void setUndoButton(JButton undoButton) {
        OperatorPanel.undoButton = undoButton;
    }

    public static JButton getRedoButton() {
        return redoButton;
    }

    public static void setRedoButton(JButton redoButton) {
        OperatorPanel.redoButton = redoButton;
    }

    public OperatorPanel(DrawingPanel drawingPanel,MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        // 创建一个柔和的边框
        Border softBorder = BorderFactory.createEtchedBorder(Color.LIGHT_GRAY, Color.GRAY);
        // 为整个面板添加边框
        setBorder(BorderFactory.createCompoundBorder(softBorder, BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        // 设置表格布局，2 行 2 列，水平和垂直间隔均为 5 像素
        setLayout(new GridLayout(2, 2, 5, 5));

        this.drawingPanel = drawingPanel;

        // 创建并添加撤销按钮
        undoButton = createButton("image/撤销.png");
        undoButton.setEnabled(false);
        add(undoButton);
        undoButton.addActionListener(e -> drawingPanel.undo());

        // 创建并添加反撤销按钮
        redoButton = createButton("image/反撤销.png");
        redoButton.setEnabled(false);
        redoButton.addActionListener(e -> drawingPanel.redo());
        add(redoButton);

        // 创建并添加清空画布按钮
        JButton clearButton = createButton("image/清空画布.png");
        add(clearButton);
        clearButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    mainWindow,
                    "确定要清空画布吗？",
                    "确认清空",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (response == JOptionPane.YES_OPTION) {
                drawingPanel.clear();
            }
        });

        // 创建将选择框内容嵌入画板的操控按钮
        confirm = createButton("image/嵌入.png");
        add(confirm);
        confirm.setEnabled(false);
        confirm.addActionListener(e->{
            DraggedImageJPanel draggedImageJPanel = drawingPanel.getDraggedImageJPanel();
            drawingPanel.drawImageInRectangle(draggedImageJPanel.getX(),draggedImageJPanel.getY()
            ,draggedImageJPanel.getWidth(),draggedImageJPanel.getHeight(),draggedImageJPanel.getImage());
            drawingPanel.remove(draggedImageJPanel);
            drawingPanel.setDraggedImageJPanel(null);
            confirm.setEnabled(false);
        });
    }

    private JButton createButton(String imagePath) {
        JButton button = new JButton();
        try {
            Image icon = ImageIO.read(new File(imagePath));
            button.setIcon(new ImageIcon(icon));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 设置按钮大小
        button.setPreferredSize(new Dimension(33, 33));
        return button;
    }
}
