package com.pluscode.engine.format;

import com.pluscode.core.feature.Feature;
import com.pluscode.core.feature.GeometryKind;
import com.pluscode.core.feature.TerrainType;
import com.pluscode.core.spatial.MapSpace;
import com.pluscode.core.spatial.Point2D;
import com.pluscode.engine.index.QuadIndex;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Writes a version-1 .plusmap file.
 */
public final class PlusMapWriter {

    private PlusMapWriter() {
    }

    public static void write(Path path, MapSpace mapSpace, int indexDepth, List<Feature> features, QuadIndex quadIndex)
            throws IOException {
        try (OutputStream out = Files.newOutputStream(path)) {
            write(out, mapSpace, indexDepth, features, quadIndex);
        }
    }

    public static void write(OutputStream out, MapSpace mapSpace, int indexDepth, List<Feature> features,
                             QuadIndex quadIndex) throws IOException {
        writeHeader(out, mapSpace, indexDepth, features.size(), quadIndex.postings().size());
        for (Feature feature : features) {
            writeFeature(out, feature);
        }
        for (Map.Entry<String, List<Long>> entry : quadIndex.postings().entrySet()) {
            writeCodeEntry(out, entry.getKey(), entry.getValue());
        }
    }

    private static void writeHeader(OutputStream out, MapSpace mapSpace, int indexDepth, int featureCount,
                                    int codeEntryCount) throws IOException {
        out.write(PlusMapFormat.MAGIC);
        writeInt(out, PlusMapFormat.VERSION);
        out.write(mapSpace.convention().ordinal());
        writePadding(out, 3);
        writeDouble(out, mapSpace.bounds().minX());
        writeDouble(out, mapSpace.bounds().maxX());
        writeDouble(out, mapSpace.bounds().minY());
        writeDouble(out, mapSpace.bounds().maxY());
        writeInt(out, indexDepth);
        writeInt(out, featureCount);
        writeInt(out, codeEntryCount);
    }

    private static void writeFeature(OutputStream out, Feature feature) throws IOException {
        writeLong(out, feature.id());
        out.write(feature.kind() == GeometryKind.POLYGON ? 1 : 0);
        out.write(feature.terrainType().ordinal());
        writePadding(out, 6);
        writeInt(out, feature.zOrder());
        writeString(out, feature.name());
        writeString(out, feature.primaryCode() == null ? "" : feature.primaryCode());
        writeInt(out, feature.points().size());
        for (Point2D point : feature.points()) {
            writeDouble(out, point.x());
            writeDouble(out, point.y());
        }
        Map<String, String> properties = feature.properties();
        writeInt(out, properties.size());
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            writeString(out, entry.getKey());
            writeString(out, entry.getValue());
        }
    }

    private static void writeCodeEntry(OutputStream out, String code, List<Long> featureIds) throws IOException {
        writeString(out, code);
        writeInt(out, featureIds.size());
        for (Long id : featureIds) {
            writeLong(out, id);
        }
    }

    private static void writeString(OutputStream out, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > Short.MAX_VALUE) {
            throw new IllegalArgumentException("String too long: " + value.length());
        }
        writeShort(out, bytes.length);
        out.write(bytes);
    }

    private static void writePadding(OutputStream out, int count) throws IOException {
        out.write(new byte[count]);
    }

    private static void writeShort(OutputStream out, int value) throws IOException {
        out.write((value >>> 8) & 0xFF);
        out.write(value & 0xFF);
    }

    private static void writeInt(OutputStream out, int value) throws IOException {
        out.write((value >>> 24) & 0xFF);
        out.write((value >>> 16) & 0xFF);
        out.write((value >>> 8) & 0xFF);
        out.write(value & 0xFF);
    }

    private static void writeLong(OutputStream out, long value) throws IOException {
        writeInt(out, (int) (value >>> 32));
        writeInt(out, (int) value);
    }

    private static void writeDouble(OutputStream out, double value) throws IOException {
        writeLong(out, Double.doubleToLongBits(value));
    }
}
