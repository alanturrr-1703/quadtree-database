package com.pluscode.core.feature;

import com.pluscode.core.spatial.Point2D;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Road (polyline) or terrain region (polygon).
 */
public final class Feature {

    private final long id;
    private final GeometryKind kind;
    private final TerrainType terrainType;
    private final String name;
    private final int zOrder;
    private final List<Point2D> points;
    private final String primaryCode;
    private final Map<String, String> properties;

    private Feature(Builder builder) {
        this.id = builder.id;
        this.kind = builder.kind;
        this.terrainType = builder.terrainType;
        this.name = builder.name == null ? "" : builder.name;
        this.zOrder = builder.zOrder;
        this.points = List.copyOf(builder.points);
        this.primaryCode = builder.primaryCode;
        this.properties = Collections.unmodifiableMap(new HashMap<>(builder.properties));
        validate();
    }

    public static Builder builder() {
        return new Builder();
    }

    public long id() {
        return id;
    }

    public GeometryKind kind() {
        return kind;
    }

    public TerrainType terrainType() {
        return terrainType;
    }

    public String name() {
        return name;
    }

    public int zOrder() {
        return zOrder;
    }

    public List<Point2D> points() {
        return points;
    }

    public String primaryCode() {
        return primaryCode;
    }

    public Map<String, String> properties() {
        return properties;
    }

    public boolean isClosed() {
        return kind == GeometryKind.POLYGON;
    }

    public String property(String key) {
        return properties.get(key);
    }

    private void validate() {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Feature must have at least 2 points");
        }
        if (kind == GeometryKind.POLYGON && points.size() < 3) {
            throw new IllegalArgumentException("Polygon must have at least 3 points");
        }
        if (terrainType == TerrainType.ROAD && kind != GeometryKind.POLYLINE) {
            throw new IllegalArgumentException("ROAD terrain type requires POLYLINE geometry");
        }
        if (terrainType != TerrainType.ROAD
                && terrainType != TerrainType.CUSTOM
                && kind != GeometryKind.POLYGON) {
            throw new IllegalArgumentException("Terrain region requires POLYGON geometry");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Feature feature)) {
            return false;
        }
        return id == feature.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public static final class Builder {
        private long id;
        private GeometryKind kind;
        private TerrainType terrainType;
        private String name = "";
        private int zOrder;
        private List<Point2D> points = List.of();
        private String primaryCode;
        private final Map<String, String> properties = new HashMap<>();

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder kind(GeometryKind kind) {
            this.kind = kind;
            return this;
        }

        public Builder terrainType(TerrainType terrainType) {
            this.terrainType = terrainType;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder zOrder(int zOrder) {
            this.zOrder = zOrder;
            return this;
        }

        public Builder points(List<Point2D> points) {
            this.points = points == null ? List.of() : points;
            return this;
        }

        public Builder primaryCode(String primaryCode) {
            this.primaryCode = primaryCode;
            return this;
        }

        public Builder property(String key, String value) {
            properties.put(key, value);
            return this;
        }

        public Builder properties(Map<String, String> properties) {
            this.properties.clear();
            if (properties != null) {
                this.properties.putAll(properties);
            }
            return this;
        }

        public Feature build() {
            return new Feature(this);
        }
    }
}
