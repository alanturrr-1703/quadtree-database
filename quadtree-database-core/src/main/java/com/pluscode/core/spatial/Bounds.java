package com.pluscode.core.spatial;

/**
 * Axis-aligned rectangle. Implement validation and spatial queries.
 *
 * @see docs/REQUIREMENTS.md#bounds
 */
public final class Bounds {

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    public Bounds(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        if (minX >= maxX || minY >= maxY) {
            throw new IllegalArgumentException("Invalid bounds: require minX < maxX and minY < maxY");
        }
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
        throw new UnsupportedOperationException("TODO: implement width");
    }

    public double height() {
        throw new UnsupportedOperationException("TODO: implement height");
    }

    public double midX() {
        throw new UnsupportedOperationException("TODO: implement midX");
    }

    public double midY() {
        throw new UnsupportedOperationException("TODO: implement midY");
    }

    public double centerX() {
        throw new UnsupportedOperationException("TODO: implement centerX");
    }

    public double centerY() {
        throw new UnsupportedOperationException("TODO: implement centerY");
    }

    public boolean contains(double x, double y) {
        throw new UnsupportedOperationException("TODO: implement contains (half-open: max edge exclusive)");
    }

    public boolean containsInclusive(double x, double y) {
        throw new UnsupportedOperationException("TODO: implement containsInclusive");
    }

    public boolean intersects(Bounds other) {
        throw new UnsupportedOperationException("TODO: implement intersects");
    }

    public Bounds union(Bounds other) {
        throw new UnsupportedOperationException("TODO: implement union");
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("TODO: implement equals");
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("TODO: implement hashCode");
    }

    @Override
    public String toString() {
        return "Bounds[" + minX + "," + maxX + " x " + minY + "," + maxY + "]";
    }
}
