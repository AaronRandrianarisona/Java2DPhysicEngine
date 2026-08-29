package com.physicengine.core;

public class Cell {
    public static final Cell EMPTY = new Cell(Material.EMPTY);

    private final Material material;

    public Cell(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isEmpty() {
        return material == Material.EMPTY;
    }
}
