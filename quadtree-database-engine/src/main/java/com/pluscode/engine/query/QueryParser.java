package com.pluscode.engine.query;

import com.pluscode.core.feature.TerrainType;

import java.util.Locale;

/**
 * Recursive-descent parser for the Phase 2 query DSL.
 */
public final class QueryParser {

    private final String input;
    private int pos;

    private QueryParser(String input) {
        this.input = input == null ? "" : input.trim();
        this.pos = 0;
    }

    public static Query parse(String dsl) {
        QueryParser parser = new QueryParser(dsl);
        Query query = parser.parseExpression();
        parser.skipWhitespace();
        if (parser.pos < parser.input.length()) {
            throw new IllegalArgumentException("Unexpected token at position " + parser.pos);
        }
        return query;
    }

    private Query parseExpression() {
        Query left = parseTerm();
        skipWhitespace();
        while (matchKeyword("OR")) {
            Query right = parseTerm();
            left = new Query.Or(left, right);
            skipWhitespace();
        }
        return left;
    }

    private Query parseTerm() {
        Query left = parseFactor();
        skipWhitespace();
        while (matchKeyword("AND")) {
            Query right = parseFactor();
            left = new Query.And(left, right);
            skipWhitespace();
        }
        return left;
    }

    private Query parseFactor() {
        skipWhitespace();
        if (matchKeyword("ALL")) {
            return new Query.All();
        }
        if (matchKeyword("CODE")) {
            return new Query.Code(readQuotedString());
        }
        if (matchKeyword("AT")) {
            double x = readNumber();
            double y = readNumber();
            return new Query.At(x, y);
        }
        if (matchKeyword("BOUNDS")) {
            double minX = readNumber();
            double minY = readNumber();
            double maxX = readNumber();
            double maxY = readNumber();
            return new Query.Bounds(minX, minY, maxX, maxY);
        }
        if (matchKeyword("TYPE")) {
            skipWhitespace();
            if (matchKeyword("IN")) {
                TerrainType first = readTerrainType();
                java.util.List<TerrainType> types = new java.util.ArrayList<>();
                types.add(first);
                skipWhitespace();
                while (pos < input.length() && !startsKeyword("AND") && !startsKeyword("OR") && peek() != ')') {
                    types.add(readTerrainType());
                    skipWhitespace();
                }
                Query combined = new Query.Type(types.getFirst());
                for (int i = 1; i < types.size(); i++) {
                    combined = new Query.Or(combined, new Query.Type(types.get(i)));
                }
                return combined;
            }
            return new Query.Type(readTerrainType());
        }
        if (matchKeyword("ID")) {
            return new Query.Id(readLong());
        }
        if (matchKeyword("NAME")) {
            return new Query.Name(readQuotedString());
        }
        if (peek() == '(') {
            pos++;
            Query inner = parseExpression();
            expect(')');
            return inner;
        }
        throw new IllegalArgumentException("Unexpected token at position " + pos);
    }

    private boolean matchKeyword(String keyword) {
        skipWhitespace();
        if (!startsKeyword(keyword)) {
            return false;
        }
        pos += keyword.length();
        if (pos < input.length() && isIdentPart(input.charAt(pos))) {
            throw new IllegalArgumentException("Unknown keyword at position " + pos);
        }
        return true;
    }

    private boolean startsKeyword(String keyword) {
        if (!input.regionMatches(true, pos, keyword, 0, keyword.length())) {
            return false;
        }
        if (pos > 0 && isIdentPart(input.charAt(pos - 1))) {
            return false;
        }
        return pos + keyword.length() >= input.length() || !isIdentPart(input.charAt(pos + keyword.length()));
    }

    private static boolean isIdentPart(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private String readQuotedString() {
        skipWhitespace();
        char quote = peek();
        if (quote != '"' && quote != '\'') {
            throw new IllegalArgumentException("Expected quoted string at position " + pos);
        }
        pos++;
        StringBuilder sb = new StringBuilder();
        while (pos < input.length()) {
            char c = input.charAt(pos++);
            if (c == quote) {
                return sb.toString();
            }
            if (c == '\\' && pos < input.length()) {
                sb.append(input.charAt(pos++));
            } else {
                sb.append(c);
            }
        }
        throw new IllegalArgumentException("Unterminated string at position " + pos);
    }

    private double readNumber() {
        skipWhitespace();
        int start = pos;
        if (peek() == '-' || peek() == '+') {
            pos++;
        }
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isDigit(c) || c == '.' || c == 'e' || c == 'E' || c == '-' || c == '+') {
                pos++;
            } else {
                break;
            }
        }
        if (start == pos) {
            throw new IllegalArgumentException("Expected number at position " + pos);
        }
        return Double.parseDouble(input.substring(start, pos));
    }

    private long readLong() {
        skipWhitespace();
        int start = pos;
        if (peek() == '-' || peek() == '+') {
            pos++;
        }
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            pos++;
        }
        if (start == pos) {
            throw new IllegalArgumentException("Expected integer at position " + pos);
        }
        return Long.parseLong(input.substring(start, pos));
    }

    private TerrainType readTerrainType() {
        skipWhitespace();
        int start = pos;
        while (pos < input.length() && isIdentPart(input.charAt(pos))) {
            pos++;
        }
        if (start == pos) {
            throw new IllegalArgumentException("Expected terrain type at position " + pos);
        }
        String token = input.substring(start, pos).toUpperCase(Locale.ROOT);
        try {
            return TerrainType.valueOf(token);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown terrain type: " + token);
        }
    }

    private void expect(char c) {
        skipWhitespace();
        if (peek() != c) {
            throw new IllegalArgumentException("Expected '" + c + "' at position " + pos);
        }
        pos++;
    }

    private char peek() {
        return pos < input.length() ? input.charAt(pos) : '\0';
    }

    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }
}
