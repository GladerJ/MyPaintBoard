package mainwindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainWindow extends JFrame {
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenu viewMenu;
    private JMenuItem flipHorizontalItem;
    private JMenuItem flipVerticalItem;
    private JMenuItem flipBothItem;
    private JMenu editMenu;
    private JMenuItem editItem1;
    private JMenuItem editItem2;
    private JMenuItem editItem3;
    private ColorSelectJPanel colorSelectJPanel;
    private JScrollPane scrollPane;
    private ToolPanel toolPanel;
    private ShapePanel shapePanel;
    private OperatorPanel operatorPanel;

    public static int getWindowWidth() {
        return width;
    }

    public static void setWindowWidth(int width) {
        MainWindow.width = width;
    }

    public static int getWindowHeight() {
        return height;
    }

    public static void setWindowHeight(int height) {
        MainWindow.height = height;
    }

    private static int width;
    private static int height;
    private DrawingPanel drawingPanel;
    private ShowBarJPanel showBar;

    public DrawingPanel getDrawingPanel() {
        return drawingPanel;
    }

    // 构造函数
    public MainWindow() {
        setTitle("Main Window");
        setSize(800, 600);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        width = (int) screenSize.getWidth();
        height = (int) screenSize.getHeight();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 设置默认关闭操作为不执行任何操作
        setLocationRelativeTo(null);
        menuBar = new JMenuBar();
        // 创建菜单栏
        createMenuBar();
        // 将菜单栏设置到窗口
        setJMenuBar(menuBar);
        setLayout(null);
        createColorSelectJPanel();
        createDrawingPanel();
        createShowBar();
        createToolPanel();
        createShapePanel();
        createOperatorPanel();
        drawingPanel.saveStateToUndoStack();
        setVisible(true);

        // 添加窗口关闭监听器
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    private void createToolPanel() {
        toolPanel = new ToolPanel(this);
        toolPanel.setBounds(10, 10, 440, 80);
        this.add(toolPanel);
    }

    private void createShapePanel() {
        shapePanel = new ShapePanel(this);
        shapePanel.setBounds(460, 10, 125, 80);
        this.add(shapePanel);
    }

    // 创建状态栏
    private void createShowBar() {
        showBar = new ShowBarJPanel();
        showBar.setBounds(10, 10 + height - 200 + 30, width, 40);
        add(showBar);
    }

    // 创建颜色选择面板
    private void createColorSelectJPanel() {
        colorSelectJPanel = new ColorSelectJPanel();
        colorSelectJPanel.setBounds(460 + 135, 10, 380, 80);
        this.add(colorSelectJPanel);
        ColorSelectJPanel.setCurrentSelectedColor(Color.BLACK);
    }

    private void createOperatorPanel(){
        operatorPanel = new OperatorPanel(drawingPanel);
        operatorPanel.setBounds(460 + 135 + 395,10,120,80);
        this.add(operatorPanel);
    }

    // 创建绘图面板
    private void createDrawingPanel() {
        drawingPanel = new DrawingPanel(width - 20, height - 245);
        scrollPane = new JScrollPane(drawingPanel);
        scrollPane.setBounds(10, 95, width - 30, height - 245);
        this.add(scrollPane);

        // 在面板初始化后，将滚动条滚动到正中间
        SwingUtilities.invokeLater(() -> drawingPanel.scrollToCenter());
    }

    // 创建菜单的方法
    private void createMenuBar() {
        // 创建文件菜单及其子菜单项
        fileMenu = new JMenu("文件");
        openItem = new JMenuItem("打开");
        saveItem = new JMenuItem("保存");
        fileMenu.add(openItem);
        fileMenu.add(saveItem);

        // 为打开菜单项添加事件处理程序
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage();
            }
        });

        // 为保存菜单项添加事件处理程序
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCanvasAsImage();
            }
        });

        // 创建查看菜单及其子菜单项
        viewMenu = new JMenu("查看");
        flipHorizontalItem = new JMenuItem("水平翻转");
        flipVerticalItem = new JMenuItem("垂直翻转");
        flipBothItem = new JMenuItem("水平且垂直翻转");
        viewMenu.add(flipHorizontalItem);
        viewMenu.add(flipVerticalItem);
        viewMenu.add(flipBothItem);

        // 添加翻转事件
        flipHorizontalItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawingPanel.flipImageHorizontally();
            }
        });

        flipVerticalItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawingPanel.flipImageVertically();
            }
        });

        flipBothItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawingPanel.flipImageBoth();
            }
        });

        // 创建编辑菜单及其子菜单项
        editMenu = new JMenu("编辑");
        editItem1 = new JMenuItem("剪切");
        editItem2 = new JMenuItem("复制");
        editItem3 = new JMenuItem("粘贴");
        editMenu.add(editItem1);
        editMenu.add(editItem2);
        editMenu.add(editItem3);
        // 将菜单添加到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(editMenu);
    }

    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("打开图片");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("图像文件", ImageIO.getReaderFileSuffixes()));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(selectedFile);
                if (image != null) {
                    drawingPanel.setImage(image);
                } else {
                    JOptionPane.showMessageDialog(this, "所选文件不是有效的图像。", "无效图像", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "加载图像时出错: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveCanvasAsImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存为图片");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG 图片", "png"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // 确保文件名以 .png 结尾
            if (!fileToSave.getAbsolutePath().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }
            try {
                BufferedImage image = drawingPanel.getCanvasImage();
                ImageIO.write(image, "png", fileToSave);
                JOptionPane.showMessageDialog(this, "图片已保存: " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "保存图片失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleWindowClosing() {
        int option = JOptionPane.showConfirmDialog(this, "是否保存当前画布？", "退出", JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            saveCanvasAsImage();
            System.exit(0);
        } else if (option == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        // 如果选择取消，则什么都不做
    }

    // 主方法
    public static void main(String[] args) {
        // 设置全局 UI 缩放比例
        System.setProperty("sun.java2d.uiScale", "1.0");
        // 启用高DPI感知
        System.setProperty("sun.java2d.dpiaware", "true");
        System.setProperty("sun.java2d.ddscale", "true");
        setUIFont(new javax.swing.plaf.FontUIResource("宋体", Font.PLAIN, 14));
        SwingUtilities.invokeLater(() -> {
            new MainWindow();
        });
    }

    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    public void flushText() {
        if (DrawingPanel.getDraggedTextArea() == null) return;
        drawingPanel.textAreaToString();
    }
}
