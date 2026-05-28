package com.pluscode.core.quadtree;

import com.pluscode.core.spatial.MapSpace;
import com.pluscode.core.spatial.Point2D;

/**
 * Backward-compatible lat/lon API. decode returns [lat, lon].
 */
public final class LegacyQuadrant {

    private static final MapSpace EARTH = MapSpace.earth();

    private LegacyQuadrant() {
    }

    public static String encode(double lat, double lon, int depth) {
        return EARTH.encode(lon, lat, depth);
    }

    public static double[] decode(String code) {
        Point2D center = EARTH.decode(code);
        return new double[] {center.y(), center.x()};
    }
}
