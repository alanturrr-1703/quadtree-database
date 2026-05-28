package com.pluscode.core.spatial;

import java.util.Objects;

/**
 * Axis-aligned rectangle in map space (x horizontal, y vertical).
 */
public final class Bounds {

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    public Bounds(double minX, double maxX, double minY, double maxY) {
        if (minX >= maxX || minY >= maxY) {
            throw new IllegalArgumentException("Invalid bounds: require minX < maxX and minY < maxY");
        }
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public static Bounds of(double minX, double maxX, double minY, double maxY) {
        return new Bounds(minX, maxX, minY, maxY);
    }

    public double minX() {
        return minX;
    }

    public double maxX() {
        return maxX;
    }

    public double minY() {
        return minY;
    }

    public double maxY() {
        return maxY;
    }

    public double width() {
        return maxX - minX;
    }

    public double height() {
        return maxY - minY;
    }

    public double midX() {
        return (minX + maxX) / 2.0;
    }

    public double midY() {
        return (minY + maxY) / 2.0;
    }

    public double centerX() {
        return midX();
    }

    public double centerY() {
        return midY();
    }

    public boolean contains(double x, double y) {
        return x >= minX && x < maxX && y >= minY && y < maxY;
    }

    public boolean containsInclusive(double x, double y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    public boolean intersects(Bounds other) {
        return minX < other.maxX && maxX > other.minX && minY < other.maxY && maxY > other.minY;
    }

    public Bounds union(Bounds other) {
        return new Bounds(
                Math.min(minX, other.minX),
                Math.max(maxX, other.maxX),
                Math.min(minY, other.minY),
                Math.max(maxY, other.maxY));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bounds bounds)) {
            return false;
        }
        return Double.compare(minX, bounds.minX) == 0
                && Double.compare(maxX, bounds.maxX) == 0
                && Double.compare(minY, bounds.minY) == 0
                && Double.compare(maxY, bounds.maxY) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, maxX, minY, maxY);
    }

    @Override
    public String toString() {
        return "Bounds[" + minX + "," + maxX + " x " + minY + "," + maxY + "]";
    }
}
