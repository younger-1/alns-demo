package younger.vrp.alns.revive;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.operation.ALNSAbstractOperation;
import younger.vrp.alns.recreate.IALNSRecreate;
import younger.vrp.alns.ruin.IALNSRuin;
import younger.vrp.instance.Cost;
import younger.vrp.instance.Node;
import younger.vrp.instance.Route;

public class SpreadRevive extends ALNSAbstractOperation implements IALNSRuin, IALNSRecreate {

    private List<Sector> sectors;

    private SpreadRevive() {
    }

    public static SpreadRevive of() {
        return new SpreadRevive();
    }

    @Override
    public ALNSSolution ruin(ALNSSolution s, int removeNum) throws Exception {
        return s;
    }

    @Override
    public ALNSSolution recreate(ALNSSolution s) throws Exception {
        return s;
    }

    private void revive_impl(ALNSSolution s, Sector sector) throws Exception {
        List<Coord> coords = sector.getAll().stream().sorted((a, b) -> a.rad > b.rad ? -1 : 1)
                .collect(Collectors.toList());
        for (int i = 0; i < coords.size() - 1; i++) {
            int iRouteFarBest = -1;
            int iRouteNearBest = -1;
            int iNodeFarBest = -1;
            int iNodeNearBest = -1;
            double bestTotal = 0;
            int iRouteFar = coords.get(i).index;
            int iRouteNear = coords.get(i + 1).index;
            Route routeFar = s.routes.get(iRouteFar);
            Route routeNear = s.routes.get(iRouteNear);
            for (int iNodeFar = 1; iNodeFar < routeFar.getSize() - 1; iNodeFar++) {
                Node evaluatedNode = routeFar.getNode(iNodeFar);
                Cost costFar = s.evaluateRemoveCustomer(iRouteFar, iNodeFar);
                double total_down = s.costs.getTotal() - costFar.getTotal();
                for (int iNodeNear = 1; iNodeNear < routeNear.getSize(); iNodeNear++) {
                    Cost costNear = s.evaluateInsertCustomer(iRouteNear, iNodeNear, evaluatedNode);
                    double total_up = costNear.getTotal() - s.costs.getTotal();
                    if (total_down - total_up > bestTotal) {
                        bestTotal = total_down - total_up;
                        iRouteFarBest = iRouteFar;
                        iRouteNearBest = iRouteNear;
                        iNodeFarBest = iNodeFar;
                        iNodeNearBest = iNodeNear;
                    }
                }
            }
            if (bestTotal != 0) {
                s.removeCustomer(iRouteFarBest, iNodeFarBest);
                Node RearrangedNode = s.removeNodes.remove(0);
                s.insertCustomer(iRouteNearBest, iNodeNearBest, RearrangedNode);
            }
        }
    }


    public void revive(ALNSSolution[] ss) throws Exception {

        for (ALNSSolution s : ss) {
            this.revive_helper(s);
            for (Sector sector : sectors) {
                revive_impl(s, sector);
            }
        }
    }

