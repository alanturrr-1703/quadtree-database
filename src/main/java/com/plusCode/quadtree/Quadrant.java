package com.plusCode.quadtree;

/**
 * Quadrant class to encode latitude/longitude into a quad-based geocode,
 * and decode a quad-geocode back into approximate coordinates.
 */
public class Quadrant {
    private Quadrant ne;
    private Quadrant nw;
    private Quadrant se;
    private Quadrant sw;
    private Quadrant parent;

    private final int depth;
    private final double minLat;
    private final double minLon;
    private final double maxLat;
    private final double maxLon;

    public Quadrant(Quadrant parent, double minLat, double maxLat, double minLon, double maxLon) {
        this.parent = parent;
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.maxLon = maxLon;
        this.depth = (parent == null) ? 0 : parent.depth + 1;
    }

    public double getMinLat() { return minLat; }
    public double getMaxLat() { return maxLat; }
    public double getMinLon() { return minLon; }
    public double getMaxLon() { return maxLon; }

    public double getMidLat() { return (minLat + maxLat) / 2; }
    public double getMidLon() { return (minLon + maxLon) / 2; }

    public Quadrant getNe() { return ne; }
    public Quadrant getNw() { return nw; }
    public Quadrant getSe() { return se; }
    public Quadrant getSw() { return sw; }

    /**
     * Subdivides the current quadrant into four child quadrants.
     */
    public void subdivide() {
        double midLat = getMidLat();
        double midLon = getMidLon();

        nw = new Quadrant(this, midLat, maxLat, minLon, midLon);
        ne = new Quadrant(this, midLat, maxLat, midLon, maxLon);
        sw = new Quadrant(this, minLat, midLat, minLon, midLon);
        se = new Quadrant(this, minLat, midLat, midLon, maxLon);
    }

    /**
     * Encodes a latitude and longitude into a quad-based string code.
     *
     * @param root the root quadrant
     * @param depth how many levels deep to encode
     * @param lat latitude
     * @param lon longitude
     * @return encoded quad code
     */
    public String encode(Quadrant root, int depth, double lat, double lon) {
        Quadrant curr = root;
        StringBuilder res = new StringBuilder();

        while (depth > 0) {
            curr.subdivide();
            double midLat = curr.getMidLat();
            double midLon = curr.getMidLon();

            if (lat < midLat && lon < midLon) { // SW = 3
                curr = curr.getSw();
                res.append('3');
            } else if (lat >= midLat && lon < midLon) { // NW = 1
                curr = curr.getNw();
                res.append('1');
            } else if (lat < midLat && lon >= midLon) { // SE = 4
                curr = curr.getSe();
                res.append('4');
            } else { // NE = 2
                curr = curr.getNe();
                res.append('2');
            }

            depth--;
        }

        return res.toString();
    }

    /**
     * Decodes a quad-based string code back into an approximate [lat, lon] center.
     *
     * @param encoded the quad code
     * @return center point of the decoded area [lat, lon]
     */
    public static double[] decode(String encoded) {
        double maxLat = 90;
        double minLat = -90;
        double maxLon = 180;
        double minLon = -180;

        for (int i = 0; i < encoded.length(); i++) {
            double midLat = (minLat + maxLat) / 2;
            double midLon = (minLon + maxLon) / 2;
            char c = encoded.charAt(i);

            switch (c) {
                case '1': // NW
                    minLat = midLat;
                    maxLon = midLon;
                    break;
                case '2': // NE
                    minLat = midLat;
                    minLon = midLon;
                    break;
                case '3': // SW
                    maxLat = midLat;
                    maxLon = midLon;
                    break;
                case '4': // SE
                    maxLat = midLat;
                    minLon = midLon;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid code character: " + c);
            }
        }

        return new double[]{(minLat + maxLat) / 2, (minLon + maxLon) / 2};
    }
}
