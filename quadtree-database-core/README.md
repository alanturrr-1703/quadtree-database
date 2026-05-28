# quadtree-database-core

Phase 1 library: spatial bounds, quad encoding (`MapSpace`), 2D geometry helpers, and map features (roads + terrain regions).

**Status:** Phase 1 complete — all unit tests pass.

## Documentation

Full per-method requirements: [../docs/REQUIREMENTS.md](../docs/REQUIREMENTS.md)

## Tests

```bash
mvn -pl quadtree-database-core clean test
mvn -pl quadtree-database-core -Dtest=BoundsTest test
```

## Packages

- `com.pluscode.core.spatial` — `Bounds`, `MapSpace`, `Point2D`
- `com.pluscode.core.geometry` — `Geometry2D`
- `com.pluscode.core.feature` — `Feature`, `TerrainType`, `GeometryKind`
- `com.pluscode.core.quadtree` — `LegacyQuadrant` (lat/lon compatibility)
