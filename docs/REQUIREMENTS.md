# Phase 1 — Core module requirements

Use this document together with the **unit tests** in `pluscode-core/src/test/java`. Implement each class until all tests pass.

**Project overview:** [../README.md](../README.md)

## How to run tests

From the repository root:

```bash
# All core tests
mvn -pl pluscode-core clean test

# One test class
mvn -pl pluscode-core -Dtest=BoundsTest test
mvn -pl pluscode-core -Dtest=MapSpaceTest test
mvn -pl pluscode-core -Dtest=Geometry2DTest test
mvn -pl pluscode-core -Dtest=FeatureTest test
mvn -pl pluscode-core -Dtest=TerrainTypeTest test

# Single test method
mvn -pl pluscode-core -Dtest=MapSpaceTest#earthSanFranciscoMatchesReadme test
```

Run `clean` if you see `Unresolved compilation problem` errors from stale builds.

## Package layout

```
pluscode-core/src/main/java/com/pluscode/core/
  spatial/     Bounds, Point2D, MapSpaceConvention, MapSpace
  geometry/    Geometry2D
  feature/     GeometryKind, TerrainType, Feature
  quadtree/    LegacyQuadrant
```

---

## Point2D

**Status:** Complete (record). No work required.

| Member | Requirement |
|--------|-------------|
| `Point2D(double x, double y)` | Immutable coordinates |
| `of(x, y)` | Factory |

---

## MapSpaceConvention

**Status:** Complete (enum). No work required.

| Value | Meaning |
|-------|---------|
| `IMAGE_Y_DOWN` | Map image: origin top-left, y increases downward |
| `EARTH` | x = longitude, y = latitude; compatible with legacy `Quadrant` |

---

## GeometryKind

**Status:** Complete (enum).

| Value | Use |
|-------|-----|
| `POLYLINE` | Roads, paths |
| `POLYGON` | Hills, water, plains, etc. |

---

## Bounds {#bounds}

**Status:** Constructor validation implemented; remaining methods TODO.

Axis-aligned rectangle `[minX, maxX) × [minY, maxY)` for `contains`; `containsInclusive` includes max edges.

### Constructor

| Method | Requirement |
|--------|-------------|
| `Bounds(minX, maxX, minY, maxY)` | Throw `IllegalArgumentException` if `minX >= maxX` or `minY >= maxY` |
| `of(...)` | Delegate to constructor |

### Methods to implement

| Method | Returns | Requirement |
|--------|---------|-------------|
| `width()` | `maxX - minX` | |
| `height()` | `maxY - minY` | |
| `midX()`, `midY()` | center of edges | `(min + max) / 2` |
| `centerX()`, `centerY()` | same as mid | |
| `contains(x, y)` | boolean | `minX <= x < maxX` and `minY <= y < maxY` |
| `containsInclusive(x, y)` | boolean | `<=` on all edges |
| `intersects(other)` | boolean | Standard AABB overlap test |
| `union(other)` | `Bounds` | Minimal box containing both |
| `equals` / `hashCode` | | Value equality on all four edges |

### Tests

`BoundsTest` — see commands above.

---

## MapSpace {#mapspace}

Stateless quadtree over a fixed rectangle. Quad digits: **1 = NW, 2 = NE, 3 = SW, 4 = SE** (relative to current cell).

### Factories

| Method | Requirement |
|--------|-------------|
| `of(minX, maxX, minY, maxY)` | Bounds + `IMAGE_Y_DOWN` |
| `of(Bounds)` | `IMAGE_Y_DOWN` |
| `of(..., convention)` | Explicit convention |
| `earth()` | Bounds `[-180,180] × [-90,90]`, convention `EARTH` |

### Encode (`encode(x, y, depth)`)

- Throw if `(x,y)` outside space bounds (inclusive of max edge for input validation).
- Throw if `depth < 0`.
- If `depth == 0`, return `""`.
- Repeat `depth` times: bisect current cell; append one digit; narrow bounds.

**Digit rules — `IMAGE_Y_DOWN` (y grows down):**

| Condition | Digit |
|-----------|-------|
| `y < midY` and `x < midX` | `1` (NW) |
| `y < midY` and `x >= midX` | `2` (NE) |
| `y >= midY` and `x < midX` | `3` (SW) |
| `y >= midY` and `x >= midX` | `4` (SE) |

**Digit rules — `EARTH` (y = latitude, x = longitude):** must match legacy [`Quadrant`](../src/main/java/com/plusCode/quadtree/Quadrant.java):

