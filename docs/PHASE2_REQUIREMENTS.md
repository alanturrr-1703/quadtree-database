# Phase 2 — Engine module requirements

Use this document together with the **unit tests** in `quadtree-database-engine/src/test/java`. Implement each class until all tests pass.

**Project overview:** [../README.md](../README.md)

## How to run tests

From the repository root:

```bash
# All engine tests
mvn -pl quadtree-database-engine clean test

# One test class
mvn -pl quadtree-database-engine -Dtest=PlusMapDatabaseTest test
mvn -pl quadtree-database-engine -Dtest=QuadIndexBuilderTest test
mvn -pl quadtree-database-engine -Dtest=QueryParserTest test
mvn -pl quadtree-database-engine -Dtest=PlusMapFormatTest test
```

## Package layout

```
quadtree-database-engine/src/main/java/com/pluscode/engine/
  PlusMapDatabase.java          # Build, open, query API
  format/                       # .plusmap binary I/O
  index/                        # Quad code postings
  query/                        # DSL parser + executor
```

---

## `.plusmap` file format (version 1)

Binary layout, big-endian:

| Section | Fields |
|---------|--------|
| Header | magic `PMAP`, version `1`, convention ordinal, bounds (4× double), `indexDepth`, `featureCount`, `codeEntryCount` |
| Features | id, kind, terrain type, z-order, name, primary code, points, properties |
| Code index | code string → sorted feature id list |

`PlusMapWriter` writes; `PlusMapReader` reads and returns `LoadedPlusMap`.

---

## QuadIndexBuilder

For each feature at `indexDepth`:

1. Compute bounding box with `Geometry2D.boundingBox`.
2. Enumerate intersecting codes via `MapSpace.enumerateCodesInBounds`.
3. Keep feature id when `Geometry2D.featureIntersectsCell` is true for the cell.

---

## Query DSL

| Form | Meaning |
|------|---------|
| `ALL` | Every feature |
| `CODE "1321"` | Features indexed at quad code |
| `AT x y` | Features at map point (encode at index depth) |
| `BOUNDS minX minY maxX maxY` | Features in any intersecting cell |
| `TYPE ROAD` | Filter by terrain type |
| `TYPE IN ROAD WATER` | Union of terrain types |
| `ID 42` | Feature by id |
| `NAME "Lake"` | Exact name match |
| `AND expr expr` | Intersection (infix: `TYPE ROAD AND BOUNDS 0 0 50 50`) |
| `OR expr expr` | Union (infix: `TYPE ROAD OR TYPE WATER`) |
| `( expr )` | Grouping |

`QueryParser.parse(dsl)` returns AST; `QueryExecutor` evaluates against a database snapshot.

---

## PlusMapDatabase

```java
PlusMapDatabase db = PlusMapDatabase.builder(mapSpace)
    .indexDepth(4)
    .addFeature(feature)
    .build();

db.writeTo(path);
PlusMapDatabase loaded = PlusMapDatabase.open(path);
List<Feature> roads = loaded.query("TYPE ROAD");
```

---

## Done when

```bash
mvn -pl quadtree-database-engine clean test
```

prints **BUILD SUCCESS** with 0 failures.
