package com.pluscode.core.geometry;

import com.pluscode.core.spatial.Bounds;
import com.pluscode.core.spatial.Point2D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Run: mvn -pl quadtree-database-core -Dtest=Geometry2DTest test
 */
class Geometry2DTest {

    @Test
    void pointInPolygon_square() {
        List<Point2D> square = List.of(
                Point2D.of(0, 0),
                Point2D.of(10, 0),
                Point2D.of(10, 10),
                Point2D.of(0, 10));
        assertTrue(Geometry2D.pointInPolygon(5, 5, square));
        assertFalse(Geometry2D.pointInPolygon(15, 5, square));
    }

    @Test
    void boundingBoxAndCentroid() {
        List<Point2D> pts = List.of(Point2D.of(0, 0), Point2D.of(10, 20));
        Bounds box = Geometry2D.boundingBox(pts);
        assertEquals(0, box.minX(), 1e-9);
        assertEquals(10, box.maxX(), 1e-9);
        Point2D c = Geometry2D.centroid(pts);
        assertEquals(5, c.x(), 1e-9);
        assertEquals(10, c.y(), 1e-9);
    }

    @Test
    void polylineIntersectsRect() {
        List<Point2D> road = List.of(Point2D.of(0, 5), Point2D.of(20, 5));
        Bounds rect = Bounds.of(8, 12, 0, 10);
        assertTrue(Geometry2D.polylineIntersectsRect(road, rect));
    }

    @Test
    void polygonIntersectsRect() {
        List<Point2D> water = List.of(
                Point2D.of(0, 0),
                Point2D.of(30, 0),
                Point2D.of(30, 30),
                Point2D.of(0, 30));
        Bounds rect = Bounds.of(25, 35, 25, 35);
        assertTrue(Geometry2D.polygonIntersectsRect(water, rect));
    }

    @Test
    void featureIntersectsCell_closedVsOpen() {
        List<Point2D> poly = List.of(
                Point2D.of(0, 0), Point2D.of(50, 0), Point2D.of(50, 50), Point2D.of(0, 50));
        Bounds cell = Bounds.of(40, 60, 40, 60);
        assertTrue(Geometry2D.featureIntersectsCell(poly, true, cell));
        List<Point2D> line = List.of(Point2D.of(0, 45), Point2D.of(100, 45));
        assertTrue(Geometry2D.featureIntersectsCell(line, false, Bounds.of(45, 55, 40, 50)));
    }
}
