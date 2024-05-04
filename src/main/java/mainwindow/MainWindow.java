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

    // 构造函数
    public MainWindow() {
        setTitle("Main Window");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        menuBar = new JMenuBar();
        // 创建菜单
        createMenuBar();
        // 将菜单栏设置到窗口
        setJMenuBar(menuBar);
        setLayout(new BorderLayout());
        ColorSelectJPanel csj = new ColorSelectJPanel();
        add(csj,BorderLayout.CENTER);
        setVisible(true);
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
        SwingUtilities.invokeLater(() -> {
            new MainWindow();
        });
    }
}
