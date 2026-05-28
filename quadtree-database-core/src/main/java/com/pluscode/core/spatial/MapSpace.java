package com.pluscode.core.spatial;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Stateless quad encoder for a map rectangle. Digits: 1=NW, 2=NE, 3=SW, 4=SE.
 */
public final class MapSpace {

    private final Bounds bounds;
    private final MapSpaceConvention convention;

    private MapSpace(Bounds bounds, MapSpaceConvention convention) {
        this.bounds = Objects.requireNonNull(bounds);
        this.convention = Objects.requireNonNull(convention);
    }

    public static MapSpace of(double minX, double maxX, double minY, double maxY) {
        return new MapSpace(Bounds.of(minX, maxX, minY, maxY), MapSpaceConvention.IMAGE_Y_DOWN);
    }

    public static MapSpace of(Bounds bounds) {
        return new MapSpace(bounds, MapSpaceConvention.IMAGE_Y_DOWN);
    }

    public static MapSpace of(double minX, double maxX, double minY, double maxY, MapSpaceConvention convention) {
        return new MapSpace(Bounds.of(minX, maxX, minY, maxY), convention);
    }

    public static MapSpace earth() {
        return new MapSpace(Bounds.of(-180, 180, -90, 90), MapSpaceConvention.EARTH);
    }

    public Bounds bounds() {
        return bounds;
    }

    public MapSpaceConvention convention() {
        return convention;
    }

    public String encode(double x, double y, int depth) {
        validatePoint(x, y);
        if (depth < 0) {
            throw new IllegalArgumentException("depth must be >= 0");
        }
        if (depth == 0) {
            return "";
        }

        double minX = bounds.minX();
        double maxX = bounds.maxX();
        double minY = bounds.minY();
        double maxY = bounds.maxY();
        StringBuilder code = new StringBuilder(depth);

        for (int i = 0; i < depth; i++) {
            double midX = (minX + maxX) / 2.0;
            double midY = (minY + maxY) / 2.0;
            char digit = digitFor(x, y, midX, midY);
            code.append(digit);
            double[] narrowed = narrowForDigit(digit, minX, maxX, minY, maxY, midX, midY);
            minX = narrowed[0];
            maxX = narrowed[1];
            minY = narrowed[2];
            maxY = narrowed[3];
        }
        return code.toString();
    }

    public Point2D decode(String code) {
        Bounds cell = boundsForCode(code);
        return Point2D.of(cell.centerX(), cell.centerY());
    }

    public Bounds boundsForCode(String code) {
        validateCode(code);
        double minX = bounds.minX();
        double maxX = bounds.maxX();
        double minY = bounds.minY();
        double maxY = bounds.maxY();

        for (int i = 0; i < code.length(); i++) {
            char digit = code.charAt(i);
            double midX = (minX + maxX) / 2.0;
            double midY = (minY + maxY) / 2.0;
            double[] narrowed = narrowForDigit(digit, minX, maxX, minY, maxY, midX, midY);
            minX = narrowed[0];
            maxX = narrowed[1];
            minY = narrowed[2];
            maxY = narrowed[3];
        }
        return Bounds.of(minX, maxX, minY, maxY);
    }

    public String parentCode(String code) {
        if (code == null || code.isEmpty()) {
            return "";
        }
        return code.substring(0, code.length() - 1);
    }

    public String childCode(String parentCode, char digit) {
        validateDigit(digit);
        if (parentCode == null) {
            parentCode = "";
        } else {
            validateCode(parentCode);
        }
        return parentCode + digit;
    }

