package gladerUI;

import javax.swing.*;
import java.awt.*;

public class FontChooser extends JDialog {
    private JComboBox<String> fontComboBox;
    private JComboBox<Integer> sizeComboBox;
    private boolean confirmed;

    public FontChooser(Frame owner) {
        super(owner, "选择字体和字号", true);
        setLayout(new BorderLayout());

        // 字体选择
        fontComboBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        add(fontComboBox, BorderLayout.NORTH);

        // 字号选择
        Integer[] sizes = {8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 40, 48, 56, 64, 72};
        sizeComboBox = new JComboBox<>(sizes);
        add(sizeComboBox, BorderLayout.CENTER);

        // 确定和取消按钮
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("确定");
        okButton.addActionListener(e -> {
            confirmed = true;
            setVisible(false);
        });
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> setVisible(false));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getSelectedFont() {
        return (String) fontComboBox.getSelectedItem();
    }

    public int getSelectedSize() {
        return (int) sizeComboBox.getSelectedItem();
    }
}
