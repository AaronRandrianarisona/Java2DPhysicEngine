package com.physicengine.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorldTest {
    @Test
    void sandFallsDownward() {
        World world = new World(5, 5);
        world.setCell(2, 0, Material.SAND);

        world.tick();

        assertEquals(Material.EMPTY, world.getCell(2, 0).getMaterial());
        assertEquals(Material.SAND, world.getCell(2, 1).getMaterial());
    }

    @Test
    void waterSettlesBelow() {
        World world = new World(5, 5);
        world.setCell(2, 0, Material.WATER);

        world.tick();

        assertEquals(Material.EMPTY, world.getCell(2, 0).getMaterial());
        assertEquals(Material.WATER, world.getCell(2, 1).getMaterial());
    }

    @Test
    void stoneDoesNotMove() {
        World world = new World(5, 5);
        world.setCell(2, 2, Material.STONE);

        world.tick();

        assertEquals(Material.STONE, world.getCell(2, 2).getMaterial());
    }

    @Test
    void fireBurnsOutOverTime() {
        World world = new World(5, 5);
        world.setCell(2, 2, Material.FIRE);

        world.tick();

        assertNotEquals(Material.FIRE, world.getCell(2, 2).getMaterial());
    }
}
