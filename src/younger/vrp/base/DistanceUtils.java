package younger.vrp.base;

public class DistanceUtils {

    private static final double EARTH_RADIUS = 6378.137;

    private static double radOf(double degree) {
        return degree * Math.PI / 180.0;
    }

    public static int getDistance(double fLng, double fLat, double tLng, double tLat) {

        double lat_1 = radOf(fLat);
        double lat_2 = radOf(tLat);
        double ver = lat_1 - lat_2;
        double hor = radOf(fLng) - radOf(tLng);
        double s = 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin(ver / 2), 2) + Math.cos(lat_1) * Math.cos(lat_2) * Math.pow(Math.sin(hor / 2), 2)));
        s = s * EARTH_RADIUS;
        s = (s * 10000) / 10.0;

        int lineDistance = (int) (Math.round(s * 100) / 100.0);
        return lineDistance;
    }

    public static int getEuclideanDistance(double x1, double y1, double x2, double y2) {
        return (int) (Math.round(Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) * 100) / 100.0);
    }
}
