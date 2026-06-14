package com.pluscode.engine;

import com.pluscode.core.feature.Feature;
import com.pluscode.core.spatial.MapSpace;
import com.pluscode.engine.format.PlusMapReader;
import com.pluscode.engine.format.PlusMapWriter;
import com.pluscode.engine.index.QuadIndex;
import com.pluscode.engine.index.QuadIndexBuilder;
import com.pluscode.engine.query.Query;
import com.pluscode.engine.query.QueryExecutor;
import com.pluscode.engine.query.QueryParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * High-level API for building, persisting, and querying .plusmap databases.
 */
public final class PlusMapDatabase {

    private final MapSpace mapSpace;
    private final int indexDepth;
    private final Map<Long, Feature> featuresById;
    private final QuadIndex quadIndex;

    private PlusMapDatabase(MapSpace mapSpace, int indexDepth, List<Feature> features, QuadIndex quadIndex) {
        this.mapSpace = mapSpace;
        this.indexDepth = indexDepth;
        this.featuresById = new HashMap<>();
        for (Feature feature : features) {
            this.featuresById.put(feature.id(), feature);
        }
        this.quadIndex = quadIndex;
    }

    public static Builder builder(MapSpace mapSpace) {
        return new Builder(mapSpace);
    }

    public static PlusMapDatabase open(Path path) throws IOException {
        PlusMapReader.LoadedPlusMap loaded = PlusMapReader.read(path);
        return new PlusMapDatabase(loaded.mapSpace(), loaded.indexDepth(), loaded.features(), loaded.quadIndex());
    }

    public MapSpace mapSpace() {
        return mapSpace;
    }

    public int indexDepth() {
        return indexDepth;
    }

    public List<Feature> features() {
        return featuresById.values().stream()
                .sorted((a, b) -> Long.compare(a.id(), b.id()))
                .toList();
    }

    public Feature feature(long id) {
        return featuresById.get(id);
    }

    public QuadIndex quadIndex() {
        return quadIndex;
    }

    public List<Feature> query(String dsl) {
        Query query = QueryParser.parse(dsl);
        return new QueryExecutor(mapSpace, indexDepth, featuresById, quadIndex).execute(query);
    }

    public void writeTo(Path path) throws IOException {
        PlusMapWriter.write(path, mapSpace, indexDepth, features(), quadIndex);
    }

    public static final class Builder {
        private final MapSpace mapSpace;
        private int indexDepth = 4;
        private final List<Feature> features = new ArrayList<>();

        private Builder(MapSpace mapSpace) {
            this.mapSpace = mapSpace;
        }

        public Builder indexDepth(int indexDepth) {
            if (indexDepth < 0) {
                throw new IllegalArgumentException("indexDepth must be >= 0");
            }
            this.indexDepth = indexDepth;
            return this;
        }

        public Builder addFeature(Feature feature) {
            features.add(feature);
            return this;
        }

        public Builder addFeatures(List<Feature> features) {
            this.features.addAll(features);
            return this;
        }

        public PlusMapDatabase build() {
            QuadIndex quadIndex = QuadIndexBuilder.build(mapSpace, indexDepth, features);
            return new PlusMapDatabase(mapSpace, indexDepth, List.copyOf(features), quadIndex);
        }

        public PlusMapDatabase buildAndWrite(Path path) throws IOException {
            PlusMapDatabase database = build();
            database.writeTo(path);
            return database;
        }
    }
}