| Condition | Digit |
|-----------|-------|
| `y < midY` and `x < midX` | `3` (SW) |
| `y >= midY` and `x < midX` | `1` (NW) |
| `y < midY` and `x >= midX` | `4` (SE) |
| else | `2` (NE) |

**Acceptance:** `earth().encode(-122.4194, 37.7749, 6)` → `"214231"` (SF README example).

### Decode (`decode(code)`)

- Validate code with `validateCode`.
- Start from space bounds; for each digit, narrow like `boundsForCode`.
- Return center `(centerX, centerY)` of final cell.

**Acceptance:** decode `"214231"` on earth → approximately `(lon=-122.40625, lat=37.78125)`.

### `boundsForCode(code)`

- Return cell bounds after processing all digits (same narrowing as decode, without averaging).

### `parentCode(code)` / `childCode(parent, digit)`

- Parent: all digits except last; empty string if length ≤ 1.
- Child: append digit; validate digit and parent.

### `enumerateCodesInBounds(region, depth)`

- Return every code at exactly `depth` whose cell intersects `region` (and space bounds).
- Include `""` when `depth == 0` and intersection non-empty.

### Static validation

| Method | Requirement |
|--------|-------------|
| `isValidDigit(c)` | `c` in `'1'..'4'` |
| `validateDigit(c)` | throw if invalid |
| `validateCode(s)` | throw if null or any char invalid |

### Tests

`MapSpaceTest` — image round-trip, earth SF, depth 0, parent/child, enumeration, validation.

---

## LegacyQuadrant {#legacyquadrant}

Thin wrapper over `MapSpace.earth()`.

| Method | Requirement |
|--------|-------------|
| `encode(lat, lon, depth)` | `earth().encode(lon, lat, depth)` |
| `decode(code)` | `[lat, lon]` from `earth().decode` → `{y, x}` |

### Tests

`MapSpaceTest#legacyQuadrantEncodeAndDecode`

---

## Geometry2D {#geometry2d}

All methods static. Throw `IllegalArgumentException` for null/empty geometry where tests expect it.

| Method | Requirement |
|--------|-------------|
| `boundingBox(points)` | Min/max x/y; if one dimension collapses to 0, expand by tiny epsilon (`1e-9`) so `Bounds` constructor succeeds |
| `centroid(points)` | Arithmetic mean of vertices |
| `pointInPolygon(x, y, polygon)` | Ray-casting; false if fewer than 3 vertices |
| `segmentIntersectsRect(x1,y1,x2,y2, rect)` | True if segment hits rectangle boundary or interior |
| `polylineIntersectsRect(polyline, rect)` | True if any segment intersects or any vertex inside (inclusive) |
| `polygonIntersectsRect(polygon, rect)` | True if any vertex inside, any rect corner/center inside polygon, or any edge intersects |
| `featureIntersectsCell(points, closed, cell)` | Delegate to polygon or polyline variant |

### Tests

`Geometry2DTest`

---

## TerrainType

| Method | Requirement |
|--------|-------------|
| `isRoad()` | true only for `ROAD` |
| `isTerrainRegion()` | true for all except `ROAD` |

### Tests

`TerrainTypeTest`

---

## Feature {#feature}

Immutable feature: road polyline or terrain polygon.

### Builder → `build()`

Calls `validate()` before returning instance.

### `validate()` rules

| Rule | Error |
|------|-------|
| At least 2 points | `IllegalArgumentException` |
| `POLYGON` needs ≥ 3 points | |
| `ROAD` must use `POLYLINE` | |
| `HILL`, `PLAIN`, `MOUNTAIN`, `BEACH`, `WATER`, `FOREST` must use `POLYGON` | |
| `CUSTOM` may use either kind | |

### Other methods

| Method | Requirement |
|--------|-------------|
| `isClosed()` | true iff `kind == POLYGON` |
| `property(key)` | from immutable map |
| `equals` / `hashCode` | by `id` only |

### Tests

`FeatureTest`

---

## Suggested implementation order

1. `Bounds` → `BoundsTest` green  
2. `TerrainType` → `TerrainTypeTest` green  
3. `MapSpace` + `LegacyQuadrant` → `MapSpaceTest` green  
4. `Geometry2D` → `Geometry2DTest` green  
5. `Feature.validate` + helpers → `FeatureTest` green  
6. Full suite: `mvn -pl pluscode-core clean test`

---

## Done when

```bash
mvn -pl pluscode-core clean test
```

prints **BUILD SUCCESS** with 0 failures.
