package com.pluscode.core.feature;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Run: mvn -pl pluscode-core -Dtest=TerrainTypeTest test
 */
class TerrainTypeTest {

    @Test
    void roadVsTerrainFlags() {
        assertTrue(TerrainType.ROAD.isRoad());
        assertFalse(TerrainType.ROAD.isTerrainRegion());
        assertTrue(TerrainType.WATER.isTerrainRegion());
        assertFalse(TerrainType.WATER.isRoad());
        assertTrue(TerrainType.MOUNTAIN.isTerrainRegion());
        assertFalse(TerrainType.MOUNTAIN.isRoad());
        assertFalse(TerrainType.CUSTOM.isRoad());
    }
}
