package younger.vrp.alns.repair;

import younger.vrp.algrithm.Cost;
import younger.vrp.algrithm.MyALNSSolution;
import younger.vrp.instance.Node;

public class GreedyBalance extends ALNSAbstractRepair implements IALNSRepair {

    @Override
    public MyALNSSolution repair(MyALNSSolution s) {
        if (s.removalCustomers.size() == 0) {
            System.err.println("removalCustomers is empty!");
            return s;
        }

        int insertCusNr = s.removalCustomers.size();

        for (int k = 0; k < insertCusNr; k++) {

            Node insertNode = s.removalCustomers.remove(0);

            double bestCost;
            int bestCusP = -1;
            int bestRouteP = -1;
            bestCost = Double.POSITIVE_INFINITY;

            for (int j = 0; j < s.routes.size(); j++) {

                if (s.routes.get(j).getSize() < 1) {
                    continue;
                }

                // ! 寻找最优插入位置 1 ~ N-1
                for (int i = 1; i < s.routes.get(j).getSize() - 1; ++i) {

                    // 评价插入情况
                    Cost newCost = new Cost(s.cost);
                    s.evaluateInsertCustomer(j, i, insertNode, newCost);

                    if (newCost.total > Double.MAX_VALUE) {
                        newCost.total = Double.MAX_VALUE;
                    }

                    // if a better insertion is found, set the position to insert in the move and
                    // update the minimum cost found
                    if (newCost.total < bestCost) {
                        // System.out.println(varCost.checkFeasible());
                        bestCusP = i;
                        bestRouteP = j;
                        bestCost = newCost.total;
                    }
                }
            }
            s.insertCustomer(bestRouteP, bestCusP, insertNode);
        }
        return s;
    }
}