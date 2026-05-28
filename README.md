# Quadtree Database

A Java project for **quad-based map indexing**, evolving from a small Earth geocoder into a **desktop map database suite**: import a map image, draw roads and terrain (hills, plains, mountains, beaches, water, etc.), and build/query your own `.plusmap` database with a custom storage and query engine.

**Repository:** [github.com/alanturrr-1703/quadtree-database](https://github.com/alanturrr-1703/quadtree-database)

---

## Current scope

| Area | Status |
|------|--------|
| **Legacy geocoder** | Original `Quadrant` lat/lon encode/decode in `src/main/java/com/plusCode/` |
| **Phase 1 — `quadtree-database-core`** | Complete (23 tests passing) |
| **Phase 2+** | Planned: `.plusmap` file format, storage engine, JavaFX desktop editor |

### Phase 1 implementation progress

| Class | Status |
|-------|--------|
| `Bounds` | Complete |
| `TerrainType` | Complete |
| `MapSpace`, `LegacyQuadrant` | Complete |
| `Geometry2D` | Complete |
| `Feature` | Complete |
| `Point2D`, `GeometryKind`, `MapSpaceConvention` | Complete |

All **23** unit tests pass: `mvn -pl quadtree-database-core test`

---

## Repository layout

```
quadtree-database/
├── pom.xml                              # Parent Maven project
├── quadtree-database-core/              # Phase 1: MapSpace, geometry, features
│   ├── src/main/java/com/pluscode/core/
│   │   ├── spatial/                     # Bounds, MapSpace, Point2D
│   │   ├── geometry/                    # Geometry2D
│   │   ├── feature/                     # Feature, TerrainType, GeometryKind
│   │   └── quadtree/                    # LegacyQuadrant (earth wrapper)
│   └── src/test/java/                   # JUnit 5 specs (TDD)
├── docs/
│   └── REQUIREMENTS.md                  # Per-class requirements
├── src/main/java/com/plusCode/          # Original demo + Quadrant (legacy)
│   ├── quadtree/Quadrant.java
│   └── Main.java
└── README.md
```

---

## Quad codes

The map is split recursively into four children per cell:

| Digit | Region |
|-------|--------|
| 1 | NW |
| 2 | NE |
| 3 | SW |
| 4 | SE |

On **custom map images**, coordinates are pixel space (origin top-left, y down). On **Earth**, `MapSpace.earth()` uses longitude/latitude and matches the original `Quadrant` behavior (e.g. San Francisco → `132141` at depth 6).

---

## Build and test

**Requirements:** Java 21+, Maven 3.9+

```bash
# From repo root — all core tests
mvn -pl quadtree-database-core test

# Clean build (recommended after code changes)
mvn -pl quadtree-database-core clean test

# One test class
mvn -pl quadtree-database-core -Dtest=BoundsTest test
mvn -pl quadtree-database-core -Dtest=MapSpaceTest test

# Single test method
mvn -pl quadtree-database-core -Dtest=BoundsTest#rejectsInvalidBounds test
```

Tests are designed to **fail until you implement** the corresponding methods. See [quadtree-database-core/README.md](quadtree-database-core/README.md) and [docs/REQUIREMENTS.md](docs/REQUIREMENTS.md).

**Suggested implementation order:** `Bounds` → `TerrainType` → `MapSpace` / `LegacyQuadrant` → `Geometry2D` → `Feature`

---

## Legacy usage (original API)

```java
Quadrant root = new Quadrant(null, -90, 90, -180, 180);
String code = root.encode(root, 6, 37.7749, -122.4194);
double[] coords = Quadrant.decode(code); // [lat, lon]
```

After Phase 1, prefer `MapSpace.earth()` / `LegacyQuadrant` in `quadtree-database-core` for the same semantics with image-map support elsewhere.

---

## Product roadmap (planned)

1. **Core** — `MapSpace`, `Bounds`, `Geometry2D`, `Feature` (current)
2. **Engine** — `.plusmap` on-disk format, quad indexes, custom query DSL
3. **Desktop** — JavaFX editor: image import, draw roads/regions, build index, query console

---

## License

MIT License — free to use, modify, and distribute.
