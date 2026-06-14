# quadtree-database-engine

Phase 2 library: `.plusmap` on-disk format, quad index, and query DSL.

**Status:** Phase 2 complete — all unit tests pass.

## Documentation

Full requirements: [../docs/PHASE2_REQUIREMENTS.md](../docs/PHASE2_REQUIREMENTS.md)

## Tests

```bash
mvn -pl quadtree-database-engine clean test
mvn -pl quadtree-database-engine -Dtest=PlusMapDatabaseTest test
```

## Packages

- `com.pluscode.engine` — `PlusMapDatabase` (build, open, query)
- `com.pluscode.engine.format` — binary `.plusmap` reader/writer
- `com.pluscode.engine.index` — quad code postings
- `com.pluscode.engine.query` — DSL parser and executor

## Quick example

```java
MapSpace space = MapSpace.of(0, 1000, 0, 800);

PlusMapDatabase db = PlusMapDatabase.builder(space)
    .indexDepth(4)
    .addFeature(roadFeature)
    .addFeature(lakeFeature)
    .buildAndWrite(Path.of("world.plusmap"));

PlusMapDatabase loaded = PlusMapDatabase.open(Path.of("world.plusmap"));
List<Feature> hits = loaded.query("AND TYPE ROAD BOUNDS 0 0 500 400");
```
