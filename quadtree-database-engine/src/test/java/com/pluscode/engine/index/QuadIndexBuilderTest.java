package com.pluscode.engine.index;

import com.pluscode.core.feature.Feature;
import com.pluscode.core.feature.GeometryKind;
import com.pluscode.core.feature.TerrainType;
import com.pluscode.core.spatial.MapSpace;
import com.pluscode.core.spatial.Point2D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Run: mvn -pl quadtree-database-engine -Dtest=QuadIndexBuilderTest test
 */
class QuadIndexBuilderTest {

    @Test
    void indexesRoadAcrossCells() {
        MapSpace space = MapSpace.of(0, 100, 0, 100);
        Feature road = Feature.builder()
                .id(1)
                .kind(GeometryKind.POLYLINE)
                .terrainType(TerrainType.ROAD)
                .points(List.of(Point2D.of(10, 25), Point2D.of(90, 25)))
                .build();

        QuadIndex index = QuadIndexBuilder.build(space, 2, List.of(road));

        assertTrue(index.featureIdsForCode("13").contains(1L));
        assertTrue(index.featureIdsForCode("14").contains(1L));
        assertTrue(index.featureIdsForCode("23").contains(1L));
        assertTrue(index.featureIdsForCode("24").contains(1L));
    }

    @Test
    void indexesPolygonInSingleCell() {
        MapSpace space = MapSpace.of(0, 100, 0, 100);
        Feature lake = Feature.builder()
                .id(2)
                .kind(GeometryKind.POLYGON)
                .terrainType(TerrainType.WATER)
                .points(List.of(
                        Point2D.of(5, 5),
                        Point2D.of(20, 5),
                        Point2D.of(12, 20)))
                .build();

        QuadIndex index = QuadIndexBuilder.build(space, 2, List.of(lake));

        assertTrue(index.featureIdsForCode("11").contains(2L));
        assertFalse(index.featureIdsForCode("44").contains(2L));
    }
}
