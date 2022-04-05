package younger.vrp.alns.recreate.repair;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.recreate.ALNSAbstractRecreate;
import younger.vrp.alns.recreate.IALNSRecreate;
import younger.vrp.instance.Cost;
import younger.vrp.instance.Node;
import younger.vrp.instance.Route;

public class GreedyRepair extends ALNSAbstractRecreate implements IALNSRecreate {

    public static GreedyRepair of() {
        return new GreedyRepair();
    }

    @Override
    public ALNSSolution recreate(ALNSSolution s) {
        if (s.removeNodes.size() == 0) {
            System.err.println("removalCustomers is empty!");
            return s;
        }

        int insertCusNr = s.removeNodes.size();

        for (int k = 0; k < insertCusNr; k++) {

            Node insertNode = s.removeNodes.remove(0);

            int nodePos = -1;
            int routePos = -1;
            double min_cost = Double.POSITIVE_INFINITY;

            for (int j = 0; j < s.routes.size(); j++) {
                Route route = s.routes.get(j);
                // ! 寻找最优插入位置 1 ~ N
                for (int i = 1; i < route.getSize(); ++i) {
                    // 评价插入情况
                    Cost newCost = s.evaluateInsertCustomer(j, i, insertNode);
                    double cost_up = (newCost.getDist() - s.costs.getDist());
                    if (cost_up < min_cost) {
                        nodePos = i;
                        routePos = j;
                        min_cost = cost_up;
                    }
                }
            }
            s.insertCustomer(routePos, nodePos, insertNode);
        }

        return s;
    }
}