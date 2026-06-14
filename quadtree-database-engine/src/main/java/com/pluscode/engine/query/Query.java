package com.pluscode.engine.query;

/**
 * Parsed query expression AST.
 */
public sealed interface Query permits Query.All, Query.And, Query.At, Query.Bounds, Query.Code, Query.Id, Query.Name, Query.Or, Query.Type {

    record All() implements Query {
    }

    record Code(String code) implements Query {
    }

    record At(double x, double y) implements Query {
    }

    record Bounds(double minX, double minY, double maxX, double maxY) implements Query {
    }

    record Type(com.pluscode.core.feature.TerrainType terrainType) implements Query {
    }

    record Id(long featureId) implements Query {
    }

    record Name(String name) implements Query {
    }

    record And(Query left, Query right) implements Query {
    }

    record Or(Query left, Query right) implements Query {
    }
}
