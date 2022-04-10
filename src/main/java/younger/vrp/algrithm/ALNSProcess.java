package younger.vrp.algrithm;

import java.util.Random;
import java.util.stream.Stream;

import younger.vrp.alns.config.VisualizationControl;
import younger.vrp.alns.operation.ALNSAbstractOperation;
import younger.vrp.alns.operation.IALNSOperation;
import younger.vrp.alns.recreate.ALNSAbstractRecreate;
import younger.vrp.alns.recreate.IALNSRecreate;
// import younger.vrp.alns.revive.SpreadRevive;
// import younger.vrp.alns.revive.StringRevive;
import younger.vrp.alns.config.IALNSConfig;
import younger.vrp.alns.ruin.ALNSAbstractRuin;
import younger.vrp.alns.ruin.IALNSRuin;
import younger.vrp.visualization.VRPDrawer;

public class ALNSProcess {
    private final IALNSConfig config;
    // new ProximityZoneDestroy(),
    // new ZoneDestroy(),
    // new NodesCountDestroy(false),
    // new SubrouteDestroy(),
    private IALNSRuin[] destroy_ops = null;
    private IALNSRecreate[] repair_ops = null;
    private IALNSRuin[] filter_ops = null;
    private IALNSRecreate[] balance_ops = null;

    private ALNSSolution s_g = null;
    private ALNSSolution[] s_1 = null;
    private ALNSSolution[] s_2 = null;

    private static final int STAGE_1 = 1;
    private static final int STAGE_2 = 2;

    /**
     * Temperature for SA
     */
    private double T;
    /**
     * time start for ALNS process
     */
    private long t_start;

    // private StringRevive stringDestroy;
    // private StringRevive stringFilter;
    // private SpreadRevive spreadRevive;
    private int max_i;
    private int allow_reduced_num;

    public ALNSProcess(ALNSSolution s_, IALNSConfig c) {
        config = c;

        s_g = new ALNSSolution(s_);
        // System.out.println(s_g.average_dist);

        prepare_two_stage();

        ALNSAbstractOperation.use(s_.getVrpCate());
        destroy_ops = ALNSAbstractRuin.common_destroy();
        filter_ops = ALNSAbstractRuin.common_filter();
        repair_ops = ALNSAbstractRecreate.common_repair();
        balance_ops = ALNSAbstractRecreate.common_balance();

        init_strategy();

        // stringDestroy = StringRevive.of(STAGE_1);
        // stringFilter = StringRevive.of(STAGE_2);
        // spreadRevive = SpreadRevive.of();

        max_i = config.get_u();
        allow_reduced_num = get_reduced_vehicle_num();
    }

    private void init_strategy() {
        init_operators(destroy_ops);
        init_operators(repair_ops);
        init_operators(filter_ops);
        init_operators(balance_ops);
    }

    private void init_operators(IALNSOperation[] ops) {
        for (IALNSOperation op : ops) {
            op.set_total_draws(0);
            op.set_draws(0);
            op.set_score(0);
            op.set_w(1.);
            op.set_p(1 / (double) ops.length);
        }
    }

    private void prepare_two_stage() {

        int parallel_num = (int) Math.sqrt(s_g.routes.size());
        s_1 = new ALNSSolution[parallel_num];
        s_2 = new ALNSSolution[parallel_num];

        for (int i = 0; i < s_1.length; i++) {
            s_1[i] = new ALNSSolution(s_g);
            s_2[i] = new ALNSSolution(s_g);
        }
    }

    private int get_ruin_number(ALNSSolution s_c2) {
        int q_l = Math.min((int) Math.ceil(0.05 * s_c2.customerNr), 30);
        int q_u = Math.min((int) Math.ceil(0.20 * s_c2.customerNr), 60);

        Random r = s_g.random;
        // Random r = new Random();
        return r.nextInt(q_u - q_l + 1) + q_l;
    }

