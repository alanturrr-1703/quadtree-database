package com.pluscode.core.quadtree;

/**
 * Backward-compatible lat/lon API. decode returns [lat, lon].
 *
 * @see docs/REQUIREMENTS.md#legacyquadrant
 */
public final class LegacyQuadrant {

    private LegacyQuadrant() {
    }

    public static String encode(double lat, double lon, int depth) {
        throw new UnsupportedOperationException("TODO: implement encode via MapSpace.earth()");
    }

    public static double[] decode(String code) {
        throw new UnsupportedOperationException("TODO: implement decode → [lat, lon]");
    }
}
