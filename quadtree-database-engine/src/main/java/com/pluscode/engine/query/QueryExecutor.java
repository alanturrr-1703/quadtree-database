package com.pluscode.engine.query;

import com.pluscode.core.feature.Feature;
import com.pluscode.core.spatial.Bounds;
import com.pluscode.core.spatial.MapSpace;
import com.pluscode.engine.index.QuadCodeLookup;
import com.pluscode.engine.index.QuadIndex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Evaluates parsed queries against an in-memory database snapshot.
 */
public final class QueryExecutor {

    private final MapSpace mapSpace;
    private final int indexDepth;
    private final Map<Long, Feature> featuresById;
    private final QuadIndex quadIndex;

    public QueryExecutor(MapSpace mapSpace, int indexDepth, Map<Long, Feature> featuresById, QuadIndex quadIndex) {
        this.mapSpace = mapSpace;
        this.indexDepth = indexDepth;
        this.featuresById = featuresById;
        this.quadIndex = quadIndex;
    }

    public List<Feature> execute(Query query) {
        Set<Long> ids = evaluateIds(query);
        return ids.stream()
                .map(featuresById::get)
                .filter(feature -> feature != null)
                .sorted((a, b) -> {
                    int z = Integer.compare(a.zOrder(), b.zOrder());
                    return z != 0 ? z : Long.compare(a.id(), b.id());
                })
                .collect(Collectors.toList());
    }

    private Set<Long> evaluateIds(Query query) {
        return switch (query) {
            case Query.All ignored -> new LinkedHashSet<>(featuresById.keySet());
            case Query.Code code -> new LinkedHashSet<>(quadIndex.featureIdsForCode(code.code()));
            case Query.At at -> {
                String code = QuadCodeLookup.codeContainingPoint(mapSpace, at.x(), at.y(), indexDepth);
                yield new LinkedHashSet<>(quadIndex.featureIdsForCode(code));
            }
            case Query.Bounds bounds -> idsForBounds(bounds);
            case Query.Type type -> filterIds(featuresById.values(), f -> f.terrainType() == type.terrainType());
            case Query.Id id -> featuresById.containsKey(id.featureId())
                    ? Set.of(id.featureId())
                    : Set.of();
            case Query.Name name -> filterIds(featuresById.values(), f -> f.name().equals(name.name()));
            case Query.And and -> intersect(evaluateIds(and.left()), evaluateIds(and.right()));
            case Query.Or or -> union(evaluateIds(or.left()), evaluateIds(or.right()));
        };
    }

    private Set<Long> idsForBounds(Query.Bounds bounds) {
        Bounds region = Bounds.of(bounds.minX(), bounds.maxX(), bounds.minY(), bounds.maxY());
        Set<Long> ids = new LinkedHashSet<>();
        for (String code : mapSpace.enumerateCodesInBounds(region, indexDepth)) {
            ids.addAll(quadIndex.featureIdsForCode(code));
        }
        return ids;
    }

    private static Set<Long> filterIds(Collection<Feature> features, Predicate<Feature> predicate) {
        Set<Long> ids = new LinkedHashSet<>();
        for (Feature feature : features) {
            if (predicate.test(feature)) {
                ids.add(feature.id());
            }
        }
        return ids;
    }

    private static Set<Long> intersect(Set<Long> left, Set<Long> right) {
        Set<Long> result = new LinkedHashSet<>(left);
        result.retainAll(right);
        return result;
    }

    private static Set<Long> union(Set<Long> left, Set<Long> right) {
        Set<Long> result = new LinkedHashSet<>(left);
        result.addAll(right);
        return result;
    }
}
