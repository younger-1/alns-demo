package younger.vrp.alns.ruin.destroy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.ruin.ALNSAbstractRuin;
import younger.vrp.alns.ruin.IALNSRuin;
import younger.vrp.instance.Node;
import younger.vrp.instance.Route;

public class BrotherRouteDestroy extends ALNSAbstractRuin implements IALNSRuin {

    private int maxCustomerNum;
    private int vehicleCapacity;
    private double travelTime;

    private BrotherRouteDestroy() {
        random = new Random();
        maxCustomerNum = vrpCate.getCons().getMaxCustomerNum();
        vehicleCapacity = vrpCate.getCons().getVehicleCapacity();
        travelTime = vrpCate.getCons().getTimeLimit();
    }

    public static BrotherRouteDestroy of() {
        return new BrotherRouteDestroy();
    }

    @Override
    public ALNSSolution ruin(ALNSSolution s, int nodes) throws Exception {

        int[] routes = random.ints(0, s.routes.size()).distinct().limit((long) Math.sqrt(s.routes.size())).toArray();
        Optional<Route> centerRoute_or_not = Arrays.stream(routes).mapToObj(x -> s.routes.get(x))
                .filter(r -> r.getSize() > 2)
                .reduce((a, b) -> getDebtSpace(a) > getDebtSpace(b) ? a : b);

        if (centerRoute_or_not.isEmpty()) {
            return s;
        }
        Route centerRoute = centerRoute_or_not.get();

        Map<Route, Integer> route_num = new HashMap<>();

        for (int i = 1; i < centerRoute.getSize() - 1; i++) {
            Route brother = getBrotherRoute(centerRoute, centerRoute.getNode(i), s);
            route_num.merge(brother, 1, Integer::sum);
        }

        for (Route route : route_num.keySet()) {
            if (route_num.get(route) != 1) {
                s.removeAllCustomerOfRoute(route);
            }
        }

        s.removeAllCustomerOfRoute(centerRoute);
        return s;
    }

    private Route getBrotherRoute(Route route, Node node, ALNSSolution s) {
        int[] d = s.distance[node.getId()];
        Route brother = s.routes.stream()
                .filter(r -> r.getId() != route.getId() && r.getSize() > 2)
                .reduce((a, b) -> minDist(a, d) < minDist(b, d) ? a : b).get();
        return brother;
    }

    private double minDist(Route route, int[] d) {
        double dist = route.getRoute().stream().filter(n -> n.getId() != 0)
                .mapToDouble(n -> d[n.getId()])
                .reduce(Double::min).getAsDouble();
        return dist;
    }

    private double getDebtSpace(Route r) {
        if (r.costs.getNodeVio() == 0 && r.costs.getLoadVio() == 0 && r.costs.getTimeVio() == 0) {
            return 0;
        }
        double nodeDebt = -(maxCustomerNum - (r.getSize() - 2)) / (double) maxCustomerNum;
        double loadDebt = -(vehicleCapacity - r.costs.getLoad()) / (double) vehicleCapacity;
        double timeDebt = -(travelTime - r.costs.getTime()) / travelTime;
        return Math.max(Math.max(nodeDebt, loadDebt), timeDebt);
    }

}
