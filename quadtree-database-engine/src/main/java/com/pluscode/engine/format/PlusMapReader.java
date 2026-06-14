package com.pluscode.engine.format;

import com.pluscode.core.feature.Feature;
import com.pluscode.core.feature.GeometryKind;
import com.pluscode.core.feature.TerrainType;
import com.pluscode.core.spatial.Bounds;
import com.pluscode.core.spatial.MapSpace;
import com.pluscode.core.spatial.MapSpaceConvention;
import com.pluscode.core.spatial.Point2D;
import com.pluscode.engine.index.QuadIndex;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads a version-1 .plusmap file.
 */
public final class PlusMapReader {

    private PlusMapReader() {
    }

    public static LoadedPlusMap read(Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)) {
            return read(in);
        }
    }

    public static LoadedPlusMap read(InputStream in) throws IOException {
        PlusMapHeader header = readHeader(in);
        List<Feature> features = new ArrayList<>(header.featureCount());
        for (int i = 0; i < header.featureCount(); i++) {
            features.add(readFeature(in));
        }
        Map<String, List<Long>> postings = new LinkedHashMap<>();
        for (int i = 0; i < header.codeEntryCount(); i++) {
            CodeEntry entry = readCodeEntry(in);
            postings.put(entry.code(), entry.featureIds());
        }
        MapSpace mapSpace = MapSpace.of(
                header.bounds().minX(),
                header.bounds().maxX(),
                header.bounds().minY(),
                header.bounds().maxY(),
                header.convention());
        return new LoadedPlusMap(mapSpace, header.indexDepth(), features, new QuadIndex(postings));
    }

    private static PlusMapHeader readHeader(InputStream in) throws IOException {
        byte[] magic = readBytes(in, 4);
        if (!Arrays.equals(magic, PlusMapFormat.MAGIC)) {
            throw new IOException("Invalid .plusmap magic");
        }
        int version = readInt(in);
        if (version != PlusMapFormat.VERSION) {
            throw new IOException("Unsupported .plusmap version: " + version);
        }
        int conventionOrdinal = in.read();
        if (conventionOrdinal < 0) {
            throw new EOFException();
        }
        readBytes(in, 3);
        double minX = readDouble(in);
        double maxX = readDouble(in);
        double minY = readDouble(in);
        double maxY = readDouble(in);
        int indexDepth = readInt(in);
        int featureCount = readInt(in);
        int codeEntryCount = readInt(in);
        MapSpaceConvention convention = MapSpaceConvention.values()[conventionOrdinal];
        Bounds bounds = Bounds.of(minX, maxX, minY, maxY);
        return new PlusMapHeader(version, convention, bounds, indexDepth, featureCount, codeEntryCount);
    }

    private static Feature readFeature(InputStream in) throws IOException {
        long id = readLong(in);
        int kindOrdinal = in.read();
        int terrainOrdinal = in.read();
        readBytes(in, 6);
        int zOrder = readInt(in);
        String name = readString(in);
        String primaryCode = readString(in);
        int pointCount = readInt(in);
        List<Point2D> points = new ArrayList<>(pointCount);
        for (int i = 0; i < pointCount; i++) {
            points.add(Point2D.of(readDouble(in), readDouble(in)));
        }
        int propertyCount = readInt(in);
        Map<String, String> properties = new HashMap<>();
        for (int i = 0; i < propertyCount; i++) {
            properties.put(readString(in), readString(in));
        }
        GeometryKind kind = kindOrdinal == 1 ? GeometryKind.POLYGON : GeometryKind.POLYLINE;
        TerrainType terrainType = TerrainType.values()[terrainOrdinal];
        Feature.Builder builder = Feature.builder()
                .id(id)
                .kind(kind)
                .terrainType(terrainType)
                .name(name)
                .zOrder(zOrder)
                .points(points)
                .properties(properties);
        if (!primaryCode.isEmpty()) {
            builder.primaryCode(primaryCode);
        }
        return builder.build();
    }

    private static CodeEntry readCodeEntry(InputStream in) throws IOException {
        String code = readString(in);
        int idCount = readInt(in);
        List<Long> ids = new ArrayList<>(idCount);
        for (int i = 0; i < idCount; i++) {
            ids.add(readLong(in));
        }
        return new CodeEntry(code, List.copyOf(ids));
    }

    private static String readString(InputStream in) throws IOException {
        int length = readShort(in);
        byte[] bytes = readBytes(in, length);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static byte[] readBytes(InputStream in, int count) throws IOException {
        byte[] buffer = new byte[count];
        int read = 0;
        while (read < count) {
            int n = in.read(buffer, read, count - read);
            if (n < 0) {
                throw new EOFException();
            }
            read += n;
        }
        return buffer;
    }

    private static int readShort(InputStream in) throws IOException {
        int hi = in.read();
        int lo = in.read();
        if (hi < 0 || lo < 0) {
            throw new EOFException();
        }
        return (hi << 8) | lo;
    }

    private static int readInt(InputStream in) throws IOException {
        int b1 = in.read();
        int b2 = in.read();
        int b3 = in.read();
        int b4 = in.read();
        if (b1 < 0 || b2 < 0 || b3 < 0 || b4 < 0) {
            throw new EOFException();
        }
        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }

    private static long readLong(InputStream in) throws IOException {
        long hi = readInt(in) & 0xFFFFFFFFL;
        long lo = readInt(in) & 0xFFFFFFFFL;
        return (hi << 32) | lo;
    }

    private static double readDouble(InputStream in) throws IOException {
        return Double.longBitsToDouble(readLong(in));
    }

    public record LoadedPlusMap(MapSpace mapSpace, int indexDepth, List<Feature> features, QuadIndex quadIndex) {
    }

    private record CodeEntry(String code, List<Long> featureIds) {
    }
}