    // ! ALNS框架
    public ALNSSolution improveSolution(String instanceName, VisualizationControl vc) throws Exception {
        VRPDrawer.setInstanceName(instanceName);
        instanceName += "-";
        if (vc.isInit_sol_chart()) {
            VRPDrawer.drawSolution(s_g, 1).saveImage(instanceName + "init");
        }

        T = 0.003 * s_g.costs.getDist();
        double descent_coefficient = config.get_sa();
        t_start = System.currentTimeMillis();

        IALNSRuin[] destroy_rare = ALNSAbstractRuin.rare_destroy();
        IALNSRecreate[] repair_rare = ALNSAbstractRecreate.rare_repair();
        IALNSRuin[] filter_rare = ALNSAbstractRuin.rare_filter();
        IALNSRecreate[] balance_rare = ALNSAbstractRecreate.rare_balance();

        int destroy_counter = 0;
        int repair_counter = 0;
        int filter_counter = 0;
        int balance_counter = 0;

        int ruin_p = 5;
        int recreate_p = 12;
        final int rare_lucky = 0;

        // int already_reduced = 0;

        int i = 0;
        int print_counter = 0;
        int segment_counter = 0;
        int max_v = config.get_v();
        int max_w = config.get_w();
        int segment_num = config.get_tau();

        while (true) {
            // ! q is the number of nodes to be removed
            int q = get_ruin_number(s_g);

            // * Stage 1
            for (int v = 0; v < max_v; v++, i++) {
                IALNSRuin destroyOperator = null;
                IALNSRecreate repairOperator = null;
                if (s_g.random.nextInt(ruin_p) == rare_lucky) {
                    destroy_counter += 1;
                    destroyOperator = (IALNSRuin) pick_rare_ops(destroy_rare);
                } else {
                    destroyOperator = (IALNSRuin) pick_ops(destroy_ops);
                }
                if (s_g.random.nextInt(recreate_p) == rare_lucky) {
                    repair_counter += 1;
                    repairOperator = (IALNSRecreate) pick_rare_ops(repair_rare);
                } else {
                    repairOperator = (IALNSRecreate) pick_ops(repair_ops);
                }

                ALNSSolution s_t = new ALNSSolution(s_2[s_g.random.nextInt(s_2.length)]);
                // destroy solution
                ALNSSolution s_destroy = destroyOperator.ruin(s_t, q);
                // repair solution
                ALNSSolution s_repair = repairOperator.recreate(s_destroy);

                s_repair.update_average_dist();

                int _w = get_worst_s_1();
                double diff = s_repair.costs.getDist() - s_1[_w].costs.getDist();
                // double diff = s_repair.cost.cost - s_1[_w].cost.cost;
                // double diff = s_repair.cost.time - s_1[_w].cost.time;
                if (diff <= 0) {
                    s_1[_w] = s_repair;
                    if (s_1[_w].costs.getTotal() < s_g.costs.getTotal()) {
                        handleNewGlobalMinimum(destroyOperator, repairOperator, s_1[_w]);
                    } else {
                        handleNewLocalMinimum(destroyOperator, repairOperator);
                    }
                } else {
                    handleWorseSolution(STAGE_1, destroyOperator, repairOperator, s_repair, diff);
                }
                T = descent_coefficient * T;
            }

            // * Revive
            // if (allow_reduced_num > 0) {
            //     already_reduced = reduce_route_revive(already_reduced, i);
            // }
            // spread_route_revive(i);

            // * Stage 2
            for (int w = 0; w < max_w; w++, i++) {
                IALNSRuin filterOperator = null;
                IALNSRecreate balanceOperator = null;
                if (s_g.random.nextInt(ruin_p) == rare_lucky) {
                    filter_counter += 1;
                    filterOperator = (IALNSRuin) pick_rare_ops(filter_rare);
                } else {
                    filterOperator = (IALNSRuin) pick_ops(filter_ops);
                }
                if (s_g.random.nextInt(recreate_p) == rare_lucky) {
                    balance_counter += 1;
                    balanceOperator = (IALNSRecreate) pick_rare_ops(balance_rare);
                } else {
                    balanceOperator = (IALNSRecreate) pick_ops(balance_ops);
                }

                ALNSSolution s_t = new ALNSSolution(s_1[s_g.random.nextInt(s_1.length)]);
                // destroy solution
                ALNSSolution s_destroy = filterOperator.ruin(s_t, q);
                // repair solution
                ALNSSolution s_repair = balanceOperator.recreate(s_destroy);

                s_repair.update_average_dist();

                int _w = get_worst_s_2();
                double diff = s_repair.costs.getTotal() - s_2[_w].costs.getTotal();
                if (diff <= 0) {
                    s_2[_w] = s_repair;
                    if (s_2[_w].costs.getTotal() < s_g.costs.getTotal()) {
                        // if (s_2[_w].cost.cost < s_g.cost.cost) {
                        handleNewGlobalMinimum(filterOperator, balanceOperator, s_2[_w]);
                    } else {
                        handleNewLocalMinimum(filterOperator, balanceOperator);
                    }
                } else {
                    handleWorseSolution(STAGE_2, filterOperator, balanceOperator, s_repair, diff);
                }
                T = descent_coefficient * T;
            }

            // ! The origin ALNS process
            // if (s_t.cost.total < s_c.cost.total) {
            //     s_c = s_t;
            //     if (s_t.cost.total < s_g.cost.total) {
            //         handleNewGlobalMinimum(destroyOperator, repairOperator, s_t);
            //     } else {
            //         handleNewLocalMinimum(destroyOperator, repairOperator);
            //     }
            // } else {
            //     handleWorseSolution(destroyOperator, repairOperator, s_t);
            // }

            if (print_counter == i / 100) {
                print_counter += 1;
                int _b1 = get_best_s_1();
                int _b2 = get_best_s_2();
                String ss = String.format(
                        "Iterations: %5d, Solution: { s_1:[dist: %9.1f, total: %8.1f], s_2:[dist: %9.1f, total: %8.1f] }",
                        i, Math.round(s_1[_b1].costs.getDist() * 100) / 100.0,
                        Math.round(s_1[_b1].costs.getTotal() * 100) / 100.0,
                        Math.round(s_2[_b2].costs.getDist() * 100) / 100.0,
                        Math.round(s_2[_b2].costs.getTotal() * 100) / 100.0);
                System.out.println(ss);
            }

            if (i > 0 && segment_counter == i / segment_num) {
                segment_counter += 1;
                segment_finished();
            }

            if ((i > max_i && s_g.feasible()) || i > max_i * 1.2)
                break;
        }

        update_global();
        // s_g = s_2[get_best_s_2()];

        // System.out.println(s_1[get_best_s_1()]);
        // System.out.println(s_2[get_best_s_2()]);

        double alns_time = Math.round((System.currentTimeMillis() - t_start) * 1000) / 1000000.;

        System.out.print(String.format("\n\u001B[33mALNS done. Cost time: %f s\u001B[0m\n", alns_time));
        System.out.println(String.format("\n\u001B[36mArrange %d customers with %d vehicles\u001B[0m\n", s_g.customerNr,
                s_g.getRoutes().size()));
        // System.out.println(String.format("\n\u001B[41m\u001B[30mALNS progress cost %.2fs to arrange %d customers.\u001B[0m\n"));

        print_ops_helper(destroy_counter, repair_counter, filter_counter, balance_counter);

        int _b1 = get_best_s_1();
        int _b2 = get_best_s_2();
        if (vc.isS1_sol_chart()) {
            VRPDrawer.drawSolution(s_1[_b1], 2).saveImage(instanceName + "stage_1");
        }
        if (vc.isS2_sol_chart()) {
            VRPDrawer.drawSolution(s_2[_b2], 3).saveImage(instanceName + "stage_2");
        }
        if (vc.isGlobal_sol_chart()) {
            VRPDrawer.drawSolution(s_g, 4).saveImage(instanceName + "best");
        }

        return s_g;
    }

