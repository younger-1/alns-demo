package younger.vrp.alns.recreate.repair;

import java.util.ArrayList;
import java.util.Collections;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.recreate.ALNSAbstractRecreate;
import younger.vrp.alns.recreate.IALNSRecreate;
import younger.vrp.instance.Cost;
import younger.vrp.instance.Node;

public class RegretRepair extends ALNSAbstractRecreate implements IALNSRecreate {

    public static RegretRepair of() {
        return new RegretRepair();
    }

    @Override
    public ALNSSolution recreate(ALNSSolution s) {
        if (s.removeNodes.size() == 0) {
            System.err.println("removalCustomers is empty!");
            return s;
        }

        ArrayList<BestPos> bestPoses = new ArrayList<BestPos>();

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

                    if (newCost.getArc() < first) {
                        bestCusP = i;
                        bestRouteP = j;
                        second = first;
                        first = newCost.getArc();
                    } else if (newCost.getArc() < second && newCost.getArc() != first) {
                        second = newCost.getArc();
                    }
                }
            }
            bestPoses.add(new BestPos(insertNode, bestCusP, bestRouteP, second - first));
        }
        Collections.sort(bestPoses);

        for (BestPos bp : bestPoses) {
            s.insertCustomer(bp.bestRroutePosition, bp.bestCustomerPosition, bp.insertNode);
        }

        return s;
    }
}

class BestPos implements Comparable<BestPos> {

    public int bestRroutePosition;
    public int bestCustomerPosition;
    public Node insertNode;
    public double deltaCost;

    public BestPos() {
    }

    public BestPos(Node insertNode, int customer, int route, double f) {
        this.insertNode = insertNode;
        this.bestRroutePosition = route;
        this.bestCustomerPosition = customer;
        this.deltaCost = f;
    }

    @Override
    public int compareTo(BestPos o) {
        BestPos s = (BestPos) o;
        if (this.bestCustomerPosition > s.bestCustomerPosition) {
            return -1;
        } else if (this.bestCustomerPosition < s.bestCustomerPosition) {
            return 1;
        } else {
            return 0;
        }
        // if (s.deltaCost > this.deltaCost) {
        // return 1;
        // } else if (this.deltaCost == s.deltaCost) {
        // return 0;
        // } else {
        // return -1;
        // }
    }
}
