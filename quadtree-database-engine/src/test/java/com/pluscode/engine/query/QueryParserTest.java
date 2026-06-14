package com.pluscode.engine.query;

import com.pluscode.core.feature.TerrainType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Run: mvn -pl quadtree-database-engine -Dtest=QueryParserTest test
 */
class QueryParserTest {

    @Test
    void parseAll() {
        assertInstanceOf(Query.All.class, QueryParser.parse("ALL"));
    }

    @Test
    void parseCode() {
        Query.Code query = (Query.Code) QueryParser.parse("CODE \"1321\"");
        assertEquals("1321", query.code());
    }

    @Test
    void parseAt() {
        Query.At query = (Query.At) QueryParser.parse("AT 12.5 30");
        assertEquals(12.5, query.x());
        assertEquals(30.0, query.y());
    }

    @Test
    void parseBounds() {
        Query.Bounds query = (Query.Bounds) QueryParser.parse("BOUNDS 0 0 100 100");
        assertEquals(0, query.minX());
        assertEquals(100, query.maxY());
    }

    @Test
    void parseType() {
        Query.Type query = (Query.Type) QueryParser.parse("TYPE road");
        assertEquals(TerrainType.ROAD, query.terrainType());
    }

    @Test
    void parseTypeIn() {
        Query query = QueryParser.parse("TYPE IN ROAD WATER");
        assertInstanceOf(Query.Or.class, query);
    }

    @Test
    void parseAnd() {
        Query.And query = (Query.And) QueryParser.parse("TYPE ROAD AND BOUNDS 0 0 50 50");
        assertInstanceOf(Query.Type.class, query.left());
        assertInstanceOf(Query.Bounds.class, query.right());
    }

    @Test
    void parseParentheses() {
        Query query = QueryParser.parse("(TYPE WATER)");
        assertInstanceOf(Query.Type.class, query);
    }

    @Test
    void rejectsUnknownKeyword() {
        assertThrows(IllegalArgumentException.class, () -> QueryParser.parse("WHERE TYPE ROAD"));
    }
}
