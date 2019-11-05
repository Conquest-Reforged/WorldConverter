package me.dags.relighter;

public class PosIterator {

    private final int radius;
    private final int centerX;
    private final int centerY;
    private final int maxIndex;

    private int i = -1;
    private int dx = 0;
    private int dy = 0;
    private int dirX = 0;
    private int dirY = -1;
    private int length = 0;

    private int posX;
    private int posY;

    public PosIterator(int centerX, int centerY, int radius) {
        this.length = radius + 1 + radius;
        this.maxIndex = length * length - 1;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.posX = centerX;
        this.posY = centerY;
    }

    public int getX() {
        return posX;
    }

    public int getY() {
        return posY;
    }

    public int index() {
        return i;
    }

    public int total() {
        return maxIndex;
    }

    public float progress() {
        return (float) index() / total();
    }

    public boolean hasNext() {
        return i < maxIndex;
    }

    public boolean next() {
        if (hasNext()) {
            if ((-radius <= dx) && (dx <= radius) && (-radius <= dy) && (dy <= radius)) {
                posX = centerX + dx;
                posY = centerY + dy;
            }
            if ((dx == dy) || ((dx < 0) && (dx == -dy)) || ((dx > 0) && (dx == 1 - dy))) {
                length = dirX;
                dirX = -dirY;
                dirY = length;
            }
            dx += dirX;
            dy += dirY;
            i++;
            return true;
        }
        return false;
    }

    public PosIterator reset() {
        this.i = 0;
        this.dx = 0;
        this.dy = 0;
        this.dirX = 0;
        this.dirY = -1;
        this.posX = centerX;
        this.posY = centerY;
        this.length = radius + 1 + radius;
        return this;
    }
}
