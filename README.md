# PlusCode Generator

A Java project for **quad-based map indexing**, evolving from a small Earth geocoder into a **desktop map database suite**: import a map image, draw roads and terrain (hills, plains, mountains, beaches, water, etc.), and build/query your own `.plusmap` database with a custom storage and query engine—no PostgreSQL or external GIS stack.

---

## Current scope

| Area | Status |
|------|--------|
| **Legacy geocoder** | Original `Quadrant` lat/lon encode/decode in `src/main/java/com/plusCode/` |
| **Phase 1 — `pluscode-core`** | In progress: tests + boilerplate; you implement against [docs/REQUIREMENTS.md](docs/REQUIREMENTS.md) |
| **Phase 2+** | Planned: `.plusmap` file format, storage engine, JavaFX desktop editor |

### Phase 1 implementation progress

| Class | Status |
|-------|--------|
| `Bounds` | Constructor validation done; size/contains/intersect methods still TODO |
| `TerrainType` | Boilerplate |
| `MapSpace`, `LegacyQuadrant` | Boilerplate |
| `Geometry2D` | Boilerplate |
| `Feature` | Boilerplate |
| `Point2D`, `GeometryKind`, `MapSpaceConvention` | Complete |

---

## Repository layout

```
PlusCodeGenerator/
├── pom.xml                          # Parent Maven project
├── pluscode-core/                   # Phase 1: MapSpace, geometry, features
│   ├── src/main/java/com/pluscode/core/
│   │   ├── spatial/                 # Bounds, MapSpace, Point2D
│   │   ├── geometry/                # Geometry2D
│   │   ├── feature/                 # Feature, TerrainType, GeometryKind
│   │   └── quadtree/                # LegacyQuadrant (earth wrapper)
│   └── src/test/java/               # JUnit 5 specs (TDD)
├── docs/
│   └── REQUIREMENTS.md              # Per-class requirements
├── src/main/java/com/plusCode/      # Original demo + Quadrant (legacy)
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

On **custom map images**, coordinates are pixel space (origin top-left, y down). On **Earth**, `MapSpace.earth()` uses longitude/latitude and matches the original `Quadrant` behavior (e.g. San Francisco → `214231` at depth 6).

---

## Build and test

**Requirements:** Java 21+, Maven 3.9+

```bash
# From repo root — all pluscode-core tests
mvn -pl pluscode-core test

# Clean build (recommended after code changes)
mvn -pl pluscode-core clean test

# One test class
mvn -pl pluscode-core -Dtest=BoundsTest test
mvn -pl pluscode-core -Dtest=MapSpaceTest test

# Single test method
mvn -pl pluscode-core -Dtest=BoundsTest#rejectsInvalidBounds test
```

Tests are designed to **fail until you implement** the corresponding methods. See [pluscode-core/README.md](pluscode-core/README.md) and [docs/REQUIREMENTS.md](docs/REQUIREMENTS.md).

**Suggested implementation order:** `Bounds` → `TerrainType` → `MapSpace` / `LegacyQuadrant` → `Geometry2D` → `Feature`

---

## Legacy usage (original API)

```java
Quadrant root = new Quadrant(null, -90, 90, -180, 180);
String code = root.encode(root, 6, 37.7749, -122.4194);
double[] coords = Quadrant.decode(code); // [lat, lon]
```

After Phase 1, prefer `MapSpace.earth()` / `LegacyQuadrant` in `pluscode-core` for the same semantics with image-map support elsewhere.

---

## Product roadmap (planned)

1. **Core** — `MapSpace`, `Bounds`, `Geometry2D`, `Feature` (current)
2. **Engine** — `.plusmap` on-disk format, quad indexes, custom query DSL
3. **Desktop** — JavaFX editor: image import, draw roads/regions, build index, query console

---

## License

MIT License — free to use, modify, and distribute.
