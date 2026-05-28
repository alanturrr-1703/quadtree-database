package com.pluscode.core.spatial;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Run: mvn -pl quadtree-database-core -Dtest=BoundsTest test
 */
class BoundsTest {

    @Test
    void dimensionsAndCenter() {
        Bounds b = Bounds.of(0, 100, 0, 50);
        assertEquals(100, b.width());
        assertEquals(50, b.height());
        assertEquals(50, b.centerX());
        assertEquals(25, b.centerY());
        assertEquals(50, b.midX());
        assertEquals(25, b.midY());
    }

    @Test
    void contains_halfOpenMaxEdge() {
        Bounds b = Bounds.of(0, 100, 0, 100);
        assertTrue(b.contains(50, 50));
        assertFalse(b.contains(100, 50));
        assertTrue(b.containsInclusive(100, 100));
        assertFalse(b.contains(-0.001, 50));
    }

    @Test
    void intersectsAndUnion() {
        Bounds a = Bounds.of(0, 10, 0, 10);
        Bounds b = Bounds.of(5, 15, 5, 15);
        assertTrue(a.intersects(b));
        assertFalse(a.intersects(Bounds.of(20, 30, 20, 30)));
        Bounds u = a.union(b);
        assertEquals(0, u.minX());
        assertEquals(15, u.maxX());
        assertEquals(0, u.minY());
        assertEquals(15, u.maxY());
    }

    @Test
    void rejectsInvalidBounds() {
        assertThrows(IllegalArgumentException.class, () -> Bounds.of(10, 0, 0, 10));
        assertThrows(IllegalArgumentException.class, () -> Bounds.of(0, 10, 10, 0));
    }
}
