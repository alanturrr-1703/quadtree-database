package com.pluscode.core.spatial;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Run: mvn -pl quadtree-database-core -Dtest=MapSpaceTest test
 */
class MapSpaceTest {

    @Test
    void imageEncodeDecodeRoundTrip() {
        MapSpace space = MapSpace.of(0, 1024, 0, 768);
        String code = space.encode(512, 384, 8);
        Point2D center = space.decode(code);
        Bounds cell = space.boundsForCode(code);
        assertTrue(cell.containsInclusive(center.x(), center.y()));
        assertEquals(8, code.length());
    }

    @Test
    void encodeDepthZeroReturnsEmptyCode() {
        MapSpace space = MapSpace.of(0, 100, 0, 100);
        assertEquals("", space.encode(50, 50, 0));
        Point2D center = space.decode("");
        assertEquals(50, center.x(), 1e-6);
        assertEquals(50, center.y(), 1e-6);
    }

    @Test
    void earthSanFranciscoMatchesReadme() {
        MapSpace earth = MapSpace.earth();
        // x = longitude, y = latitude
        String code = earth.encode(-122.4194, 37.7749, 6);
        assertEquals("132141", code);
        Point2D center = earth.decode(code);
        assertEquals(37.96875, center.y(), 1e-4);
        assertEquals(-120.9375, center.x(), 1e-4);
    }

    @Test
    void legacyQuadrantEncodeAndDecode() {
        String code = com.pluscode.core.quadtree.LegacyQuadrant.encode(37.7749, -122.4194, 6);
        assertEquals("132141", code);
        double[] latLon = com.pluscode.core.quadtree.LegacyQuadrant.decode("132141");
        assertEquals(37.96875, latLon[0], 1e-4);
        assertEquals(-120.9375, latLon[1], 1e-4);
    }

    @Test
    void parentAndChildCode() {
        MapSpace space = MapSpace.of(0, 100, 0, 100);
        String code = space.encode(25, 75, 4);
        assertEquals(code.substring(0, code.length() - 1), space.parentCode(code));
        assertEquals(code, space.childCode(space.parentCode(code), code.charAt(code.length() - 1)));
    }

    @Test
    void enumerateCodesInBounds() {
        MapSpace space = MapSpace.of(0, 100, 0, 100);
        Bounds quarter = Bounds.of(0, 50, 0, 50);
        List<String> codes = space.enumerateCodesInBounds(quarter, 2);
        assertFalse(codes.isEmpty());
        for (String code : codes) {
            assertTrue(space.boundsForCode(code).intersects(quarter));
        }
    }

    @Test
    void isValidDigitAndValidateCode() {
        assertTrue(MapSpace.isValidDigit('1'));
        assertFalse(MapSpace.isValidDigit('5'));
        MapSpace.validateCode("1234");
        assertThrows(IllegalArgumentException.class, () -> MapSpace.validateCode("125"));
        assertThrows(IllegalArgumentException.class, () -> MapSpace.validateCode(null));
    }

    @Test
    void rejectsInvalidCodeInBoundsForCode() {
        MapSpace space = MapSpace.of(0, 100, 0, 100);
        assertThrows(IllegalArgumentException.class, () -> space.boundsForCode("215"));
    }

    @Test
    void rejectsPointOutsideBounds() {
        MapSpace space = MapSpace.of(0, 100, 0, 100);
        assertThrows(IllegalArgumentException.class, () -> space.encode(200, 50, 4));
    }
}
