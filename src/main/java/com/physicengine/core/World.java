package com.physicengine.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class World {
    private final int width;
    private final int height;
    private final Cell[][] grid;
    private final Random random = new Random();
    private final Map<String, Integer> fireTimers = new HashMap<>();

    public World(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("World dimensions must be positive.");
        }
        this.width = width;
        this.height = height;
        this.grid = new Cell[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = Cell.EMPTY;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Cell getCell(int x, int y) {
        if (!inBounds(x, y)) {
            return Cell.EMPTY;
        }
        return grid[y][x];
    }

    public void setCell(int x, int y, Material material) {
        if (!inBounds(x, y)) {
            return;
        }
        grid[y][x] = new Cell(material);
    }

    public void tick() {
        applyEmergentReactions();
        for (int y = height - 2; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                processCell(x, y);
            }
        }
        //applyEmergentReactions();
    }

    private void processCell(int x, int y) {
        Cell cell = grid[y][x];
        if (cell == null || cell.isEmpty()) {
            return;
        }

        Material material = cell.getMaterial();
        switch (material) {
            case SAND:
                moveSand(x, y);
                break;
            case WATER:
                moveWater(x, y);
                break;
            case FIRE:
                burnOrSpreadFire(x, y);
                break;
            case LAVA:
                moveLava(x, y);
                break;
            case STEAM:
                moveSteam(x, y);
                break;
            case ICE:
                meltIce(x, y);
                break;
            case ACID:
                dissolveNearby(x, y);
                break;
            case POWDER:
                movePowder(x, y);
                break;
            case PLANT:
                growPlant(x, y);
                break;
            case GAS:
                moveGas(x, y);
                break;
            case SMOKE:
                moveSmoke(x, y);
                break;
            case STONE:
            case WOOD:
            case METAL:
            case EMPTY:
                break;
            default:
                break;
        }
    }

    private void applyEmergentReactions() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Material material = grid[y][x].getMaterial();

                if (material == Material.FIRE) {
                    int life = fireTimers.getOrDefault(key(x, y), 7);
                    if (random.nextInt(100) < 22) {
                        life--;
                    }

                    if (life <= 0) {
                        grid[y][x] = random.nextInt(100) < 80 ? new Cell(Material.SMOKE) : Cell.EMPTY;
                        fireTimers.remove(key(x, y));
                        continue;
                    }

                    fireTimers.put(key(x, y), life);
                }

                if (material == Material.LAVA) {
                    for (int[] offset : neighborOffsets()) {
                        int nx = x + offset[0];
                        int ny = y + offset[1];
                        if (inBounds(nx, ny) && grid[ny][nx].getMaterial() == Material.WATER) {
                            grid[ny][nx] = new Cell(Material.STEAM);
                            grid[y][x] = new Cell(Material.STONE);
                            fireTimers.remove(key(x, y));
                            break;
                        }
                    }
                }

                if (material == Material.ACID) {
                    for (int[] offset : neighborOffsets()) {
                        int nx = x + offset[0];
                        int ny = y + offset[1];
                        if (inBounds(nx, ny) && grid[ny][nx].getMaterial() == Material.WOOD) {
                            grid[ny][nx] = Cell.EMPTY;
                        }
                    }
                }

                if (material == Material.POWDER) {
                    for (int[] offset : neighborOffsets()) {
                        int nx = x + offset[0];
                        int ny = y + offset[1];
                        if (inBounds(nx, ny) && grid[ny][nx].getMaterial() == Material.FIRE) {
                            grid[y][x] = new Cell(Material.FIRE);
                            fireTimers.put(key(x, y), 7);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void burnOrSpreadFire(int x, int y) {
        int[][] neighbors = neighborOffsets();
        for (int[] n : neighbors) {
            int nx = x + n[0];
            int ny = y + n[1];
            if (inBounds(nx, ny)) {
                Material neighbor = grid[ny][nx].getMaterial();
                if ((neighbor == Material.WOOD || neighbor == Material.PLANT || neighbor == Material.POWDER) && random.nextInt(100) < 30) {
                    grid[ny][nx] = new Cell(Material.FIRE);
                    fireTimers.put(key(nx, ny), 7 + random.nextInt(4));
                    if (ny > 0 && grid[ny - 1][nx].isEmpty()) {
                        grid[ny - 1][nx] = new Cell(Material.SMOKE);
                    }
                }
            }
        }
    }

    private void moveSand(int x, int y) {
        // Sink in water
        if (inBounds(x, y + 1) && grid[y + 1][x].getMaterial() == Material.WATER) {
            swapCells(x, y, x, y + 1);
            return;
        }
        
        // Fall through empty space
        if (canMove(x, y + 1)) {
            swapCells(x, y, x, y + 1);
            return;
        }

        int dir = random.nextBoolean() ? -1 : 1;
        if (canMove(x + dir, y + 1)) {
            swapCells(x, y, x + dir, y + 1);
            return;
        }
        if (canMove(x - dir, y + 1)) {
            swapCells(x, y, x - dir, y + 1);
        }
    }

    private void moveWater(int x, int y) {
        if (canMove(x, y + 1)) { 
            swapCells(x, y, x, y + 1);
            return;
        }

        int[][] diagonalMoves = {{-1, 1}, {1, 1}, {-1, 0}, {1, 0}};
        shuffleDiagonalMoves(diagonalMoves);
        for (int[] move : diagonalMoves) {
            int nx = x + move[0];
            int ny = y + move[1];
            if (canMove(nx, ny)) {
                swapCells(x, y, nx, ny);
                return;
            }
        }
    }

    private void movePowder(int x, int y) {
        if (canMove(x, y + 1)) {
            swapCells(x, y, x, y + 1);
            return;
        }
        int[] directions = {-1, 1};
        shuffle(directions);
        for (int offset : directions) {
            if (canMove(x + offset, y + 1)) {
                swapCells(x, y, x + offset, y + 1);
                return;
            }
        }
    }

    private void moveLava(int x, int y) {
        if (canMove(x, y + 1)) {
            swapCells(x, y, x, y + 1);
            return;
        }

        int[][] diagonalMoves = {{-1, 1}, {1, 1}, {-1, 0}, {1, 0}, {0, 1}};
        shuffleDiagonalMoves(diagonalMoves);
        for (int[] move : diagonalMoves) {
            int nx = x + move[0];
            int ny = y + move[1];
            if (inBounds(nx, ny)) {
                Material target = grid[ny][nx].getMaterial();
                if (target == Material.WATER) {
                    grid[ny][nx] = new Cell(Material.STEAM);
                    grid[y][x] = new Cell(Material.STONE);
                    return;
                }
                if (target == Material.EMPTY || target == Material.SAND) {
                    swapCells(x, y, nx, ny);
                    return;
                }
            }
        }

        if (random.nextInt(1000) < 1) {
            grid[y][x] = new Cell(Material.STONE);
        }
    }

    private void moveSteam(int x, int y) {
        // Rise upward gradually - only 60% of the time
        if (canMove(x, y - 1) && random.nextInt(100) < 60) {
            swapCells(x, y, x, y - 1);
            return;
        }
        
        // Spread horizontally while rising is blocked or paused
        if (random.nextInt(100) < 40) {
            int[] directions = {-1, 1};
            shuffle(directions);
            for (int dir : directions) {
                int targetX = x + dir;
                if (inBounds(targetX, y) && canMove(targetX, y)) {
                    swapCells(x, y, targetX, y);
                    return;
                }
            }
        }
        
        // Condense into water when cooling or reaching ceiling - very slowly
        if (y < 2 || random.nextInt(100) < 5) {
            grid[y][x] = new Cell(Material.WATER);
        }
    }

    private void moveGas(int x, int y) {
        if (canMove(x, y - 1)) {
            swapCells(x, y, x, y - 1);
            return;
        }
        if (random.nextInt(100) < 15) {
            int dir = random.nextBoolean() ? -1 : 1;
            if (canMove(x + dir, y)) {
                swapCells(x, y, x + dir, y);
            }
        }
    }

    private void moveSmoke(int x, int y) {
        // Rise slowly and drift
        if (canMove(x, y - 1) && random.nextInt(100) < 70) {
            swapCells(x, y, x, y - 1);
            return;
        }
        
        // Drift horizontally
        if (random.nextInt(100) < 35) {
            int dir = random.nextBoolean() ? -1 : 1;
            if (canMove(x + dir, y)) {
                swapCells(x, y, x + dir, y);
                return;
            }
        }
        
        // Dissipate
        if (random.nextInt(100) < 8) {
            grid[y][x] = Cell.EMPTY;
        }
    }

    private void dissolveNearby(int x, int y) {
        int[][] neighbors = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] n : neighbors) {
            int nx = x + n[0];
            int ny = y + n[1];
            if (!inBounds(nx, ny)) {
                continue;
            }
            Material neighbor = grid[ny][nx].getMaterial();
            if (neighbor == Material.WOOD || neighbor == Material.PLANT || neighbor == Material.STONE || neighbor == Material.METAL) {
                grid[ny][nx] = Cell.EMPTY;
            }
        }
        if (random.nextInt(100) < 10) {
            grid[y][x] = Cell.EMPTY;
        }
    }

    private void growPlant(int x, int y) {
        if (random.nextInt(100) < 20) {
            int[][] neighbors = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            for (int[] n : neighbors) {
                int nx = x + n[0];
                int ny = y + n[1];
                if (inBounds(nx, ny) && grid[ny][nx].getMaterial() == Material.WATER) {
                    grid[ny][nx] = new Cell(Material.PLANT);
                    return;
                }
            }
        }
    }

    private void meltIce(int x, int y) {
        if (random.nextInt(100) < 8) {
            grid[y][x] = new Cell(Material.WATER);
        }
    }

    private boolean canMove(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }
        return grid[y][x].isEmpty();
    }

    private void swapCells(int x1, int y1, int x2, int y2) {
        Cell temp = grid[y1][x1];
        grid[y1][x1] = grid[y2][x2];
        grid[y2][x2] = temp;
    }

    private void shuffle(int[] values) {
        for (int i = values.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = values[i];
            values[i] = values[j];
            values[j] = temp;
        }
    }

    private void shuffleDiagonalMoves(int[][] moves) {
        for (int i = moves.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = moves[i];
            moves[i] = moves[j];
            moves[j] = temp;
        }
    }

    private int[][] neighborOffsets() {
        return new int[][] {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
    }

    private String key(int x, int y) {
        return x + "," + y;
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public String renderAscii() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < width; x++) {
                row.append(symbolFor(grid[y][x].getMaterial()));
            }
            sb.append(row).append('\n');
        }
        return sb.toString();
    }

    private char symbolFor(Material material) {
        switch (material) {
            case EMPTY:
                return '.';
            case SAND:
                return 'S';
            case WATER:
                return 'W';
            case STONE:
                return '#';
            case WOOD:
                return 'T';
            case FIRE:
                return 'F';
            case LAVA:
                return 'L';
            case STEAM:
                return 'V';
            case ICE:
                return 'I';
            case ACID:
                return 'A';
            case METAL:
                return 'M';
            case POWDER:
                return 'P';
            case PLANT:
                return 'G';
            case GAS:
                return 'X';
            default:
                return '?';
        }
    }
}
