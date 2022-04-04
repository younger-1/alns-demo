package younger.vrp.alns.ruin.destroy;

import java.util.Random;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.alns.ruin.ALNSAbstractRuin;
import younger.vrp.alns.ruin.IALNSRuin;
import younger.vrp.instance.Node;
import younger.vrp.instance.Route;

public class ShawDestroy extends ALNSAbstractRuin implements IALNSRuin {

    private ShawDestroy() {
        this.random = new Random();
    }

    public static ShawDestroy of() {
        return new ShawDestroy();
    }

    @Override
    public ALNSSolution ruin(ALNSSolution s, int removeNr) throws Exception {

        if (s.removeNodes.size() != 0) {
            System.err.println("removalCustomers is not empty.");
            return s;
        }

        // 选择被移除客户所在的路径
        int l = s.routes.size();
        int removeRoutePos = random.ints(0, l).distinct().limit(l)
                .filter(x -> s.routes.get(x).getSize() > 2)
                .findFirst().getAsInt();

        Route removeRoute = s.routes.get(removeRoutePos);

        // 选择被移除客户
        int removeCusPos = random.nextInt(removeRoute.getSize() - 2) + 1;

        // !记录被移除的点
        Node lastRemove = removeRoute.getNode(removeCusPos);
        Route lastRoute = removeRoute;

        s.removeCustomer(removeRoutePos, removeCusPos);

        int lastRemovePos = -1;
        int lastRoutePos = -1;
        int[][] distance = s.distance;

        while (s.removeNodes.size() < removeNr) {
            double minRelate = Double.MAX_VALUE;

            for (int j = 0; j < s.routes.size(); j++) {

                for (int i = 1; i < s.routes.get(j).getSize() - 1; ++i) {
                    Node relatedNode = s.routes.get(j).getNode(i);
                    int ll = (lastRoute.getId() == s.routes.get(j).getId()) ? 0 : 1;
                    double d = distance[lastRemove.getId()][relatedNode.getId()];
                    double fitness = d * (1 + 0.5 * ll + 0.1 * Math.abs(lastRemove.getDemand() - relatedNode.getDemand()));
                    if (minRelate > fitness) {
                        minRelate = fitness;
                        lastRemovePos = i;
                        lastRoutePos = j;
                    }
                }
            }
            lastRoute = s.routes.get(lastRoutePos);
            lastRemove = lastRoute.getNode(lastRemovePos);
            s.removeCustomer(lastRoutePos, lastRemovePos);
        }

        return s;
    }
}
