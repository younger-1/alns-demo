package younger.vrp.alns.ruin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import younger.vrp.algrithm.ALNSSolution;
import younger.vrp.instance.Node;
import younger.vrp.instance.Route;

public class RadialRuin extends ALNSAbstractRuin implements IALNSRuin {

    private RadialRuin() {
        this.random = new Random();
    }

    public static RadialRuin of() {
        return new RadialRuin();
    }

    /**
     * @implNote
     * RadialRuin must remove at least 10 nodes.
     */
    @Override
    public ALNSSolution ruin(ALNSSolution s, int removeNr) throws Exception {

        if (s.removeNodes.size() != 0) {
            System.err.println("removalCustomers is not empty.");
            return s;
        } else if (s.customerNr <= 10) {
            System.err.println("Not enough customers to carry out RadialRuin.");
            return s;
        }

        // int routeCustomerNum = vrpCate.getCons().getMaxCustomerNum();
        int routeCustomerNum = s.customerNr / s.routes.size();

        int ruin_num = 0;
        int ruin_times = random.nextInt((int) Math.sqrt(s.routes.size()));
        Map<Integer, Double> id_dist = new HashMap<>();
        boolean is_continue = true;
        int fail_num = 0;

        while (is_continue && ruin_num < ruin_times || s.removeNodes.size() < removeNr) {
            boolean is_ruin = true;
            int routePos = random.ints(0, s.routes.size()).filter(i -> s.routes.get(i).getSize() > 2).findAny().getAsInt();
            int nodePos = random.nextInt(s.routes.get(routePos).getSize() - 2) + 1;
            Node center = s.routes.get(routePos).getNode(nodePos);

            int node_num = routeCustomerNum / 2 + random.nextInt(routeCustomerNum / 2);
            double radial = get_radial(s, center, node_num);

            for (int id : id_dist.keySet()) {
                if (s.distance[id][center.getId()] < (1.5 + random.nextDouble() / 2) * (id_dist.get(id) + radial)) {
                    is_ruin = false;
                }
            }

            if (is_ruin) {
                id_dist.put(center.getId(), radial);
                ruin_impl(s, center, radial);
                ruin_num++;
                fail_num = 0;
                is_continue = true;
            } else {
                fail_num++;
            }

            if (fail_num > 4) {
                is_continue = false;
            }
            // System.out.println("  wo: " + removeNr);
            // System.out.println("   i: " + s.routes.get(routePos).getSize());
            // System.out.println("   u: " + s.removeNodes.size());
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

    private void ruin_impl(ALNSSolution s, Node center, double radial) {
        int[] d = s.distance[center.getId()];
        // s.routes.stream().forEach(r -> r.getRoute().stream()
        //         .filter(n -> s.distance[n.getId()][center.getId()] <= radial)
        //         .forEach(n -> s.removeCustomerWithID(n.getId())));
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
    }

}
