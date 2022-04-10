package younger.vrp.alns.ruin;

import java.util.Random;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.instance.Route;

public class RandomRuin extends ALNSAbstractRuin implements IALNSRuin {

    private RandomRuin() {
        this.random = new Random();
    }

    public static RandomRuin of() {
        return new RandomRuin();
    }

    @Override
    public ALNSSolution ruin(ALNSSolution s, int removeNr) throws Exception {

        if (s.removeNodes.size() != 0) {
            System.err.println("removalCustomers is not empty.");
            return s;
        }

        while (s.removeNodes.size() < removeNr) {
 			// 选择被移除客户所在的路径
            int l = s.routes.size();
            int removeRoutePos = random.ints(0, l).distinct().limit(l)
                    .filter(x -> s.routes.get(x).getSize() > 2)
                    .findFirst().getAsInt();

			// 选择被移除的客户
            Route removeRoute = s.routes.get(removeRoutePos);
            int removeCusPos = random.nextInt(removeRoute.getSize() - 2) + 1;

            s.removeCustomer(removeRoutePos, removeCusPos);
        }

        return s;
    }

}
