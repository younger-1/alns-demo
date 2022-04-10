package younger.vrp.alns.recreate.balance;

import java.util.ArrayList;
import java.util.Collections;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.recreate.ALNSAbstractRecreate;
import younger.vrp.alns.recreate.IALNSRecreate;
import younger.vrp.instance.Cost;
import younger.vrp.instance.Node;

public class RegretBalance extends ALNSAbstractRecreate implements IALNSRecreate {

    public static RegretBalance of() {
        return new RegretBalance();
    }

    @Override
    public ALNSSolution recreate(ALNSSolution s) {
        if (s.removeNodes.size() == 0) {
            System.err.println("removalCustomers is empty!");
            return s;
        }

        ArrayList<BestPosition> bestPoses = new ArrayList<BestPosition>();

        int insertCusNr = s.removeNodes.size();

        for (int k = 0; k < insertCusNr; k++) {

            Node insertNode = s.removeNodes.remove(0);

            double first, second;
            int bestCusP = -1;
            int bestRouteP = -1;
            first = second = Double.POSITIVE_INFINITY;

            for (int j = 0; j < s.routes.size(); j++) {

                if (s.routes.get(j).getSize() < 1) {
                    continue;
                }

                // 寻找最优插入位置
                for (int i = 1; i < s.routes.get(j).getSize(); ++i) {

                    // 评价插入情况
                    Cost newCost = s.evaluateInsertCustomer(j, i, insertNode);

                    if (newCost.getTotal() > Double.MAX_VALUE) {
                        newCost.setTotal(Double.MAX_VALUE);
                    }

                    if (newCost.getTotal() < first) {
                        bestCusP = i;
                        bestRouteP = j;
                        second = first;
                        first = newCost.getTotal();
                    } else if (newCost.getTotal() < second && newCost.getTotal() != first) {
                        second = newCost.getTotal();
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
