package com.plusCode.quadtree;

public class  Quadrant {
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
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
        this.depth = (parent == null) ? 0 : parent.depth + 1;
    }

    public Quadrant getParent() {
        return parent;
    }

    public void setParent(Quadrant parent) {
        this.parent = parent;
    }

    public Quadrant getNe() { return ne; }
    public Quadrant getNw() { return nw; }
    public Quadrant getSe() { return se; }
    public Quadrant getSw() { return sw; }

    public double getMinLat() { return minLat; }
    public double getMinLon() { return minLon; }
    public double getMaxLat() { return maxLat; }
    public double getMaxLon() { return maxLon; }
    public double getMidLat() { return (getMinLat() + getMaxLat()) / 2; }
    public double getMidLon() { return (getMinLon() + getMaxLon()) / 2; }

    public int getDepth() { return depth; }

    public void subdivide() {
        double midLat = (minLat + maxLat) / 2;
        double midLon = (minLon + maxLon) / 2;

        nw = new Quadrant(this, midLat, maxLat, minLon, midLon);
        ne = new Quadrant(this, midLat, maxLat, midLon, maxLon);
        sw = new Quadrant(this, minLat, midLat, minLon, midLon);
        se = new Quadrant(this, minLat, midLat, midLon, maxLon);
    }

    public String encode(Quadrant root, int depth, double lat, double lon) {
        Quadrant curr = root;
        StringBuilder res = new StringBuilder();
        while(depth > 0) {
            curr.subdivide();
            if (lat >= curr.getMinLat() && lat < curr.getMidLat() && lon >= curr.getMinLon() && lon < curr.getMidLon()) {
                curr = curr.getSw();
                res.append(3);
            }
            else if (lat >= curr.getMidLat() && lat < curr.getMaxLat() && lon >= curr.getMinLon() && lon < curr.getMidLon()) {
                curr = curr.getNw();
                res.append(1);
            }
            else if (lat >= curr.getMinLat() && lat < curr.getMidLat() && lon >= curr.getMidLon() && lon < curr.getMaxLon()) {
                curr = curr.getSe();
                res.append(4);
            }
            else if (lat >= curr.getMidLat() && lat < curr.getMaxLat() && lon >= curr.getMidLon() && lon < curr.getMaxLon()) {
                curr = curr.getNe();
                res.append(2);
            }
            depth--;
        }
        return res.toString();
    }

    public static double[] decode(String encoded) {
        double[] res = new double[2];
        int i = 0;
        double maxLat = 90;
        double maxLon = 180;
        double minLat = -90;
        double minLon = -180;
        while(i < encoded.length()){
            double midLat = (minLat + maxLat) / 2;
            double midLon = (minLon + maxLon) / 2;
            if (encoded.charAt(i) == '1') {
                // NW
                maxLat = midLat;
                maxLon = midLon;
                // minLat and minLon stay the same
            } else if (encoded.charAt(i) == '2') {
                // NE
                maxLat = midLat;
                minLon = midLon;
                // minLat stays, maxLon stays
            } else if (encoded.charAt(i) == '3') {
                // SW
                minLat = midLat;
                maxLon = midLon;
                // maxLat stays, minLon stays
            } else if (encoded.charAt(i) == '4') {
                // SE
                minLat = midLat;
                minLon = midLon;
                // maxLat and maxLon stay the same
            }
            i++;
        }
        res[0] = (maxLat + minLat) / 2;
        res[1] = (maxLon + minLon) / 2;
        return res;
    }
}