    public List<String> enumerateCodesInBounds(Bounds region, int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("depth must be >= 0");
        }
        if (!region.intersects(bounds)) {
            return List.of();
        }
        Bounds query = clip(region, bounds);
        if (depth == 0) {
            return List.of("");
        }
        List<String> result = new ArrayList<>();
        enumerateRecursive(query, bounds, "", depth, result);
        return List.copyOf(result);
    }

    public static boolean isValidDigit(char c) {
        return c == '1' || c == '2' || c == '3' || c == '4';
    }

    public static void validateCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("code must not be null");
        }
        for (int i = 0; i < code.length(); i++) {
            validateDigit(code.charAt(i));
        }
    }

    public static void validateDigit(char digit) {
        if (!isValidDigit(digit)) {
            throw new IllegalArgumentException("Invalid code character: " + digit);
        }
    }

    private double[] narrowForDigit(char digit, double minX, double maxX, double minY, double maxY,
                                     double midX, double midY) {
        if (convention == MapSpaceConvention.IMAGE_Y_DOWN) {
            return switch (digit) {
                case '1' -> new double[] {minX, midX, minY, midY};
                case '2' -> new double[] {midX, maxX, minY, midY};
                case '3' -> new double[] {minX, midX, midY, maxY};
                case '4' -> new double[] {midX, maxX, midY, maxY};
                default -> throw new IllegalArgumentException("Invalid digit: " + digit);
            };
        }
        return switch (digit) {
            case '1' -> new double[] {minX, midX, midY, maxY};
            case '2' -> new double[] {midX, maxX, midY, maxY};
            case '3' -> new double[] {minX, midX, minY, midY};
            case '4' -> new double[] {midX, maxX, minY, midY};
            default -> throw new IllegalArgumentException("Invalid digit: " + digit);
        };
    }

    private char digitFor(double x, double y, double midX, double midY) {
        boolean west = x < midX;
        boolean south = y < midY;

        if (convention == MapSpaceConvention.IMAGE_Y_DOWN) {
            if (!south && west) {
                return '1';
            }
            if (!south && !west) {
                return '2';
            }
            if (south && west) {
                return '3';
            }
            return '4';
        }

        if (south && west) {
            return '3';
        }
        if (!south && west) {
            return '1';
        }
        if (south && !west) {
            return '4';
        }
        return '2';
    }

    private void enumerateRecursive(Bounds query, Bounds cell, String prefix, int depthRemaining, List<String> result) {
        if (depthRemaining == 0) {
            if (cell.intersects(query)) {
                result.add(prefix);
            }
            return;
        }

        double midX = cell.midX();
        double midY = cell.midY();

        double[] c1 = narrowForDigit('1', cell.minX(), cell.maxX(), cell.minY(), cell.maxY(), midX, midY);
        double[] c2 = narrowForDigit('2', cell.minX(), cell.maxX(), cell.minY(), cell.maxY(), midX, midY);
        double[] c3 = narrowForDigit('3', cell.minX(), cell.maxX(), cell.minY(), cell.maxY(), midX, midY);
        double[] c4 = narrowForDigit('4', cell.minX(), cell.maxX(), cell.minY(), cell.maxY(), midX, midY);
        tryChild(query, prefix, depthRemaining, result, Bounds.of(c1[0], c1[1], c1[2], c1[3]), '1');
        tryChild(query, prefix, depthRemaining, result, Bounds.of(c2[0], c2[1], c2[2], c2[3]), '2');
        tryChild(query, prefix, depthRemaining, result, Bounds.of(c3[0], c3[1], c3[2], c3[3]), '3');
        tryChild(query, prefix, depthRemaining, result, Bounds.of(c4[0], c4[1], c4[2], c4[3]), '4');
    }

    private void tryChild(Bounds query, String prefix, int depthRemaining, List<String> result, Bounds child, char digit) {
        if (child.intersects(query)) {
            enumerateRecursive(query, child, prefix + digit, depthRemaining - 1, result);
        }
    }

    private static Bounds clip(Bounds region, Bounds space) {
        return Bounds.of(
                Math.max(region.minX(), space.minX()),
                Math.min(region.maxX(), space.maxX()),
                Math.max(region.minY(), space.minY()),
                Math.min(region.maxY(), space.maxY()));
    }

    private void validatePoint(double x, double y) {
        if (!bounds.containsInclusive(x, y)) {
            throw new IllegalArgumentException(
                    "Point (" + x + ", " + y + ") outside map bounds " + bounds);
        }
    }
}
