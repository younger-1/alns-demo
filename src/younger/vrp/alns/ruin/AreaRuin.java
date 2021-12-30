package younger.vrp.alns.ruin;

import java.util.Arrays;
import java.util.Random;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.instance.Node;
import younger.vrp.instance.Route;

public class AreaRuin extends ALNSAbstractRuin implements IALNSRuin {

    private AreaRuin() {
        this.random = new Random();
    }

    public static AreaRuin of() {
        return new AreaRuin();
    }

    @Override
    public ALNSSolution ruin(ALNSSolution s, int nodes) throws Exception {
        if (s.removeNodes.size() != 0) {
            System.err.println("removalCustomers is not empty.");
            return s;
        } else if (s.customerNr <= 10) {
            System.err.println("Not enough customers to carry out RadialRuin.");
            return s;
        }

        int routePos = random.ints(0, s.routes.size()).filter(i -> s.routes.get(i).getSize() > 2).findAny().getAsInt();
        int nodePos = random.nextInt(s.routes.get(routePos).getSize() - 2) + 1;
        Node center = s.routes.get(routePos).getNode(nodePos);

        double radial = get_radial(s, center, nodes);

        int[] d = s.distance[center.getId()];
        for (int i = 0; i < s.routes.size(); i++) {
            Route route = s.routes.get(i);
            for (int j = 1; j < route.getSize() - 1; j++) {
                Node n = route.getNode(j);
                if (d[n.getId()] <= radial) {
                    s.removeCustomer(i, j);
                    j--;
                }
            }
        }
        return s;
    }

    private double get_radial(ALNSSolution s, Node center, int node_num) {

        int[] d = s.distance[center.getId()];
        double[] min_dist = new double[node_num];
        for (int i = 0; i < min_dist.length; i++) {
            min_dist[i] = Double.MAX_VALUE;
        }

        for (int i = 0; i < d.length; i++) {
            double max_dist_diff = 0;
            int max_dist_diff_index = 0;
            boolean is_add_dist = false;
            if (d[i] > 0) {
                for (int j = 0; j < min_dist.length; j++) {
                    double tmp = min_dist[j] - d[i];
                    if (tmp > 0) {
                        is_add_dist = true;
                        if (tmp > max_dist_diff) {
                            max_dist_diff = tmp;
                            max_dist_diff_index = j;
                        }
                    }
                }
            }
            if (is_add_dist) {
                min_dist[max_dist_diff_index] = d[i];
            }
        }
        Arrays.sort(min_dist);

        // double scale = r.nextDouble() + 1;
        double radial = min_dist[min_dist.length - 1];

        return radial;
    }

}
