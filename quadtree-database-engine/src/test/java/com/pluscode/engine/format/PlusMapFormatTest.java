package com.pluscode.engine.format;

import com.pluscode.core.feature.Feature;
import com.pluscode.core.feature.GeometryKind;
import com.pluscode.core.feature.TerrainType;
import com.pluscode.core.spatial.MapSpace;
import com.pluscode.core.spatial.Point2D;
import com.pluscode.engine.index.QuadIndexBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Run: mvn -pl quadtree-database-engine -Dtest=PlusMapFormatTest test
 */
class PlusMapFormatTest {

    @Test
    void writeAndReadRoundTrip(@TempDir Path tempDir) throws Exception {
        MapSpace space = MapSpace.of(0, 200, 0, 150);
        Feature feature = Feature.builder()
                .id(7)
                .kind(GeometryKind.POLYGON)
                .terrainType(TerrainType.FOREST)
                .name("North Woods")
                .zOrder(3)
                .points(List.of(
                        Point2D.of(10, 10),
                        Point2D.of(40, 10),
                        Point2D.of(25, 35)))
                .primaryCode("121")
                .property("density", "high")
                .build();
        var index = QuadIndexBuilder.build(space, 2, List.of(feature));
        Path file = tempDir.resolve("forest.plusmap");

        PlusMapWriter.write(file, space, 2, List.of(feature), index);
        PlusMapReader.LoadedPlusMap loaded = PlusMapReader.read(file);

        assertEquals(2, loaded.indexDepth());
        assertEquals(1, loaded.features().size());
        Feature read = loaded.features().getFirst();
        assertEquals(7, read.id());
        assertEquals("North Woods", read.name());
        assertEquals("high", read.property("density"));
        assertEquals(1, loaded.quadIndex().postings().size());
    }

    @Test
    void rejectsInvalidMagic(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("bad.plusmap");
        java.nio.file.Files.write(file, new byte[] {'B', 'A', 'D', '!'});
        assertThrows(java.io.IOException.class, () -> PlusMapReader.read(file));
    }
}