    private int reduce_route_revive(int already_reduced, int i) throws Exception {
        int begin_reduce = (int) (1 / 5. * max_i);
        int end_reduce = (int) (4 / 5. * max_i);
        // FIXME: to be optimized
        int interval_reduce = 2 * (end_reduce - begin_reduce) / allow_reduced_num;
        if (already_reduced == (i - begin_reduce) / interval_reduce) {
            // FIXME: s_g.feasible() may not good
            if (s_g.feasible() && begin_reduce < i && i < end_reduce) {
                // stringDestroy.revive(s_1);
                // stringFilter.revive(s_2);
                already_reduced += 1;
            }
        }
        return already_reduced;
    }

    private void spread_route_revive(int iter) throws Exception {
        if (iter > 100 && Math.random() > 0.4) {
            // spreadRevive.revive(s_1);
            // spreadRevive.revive(s_2);
        }
    }

    private int get_reduced_vehicle_num() {
        int forNode = s_g.customerNr / s_g.getVrpCate().getCons().getMaxCustomerNum() + 1;
        int forLoad = (int) s_g.costs.getLoad() / s_g.getVrpCate().getCons().getVehicleCapacity() + 1;
        int neededRoute = Math.max(forNode, forLoad);
        return s_g.routes.size() - neededRoute;
    }

    private IALNSOperation pick_rare_ops(IALNSOperation[] ops) {
        int n = s_g.random.nextInt(ops.length);
        ops[n].drawn();
        return ops[n];
    }