    private void revive_helper(ALNSSolution s) {

        // double x_min = coords.stream().mapToDouble(c -> c.x).min().getAsDouble();
        // double y_min = coords.stream().mapToDouble(c -> c.y).min().getAsDouble();
        // double x_diff = coords.stream().mapToDouble(c -> c.x).max().getAsDouble()
        //         - coords.stream().mapToDouble(c -> c.x).min().getAsDouble();
        // double y_diff = coords.stream().mapToDouble(c -> c.y).max().getAsDouble()
        //         - coords.stream().mapToDouble(c -> c.y).min().getAsDouble();

        // * Larger the sector, easier the spread.
        int sectors_num = (int) Math.rint(Math.cbrt(s.routes.size())) + 1;
        int vehicles_num = s.routes.size() / (sectors_num - 1);
        double interval = 2 * Math.PI / sectors_num;

        Coord.resetIndex();
        List<Coord> coords = new ArrayList<>();
        // Queue<Coord> coords = new PriorityQueue<>((a, b) -> a.rad < b.rad ? -1 : 1);
        Node depot = s.routes.get(0).getNode(0);
        s.routes.stream().filter(r -> r.getSize() > 2).forEachOrdered(route -> {
            double x = route.getRoute().stream().mapToDouble(n -> n.getX()).average().getAsDouble();
            double y = route.getRoute().stream().mapToDouble(n -> n.getY()).average().getAsDouble();
            // optimize: may change depot.
            double dist = getDist(depot, x, y);
            double rad = getRad(depot, x, y);
            coords.add(Coord.of(x, y, rad, dist));
        });
        List<Coord> coordsNew = coords.stream().sorted((a, b) -> a.rad < b.rad ? -1 : 1).collect(Collectors.toList());

        double[] radDiff = IntStream.range(0, coords.size())
                .mapToDouble(i -> i == 0
                        ? coordsNew.get(0).rad - coordsNew.get(coords.size() - 1).rad
                        : coordsNew.get(i).rad - coordsNew.get(i - 1).rad)
                .toArray();

        // IntStream.range(0, coords.size()).filter(i -> radDiff[i] > interval / 4 && radDiff[i + 1] > interval / 4)
        //         .forEach(i -> coordsCopy.remove(i));

        sectors = new ArrayList<>();
        Sector currentSector = Sector.of();
        for (int i = 0; i < coordsNew.size(); i++) {
            Coord coord = coordsNew.get(i);
            boolean addToExistedSector = false;
            if (currentSector.size() < vehicles_num && currentSector.getRange() < interval) {
                addToExistedSector = true;
            }
            if (radDiff[i] > interval / 4) {
                addToExistedSector = false;
            }
            if (addToExistedSector) {
                currentSector.add(coord);
            } else {
                if (currentSector.getRange() > interval / 2 || currentSector.size() > vehicles_num / 6) {
                    sectors.add(currentSector);
                }
                currentSector = Sector.of();
                currentSector.add(coord);
            }
        }

    }

    private double getDist(Node depot, double x, double y) {
        final double EARTH_RADIUS = 6378.137;

        double lat_1 = radOf(depot.getY());
        double lat_2 = radOf(y);
        double ver = lat_1 - lat_2;
        double hor = radOf(depot.getX()) - radOf(x);
        double s = 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin(ver / 2), 2) + Math.cos(lat_1) * Math.cos(lat_2) * Math.pow(Math.sin(hor / 2), 2)));
        s = s * EARTH_RADIUS;
        s = (s * 10000) / 10.0;
        return (double) (Math.round(s * 100) / 100.0);
    }

    private double radOf(double degree) {
        return degree * Math.PI / 180.0;
    }

    private double getRad(Node depot, double x, double y) {
        if (x - depot.getX() == 0 && y - depot.getY() == 0) {
            // throw new IllegalArgumentException("SpreadRevive.getRad: Two nodes are overlapping");
            System.err.println("SpreadRevive.getRad: Two nodes are overlapping");
            return 0;
        } else if (x - depot.getX() == 0) {
            return (y - depot.getY() > 0) ? Math.PI / 2. : 3 * Math.PI / 2.;
        }
        double slope = (y - depot.getY()) / (x - depot.getX());
        double rad = (x - depot.getX() >= 0) ? Math.atan(slope) : Math.atan(slope) + Math.PI;
        // -90~270 to 0~360
        rad = rad > 0 ? rad : rad + 2 * Math.PI;
        return rad;
    }

}

class Coord {

    private static int static_index = 0;
    public int index;
    public double x;
    public double y;
    public double rad;
    public double dist;

    private Coord(double x, double y, double rad, double dist) {
        this.index = static_index;
        this.x = x;
        this.y = y;
        this.rad = rad;
        this.dist = dist;
        static_index += 1;
    }

    public static Coord of(double x, double y, double rad, double dist) {
        return new Coord(x, y, rad, dist);
    }

    public static void resetIndex() {
        static_index = 0;
    }
}

class Sector {

    private List<Coord> routes;

    private Sector() {
        routes = new ArrayList<>();
    }

    public static Sector of() {
        return new Sector();
    }

    public void add(Coord co) {
        this.routes.add(co);
    }

    public Coord get(int i) {
        return this.routes.get(i);
    }

    public int size() {
        return this.routes.size();
    }

    public double getRange() {
        return size() == 0 ? 0 : routes.get(size() - 1).rad - routes.get(0).rad;
    }

    public List<Coord> getAll() {
        return routes;
    }
}