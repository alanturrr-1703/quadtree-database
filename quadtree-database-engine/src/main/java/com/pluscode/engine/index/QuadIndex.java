package com.pluscode.engine.index;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Quad code → feature id postings, sorted by code for lookup.
 */
public final class QuadIndex {

    private final Map<String, List<Long>> postings;

    public QuadIndex(Map<String, List<Long>> postings) {
        this.postings = Collections.unmodifiableMap(new TreeMap<>(postings));
    }

    public Map<String, List<Long>> postings() {
        return postings;
    }

    public List<Long> featureIdsForCode(String code) {
        return postings.getOrDefault(code, List.of());
    }
}
