package com.pluscode.core.feature;

import com.pluscode.core.spatial.Point2D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Run: mvn -pl pluscode-core -Dtest=FeatureTest test
 */
class FeatureTest {

    @Test
    void buildRoadPolyline() {
        Feature road = Feature.builder()
                .id(1)
                .kind(GeometryKind.POLYLINE)
                .terrainType(TerrainType.ROAD)
                .name("Main St")
                .zOrder(10)
                .points(List.of(Point2D.of(0, 0), Point2D.of(100, 0)))
                .primaryCode("1234")
                .property("lanes", "2")
                .build();

        assertEquals(TerrainType.ROAD, road.terrainType());
        assertEquals("2", road.property("lanes"));
        assertFalse(road.isClosed());
    }

    @Test
    void buildWaterPolygon() {
        Feature water = Feature.builder()
                .id(2)
                .kind(GeometryKind.POLYGON)
                .terrainType(TerrainType.WATER)
                .name("Lake")
                .points(List.of(
                        Point2D.of(0, 0),
                        Point2D.of(50, 0),
                        Point2D.of(25, 40)))
                .build();

        assertTrue(water.isClosed());
        assertTrue(water.terrainType().isTerrainRegion());
    }

    @Test
    void rejectsRoadAsPolygon() {
        assertThrows(IllegalArgumentException.class, () -> Feature.builder()
                .id(3)
                .kind(GeometryKind.POLYGON)
                .terrainType(TerrainType.ROAD)
                .points(List.of(Point2D.of(0, 0), Point2D.of(1, 0), Point2D.of(0, 1)))
                .build());
    }

    @Test
    void rejectsHillAsPolyline() {
        assertThrows(IllegalArgumentException.class, () -> Feature.builder()
                .id(4)
                .kind(GeometryKind.POLYLINE)
                .terrainType(TerrainType.HILL)
                .points(List.of(Point2D.of(0, 0), Point2D.of(10, 10)))
                .build());
    }
}
