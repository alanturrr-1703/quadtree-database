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
        return this != ROAD;
    }

    public boolean isRoad() {
        return this == ROAD;
    }
}
