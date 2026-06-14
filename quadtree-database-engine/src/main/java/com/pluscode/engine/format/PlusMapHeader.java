package com.pluscode.engine.format;

import com.pluscode.core.spatial.Bounds;
import com.pluscode.core.spatial.MapSpaceConvention;

/**
 * Parsed header of a .plusmap file.
 */
public record PlusMapHeader(
        int version,
        MapSpaceConvention convention,
        Bounds bounds,
        int indexDepth,
        int featureCount,
        int codeEntryCount) {
}
