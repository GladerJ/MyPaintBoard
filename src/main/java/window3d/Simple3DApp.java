package window3d;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Simple3DApp extends JFrame implements ActionListener {
    private Canvas3D canvas;
    private SimpleUniverse universe;
    private BranchGroup scene;
    private TransformGroup objTransform;
    private Color currentColor = Color.WHITE; // 默认颜色
    private String currentShape = ""; // 当前形状类型
    private boolean isSpecularEnabled = true; // 用于跟踪高光是否启用

    public Simple3DApp() {
        // 设置窗口标题
        setTitle("Simple3DApp");

        // 创建Canvas3D对象
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        getContentPane().add(canvas, BorderLayout.CENTER);

        // 创建SimpleUniverse对象
        universe = new SimpleUniverse(canvas);

        // 创建BranchGroup对象作为场景图的根节点
        scene = new BranchGroup();
        scene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND); // 设置允许添加子节点的能力
        scene.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);  // 设置允许写子节点的能力
        objTransform = new TransformGroup();
        objTransform.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        objTransform.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        objTransform.setCapability(TransformGroup.ALLOW_CHILDREN_READ); // 添加读取能力
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); // 允许变换写入
        scene.addChild(objTransform);

        // 设置观察者位置
        universe.getViewingPlatform().setNominalViewingTransform();

        // 添加场景到SimpleUniverse
        universe.addBranchGraph(scene);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();

        // 添加形状菜单
        JMenu shapeMenu = new JMenu("添加形状");
        JMenuItem addCube = new JMenuItem("添加立方体");
        JMenuItem addSphere = new JMenuItem("添加球体");

        addCube.addActionListener(this);
        addSphere.addActionListener(this);

        shapeMenu.add(addCube);
        shapeMenu.add(addSphere);
        menuBar.add(shapeMenu);

        // 添加颜色菜单
        JMenu colorMenu = new JMenu("设置颜色");
        JMenuItem redColor = new JMenuItem("红色");
        JMenuItem greenColor = new JMenuItem("绿色");
        JMenuItem blueColor = new JMenuItem("蓝色");

        redColor.addActionListener(this);
        greenColor.addActionListener(this);
        blueColor.addActionListener(this);

        colorMenu.add(redColor);
        colorMenu.add(greenColor);
        colorMenu.add(blueColor);
        menuBar.add(colorMenu);

        // 添加高光菜单
        JMenu specularMenu = new JMenu("高光");
        JMenuItem enableSpecular = new JMenuItem("添加高光");
        JMenuItem disableSpecular = new JMenuItem("取消高光");

        enableSpecular.addActionListener(this);
        disableSpecular.addActionListener(this);

        specularMenu.add(enableSpecular);
        specularMenu.add(disableSpecular);
        menuBar.add(specularMenu);

        setJMenuBar(menuBar);

        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        // 添加鼠标旋转行为
        addMouseRotateBehavior();

        // 添加默认光照
        addDefaultLight();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("添加立方体")) {
            clearObjects();
            addCube();
            currentShape = "cube";
        } else if (command.equals("添加球体")) {
            clearObjects();
            addSphere();
            currentShape = "sphere";
        } else if (command.equals("红色")) {
            currentColor = Color.RED;
            updateShapeColor();
        } else if (command.equals("绿色")) {
            currentColor = Color.GREEN;
            updateShapeColor();
        } else if (command.equals("蓝色")) {
            currentColor = Color.BLUE;
            updateShapeColor();
        } else if (command.equals("添加高光")) {
            enableSpecular();
        } else if (command.equals("取消高光")) {
            disableSpecular();
        }
    }

    private void clearObjects() {
        // 使用临时数组来存储子节点
        Node[] children = new Node[objTransform.numChildren()];
        for (int i = 0; i < children.length; i++) {
            children[i] = objTransform.getChild(i);
        }

        // 移除objTransform中的所有子节点
        for (Node child : children) {
            if (child instanceof BranchGroup) {
                ((BranchGroup) child).detach();
            }
            objTransform.removeChild(child);
        }
    }

    private void addCube() {
        Appearance appearance = createAppearance();
        Box cube = new Box(0.3f, 0.3f, 0.3f, Box.GENERATE_NORMALS, appearance);
        Transform3D transform = new Transform3D();
        TransformGroup tg = new TransformGroup(transform);
        tg.addChild(cube);

        BranchGroup bg = new BranchGroup();
        bg.setCapability(BranchGroup.ALLOW_DETACH); // 设置允许分离的能力
        bg.addChild(tg);
        objTransform.addChild(bg);
    }

    private void addSphere() {
        Appearance appearance = createAppearance();
        Sphere sphere = new Sphere(0.3f, Sphere.GENERATE_NORMALS, 80, appearance);
        Transform3D transform = new Transform3D();
        TransformGroup tg = new TransformGroup(transform);
        tg.addChild(sphere);

        BranchGroup bg = new BranchGroup();
        bg.setCapability(BranchGroup.ALLOW_DETACH); // 设置允许分离的能力
        bg.addChild(tg);
        objTransform.addChild(bg);
    }

    private Appearance createAppearance() {
        Appearance appearance = new Appearance();
        ColoringAttributes colorAttr = new ColoringAttributes();
        colorAttr.setColor(new Color3f(currentColor));
        appearance.setColoringAttributes(colorAttr);

        // 设置材质以启用高光
        Material material = new Material();
        material.setDiffuseColor(new Color3f(currentColor)); // 漫反射颜色
        if (isSpecularEnabled) {
            material.setSpecularColor(new Color3f(Color.WHITE)); // 高光颜色
            material.setShininess(64.0f); // 高光强度
        } else {
            material.setSpecularColor(new Color3f(0.0f, 0.0f, 0.0f)); // 取消高光
            material.setShininess(0.0f);
        }
        appearance.setMaterial(material);

        return appearance;
    }

    private void updateShapeColor() {
        if (currentShape.equals("sphere")) {
            clearObjects();
            addSphere();
        } else if (currentShape.equals("cube")) {
            clearObjects();
            addCube();
        }
    }

    private void addMouseRotateBehavior() {
        MouseRotate mouseRotate = new MouseRotate();
        mouseRotate.setTransformGroup(objTransform);
        mouseRotate.setSchedulingBounds(new BoundingSphere());

        BranchGroup behaviorBranch = new BranchGroup();
        behaviorBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND); // 设置允许添加子节点的能力
        behaviorBranch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);  // 设置允许写子节点的能力
        behaviorBranch.addChild(mouseRotate);
        scene.addChild(behaviorBranch);
    }

    private void addDefaultLight() {
        BranchGroup lightGroup = new BranchGroup();
        lightGroup.setCapability(BranchGroup.ALLOW_DETACH);

        // 添加定向光
        DirectionalLight directionalLight = new DirectionalLight(new Color3f(Color.WHITE), new Vector3f(-1.0f, -1.0f, -1.0f));
        directionalLight.setInfluencingBounds(new BoundingSphere());
        lightGroup.addChild(directionalLight);

        // 添加环境光
        AmbientLight ambientLight = new AmbientLight(new Color3f(Color.WHITE));
        ambientLight.setInfluencingBounds(new BoundingSphere());
        lightGroup.addChild(ambientLight);

        scene.addChild(lightGroup);
    }

    private void enableSpecular() {
        if (!isSpecularEnabled) {
            isSpecularEnabled = true;
            updateShapeColor();
        }
    }

    private void disableSpecular() {
        if (isSpecularEnabled) {
            isSpecularEnabled = false;
            updateShapeColor();
        }
    }

    public static void main(String[] args) {
        new Simple3DApp();
    }
}
