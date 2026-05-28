package com.pluscode.core.spatial;

/**
 * Immutable 2D point in map coordinates.
 */
public record Point2D(double x, double y) {

    public static Point2D of(double x, double y) {
        return new Point2D(x, y);
    }
}
