package com.pluscode.core.geometry;

import com.pluscode.core.spatial.Bounds;
import com.pluscode.core.spatial.Point2D;

import java.util.List;

/**
 * 2D geometry helpers for indexing roads and terrain polygons.
 */
public final class Geometry2D {

    private static final double EPSILON = 1e-9;

    private Geometry2D() {
    }

    public static Bounds boundingBox(List<Point2D> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("points must not be empty");
        }
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (Point2D p : points) {
            minX = Math.min(minX, p.x());
            maxX = Math.max(maxX, p.x());
            minY = Math.min(minY, p.y());
            maxY = Math.max(maxY, p.y());
        }
        if (minX == maxX) {
            maxX = minX + EPSILON;
        }
        if (minY == maxY) {
            maxY = minY + EPSILON;
        }
        return Bounds.of(minX, maxX, minY, maxY);
    }

    public static Point2D centroid(List<Point2D> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("points must not be empty");
        }
        double sumX = 0;
        double sumY = 0;
        for (Point2D p : points) {
            sumX += p.x();
            sumY += p.y();
        }
        return Point2D.of(sumX / points.size(), sumY / points.size());
    }

    public static boolean pointInPolygon(double x, double y, List<Point2D> polygon) {
        if (polygon == null || polygon.size() < 3) {
            return false;
        }
        boolean inside = false;
        int n = polygon.size();
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygon.get(i).x();
            double yi = polygon.get(i).y();
            double xj = polygon.get(j).x();
            double yj = polygon.get(j).y();
            boolean intersect = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) {
                inside = !inside;
            }
        }
        return inside;
    }

    public static boolean polylineIntersectsRect(List<Point2D> polyline, Bounds rect) {
        if (polyline == null || polyline.size() < 2) {
            return false;
        }
        for (int i = 0; i < polyline.size() - 1; i++) {
            Point2D a = polyline.get(i);
            Point2D b = polyline.get(i + 1);
            if (segmentIntersectsRect(a.x(), a.y(), b.x(), b.y(), rect)) {
                return true;
            }
        }
        for (Point2D p : polyline) {
            if (rect.containsInclusive(p.x(), p.y())) {
                return true;
            }
        }
        return false;
    }

    public static boolean polygonIntersectsRect(List<Point2D> polygon, Bounds rect) {
        if (polygon == null || polygon.size() < 3) {
            return false;
        }
        Point2D center = centroid(polygon);
        if (rect.containsInclusive(center.x(), center.y()) && pointInPolygon(center.x(), center.y(), polygon)) {
            return true;
        }
        for (Point2D p : polygon) {
            if (rect.containsInclusive(p.x(), p.y())) {
                return true;
            }
        }
        double[] xs = {rect.minX(), rect.maxX(), rect.maxX(), rect.minX(), rect.centerX()};
        double[] ys = {rect.minY(), rect.minY(), rect.maxY(), rect.maxY(), rect.centerY()};
        for (int i = 0; i < xs.length; i++) {
            if (pointInPolygon(xs[i], ys[i], polygon)) {
                return true;
            }
        }
        int n = polygon.size();
        for (int i = 0; i < n; i++) {
            Point2D a = polygon.get(i);
            Point2D b = polygon.get((i + 1) % n);
            if (segmentIntersectsRect(a.x(), a.y(), b.x(), b.y(), rect)) {
                return true;
            }
        }
        return false;
    }

    public static boolean featureIntersectsCell(List<Point2D> points, boolean closed, Bounds cell) {
        if (points == null || points.isEmpty()) {
            return false;
        }
        if (closed) {
            return polygonIntersectsRect(points, cell);
        }
        return polylineIntersectsRect(points, cell);
    }

    public static boolean segmentIntersectsRect(double x1, double y1, double x2, double y2, Bounds rect) {
        if (rect.containsInclusive(x1, y1) || rect.containsInclusive(x2, y2)) {
            return true;
        }
        return segmentsIntersect(x1, y1, x2, y2, rect.minX(), rect.minY(), rect.maxX(), rect.minY())
                || segmentsIntersect(x1, y1, x2, y2, rect.maxX(), rect.minY(), rect.maxX(), rect.maxY())
                || segmentsIntersect(x1, y1, x2, y2, rect.maxX(), rect.maxY(), rect.minX(), rect.maxY())
                || segmentsIntersect(x1, y1, x2, y2, rect.minX(), rect.maxY(), rect.minX(), rect.minY());
    }

    private static boolean segmentsIntersect(double x1, double y1, double x2, double y2,
                                             double x3, double y3, double x4, double y4) {
        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (Math.abs(d) < 1e-12) {
            return false;
        }
        double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / d;
        double u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / d;
        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
    }
}
