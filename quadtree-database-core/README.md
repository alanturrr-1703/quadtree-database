# quadtree-database-core

Phase 1 library: spatial bounds, quad encoding (`MapSpace`), 2D geometry helpers, and map features (roads + terrain regions).

## Documentation

Full per-method requirements: [../docs/REQUIREMENTS.md](../docs/REQUIREMENTS.md)

## Tests

```bash
mvn -pl quadtree-database-core clean test
mvn -pl quadtree-database-core -Dtest=BoundsTest test
```

## Implementation status

| Class | Package | Notes |
|-------|---------|--------|
| `Point2D` | `spatial` | Done |
| `MapSpaceConvention` | `spatial` | Done |
| `GeometryKind` | `feature` | Done |
| `Bounds` | `spatial` | Validation in constructor; other methods TODO |
| `TerrainType` | `feature` | TODO |
| `MapSpace` | `spatial` | TODO |
| `LegacyQuadrant` | `quadtree` | TODO |
| `Geometry2D` | `geometry` | TODO |
| `Feature` | `feature` | Builder wired; `validate()` etc. TODO |

## Packages

- `com.pluscode.core.spatial` — `Bounds`, `MapSpace`, `Point2D`
- `com.pluscode.core.geometry` — `Geometry2D`
- `com.pluscode.core.feature` — `Feature`, `TerrainType`, `GeometryKind`
- `com.pluscode.core.quadtree` — `LegacyQuadrant` (lat/lon compatibility)