    private void print_ops_helper(int destroy_counter, int repair_counter, int filter_counter, int balance_counter) {
        System.out.println("Stage-1:");
        System.out.println("----- -----");
        print_ops_count(destroy_ops);
        System.out.println("Rare destroy" + " are used " + destroy_counter + " times.");
        System.out.println("----- -----");
        print_ops_count(repair_ops);
        System.out.println("Rare repair" + " are used " + repair_counter + " times.");
        System.out.println("\n");

        System.out.println("Stage-2:");
        System.out.println("----- -----");
        print_ops_count(filter_ops);
        System.out.println("Rare filter" + " are used " + filter_counter + " times.");
        System.out.println("----- -----");
        print_ops_count(balance_ops);
        System.out.println("Rare balance" + " are used " + balance_counter + " times.");
    }

    private void print_ops_count(IALNSOperation[] ops) {
        for (IALNSOperation op : ops) {
            System.out.println(op.getClass().getSimpleName() + " is used " + op.get_total_draws() + " times.");
        }
    }

    private void handleWorseSolution(int stage, IALNSRuin destroyOperator, IALNSRecreate repairOperator,
            ALNSSolution s_repair, double diff) {
        double p_accept = Math.exp(-diff / T);
        if (Math.random() < p_accept) {
            if (1 == stage) {
                s_1[get_worst_s_1()] = s_repair;
            }
            if (2 == stage) {
                s_2[get_worst_s_2()] = s_repair;
            }
        }
        destroyOperator.add_to_score(config.get_reward_3());
        repairOperator.add_to_score(config.get_reward_3());
    }

    private void handleNewLocalMinimum(IALNSRuin destroyOperator, IALNSRecreate repairOperator) {
        destroyOperator.add_to_score(config.get_reward_2());
        repairOperator.add_to_score(config.get_reward_2());
    }

    private void handleNewGlobalMinimum(IALNSRuin destroyOperator, IALNSRecreate repairOperator, ALNSSolution s_t) {
        if (s_t.feasible())
            s_g = s_t;
        destroyOperator.add_to_score(config.get_reward_1());
        repairOperator.add_to_score(config.get_reward_1());
    }

    private void segment_finished() {
        calc_operator_weight(destroy_ops);
        calc_operator_weight(repair_ops);
        calc_operator_weight(filter_ops);
        calc_operator_weight(balance_ops);
    }

    private void calc_operator_weight(IALNSOperation[] ops) {
        double w_sum = 0;
        for (IALNSOperation op : ops) {
            double w_old1 = (1 - config.get_lambda()) * op.get_w();
            double factor = op.get_draws() < 1 ? 0 : (double) op.get_score() / (double) op.get_draws();
            double w_old2 = config.get_lambda() * factor;
            double w_new = w_old1 + w_old2;
            w_sum += w_new;
            op.set_w(w_new);
        }
        for (IALNSOperation op : ops) {
            op.set_p(op.get_w() / w_sum);
            op.set_draws(0);
            op.set_score(0);
        }
    }

    private IALNSOperation pick_ops(IALNSOperation[] ops) {
        double random = Math.random();
        double threshold = 0.;
        for (IALNSOperation op : ops) {
            threshold += op.get_p();
            if (random <= threshold) {
                op.drawn();
                return op;
            }
        }

        ops[ops.length - 1].drawn();
        return ops[ops.length - 1];
    }

    private int get_worst_s_1() {
        return Stream.iterate(0, x -> x + 1).limit(s_1.length).reduce((a, b) -> {
            return s_1[a].costs.getDist() < s_1[b].costs.getDist() ? b : a;
            // return s_1[a].cost.cost < s_1[b].cost.cost ? b : a;
            // return s_1[a].cost.time < s_1[b].cost.time ? b : a;
        }).get();
    }

    private int get_worst_s_2() {
        return Stream.iterate(0, x -> x + 1).limit(s_2.length).reduce((a, b) -> {
            return s_2[a].costs.getTotal() < s_2[b].costs.getTotal() ? b : a;
        }).get();
    }

    private int get_best_s_1() {
        return Stream.iterate(0, x -> x + 1).limit(s_1.length).reduce((a, b) -> {
            return s_1[a].costs.getDist() > s_1[b].costs.getDist() ? b : a;
            // return s_1[a].cost.cost > s_1[b].cost.cost ? b : a;
            // return s_1[a].cost.time > s_1[b].cost.time ? b : a;
        }).get();
    }

    private int get_best_s_2() {
        return Stream.iterate(0, x -> x + 1).limit(s_2.length).reduce((a, b) -> {
            return s_2[a].costs.getTotal() > s_2[b].costs.getTotal() ? b : a;
        }).get();
    }

    private void update_global() {
        for (ALNSSolution s : s_2) {
            if (s.costs.getTotal() < s_g.costs.getTotal() && s.feasible())
                s_g = s;
        }
    }
}
