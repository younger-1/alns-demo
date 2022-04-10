package younger.vrp.alns.recreate.balance;

import java.util.HashSet;
import java.util.Set;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.recreate.ALNSAbstractRecreate;
import younger.vrp.alns.recreate.IALNSRecreate;
import younger.vrp.base.IDistance;
import younger.vrp.instance.Cost;
import younger.vrp.instance.Node;
import younger.vrp.instance.Route;

public class GreedyBalance extends ALNSAbstractRecreate implements IALNSRecreate {

    public static GreedyBalance of() {
        return new GreedyBalance();
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
            Set<Integer> nbs = new HashSet<>(IDistance.getDistanceInstance().getNeighbours(insertNode.getId()));

            int nodePos = -1;
            int routePos = -1;
            double min_total = Double.POSITIVE_INFINITY;

            for (int j = 0; j < s.routes.size(); j++) {
                Route route = s.routes.get(j);
                // ! 寻找最优插入位置 1 ~ N
                for (int i = 1; i < route.getSize(); ++i) {
                    if (!nbs.contains(route.getNodeId(i))) continue;
                    // 评价插入情况
                    Cost newCost = s.evaluateInsertCustomer(j, i, insertNode);
                    double total_up = (newCost.getTotal() - s.costs.getTotal());
                    // double total_up = (newCost.total - s.costs.getTotal()) * route.costs.getTotal();
                    if (total_up < min_total) {
                        nodePos = i;
                        routePos = j;
                        min_total = total_up;
                    }
                }
            }
            s.insertCustomer(routePos, nodePos, insertNode);
        }

        return s;
    }
}
