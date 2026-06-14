package com.pluscode.engine.index;

import com.pluscode.core.feature.Feature;
import com.pluscode.core.geometry.Geometry2D;
import com.pluscode.core.spatial.Bounds;
import com.pluscode.core.spatial.MapSpace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Builds quad code postings for features at a fixed index depth.
 */
public final class QuadIndexBuilder {

    private QuadIndexBuilder() {
    }

    public static QuadIndex build(MapSpace mapSpace, int indexDepth, List<Feature> features) {
        if (indexDepth < 0) {
            throw new IllegalArgumentException("indexDepth must be >= 0");
        }
        Map<String, TreeSet<Long>> postings = new HashMap<>();
        for (Feature feature : features) {
            indexFeature(mapSpace, indexDepth, feature, postings);
        }
        Map<String, List<Long>> frozen = new HashMap<>();
        for (Map.Entry<String, TreeSet<Long>> entry : postings.entrySet()) {
            frozen.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return new QuadIndex(frozen);
    }

    private static void indexFeature(MapSpace mapSpace, int indexDepth, Feature feature,
                                     Map<String, TreeSet<Long>> postings) {
        Bounds bbox = Geometry2D.boundingBox(feature.points());
        for (String code : mapSpace.enumerateCodesInBounds(bbox, indexDepth)) {
            Bounds cell = mapSpace.boundsForCode(code);
            if (Geometry2D.featureIntersectsCell(feature.points(), feature.isClosed(), cell)) {
                postings.computeIfAbsent(code, ignored -> new TreeSet<>()).add(feature.id());
            }
        }
    }
}
