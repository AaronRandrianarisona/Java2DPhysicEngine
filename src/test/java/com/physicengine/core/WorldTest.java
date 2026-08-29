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
    void fireSpreadsProgressivelyAndConsumesFuel() {
        World world = new World(6, 6);
        world.setCell(2, 2, Material.FIRE);
        world.setCell(2, 3, Material.WOOD);

        world.tick();

        assertEquals(Material.FIRE, world.getCell(2, 3).getMaterial());
        assertEquals(Material.FIRE, world.getCell(2, 2).getMaterial());

        for (int i = 0; i < 8; i++) {
            world.tick();
        }

        assertNotEquals(Material.FIRE, world.getCell(2, 2).getMaterial());
    }

    @Test
    void lavaAndWaterReactVerticallyAndDiagonally() {
        World verticalWorld = new World(6, 6);
        verticalWorld.setCell(2, 1, Material.LAVA);
        verticalWorld.setCell(2, 0, Material.WATER);

        verticalWorld.tick();

        assertTrue(verticalWorld.getCell(2, 0).getMaterial() == Material.STEAM || verticalWorld.getCell(2, 0).getMaterial() == Material.EMPTY);
        assertTrue(verticalWorld.getCell(2, 1).getMaterial() == Material.STONE || verticalWorld.getCell(2, 1).getMaterial() == Material.EMPTY);

        World diagonalWorld = new World(6, 6);
        diagonalWorld.setCell(1, 1, Material.LAVA);
        diagonalWorld.setCell(2, 0, Material.WATER);

        diagonalWorld.tick();

        assertTrue(diagonalWorld.getCell(2, 0).getMaterial() == Material.STEAM || diagonalWorld.getCell(2, 0).getMaterial() == Material.EMPTY);
        assertTrue(diagonalWorld.getCell(1, 1).getMaterial() == Material.STONE || diagonalWorld.getCell(1, 1).getMaterial() == Material.EMPTY);
    }

    @Test
    void worldRejectsNonPositiveDimensions() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new World(0, 5));

        assertEquals("World dimensions must be positive.", exception.getMessage());
    }

    @Test
    void outOfBoundsReadsAndWritesAreIgnored() {
        World world = new World(2, 2);

        world.setCell(-1, 0, Material.SAND);
        world.setCell(0, -1, Material.WATER);
        world.setCell(2, 0, Material.STONE);

        assertEquals(Material.EMPTY, world.getCell(-1, 0).getMaterial());
        assertEquals(Material.EMPTY, world.getCell(0, -1).getMaterial());
        assertEquals(Material.EMPTY, world.getCell(2, 0).getMaterial());
    }

    @Test
    void renderAsciiShowsSymbolsForConfiguredCells() {
        World world = new World(2, 2);
        world.setCell(0, 0, Material.SAND);
        world.setCell(1, 1, Material.WATER);

        String rendered = world.renderAscii();

        assertTrue(rendered.contains("S"));
        assertTrue(rendered.contains("W"));
        assertTrue(rendered.contains("."));
    }
}
