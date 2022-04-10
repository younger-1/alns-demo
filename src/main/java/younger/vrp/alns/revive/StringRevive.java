package younger.vrp.alns.revive;

import java.util.Arrays;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.operation.ALNSAbstractOperation;
import younger.vrp.alns.recreate.IALNSRecreate;
import younger.vrp.alns.recreate.balance.GreedyBalance;
import younger.vrp.alns.recreate.repair.GreedyRepair;
import younger.vrp.alns.ruin.IALNSRuin;
import younger.vrp.instance.Route;

public class StringRevive extends ALNSAbstractOperation implements IALNSRuin, IALNSRecreate {

    private IALNSRecreate recreate_ops;
    private int stage;
    private int maxCustomerNum;
    private int vehicleCapacity;
    private double travelTime;

    private StringRevive(int stage) {
        this.stage = stage;
        if (stage == 1) {
            this.recreate_ops = new GreedyRepair();
        } else if (stage == 2) {
            this.recreate_ops = new GreedyBalance();
        } else {
            throw new IllegalArgumentException("Please input 1 or 2 for StringRevive");
        }

        maxCustomerNum = vrpCate.getCons().getMaxCustomerNum();
        vehicleCapacity = vrpCate.getCons().getVehicleCapacity();
        travelTime = vrpCate.getCons().getTimeLimit();
    }

    public static StringRevive of(int stage) {
        return new StringRevive(stage);
    }

    @Override
    public ALNSSolution ruin(ALNSSolution s, int removeNum) throws Exception {

        // Collections.sort(s.routes, (a, b) -> a.costs.getDist() - a.costs.getArc() > b.costs.getDist() - b.costs.getArc() ? -1 : 1);

        int vehicleFarAwayDepot = (int) Math.sqrt(s.routes.size());
        int[] route_id = new int[vehicleFarAwayDepot];
        for (int i = 0; i < route_id.length; i++) {
            route_id[i] = i;
        }

        for (int i = route_id.length; i < s.routes.size(); i++) {
            Route r = s.routes.get(i);
            double max_diff = 0;
            int max_diff_index = 0;
            boolean farAwayDepot = false;
            for (int j = 0; j < route_id.length; j++) {
                Route rr = s.routes.get(route_id[j]);
                double tmp = (r.costs.getDist() - r.costs.getArc()) - (rr.costs.getDist() - rr.costs.getArc());
                if (tmp > 0) {
                    farAwayDepot = true;
                    if (tmp > max_diff) {
                        max_diff = tmp;
                        max_diff_index = j;
                    }
                }
            }
            if (farAwayDepot) {
                route_id[max_diff_index] = i;
            }
        }

        int routeIndex;
        if (stage == 1) {
            routeIndex = Arrays.stream(route_id)
                    .reduce((a, b) -> getDebtSpace(s.routes.get(a)) > getDebtSpace(s.routes.get(b)) ? a : b).getAsInt();
        } else {
            routeIndex = Arrays.stream(route_id)
                    .reduce((a, b) -> getFreeSpace(s.routes.get(a)) > getFreeSpace(s.routes.get(b)) ? a : b).getAsInt();
        }

        removeNum = s.routes.get(routeIndex).getRoute().size() - 2;
        for (int j = 0; j < removeNum; j++) {
            s.removeCustomer(routeIndex, 1);
        }

        s.removeRoute(routeIndex);

        return s;
    }

    private double getFreeSpace(Route r) {
        if (r.costs.getNodeVio() > 0 || r.costs.getLoadVio() > 0 || r.costs.getTimeVio() > 0) {
            return 0;
        }
        double nodeFree = (maxCustomerNum - (r.getSize() - 2)) / (double) maxCustomerNum;
        double loadFree = (vehicleCapacity - r.costs.getLoad()) / (double) vehicleCapacity;
        double timeFree = (travelTime - r.costs.getTime()) / travelTime;
        return Math.min(Math.min(nodeFree, loadFree), timeFree);
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
    @Override
    public ALNSSolution recreate(ALNSSolution s) throws Exception {
        recreate_ops.recreate(s);
        return s;
    }

    public void revive(ALNSSolution[] ss) throws Exception {

        for (ALNSSolution s : ss) {
            this.ruin(s, 0);
            this.recreate(s);
        }
    }
}
