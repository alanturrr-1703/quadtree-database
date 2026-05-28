package com.pluscode.core.spatial;

/**
 * Quad digit layout for encode/decode.
 * <ul>
 *   <li>{@link #IMAGE_Y_DOWN} — map image: origin top-left, y grows downward.</li>
 *   <li>{@link #EARTH} — x = longitude, y = latitude; matches legacy {@code Quadrant}.</li>
 * </ul>
 */
public enum MapSpaceConvention {
    IMAGE_Y_DOWN,
    EARTH
}
