package mainwindow;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenu viewMenu;
    private JMenuItem viewItem1;
    private JMenuItem viewItem2;
    private JMenu editMenu;
    private JMenuItem editItem1;
    private JMenuItem editItem2;
    private JMenuItem editItem3;
    private ColorSelectJPanel colorSelectJPanel;
    private JScrollPane scrollPane;
    private ToolPanel toolPanel;

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

    // 构造函数
    public MainWindow() {
        setTitle("Main Window");
        setSize(800, 600);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        width = (int) screenSize.getWidth();
        height = (int) screenSize.getHeight();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        setVisible(true);
    }

    private void createToolPanel(){
        toolPanel = new ToolPanel();
        toolPanel.setBounds(10,10,450,65);
        this.add(toolPanel);
    }

    //创建状态栏
    private void createShowBar() {
        showBar = new ShowBarJPanel();
        showBar.setBounds(10, 10 + height - 200 + 30, width, 40);
        add(showBar);
    }

    //创建颜色选择面板
    private void createColorSelectJPanel() {
        colorSelectJPanel = new ColorSelectJPanel();
        colorSelectJPanel.setBounds(width / 3, 15, 310, 53);
        this.add(colorSelectJPanel);
        ColorSelectJPanel.setCurrentSelectedColor(Color.BLACK);
    }

    //创建绘图面板
    private void createDrawingPanel() {
        drawingPanel = new DrawingPanel(width - 20,height - 230);
        scrollPane = new JScrollPane(drawingPanel);
        scrollPane.setBounds(10, 80, width - 20, height - 230);
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
        // 创建查看菜单及其子菜单项
        viewMenu = new JMenu("查看");
        viewItem1 = new JMenuItem("显示");
        viewItem2 = new JMenuItem("隐藏");
        viewMenu.add(viewItem1);
        viewMenu.add(viewItem2);
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

}