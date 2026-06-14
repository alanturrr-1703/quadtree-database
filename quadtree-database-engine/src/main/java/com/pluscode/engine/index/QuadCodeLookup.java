package com.pluscode.engine.index;

import com.pluscode.core.spatial.Bounds;
import com.pluscode.core.spatial.MapSpace;
import com.pluscode.core.spatial.MapSpaceConvention;

/**
 * Locates the quad code whose cell contains a map point.
 */
public final class QuadCodeLookup {

    private QuadCodeLookup() {
    }

    public static String codeContainingPoint(MapSpace mapSpace, double x, double y, int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("depth must be >= 0");
        }
        if (!mapSpace.bounds().containsInclusive(x, y)) {
            throw new IllegalArgumentException(
                    "Point (" + x + ", " + y + ") outside map bounds " + mapSpace.bounds());
        }
        if (depth == 0) {
            return "";
        }
        return descend(mapSpace, mapSpace.bounds(), x, y, depth, new StringBuilder()).toString();
    }

    private static StringBuilder descend(MapSpace mapSpace, Bounds cell, double x, double y, int remaining,
                                       StringBuilder prefix) {
        if (remaining == 0) {
            return prefix;
        }
        char digit = digitContainingPoint(mapSpace.convention(), cell, x, y);
        Bounds child = childBounds(mapSpace.convention(), cell, digit);
        prefix.append(digit);
        return descend(mapSpace, child, x, y, remaining - 1, prefix);
    }

    private static char digitContainingPoint(MapSpaceConvention convention, Bounds cell, double x, double y) {
        for (char digit : new char[] {'1', '2', '3', '4'}) {
            if (childBounds(convention, cell, digit).containsInclusive(x, y)) {
                return digit;
            }
        }
        throw new IllegalArgumentException(
                "Point (" + x + ", " + y + ") not contained in cell " + cell);
    }

    private static Bounds childBounds(MapSpaceConvention convention, Bounds cell, char digit) {
        double midX = cell.midX();
        double midY = cell.midY();
        if (convention == MapSpaceConvention.IMAGE_Y_DOWN) {
            return switch (digit) {
                case '1' -> Bounds.of(cell.minX(), midX, cell.minY(), midY);
                case '2' -> Bounds.of(midX, cell.maxX(), cell.minY(), midY);
                case '3' -> Bounds.of(cell.minX(), midX, midY, cell.maxY());
                case '4' -> Bounds.of(midX, cell.maxX(), midY, cell.maxY());
                default -> throw new IllegalArgumentException("Invalid digit: " + digit);
            };
        }
        return switch (digit) {
            case '1' -> Bounds.of(cell.minX(), midX, midY, cell.maxY());
            case '2' -> Bounds.of(midX, cell.maxX(), midY, cell.maxY());
            case '3' -> Bounds.of(cell.minX(), midX, cell.minY(), midY);
            case '4' -> Bounds.of(midX, cell.maxX(), cell.minY(), midY);
            default -> throw new IllegalArgumentException("Invalid digit: " + digit);
        };
    }
}
