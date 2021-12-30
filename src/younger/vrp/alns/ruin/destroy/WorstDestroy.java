package younger.vrp.alns.ruin.destroy;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.ruin.ALNSAbstractRuin;
import younger.vrp.alns.ruin.IALNSRuin;
import younger.vrp.instance.Cost;
import younger.vrp.instance.Route;

public class WorstDestroy extends ALNSAbstractRuin implements IALNSRuin {

    public static WorstDestroy of() {
        return new WorstDestroy();
    }

    @Override
    public ALNSSolution ruin(ALNSSolution s, int removeNr) throws Exception {

        if (s.removeNodes.size() != 0) {
            System.err.println("removalCustomers is not empty.");
            return s;
        }

        while (s.removeNodes.size() < removeNr) {
            int nodePos = -1;
            int routePos = -1;
            double max_cost = 0;
            for (int j = 0; j < s.routes.size(); j++) {
                Route route = s.routes.get(j);
                for (int i = 1; i < route.getSize() - 1; ++i) {
                    Cost newCost = s.evaluateRemoveCustomer(j, i);
                    double cost_down = (s.costs.getArc() - newCost.getArc());
                    // double cost_down = (s.costs.getArc() - newCost.arc) * route.costs.getArc();
                    if (cost_down > max_cost) {
                        max_cost = cost_down;
                        nodePos = i;
                        routePos = j;
                    }
                }
            }
            s.removeCustomer(routePos, nodePos);
        }

        return s;
    }
}