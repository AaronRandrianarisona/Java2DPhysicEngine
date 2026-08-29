package com.physicengine;

import com.physicengine.core.Material;
import com.physicengine.core.World;

public class Main {
    public static void main(String[] args) {
        World world = new World(100, 35);
        seedWorld(world);

        if (args.length > 0 && "ascii".equalsIgnoreCase(args[0])) {
            System.out.println("Initial world:");
            System.out.println(world.renderAscii());
            for (int i = 0; i < 12; i++) {
                world.tick();
                System.out.println("Tick " + (i + 1) + ":");
                System.out.println(world.renderAscii());
            }
            return;
        }

        SandboxFrame.showSandbox(world);
    }

    private static void seedWorld(World world) {
        for (int x = 0; x < world.getWidth(); x++) {
            world.setCell(x, world.getHeight() - 1, Material.STONE);
        }

        // Sand column
        for (int x = 5; x < 15; x++) {
            world.setCell(x, 8, Material.SAND);
        }
        
        // Water pool with lava interaction
        for (int x = 20; x < 30; x++) {
            world.setCell(x, 10, Material.WATER);
        }
        
        // Lava flow zone - watch steam spread and condense
        for (int y = 3; y < 8; y++) {
            world.setCell(33, y, Material.LAVA);
        }
        
        // Wood and fire
        for (int x = 40; x < 50; x++) {
            world.setCell(x, 10, Material.WOOD);
        }
        world.setCell(45, 4, Material.FIRE);
        
        // Ice
        world.setCell(10, 2, Material.ICE);
        
        // Acid and plant zones
        for (int x = 55; x < 59; x++) {
            world.setCell(x, 8, Material.ACID);
        }
        for (int x = 55; x < 59; x++) {
            world.setCell(x, 10, Material.PLANT);
        }
    }
}
