package com.pluscode.core.spatial;

import java.util.List;

/**
 * Stateless quad encoder for a map rectangle. Digits: 1=NW, 2=NE, 3=SW, 4=SE.
 *
 * @see docs/REQUIREMENTS.md#mapspace
 */
public final class MapSpace {

    private final Bounds bounds;
    private final MapSpaceConvention convention;

    private MapSpace(Bounds bounds, MapSpaceConvention convention) {
        this.bounds = bounds;
        this.convention = convention;
    }

    public static MapSpace of(double minX, double maxX, double minY, double maxY) {
        throw new UnsupportedOperationException("TODO: implement of(minX, maxX, minY, maxY)");
    }

    public static MapSpace of(Bounds bounds) {
        throw new UnsupportedOperationException("TODO: implement of(Bounds)");
    }

    public static MapSpace of(double minX, double maxX, double minY, double maxY, MapSpaceConvention convention) {
        throw new UnsupportedOperationException("TODO: implement of(..., convention)");
    }

    public static MapSpace earth() {
        throw new UnsupportedOperationException("TODO: implement earth (lon [-180,180], lat [-90,90], EARTH convention)");
    }

    public Bounds bounds() {
        return bounds;
    }

    public MapSpaceConvention convention() {
        return convention;
    }

    public String encode(double x, double y, int depth) {
        throw new UnsupportedOperationException("TODO: implement encode");
    }

    public Point2D decode(String code) {
        throw new UnsupportedOperationException("TODO: implement decode");
    }

    public Bounds boundsForCode(String code) {
        throw new UnsupportedOperationException("TODO: implement boundsForCode");
    }

    public String parentCode(String code) {
        throw new UnsupportedOperationException("TODO: implement parentCode");
    }

    public String childCode(String parentCode, char digit) {
        throw new UnsupportedOperationException("TODO: implement childCode");
    }

    public List<String> enumerateCodesInBounds(Bounds region, int depth) {
        throw new UnsupportedOperationException("TODO: implement enumerateCodesInBounds");
    }

    public static boolean isValidDigit(char c) {
        throw new UnsupportedOperationException("TODO: implement isValidDigit");
    }

    public static void validateCode(String code) {
        throw new UnsupportedOperationException("TODO: implement validateCode");
    }

    public static void validateDigit(char digit) {
        throw new UnsupportedOperationException("TODO: implement validateDigit");
    }
}
