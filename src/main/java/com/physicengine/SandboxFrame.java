package com.physicengine;

import com.physicengine.core.Cell;
import com.physicengine.core.Material;
import com.physicengine.core.World;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SandboxFrame extends JFrame {
    private static final int CELL_SIZE = 20;
    private final World world;
    private Material selectedMaterial = Material.SAND;
    private final JPanel worldPanel;

    public SandboxFrame(World world) {
        super("Pixel Sandbox Physics Engine");
        this.world = world;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Material material : Material.values()) {
            if (material == Material.EMPTY) {
                continue;
            }
            JButton button = new JButton(material.name());
            button.setBackground(colorFor(material));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> selectedMaterial = material);
            toolbar.add(button);
        }

        JButton eraseButton = new JButton("Erase");
        eraseButton.addActionListener(e -> selectedMaterial = Material.EMPTY);
        toolbar.add(eraseButton);

        JLabel status = new JLabel(" Selected: SAND ");
        toolbar.add(status);

        worldPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintWorld(g);
            }
        };

        worldPanel.setPreferredSize(new Dimension(world.getWidth() * CELL_SIZE, world.getHeight() * CELL_SIZE));
        worldPanel.setBackground(Color.BLACK);
        worldPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / CELL_SIZE;
                int y = e.getY() / CELL_SIZE;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    world.setCell(x, y, selectedMaterial);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    world.setCell(x, y, Material.EMPTY);
                }
                worldPanel.repaint();
                status.setText(" Selected: " + selectedMaterial.name() + " ");
            }
        });

        add(toolbar, BorderLayout.NORTH);
        add(worldPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);

        Timer timer = new Timer(80, e -> {
            world.tick();
            worldPanel.repaint();
        });
        timer.start();
    }

    private void paintWorld(Graphics g) {
        for (int y = 0; y < world.getHeight(); y++) {
            for (int x = 0; x < world.getWidth(); x++) {
                Cell cell = world.getCell(x, y);
                g.setColor(colorFor(cell.getMaterial()));
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private Color colorFor(Material material) {
        switch (material) {
            case EMPTY:
                return Color.BLACK;
            case SAND:
                return new Color(214, 175, 79);
            case WATER:
                return new Color(60, 120, 220);
            case STONE:
                return new Color(125, 125, 125);
            case WOOD:
                return new Color(128, 80, 40);
            case FIRE:
                return new Color(255, 120, 30);
            case LAVA:
                return new Color(180, 30, 20);
            case STEAM:
                return new Color(170, 220, 255);
            case ICE:
                return new Color(120, 220, 255);
            case ACID:
                return new Color(100, 200, 50);
            case METAL:
                return new Color(180, 180, 180);
            case POWDER:
                return new Color(240, 200, 100);
            case PLANT:
                return new Color(100, 180, 50);
            case GAS:
                return new Color(200, 200, 200);
            case SMOKE:
                return new Color(80, 80, 80);
            default:
                return Color.BLACK;
        }
    }

    public static void showSandbox(World world) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Headless environment detected. Rendering is unavailable in this terminal session.");
            System.out.println(world.renderAscii());
            return;
        }

        SwingUtilities.invokeLater(() -> {
            SandboxFrame frame = new SandboxFrame(world);
            frame.setVisible(true);
        });
    }
}
