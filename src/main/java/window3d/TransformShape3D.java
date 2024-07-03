import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransformShape3D extends JFrame {

    private TransformGroup objTransform;
    private Transform3D transform3D;

    public TransformShape3D() {
        // 设置简单的 JFrame 窗口
        setTitle("Java 3D Example");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建简单的 Java 3D Canvas
        Canvas3D canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(canvas3D, BorderLayout.CENTER);

        // 创建一个简单的场景图并将其添加到 Universe 中
        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

        // 设置观察者的视点
        simpleU.getViewingPlatform().setNominalViewingTransform();

        // 创建场景分支组
        BranchGroup scene = createSceneGraph();

        // 将分支组编译
        scene.compile();

        // 将场景分支组添加到 SimpleUniverse 中
        simpleU.addBranchGraph(scene);

        // 添加控制面板
        addControlPanel();
    }

    public BranchGroup createSceneGraph() {
        // 创建根分支组节点
        BranchGroup objRoot = new BranchGroup();

        // 创建一个变换组节点，用于进行缩放、旋转、平移等操作
        objTransform = new TransformGroup();
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        // 将变换组节点添加到根分支组节点
        objRoot.addChild(objTransform);

        // 创建一个球体对象
        Sphere sphere = new Sphere(0.5f);

        // 将球体对象添加到变换组节点
        objTransform.addChild(sphere);

        // 创建一个颜色属性
        ColoringAttributes colorAttr = new ColoringAttributes();
        colorAttr.setColor(new Color3f(0.2f, 0.2f, 0.2f));

        // 创建光照对象
        AmbientLight ambientLight = new AmbientLight(true, new Color3f(Color.WHITE));
        ambientLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        objRoot.addChild(ambientLight);

        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        objRoot.addChild(directionalLight);

        // 初始化 Transform3D 对象
        transform3D = new Transform3D();
        objTransform.setTransform(transform3D);

        return objRoot;
    }

    private void addControlPanel() {
        JPanel controlPanel = new JPanel(new GridLayout(4, 3));

        // 缩放比例输入框
        controlPanel.add(new JLabel("Scale:"));
        JTextField scaleField = new JTextField("1.0");
        controlPanel.add(scaleField);
        JButton scaleButton = new JButton("Apply");
        controlPanel.add(scaleButton);

        // 旋转角度输入框
        controlPanel.add(new JLabel("Rotate (x y z angle):"));
        JTextField rotateField = new JTextField("0 0 1 0");
        controlPanel.add(rotateField);
        JButton rotateButton = new JButton("Apply");
        controlPanel.add(rotateButton);

        // 平移输入框
        controlPanel.add(new JLabel("Translate (x y z):"));
        JTextField translateField = new JTextField("0 0 0");
        controlPanel.add(translateField);
        JButton translateButton = new JButton("Apply");
        controlPanel.add(translateButton);

        getContentPane().add(controlPanel, BorderLayout.SOUTH);

        // 添加缩放按钮事件
        scaleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double scaleFactor = Double.parseDouble(scaleField.getText());
                    scale(scaleFactor);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid scale factor");
                }
            }
        });

        // 添加旋转按钮事件
        rotateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String[] values = rotateField.getText().split(" ");
                    if (values.length != 4) throw new NumberFormatException();
                    double x = Double.parseDouble(values[0]);
                    double y = Double.parseDouble(values[1]);
                    double z = Double.parseDouble(values[2]);
                    double angle = Double.parseDouble(values[3]);
                    rotate(angle, new Vector3d(x, y, z));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid rotation values");
                }
            }
        });

        // 添加平移按钮事件
        translateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String[] values = translateField.getText().split(" ");
                    if (values.length != 3) throw new NumberFormatException();
                    double x = Double.parseDouble(values[0]);
                    double y = Double.parseDouble(values[1]);
                    double z = Double.parseDouble(values[2]);
                    translate(new Vector3d(x, y, z));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid translation values");
                }
            }
        });
    }

    public void scale(double scaleFactor) {
        Transform3D scaleTransform = new Transform3D();
        scaleTransform.setScale(scaleFactor);
        transform3D.mul(scaleTransform);
        objTransform.setTransform(transform3D);
    }

    public void rotate(double angle, Vector3d axis) {
        Transform3D rotateTransform = new Transform3D();
        rotateTransform.set(new AxisAngle4d(axis, angle));
        transform3D.mul(rotateTransform);
        objTransform.setTransform(transform3D);
    }

    public void translate(Vector3d vector) {
        Transform3D translateTransform = new Transform3D();
        translateTransform.setTranslation(vector);
        transform3D.mul(translateTransform);
        objTransform.setTransform(transform3D);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TransformShape3D app = new TransformShape3D();
                app.setVisible(true);
            }
        });
    }
}
