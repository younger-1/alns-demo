package zll.vrptw.alns.repair;

import java.util.ArrayList;
import java.util.Collections;

import zll.vrptw.algrithm.Cost;
import zll.vrptw.algrithm.MyALNSSolution;
import zll.vrptw.instance.Node;

public class RegretBalance extends ALNSAbstractRepair implements IALNSRepair {

    @Override
    public MyALNSSolution repair(MyALNSSolution s) {
        if (s.removalCustomers.size() == 0) {
            System.err.println("removalCustomers is empty!");
            return s;
        }

        ArrayList<BestPosition> bestPoses = new ArrayList<BestPosition>();

        int insertCusNr = s.removalCustomers.size();

        for (int k = 0; k < insertCusNr; k++) {

            Node insertNode = s.removalCustomers.remove(0);

            double first, second;
            int bestCusP = -1;
            int bestRouteP = -1;
            first = second = Double.POSITIVE_INFINITY;

            for (int j = 0; j < s.routes.size(); j++) {

                if (s.routes.get(j).getRoute().size() < 1) {
                    continue;
                }

                // 寻找最优插入位置
                for (int i = 1; i < s.routes.get(j).getRoute().size() - 1; ++i) {

                    // 评价插入情况
                    Cost newCost = new Cost(s.cost);
                    s.evaluateInsertCustomer(j, i, insertNode, newCost);

                    if (newCost.total > Double.MAX_VALUE) {
                        newCost.total = Double.MAX_VALUE;
                    }

                    // if a better insertion is found, set the position to insert in the move and
                    // update the minimum cost found
                    if (newCost.total < first) {
                        // System.out.println(varCost.checkFeasible());
                        bestCusP = i;
                        bestRouteP = j;
                        second = first;
                        first = newCost.total;
                    } else if (newCost.total < second && newCost.total != first) {
                        second = newCost.total;
                    }
                }
            }
            bestPoses.add(new BestPosition(insertNode, bestCusP, bestRouteP, second - first));
        }
        Collections.sort(bestPoses);

        for (BestPosition bp : bestPoses) {
            s.insertCustomer(bp.bestRroutePosition, bp.bestCustomerPosition, bp.insertNode);
        }

        return s;
    }
}

class BestPosition implements Comparable<BestPosition> {

    public int bestRroutePosition;
    public int bestCustomerPosition;
    public Node insertNode;
    public double deltaCost;

    public BestPosition() {
    }

    public BestPosition(Node insertNode, int customer, int route, double f) {
        this.insertNode = insertNode;
        this.bestRroutePosition = route;
        this.bestCustomerPosition = customer;
        this.deltaCost = f;
    }

    @Override
    public int compareTo(BestPosition o) {
        BestPosition s = (BestPosition) o;
        if (this.bestCustomerPosition > s.bestCustomerPosition) {
            return -1;
        } else if (this.bestCustomerPosition < s.bestCustomerPosition) {
            return 1;
        } else {
            return 0;
        }
    }
}
