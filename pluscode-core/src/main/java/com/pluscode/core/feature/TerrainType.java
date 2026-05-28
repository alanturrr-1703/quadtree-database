package com.pluscode.core.feature;

public enum TerrainType {
    ROAD,
    HILL,
    PLAIN,
    MOUNTAIN,
    BEACH,
    WATER,
    FOREST,
    CUSTOM;

    public boolean isTerrainRegion() {
        throw new UnsupportedOperationException("TODO: implement isTerrainRegion");
    }

    public boolean isRoad() {
        throw new UnsupportedOperationException("TODO: implement isRoad");
    }
}
