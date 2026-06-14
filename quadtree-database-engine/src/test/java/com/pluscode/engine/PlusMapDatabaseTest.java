package com.pluscode.engine;

import com.pluscode.core.feature.Feature;
import com.pluscode.core.feature.GeometryKind;
import com.pluscode.core.feature.TerrainType;
import com.pluscode.core.spatial.MapSpace;
import com.pluscode.core.spatial.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Run: mvn -pl quadtree-database-engine -Dtest=PlusMapDatabaseTest test
 */
class PlusMapDatabaseTest {

    private static final MapSpace SPACE = MapSpace.of(0, 100, 0, 100);

    @Test
    void roundTripPreservesFeatures(@TempDir Path tempDir) throws Exception {
        Feature road = road();
        Feature lake = lake();
        Path file = tempDir.resolve("map.plusmap");

        PlusMapDatabase built = PlusMapDatabase.builder(SPACE)
                .indexDepth(2)
                .addFeature(road)
                .addFeature(lake)
                .buildAndWrite(file);

        PlusMapDatabase loaded = PlusMapDatabase.open(file);

        assertEquals(2, loaded.features().size());
        assertEquals(road.name(), loaded.feature(1).name());
        assertEquals(lake.terrainType(), loaded.feature(2).terrainType());
        assertEquals("2", loaded.feature(1).property("lanes"));
        assertEquals(built.indexDepth(), loaded.indexDepth());
    }

    @Test
    void queryTypeRoad() {
        PlusMapDatabase db = database();
        List<Feature> roads = db.query("TYPE ROAD");
        assertEquals(1, roads.size());
        assertEquals("Main St", roads.getFirst().name());
    }

    @Test
    void queryAtPointOnRoad() {
        PlusMapDatabase db = database();
        List<Feature> hits = db.query("AT 80 10");
        assertEquals(1, hits.size());
        assertEquals(1L, hits.getFirst().id());
    }

    @Test
    void queryBoundsIntersection() {
        PlusMapDatabase db = database();
        List<Feature> hits = db.query("TYPE WATER AND BOUNDS 0 0 30 30");
        assertEquals(1, hits.size());
        assertEquals(2L, hits.getFirst().id());
    }

    @Test
    void queryCodeLookup() {
        PlusMapDatabase db = database();
        List<Feature> hits = db.query("CODE \"22\"");
        assertEquals(1, hits.size());
        assertEquals(1L, hits.getFirst().id());
    }

    @Test
    void queryAndCombinesFilters() {
        PlusMapDatabase db = database();
        List<Feature> hits = db.query("TYPE ROAD AND BOUNDS 40 0 60 20");
        assertEquals(1, hits.size());
        assertEquals(1L, hits.getFirst().id());

        List<Feature> empty = db.query("TYPE ROAD AND BOUNDS 50 50 60 60");
        assertTrue(empty.isEmpty());
    }

    @Test
    void queryOrCombinesFilters() {
        PlusMapDatabase db = database();
        List<Feature> hits = db.query("TYPE ROAD OR TYPE WATER");
        assertEquals(2, hits.size());
    }

    @Test
    void queryAllReturnsEveryFeature() {
        PlusMapDatabase db = database();
        assertEquals(2, db.query("ALL").size());
    }

    @Test
    void queryNameExactMatch() {
        PlusMapDatabase db = database();
        List<Feature> hits = db.query("NAME \"Lake\"");
        assertEquals(1, hits.size());
        assertEquals(TerrainType.WATER, hits.getFirst().terrainType());
    }

    @Test
    void queryIdLookup() {
        PlusMapDatabase db = database();
        List<Feature> hits = db.query("ID 2");
        assertEquals(1, hits.size());
        assertEquals("Lake", hits.getFirst().name());
    }

    @Test
    void queryTypeIn() {
        PlusMapDatabase db = database();
        List<Feature> hits = db.query("TYPE IN ROAD WATER");
        assertEquals(2, hits.size());
    }

    private static PlusMapDatabase database() {
        return PlusMapDatabase.builder(SPACE)
                .indexDepth(2)
                .addFeature(road())
                .addFeature(lake())
                .build();
    }

    private static Feature road() {
        return Feature.builder()
                .id(1)
                .kind(GeometryKind.POLYLINE)
                .terrainType(TerrainType.ROAD)
                .name("Main St")
                .zOrder(10)
                .points(List.of(Point2D.of(10, 10), Point2D.of(90, 10)))
                .primaryCode("34")
                .property("lanes", "2")
                .build();
    }

    private static Feature lake() {
        return Feature.builder()
                .id(2)
                .kind(GeometryKind.POLYGON)
                .terrainType(TerrainType.WATER)
                .name("Lake")
                .zOrder(5)
                .points(List.of(
                        Point2D.of(0, 0),
                        Point2D.of(30, 0),
                        Point2D.of(15, 25)))
                .build();
    }
}
